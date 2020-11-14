package unsw.ui.LoadSave;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoadSaveTest extends Application{

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadSave.fxml"));
        Parent root = loader.load();
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
