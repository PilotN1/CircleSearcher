package searchengine.services.search;

import searchengine.dto.search.SearchResult;

public interface SearchService {
    SearchResult search(String query, String site, int offset, int limit);

}
