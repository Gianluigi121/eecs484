// find the oldest friend for each user who has a friend. 
// For simplicity, use only year of birth to determine age, if there is a tie, use the one with smallest user_id
// return a javascript object : key is the user_id and the value is the oldest_friend id
// You may find query 2 and query 3 helpful. You can create selections if you want. Do not modify users collection.
//
//You should return something like this:(order does not matter)
//{user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname){
	db = db.getSiblingDB(dbname);
	var results = {};
	// TODO: implement oldest friends
	// return an javascript object described above
	
	// Find all friends of each user
	var friends_arr = {};
	// Sort by user_id in ascending order
	db.users.aggregate([{ $sort: {user_id : 1} }]).forEach( function(user) {
		// Proceed if users has any friends...
		if (user.friends.length > 0) {
			// Add user's friends into friends_arr...
			if (!(user.user_id in friends_arr)) {
				friends_arr[user.user_id] = user.friends;
			}
			else {
				for (var f = 0; f < user.friends.length; ++f) {
					if (friends_arr[user.user_id].indexOf(user.friends[f]) === -1) 
						friends_arr[user.user_id].push(user.friends[f]);
				}
			}

			// Iterate thru user's friends...
			for (var i = 0; i < user.friends.length; ++i) {

				// If user's friend exists in friends_arr...
				if (user.friends[i] in friends_arr) {
					// Check if user is already logged as a friend of user's friend
					if (friends_arr[user.friends[i]].indexOf(user.user_id) === -1)
						// Add user as a friend of user's friend
						friends_arr[user.friends[i]].push(user.user_id);
				} else {
					// Else create new array for user's friend and add user
					friends_arr[user.friends[i]] = [user.user_id];
				}
			}
		}
	});

	// Find oldest friend of each user that has friends
	for (var i = 0; i < db.users.count(); ++i) {

		// Check if User i has friends
		if (i in friends_arr) {
			var oldest_year = Number.MAX_VALUE;
			var oldest_ID = Number.MAX_VALUE;

			for (var j = 0; j < friends_arr[i].length; ++j) {
				var yob;	// Get YOB of friend
				db.users.find({ user_id: friends_arr[i][j] }).forEach( function(u) { yob = u.YOB; });

				if (yob < oldest_year) {	// If year is smaller, then older
					oldest_year = yob;
					oldest_ID = friends_arr[i][j];
				}
				else if (yob === oldest_year) {				// If year is the same, then check IDs
					if (friends_arr[i][j] < oldest_ID) {		// Ties beaten by smaller ID
						oldest_year = yob;
						oldest_ID = friends_arr[i][j];
					}
				}
			}

			results[i] = oldest_ID;
		}
		else {
			continue;
		}
	}

	return results;
}