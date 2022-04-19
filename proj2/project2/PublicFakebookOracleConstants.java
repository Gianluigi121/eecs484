package project2;

import java.sql.ResultSet;

/*
    The FakebookConstants class contains constants that you are used by the FakebookOracle
    class to refer to the data schema and the data tables therein contained. This class
    cannot be instantiated directly: all of the constants are static member variables that
    can be accessed as FakebookConstants.<field>.
*/
final class FakebookOracleConstants {
    // Prefix Constant
    private static final String prefix = "project2.PUBLIC_";

    // Table Names
    public static final String UsersTable = String.format("%s%s", prefix, "Users");
    public static final String CitiesTable = String.format("%s%s", prefix, "Cities");
    public static final String FriendsTable = String.format("%s%s", prefix, "Friends");
    public static final String CurrentCitiesTable = String.format("%s%s", prefix, "User_Current_Cities");
    public static final String HometownCitiesTable = String.format("%s%s", prefix, "User_Hometown_Cities");
    public static final String ProgramsTable = String.format("%s%s", prefix, "Programs");
    public static final String EducationTable = String.format("%s%s", prefix, "Education");
    public static final String EventsTable = String.format("%s%s", prefix, "User_Events");
    public static final String AlbumsTable = String.format("%s%s", prefix, "Albums");
    public static final String PhotosTable = String.format("%s%s", prefix, "Photos");
    public static final String TagsTable = String.format("%s%s", prefix, "Tags");
    
    // Printing Constants
    public static final String PrintDecoration = ">>>>>>>>>>>>>>>";
    
    // Result Set Constants Renamed
    public static final int AllScroll = ResultSet.TYPE_SCROLL_INSENSITIVE;
    public static final int ReadOnly = ResultSet.CONCUR_READ_ONLY;
    
    // [Constructor]
    // EFFECTS:  throws an AssertionError
    private FakebookOracleConstants() {
        throw new AssertionError();
    }
}
