import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import budgettracker.Transaction;
import budgettracker.UserAccount;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.TextField;

import javafx.collections.ObservableList;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;



public class UIController{
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
    public TableColumn<Transaction, Date> dateCol;
    @FXML
    public TableColumn<Transaction, Character> signCol;
    @FXML
    public TableColumn<Transaction, String> itemCol;
    @FXML
    public TableColumn<Transaction, Double> priceCol;
    @FXML
    public TableColumn<Transaction, String> categoryCol;

    //Location is the location of FXML document, so sure we need it but it automacically gets loaded in
    @FXML
    private URL location;
    //This is a java object that can also be automatically loaded, but I'm not sure what it's for
    @FXML
    private ResourceBundle resources;

    private DecimalFormat moneyFormat;

    private UserAccount account = new UserAccount();
    
    private ObservableList<PieChart.Data> pieGraphData =
        FXCollections.observableArrayList(
            new PieChart.Data("Entertainment", account.getCategoryValues().get(0)),
            new PieChart.Data("Food", account.getCategoryValues().get(1)),
            new PieChart.Data("Transportation", account.getCategoryValues().get(2)),
            new PieChart.Data("Home & Utilities", account.getCategoryValues().get(3)),
            new PieChart.Data("Personal & Family Care", account.getCategoryValues().get(4)),
            new PieChart.Data("Others", account.getCategoryValues().get(5)));

    //public constructor, params must be empty
    //Even if it stays empty forever we cannot delete
    //Or it will fail to instatiate 
    public UIController(){
        moneyFormat  = new DecimalFormat("$##.00");
        moneyFormat.setRoundingMode(java.math.RoundingMode.UNNECESSARY);
    }

    //Function will be called when everything has loaded
    //Must be void, cannot have params
    @FXML
    private void initialize(){
        populateCategories();
        generatePriceFilter();
        formatTablePrice();
        initilizePieGraph();
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
        Double p = Double.parseDouble(price.getText());
        String c = category.getValue();

        Date date= new Date();

        //store transaction in account
        account.newTransaction(date, sign, i, p, c);

        //save respective values to table
        dateCol.setCellValueFactory(new PropertyValueFactory<Transaction, Date>("Date"));
        itemCol.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Item"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Transaction, Double>("Price"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Category"));

        transactionTable.setItems(account.getTransactions());
        int counter = 0;
        for (Node n: transactionTable.lookupAll("TableRow")) {
          if (n instanceof TableRow) {
            TableRow row = (TableRow) n;
            if (transactionTable.getItems().get(counter).getSign() == '+') {
              row.setStyle("-fx-background-color: #D9FFF5;");
            } else{
                row.setStyle("-fx-background-color: #FFAD98;");
            }
            counter++;
            if (counter == transactionTable.getItems().size())
              break;
          }
        }
        addDataPieGraph(c, p, sign);
    }

    @FXML
    private void populateCategories(){
        category.setItems(account.getCategories());
    }

    private void generatePriceFilter(){
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if(Pattern.matches("[\\d]*[\\.]?[\\d]{0,2}", c.getControlNewText())){
                return c;
            }else{
                return null;
            }
        };
        TextFormatter<String> format = new TextFormatter<>(filter);
        price.setTextFormatter(format);
    }

    private void formatTablePrice(){
        priceCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double p, boolean empty){
                super.updateItem(p, empty);
                if(p == null){
                    setText(null);
                }else{
                    setText(moneyFormat.format(p.doubleValue()));
                }
            }
        });
    }

    private void addDataPieGraph(String category, double p, char s){
        for(Data d : pieGraphData)
        {
            if(d.getName().equals(category) && s == '-')
            {
                d.setPieValue(d.getPieValue() + p);
                return;
            }
        }
        pieGraphData.add(new Data(category, p)); //Only if category does not exist
    }

    private void initilizePieGraph(){
        pieGraph.getData().addAll(pieGraphData);
    }
}
