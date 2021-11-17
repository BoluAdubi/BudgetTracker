package budgettracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class FileOperations {
    
    public boolean saveTransactions(UserAccount account){
        try{

            ArrayList<String[]> transactionArray = new ArrayList<String[]>();

            for(Transaction t : account.getTransactions()){
                String[] transaction = new String[5];
                
                transaction[0] = t.getDate().toString();
                transaction[1] = t.getItem();
                transaction[2] = Double.toString(t.getPrice());
                transaction[3] = t.getCategory();
                transaction[4] = Character.toString(t.getSign());

                transactionArray.add(transaction);
            }

            File csvFile = new File("src/data/saves/transactionHistory.csv");
            FileWriter fileWriter = new FileWriter(csvFile, false);
    
            fileWriter.write("date,item,price,category,sign\n");
            
            for (String[] data : transactionArray) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    line.append(data[i].replaceAll("\"","\"\""));
                    if (i != data.length - 1) {
                        line.append(',');
                    }
                }
                line.append("\n");
                fileWriter.write(line.toString());

            }
            fileWriter.close();
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public boolean addTransactionCSV(File csv, UserAccount account){
        try{
            BufferedReader readCsv = new BufferedReader(new FileReader(csv));
            
            String[] topLine = readCsv.readLine().split(",");

            ArrayList<Transaction> transactions = new ArrayList<Transaction>();

            while(true){
                String rowString = readCsv.readLine();
                String[] row;
                if(rowString == null){
                    break;
                }else{
                    row = rowString.split(",");
                }

                if(row[4].equals("-")){
                    try{
                        LocalDateTime.parse(row[0]);
                    }catch(Exception e){
                        System.out.println("Date failed");
                        return false;
                    }
                    if(row[1].equals("")){
                        System.out.println("Item failed");
                        return false;
                    }
                    if(!(row[2].matches("[\\d]*[\\.]?[\\d]{0,2}") || row[2].equals(""))){
                        System.out.println("Price failed");
                        return false;
                    }
                    if(!account.getCategories().contains(row[3]) || row[4] == ""){ //this looks like a bug
                        System.out.println("Category failed");
                        return false;
                    }
                } else if (row[4].equals("+")) {
                    try{
                        LocalDateTime.parse(row[0]);
                    }catch(Exception e){
                        System.out.println("Date failed");
                        return false;
                    }
                    if(!(row[2].matches("[\\d]*[\\.]?[\\d]{0,2}") || row[2].equals(""))){
                        System.out.println("Price failed");
                        return false;
                    }
                } else {
                    System.out.println("Sign failed");
                    return false;
                }
                transactions.add(new Transaction(LocalDateTime.parse(row[0]), row[4].toCharArray()[0], row[1], Double.parseDouble(row[2]), row[3]));
            }

            readCsv.close();
            for(Transaction t : transactions){
                account.addTransaction(t);
            }
            return true;
        }catch(Exception e){
            System.out.println(e.toString());
            return false;
        }
    }

    public boolean saveGoals(UserAccount account){
        try{
            ArrayList<String[]> goalArray = new ArrayList<String[]>();
            ArrayList<Goal> goals = new ArrayList<Goal>(Arrays.asList(account.getGoals()));

            for(Goal g : goals){
                String[] goal = new String[6];
                
                goal[0] = g.getGoalCategory();
                goal[1] = Double.toString(g.getGoalPrice());
                goal[2] = Integer.toString(g.getGoalTime());
                goal[3] = Boolean.toString(g.getGoalRepeat());
                goal[4] = g.getGoalStartDate().toString();
                goal[5] = g.getGoalEndDate().toString();

                goalArray.add(goal);
            }

            File csvFile = new File("src/data/saves/goalHistory.csv");
            FileWriter fileWriter = new FileWriter(csvFile, false);
    
            fileWriter.write("category,price,time,repeat,startDate,endDate\n");
            
            for (String[] data : goalArray) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    line.append(data[i].replaceAll("\"","\"\""));
                    if (i != data.length - 1) {
                        line.append(',');
                    }
                }
                line.append("\n");
                fileWriter.write(line.toString());

            }
            fileWriter.close();
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public boolean addGoalCSV(File csv, UserAccount account){
        try{
            BufferedReader readCsv = new BufferedReader(new FileReader(csv));
            
            String[] topLine = readCsv.readLine().split(",");

            ArrayList<Goal> goals = new ArrayList<Goal>();

            while(true){
                String rowString = readCsv.readLine();
                String[] row;
                if(rowString == null){
                    break;
                }else{
                    row = rowString.split(",");
                }

                if(!account.getCategories().contains(row[0]) || row[0].equals("")){
                    System.out.println("Category failed");
                    return false;
                }
                if(!(row[1].matches("[\\d]*[\\.]?[\\d]{0,2}") || row[1].equals(""))){
                    System.out.println("Price failed");
                    return false;
                }
                if(!row[2].matches("[\\d]{1,3}") || row[2].equals("")){
                    System.out.println("Time failed");
                    return false;
                }
                if(!row[3].equals("true") && !row[3].equals("false")){
                    System.out.println("Repeat failed: " + row[0]);
                    return false;
                }
                try{
                    LocalDate.parse(row[4]);
                    LocalDate.parse(row[5]);
                }catch(Exception e){
                    System.out.println("Date failed");
                    return false;
                }
                goals.add(new Goal(row[0], Double.parseDouble(row[1]), Integer.parseInt(row[2]), Boolean.parseBoolean(row[3]), LocalDate.parse(row[4]), LocalDate.parse(row[5])));
            }

            readCsv.close();
            account.addGoals(goals.toArray(Goal [] :: new));
            return true;
        }catch(Exception e){
            System.out.println(e.toString());
            return false;
        }
    }

    
}
