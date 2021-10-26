package budgettracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;


public class UserAccount{

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private ObservableList<String> categories = FXCollections.observableArrayList();
    
    private ObservableList<Double> categoryValues = FXCollections.observableArrayList();

    public UserAccount(){
        fillHashMap();
        for(int i=0;i<6;i++){
            categoryValues.add(0.00);
        }
    }

    public void newTransaction(Date dateR, char signR, String itemR, double priceR, String categoryR){
        Transaction t = new Transaction(dateR, signR, itemR, priceR, categoryR);
        transactions.add(t);
    }

    public ObservableList<Double> getCategoryValues(){
        return categoryValues;
    }

    public ObservableList<String> getCategories(){
        return categories;
    }
    public ObservableList<Transaction> getTransactions(){
        return transactions;
    }

    private void fillHashMap(){
        categories.add("Entertainment");
        categories.add("Food");
        categories.add("Transportation");
        categories.add("Home & Utilities");
        categories.add("Personal & Family Care");
        categories.add("Others");
    }
}