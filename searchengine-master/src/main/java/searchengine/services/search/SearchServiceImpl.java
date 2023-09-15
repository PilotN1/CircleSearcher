package searchengine.services.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResult;
import searchengine.model.*;
import searchengine.repositories.IndexDao;
import searchengine.services.indexing.UrlFormatter;
import searchengine.services.lemmatization.Lemmas;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private final IndexDao indexDao;
    private final SearchResult searchResult;
    private SearchData searchData;
    private static String localQuery;
    private List<SearchData> searchDataList;

    public SearchServiceImpl(IndexDao indexDao) {
        this.indexDao = indexDao;
        this.searchResult = new SearchResult();
        this.searchData = new SearchData();
    }

    @Override
    public SearchResult search(String query, String site, int offset, int limit) {

        if (query.isBlank()) {
            searchResult.setCount(0);
            searchResult.setResult(true);
            searchResult.setData(new ArrayList<>());
            return searchResult;
        }

        if (searchDataList != null && Objects.equals(localQuery, query)) {
            List<SearchData> dataList = new ArrayList<>();
            for (int i = offset; i < Math.min(limit, searchDataList.size()); i++) {
                dataList.add(searchDataList.get(i));
            }
            searchResult.setData(dataList);
            searchResult.setCount(searchDataList.size());
            searchResult.setResult(true);

            return searchResult;
        }
        localQuery = query;

        Set<String> lemmaQuery = new Lemmas().getLemmasFromText(query).keySet();
        List<Page> pages = new ArrayList<>();
        for (String lemma : lemmaQuery) {
            List<Page> pages1 = site == null ?
                    indexDao.getPagesByLemma(lemma) :
                    indexDao.getPagesByLemmaFromSite(lemma, UrlFormatter.getMainFormattedUrl(site));
            if (pages.isEmpty()) {
                pages.addAll(pages1);
            } else {
                List<Page> saveList = pages;
                pages.retainAll(pages1);
                if (pages.isEmpty()) {
                    pages = saveList;
                    break;
                }
            }
        }

        searchDataList = new ArrayList<>();

        for (Page page : pages) {
            float relevance = 0;
            float maxRelevance = page.getSearchIndex().stream().max(new RankComparator()).get().getRank();
            for (Index index : page.getSearchIndex()) {
                relevance += index.getRank();
            }
            relevance /= maxRelevance;
            searchData = new SearchData();
            searchData.setRelevance(relevance);
            searchData.setSite(page.getSite().getUrl());
            searchData.setSiteName(page.getSite().getName());
            searchData.setUri(page.getPath().substring(page.getPath().indexOf("/", 8)));
            Document document = Jsoup.parse(page.getContent());
            searchData.setTitle(document.title());
            searchData.setSnippet(getSnippet(document.text(), query));
            searchDataList.add(searchData);
        }
        List<SearchData> dataList = new ArrayList<>();
        for (int i = offset; i < Math.min(limit, searchDataList.size()); i++) {
            dataList.add(searchDataList.get(i));
        }
        searchResult.setData(dataList);
        searchResult.setCount(searchDataList.size());
        searchResult.setResult(true);

        return searchResult;
    }

    private String getSnippet(String text, String query) {
        query = query.toLowerCase();
        String result = text.toLowerCase().replaceAll("[^а-я]", " ").replaceAll(" +", " ");
        if (result.contains(query)) {
            int i = result.indexOf(query);
            result = result.replace(query, "<b>" + query + "</b>");
            return "<p>..." + result.substring(i - (Math.min(i, 175)), (Math.min(i + 175, result.length()))) + "...</p>";
        }
        for (String word : query.split(" ")) {
            result = result.replaceAll(word, "<b>" + word + "</b>");
            int i = result.indexOf(word);
            if (i == -1) {
                continue;
            }
            return "<p>..." + result.substring(i - (Math.min(i, 175)), (Math.min(i + 175, result.length()))) + "...</p>";
        }
        return "..." + result.substring(0, 350) + "...";
    }

}
