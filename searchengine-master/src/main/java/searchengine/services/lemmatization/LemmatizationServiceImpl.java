package searchengine.services.lemmatization;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.*;
import searchengine.repositories.*;
import searchengine.services.indexing.UrlFormatter;

import java.io.IOException;
import java.util.*;

@Service
public class LemmatizationServiceImpl implements LemmatizationService {
    @Autowired
    private final PageDao pageDao;
    @Autowired
    private final SiteDao siteDao;
    @Autowired
    private final LemmaDao lemmaDao;
    @Autowired
    private final IndexDao indexDao;
    @Autowired
    private final SitesList sitesList;
    private final IndexingResponse response;
    private volatile boolean stop;


    public LemmatizationServiceImpl(PageDao pageDao, SiteDao siteDao, LemmaDao lemmaDao, IndexDao indexDao, SitesList sitesList) {
        this.pageDao = pageDao;
        this.siteDao = siteDao;
        this.lemmaDao = lemmaDao;
        this.indexDao = indexDao;
        this.sitesList = sitesList;
        response = new IndexingResponse();
        stop = false;
    }

    public void stopLemmatization() {
        stop = true;
    }
    public IndexingResponse saveLemmasFromPage(String url) {
        stop = false;
        boolean inConfig = false;
        searchengine.config.Site mainSite = new searchengine.config.Site();
        mainSite.setUrl(UrlFormatter.getMainFormattedUrl(url));

        if (mainSite.getUrl() == null) {
            response.setResult(false);
            response.setError("неверный формат ссылки =>" + url);
            return response;
        }
        for (searchengine.config.Site site : sitesList.getSites()) {
            if (mainSite.getUrl().equals(UrlFormatter.getMainFormattedUrl(site.getUrl()))) {
                inConfig = true;
                mainSite.setName(site.getName());
            }
        }
        if (!inConfig) {
            response.setResult(false);
            response.setError("Сайт за пределами индексации");
            return response;
        }

        deleteOldData(UrlFormatter.getFormattedUrl(url));

        Page page = new Page();
        if (pageDao.getPageByUrl(url) == null) {
            try {
                page.setContent(Jsoup.connect(url).get().toString());
                page.setPath(UrlFormatter.getFormattedUrl(url));
                page.setCode(Jsoup.connect(url).execute().statusCode());
            } catch (IOException e) {
                page.setCode(404);
            }
            Site currentSite = siteDao.getSiteByUrl(mainSite.getUrl());
            if (currentSite == null) {
                Site site = new Site();
                site.setUrl(mainSite.getUrl());
                site.setLastError("none");
                site.setStatus(Status.INDEXED);
                site.setStatusTime(new Date());

                site.setName(mainSite.getName());
                siteDao.saveSite(site);
                page.setSite(site);
            } else {
                page.setSite(currentSite);
            }
            pageDao.savePage(page);
        } else {
            page = pageDao.getPageByUrl(url);
        }
        List<Page> pages = new ArrayList<>();
        pages.add(page);
        return saveLemmasFromList(pages);
    }
    public IndexingResponse saveLemmasFromList(List<Page> pages) {
        List<Index> indexList = new ArrayList<>();
        List<Lemma> lemmaList = new ArrayList<>();

        for (Page page : pages) {
            if (stop) {
                response.setResult(false);
                response.setError("Индексация остановлена пользователем");
                stop = false;
                return response;
            }
            Map<String, Integer> lemmas = new Lemmas().getLemmasFromHtml(String.valueOf(page.getContent()));
            Set<String> lemmaSet = lemmas.keySet();

            for (String lemma : lemmaSet) {
                Site siteForLemma = page.getSite();

                Lemma lemmaToSave = lemmaDao.getLemmaByNameAndSite(lemma, siteForLemma);

                for (Lemma lemma1 : lemmaList) {
                    if (lemma1.getLemma().equals(lemma)) {
                        lemmaToSave = lemma1;
                        break;
                    }
                }

                if (lemmaToSave != null) {
                    lemmaToSave.setFrequency(lemmaToSave.getFrequency() + 1);
                } else {
                    lemmaToSave = new Lemma();
                    lemmaToSave.setFrequency(1);
                    lemmaToSave.setLemma(lemma);
                    lemmaToSave.setSite(siteForLemma);
                }
                lemmaList.add(lemmaToSave);

                Index indexToSave = new Index();
                indexToSave.setLemma(lemmaToSave);
                indexToSave.setPage(page);
                indexToSave.setRank((float) (lemmas.get(lemma)));
                indexList.add(indexToSave);
            }
        }

        lemmaDao.saveAllLemmas(lemmaList);
        indexDao.saveAllIndexes(indexList);

        response.setResult(true);
        return response;
    }

    public void deleteOldData(String url) {

        Page pageToDelete = pageDao.getPageByUrl(url);
        if (pageToDelete == null) {
            return;
        }
        List<Index> indexesToDelete = new ArrayList<>();
        List<Lemma> lemmasToDelete = new ArrayList<>();
        List<Lemma> lemmasToUpdate = new ArrayList<>();
        for (Index index : indexDao.getIndexesByPage(pageToDelete)) {
            indexesToDelete.add(index);
            Lemma currentLemma = index.getLemma();
            if (currentLemma.getFrequency() > 1) {
                currentLemma.setFrequency(currentLemma.getFrequency() - 1);
                lemmasToUpdate.add(currentLemma);
            } else {
                lemmasToDelete.add(currentLemma);
            }
        }
        indexDao.deleteAllIndexes(indexesToDelete);
        lemmaDao.deleteAllLemmas(lemmasToDelete);
        lemmaDao.saveAllLemmas(lemmasToUpdate);
        pageDao.deletePage(pageToDelete);
    }
}
