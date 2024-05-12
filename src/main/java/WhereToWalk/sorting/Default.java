package WhereToWalk.sorting;

import WhereToWalk.loc.LocationDistance;
import WhereToWalk.loc.LocationFinder;

import java.util.Comparator;

public class Default implements Comparator<String[]> {
    @Override
    public int compare(String[] s1, String[] s2) {
        // latitude: index = 33
        // latitude: index = 34
        return (int) Math.floor(Double.parseDouble(s1[2])-Double.parseDouble(s2[2]));
    };

    public static void main(String[] args) {
        Comparator<String[]> sorter = new Default();
    }
}
