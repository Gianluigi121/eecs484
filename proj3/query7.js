//  Find the number of users born in each month using MapReduce

var num_month_mapper = function() {
  // Implement the map function
    emit( this.MOB, {count: 1} );
}

var num_month_reducer = function(key, values) {
  // Implement the reduce function
    var ret = {count: 0};
    for (i = 0; i < values.length; ++i) {
        ret.count += values[i].count;
    }
    return ret;
}

var num_month_finalizer = function(key, reduceVal) {
  // We've implemented a simple forwarding finalize function. This implementation 
  // is naive: it just forwards the reduceVal to the output collection.
  // Feel free to change it if needed.
    return reduceVal.count;
}
