package budgettracker;


public class Goal {

    private String goalCategory;

    //The price that is goal is broken if gone over
    private double goalPrice;

    /**
     * Constructor
     * @param category : String 
     * @param price : double
     */
    public Goal(String category, double price){
        goalCategory = category;
        goalPrice = price;
    }
    /** 
     * Returns the category of this goal
     * @return String
     */
    public String getGoalCategory() {
        return goalCategory;
    }

    
    /** 
     * Returns the price of the goal
     * @return double
     */
    public double getGoalPrice() {
        return goalPrice;
    }

    
    /** 
     * Sets a new price for this goal
     * @param goalPrice : double, the new price
     */
    public void setGoalPrice(double goalPrice) {
        this.goalPrice = goalPrice;
    }
}
