package searchengine.services.lemmatization;

import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.Page;

import java.util.List;

public interface LemmatizationService {
    IndexingResponse saveLemmasFromPage(String url);
    IndexingResponse saveLemmasFromList(List<Page> pages);
}
