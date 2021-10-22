import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;

public class UIController {
    //The order these things are initilized in is: Constructor, @FXML loaded, then initilize() (Constructor can't access @FXML fields)
    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML
    private Button addMoney;
    @FXML
    private Button subMoney;
    @FXML
    private PieChart pieGraph;
    @FXML
    private TextField item;
    @FXML
    private TextField category;
    @FXML
    private TextField price;
    @FXML 
    private ListView<String> chargeList;

    //Location is the location of FXML document, so sure we need it but it automacically gets loaded in
    @FXML
    private URL location;
    //This is a java object that can also be automatically loaded, but I'm not sure what it's for
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
    private void savePosCharge(){
        saveCharge('+');
    }

    @FXML
    private void saveNegCharge(){
        saveCharge('-');
    }
    
    @FXML
    private void saveCharge(char sign){
        String i = item.getText();
        String p = price.getText();
        String c = category.getText();
        String line = "";
        if(sign == '+'){
            line = "+ " + i + p + c;
        }else if (sign == '-'){
            line = "- " + i + p + c;
        }
        chargeList.getItems().add(line);
    }
}
