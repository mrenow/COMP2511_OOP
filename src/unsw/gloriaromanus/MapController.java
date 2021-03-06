package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
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
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol.Style;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import unsw.engine.Faction;
import unsw.engine.FactionType;
import unsw.engine.GameController;
import unsw.engine.ItemType;
import unsw.engine.Province;
import unsw.engine.Unit;
import unsw.ui.Observer.Observable;
import unsw.ui.Observer.Observer;
import util.MathUtil;

public class MapController extends Controller{
	private GameController game;
	
	@FXML
	private MapView mapView;
	private ArcGISMap map;
	
	private FeatureLayer featureLayer_provinces;
	private Map<String, ProvinceFeatureInfo> provinceFeatureMap = new HashMap<>();
	
	private Observable<ProvinceMouseEvent> triggerProvinceSelected = new Observable<ProvinceMouseEvent>();
	
	private ListProperty<Unit> unitSelection = new SimpleListProperty<Unit>();

	// Symbols
	private Map<FactionType, Symbol> factionSymbolMap = new EnumMap<>(FactionType.class);
	private static final FillSymbol CAN_MOVE_SYMBOL = new SimpleFillSymbol(Style.FORWARD_DIAGONAL, 0xC000A0F0, null);
	private static final FillSymbol CAN_ATTACK_SYMBOL = new SimpleFillSymbol(Style.DIAGONAL_CROSS, 0xA0F000A0, null);

	private static final FillSymbol SELECT_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x70FFD000, 6));
	private static final FillSymbol ON_HOVER_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x60F0F080, 2));	
	private static final FillSymbol ATTACK_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x60F000A0, 6));	
	private static final FillSymbol MOVE_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x6030F0F0, 6));	
	private static final FillSymbol ACTION_SYMBOL = new SimpleFillSymbol(Style.NULL, 0 , new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0x6000F000, 6));	
	private static final MarkerSymbol ATTACK_ICON = new PictureMarkerSymbol(Images.INVADE_ICON);
	private static final MarkerSymbol MOVE_ICON = new PictureMarkerSymbol(Images.MOVE_ICON);
	

	private static final Symbol NO_SYMBOL = new SimpleFillSymbol(Style.SOLID, 0x001111 , null);
	
	// Faction highlight
	private static final int COLOUR_LAYER = 0;
	private static final int PATTERN_LAYER = 1; 
	private static final int HIGHLIGHT_LAYER = 2; 
	private static final int LABEL_LAYER = 3; 
	private static final int MARKER_LAYER = 4; 
	private static final int NUM_LAYERS = 5; 
	
	private GraphicsOverlay[] overlays = new GraphicsOverlay[NUM_LAYERS];	
	
	private Graphic uniqueHoverMarker = new Graphic(new Point(0,0), NO_SYMBOL);
	private Graphic dummyMarkerGraphic = new Graphic(new Point(0,0), NO_SYMBOL);
	
	private Graphic uniqueHoverOutline = new Graphic(new Point(0,0), NO_SYMBOL);
	private Graphic uniqueTargetOutline = new Graphic(new Point(0,0), NO_SYMBOL);
	private Graphic uniqueActionOutline = new Graphic(new Point(0,0), NO_SYMBOL);
	
	
	// Map constraints
	private final double X_MAX = 5E6;
	private final double X_MIN = -3E6;
	private final double Y_MAX = 8E6;
	private final double Y_MIN = 2E6;
	// Map scale derived manually >:(
	private final double PIXELS_PER_UNIT_X = 3778;
	private final double PIXELS_PER_UNIT_Y = 3842;

	private final double ZOOM_RATE = 0.001;
	private final double MAX_SCALE = 3E6;
	
	private final int FONT_SIZE = 12;
	
	// Used for custom panning algorithm
	private Point mouseAnchor = null;
		
	public MapController(GameController game) {
		this.game = game;
		GloriaRomanusApplication.loadExistingController(this, "src/unsw/gloriaromanus/map.fxml");
	}
	
	@FXML
	private void initialize() throws Exception {
		// Generate symbol and feature structures. Province Feature map will only be fully initialized after feature layer initialized
		int id = 0;
		for(Province p : game.getProvinces(null)) {
			provinceFeatureMap.put(p.getName(), new ProvinceFeatureInfo(id, p));
			id++;
		}
		
		for (Faction f : game.getFactions()) {
			factionSymbolMap.put(f.getType(), new SimpleFillSymbol(Style.SOLID, game.getFactionColour(f), null));
		}
		
		factionSymbolMap.put(FactionType.NO_ONE, NO_SYMBOL);
		
		
		
		map = new ArcGISMap(Basemap.Type.OCEANS, 41.883333, 12.5, 0);

		map.getBasemap().getReferenceLayers().remove(0);
		
		map.setMaxScale(MAX_SCALE);
		
		mapView.setMap(map);

		// Update scale bounds on resize
		mapView.widthProperty().addListener((e, prev, next) -> updateMinScale());
		mapView.heightProperty().addListener((e, prev, next) -> updateMinScale());
		
		// Dragging scheme:
		mapView.setOnMousePressed(e -> mouseAnchor = mapView.screenToLocation(new Point2D(e.getX(), e.getY())));
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
		
		mapView.setOnScroll(e -> {
			Point centre = mapView.screenToLocation(new Point2D(e.getX(), e.getY()));
			zoomAtPoint(centre, 1 - ZOOM_RATE * e.getDeltaY());
		});
		
		mapView.addViewpointChangedListener(e -> setConstrainedViewpoint(mapView.getVisibleArea().getExtent(), mapView.getMapScale()));

		// Province event handling
		mapView.setOnMouseMoved(provinceToMouseEventHandler((e, provinceName) -> {
			ProvinceFeatureInfo pfi = provinceFeatureMap.get(provinceName);

			setUniqueShape(uniqueHoverOutline, provinceName, ON_HOVER_SYMBOL);
			if(isDestination(pfi)) {
				setUniqueMarker(uniqueHoverMarker, provinceName, MOVE_ICON);
			}else if(isAttackable(pfi)) {
				setUniqueMarker(uniqueHoverMarker, provinceName, ATTACK_ICON);
			}else {
				clearUniqueGraphic(uniqueHoverMarker);
			}
		}));
			
		mapView.setOnMouseClicked(provinceToMouseEventHandler((e, provinceName)->{
			// TODO : Sets patterns for testing only
			ProvinceFeatureInfo pfi = provinceFeatureMap.get(provinceName);
			triggerProvinceSelected.notifyUpdate(new ProvinceMouseEvent(pfi.getProvince(), e, isDestination(pfi), isAttackable(pfi)));	
		}));
		
		
		// Overlays
		
		for(int i = 0; i < overlays.length; i ++) {
			overlays[i] = new GraphicsOverlay();
			mapView.getGraphicsOverlays().add(overlays[i]);
		}

		FeatureCollection fc = new ObjectMapper().readValue(new File("src/unsw/gloriaromanus/provinces_label.geojson"), FeatureCollection.class);
		initProvinceCentres(fc);
		
		GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
		gpkg_provinces.loadAsync();
		gpkg_provinces.addDoneLoadingListener(() -> {
			if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
				// create province border feature
				initFeatureLayer(gpkg_provinces);
				initProvinceShapes(featureLayer_provinces);
				game.attatchProvinceChangedObserver(this::updateProvinceGraphics);
			} else {
				System.out.println("load failure");
			}
		});
		unitSelection.addListener((ListChangeListener<Unit>)(c -> updateActionOverlay()));
	}
	/**
	 *  called after province shape data is loaded, and after province point data is initialized
	 * @param provinceShapeLayer
	 */
	private void initProvinceShapes(FeatureLayer provinceShapeLayer) {
		QueryParameters provinceParams = new QueryParameters();
		provinceParams.setWhereClause("");
		ListenableFuture<FeatureQueryResult> result = provinceShapeLayer.getFeatureTable().queryFeaturesAsync(provinceParams);
		result.addDoneListener(()->{ try {
		// TODO : find out whether not done case actually matters
			for(Feature f: result.get()) {
				String name = (String)f.getAttributes().get("name");
				provinceFeatureMap.get(name).setShape((Polygon)f.getGeometry());		
			}
			// All set up to initialize fill graphics for each province
			List<ProvinceFeatureInfo> provinceFeatures = new ArrayList<>(provinceFeatureMap.values());
			// sorts in ID order.
			Collections.sort(provinceFeatures);
			
			for(ProvinceFeatureInfo pfi : provinceFeatures) {
				Graphic g;
				// for other potential layer initializations.
				// Faction colour map
				g = new Graphic(pfi.getShape(), NO_SYMBOL);
				overlays[COLOUR_LAYER].getGraphics().add(g);
				updateProvinceGraphics(pfi.getProvince());

				g = new Graphic(pfi.getShape(), NO_SYMBOL);
				overlays[PATTERN_LAYER].getGraphics().add(g);
				
				g = new Graphic(pfi.getShape(), NO_SYMBOL);
				overlays[HIGHLIGHT_LAYER].getGraphics().add(g);
			}
			overlays[HIGHLIGHT_LAYER].getGraphics().add(uniqueTargetOutline);
			overlays[HIGHLIGHT_LAYER].getGraphics().add(uniqueActionOutline);
			overlays[HIGHLIGHT_LAYER].getGraphics().add(uniqueHoverOutline);
			
					
		} catch (InterruptedException | ExecutionException e) {
			System.out.print("Async was interrupted");
			e.printStackTrace();
		}});
	}
	/**
	 * Called after province point data is loaded
	 * @param provincePointCollection
	 */
	private void initProvinceCentres(FeatureCollection provincePointCollection) {
	
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
		// All ready to initialize point graphics for each province
		List<ProvinceFeatureInfo> provinceFeatures = new ArrayList<>(provinceFeatureMap.values());
		Collections.sort(provinceFeatures);
		for(ProvinceFeatureInfo pfi : provinceFeatures) {
			Graphic g;
			// for other potential layer initializations.
			g = new Graphic(pfi.getCentre(), NO_SYMBOL);
			overlays[MARKER_LAYER].getGraphics().add(g);
		
			g = new Graphic(pfi.getCentre(), NO_SYMBOL);
			overlays[LABEL_LAYER].getGraphics().add(g);
		}
		overlays[LABEL_LAYER].setMinScale(2E7);
		overlays[MARKER_LAYER].getGraphics().add(uniqueHoverMarker);
		overlays[MARKER_LAYER].getGraphics().add(dummyMarkerGraphic);
	}
	
	private void initFeatureLayer(GeoPackage gpkg_provinces) {
		FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);
		
		// Make sure a feature table was found in the package
		if (geoPackageTable_provinces == null) {
			System.out.println("no geoPackageTable found");
			System.exit(1);
		}

		// Create a layer to show the feature table
		featureLayer_provinces = new FeatureLayer(geoPackageTable_provinces);

		map.getOperationalLayers().add(featureLayer_provinces);
	}
	
	private EventHandler<MouseEvent> provinceToMouseEventHandler(BiConsumer<MouseEvent, String> action){
		return (e) -> {
			if(featureLayer_provinces == null) return;
			final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(featureLayer_provinces,
					new Point2D(e.getX(), e.getY()), 0, false, 25);

			// add a listener to the future
			identifyFuture.addDoneListener(() -> {
				try {
					
					IdentifyLayerResult identifyLayerResult = identifyFuture.get();
					// Only trigger on unique result province exists
					if(identifyLayerResult.getElements().size()==1) {

						Feature f = (Feature) identifyLayerResult.getElements().get(0);
						String province = (String) f.getAttributes().get("name");
						action.accept(e, province);
					}else {

						clearUniqueGraphic(uniqueHoverMarker);
					}
				} catch (InterruptedException | ExecutionException ex) {
					// ... must deal with checked exceptions thrown from the async identify
					// operation
					ex.printStackTrace();
					System.out.println("InterruptedException occurred");
				}
			});
		};
	}

	
	private void setConstrainedViewpoint(Envelope visArea, double mapScale) {

		double deltaX = MathUtil.constrain(0, X_MIN - visArea.getXMin(), X_MAX - visArea.getXMax());
		double deltaY = MathUtil.constrain(0, Y_MIN - visArea.getYMin(), Y_MAX - visArea.getYMax());
		
		// Shift mouse anchor appropriately if it exists.
	
		if((deltaX != 0 || deltaY != 0) && mouseAnchor != null) {
			mouseAnchor = new Point(mouseAnchor.getX() + deltaX, mouseAnchor.getY() + deltaY , mouseAnchor.getSpatialReference());
		}	
		Point oldCentre = visArea.getCenter();
		Point newCentre = new Point(oldCentre.getX() + deltaX, oldCentre.getY() + deltaY,
				oldCentre.getSpatialReference());
		mapView.setViewpoint(new Viewpoint(newCentre, mapScale));
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
	
	// Observer<Province>
	private void updateProvinceGraphics(Province p) {
		
		String text = String.format("%s\n%d ????", p.getName(), p.getMilitaryIndex());
		TextSymbol ts = new TextSymbol(FONT_SIZE, text, toSolidColour(game.getFactionColour(p.getOwner())),
				HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
		// White outline
		ts.setHaloColor(0xFFFFFFFF);
		ts.setHaloWidth(2);
		setProvinceSymbols(List.of(p), LABEL_LAYER, ts);
		setProvinceSymbols(List.of(p), COLOUR_LAYER, factionSymbolMap.get(p.getOwner().getType()));
	}
	
	private boolean isAttackable(ProvinceFeatureInfo pfi) {
		return CAN_ATTACK_SYMBOL.equals(overlays[PATTERN_LAYER].getGraphics().get(pfi.getId()).getSymbol());
	}
	
	private boolean isDestination(ProvinceFeatureInfo pfi) {
		return CAN_MOVE_SYMBOL.equals(overlays[PATTERN_LAYER].getGraphics().get(pfi.getId()).getSymbol());
	}

	void updateTargetProvince(ProvinceMouseEvent p) {
		if(p == null) {
			clearUniqueGraphic(uniqueTargetOutline);
			return;
		}
		ProvinceFeatureInfo pfi = provinceFeatureMap.get(p.getName());
		if(isAttackable(pfi)) {
			setUniqueShape(uniqueTargetOutline, p.getName(), ATTACK_SYMBOL);
		} else if(isDestination(pfi)) {
			setUniqueShape(uniqueTargetOutline, p.getName(), MOVE_SYMBOL);
		} else {
			setUniqueShape(uniqueTargetOutline, p.getName(), SELECT_SYMBOL);
		}
		
		
	}
	
	void updateActionProvince(ProvinceMouseEvent p) {
		if(p == null) {
			clearUniqueGraphic(uniqueTargetOutline);
			return;
		}
		setUniqueShape(uniqueActionOutline, p.getName(), ACTION_SYMBOL);
	}

	
	void updateActionOverlay() {
		clearGraphicLayer(PATTERN_LAYER);
		setProvinceSymbols(game.getDestinations(unitSelection), PATTERN_LAYER, CAN_MOVE_SYMBOL);
		setProvinceSymbols(game.getAttackable(unitSelection), PATTERN_LAYER, CAN_ATTACK_SYMBOL);
	}
	
	void setProvinceSymbols(Collection<Province> provinces, int layer, Symbol symb) {
		for(Province p : provinces) {
			ProvinceFeatureInfo pfi = provinceFeatureMap.get(p.getName());
			overlays[layer].getGraphics().get(pfi.getId()).setSymbol(symb);	
		}
	}

	void setNamedProvinceSymbols(Collection<String> provinceNames, int layer, Symbol symb) {
		for(String name : provinceNames) {
			ProvinceFeatureInfo pfi = provinceFeatureMap.get(name);
			overlays[layer].getGraphics().get(pfi.getId()).setSymbol(symb);	
		}
	}
	void setUniqueMarker(Graphic g, Province p, Symbol symb) {
		if(p == null) {
			clearUniqueGraphic(g);
			return;
		}
		g.setGeometry(provinceFeatureMap.get(p.getName()).getCentre());
		g.setSymbol(symb);
	}
	void setUniqueMarker(Graphic g, String s, Symbol symb) {
		if(s == null) {
			clearUniqueGraphic(g);
			return;
		}
		g.setGeometry(provinceFeatureMap.get(s).getCentre());
		g.setSymbol(symb);
	}
	void setUniqueShape(Graphic g, Province p, Symbol symb) {
		if(p == null) {
			clearUniqueGraphic(g);
			return;
		}
		g.setGeometry(provinceFeatureMap.get(p.getName()).getShape());
		g.setSymbol(symb);
	}
	void setUniqueShape(Graphic g, String s, Symbol symb) {
		if(s == null) {
			clearUniqueGraphic(g);
			return;
		}
		g.setGeometry(provinceFeatureMap.get(s).getShape());
		g.setSymbol(symb);
	}
	
	void clearGraphicLayer(int layer) {
		for(Graphic g : overlays[layer].getGraphics()) {
			g.setSymbol(NO_SYMBOL);
		}		
	}
	void clearUniqueGraphic(Graphic unique) {
		unique.setGeometry(new Point(0,0));
		// wtf arcgis???????
		// WHy are you Like this
		unique.setVisible(true);
	}

	@Override
	void terminate() {

		if (mapView != null) {
			mapView.dispose();
		}
	}
	
    ListProperty<Unit> getUnitSelectionProperty() {
    	return unitSelection;
    }
 
    void attachProvinceSelectedObserver(Observer<ProvinceMouseEvent> o) {
    	triggerProvinceSelected.attach(o);
    }
    

	private Integer toSolidColour(Integer colour) {
		return 0xFF000000 | colour;
	}

}
	