package budgettracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;


public class UserAccount{

    ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    public UserAccount(){

    }

    public void newTransaction(Date dateR, char signR, String itemR, double priceR, String categoryR){
        Transaction t = new Transaction(dateR, signR, itemR, priceR, categoryR);
        transactions.add(t);
    }

    public ObservableList<Transaction> getTransactions(){
        return transactions;
    }
}