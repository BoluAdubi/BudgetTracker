package budgettracker;

import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class UserAccount{

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private HashMap<String, Double> categories = new HashMap<String, Double>();
    
    private ObservableList<Double> cate = FXCollections.observableArrayList();

    public UserAccount(){
        fillHashMap();
        for(int i=0;i<6;i++){
            cate.add(0.00);
        }
    }

    public void newTransaction(String itemR, double priceR, String categoryR, char signR){
        Transaction t = new Transaction(itemR, priceR, categoryR, signR);
        transactions.add(t);
    }

    public ObservableList<Double> getCategoryValues(){
        return cate;
    }

    public HashMap<String,Double> getCategories(){
        return categories;
    }
    public ObservableList<Transaction> getTransactions(){
        return transactions;
    }

    private void fillHashMap(){
        categories.put("Entertainment", 1.0);
        categories.put("Food", 1.0);
        categories.put("Transportation", 1.0);
        categories.put("Home & Utilities", 1.0);
        categories.put("Personal & Family Care", 1.00);
        categories.put("Others", 1.00);
    }
}