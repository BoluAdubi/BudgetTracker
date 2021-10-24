import java.net.URL;
import java.util.ResourceBundle;

import budgettracker.Transaction;
import budgettracker.UserAccount;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

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
    private ChoiceBox<String> category;
    @FXML
    private TextField price;
    @FXML 
    private TableView<Transaction> transactionTable;
    @FXML
    public TableColumn<Transaction, Integer> itemCol;
    @FXML
    public TableColumn<Transaction, String> priceCol;
    @FXML
    public TableColumn<Transaction, String> categoryCol;
    @FXML
    public TableColumn<Transaction, String> signCol;

    //Location is the location of FXML document, so sure we need it but it automacically gets loaded in
    @FXML
    private URL location;
    //This is a java object that can also be automatically loaded, but I'm not sure what it's for
    @FXML
    private ResourceBundle resources;

    UserAccount account = new UserAccount();

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

        //store transaction in account
        account.newTransaction(i, Double.parseDouble(p), c, sign);

        //save respective values to table
        itemCol.setCellValueFactory(new PropertyValueFactory<>("Item"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("Category"));
        signCol.setCellValueFactory(new PropertyValueFactory<>("Sign"));

        transactionTable.setItems(account.getTransactions());
    }

    @FXML
    private void populateCategories(){
        String categories[] = { "choice 1", "choice 2", "choice 3", "choice 4", "choice 5" };
        category.setItems(FXCollections.observableArrayList(categories));
    }
}
