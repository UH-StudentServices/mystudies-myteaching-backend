package fi.helsinki.opintoni.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NameSorting {

    /**
     * Comparator function to compare 2 names (arbitrary number of given names and one surname)
     * based on their surname.
     * */
    public static int compareNames(String name1, String name2) {
        String p1ToBeSorted = convertToSortableName(name1);
        String p2ToBeSorted = convertToSortableName(name2);
        return p1ToBeSorted.compareTo(p2ToBeSorted);
    }

    private  static String convertToSortableName(String name){
        List<String> nameParts = Arrays.asList(name.trim().toLowerCase().split(" "));
        Collections.rotate(nameParts,1);
        return String.join("", nameParts);
    }
}
