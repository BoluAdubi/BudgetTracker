package budgettracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class UserAccount{

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private ObservableList<String> categories = FXCollections.observableArrayList();
    
    private ObservableList<Double> categoryExpenseValues = FXCollections.observableArrayList();

    private ObservableList<Goal> goals = FXCollections.observableArrayList();

    public UserAccount(){
        fillCategories();
        for(int i=0;i<categories.size();i++){
            categoryExpenseValues.add(0.00);
        }
    }

    public void newTransaction(Date dateR, char signR, String itemR, double priceR, String categoryR){
        Transaction t = new Transaction(dateR, signR, itemR, priceR, categoryR);
        transactions.add(t);
        if(t.getSign() == '-')
            updateCategoryValues(t);
    }

    public ObservableList<Double> getCategoryExpenseValues(){
        return categoryExpenseValues;
    }

    public ObservableList<String> getCategories(){
        return categories;
    }
    public ObservableList<Transaction> getTransactions(){
        return transactions;
    }

    /**
     * First value of double array is current charges to that category, second value is the goal price
     * @return
     */
    public HashMap<String, Double[]> getGoals() {
        HashMap<String, Double[]> goalsAndPrices = new HashMap<String, Double[]>();
        for(Goal g : goals){
            String c = g.getGoalCategory();
            Double arr[] = {categoryExpenseValues.get(getCategoryExpenseIndex(c)), g.getGoalPrice()};
            goalsAndPrices.put(c, arr);
        }
        return goalsAndPrices;
    }

    public void createGoal(String goalCategory, double goalPrice){
        for(Goal g : goals){
            if(g.getGoalCategory() == goalCategory){
                g.setGoalPrice(goalPrice);
                return;
            }
        }
        goals.add(new Goal(goalCategory, goalPrice));
    }

    public HashMap<String, Double> checkGoals(){
        HashMap<String, Double> brokenGoals = new HashMap<String, Double>();
        for(int i = 0; i < goals.size(); i++){
            if(categories.contains(goals.get(i).getGoalCategory())){
                if(categoryExpenseValues.get(i) > goals.get(i).getGoalPrice()){
                    brokenGoals.put(goals.get(i).getGoalCategory(), goals.get(i).getGoalPrice());
                }
            }
        }
        return brokenGoals;
    }

    private int getCategoryExpenseIndex(String category){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i) == category){
                return i;
            }
        }
        return -1; //shouldn't ever happen
    }

    private void updateCategoryValues(Transaction t){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i) == t.getCategory()){
                categoryExpenseValues.set(i, categoryExpenseValues.get(i) + t.getPrice());
            }
        }
    }

    private void fillCategories(){
        categories.add("Entertainment");
        categories.add("Food");
        categories.add("Transportation");
        categories.add("Home & Utilities");
        categories.add("Personal & Family Care");
        categories.add("Others");
    }
}