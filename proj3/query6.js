// query6 : Find the Average friend count per user for users
//
// Return a decimal variable as the average user friend count of all users
// in the users document.

function find_average_friendcount(dbname){
    db = db.getSiblingDB(dbname)
    // TODO: return a decimal number of average friend count
    var sum = 0;
    db.users.aggregate([
                        { $group:
                            {
                                _id: null,
                                count: { $sum: { $size: "$friends" }}
                            }
                        }
    ]).forEach( function(f) { sum += f.count });
    return sum / db.users.find().count();
}
