import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import budgettracker.FileOperations;
import budgettracker.Transaction;
import budgettracker.Goal;
import budgettracker.UserAccount;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;



public class InsightsController{
    //The order of initilization: Constructor, @FXML variables and methods loaded, then initilize() (Constructor can't access @FXML fields)

    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML 
    private Button returnHome;
    @FXML
    private Button createGoal;
    @FXML
    private TextField goalPrice;
    @FXML
    private ChoiceBox<String> goalCategory;
    @FXML
    private ChoiceBox<Integer> goalTime;
    @FXML
    private CheckBox repeatGoal;
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
    private ChoiceBox<String> graphTimePeriod;
    @FXML 
    private BarChart<LocalDate, Double> barGraph;
    @FXML
    private LineChart<String, BigDecimal> lineGraph;
    @FXML
    private Label foodProgress;
    @FXML
    private Label entertainmentProgress;
    @FXML
    private Label transportationProgress;
    @FXML
    private Label homeProgress;
    @FXML
    private Label personalProgress;
    @FXML
    private Label othersProgress;
    @FXML
    private Label foodTimeframe;
    @FXML
    private Label entertainmentTimeframe;
    @FXML
    private Label transportationTimeframe;
    @FXML
    private Label homeTimeframe;
    @FXML
    private Label personalTimeframe;
    @FXML
    private Label othersTimeframe;
    @FXML
    private Label foodRepeat;
    @FXML
    private Label entertainmentRepeat;
    @FXML
    private Label transportationRepeat;
    @FXML
    private Label homeRepeat;
    @FXML
    private Label personalRepeat;
    @FXML
    private Label othersRepeat;
    

    //Format for prices
    private DecimalFormat moneyFormat;

    //Location is the location of FXML document
    @FXML
    private URL location;

    //This is a java object that can also be automatically loaded
    @FXML
    private ResourceBundle resources;

    private UserAccount account = UserAccount.getInstance();
    
    private Stage stage;

    /**
     * Constructor, params must be empty, defines money format for the table
    */
    public InsightsController(){
        moneyFormat  = new DecimalFormat("$##.00");
        moneyFormat.setRoundingMode(java.math.RoundingMode.HALF_EVEN);
    }

    /**
     * Function will be called when everything has loaded
     * Must be void, cannot have params, calls setup functions
    */
    @FXML
    private void initialize(){
        populateCategories();
        generatePriceFilter();
        populateTime();
        updateGoals();
        initilizeLineGraph();
    }

    /**
     * Calls createGoal() in the account object after the addGoal button is clicked,
     * if all neccessary fields are filled out
     * (Will overwrite old goals of the same category)
     */
    @FXML
    private void addGoal(){
        if(checkForDataGoal()){
            account.createGoal(goalCategory.getValue(), Double.parseDouble(goalPrice.getText()), goalTime.getValue(), repeatGoal.isSelected()); // need to create chice box for time and repeat
            updateGoals();
            clearDataGoal();
        }
    }

    @FXML
    private void updateGraphs(){
        initilizeLineGraph();
        //initilizeBarGraph();
    }

    private void initilizeLineGraph(){
        LocalDate today = LocalDate.now();
        String timePeriod = graphTimePeriod.getValue();
        LocalDate start = LocalDate.now();
        ObservableList<Transaction> transactions = account.getTransactions();

        if(timePeriod.equals("All")){
            for(Transaction t : transactions){
                if(t.getDate().toLocalDate().isBefore(start)){
                    start = t.getDate().toLocalDate();
                }
            }
        }else{
            start = today.minusDays(Integer.parseInt(graphTimePeriod.getValue()));
        }
        
        ObservableList<Transaction> filteredTransactions = getTransactionsAfter(start);
        XYChart.Series<String,BigDecimal> series = new XYChart.Series<String,BigDecimal>();
        
        BigDecimal sum = new BigDecimal(0);
        for(Transaction t : filteredTransactions){
            if(t.getSign() == '-'){
                sum = sum.add(BigDecimal.valueOf(t.getPrice()*-1));
                series.getData().add(new XYChart.Data<String, BigDecimal>(t.getDate().toLocalDate().toString(), sum));        
            }else{
                sum = sum.add(BigDecimal.valueOf(t.getPrice()));
                series.getData().add(new XYChart.Data<String, BigDecimal>(t.getDate().toLocalDate().toString(), sum));        
            }
        }

        lineGraph.setCreateSymbols(false);

        lineGraph.getData().clear();
        lineGraph.layout();

		lineGraph.setAnimated(true);
        lineGraph.getData().add(series);
        lineGraph.setAnimated(false);

    }

    private ObservableList<Transaction> getTransactionsAfter(LocalDate start){
        ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();
        for(Transaction t : account.getTransactions()){
            if(t.getDate().toLocalDate().isAfter(start)){
                filteredTransactions.add(t);
            }
        }
        filteredTransactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        return filteredTransactions;
    }

    /**
     * Calls checkGoals() in the account, and receives back a HashMap of 
     * goals and prices to reflect on the FXML ProgressBars
     * The format of the HashMap: <{category, [currentExpenditure, goalPrice]} , ... >
     */

    private void updateGoals(){
        HashMap<String, Double[]> goalData = account.getGoalData();
        Goal[] goals = account.getGoals();
        for(Goal g : goals){
            if(g.getGoalCategory().equals("Entertainment")){
                pBarEntertainment.setProgress(goalData.get("Entertainment")[0]/goalData.get("Entertainment")[1]);
                entertainmentProgress.setText(moneyFormat.format(goalData.get("Entertainment")[0]) + "/" + moneyFormat.format(goalData.get("Entertainment")[1]));
                entertainmentTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    entertainmentRepeat.setText("Repeat: Yes");
                }else{
                    entertainmentRepeat.setText("Repeat: No");
                }
            }
            else if(g.getGoalCategory().equals("Food")){
                pBarFood.setProgress(goalData.get("Food")[0]/goalData.get("Food")[1]);
                foodProgress.setText(moneyFormat.format(goalData.get("Food")[0]) + "/" + moneyFormat.format(goalData.get("Food")[1]));
                foodTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    foodRepeat.setText("Repeat: Yes");
                }else{
                    foodRepeat.setText("Repeat: No");
                }
            }
            else if(g.getGoalCategory().equals("Transportation")){
                pBarTransportation.setProgress(goalData.get("Transportation")[0]/goalData.get("Transportation")[1]);
                transportationProgress.setText(moneyFormat.format(goalData.get("Transportation")[0]) + "/" + moneyFormat.format(goalData.get("Transportation")[1]));
                transportationTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    transportationRepeat.setText("Repeat: Yes");
                }else{
                    transportationRepeat.setText("Repeat: No");
                }
            }
            else if(g.getGoalCategory().equals("Home & Utilities")){
                pBarHome.setProgress(goalData.get("Home & Utilities")[0]/goalData.get("Home & Utilities")[1]);
                homeProgress.setText(moneyFormat.format(goalData.get("Home & Utilities")[0]) + "/" + moneyFormat.format(goalData.get("Home & Utilities")[1]));
                homeTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    homeRepeat.setText("Repeat: Yes");
                }else{
                    homeRepeat.setText("Repeat: No");
                }
            }
            else if(g.getGoalCategory().equals("Personal & Family Care")){
                pBarPersonal.setProgress(goalData.get("Personal & Family Care")[0]/goalData.get("Personal & Family Care")[1]);
                personalProgress.setText(moneyFormat.format(goalData.get("Personal & Family Care")[0]) + "/" + moneyFormat.format(goalData.get("Personal & Family Care")[1]));
                personalTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    personalRepeat.setText("Repeat: Yes");
                }else{
                    personalRepeat.setText("Repeat: No");
                }
            }
            else if(g.getGoalCategory().equals("Others")){
                pBarOthers.setProgress(goalData.get("Others")[0]/goalData.get("Others")[1]);
                othersProgress.setText(moneyFormat.format(goalData.get("Others")[0]) + "/" + moneyFormat.format(goalData.get("Others")[1]));
                othersTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    othersRepeat.setText("Repeat: Yes");
                }else{
                    othersRepeat.setText("Repeat: No");
                }
            }

        }
    }

    /**
     * Populates the drop down menus with categories
    */
    private void populateCategories(){
        //Add categories to Dropdown menus
        goalCategory.setItems(account.getCategories());
    }
    
    private void populateTime(){
        ObservableList<Integer> goalTimeFrames = FXCollections.observableArrayList();
        goalTimeFrames.add(7);
        goalTimeFrames.add(14);
        goalTimeFrames.add(30);
        goalTimeFrames.add(365);
        goalTime.setItems(goalTimeFrames);

        ObservableList<String> graphTimeFrames = FXCollections.observableArrayList();
        graphTimeFrames.add("All");
        graphTimeFrames.add("7");
        graphTimeFrames.add("14");
        graphTimeFrames.add("30");
        graphTimeFrames.add("90");
        graphTimeFrames.add("180");
        graphTimeFrames.add("365");
        graphTimePeriod.setItems(graphTimeFrames);
    }
    
    @FXML
    private void toHome(ActionEvent e) throws IOException{
        Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("home.fxml"));
        Parent root = (Pane) loader.load();

        HomeController controller = loader.getController();
        controller.setStage(s);

        Scene newScene = new Scene(root);
        s.setScene(newScene);
        s.show();
    }

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
                populateCategories();
                populateTime();
                updateGoals();
            }
        }); 
    }
     /**
     * Creates and applies a regular expression filter that ensures you 
     * can only type correctly formatted prices in the price textfields
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
        goalPrice.setTextFormatter(format1);
    }

    /** 
     * Returns true if all the necessary fields to add a new goal have been filled.
     * @return boolean
     */
    private boolean checkForDataGoal(){
        if(!goalCategory.getSelectionModel().isEmpty() && goalPrice.getText() != "" && !goalTime.getSelectionModel().isEmpty()){
            return true;
        }else{
            return false;
        }
    
    }

    /**
     * Clears goal fields after adding a goal
     */
    private void clearDataGoal(){
        goalCategory.getSelectionModel().clearSelection();
        goalPrice.clear();
    }
}