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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
import unsw.engine.VicCondition.*;
import unsw.ui.TopBar;
import unsw.ui.Observer.MsgObserverable;
import unsw.ui.Observer.Observable;
import unsw.ui.Observer.Subject;





public class GloriaRomanusController extends Controller{


	private GameController game;
	private MapController mapController;
	
	private MsgObserverable turnchange = new MsgObserverable();

	@FXML
	private void initialize() throws Exception {
		// TODO = you should rely on an object oriented design to determine ownership
		game = new GameController("src/unsw/gloriaromanus/province_id_adjacent.json",
				"src/unsw/gloriaromanus/landlocked_provinces.json",
				List.of(FactionType.ROME,
						FactionType.GAUL,
						FactionType.CARTHAGE,
						FactionType.PARTHIA,
						FactionType.BRITAIN));
		
		mapController = new MapController(game);
		
		GloriaRomanusApplication.loadExistingController(mapController, "src/unsw/gloriaromanus/map.fxml");
		// adds to the first index of the child list
		((Pane)getRoot()).getChildren().add(0, mapController.getRoot());
		

		//topbar observer and observerable implement

		
		VicComposite vic = generateVic();
		game.setVic(vic);
		displayInfo();
		game.setTurnObserverable(turnchange);
	}

	private VicComposite generateVic(){
		VicLeaf l1 = new VicLeaf(VictoryCondition.CONQUEST);
		VicLeaf l2 = new VicLeaf(VictoryCondition.WEALTH);
		VicLeaf l3 = new VicLeaf(VictoryCondition.TREASURY);
		VicComposite vic1 = new VicComposite(VictoryCondition.AND);
		VicComposite vic2 = new VicComposite(VictoryCondition.OR);

		vic2.addSubVic(l2);
		vic2.addSubVic(l3);
		vic1.addSubVic(l1);
		vic1.addSubVic(vic2);
		return vic1;
	}

	@FXML
	private HBox topbox;
	private TopBar topBar;
	private void displayInfo(){
		this.topBar=new TopBar(topbox, game);
		
		turnchange.attach(topBar);
		
	}
	
	@Override
	void terminate() {
		mapController.terminate();
	}
	
}