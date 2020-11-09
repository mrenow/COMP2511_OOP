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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import util.ArrayUtil;
import util.MathUtil;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable.FeatureRequestMode;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
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
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol.Style;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.engine.*;





public class GloriaRomanusController {

	private GameController game;
	
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
	private Map<String, ProvinceFeatureInfo> provinceFeatureMap = new HashMap<>();
	
	

	// Symbols
	private Map<FactionType, SimpleFillSymbol> factionSymbolMap = new EnumMap<>(FactionType.class);
	private static final FillSymbol CAN_MOVE_SYMBOL = new SimpleFillSymbol(Style.FORWARD_DIAGONAL, 0xC000A0F0, null);
	private static final FillSymbol CAN_ATTACK_SYMBOL = new SimpleFillSymbol(Style.DIAGONAL_CROSS, 0xA0F000A0, null);
	private static final FillSymbol ON_HOVER_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x60F0E040, 2));	
	private static final MarkerSymbol ATTACK_ICON = new PictureMarkerSymbol("images/legionary.png");

	private static final Symbol NO_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , null);
	
	// Faction highlight
	private static final int COLOUR_LAYER = 0;
	private static final int PATTERN_LAYER = 1; 
	private static final int HIGHLIGHT_LAYER = 2; 
	private static final int MARKER_LAYER = 3; 
	private static final int LABEL_LAYER = 4; 
	private static final int NUM_LAYERS = 5; 
	
	private GraphicsOverlay[] overlays = new GraphicsOverlay[NUM_LAYERS];	
	
	private Graphic uniqueHoverMarker = new Graphic(new Point(0,0), NO_SYMBOL);
	private Graphic uniqueHoverOutline = new Graphic(new Point(0,0), NO_SYMBOL);
	
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
		game = new GameController("src/unsw/gloriaromanus/province_id_adjacent.json", "src/unsw/gloriaromanus/landlocked_provinces.json", List.of(FactionType.ROME, FactionType.GAUL)); // TODO NUM FACTINOS
		
		
		// Initialize feature map. Still Shape and Point fields remaining.
		int id = 0;
		for(Province p : game.getProvinces(null)) {
			provinceFeatureMap.put(p.getName(), new ProvinceFeatureInfo(id, p));
			id++;
		}
		// TODO: better algorithm than just randomly generating colours
		for (Faction f : game.getFactions()) {
			factionSymbolMap.put(f.getType(), new SimpleFillSymbol(Style.SOLID, 0x88000000 + new Random().nextInt(0x1000000), null));
		}
		factionSymbolMap.put(FactionType.NO_ONE, new SimpleFillSymbol(Style.SOLID, 0x88000000 + new Random().nextInt(0x1000000), null));
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

	// RUBBISH
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

		// testing
		mapView.setOnKeyTyped(this::debugActions);
		// Overlays
		
		for(int i = 0; i < overlays.length; i ++) {
			overlays[i] = new GraphicsOverlay();
			mapView.getGraphicsOverlays().add(overlays[i]);
		}
		
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
				System.out.println(featureLayer_provinces.getFeatureTable().getTotalFeatureCount());

				initProvinceShapes(featureLayer_provinces);
			} else {
				System.out.println("load failure");
			}
		});
		FeatureCollection fc = new ObjectMapper().readValue(new File("src/unsw/gloriaromanus/provinces_label.geojson"), FeatureCollection.class);
		initProvinceCentres(fc);
		
	}
	private void initProvinceShapes(FeatureLayer provinceShapeLayer) {
		QueryParameters provinceParams = new QueryParameters();
		provinceParams.setWhereClause("");
		ListenableFuture<FeatureQueryResult> result = provinceShapeLayer.getFeatureTable().queryFeaturesAsync(provinceParams);
		result.addDoneListener(()->{
			// TODO : find out whether not done case actually matters
			try {
				for(Feature f: result.get()) {
					String name = (String)f.getAttributes().get("name");
					provinceFeatureMap.get(name).setShape((Polygon)f.getGeometry());		
				}
				// All set up to initialize graphics
				List<ProvinceFeatureInfo> provinceFeatures = new ArrayList<>(provinceFeatureMap.values());
				Collections.sort(provinceFeatures);
				
				for(ProvinceFeatureInfo pfi : provinceFeatures) {
					Graphic g;
					// for other potential layer initializations.
					g = new Graphic(pfi.getShape(), factionSymbolMap.get(pfi.getOwner().getType()));
					overlays[COLOUR_LAYER].getGraphics().add(g);

					g = new Graphic(pfi.getShape(), NO_SYMBOL);
					overlays[PATTERN_LAYER].getGraphics().add(g);
					
					g = new Graphic(pfi.getShape(), NO_SYMBOL);
					overlays[HIGHLIGHT_LAYER].getGraphics().add(g);
				}
				overlays[HIGHLIGHT_LAYER].getGraphics().add(uniqueHoverOutline);
				
						
			} catch (InterruptedException | ExecutionException e) {
				System.out.print("Async was interrupted");
				e.printStackTrace();
			}
		});
	}
	private void initProvinceCentres(FeatureCollection provincePointCollection) {
		GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
	
		for (org.geojson.Feature f : provincePointCollection.getFeatures()) {
			if (f.getGeometry() instanceof org.geojson.Point) {
				org.geojson.Point p = (org.geojson.Point) f.getGeometry();
				LngLatAlt coor = p.getCoordinates();
				Point centre = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
				String name = (String) f.getProperty("name");
				provinceFeatureMap.get(name).setCentre(centre);
			} else {
				// badlyness
				System.out.println("Non-point geo json object in file");
			}
		}
		// All ready to set up point graphics
		List<ProvinceFeatureInfo> provinceFeatures = new ArrayList<>(provinceFeatureMap.values());
		Collections.sort(provinceFeatures);
		for(ProvinceFeatureInfo pfi : provinceFeatures) {
			Graphic g;
			// for other potential layer initializations.
			g = new Graphic(pfi.getCentre(), NO_SYMBOL);
			overlays[MARKER_LAYER].getGraphics().add(g);
			
			TextSymbol ts = new TextSymbol(10, pfi.getName(), 0xFFFF0000,
					HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
			ts.setHaloColor(0xFFFFFFFF);
			ts.setHaloWidth(2);
			g = new Graphic(pfi.getCentre(), ts);
			overlays[LABEL_LAYER].getGraphics().add(g);
			
		}
		overlays[LABEL_LAYER].setMinScale(1.5E7);
		overlays[MARKER_LAYER].getGraphics().add(uniqueHoverMarker);
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
		
		mapView.setOnMouseMoved(e -> {
			final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp,
					new Point2D(e.getX(), e.getY()), 0, false, 25);

			// add a listener to the future
			identifyFuture.addDoneListener(() -> {
				try {
					IdentifyLayerResult identifyLayerResult = identifyFuture.get();
					if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
						FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
						if(identifyLayerResult.getElements().size()==1) {
							// note maybe best to track whether selected...
							System.out.println("hey listen");
							Feature f = (Feature) identifyLayerResult.getElements().get(0);
							String province = (String) f.getAttributes().get("name");
							setUniqueMarker(uniqueHoverMarker, province, ATTACK_ICON);
							setUniqueShape(uniqueHoverOutline, province, ON_HOVER_SYMBOL);
							
							//setNamedProvinceSymbols(List.of(province), MARKER_LAYER, ATTACK_ICON);
						}

					}
				} catch (InterruptedException | ExecutionException ex) {
					// ... must deal with checked exceptions thrown from the async identify
					// operation
					ex.printStackTrace();
					System.out.println("InterruptedException occurred");
				}
			});
		});
		mapView.setOnMouseClicked(e -> {
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
							
							if(e.getButton() == MouseButton.PRIMARY) {
								setNamedProvinceSymbols(List.of(province), PATTERN_LAYER, CAN_MOVE_SYMBOL);
							}else {
								setNamedProvinceSymbols(List.of(province), PATTERN_LAYER, CAN_ATTACK_SYMBOL);
									
							}
							
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
					ex.printStackTrace();
					System.out.println("InterruptedException occurred");
				}
			});
			
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
	
	private void setProvinceSymbols(Collection<Province> provinces, int layer, Symbol symb) {
		for(Province p : provinces) {
			ProvinceFeatureInfo pfi = provinceFeatureMap.get(p.getName());
			overlays[layer].getGraphics().get(pfi.getId()).setSymbol(symb);	
		}
	}
	private void setNamedProvinceSymbols(Collection<String> provinceNames, int layer, Symbol symb) {
		for(String name : provinceNames) {
			ProvinceFeatureInfo pfi = provinceFeatureMap.get(name);
			overlays[layer].getGraphics().get(pfi.getId()).setSymbol(symb);	
		}
	}
	private void setUniqueMarker(Graphic g, Province p, Symbol symb) {
		g.setGeometry(provinceFeatureMap.get(p.getName()).getCentre());
		g.setSymbol(symb);
	}
	private void setUniqueMarker(Graphic g, String s, Symbol symb) {
		g.setGeometry(provinceFeatureMap.get(s).getCentre());
		g.setSymbol(symb);
	}
	private void setUniqueShape(Graphic g, Province p, Symbol symb) {
		g.setGeometry(provinceFeatureMap.get(p.getName()).getShape());
		g.setSymbol(symb);
	}
	private void setUniqueShape(Graphic g, String s, Symbol symb) {
		g.setGeometry(provinceFeatureMap.get(s).getShape());
		g.setSymbol(symb);
	}
	
	private void clearGraphicLayer(int layer) {
		for(Graphic g : overlays[layer].getGraphics()) {
			g.setSymbol(NO_SYMBOL);
		}
		
	}
	
	/**
	 * Stops and releases all resources used in application.
	 */
	void terminate() {

		if (mapView != null) {
			mapView.dispose();
		}
	}
	private void debugActions(KeyEvent e) {
		switch(e.getCharacter()) {
		case "1":
			
			break;
		case "2":
			
			break;
		case "c":
			clearGraphicLayer(PATTERN_LAYER);
			break;
		}
		
	}
}