package budgettracker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Iterator;

import javax.print.DocFlavor.STRING;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ChoiceBox;

public class TestSuite {

    private UserAccount account = UserAccount.getInstance();

    private DecimalFormat moneyFormat  = new DecimalFormat("##.00");

    private LineChart<String,BigDecimal> lineGraph;

    private BarChart<String,BigDecimal> barGraph;

    private ChoiceBox<String> timeBox;

    private String originalTimeSelection;
    
    public TestSuite(LineChart<String,BigDecimal> line, BarChart<String,BigDecimal> bar, ChoiceBox<String> choice){
        lineGraph = line;
        barGraph = bar;
        timeBox = choice;
        originalTimeSelection = choice.getValue(); 
        runTests();
    }
    
    private void runTests(){
        checkPie();
        checkLine();
        //checkBar();
        timeBox.setValue(originalTimeSelection);
    }

    private void checkPie(){
        Double food = 0.0;
        Double transportation = 0.0;
        Double home = 0.0;
        Double personal = 0.0;
        Double entertainment = 0.0;
        Double others = 0.0;

        for(Transaction t : account.getTransactions()){
            if(t.getSign() == '-'){   
                switch(t.getCategory()){
                    case "Food":
                        food += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Transportation":
                        transportation += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Home & Utilities":
                        home += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Personal & Family Care":
                        personal += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Entertainment":
                        entertainment += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Others":
                        others += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    default:
                        System.out.println("Pie Chart Value Check: FAILED");
                        break;
                }
            }
        }

        if( food.compareTo(account.getPieValues().get(account.getCategoryExpenseIndex("Food"))) == 0 && transportation.compareTo(account.getPieValues().get(account.getCategoryExpenseIndex("Transportation"))) == 0 &&
            personal.compareTo(account.getPieValues().get(account.getCategoryExpenseIndex("Personal & Family Care"))) == 0 && entertainment.compareTo(account.getPieValues().get(account.getCategoryExpenseIndex("Entertainment"))) == 0 &&
            others.compareTo(account.getPieValues().get(account.getCategoryExpenseIndex("Others"))) == 0 && home.compareTo(account.getPieValues().get(account.getCategoryExpenseIndex("Home & Utilities"))) == 0){
            System.out.println("Pie Chart Value Check: PASSED");
        }else{
            System.out.println("Pie Chart Value Check: FAILED");
        }
    }

    private void checkLine(){
        String[] timePeriods = {"All","365","180","90","30","14","7"};

        for(String time : timePeriods){
            timeBox.setValue(time);

            ObservableList<Transaction> transactions = FXCollections.observableArrayList();
            LocalDate today = LocalDate.now();
            LocalDate start = today;
            if(time.equals("All")){
                for(Transaction t : account.getTransactions()){
                    if(t.getDate().toLocalDate().isBefore(start)){
                        start = t.getDate().toLocalDate();
                    }
                }
            }else{
                start = today.minusDays(Integer.parseInt(time));
            }

            for(Transaction t : account.getTransactions()){
                if(t.getDate().toLocalDate().isAfter(start.minusDays(1))){
                    transactions.add(t);
                }
            }

            transactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));

            boolean failFlag = false;

            for(XYChart.Series<String,BigDecimal> s : lineGraph.getData()){
                BigDecimal sum = new BigDecimal(0);
                sum = sum.setScale(2, RoundingMode.HALF_EVEN);

                for(int i = 0; i < s.getData().size(); i++){
                    if(transactions.get(i).getSign() == '-'){
                        sum = sum.subtract(new BigDecimal(transactions.get(i).getPrice()).setScale(2, RoundingMode.HALF_EVEN));
                    }else{
                        sum = sum.add(new BigDecimal(transactions.get(i).getPrice()).setScale(2, RoundingMode.HALF_EVEN));
                    }

                    if(s.getData().get(i).getXValue().equals(transactions.get(i).getDate().toLocalDate().toString()) && s.getData().get(i).getYValue().compareTo(sum) == 0){
                    }else{
                        failFlag = true;
                        System.out.println("Series: "+s.getData().get(i).getXValue()+" Value: "+transactions.get(i).getDate().toLocalDate().toString());
                        System.out.println("Series: "+s.getData().get(i).getYValue()+" Value: "+sum);
                        System.out.println("Line Chart Value Check("+time+"): FAILED");
                        break;
                    }
                }
            }

            if(!failFlag){
                System.out.println("Line Chart Value Check("+time+"): PASSED");
            }
        }
    }

   /* private void checkBar(){
        String[] timePeriods = {"All","365","180","90","30","14","7"};

        for(XYChart.Series<String, BigDecimal> s : barGraph.getData()){
            System.out.println(s.getName()+": ");
            System.out.println(s.getData().toString()+"\n");
        }
        
        
        for(String time : timePeriods){
            timeBox.setValue(time);

            ObservableList<BigDecimal> foodSum = FXCollections.observableArrayList();
            ObservableList<BigDecimal> entertainmentSum = FXCollections.observableArrayList();
            ObservableList<BigDecimal> transportationSum = FXCollections.observableArrayList();
            ObservableList<BigDecimal> homeSum = FXCollections.observableArrayList();
            ObservableList<BigDecimal> personalSum = FXCollections.observableArrayList();
            ObservableList<BigDecimal> othersSum = FXCollections.observableArrayList();

            LocalDate today = LocalDate.now();
            LocalDate start = today;

            if(time.equals("All")){
                for(Transaction t : account.getTransactions()){
                    if(t.getDate().toLocalDate().isBefore(start)){
                        start = t.getDate().toLocalDate();
                    }
                }
            }else{
                start = today.minusDays(Integer.parseInt(time));
            }

            Duration diff = Duration.between(start, today);
            Duration step = Duration.ofSeconds(0);
    
            if(time.equals("All") || Integer.parseInt(time) == 365){
                step = diff.dividedBy(5);
            }else if(Integer.parseInt(time) == 30){
                step = diff.dividedBy(5);
            }else if(Integer.parseInt(time) == 14){
                step = diff.dividedBy(7);
            }else if(Integer.parseInt(time) == 90){
                step = diff.dividedBy(5);
            }else if(Integer.parseInt(time) == 180){
                step = diff.dividedBy(5);
            }else if(Integer.parseInt(time) == 7){
                step = diff.dividedBy(7);
            }

            LocalDate end = start.plus(step);
            ObservableList<Transaction> transactions = account.getTransactions();
            transactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
            Iterator<Transaction> iterator = transactions.iterator();
            int counter = 0;

            while(iterator.hasNext()){
                Transaction t = iterator.next();
                if(!t.getDate().toLocalDate().isAfter(start.minusDays(1)) || !t.getDate().toLocalDate().isBefore(end.plusDays(1))){
                    start = end;
                    end = end.plus(diff);
                    counter++;
                }
                if(t.getDate().toLocalDate().isAfter(start.minusDays(1)) && t.getDate().toLocalDate().isBefore(end.plusDays(1))){
                    switch(t.getCategory()){
                        case "Food":
                        foodSum.add(Double.valueOf(moneyFormat.format(t.getPrice())));
                        break;
                    case "Transportation":
                        transportation += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Home & Utilities":
                        home += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Personal & Family Care":
                        personal += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Entertainment":
                        entertainment += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    case "Others":
                        others += Double.valueOf(moneyFormat.format(t.getPrice()));
                        break;
                    default:
                        System.out.println("Pie Chart Value Check: FAILED");
                        break;
                    }
                }else{
                    System.out.println("An error occured in the loop");
                }
            }
        }
             
            for(Transaction t : account.getTransactions()){
                if(t.getDate().toLocalDate().isAfter(start.minusDays(1))){
                    transactions.add(t);
                }
            }

            transactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));

            boolean failFlag = false;

            for(XYChart.Series<String,BigDecimal> s : lineGraph.getData()){
                BigDecimal sum = new BigDecimal(0);
                sum = sum.setScale(2, RoundingMode.HALF_EVEN);

                for(int i = 0; i < s.getData().size(); i++){
                    if(transactions.get(i).getSign() == '-'){
                        sum = sum.subtract(new BigDecimal(transactions.get(i).getPrice()).setScale(2, RoundingMode.HALF_EVEN));
                    }else{
                        sum = sum.add(new BigDecimal(transactions.get(i).getPrice()).setScale(2, RoundingMode.HALF_EVEN));
                    }

                    if(s.getData().get(i).getXValue().equals(transactions.get(i).getDate().toLocalDate().toString()) && s.getData().get(i).getYValue().compareTo(sum) == 0){
                    }else{
                        failFlag = true;
                        System.out.println("Series: "+s.getData().get(i).getXValue()+" Value: "+transactions.get(i).getDate().toLocalDate().toString());
                        System.out.println("Series: "+s.getData().get(i).getYValue()+" Value: "+sum);
                        System.out.println("Line Chart Value Check("+time+"): FAILED");
                        break;
                    }
                }
            }

            if(!failFlag){
                System.out.println("Line Chart Value Check("+time+"): PASSED");
            }
        }
    }*/
}
