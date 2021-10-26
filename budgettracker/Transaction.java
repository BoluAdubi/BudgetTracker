package budgettracker;

import java.util.Date;


public class Transaction{
    private Date date;
    private String item;
    private double price; 
    private String category; 
    private char sign;

    public Transaction(Date dateR, char signR, String itemR, double priceR, String categoryR){
        date = dateR;
        sign = signR;
        item = itemR;
        price = priceR;
        category = categoryR;
    }
    public String getCategory() {
        return category;
    }
    public String getItem() {
        return item;
    }
    public double getPrice() {
        return price;
    }
    public char getSign() {
        return sign;
    }
    public Date getDate() {
        return date;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setSign(char sign) {
        this.sign = sign;
    }
}