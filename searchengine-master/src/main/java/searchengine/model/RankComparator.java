package searchengine.model;

import java.util.Comparator;

public class RankComparator implements Comparator<Index> {
    public int compare(Index o1, Index o2) {
        return o1.getRank().compareTo(o2.getRank());
    }
}