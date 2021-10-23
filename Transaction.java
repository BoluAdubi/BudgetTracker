
public class Transaction{
    private String item;
    private double price; 
    private String category; 
    private char sign;
    public Transaction(String itemR, double priceR, String categoryR, char signR){
        item = itemR;
        price = priceR;
        category = categoryR;
        sign = signR;
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