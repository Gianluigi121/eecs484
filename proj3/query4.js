
// query 4: find user pairs (A,B) that meet the following constraints:
// i) user A is male and user B is female
// ii) their Year_Of_Birth difference is less than year_diff
// iii) user A and B are not friends
// iv) user A and B are from the same hometown city
// The following is the schema for output pairs:
// [
//      [user_id1, user_id2],
//      [user_id1, user_id3],
//      [user_id4, user_id2],
//      ...
//  ]
// user_id is the field from the users collection. Do not use the _id field in users.
  
function suggest_friends(year_diff, dbname) {
    db = db.getSiblingDB(dbname);
    var pairs = [];
    // TODO: implement suggest friends
    db.users.find().forEach( function(user1) {
        db.users.find({ user_id: { $ne: user1.user_id } }).forEach( function(user2) {
            var pair = [user1.user_id, user2.user_id]; 
            if ( !pairInArray(pairs, pair) ) {
                if ( (user1.gender === "male" && user2.gender === "female") || (user2.gender === "male" && user1.gender === "female") ) {
                    if (Math.abs(user1.YOB - user2.YOB) < year_diff) {
                        if ( user2.friends.indexOf(user1.user_id) === -1 && user1.friends.indexOf(user2.user_id) === -1 ) {
                            if (user1.hometown.city === user2.hometown.city) {
                                pairs.push(pair);
                            }
                        }
                    }
                }
            }
        });
    });
    // Return an array of arrays.

    return pairs;
}

function pairInArray(arr, pair) {
    for (var i = 0; i < arr.length; ++i) {
        if ( (arr[i][0] === pair[0] && arr[i][1] === pair[1]) || (arr[i][1] === pair[0] && arr[i][0] === pair[1]) ) {
            return true;
        }
    }

    return false;
}
