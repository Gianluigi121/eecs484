package project2;

import java.util.ArrayList;

/*
    The FakebookArrayList class is an ArrayList that allows customization of the toString
    function, specifically the inter-item delimiter and the removal of leading/trailing
    brackets.
*/
final class FakebookArrayList<T> extends ArrayList<T> {
    // [Constructor]
    public FakebookArrayList(String delim) {
        super();
        delimiter = delim;
    }
    
    @Override
    // [String Converter Override]
    // EFFECTS:  constructs a string representation of the elements contained in this
    //   FakebookArrayList instance, each one delimited by <delimiter>
    public String toString() {
        String result = "";
        
        int num = size();
        for (int idx = 0; idx < num; idx++) {
            if (idx != 0) {
                result += delimiter;
            }
            result += get(idx);
        }

		if (result.equals("")) {
			return "[]";
		}
	    return result;
    }
    
    // Member Variables
    private String delimiter;
}
