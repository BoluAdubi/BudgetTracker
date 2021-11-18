import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import budgettracker.Transaction;
import budgettracker.UserAccount;
import budgettracker.FileOperations;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.TextField;
import javafx.scene.Node;

import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Date;

/** -------------------------------------------------------------
* The home controller class handles the user interface on the home page. The UI includes presenting the user
* with a way to log transactions into a table as well as a pie chart that shows the user which category has the
* highest spending. This class has features including loading in csvs, dynamically updating the transactions log and piechart,
* switching to the insights UI and saving the transaction and goal data upon a close window request.
* file: HomeController.java
* date: 11/17/2021
* @author Team 19
-------------------------------------------------------------*/
public class HomeController{
    //The order of initilization: Constructor, @FXML variables and methods loaded, then initilize() (Constructor can't access @FXML fields)

    //Controls from FXML, the variables are automatically assigned based on fx:id
    @FXML
    private Button addMoney;
    @FXML
    private Button subMoney;
    @FXML
    private Button toInsightsBtn;
    @FXML
    private TextField item;
    @FXML
    private TextField price;
    @FXML
    private ChoiceBox<String> category;
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

    //Format for prices in TableView
    private DecimalFormat moneyFormat;

    private Stage stage;

    //Account Object for the user
    private UserAccount account = UserAccount.getInstance();

    /**
     * Data to be loaded into the pieChart
    */
    private ObservableList<PieChart.Data> pieGraphData =
        FXCollections.observableArrayList(
            new PieChart.Data("Entertainment", account.getPieValues().get(0)),
            new PieChart.Data("Food", account.getPieValues().get(1)),
            new PieChart.Data("Transportation", account.getPieValues().get(2)),
            new PieChart.Data("Home & Utilities", account.getPieValues().get(3)),
            new PieChart.Data("Personal & Family Care", account.getPieValues().get(4)),
            new PieChart.Data("Others", account.getPieValues().get(5)));


    /**
     * Constructor, params must be empty, defines money format for the table
    */
     public HomeController(){
        moneyFormat  = new DecimalFormat("$##.00");
        moneyFormat.setRoundingMode(java.math.RoundingMode.UNNECESSARY);
    }

    /**
     * This function saves goal and transaction data on a close request so when the application is reopened
     * the user can continue where they left off. When the window is reopened, this function calls updatepiegraph()
     * and updateTable() so that the home UI is exactly the same as when the close request occured.
     * @param stage : Stage Window for UI
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                FileOperations f = new FileOperations();
                f.saveTransactions(account);
                f.saveGoals(account);
            }
        });
        this.stage.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                updateDataPieGraph();
                updateTable();
            }
        });
    }

    /**
     * When functuion is called a stage object, FXML loader and scene are instantiated and the
     * UI switches to the insights page. This is done by insights.fxml being passed into setLoaction.
     * (This function is called when the button 'Insigts Page' is clicked)
     * @param e
     */
    @FXML
    private void toInsights(ActionEvent e) throws IOException{
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("insights.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root);
        s.setScene(newScene);
        s.show();
    }

    /**
     * Function will be called when everything has loaded
     * Must be void, cannot have params, calls setup functions
    */
    @FXML
    private void initialize(){
        generatePriceFilter();
        formatTablePrice();
        initilizeTableColumns();
        initilizePieGraph();
        initilizeTableColors();
        populateCategories();
        updateTable();
    }


    /**
     * Sign is provided by user depending on which button they press, '+' or '-'
     * This function grabs the transaction data from the input controls and
     * adds the transaction to the account, then updates the table, pie graph
     * progress bars.
     * @param sign : char, '+' or '-', depending if it was an expense or income
     */
    @FXML
    private void loadCsv(){
        FileChooser f = new FileChooser();
        File csv = f.showOpenDialog(stage.getScene().getWindow());
        FileOperations fileOps = new FileOperations();
        if(fileOps.addTransactionCSV(csv, account)){
            System.out.println("Successfully Loaded.");
            updateDataPieGraph();
        }else{
            System.out.println("CSV Loading failed.");
        }


    }

    /**
     * If all the data needed for the transaction has been filled out, this function
     * calls saveChange with '-' (Because you cannot send params with FXML)
     */
    @FXML
    private void savePosCharge(){
        if(checkForDataTransaction('+')){
            saveCharge('+');
            clearDataTransaction();
        }
    }
    /**
     * If all the data needed for the transaction has been filled out, this function
     * calls saveCharge with '+' (Because you cannot send params with FXML)
     */
    @FXML
    private void saveNegCharge(){
        if(checkForDataTransaction('-')){
            saveCharge('-');
            clearDataTransaction();
        }
    }


    /**
     * Sign is provided by user depending on which button they press, '+' or '-'
     * This function grabs the transaction data from the input controls and
     * adds the transaction to the account, then updates the table, pie graph
     * progress bars.
     * @param sign : char, '+' or '-', depending if it was an expense or income
     */
    private void saveCharge(char sign){
        String i = item.getText();
        Double p = Double.parseDouble(price.getText());
        String c = category.getValue();
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        //store transaction in account
        account.newTransaction(date, sign, i, p, c);
        //add respective values to table
        updateTable();
        updateDataPieGraph();
    }


    /**
     * Populates the drop down menus with categories
     *  
     *  
     */
    private void populateCategories(){
        //Add categories to Dropdown menus
        category.setItems(account.getCategories());
    }

    /**
     * Applies a rowFactory to each row of the table, coloring the rows whos sign
     * is a '+' with green and rows whos sign is a '-' with red
     *  
     *  
     */
    private void initilizeTableColors(){
        transactionTable.setRowFactory(tv -> new TableRow<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setStyle("");
                else if (item.getSign() == '+')
                    setStyle("-fx-background-color: #81A19A;");
                else if (item.getSign() == '-')
                    setStyle("-fx-background-color: #FFAD98;");
                else
                    setStyle("");
            }
        });
    }

    /**
     * Initailises the tablecolumns that stores the inputted transaction data
     *  
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
     * Creates and applies a regular expression filter that ensures you
     * can only type correctly formatted prices in the price textfields
     *  
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
        price.setTextFormatter(format1);
    }

    /**
     * Creates and applies a format for the priceColumn of the table
     * $\d*.\d\d
     *  
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
     * When called, this function updates the table with any new user transactions
     *  
     *  
     */
    private void updateTable(){
        transactionTable.setItems(account.getTransactions());
    }


    /**
     * Add the new data to the pie graph and update the graph
     *
     * Note: If we add functionality of deleting transactions this will break,
     *       it needs to actually use the observable list instead of just
     *       being passed the values each time a new transaction is made.
     * @param category
     * @param p
     * @param s
     */
    private void updateDataPieGraph(){
        for(Data d : pieGraphData)
        {
            Double currentVal = account.getPieValues().get(account.getCategoryExpenseIndex(d.getName()));
            if(d.getPieValue() != currentVal);
            {
                d.setPieValue(currentVal);
            }
        }
    }

    /**
     * Assigns the piegraph data to the pie graph
     *  
     *  
     */
    private void initilizePieGraph(){
        pieGraph.getData().addAll(pieGraphData);
    }


    /**
     * Function takes the sign of the button clicked, then returns true if all the necessary fields
     * for that sign have been entered.
     * @param sign : Char '+'/'-'
     * @return boolean : whether or not the fields have been filled
     */
    private boolean checkForDataTransaction(char sign){
        if(sign == '-'){
            if(!category.getSelectionModel().isEmpty() && price.getText() != "" && item.getText() != ""){
                return true;
            }else{
                return false;
            }
        }
        else if(sign == '+'){
            if(price.getText() != ""){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * Clears transaction fields after adding a transaction
     *  
     *  
     */
    private void clearDataTransaction(){
        category.getSelectionModel().clearSelection();
        price.clear();
        item.clear();
    }
}
