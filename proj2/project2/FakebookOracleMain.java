package project2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class FakebookOracleMain {
    // Member Variables
    private static String username = "cktran";                    // place your uniqname here
    private static String password = "CKTran45";                    // place your Oracle/SQL*PLUS password here
    
    // [Main Function]
    // Expected Command Line Arguments:
    //  [0] either "all" or a digit 0-9, representing what query to run
    //  [1] either "p" or "t", indicating "print results" and "time queries" respectively
    public static void main(String[] args) {
        int start = 0;                                // inclusive
        int stop = 10;                                // exclusive
        if (!(args[0].equals("all"))) {
            start = Integer.parseInt(args[0]);
            stop = start + 1;
        }

        boolean print = args[1].equals("p");
        if (print) {
            query(start, stop);
        }
        else {
            time(start, stop);
        }
    }
    
    private static void query(int start, int stop) {
        try (Connection oracleConnection = getConnection()) {
            FakebookOracle db = new StudentFakebookOracle(oracleConnection);
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            
            for (int query = start; query < stop; query++) {
                switch (query) {
                    case 0:
                        db.printQuery0(out, db.findMonthOfBirthInfo());
                        break;
                    case 1:
                        db.printQuery1(out, db.findNameInfo());
                        break;
                    case 2:
                        db.printQuery2(out, db.lonelyUsers());
                        break;
                    case 3:
                        db.printQuery3(out, db.liveAwayFromHome());
                        break;
                    case 4:
                        db.printQuery4(out, db.findPhotosWithMostTags(5));
                        break;
                    case 5:
                        db.printQuery5(out, db.matchMaker(5, 2));
                        break;
                    case 6:
                        db.printQuery6(out, db.suggestFriends(5));
                        break;
                    case 7:
                        db.printQuery7(out, db.findEventStates());
                        break;
                    case 8:
                        db.printQuery8(out, db.findAgeInfo(215L));
                        break;
                    case 9:
                        db.printQuery9(out, db.findPotentialSiblings());
                        break;
                    default:
                        break;
                }
            }
            
            oracleConnection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void time(int start, int stop) {
        long beginTime = 0L;
        long endTime = 0L;
        
        try (Connection oracleConnection = getConnection()) {
            FakebookOracle db = new StudentFakebookOracle(oracleConnection);
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            
            for (int query = start; query < stop; query++) {
                beginTime = System.currentTimeMillis();
                switch (query) {
                    case 0:
                        db.findMonthOfBirthInfo();
                        break;
                    case 1:
                        db.findNameInfo();
                        break;
                    case 2:
                        db.lonelyUsers();
                        break;
                    case 3:
                        db.liveAwayFromHome();
                        break;
                    case 4:
                        db.findPhotosWithMostTags(5);
                        break;
                    case 5:
                        db.matchMaker(5, 2);
                        break;
                    case 6:
                        db.suggestFriends(5);
                        break;
                    case 7:
                        db.findEventStates();
                        break;
                    case 8:
                        db.findAgeInfo(215L);
                        break;
                    case 9:
                        db.findPotentialSiblings();
                        break;
                    default:
                        break;
                }
                endTime = System.currentTimeMillis();
                
                out.write(String.format("Query %d Time: %.3f%n", query, (endTime - beginTime) / 1000.0));
                out.flush();
            }
            
            oracleConnection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return DriverManager.getConnection("jdbc:oracle:thin:@forktail.dsc.umich.edu:1521:COURSEDB", username, password);
    }
}
