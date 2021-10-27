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
import javafx.scene.control.ProgressBar;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;



public class UIController{
    //The order of initilization: Constructor, @FXML variables and methods loaded, then initilize() (Constructor can't access @FXML fields)

    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML
    private Button addMoney;
    @FXML
    private Button subMoney;
    @FXML
    private Button createGoal;
    @FXML
    private TextField item;
    @FXML
    private TextField price;
    @FXML
    private TextField goalPrice;
    @FXML
    private ChoiceBox<String> category;
    @FXML
    private ChoiceBox<String> goalCategory;
    @FXML 
    private ProgressBar pBarEntertainment;
    @FXML 
    private ProgressBar pBarFood;
    @FXML 
    private ProgressBar pBarTransportation;
    @FXML 
    private ProgressBar pBarHome;
    @FXML 
    private ProgressBar pBarPersonal;
    @FXML 
    private ProgressBar pBarOthers;
    @FXML
    private PieChart pieGraph;
    @FXML 
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Date> dateCol;
    @FXML
    private TableColumn<Transaction, Character> signCol;
    @FXML
    private TableColumn<Transaction, String> itemCol;
    @FXML
    private TableColumn<Transaction, Double> priceCol;
    @FXML
    private TableColumn<Transaction, String> categoryCol;

    //Location is the location of FXML document
    @FXML
    private URL location;
    //This is a java object that can also be automatically loaded
    @FXML
    private ResourceBundle resources;

    private DecimalFormat moneyFormat;
    
    //Account Object for the user
    private UserAccount account = new UserAccount();

    //Loading categories and values into the piechart -- might want to look at again
    private ObservableList<PieChart.Data> pieGraphData =
        FXCollections.observableArrayList(
            new PieChart.Data("Entertainment", account.getCategoryExpenseValues().get(0)),
            new PieChart.Data("Food", account.getCategoryExpenseValues().get(1)),
            new PieChart.Data("Transportation", account.getCategoryExpenseValues().get(2)),
            new PieChart.Data("Home & Utilities", account.getCategoryExpenseValues().get(3)),
            new PieChart.Data("Personal & Family Care", account.getCategoryExpenseValues().get(4)),
            new PieChart.Data("Others", account.getCategoryExpenseValues().get(5)));


    /**
     * Constructor, params must be empty, defines monesy format for the table
    */
     public UIController(){
        moneyFormat  = new DecimalFormat("$##.00");
        moneyFormat.setRoundingMode(java.math.RoundingMode.UNNECESSARY);
    }

    /**
     * Function will be called when everything has loaded
     * Must be void, cannot have params, calls setup functions
    */
    @FXML
    private void initialize(){
        populateCategories();
        generatePriceFilter();
        formatTablePrice();
        initilizeTableColumns();
        initilizePieGraph();
    }

    /**
     * Calls createGoal() in the account object after the addGoal button is clicked
     * Will overwrite old goals of the same category
     */
    @FXML
    private void addGoal(){
        account.createGoal(goalCategory.getValue(), Double.parseDouble(goalPrice.getText()));
        updateGoals();
    }

    /**
     * Calls saveChange with '-' (Because you cannot send params with FXML)
     */
    @FXML
    private void savePosCharge(){
        saveCharge('+');
    }
    /**
     * Calls saveCharge with '+' (Because you cannot send params with FXML)
     */
    @FXML
    private void saveNegCharge(){
        saveCharge('-');
    }
    
    
    /** 
     * Sign is provided by user depending on which button they press, '+' or '-'
     * @param sign : char, '+' or '-', depending if it was an expense or income
     */
    private void saveCharge(char sign){
        String i = item.getText();
        Double p = Double.parseDouble(price.getText());
        String c = category.getValue();
        Date date = new Date();

        //store transaction in account
        account.newTransaction(date, sign, i, p, c);
        //add respective values to table
        transactionTable.setItems(account.getTransactions());
        updateTableColors();
        updateGoals();
        addDataPieGraph(c, p, sign);
    }

    /**
     * Calls checkGoals() in the account, and receives back a HashMap of 
     * goals and prices to reflect on the FXML ProgressBars
     */
    private void updateGoals(){
        HashMap<String, Double[]> goals = account.getGoals();
        for(String c : account.getCategories()){
            if(goals.containsKey(c) && c == "Entertainment"){
                pBarEntertainment.setProgress(goals.get(c)[0]/goals.get(c)[1]);
            }
            else if(goals.containsKey(c) && c == "Food"){
                pBarFood.setProgress(goals.get(c)[0]/goals.get(c)[1]);

            }
            else if(goals.containsKey(c) && c == "Transportation"){
                pBarTransportation.setProgress(goals.get(c)[0]/goals.get(c)[1]);
            }
            else if(goals.containsKey(c) && c == "Home & Utilities"){
                pBarHome.setProgress(goals.get(c)[0]/goals.get(c)[1]);
            }
            else if(goals.containsKey(c) && c == "Personal & Family Care"){
                pBarPersonal.setProgress(goals.get(c)[0]/goals.get(c)[1]);
            }
            else if(goals.containsKey(c) && c == "Others"){
                pBarOthers.setProgress(goals.get(c)[0]/goals.get(c)[1]);
            }
        }
    }

    /**
     * Populates the drop down menus with categories
     */
    private void populateCategories(){
        //Add categories to Dropdown menus
        category.setItems(account.getCategories());
        goalCategory.setItems(account.getCategories());
    }

    /**
     * Loops through the rows of the table, coloring the rows whos sign 
     * is a '+' with green and rows whos sign is a '-' with red
     */
    private void updateTableColors(){
        int counter = 0;
        for (Node n: transactionTable.lookupAll("TableRow")) {
          if (n instanceof TableRow) {
            TableRow<?> row = (TableRow<?>) n;
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
    }

    /**
     * 
     */
    private void initilizeTableColumns(){
        //Enables writing to the tables
        dateCol.setCellValueFactory(new PropertyValueFactory<Transaction, Date>("Date"));
        itemCol.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Item"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Transaction, Double>("Price"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Category"));
    }

    /**
     * 
     */
    private void generatePriceFilter(){
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if(Pattern.matches("[\\d]*[\\.]?[\\d]{0,2}", c.getControlNewText())){
                return c;
            }else{
                return null;
            }
        };
        TextFormatter<String> format1 = new TextFormatter<>(filter);
        TextFormatter<String> format2 = new TextFormatter<>(filter);
        price.setTextFormatter(format1);
        goalPrice.setTextFormatter(format2);
    }

    /**
     * 
     */
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

    
    /** 
     * @param category
     * @param p
     * @param s
     */
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

    /**
     * 
     */
    private void initilizePieGraph(){
        pieGraph.getData().addAll(pieGraphData);
    }
}
