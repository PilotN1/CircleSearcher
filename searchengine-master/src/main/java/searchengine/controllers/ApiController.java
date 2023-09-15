package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.search.SearchResult;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexing.IndexingService;
import searchengine.services.lemmatization.LemmatizationService;
import searchengine.services.search.SearchService;
import searchengine.services.statistics.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private final SearchService searchService;
    @Autowired
    private final StatisticsService statisticsService;
    @Autowired
    private final IndexingService indexingService;
    @Autowired
    private final LemmatizationService lemmatizationService;

    public ApiController(SearchService searchService, StatisticsService statisticsService, IndexingService indexingService, LemmatizationService lemmatizationService) {
        this.searchService = searchService;
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
        this.lemmatizationService = lemmatizationService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(String url) {
        return ResponseEntity.ok(lemmatizationService.saveLemmasFromPage(url));
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
        return ResponseEntity.ok(indexingService.startIndexing());
    }
    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopIndexing());
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResult> search(String query, String site, int offset, int limit) {
        return ResponseEntity.ok(searchService.search(query, site, offset, limit));
    }
}
