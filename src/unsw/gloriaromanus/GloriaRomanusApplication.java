package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GloriaRomanusApplication extends Application {
	public static GloriaRomanusApplication app;
	
	private Controller mainController;
    private Scene scene;
	
  @Override
  public void start(Stage stage) throws IOException {
    // set up the scene
   app = this;
   /*
	mainController = loadController("src/unsw/gloriaromanus/main.fxml");
    scene = new Scene(mainController.getRoot());
    // set up the stage
    */

    // Controller controller = GloriaRomanusApplication.loadController("src/unsw/ui/MainMenu/MainMenu.fxml");
    // GloriaRomanusApplication.app.setScene(controller);
    mainController =loadController("src/unsw/ui/MainMenu/MainMenu.fxml");
    scene = new Scene(mainController.getRoot());
    stage.setTitle("Gloria Romanus");
    stage.setWidth(1200);
    stage.setHeight(800);
    stage.setScene(scene);
    stage.show();

  }
  public static <T extends Controller> T loadController(String fileName) {
	try {
		FXMLLoader loader = new FXMLLoader(new File(fileName).toURI().toURL());
		Parent root = loader.load();
		T c = loader.getController();
		c.setRoot(root);
		return c;
	}catch(IOException e){
		System.err.println("Failed to Load!!");
		e.printStackTrace();
		return null;
	}
  }
  
  
  // Loads nodes into existing controller
  public static Controller loadExistingController(Controller c, String fileName ){
	try {
		FXMLLoader loader = new FXMLLoader(new File(fileName).toURI().toURL());
		loader.setController(c);
		Parent root = loader.load();
		c.setRoot(root);
	}catch(IOException e){
		System.err.println("Failed to Load!!");
		e.printStackTrace();
		return null;
	}
	return c;
  }
  
  public void setScene(Controller c) {
	  // Switch main controller
	  mainController.terminate();
	  scene.setRoot(c.getRoot());
	  mainController = c;
  }
  public Parent getSceneRoot() {
	  return scene.getRoot();
  }
  public Controller getMainController() {
	  return mainController;
  }
  
  public void displayText(Object txt) {
	  System.out.println(txt.toString());
	  
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    mainController.terminate();
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    launch(args);
  }
}