/*
 * This file is part of MystudiesMyteaching application.
 *
 * MystudiesMyteaching application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MystudiesMyteaching application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MystudiesMyteaching application.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    private  static String convertToSortableName(String name) {
        List<String> nameParts = Arrays.asList(name.trim().toLowerCase().split(" "));
        Collections.rotate(nameParts,1);
        return String.join("", nameParts);
    }
}
