import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import budgettracker.FileOperations;
import budgettracker.Transaction;
import budgettracker.Goal;
import budgettracker.TestSuite;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;



public class InsightsController{
    //The order of initilization: Constructor, @FXML variables and methods loaded, then initilize() (Constructor can't access @FXML fields)

    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML 
    private Button returnHome;
    @FXML
    private Button createGoal;
    @FXML
    private Button testsBtn;
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
    private BarChart<String, BigDecimal> barGraph;
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
    @FXML
    private Label foodGoalLabel;
    @FXML
    private Label entertainmentGoalLabel;   
    @FXML
    private Label transportationGoalLabel;    
    @FXML
    private Label homeGoalLabel;    
    @FXML
    private Label personalGoalLabel;
    @FXML
    private Label othersGoalLabel;

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
        updateGraphs();
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
    private void runTests(){
        TestSuite t = new TestSuite(lineGraph, barGraph, graphTimePeriod);
    }

    @FXML
    private void updateGraphs(){
        initilizeLineGraph();
        initilizeBarGraph();
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

    private void initilizeBarGraph(){
        LocalDateTime today = LocalDateTime.now();
        String timePeriod = graphTimePeriod.getValue();
        LocalDateTime start = LocalDateTime.now();
        ObservableList<Transaction> transactions = account.getTransactions();

        if(timePeriod.equals("All")){
            for(Transaction t : transactions){
                if(t.getDate().isBefore(start)){
                    start = t.getDate();
                }
            }
        }else{
            start = today.minusDays(Integer.parseInt(timePeriod));
        }
        
        Duration diff = Duration.between(start, today);
        Duration step = Duration.ofSeconds(0);

        if(timePeriod.equals("All") || Integer.parseInt(timePeriod) == 365){
            step = diff.dividedBy(5);
        }else if(Integer.parseInt(timePeriod) == 30){
            step = diff.dividedBy(5);
        }else if(Integer.parseInt(timePeriod) == 14){
            step = diff.dividedBy(7);
        }else if(Integer.parseInt(timePeriod) == 90){
            step = diff.dividedBy(5);
        }else if(Integer.parseInt(timePeriod) == 180){
            step = diff.dividedBy(5);
        }else if(Integer.parseInt(timePeriod) == 7){
            step = diff.dividedBy(7);
        }

        ObservableList<Transaction> filteredTransactions = getTransactionsAfter(start.toLocalDate());
        XYChart.Series<String,BigDecimal> foodSeries = new XYChart.Series<String,BigDecimal>();
        foodSeries.setName("Food");
        XYChart.Series<String,BigDecimal> entertainmentSeries = new XYChart.Series<String,BigDecimal>();
        entertainmentSeries.setName("Entertainment");
        XYChart.Series<String,BigDecimal> personalSeries = new XYChart.Series<String,BigDecimal>();
        personalSeries.setName("Personal & Family Care");
        XYChart.Series<String,BigDecimal> homeSeries = new XYChart.Series<String,BigDecimal>();
        homeSeries.setName("Home & Utilities");
        XYChart.Series<String,BigDecimal> transportationSeries = new XYChart.Series<String,BigDecimal>();
        transportationSeries.setName("Transportation");
        XYChart.Series<String,BigDecimal> othersSeries = new XYChart.Series<String,BigDecimal>();
        othersSeries.setName("Others");

        LocalDateTime currentEnd = start.plus(step);

        BigDecimal fSum = new BigDecimal(0);
        BigDecimal eSum = new BigDecimal(0);
        BigDecimal pSum = new BigDecimal(0);
        BigDecimal hSum = new BigDecimal(0);
        BigDecimal tSum = new BigDecimal(0);
        BigDecimal oSum = new BigDecimal(0);

        Iterator<Transaction> iterator = filteredTransactions.iterator();

        while(iterator.hasNext()){
            Transaction t = iterator.next();

            if(t.getDate().isAfter(currentEnd)){
                foodSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), fSum));
                entertainmentSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), eSum));
                personalSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), pSum));
                homeSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), hSum));
                transportationSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), tSum));
                othersSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), oSum));

                start = currentEnd;
                currentEnd = currentEnd.plus(step); 
                fSum = new BigDecimal(0);
                eSum = new BigDecimal(0);
                pSum = new BigDecimal(0);
                hSum = new BigDecimal(0);
                tSum = new BigDecimal(0);
                oSum = new BigDecimal(0);
            }
            if(t.getSign() == '-'){                
                switch(t.getCategory()){
                    case "Food":
                        fSum = fSum.add(BigDecimal.valueOf(t.getPrice()));
                        break;
                    case "Entertainment":
                        eSum = eSum.add(BigDecimal.valueOf(t.getPrice()));
                        break;
                    case "Personal & Family Care":
                        pSum = pSum.add(BigDecimal.valueOf(t.getPrice()));
                        break;
                    case "Home & Utilities":
                        hSum = hSum.add(BigDecimal.valueOf(t.getPrice()));
                        break;
                    case "Transportation":
                        tSum = tSum.add(BigDecimal.valueOf(t.getPrice()));
                        break;
                    case "Others":
                        oSum = oSum.add(BigDecimal.valueOf(t.getPrice()));
                        break;
                    default:
                        System.out.println("Data had bad category");
                        break;
                }
            }
            if(!iterator.hasNext()){
                foodSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), fSum));
                entertainmentSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), eSum));
                personalSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), pSum));
                homeSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), hSum));
                transportationSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), tSum));
                othersSeries.getData().add(new XYChart.Data<String, BigDecimal>(start.toLocalDate().toString() + " - " + currentEnd.toLocalDate().toString(), oSum)); 
            }
        }

        barGraph.getData().clear();
        barGraph.layout();
        barGraph.setAnimated(true);
        barGraph.getData().addAll(Arrays.asList(foodSeries, entertainmentSeries, personalSeries, homeSeries, transportationSeries, othersSeries));
        barGraph.setAnimated(false);
    }

    private ObservableList<Transaction> getTransactionsAfter(LocalDate start){
        ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();
        for(Transaction t : account.getTransactions()){
            if(t.getDate().toLocalDate().isAfter(start) || t.getDate().toLocalDate().equals(start)){
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
                entertainmentGoalLabel.setText("Entertainment");
                pBarEntertainment.setProgress(goalData.get("Entertainment")[0]/goalData.get("Entertainment")[1]);
                entertainmentProgress.setText(moneyFormat.format(goalData.get("Entertainment")[0]) + "/" + moneyFormat.format(goalData.get("Entertainment")[1]));
                entertainmentTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    entertainmentRepeat.setText("Repeat: Yes");
                }else{
                    entertainmentRepeat.setText("Repeat: No");
                }
                checkGoalExpiration(g);
            }
            else if(g.getGoalCategory().equals("Food")){
                foodGoalLabel.setText("Food");
                pBarFood.setProgress(goalData.get("Food")[0]/goalData.get("Food")[1]);
                foodProgress.setText(moneyFormat.format(goalData.get("Food")[0]) + "/" + moneyFormat.format(goalData.get("Food")[1]));
                foodTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    foodRepeat.setText("Repeat: Yes");
                }else{
                    foodRepeat.setText("Repeat: No");
                }
                checkGoalExpiration(g);
            }
            else if(g.getGoalCategory().equals("Transportation")){
                transportationGoalLabel.setText("Transportation");
                pBarTransportation.setProgress(goalData.get("Transportation")[0]/goalData.get("Transportation")[1]);
                transportationProgress.setText(moneyFormat.format(goalData.get("Transportation")[0]) + "/" + moneyFormat.format(goalData.get("Transportation")[1]));
                transportationTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    transportationRepeat.setText("Repeat: Yes");
                }else{
                    transportationRepeat.setText("Repeat: No");
                }
                checkGoalExpiration(g);
            }
            else if(g.getGoalCategory().equals("Home & Utilities")){
                homeGoalLabel.setText("Home & Utilities");
                pBarHome.setProgress(goalData.get("Home & Utilities")[0]/goalData.get("Home & Utilities")[1]);
                homeProgress.setText(moneyFormat.format(goalData.get("Home & Utilities")[0]) + "/" + moneyFormat.format(goalData.get("Home & Utilities")[1]));
                homeTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    homeRepeat.setText("Repeat: Yes");
                }else{
                    homeRepeat.setText("Repeat: No");
                }
                checkGoalExpiration(g);
            }
            else if(g.getGoalCategory().equals("Personal & Family Care")){
                personalGoalLabel.setText("Personal & Family Care");
                pBarPersonal.setProgress(goalData.get("Personal & Family Care")[0]/goalData.get("Personal & Family Care")[1]);
                personalProgress.setText(moneyFormat.format(goalData.get("Personal & Family Care")[0]) + "/" + moneyFormat.format(goalData.get("Personal & Family Care")[1]));
                personalTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    personalRepeat.setText("Repeat: Yes");
                }else{
                    personalRepeat.setText("Repeat: No");
                }
                checkGoalExpiration(g);
            }
            else if(g.getGoalCategory().equals("Others")){
                othersGoalLabel.setText("Others");
                pBarOthers.setProgress(goalData.get("Others")[0]/goalData.get("Others")[1]);
                othersProgress.setText(moneyFormat.format(goalData.get("Others")[0]) + "/" + moneyFormat.format(goalData.get("Others")[1]));
                othersTimeframe.setText("Timeframe: " + g.getGoalTime() + " days");
                if(g.getGoalRepeat() == true){
                    othersRepeat.setText("Repeat: Yes");
                }else{
                    othersRepeat.setText("Repeat: No");
                }
                checkGoalExpiration(g);
            }
        }
    }

    private void checkGoalExpiration(Goal g){
        if(g.isExpired() && g.getGoalEndDate().plusDays(1).isBefore(LocalDate.now())){
            System.out.println("running switch");
            switch(g.getGoalCategory()){
                case "Food":
                    if(g.isBroken(account) && !g.getGoalRepeat()){
                        foodGoalLabel.setText(foodGoalLabel.getText() + " - Expired - You did not stay under you goal >:(");
                        pBarFood.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(!g.isBroken(account) && !g.getGoalRepeat()){
                        foodGoalLabel.setText(foodGoalLabel.getText() + " - Expired - You stayed under your goal!");
                        pBarFood.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(g.isBroken(account) && g.getGoalRepeat()){
                        foodGoalLabel.setText(foodGoalLabel.getText() + " - You did not stay under your goal >:(");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarFood.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }else if(!g.isBroken(account) && g.getGoalRepeat()){
                        foodGoalLabel.setText(foodGoalLabel.getText() + " - You stayed under your goal!");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarFood.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }
                    break;
                case "Entertainment":
                    if(g.isBroken(account) && !g.getGoalRepeat()){
                        entertainmentGoalLabel.setText(entertainmentGoalLabel.getText() + " - Expired - You did not stay under you goal >:(");
                        pBarEntertainment.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(!g.isBroken(account) && !g.getGoalRepeat()){
                        entertainmentGoalLabel.setText(entertainmentGoalLabel.getText() + " - Expired - You stayed under your goal!");
                        pBarEntertainment.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(g.isBroken(account) && g.getGoalRepeat()){
                        entertainmentGoalLabel.setText(entertainmentGoalLabel.getText() + " - You did not stay under your goal >:(");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarEntertainment.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }else if(!g.isBroken(account) && g.getGoalRepeat()){
                        entertainmentGoalLabel.setText(entertainmentGoalLabel.getText() + " - You stayed under your goal!");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarEntertainment.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }
                    break;
                case "Personal & Family Care":
                    if(g.isBroken(account) && !g.getGoalRepeat()){
                        personalGoalLabel.setText(personalGoalLabel.getText() + " - Expired - You did not stay under you goal >:(");
                        pBarPersonal.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(!g.isBroken(account) && !g.getGoalRepeat()){
                        personalGoalLabel.setText(personalGoalLabel.getText() + " - Expired - You stayed under your goal!");
                        pBarPersonal.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(g.isBroken(account) && g.getGoalRepeat()){
                        personalGoalLabel.setText(personalGoalLabel.getText() + " - You did not stay under your goal >:(");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarPersonal.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }else if(!g.isBroken(account) && g.getGoalRepeat()){
                        personalGoalLabel.setText(personalGoalLabel.getText() + " - You stayed under your goal!");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarPersonal.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }
                    break;
                case "Home & Utilities":
                    if(g.isBroken(account) && !g.getGoalRepeat()){
                        homeGoalLabel.setText(homeGoalLabel.getText() + " - Expired - You did not stay under you goal >:(");
                        pBarHome.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(!g.isBroken(account) && !g.getGoalRepeat()){
                        homeGoalLabel.setText(homeGoalLabel.getText() + " - Expired - You stayed under your goal!");
                        pBarHome.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(g.isBroken(account) && g.getGoalRepeat()){
                        homeGoalLabel.setText(homeGoalLabel.getText() + " - You did not stay under your goal >:(");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarHome.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }else if(!g.isBroken(account) && g.getGoalRepeat()){
                        homeGoalLabel.setText(homeGoalLabel.getText() + " - You stayed under your goal!");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarHome.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }
                    break;
                case "Transportation":
                    if(g.isBroken(account) && !g.getGoalRepeat()){
                        transportationGoalLabel.setText(transportationGoalLabel.getText() + " - Expired - You did not stay under you goal >:(");
                        pBarTransportation.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(!g.isBroken(account) && !g.getGoalRepeat()){
                        transportationGoalLabel.setText(transportationGoalLabel.getText() + " - Expired - You stayed under your goal!");
                        pBarTransportation.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(g.isBroken(account) && g.getGoalRepeat()){
                        transportationGoalLabel.setText(transportationGoalLabel.getText() + " - You did not stay under your goal >:(");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarTransportation.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }else if(!g.isBroken(account) && g.getGoalRepeat()){
                        transportationGoalLabel.setText(transportationGoalLabel.getText() + " - You stayed under your goal!");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarTransportation.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }
                    break;
                case "Others":
                    if(g.isBroken(account) && !g.getGoalRepeat()){
                        othersGoalLabel.setText(othersGoalLabel.getText() + " - Expired - You did not stay under you goal >:(");
                        pBarOthers.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(!g.isBroken(account) && !g.getGoalRepeat()){
                        othersGoalLabel.setText(othersGoalLabel.getText() + " - Expired - You stayed under your goal!");
                        pBarOthers.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }else if(g.isBroken(account) && g.getGoalRepeat()){
                        othersGoalLabel.setText(othersGoalLabel.getText() + " - You did not stay under your goal >:(");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarOthers.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }else if(!g.isBroken(account) && g.getGoalRepeat()){
                        othersGoalLabel.setText(othersGoalLabel.getText() + " - You stayed under your goal!");
                        account.emptyExpenseValue(g.getGoalCategory());
                        pBarOthers.setProgress(0);
                        g.setGoalStartDate(g.getGoalEndDate());
                        g.setGoalEndDate(g.getGoalStartDate().plusDays(g.getGoalTime()));
                    }
                    break;
                default:
                    System.out.println("Goal had bad category");
                    break;
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