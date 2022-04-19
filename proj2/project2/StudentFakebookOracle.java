package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }
    
    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                "SELECT COUNT(*) AS Birthed, Month_of_Birth " +         // select birth months and number of uses with that birth month
                "FROM " + UsersTable + " " +                            // from all users
                "WHERE Month_of_Birth IS NOT NULL " +                   // for which a birth month is available
                "GROUP BY Month_of_Birth " +                            // group into buckets by birth month
                "ORDER BY Birthed DESC, Month_of_Birth ASC");           // sort by users born in that month, descending; break ties by birth month
            
            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) {                       // step through result rows/records one by one
                if (rst.isFirst()) {                   // if first record
                    mostMonth = rst.getInt(2);         //   it is the month with the most
                }
                if (rst.isLast()) {                    // if last record
                    leastMonth = rst.getInt(2);        //   it is the month with the least
                }
                total += rst.getInt(1);                // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);
            
            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +                // select ID, first name, and last name
                "FROM " + UsersTable + " " +                              // from all users
                "WHERE Month_of_Birth = " + mostMonth + " " +             // born in the most popular birth month
                "ORDER BY User_ID");                                      // sort smaller IDs first
                
            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +                // select ID, first name, and last name
                "FROM " + UsersTable + " " +                              // from all users
                "WHERE Month_of_Birth = " + leastMonth + " " +            // born in the least popular birth month
                "ORDER BY User_ID");                                      // sort smaller IDs first
                
            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close();                            // if you close the statement first, the result set gets closed automatically

            return info;

        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }
    
    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {

            FirstNameInfo info = new FirstNameInfo();
            
            // Step 1
            // ------------
            // * Find longest first name
            ResultSet rst = stmt.executeQuery(
                "SELECT DISTINCT First_Name " +
                "FROM " + UsersTable + " " +                           
                "WHERE LENGTH(First_Name) = (SELECT MAX(LENGTH(First_Name)) FROM " + UsersTable + ") " +
                "ORDER BY First_Name");
            
            while (rst.next()) {                                     info.addLongName(rst.getString(1));
            }
            
            // Step 2
            // ------------
            // * Find shortest first name
            rst = stmt.executeQuery(
                "SELECT DISTINCT First_Name " +
                "FROM " + UsersTable + " " +
                "WHERE LENGTH(First_Name) = (SELECT MIN(LENGTH(First_Name)) FROM " + UsersTable + ") " +
                "ORDER BY First_Name");
            
            while (rst.next()) {                                     info.addShortName(rst.getString(1));
            }

            // Step 3
            // ------------
            // * Find common first name
            rst = stmt.executeQuery(
                "SELECT First_Name, COUNT(*) " +
                "FROM " + UsersTable + " " +
                "GROUP BY First_Name " +
                "HAVING COUNT(*) = (SELECT MAX(COUNT(*)) FROM " + UsersTable + " GROUP BY First_Name)"
            );
                
            while (rst.next()) {
                info.addCommonName(rst.getString(1));
                info.setCommonNameCount(rst.getLong(2));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close();
            return info;
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }
    
    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            
            ResultSet rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +
                "FROM " + UsersTable + " " +
                "WHERE User_ID NOT IN (SELECT User1_ID FROM " + FriendsTable + " UNION SELECT User2_ID FROM " + FriendsTable + " ) " +
                "ORDER BY User_ID"
            );
            
            while (rst.next()) {
                results.add(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }
            
            rst.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            
            ResultSet rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +
                "FROM " + HometownCitiesTable + " NATURAL JOIN " + CurrentCitiesTable + " " +
                "NATURAL JOIN " + UsersTable + " " +
                "WHERE current_city_id <> hometown_city_id " +
                "ORDER BY User_ID ASC"
            );
            
            while (rst.next()) {
                results.add(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }
            
            rst.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */
            stmt.executeUpdate(
                "CREATE OR REPLACE VIEW Tag_Info_Table AS " +
                "SELECT Tg.tag_photo_id, COUNT(Tg.tag_photo_id) AS \"NUM_TAG\" " + 
                "FROM " + TagsTable + " Tg " +
                "GROUP BY Tg.tag_photo_id " +
                "ORDER BY NUM_TAG DESC, Tg.tag_photo_id ASC"
            );

            ResultSet rst = stmt.executeQuery(
                "SELECT U.User_ID, U.First_Name, U.Last_Name, P.photo_id, P.album_id, P.photo_link, A.album_name " + 
                "FROM " + UsersTable + " U, " + PhotosTable + " P, " + AlbumsTable + " A, Tag_Info_Table TI, " + TagsTable + " T " +
                "WHERE TI.tag_photo_id = T.tag_photo_id AND T.tag_photo_id = P.photo_id AND P.album_id = A.album_id " +
                "AND T.tag_subject_id = U.User_ID " +
                "AND TI.tag_photo_id IN (SELECT T1.tag_photo_id FROM Tag_Info_Table T1 WHERE ROWNUM <= " + num + ") " + 
                "ORDER BY TI.NUM_TAG DESC, T.tag_photo_id ASC, U.User_ID ASC"
            );

            Long photo_ID_curr = -1L;
			//PhotoInfo p = new PhotoInfo(-1L, -1L, "", "");
			TaggedPhotoInfo tp = null;

			while (rst.next()) {

				if (!photo_ID_curr.equals(rst.getLong(4))) {
					if (tp != null) {
						results.add(tp);
					}
				    Long photo_ID = rst.getLong(4);
				    Long album_ID = rst.getLong(5);
				    String photo_Link = rst.getString(6);
				    String album_Name = rst.getString(7);
					PhotoInfo p = new PhotoInfo(photo_ID, album_ID, photo_Link, album_Name);
					tp = new TaggedPhotoInfo(p);
					photo_ID_curr = photo_ID;
				}

				Long userID = rst.getLong(1);
				String userFirst = rst.getString(2);
				String userLast = rst.getString(3);
				tp.addTaggedUser(new UserInfo(userID, userFirst, userLast));
			}
			results.add(tp);
			stmt.executeUpdate("DROP VIEW Tag_Info_Table");

			rst.close();
			stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */
            stmt.executeUpdate(
                "CREATE VIEW Common_Photo AS " +
                "SELECT U1.User_ID AS U1_ID, U2.User_ID AS U2_ID, COUNT(*) AS \"NUM_COUNT\" " + 
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + TagsTable + " T1, " + TagsTable + " T2 " +
                "WHERE U1.User_ID = T1.tag_subject_id AND U2.User_ID = T2.tag_subject_id AND T1.tag_photo_id = T2.tag_photo_id " + 
                    "AND U1.User_ID < U2.User_ID AND U1.gender = U2.gender " + 
                    "AND U1.Year_of_Birth IS NOT NULL AND U2.Year_of_Birth IS NOT NULL " +
                    "AND ABS(U1.Year_of_Birth - U2.Year_of_Birth) <= " + yearDiff + " " +
                "GROUP BY U1.User_ID, U2.User_ID " +
                "ORDER BY NUM_COUNT DESC, U1_ID ASC, U2_ID ASC"
            );
		    
		    ResultSet rst = stmt.executeQuery(
                "SELECT U1.User_ID, U1.First_Name, U1.Last_Name, U1.Year_of_Birth, U2.User_ID, U2.First_Name, U2.Last_Name, U2.Year_of_Birth, P.photo_id, P.album_id, P.photo_link, A.album_name " + 
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + " Common_Photo CP, " + PhotosTable + " P, " + AlbumsTable + " A, " + TagsTable + " T1, " + TagsTable + " T2 " + 
                "WHERE (U1.User_ID, U2.User_ID) IN ( " + 
                    "SELECT CP1.U1_ID, CP1.U2_ID " +
                    "FROM Common_Photo CP1) " + 
                    "AND CP.U1_ID = U1.User_ID AND CP.U2_ID = U2.User_ID " +
                    "AND U1.User_ID = T1.tag_subject_id AND U2.user_id = T2.tag_subject_id AND T1.tag_photo_id = T2.tag_photo_id " +
                    "AND NOT EXISTS ( " + 
                        "SELECT F.user1_id, F.user2_id " + 
                        "FROM " + FriendsTable + " F " + 
                        "WHERE U1.User_ID = F.user1_id AND U2.User_ID = F.user2_id) " + 
                    "AND T1.tag_photo_id = P.photo_id AND P.album_id = A.album_id " + 
                    "AND ROWNUM <= " + num + " " + 
                    "ORDER BY CP.NUM_COUNT DESC, U1.User_ID ASC, U2.User_ID ASC"
            );

            Long id1 = 0L;
		    Long id2 = 0L;
		    MatchPair mp = new MatchPair(
                new UserInfo (id1, "", ""), -1, 
                new UserInfo (id2, "", ""), -1
            );	
            
		    while (rst.next()) {
		    	long user1_ID = rst.getLong(1);
		    	long user2_ID= rst.getLong(5);

		    	if (user1_ID != id1 || user2_ID != id2) {
		    	    	if (id1 != 0L) {
                            results.add(mp);
                        }

		    	    	String user1_First = rst.getString(2);
		    	    	String user1_Last = rst.getString(3);
		    	    	int user1_Year = rst.getInt(4);

		    	    	String user2_First = rst.getString(6);
		    	    	String user2_Last = rst.getString(7);
		    	    	int user2_Year = rst.getInt(8);

		    	    	mp = new MatchPair(
                            new UserInfo (user1_ID, user1_First, user1_Last), user1_Year, 
                            new UserInfo (user2_ID, user2_First, user2_Last), user2_Year
                        );

		    	    	id1 = user1_ID;
                        id2 = user2_ID;
		    	}

		    	Long commonPhoto_ID = rst.getLong(9);
		    	Long commonPhoto_AlbumID = rst.getLong(10);
		    	String commonPhoto_Link = rst.getString(11);
		    	String commonPhoto_AlbumName = rst.getString(12);

		    	mp.addSharedPhoto(
                    new PhotoInfo(commonPhoto_ID, commonPhoto_AlbumID, commonPhoto_Link, commonPhoto_AlbumName)
                );			

		        results.add(mp);
		    }


            stmt.executeUpdate("DROP VIEW Common_Photo");
            rst.close();
		    stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(16, "The", "Hacker");
                UserInfo u2 = new UserInfo(80, "Dr.", "Marbles");
                UserInfo u3 = new UserInfo(192, "Digit", "Le Boid");
                UsersPair up = new UsersPair(u1, u2);
                up.addSharedFriend(u3);
                results.add(up);
            */

            stmt.executeQuery(
                " CREATE OR REPLACE VIEW Common_Friends AS " + 
                " SELECT T.user1_id AS user1_id, T.user2_id AS user2_id, COUNT(*) AS NUM_CNT" + 
                " FROM " +  
                    " (SELECT F1.user1_id AS user1_id, F2.user2_id AS user2_id " + 
                    " FROM " + FriendsTable + " F1, " + FriendsTable + " F2 " + 
                    " WHERE F1.user2_id = F2.user1_id " + 
                    " AND (F1.user1_id, F2.user2_id) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + " F3) " +
                    " UNION ALL " + 
                    " SELECT F1.user2_id AS user1_id, F2.user2_id AS user2_id " + 
                    " FROM " + FriendsTable + " F1, " + FriendsTable + " F2 " + 
                    " WHERE F1.user1_id = F2.user1_id AND F1.user2_id < F2.user2_id" + 
                    " AND (F1.user2_id, F2.user2_id) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + " F3) " +
                    " UNION ALL " + 
                    " SELECT F1.user1_id AS user1_id, F2.user1_id AS user2_id " + 
                    " FROM " + FriendsTable + " F1, " + FriendsTable + " F2 " + 
                    " WHERE F1.user2_id = F2.user2_id AND F1.user1_id < F2.user1_id" + 
                    " AND (F1.user1_id, F2.user1_id) NOT IN (SELECT DISTINCT * FROM " + FriendsTable + " F3) " +
                " ) T " +
                " GROUP BY T.user1_id, T.user2_id " +
                " ORDER BY COUNT(*) DESC, T.user1_id ASC, T.user2_id ASC "
            );
             
            ResultSet rst = stmt.executeQuery(
                "SELECT CF.user1_id, CF.user2_id, U1.first_name, U1.last_name, U2.first_name, U2.last_name " + 
                "FROM ( " +
                    "SELECT ComF.user1_id, ComF.user2_id, ComF.NUM_CNT " + 
                    "FROM Common_Friends ComF " + 
                    "WHERE ROWNUM <= " + num + " " +
                ") CF, " + UsersTable + " U1, " + UsersTable + " U2 " + 
                "WHERE U1.user_id = CF.user1_id AND U2.user_id = CF.user2_id " + 
                "ORDER BY CF.NUM_CNT DESC, CF.user1_id, CF.user2_id"
            ); 

            Statement stmt2 = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly);
 
            while(rst.next()) { 
                Long user1_ID = rst.getLong(1);
                String user1_First = rst.getString(3);
                String user1_Last = rst.getString(4);

                Long user2_ID = rst.getLong(2);
                String user2_First = rst.getString(5);
                String user2_Last = rst.getString(6); 

                UsersPair pair = new UsersPair(
                    new UserInfo(user1_ID, user1_First, user1_Last), 
                    new UserInfo(user2_ID, user2_First, user2_Last)
                );
               
                ResultSet rst2 = stmt2.executeQuery(
                    "SELECT U.User_ID, U.first_name, U.last_name " +
                    "FROM " + UsersTable + " U, ( " + 
                        "SELECT F1.user2_id AS User_ID " + 
                        "FROM " + FriendsTable + " F1, " + FriendsTable + " F2 " +
                        "WHERE (F1.user1_id = " + Long.toString(user1_ID) + 
                            "AND F2.user2_id = " + Long.toString(user2_ID) + 
                            "AND F2.user1_id = F1.user2_id) " + 
                        "UNION ALL " + 
                        "SELECT F1.user1_id AS User_ID " + 
                        "FROM " + FriendsTable + " F1, " + FriendsTable + " F2 " +
                        "WHERE (F1.user2_id = " + Long.toString(user1_ID) + 
                            "AND F2.user2_id = " + Long.toString(user2_ID) + 
                            "AND F2.user1_id = F1.user1_id) " +
                        "UNION ALL " + 
                        "SELECT F1.user2_id AS User_ID " + 
                        "FROM " + FriendsTable + " F1, " + FriendsTable + " F2 " +
                        "WHERE (F1.user1_id = " + Long.toString(user1_ID) + 
                            "AND F2.user1_id = " + Long.toString(user2_ID) + 
                            "AND F2.user2_id = F1.user2_id) " +
                    ") F " +
                    "WHERE U.User_ID = F.User_ID " + 
                    "ORDER BY U.User_ID " 
                ); 
                
                while(rst2.next()) {
                    pair.addSharedFriend(
                        new UserInfo(rst2.getLong(1), rst2.getString(2), rst2.getString(3))
                    );
                }

                rst2.close();
                results.add(pair);
            }
            
            stmt.executeQuery("DROP VIEW Common_Friends ");

            rst.close();
            stmt.close(); 
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            
            ResultSet rst = stmt.executeQuery(
                "SELECT State_Name, COUNT(*) " +
                "FROM " + EventsTable + " E1 " +
                    "LEFT JOIN " + CitiesTable + " C1 ON E1.event_city_id = C1.city_id " +
                "GROUP BY State_Name " +
                "HAVING COUNT(*) = ( " + 
                    "SELECT MAX(COUNT(*)) " +
                    "FROM " + EventsTable + " E2 " +
                        "LEFT JOIN " + CitiesTable + " C2 ON E2.event_city_id = C2.city_id " +
                    "GROUP BY State_Name) " +
                "ORDER BY State_Name ASC"
            );
            
            EventStateInfo info = null;
            while (rst.next()) {
                if (rst.isFirst())  {
                    info = new EventStateInfo(rst.getInt(2));
                }
                info.addState(rst.getString(1));
            }
            
            rst.close();
            stmt.close();
            return info;
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }
    
    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {

            Long old_ID = 0L;
            String old_First = "";
            String old_Last = "";
            Long young_ID = 0L;
            String young_First = "";
            String young_Last = "";

            String old_view = (
                "CREATE VIEW User_Friends_Old AS " +
                "SELECT U1.User_ID AS User_ID " + 
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + FriendsTable + " F " + 
                "WHERE U2.User_ID = " + Long.toString(userID) + 
                    "AND U1.Year_of_Birth IS NOT NULL " + 
                    "AND ((U1.User_ID = F.user2_id AND U2.User_ID = F.user1_id) " +
                        "OR (U1.User_ID = F.user1_id AND U2.User_ID = F.user2_id))" + 
                "ORDER BY U1.Year_of_Birth ASC, U1.Month_of_Birth ASC, U1.Day_of_Birth ASC, U1.User_ID DESC"
            );
		 
            stmt.executeUpdate(old_view);
		 	 
            ResultSet rst = stmt.executeQuery(
                "SELECT U.User_ID, U.First_Name, U.Last_Name " + 
                "FROM User_Friends_Old UF, " + UsersTable + " U " + 
                "WHERE U.User_ID = UF.User_ID"
            );

            if (rst.first()) {
                old_ID = rst.getLong(1);
                old_First = rst.getString(2);
                old_Last = rst.getString(3);
            }

            String young_view = (
                "CREATE VIEW User_Friends_Young AS " +
                "SELECT U1.User_ID AS User_ID " + 
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + FriendsTable + " F " + 
                "WHERE U2.User_ID = " + Long.toString(userID) + 
                    "AND U1.Year_of_Birth IS NOT NULL " + 
                    "AND ((U1.User_ID = F.user2_id AND U2.User_ID = F.user1_id) " +
                        "OR (U1.User_ID = F.user1_id AND U2.User_ID = F.user2_id))" + 
                "ORDER BY U1.Year_of_Birth DESC, U1.Month_of_Birth DESC, U1.Day_of_Birth DESC, U1.User_ID DESC"
            );
		 
            stmt.executeUpdate(young_view);

            rst = stmt.executeQuery(
                "SELECT U.User_ID, U.First_Name, U.Last_Name " + 
                "FROM User_Friends_Young UF, " + UsersTable + " U " + 
                "WHERE U.User_ID = UF.User_ID"
            );

            if (rst.first()) {
                young_ID = rst.getLong(1);
                young_First = rst.getString(2);
                young_Last = rst.getString(3);
            }

            stmt.executeUpdate("DROP VIEW User_Friends_Old ");
            stmt.executeUpdate("DROP VIEW User_Friends_Young ");

            // Close resources being used
            rst.close();
            stmt.close();
            return new AgeInfo(new UserInfo(old_ID, old_First, old_Last), new UserInfo(young_ID, young_First, young_Last));
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }
    
    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {

            // Find all pairs of users
            ResultSet rst = stmt.executeQuery(
                "SELECT U1.User_ID, U1.First_Name, U1.Last_Name, U2.User_ID, U2.First_Name, U2.Last_Name " + 
                "FROM " + UsersTable + " U1, " + UsersTable + " U2, " + FriendsTable + " F, " + 
                   HometownCitiesTable + " UH1, " + HometownCitiesTable + " UH2 " + 
                "WHERE U1.User_ID < U2.User_ID AND U1.Last_Name = U2.Last_Name " +  // same last name
                    " AND U1.User_ID = UH1.User_ID AND U2.User_ID = UH2.User_ID AND UH1.hometown_city_id IS NOT NULL AND UH1.hometown_city_id = UH2.hometown_city_id " +    // same hometown
                    " AND ( (U1.User_ID = F.user1_id AND U2.User_ID = F.user2_id) OR (U1.User_ID = F.user2_id AND U2.User_ID = F.user1_id) )" + // are friends
                    " AND U1.Year_of_Birth IS NOT NULL AND U2.Year_of_Birth IS NOT NULL " + 
                    " AND (ABS(U1.Year_of_Birth - U2.Year_of_Birth) < 10) ORDER BY U1.User_ID, U2.User_ID"  // less than 10 birth years apart
            );

            while (rst.next()) {
                Long user1_id = rst.getLong(1);
                String user1_first = rst.getString(2);
                String user1_last = rst.getString(3);
                UserInfo u1 = new UserInfo(user1_id, user1_first, user1_last);

                Long user2_id = rst.getLong(4);
                String user2_first = rst.getString(5);
                String user2_last = rst.getString(6);
                UserInfo u2 = new UserInfo(user2_id, user2_first, user2_last);

                SiblingInfo si = new SiblingInfo(u1, u2);

                results.add(si);
            }
            
            // Close resources being used
            rst.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
