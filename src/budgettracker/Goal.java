package budgettracker;

//import java.time.LocalDateTime;
//import java.util.Date;

public class Goal {

    private String goalCategory;

    //The price that is goal is broken if gone over
    private double goalPrice;

    private int goalTime;

    private boolean repeatGoal;

    /**
     * Constructor
     * @param category : String 
     * @param price : double
     * @param time : int
     */
    public Goal(String category, double price, int time, boolean repeat){
        goalCategory = category;
        goalPrice = price;
        goalTime = time;
        repeatGoal = repeat;
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
     * Returns the time frame of the goal
     * @return int
     */
    public int getGoalTime() {
        return goalTime;
    }

    /** 
     * Returns true if user wants to repeat a goal, false otherwise
     * @return bool
     */
    public boolean getRepeatGoal() {
        return repeatGoal;
    }

    
    /** 
     * Sets a new price for this goal
     * @param goalPrice : double, the new price
     */
    public void setGoalPrice(double goalPrice) {
        this.goalPrice = goalPrice;
    }

    /** 
     * Sets a new time frame for this goal
     * @param goalTime : int, the new time frame
     */
    public void setGoalTime(int goalTime) {
        this.goalTime = goalTime;
    }

    /** 
     * Sets whether user wants to repeat goal after its completion
     * @param repeatGoal : boolean, true for goal to be repeated, false otherwise
     */
    public void setRepeatGoal(boolean repeatGoal) {
        this.repeatGoal = repeatGoal;
    }


}
