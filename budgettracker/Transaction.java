package budgettracker;

import java.util.Date;


public class Transaction{
    private Date date;
    private String item;
    private double price; 
    private String category; 
    private char sign;

    /**
     * Takes in the data, sign, item, price and category and creates getters and setters so the data can be accessed 
     * @param dateR
     * @param signR
     * @param itemR
     * @param priceR
     * @param categoryR
     */
    public Transaction(Date dateR, char signR, String itemR, double priceR, String categoryR){
        date = dateR;
        sign = signR;
        item = itemR;
        price = priceR;
        category = categoryR;
    }
    
    /** 
     * getter for getting a ctegory
     * @return String
     */
    public String getCategory() {
        return category;
    }
    
    /** 
     * getter for item 
     * @return String
     */
    public String getItem() {
        return item;
    }
    
    /** 
     * getter for price 
     * @return double
     */
    public double getPrice() {
        return price;
    }
    
    /** 
     * getter for sign
     * @return char
     */
    public char getSign() {
        return sign;
    }
    
    /** 
     * getter for date
     * @return Date
     */
    public Date getDate() {
        return date;
    }
    
    /** 
     * setter for Category
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * setter for Item  
     * @param item
     */
    public void setItem(String item) {
        this.item = item;
    }
    
    /** 
     * setter for price
     * @param price
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /** 
     * setter for sign 
     * @param sign
     */
    public void setSign(char sign) {
        this.sign = sign;
    }
}