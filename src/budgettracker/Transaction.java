/** -------------------------------------------------------------
* The transaction class handles the functionality of creating a user transaction.
* This class defines a transaction as an item, category, price, sign and a date stamp.
* Transactions.java also has methods that get and set transaction data.
* @file Transaction.java
* @author Team 19
* @date:11/17/2021
-------------------------------------------------------------*/

package budgettracker;

import java.time.LocalDateTime;




public class Transaction{

    private LocalDateTime date;
    private String item;
    private double price;
    private String category;
    private char sign;

    /**
     * Takes in the date, sign, item, price and category for the new Transaction
     * @param dateR
     * @param signR
     * @param itemR
     * @param priceR
     * @param categoryR
     */
    public Transaction(LocalDateTime dateR, char signR, String itemR, double priceR, String categoryR){
        date = dateR;
        sign = signR;
        item = itemR;
        price = priceR;
        category = categoryR;
    }

    /**
     * getter for the category
     * @return String
     */
    public String getCategory() {
        return category;
    }

    /**
     * getter for the item name
     * @return String
     */
    public String getItem() {
        return item;
    }

    /**
     * getter for the price
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * getter for the sign
     * @return char
     */
    public char getSign() {
        return sign;
    }

    /**
     * getter for the date
     * @return Date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * setter for the Category
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * setter for the Item
     * @param item
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * setter for the price
     * @param price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * setter for the sign
     * @param sign
     */
    public void setSign(char sign) {
        this.sign = sign;
    }
}
