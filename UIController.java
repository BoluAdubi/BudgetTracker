import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UIController {

    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML
    private Button button;
    @FXML
    private Label label;
    @FXML
    private TextField textfield;

    //I'm not sure what these two are for or if we need them
    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    //public constructor, params must be empty
    //Even if it stays empty forever we cannot delete
    //Or it will fail to instatiate 
    public UIController(){

    }

    //Function will be called when everything has loaded
    //Must be void, cannot have params
    @FXML
    private void initilize(){
        System.out.println("initilized");
    }

    @FXML
    private void getTextField(){
        String textValue = textfield.getText();
        label.setText(textValue);
    }
}
