import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class UserAccount{

    ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    public UserAccount(){

    }

    public void newTransaction(String itemR, double priceR, String categoryR, char signR){
        Transaction t = new Transaction(itemR, priceR, categoryR, signR);
        transactions.add(t);
    }
}