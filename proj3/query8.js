// query 8: Find the city average friend count per user using MapReduce
// Using the same terminology in query6, we are asking you to write the mapper,
// reducer and finalizer to find the average friend count for each city.


var city_average_friendcount_mapper = function() {
  // implement the Map function of average friend count
    emit( this.hometown.city, {count: this.friends.length, user: 1} );
}

var city_average_friendcount_reducer = function(key, values) {
  // implement the reduce function of average friend count
    var ret = {count: 0, user: 0};
    for (i = 0; i < values.length; ++i) {
        ret.count += values[i].count;
        ret.user += values[i].user;
    }
    return ret;
}

var city_average_friendcount_finalizer = function(key, reduceVal) {
  // We've implemented a simple forwarding finalize function. This implementation 
  // is naive: it just forwards the reduceVal to the output collection.
  // Feel free to change it if needed.
    return reduceVal.count / reduceVal.user;
}
