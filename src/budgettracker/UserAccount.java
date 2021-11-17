package budgettracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.io.File;
import java.time.LocalDate;


public class UserAccount{

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private ObservableList<String> categories = FXCollections.observableArrayList();
    
    private ObservableList<Double> categoryExpenseValues = FXCollections.observableArrayList();

    private ObservableList<Double> pieValues = FXCollections.observableArrayList();

    private ObservableList<Goal> goals = FXCollections.observableArrayList();

    private static UserAccount INSTANCE = new UserAccount();

    public boolean goalLoadingPassed = false;

    public boolean transactionLoadingPassed = false;

    /**
     * The constructor calls fillCategories and then instantiates each value in categoryExpenseValues to zero.
     * @param none
     * @return none
     */
    private UserAccount(){
        fillCategories();
        for(int i=0;i<categories.size();i++){
            categoryExpenseValues.add(0.00);
        }
        for(int i=0;i<categories.size();i++){
            pieValues.add(0.00);
        }
        FileOperations f = new FileOperations();
        if(f.addGoalCSV(new File("src/data/saves/goalHistory.csv"), this)){
            goalLoadingPassed = true;
        }else{
            System.out.println("Loading goalHistory.csv failed, it may not exist or be formatted incorrectly");
        }
        if(f.addTransactionCSV(new File("src/data/saves/transactionHistory.csv"), this)){
            transactionLoadingPassed = true;
        }else{
            System.out.println("Loading transactionHistory.csv failed, it may not exist or be formatted incorrectly");
        }
    }

    public static UserAccount getInstance(){
        return INSTANCE;
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
    public void newTransaction(LocalDateTime dateR, char signR, String itemR, double priceR, String categoryR){
        Transaction t = new Transaction(dateR, signR, itemR, priceR, categoryR);
        transactions.add(t);
        if(t.getSign() == '-'){
            updatePieValues(t);
            for(Goal g : goals){
                if(g.getGoalCategory().equals(categoryR) && ((dateR.toLocalDate().isAfter(g.getGoalStartDate()) && dateR.toLocalDate().isBefore(g.getGoalEndDate())
                                                         || dateR.toLocalDate().equals(g.getGoalStartDate()) || dateR.toLocalDate().equals(g.getGoalEndDate())))){
                    updateCategoryValues(t);
                }
            }
        }
    }

    public void addTransaction(Transaction newTransaction){
        transactions.add(newTransaction);
        if(newTransaction.getSign() == '-'){
            updatePieValues(newTransaction);
            for(Goal g : goals){
                if(g.getGoalCategory().equals(newTransaction.getCategory()) && ((newTransaction.getDate().toLocalDate().isAfter(g.getGoalStartDate()) && newTransaction.getDate().toLocalDate().isBefore(g.getGoalEndDate())) 
                                                                            || newTransaction.getDate().toLocalDate().equals(g.getGoalStartDate()) || newTransaction.getDate().toLocalDate().equals(g.getGoalEndDate()))){
                    updateCategoryValues(newTransaction);
                }
            }
        }
    }

    /**
     * When called, this function obtains and returns categoryExpenseValues
     * @param none
     * @return categoryExpenseValues
     */
    public ObservableList<Double> getCategoryExpenseValues(){
        return categoryExpenseValues;
    }

    public ObservableList<Double> getPieValues(){
        return pieValues;
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
     * Returns a hashmap for the UIController to use to update the goal progressbars.
     * @return HashMap<String, Double[]> : <{category, [currentExpenditure, goalPrice]} , ... >
     */
    public HashMap<String, Double[]> getGoalData() {
        HashMap<String, Double[]> goalsAndPrices = new HashMap<String, Double[]>();
        for(Goal g : goals){
            String c = g.getGoalCategory();
            Double arr[] = {categoryExpenseValues.get(getCategoryExpenseIndex(c)), g.getGoalPrice()};
            goalsAndPrices.put(c, arr);
        }
        return goalsAndPrices;
    }

    public Goal[] getGoals(){
        return goals.toArray(new Goal[goals.size()]);
    }

    public void emptyExpenseValue(String category){
        categoryExpenseValues.set(getCategoryExpenseIndex(category), 0.0);
    }

    /**
     * This function uses a loop to go through all the goals that have been created and reset them if said category 
     * already has a set goal. If a category does not have a goal, this function creates a goal and goalPrice for it.
     * @param goalCategory
     * @param goalPrice
     * @return none
     */
    public void createGoal(String goalCategory, double goalPrice, int goalTime, boolean repeatGoal){
        for(Goal g : goals){
            if(g.getGoalCategory().equals(goalCategory)){
                g.setGoalPrice(goalPrice);
                g.setGoalTime(goalTime);
                g.setGoalRepeat(repeatGoal);
                g.setGoalStartDate(LocalDate.now());
                g.setGoalEndDate(LocalDate.now().plusDays(goalTime));
                emptyExpenseValue(g.getGoalCategory());

                return;
            }
        }
        goals.add(new Goal(goalCategory, goalPrice, goalTime, repeatGoal));
        emptyExpenseValue(goalCategory);
    }

    public void addGoals(Goal[] goalArr){
        goals.addAll(goalArr);
    }

    /** 
     * Returns a hashmap of the goals that have been broken. This method is not
     * used and needs to be updated to have the same return format of getGoals()
     * This will be helpful when we add functionality of custom categories.
     * @return HashMap<String, Double>
     */
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
     * This function compares the category given in the parameter to all the categories being used. 
     * Once it finds the category, this function sends the index of the category back to the caller.
     * This is used to find the correct index in categoryExpenseValues to update. A different
     * Data structure that could hold both of these things would be nice, but the expenseValues are needed
     * to undate the pieChart, so it must be an observable list. (This has not been implemented yet, right
     * now the pieChart is updated manually)
     * @param category
     * @return none
     */
    public int getCategoryExpenseIndex(String category){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).equals(category)){
                return i;
            }
        }
        return -1; //shouldn't ever happen
    }

    /**
     * This function compares the category of the t parameter to all the categories being used. If it equals to
     * one of these categories, this function sends the index of the category and the value of its expense values 
     * plus the price of t, to categoryExpenseValues.set() to increase the amount spent of said category.
     * @param t
     * @return none
     */
    private void updateCategoryValues(Transaction t){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).equals(t.getCategory())){
                categoryExpenseValues.set(i, categoryExpenseValues.get(i) + t.getPrice());
            }
        }
    }

    private void updatePieValues(Transaction t){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).equals(t.getCategory())){
                pieValues.set(i, pieValues.get(i) + t.getPrice());
            }
        }
    }

    /**
     * This function declares all the categories that will be used by the user,
     * then adds them to the observablelist categories
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