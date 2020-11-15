package unsw.ui.VicUI;

import java.io.FileInputStream;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import unsw.gloriaromanus.Controller;
import unsw.gloriaromanus.GloriaRomanusApplication;
import unsw.ui.UIPath;

public class VicUIController extends Controller{

    @FXML private StackPane pane;
    @FXML private ImageView vicImage;
    @FXML
    private void initialize()throws Exception{
        try {
            Image im = new Image(new FileInputStream("src/unsw/ui/VicUI/Victory.JPG"));
            vicImage.setImage(im);
            
        

        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("stupid");
        }
        
    }
    @FXML
    private void quit(){
        Controller controller = GloriaRomanusApplication.loadController(UIPath.MENU.getPath());
        GloriaRomanusApplication.app.setScene(controller);
    }
}
