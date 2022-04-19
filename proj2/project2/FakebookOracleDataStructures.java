package project2;

/*
    The UserInfo class stores a subset of the information about Fakebook users;
    specifically, it stores user IDs, first names, and last names. Query 0,
    Query 2, Query 3, Query 4, Query 5, Query 6, Query 8, and Query 9 will use this data
    structure.
*/
final class UserInfo {
    // [Constructor]
    public UserInfo(long id, String fname, String lname) {
        userID = id;
        firstName = fname;
        lastName = lname;
    }
    
    // [UserInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this UserInfo instance
    public String toString() {
        return String.format("%s %s (%d)", firstName, lastName, userID);
    }
    
    // Member Variables
    private long userID;
    private String firstName;
    private String lastName;
}

/*
    The PhotoInfo class stores a subset of the information about Fakebook photos;
    specifically, it stores photo IDs, links, album IDs, and album names (the
    latter two of which refer to the album that contains the photo in question).
    Query 4 and Query 5 will use this data structure.
*/
final class PhotoInfo {
    // [Constructor]
    public PhotoInfo(long pID, long aID, String link, String albName) {
        photoID = pID;
        this.link = link;
        albumID = aID;
        albumName = albName;
    }
    
    // [PhotoInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this PhotoInfo instance
    public String toString() {
        return String.format("(Photo #%d) from Album #%d (%s) at url '%s'",
            photoID, albumID, albumName, link);
    }
    
    // Member Variables
    private long photoID;
    private String link;
    private long albumID;
    private String albumName;
}

/*
    The TaggedPhotoInfo class stores information about users tagged in a particular
    photo; specifically, it stores a PhotoInfo instance identifying the particular
    photo and a list of UserInfo instances identifying the users tagged in that
    photo. Query 4 will use this data structure.
*/
final class TaggedPhotoInfo {
    // [Constructor]
    // REQUIRES: <photo> is not NULL
    public TaggedPhotoInfo(PhotoInfo photo) {
        this.photo = photo;
        usersTagged = new FakebookArrayList<UserInfo>(", ");
    }
    
    // [Add User Function]
    // REQUIERS: <taggedUser> is not NULL
    // MODIFIES: <usersTagged>
    // EFFECTS:  adds <taggedUser> to the list of tagged users
    public void addTaggedUser(UserInfo taggedUser) {
        usersTagged.add(taggedUser);
    }
    
    // [TaggedPhotoInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this TaggedPhotoInfo instance
    public String toString() {
        return String.format("%s%n%d Tags%n%s", photo, usersTagged.size(), usersTagged);
    }
    
    // Member Variables
    private PhotoInfo photo;
    private FakebookArrayList<UserInfo> usersTagged;
}

/*
    The MatchPair class stores information about pairs of users that are tagged in
    at least one common photo; specifically, it stores one UserInfo instance per
    user (plus each user's birth year) and a list of PhotoInfo instances identifying
    the photos in which they are both tagged. Query 5 will use this data structure.
*/
final class MatchPair {
    // [Constructor]
    // REQUIRES: neither <user1> nor <user2> is NULL
    public MatchPair(UserInfo user1, long user1yr, UserInfo user2, long user2yr) {
        this.user1 = user1;
        this.user2 = user2;
        user1Year = user1yr;
        user2Year = user2yr;
        sharedPhotos = new FakebookArrayList<PhotoInfo>(", ");
    }
    
    // [Add Photo Function]
    // REQUIERS: <sharedPhoto> is not NULL
    // MODIFIES: <sharedPhoto>
    // EFFECTS:  adds <sharedPhoto> to the list of shared photos
    public void addSharedPhoto(PhotoInfo sharedPhoto) {
        sharedPhotos.add(sharedPhoto);
    }
    
    // [MatchPair-to-String Converter]
    // EFFECTS:  returns a string representation of this MatchPair instance
    public String toString() {
        return String.format("%s born in %d / %s born in %d%n" +
            "They are not Fakebook friends and are tagged in %d common photo(s)%n%s",
            user1, user1Year, user2, user2Year, sharedPhotos.size(), sharedPhotos);
    }
    
    // Member Variables
    private UserInfo user1;
    private UserInfo user2;
    private long user1Year;
    private long user2Year;
    private FakebookArrayList<PhotoInfo> sharedPhotos;
}

/*
    The UsersPair class stores information about pairs of users that have common
    friends; specifically, it stores one UserInfo instance for each of the two
    users and a list of UserInfo instances identifying the two users' common
    friends. Query 6 will use this data structure.
*/
final class UsersPair {
    // [Constructor]
    // REQUIRES: neither <user1> nor <user2> is NULL
    public UsersPair(UserInfo user1, UserInfo user2) {
        this.user1 = user1;
        this.user2 = user2;
        sharedFriends = new FakebookArrayList<UserInfo>(", ");
    }
    
    // [Add Friend Function]
    // REQUIERS: <sharedFriend> is not NULL
    // MODIFIES: <sharedFriend>
    // EFFECTS:  adds <sharedFriend> to the list of shared photos
    public void addSharedFriend(UserInfo sharedFriend) {
        sharedFriends.add(sharedFriend);
    }
    
    // [UserPair-to-String Converter]
    // EFFECTS:  returns a string representation of this UsersPair instance
    public String toString() {
        return String.format("%s and %s are not Fakebook friends but share %d common friends:%n%s",
            user1, user2, sharedFriends.size(), sharedFriends);
    }
    
    // Member Variables
    private UserInfo user1;
    private UserInfo user2;
    private FakebookArrayList<UserInfo> sharedFriends;
}

/*
    The SiblingInfo class stores information about pairs of users that may be
    siblings; specifically, it stores one UserInfo instance for each of th two
    users. Query 9 will use this data structure.
*/
final class SiblingInfo {
    // [Constructor]
    // REQUIRES: neither <user1> nor <user2> are NULL
    public SiblingInfo(UserInfo user1, UserInfo user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
    
    // [SiblingInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this SiblingInfo instance
    public String toString() {
        return String.format("%s and %s", user1.toString(), user2.toString());
    }
    
    // Member Variables
    private UserInfo user1;
    private UserInfo user2;
}

/*
    The BirthMonthInfo class stores information about users' birth months;
    specifically, it stores the total number of users that have birth month
    information, the month in which the most users were born and who those users
    are, and the month in which the fewest (but at least 1) users were born
    and who those users are. Query 0 will use this data structure.
*/
final class BirthMonthInfo {
    // [Constructor]
    public BirthMonthInfo(long totalUsers, int mostPopularMonth, int leastPopularMonth) {
        usersWithBirthMonth = totalUsers;
        monthOfMostUsers = mostPopularMonth;
        monthOfLeastUsers = leastPopularMonth;
        usersBornInMostPopularMonth = new FakebookArrayList<UserInfo>(", ");
        usersBornInLeastPopularMonth = new FakebookArrayList<UserInfo>(", ");
    }
    
    // [Add User (Most Popular Month) Function]
    // REQUIERS: <user> is not NULL
    // MODIFIES: <user>
    // EFFECTS:  adds <user> to the list of users born in the most popular month
    public void addMostPopularBirthMonthUser(UserInfo user) {
        usersBornInMostPopularMonth.add(user);
    }
    
    // [Add User (Least Popular Month) Function]
    // REQUIERS: <user> is not NULL
    // MODIFIES: <user>
    // EFFECTS:  adds <user> to the list of users born in the least popular month
    public void addLeastPopularBirthMonthUser(UserInfo user) {
        usersBornInLeastPopularMonth.add(user);
    }
    
    // [BirthMonthInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this BirthMonthInfo instance
    public String toString() {
        return String.format("Users with birth month info: %d%n" +
            "The most common birth month is %d (%d users)%nUsers: %s%n" +
            "The least common birth month is %d (%d users)%nUsers: %s",
            usersWithBirthMonth,
			monthOfMostUsers, usersBornInMostPopularMonth.size(), usersBornInMostPopularMonth,
			monthOfLeastUsers, usersBornInLeastPopularMonth.size(), usersBornInLeastPopularMonth);
    }
    
    // Member Variables
    private long usersWithBirthMonth;
    private int monthOfMostUsers;
    private int monthOfLeastUsers;
    private FakebookArrayList<UserInfo> usersBornInMostPopularMonth;
    private FakebookArrayList<UserInfo> usersBornInLeastPopularMonth;
}

/*
    The FirstNameInfo class stors information about users' first names; specifically,
    it stores a list of the first names with the most letters, a list of the first
    names with the fewest letters, and a list of the most commonly-held first names and
    how many users have that first name. Query 1 will use this data structure.
*/
final class FirstNameInfo {
    // [Constructor]
    public FirstNameInfo() {
        commonCount = 0;
        longestFirstNames = new FakebookArrayList<String>(", ");
        shortestFirstNames = new FakebookArrayList<String>(", ");
        mostCommonFirstNames = new FakebookArrayList<String>(", ");
    }
    
    // [Add Long First Name Function]
    public void addLongName(String name) {
        longestFirstNames.add(name);
    }
    
    // [Add Short First Name Function]
    public void addShortName(String name) {
        shortestFirstNames.add(name);
    }
    
    // [Add Common First Name Function]
    public void addCommonName(String name) {
        mostCommonFirstNames.add(name);
    }
    
    // [Set Common First Name Count Function]
    public void setCommonNameCount(long count) {
        commonCount = count;
    }
    
    // [FirstNameInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this FirstNameInfo instance
    public String toString() {
        return String.format("The longest first name(s) is (are) %s%n" +
            "The shortest first name(s) is (are) %s%n" +
            "The most common first name(s) is (are) %s - having %d users having each one",
            longestFirstNames, shortestFirstNames, mostCommonFirstNames, commonCount);
    }
    
    // Member Variables
    private long commonCount;
    private FakebookArrayList<String> longestFirstNames;
    private FakebookArrayList<String> shortestFirstNames;
    private FakebookArrayList<String> mostCommonFirstNames;
}

/*
    The EventStateInfo class stores information about the states in which events are
    held; specifically, it stores a list of the states in which the most events are
    held and the number of events therein held. Query 7 will use this data structure.
*/
final class EventStateInfo {
    // [Constructor]
    public EventStateInfo(long count) {
        eventCount = count;
        popularStates = new FakebookArrayList<String>(", ");
    }
    
    // [Add State Function]
    public void addState(String state) {
        popularStates.add(state);
    }
    
    // [EventStateInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this EventStateInfo instance
    public String toString() {
        return String.format("State(s) with the most events: %s%n" + 
            "Number of events in that (those) state(s): %d",
            popularStates, eventCount);
    }
    
    // Member Variables
    private long eventCount;
    private FakebookArrayList<String> popularStates;
}

/*
    The EventStateInfo class stores the oldest and youngest friend of a particular user,
    though it does not store information about who that particular user is. Query 8 will
    use this data structure.
*/
final class AgeInfo {
    // [Constructor]
    // REQUIRES: neither <oldest> nor <youngest> is NULL
    public AgeInfo(UserInfo oldest, UserInfo youngest) {
        oldestFriend = oldest;
        youngestFriend = youngest;
    }
    
    // [AgeInfo-to-String Converter]
    // EFFECTS:  returns a string representation of this AgeInfo instance
    public String toString() {
        return String.format("Oldest friend: %s%nYoungest friend: %s",
            oldestFriend, youngestFriend);
    }
    
    // Member Variabls
    UserInfo oldestFriend;
    UserInfo youngestFriend;
}
