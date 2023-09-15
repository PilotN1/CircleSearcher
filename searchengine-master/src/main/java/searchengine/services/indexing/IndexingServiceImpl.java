package searchengine.services.indexing;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.Index;
import searchengine.model.Site;
import searchengine.model.Page;
import searchengine.model.Status;
import searchengine.repositories.*;
import searchengine.services.lemmatization.LemmatizationServiceImpl;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;


@Service
public class IndexingServiceImpl implements IndexingService {
    @Autowired
    private SiteDao siteDao;
    @Autowired
    private PageDao pageDao;
    @Autowired
    private LemmaDao lemmaDao;
    @Autowired
    private IndexDao indexDao;
    @Autowired
    private final SitesList sites;
    private static Logger logger;
    private IndexingPool indexingPool;
    private IndexingResponse response;
    private final ForkJoinPool pool;
    private volatile boolean isStarted;
    private final LemmatizationServiceImpl lemmatizationService;
    private Site generalPage;
    private Thread timer;
    public volatile boolean stop;
    @Value("${indexing-settings.userAgent}")
    private String userAgent;
    @Value("${indexing-settings.referrer}")
    private String referrer;

    public IndexingServiceImpl(SiteDao siteDao, PageDao pageDao, LemmaDao lemmaDao, IndexDao indexDao, SitesList sites) {
        this.siteDao = siteDao;
        this.pageDao = pageDao;
        this.lemmaDao = lemmaDao;
        this.indexDao = indexDao;
        this.sites = sites;
        response = new IndexingResponse();
        isStarted = false;
        pool = new ForkJoinPool();
        logger = LoggerFactory.getLogger(IndexingService.class);
        lemmatizationService = new LemmatizationServiceImpl(pageDao, siteDao, lemmaDao, indexDao, sites);
    }

    @Override
    public IndexingResponse startIndexing() {
        stop = false;
        if (isStarted) {
            response.setResult(true);
            response.setError("Индесация уже запущена");
            return response;
        }
        isStarted = true;

        for (searchengine.config.Site site : sites.getSites()) {

            deleteOldData(site);

            generalPage = new Site();
            timer = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        generalPage.setStatusTime(new Date());
                        siteDao.saveSite(generalPage);
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            try {
                generalPage.setName(site.getName());
                generalPage.setUrl(UrlFormatter.getMainFormattedUrl(site.getUrl()));
                generalPage.setStatus(Status.INDEXING);
                generalPage.setLastError("none");

                if (stop) {
                    return indexingStopped();
                }

                timer.start();

                indexingPool = new IndexingPool(site.getUrl(), userAgent, referrer);
                indexingPool.clearCopyList();
                List<String> pagesList = (pool.invoke(indexingPool));

                Set<Page> pageToSaveList = new HashSet<>();
                for (String page : pagesList) {
                    Page pageToSave = new Page();

                    pageToSave.setPath(UrlFormatter.getFormattedUrl(page));
                    pageToSave.setSite(generalPage);
                    pageToSave.setCode(Jsoup.connect(page).execute().statusCode());
                    pageToSave.setContent(String.valueOf(Jsoup.connect(page).get()));

                    pageToSaveList.add(pageToSave);
                }
                pageDao.saveAllPages(pageToSaveList);

                response = lemmatizationService.saveLemmasFromList(new ArrayList<>(pageToSaveList));

                if (!response.isResult()) {
                    if (response.getError().equals("Индексация остановлена пользователем")) {
                        return indexingStopped();
                    }
                    generalPage.setStatus(Status.FAILED);
                    generalPage.setStatusTime(new Date());
                    generalPage.setLastError("Ошибка лемматизации");
                    timer.interrupt();
                    siteDao.saveSite(generalPage);
                    return response;
                }

                generalPage.setStatus(Status.INDEXED);
                generalPage.setStatusTime(new Date());

            } catch (CancellationException ignored) {
            } catch (Exception exception) {
                exception.printStackTrace();
                generalPage.setStatus(Status.FAILED);
                generalPage.setLastError(exception.getMessage());
                generalPage.setStatusTime(new Date());
                response.setError(generalPage.getLastError());
                logger.error(exception.getMessage());
            }
            timer.interrupt();
            siteDao.saveSite(generalPage);
        }

        isStarted = false;
        response.setResult(Objects.equals(response.getError(), null));
        return response;
    }

    @Override
    public IndexingResponse stopIndexing() {
        if (indexingPool != null) {
            indexingPool.cancel(false);
        }
        isStarted = false;
        stop = true;
        lemmatizationService.stopLemmatization();
        response.setResult(true);
        return response;
    }

    private void deleteOldData(searchengine.config.Site site) {
        Site siteToDelete = siteDao.getSiteByUrl(UrlFormatter.getMainFormattedUrl(site.getUrl()));
        if (siteToDelete != null) {
            List<Index> indexesToDelete = new ArrayList<>();
            for (Page page : pageDao.getPagesBySite(siteToDelete)) {
                indexesToDelete.addAll(page.getSearchIndex());
            }
            indexDao.deleteAllIndexes(indexesToDelete);
            lemmaDao.deleteLemmasBySite(siteToDelete);
            pageDao.deletePagesBySite(siteToDelete);
            siteDao.deleteSite(siteToDelete);
        }
    }

    private IndexingResponse indexingStopped() {
        timer.interrupt();
        generalPage.setStatus(Status.FAILED);
        generalPage.setStatusTime(new Date());
        generalPage.setLastError("Индексация остановлена пользователем");
        response.setError(generalPage.getLastError());
        response.setResult(false);
        siteDao.saveSite(generalPage);
        return response;
    }
}
