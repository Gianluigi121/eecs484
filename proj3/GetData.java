import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.Vector;



//json.simple 1.1
// import org.json.simple.JSONObject;
// import org.json.simple.JSONArray;

// Alternate implementation of JSON modules.
import org.json.JSONObject;
import org.json.JSONArray;

public class GetData{
	
    static String prefix = "project3.";
	
    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;
	
    // You must refer to the following variables for the corresponding 
    // tables in your database

    String cityTableName = null;
    String userTableName = null;
    String friendsTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;
    String programTableName = null;
    String educationTableName = null;
    String eventTableName = null;
    String participantTableName = null;
    String albumTableName = null;
    String photoTableName = null;
    String coverPhotoTableName = null;
    String tagTableName = null;

    // This is the data structure to store all users' information
    // DO NOT change the name
    JSONArray users_info = new JSONArray();		// declare a new JSONArray

	
    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
	super();
	String dataType = u;
	oracleConnection = c;
	// You will use the following tables in your Java code
	cityTableName = prefix+dataType+"_CITIES";
	userTableName = prefix+dataType+"_USERS";
	friendsTableName = prefix+dataType+"_FRIENDS";
	currentCityTableName = prefix+dataType+"_USER_CURRENT_CITIES";
	hometownCityTableName = prefix+dataType+"_USER_HOMETOWN_CITIES";
	programTableName = prefix+dataType+"_PROGRAMS";
	educationTableName = prefix+dataType+"_EDUCATION";
	eventTableName = prefix+dataType+"_USER_EVENTS";
	albumTableName = prefix+dataType+"_ALBUMS";
	photoTableName = prefix+dataType+"_PHOTOS";
	tagTableName = prefix+dataType+"_TAGS";
    }
	
	
	
	
    //implement this function

@SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException{ 

    	JSONArray users_info = new JSONArray();
		
		// Your implementation goes here....
		try (Statement stmt = oracleConnection.createStatement()) {

            // Create list of User IDs
    		Vector<Long> users_list = new Vector<Long>();
            ResultSet rst = stmt.executeQuery(
				"SELECT DISTINCT U.User_ID " +
				"FROM " + userTableName + " U "
			);
            
			while (rst.next()) {
				users_list.add(rst.getLong(1));
			}

            // Iterate thru user IDs and get data of each
            for (Long user_id : users_list) {
                
                // Get User Info
                rst = stmt.executeQuery(
                    "SELECT DISTINCT U.User_ID, U.First_Name, U.Last_Name, U.Gender, U.Year_Of_Birth, U.Month_Of_Birth, U.Day_Of_Birth " +
                    "FROM " + userTableName + " U " +
                    "WHERE U.User_ID = " + user_id
                );
                
                JSONObject user = new JSONObject();
                if (rst.next()) {
                    user.put("user_id", rst.getLong(1));
                    user.put("first_name", rst.getString(2));
                    user.put("last_name", rst.getString(3));
                    user.put("gender", rst.getString(4));
                    user.put("YOB", rst.getInt(5));
                    user.put("MOB", rst.getInt(6));
                    user.put("DOB", rst.getInt(7));
                }
                
                // Get Hometown
                rst = stmt.executeQuery(
                    "SELECT C.city_name, C.state_name, C.country_name " +
                    "FROM " + hometownCityTableName + " HT " + 
                        "JOIN " + cityTableName + " C ON HT.hometown_city_id = C.city_id " +
                    "WHERE HT.user_id = " + user_id
                );

                JSONObject hometown = new JSONObject();
                if (rst.next()) {
                    hometown.put("city", rst.getString(1));
                    hometown.put("state", rst.getString(2));
                    hometown.put("country", rst.getString(3));
                }
                user.put("hometown", hometown);

                // Get Current City
                rst = stmt.executeQuery(
                    "SELECT C.city_name, C.state_name, C.country_name " +
                    "FROM " + currentCityTableName + " CC " +
                        "JOIN " +  cityTableName + " C ON CC.current_city_id = C.city_id " +
                    "WHERE CC.user_id = " + user_id
                );

                JSONObject current = new JSONObject();
                if (rst.next()) {
                    current.put("city", rst.getString(1));
                    current.put("state", rst.getString(2));
                    current.put("country", rst.getString(3));
                }
                user.put("current", current);
                
                // Get Friends
                rst = stmt.executeQuery(
                    "SELECT F.user2_id " +
                    "FROM " + friendsTableName + " F " +
                    "WHERE F.user1_id = " + user_id + " AND F.user1_id < F.user2_id " +
                    "ORDER BY F.user2_id"
                );

                JSONArray friends = new JSONArray();
                while (rst.next()) {
                    friends.put(rst.getLong(1));
                }
                user.put("friends", friends);
                
                users_info.put(user);
            }
            
            stmt.close();
            rst.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users_info;
    }

    // This outputs to a file "output.json"
    public void writeJSON(JSONArray users_info) {
	// DO NOT MODIFY this function
	try {
	    FileWriter file = new FileWriter(System.getProperty("user.dir")+"/output.json");
	    file.write(users_info.toString());
	    file.flush();
	    file.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
		
    }
}
