package unsw.ui;

import java.io.FileInputStream;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import unsw.gloriaromanus.Controller;

public class VicUIController extends Controller{

    @FXML
    private void initialize()throws Exception{
        try {
            Image im = new Image(new FileInputStream("src/unsw/ui/Victory.JPG"));
            
            ImageView image = new ImageView(im);
            StackPane pane = new StackPane(image);
            

        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("stupid");
        }
        
    }
}
