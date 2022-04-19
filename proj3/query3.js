//query3
//create a collection "cities" to store every user that lives in every city
//Each document(city) has following schema:
/*
{
  _id: city
  users:[userids]
}
*/

function cities_table(dbname) {
    db = db.getSiblingDB(dbname);
    // TODO: implemente cities collection here
    db.createCollection("cities");
    db.users.distinct( "current.city" ).forEach( function(city) {
            var user_arr = [];
            db.users.find( { "current.city": city } ).forEach( function (user) {
                if (!(user_arr.includes(user.user_id))) {
                    user_arr.push(user.user_id);
                }
            });

            db.cities.insertOne({
                _id: city,
                users: user_arr
            });
    });

    // Returns nothing. Instead, it creates a collection inside the datbase.

}
