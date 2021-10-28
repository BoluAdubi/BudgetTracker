package budgettracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;
import java.util.HashMap;


public class UserAccount{

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private ObservableList<String> categories = FXCollections.observableArrayList();
    
    private ObservableList<Double> categoryExpenseValues = FXCollections.observableArrayList();

    private ObservableList<Goal> goals = FXCollections.observableArrayList();

    /**
     * This function calls fillCategories and then assigns each category the money amount input by the user.
     * @param none
     * @return none
     */
    public UserAccount(){
        fillCategories();
        for(int i=0;i<categories.size();i++){
            categoryExpenseValues.add(0.00);
        }
    }

    /**
     * This function creates a new transaction object t and passes all the parameters to the Transactions class.
     * It then adds t to the transactions list and passes it to updateCategoryValues if its sign data member 
     * is negative.
     * @param dateR
     * @param signR
     * @param itemR
     * @param priceR
     * @param categoryR
     * @return none 
     */
    public void newTransaction(Date dateR, char signR, String itemR, double priceR, String categoryR){
        Transaction t = new Transaction(dateR, signR, itemR, priceR, categoryR);
        transactions.add(t);
        if(t.getSign() == '-')
            updateCategoryValues(t);
    }

    /**
     * When called, this function obtains and returns categoryExpenseValues
     * @param none
     * @return categoryExpenseValues
     */
    public ObservableList<Double> getCategoryExpenseValues(){
        return categoryExpenseValues;
    }

    /**
     *  When called, this function obtains and returns categories
     * @param none
     * @return categories
     */
    public ObservableList<String> getCategories(){
        return categories;
    }

    /**
     *  When called, this function obtains and returns transactions
     * @param none
     * @return transactions
     */
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

    /**
     * This function uses a loop to go through all the goals of each category and reset them if said category 
     * already has a set goal. If a category does not have a goal, this function declares a goal, goalPrice for it.
     * @param goalCategory
     * @param goalPrice
     * @return none
     */
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

    /**
     * This function compares the category of the t parameter to all the categories being used. If it equals to
     * one of these categories, this function sends the name of the category and the value of its expense values 
     * plus the price of t, to categoryExpenseValues.set to increase the amount spent of said category.
     * @param t
     * @return none
     */
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

    /**
     * This function declares all the categories that will be used by the user
     * @param none
     * @return none
     */
    private void fillCategories(){
        categories.add("Entertainment");
        categories.add("Food");
        categories.add("Transportation");
        categories.add("Home & Utilities");
        categories.add("Personal & Family Care");
        categories.add("Others");
    }
}