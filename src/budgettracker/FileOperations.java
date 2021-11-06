package budgettracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
            FileWriter fileWriter = new FileWriter(csvFile);
    
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
                System.out.println(line);
                fileWriter.write(line.toString());

            }
            fileWriter.close();
            return true;
        }catch(Exception e){
            System.out.println("Save Failed");
            return false;
        }
    }

    public boolean addTransactionCSV(File csv, UserAccount account){
        try{
            System.out.println(csv.toString());

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
                for(int i = 0; i < row.length; i++){
                    switch(i){
                        case 0:
                            try{
                                LocalDateTime.parse(row[i]);
                                break;
                            }catch(Exception e){
                                System.out.println("Date failed");
                                return false;
                            }
                        case 1:
                            if(row[i].equals("")){
                                System.out.println("Item failed");
                                return false;
                            }
                            break;
                        case 2:
                            if(!(row[i].matches("[\\d]*[\\.]?[\\d]{0,2}") || row[i].equals(""))){
                                System.out.println("Price failed");
                                return false;
                            }
                            break;
                        case 3:
                            if(!account.getCategories().contains(row[i])){
                                System.out.println("Category failed");
                                return false;
                            }
                            break;
                        case 4:
                            System.out.println(row[i]);
                            if(!(row[i].equals("-")) && !(row[i].equals("+"))){
                                System.out.println("Sign failed");
                                return false;
                            }
                            break;
                    }
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
}
