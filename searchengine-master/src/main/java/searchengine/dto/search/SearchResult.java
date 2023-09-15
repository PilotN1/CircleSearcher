package searchengine.dto.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private boolean result;
    private int count;
    private List<SearchData> data;
}
