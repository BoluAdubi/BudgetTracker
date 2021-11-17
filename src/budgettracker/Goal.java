/* -------------------------------------------------------------
*
*@file Goal.js
*@author Team 19 
*Description: Deals with the functionality of the goals in the program - 
*               checks if a goal has expired, it it has been broken, getters for goalCatagory, goalprice, goaltime, getGoalrepeate
*               setters for start date, end date, price  
*@date:11/17/2021
-------------------------------------------------------------*/


package budgettracker;

import java.time.LocalDate;
import java.util.HashMap;

public class Goal {

    private String goalCategory;

    //The price that is goal is broken if gone over
    private double goalPrice;

    private int goalTime;

    private boolean repeatGoal;

    private LocalDate goalStartDate;

    private LocalDate goalEndDate;

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
        goalStartDate = LocalDate.now();
        goalEndDate = goalStartDate.plusDays(time);
    }

    /**
     * Constructor
     * @param category : String 
     * @param price : double
     * @param time : int
     */
    public Goal(String category, double price, int time, boolean repeat, LocalDate startDate, LocalDate endDate){
        goalCategory = category;
        goalPrice = price;
        goalTime = time;
        repeatGoal = repeat;
        goalStartDate = startDate;
        goalEndDate = endDate;
    }

     /**
     * isBroken - Checks if the goal has been broken - overspent
     * @param account : UserAccount  
     * @return boolean
     */
    public boolean isBroken(UserAccount account){
        HashMap<String, Double[]> goalData = account.getGoalData();
        if(goalData.get(goalCategory)[0] > goalData.get(goalCategory)[1]){
            return true;
        }else{
            return false;
        }
    }

     /**
     * isExpired - Checks if the goal has expired 
     * 
     * @return boolean
     */
    public boolean isExpired(){
        if(LocalDate.now().isAfter(this.goalEndDate)){
            return true;
        }else{
            return false;
        }
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
    public boolean getGoalRepeat() {
        return repeatGoal;
    }

    /** 
     * Returns the date of the start of the goal
     * @return LocalDate
     */
    public LocalDate getGoalStartDate() {
        return goalStartDate;
    }

    /** 
     * Returns the date of the end of the goal
     * @return LocalDate
     */
    public LocalDate getGoalEndDate() {
        return goalEndDate;
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
    public void setGoalRepeat(boolean repeatGoal) {
        this.repeatGoal = repeatGoal;
    }

    /** 
     * Sets a new start date for the goal
     * @param goalTime : int, the new start date
     */
    public void setGoalStartDate(LocalDate goalStartDate) {
        this.goalStartDate = goalStartDate;
    }

    /** 
     * Sets a new end date for the goal
     * @param goalTime : int, the new end date
     */
    public void setGoalEndDate(LocalDate goalEndDate) {
        this.goalEndDate = goalEndDate;
    }
}
