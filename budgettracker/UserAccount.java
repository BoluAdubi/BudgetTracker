package budgettracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;


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

    public ObservableList<Goal> getGoals() {
        return goals;
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

    public ArrayList<String> checkGoals(){
        ArrayList<String> brokenGoals = new ArrayList<String>();
        for(int i = 0; i < goals.size(); i++){
            if(categories.contains(goals.get(i).getGoalCategory())){
                if(categoryExpenseValues.get(i) > goals.get(i).getGoalPrice()){
                    brokenGoals.add(goals.get(i).getGoalCategory());
                }
            }
        }
        return brokenGoals;
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