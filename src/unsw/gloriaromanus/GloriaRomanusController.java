package unsw.gloriaromanus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import util.ArrayUtil;
import util.MathUtil;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.ServiceFeatureTable.FeatureRequestMode;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

import org.json.JSONArray;
import org.json.JSONObject;

public class GloriaRomanusController {

	@FXML
	private MapView mapView;
	@FXML
	private TextField invading_province;
	@FXML
	private TextField opponent_province;
	@FXML
	private TextArea output_terminal;

	private ArcGISMap map;

	private Map<String, String> provinceToOwningFactionMap;

	private Map<String, Integer> provinceToNumberTroopsMap;

	private String humanFaction;

	private Feature currentlySelectedHumanProvince;
	private Feature currentlySelectedEnemyProvince;

	private FeatureLayer featureLayer_provinces;
	
	// Map constraints
	private final double X_MAX = 5E6;
	private final double X_MIN = -3E6;
	private final double Y_MAX = 8E6;
	private final double Y_MIN = 2E6;
	private final double PIXELS_PER_UNIT_X = 3778;
	private final double PIXELS_PER_UNIT_Y = 3842;

	// Used for custom panning algorithm
	private Point mouseAnchor = null;

	@FXML
	private void initialize() throws JsonParseException, JsonMappingException, IOException {
		// TODO = you should rely on an object oriented design to determine ownership
		provinceToOwningFactionMap = getProvinceToOwningFactionMap();

		provinceToNumberTroopsMap = new HashMap<String, Integer>();
		Random r = new Random();
		for (String provinceName : provinceToOwningFactionMap.keySet()) {
			provinceToNumberTroopsMap.put(provinceName, r.nextInt(500));
		}

		// TODO = load this from a configuration file you create (user should be able to
		// select in loading screen)
		humanFaction = "Rome";

		currentlySelectedHumanProvince = null;
		currentlySelectedEnemyProvince = null;

		initializeProvinceLayers();
	}

	@FXML
	public void clickedInvadeButton(ActionEvent e) throws IOException {
		if (currentlySelectedHumanProvince != null && currentlySelectedEnemyProvince != null) {
			String humanProvince = (String) currentlySelectedHumanProvince.getAttributes().get("name");
			String enemyProvince = (String) currentlySelectedEnemyProvince.getAttributes().get("name");
			if (confirmIfProvincesConnected(humanProvince, enemyProvince)) {
				// TODO = have better battle resolution than 50% chance of winning
				Random r = new Random();
				int choice = r.nextInt(2);
				if (choice == 0) {
					// human won. Transfer 40% of troops of human over. No casualties by human, but
					// enemy loses all troops
					int numTroopsToTransfer = provinceToNumberTroopsMap.get(humanProvince) * 2 / 5;
					provinceToNumberTroopsMap.put(enemyProvince, numTroopsToTransfer);
					provinceToNumberTroopsMap.put(humanProvince,
							provinceToNumberTroopsMap.get(humanProvince) - numTroopsToTransfer);
					provinceToOwningFactionMap.put(enemyProvince, humanFaction);
					printMessageToTerminal("Won battle!");
				} else {
					// enemy won. Human loses 60% of soldiers in the province
					int numTroopsLost = provinceToNumberTroopsMap.get(humanProvince) * 3 / 5;
					provinceToNumberTroopsMap.put(humanProvince,
							provinceToNumberTroopsMap.get(humanProvince) - numTroopsLost);
					printMessageToTerminal("Lost battle!");
				}
				resetSelections(); // reset selections in UI
				addAllPointGraphics(); // reset graphics
			} else {
				printMessageToTerminal("Provinces not adjacent, cannot invade!");
			}
		}
	}

	private void setConstrainedViewpoint(Envelope visArea, double mapScale) {

		double deltaX = 0, deltaY = 0;

		if (visArea.getXMax() > X_MAX) {
			deltaX = X_MAX - visArea.getXMax();
		}
		if (visArea.getYMax() > Y_MAX) {
			deltaY = Y_MAX - visArea.getYMax();
		}
		if (visArea.getXMin() < X_MIN) {
			deltaX = X_MIN - visArea.getXMin();
		}
		if (visArea.getYMin() < Y_MIN) {
			deltaY = Y_MIN - visArea.getYMin();
		}
		// Shift mouse anchor appropriately if it exists.
		if(mouseAnchor != null && (deltaX != 0 || deltaY != 0)) {
			mouseAnchor = new Point(mouseAnchor.getX() + deltaX, mouseAnchor.getY() + deltaY , mouseAnchor.getSpatialReference());
		}
//      deltaX = 0;
//      deltaY = 0;
		System.out.println(deltaX + " " + deltaY);
		System.out.println(mapView.getWidth()+ " " + mapView.getHeight());
		
		System.out.println(visArea.getXMin() + " " + visArea.getXMax() + " " + visArea.getYMin() + " " + visArea.getYMax());
		Point oldCentre = visArea.getCenter();
		Point newCentre = new Point(oldCentre.getX() + deltaX, oldCentre.getY() + deltaY,
				oldCentre.getSpatialReference());
		System.out.println(mapScale);
		mapView.setViewpoint(new Viewpoint(newCentre, mapScale));
		System.out.println(newCentre);
	}

	private void zoomAtPoint(Point centre, double factor) {
		double oldScale = mapView.getMapScale();
		double newScale = MathUtil.constrain(oldScale * factor, map.getMaxScale(), map.getMinScale());
		// zoom change
		factor = newScale / oldScale;

		// zooming affects offset
		Envelope viewArea = mapView.getVisibleArea().getExtent();
		Point viewCentre = viewArea.getCenter();
		assert centre.getSpatialReference().equals(viewCentre.getSpatialReference());
		// newViewCentre = lerp (centre, viewCentre, factor)
		Point newViewCentre = new Point(
				viewCentre.getX() * (factor) + centre.getX() * (1 - factor),
				viewCentre.getY() * (factor) + centre.getY() * (1 - factor), viewCentre.getSpatialReference());
		Envelope newViewArea = new Envelope(newViewCentre, viewArea.getWidth() * factor, viewArea.getHeight() * factor);
		// mapView.setViewpointAsync(new Viewpoint(newViewCentre, newScale));
		setConstrainedViewpoint(newViewArea, newScale);
	}
	
	private void updateMinScale() {
		// Basically unit bashing
		map.setMinScale(MathUtil.min(PIXELS_PER_UNIT_X*(X_MAX-X_MIN)/mapView.getWidth(), PIXELS_PER_UNIT_Y*(Y_MAX-Y_MIN)/mapView.getHeight()));
	}
	
	private void setHighlight() {
		
		
		
	}

	/**
	 * run this initially to update province owner, change feature in each
	 * FeatureLayer to be visible/invisible depending on owner. Can also update
	 * graphics initially
	 */
	private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {
		map = new ArcGISMap(Basemap.Type.OCEANS, 41.883333, 12.5, 0);
		//map.getLoadSettings().setFeatureRequestMode(FeatureRequestMode.MANUAL_CACHE);
		map.getBasemap().getReferenceLayers().remove(0);
		map.setMinScale(3E7);
		map.setMaxScale(3E6);
		mapView.setMap(map);
		EventHandler<? super MouseEvent> oldDragged = mapView.getOnMouseDragged();
		mapView.setOnMousePressed((e)->{
			mouseAnchor = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));
		});

		mapView.widthProperty().addListener((e, prev, next) -> updateMinScale());
		
		mapView.heightProperty().addListener((e, prev, next) -> updateMinScale());
		
		mapView.setOnMouseDragged(e -> {
			if(mouseAnchor != null) {
				Point mouseLoc = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));
				Envelope visArea = mapView.getVisibleArea().getExtent();
				Point oldCentre = visArea.getCenter();
				Point newCentre = new Point(
						oldCentre.getX() + mouseAnchor.getX() - mouseLoc.getX(),
						oldCentre.getY() + mouseAnchor.getY() - mouseLoc.getY(),
						visArea.getSpatialReference());
				Envelope newVisArea = new Envelope(newCentre, visArea.getWidth(), visArea.getHeight());
				setConstrainedViewpoint(newVisArea, mapView.getMapScale());
			}
		});
		EventHandler<? super ScrollEvent> oldScrolled = mapView.getOnScroll();
		// mapView.on
		mapView.setOnScroll(e -> {
			Point mapPos = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));

			zoomAtPoint(mapPos, 1 - 0.001 * e.getDeltaY());
		});
		mapView.addViewpointChangedListener((e)->setConstrainedViewpoint(mapView.getVisibleArea().getExtent(), mapView.getMapScale()));

		// note - tried having different FeatureLayers for AI and human provinces to
		// allow different selection colors, but deprecated setSelectionColor method
		// does nothing
		// so forced to only have 1 selection color (unless construct graphics overlays
		// to give color highlighting)
		GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
		gpkg_provinces.loadAsync();
		gpkg_provinces.addDoneLoadingListener(() -> {
			if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
				// create province border feature
				featureLayer_provinces = createFeatureLayer(gpkg_provinces);
				map.getOperationalLayers().add(featureLayer_provinces);

			} else {
				System.out.println("load failure");
			}
		});

		addAllPointGraphics();
	}

	// Called during init time.
	private void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
		mapView.getGraphicsOverlays().clear();

		InputStream inputStream = new FileInputStream(new File("src/unsw/gloriaromanus/provinces_label.geojson"));

		// Contains all of the locations and labels for provinces.
		FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

		GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

		for (org.geojson.Feature f : fc.getFeatures()) {
			if (f.getGeometry() instanceof org.geojson.Point) {
				org.geojson.Point p = (org.geojson.Point) f.getGeometry();
				LngLatAlt coor = p.getCoordinates();
				Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
				PictureMarkerSymbol s = null;
				String province = (String) f.getProperty("name");
				String faction = provinceToOwningFactionMap.get(province);

				TextSymbol t = new TextSymbol(10,
						faction + "\n" + province + "\n" + provinceToNumberTroopsMap.get(province), 0xFFFF0000,
						HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);

				switch (faction) {
				case "Gaul":
					s = new PictureMarkerSymbol("images/Celtic_Druid.png");
					break;
				case "Rome":
					s = new PictureMarkerSymbol("images/legionary.png");
					break;
				}
				t.setHaloColor(0xFFFFFFFF);
				t.setHaloWidth(2);
				Graphic gPic = new Graphic(curPoint, s);
				Graphic gText = new Graphic(curPoint, t);
				//graphicsOverlay.getGraphics().add(gPic);
				graphicsOverlay.getGraphics().add(gText);
				graphicsOverlay.setMinScale(1.5E7);
			} else {
				// badlyness
				System.out.println("Non-point geo json object in file");
			}
		}

		inputStream.close();
		mapView.getGraphicsOverlays().add(graphicsOverlay);
	}

	private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
		// Ez: ?? taking a random feature table?
		FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);
		// Make sure a feature table was found in the package
		if (geoPackageTable_provinces == null) {
			System.out.println("no geoPackageTable found");
			return null;
		}

		System.out.println("Feature fields:" + geoPackageTable_provinces);

		// Create a layer to show the feature table
		FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

		// https://developers.arcgis.com/java/latest/guide/identify-features.htm
		// listen to the mouse clicked event on the map view
		mapView.setOnMouseClicked(e -> {
			// was the main button pressed?
			if (e.getButton() == MouseButton.PRIMARY) {
				// get the screen point where the user clicked or tapped
				Point2D screenPoint = new Point2D(e.getX(), e.getY());
				// specifying the layer to identify, where to identify, tolerance around point,
				// to return pop-ups only, and
				// maximum results
				// note - if select right on border, even with 0 tolerance, can select multiple
				// features - so have to check length of result when handling it
				final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp,
						screenPoint, 0, false, 25);

				// add a listener to the future
				identifyFuture.addDoneListener(() -> {
					try {
						// get the identify results from the future - returns when the operation is
						// complete
						IdentifyLayerResult identifyLayerResult = identifyFuture.get();
						// a reference to the feature layer can be used, for example, to select
						// identified features
						if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
							FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
							// select all features that were identified
							List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f)
									.collect(Collectors.toList());

							if (features.size() > 1) {
								printMessageToTerminal(
										"Have more than 1 element - you might have clicked on boundary!");
							} else if (features.size() == 1) {
								// note maybe best to track whether selected...
								Feature f = features.get(0);
								String province = (String) f.getAttributes().get("name");

								if (provinceToOwningFactionMap.get(province).equals(humanFaction)) {
									// province owned by human
									if (currentlySelectedHumanProvince != null) {
										featureLayer.unselectFeature(currentlySelectedHumanProvince);
									}
									currentlySelectedHumanProvince = f;
									invading_province.setText(province);
								} else {
									if (currentlySelectedEnemyProvince != null) {
										featureLayer.unselectFeature(currentlySelectedEnemyProvince);
									}
									currentlySelectedEnemyProvince = f;
									opponent_province.setText(province);
								}

								featureLayer.selectFeature(f);
							}
						}
					} catch (InterruptedException | ExecutionException ex) {
						// ... must deal with checked exceptions thrown from the async identify
						// operation
						System.out.println("InterruptedException occurred");
					}
				});
			}
		});
		return flp;
	}

	private Map<String, String> getProvinceToOwningFactionMap() throws IOException {
		String content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
		JSONObject ownership = new JSONObject(content);
		Map<String, String> m = new HashMap<String, String>();
		for (String key : ownership.keySet()) {
			// key will be the faction name
			JSONArray ja = ownership.getJSONArray(key);
			// value is province name
			for (int i = 0; i < ja.length(); i++) {
				String value = ja.getString(i);
				m.put(value, key);
			}
		}
		return m;
	}

	private ArrayList<String> getHumanProvincesList() throws IOException {
		// https://developers.arcgis.com/labs/java/query-a-feature-layer/

		String content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
		JSONObject ownership = new JSONObject(content);
		return ArrayUtil.convert(ownership.getJSONArray(humanFaction));
	}

	private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
		String content = Files
				.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
		JSONObject provinceAdjacencyMatrix = new JSONObject(content);
		return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
	}

	private void resetSelections() {
		featureLayer_provinces
				.unselectFeatures(Arrays.asList(currentlySelectedEnemyProvince, currentlySelectedHumanProvince));
		currentlySelectedEnemyProvince = null;
		currentlySelectedHumanProvince = null;
		invading_province.setText("");
		opponent_province.setText("");
	}

	private void printMessageToTerminal(String message) {
		output_terminal.appendText(message + "\n");
	}

	/**
	 * Stops and releases all resources used in application.
	 */
	void terminate() {

		if (mapView != null) {
			mapView.dispose();
		}
	}
}