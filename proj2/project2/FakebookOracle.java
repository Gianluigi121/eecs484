package project2;

import java.sql.SQLException;
import java.io.IOException;
import java.io.Writer;

public abstract class FakebookOracle {
    // Abstract Query Functions to be Implemented
    public abstract BirthMonthInfo findMonthOfBirthInfo() throws SQLException;
    public abstract FirstNameInfo findNameInfo() throws SQLException;
    public abstract FakebookArrayList<UserInfo> lonelyUsers() throws SQLException;
    public abstract FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException;
    public abstract FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException;
    public abstract FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException;
    public abstract FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException;
    public abstract EventStateInfo findEventStates() throws SQLException;
    public abstract AgeInfo findAgeInfo(long userID) throws SQLException;
    public abstract FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException;
    
    // Query Result Printing Functions
    public void printQuery0(Writer writer, BirthMonthInfo results) throws IOException {
        String output = String.format("%sQuery 0%s%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery1(Writer writer, FirstNameInfo results) throws IOException {
        String output = String.format("%sQuery 1%s%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery2(Writer writer, FakebookArrayList<UserInfo> results) throws IOException {
        String output = String.format("%sQuery 2%s%nNumber of lonely users: %d%nLonely users are: %s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results.size(), results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery3(Writer writer, FakebookArrayList<UserInfo> results) throws IOException {
        String output = String.format("%sQuery 3%s%nNumber of users who live away from home: %d%nThose users are: %s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results.size(), results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery4(Writer writer, FakebookArrayList<TaggedPhotoInfo> results) throws IOException {
        String output = String.format("%sQuery 4%s%nThe following are the top %d photo(s) with the most tags:%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results.size(), results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery5(Writer writer, FakebookArrayList<MatchPair> results) throws IOException {
        String output = String.format("%sQuery 5%s%nTop %d match(es):%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results.size(), results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery6(Writer writer, FakebookArrayList<UsersPair> results) throws IOException {
        String output = String.format("%sQuery 6%s%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery7(Writer writer, EventStateInfo results) throws IOException {
        String output = String.format("%sQuery 7%s%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery8(Writer writer, AgeInfo results) throws IOException {
        String output = String.format("%sQuery 8%s%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results);
        writer.write(output);
        writer.flush();
    }
    
    public void printQuery9(Writer writer, FakebookArrayList<SiblingInfo> results) throws IOException {
        String output = String.format("%sQuery 9%s%n%d pair(s) of potential siblings:%n%s%n%n",
            FakebookOracleConstants.PrintDecoration, FakebookOracleConstants.PrintDecoration, results.size(), results);
        writer.write(output);
        writer.flush();
    }
}
