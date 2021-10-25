import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.util.ResourceBundle;

public class UIController implements Initializable {
    //The order these things are initilized in is: Constructor, @FXML loaded, then initilize() (Constructor can't access @FXML fields)
    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML
    private Button addMoney;
    @FXML
    private Button subMoney;
    @FXML
    private PieChart pieGraph;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Entertainment", 2),
                        new PieChart.Data("Food", 25),
                        new PieChart.Data("Transportation", 50),
                        new PieChart.Data("Home & Utilities", 3),
                        new PieChart.Data("Personal & Family Care", 3));
        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " amount: ", data.pieValueProperty()
                        )
                )
        );
        pieGraph.getData().addAll(pieChartData);
    }

    @FXML
    private TextField item;
    @FXML
    private ChoiceBox<String> category;
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
    private void initialize(){
        populateCategories();
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
        String c = category.getValue();
        String line = "";
        if(sign == '+'){
            line = "+ " + i + p + c;
        }else if (sign == '-'){
            line = "- " + i + p + c;
        }
        chargeList.getItems().add(line);
    }

    @FXML
    private void populateCategories(){
        category.getItems().add("Choice 1");
        category.getItems().add("Choice 2");
        category.getItems().add("Choice 3");
    }
}
