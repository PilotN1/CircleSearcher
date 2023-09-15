package searchengine.services.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.*;
import searchengine.services.indexing.UrlFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private SiteDao siteDao;
    @Autowired
    private PageDao pageDao;
    @Autowired
    private LemmaDao lemmaDao;
    @Autowired
    private SitesList sites;


    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();
        for (Site site : sitesList) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            searchengine.model.Site currentSite = siteDao.getSiteByUrl(UrlFormatter.getMainFormattedUrl(site.getUrl()));
            if (currentSite != null) {
                item.setPages(pageDao.countPagesBySite(currentSite));
                item.setLemmas(lemmaDao.countLemmasBySite(currentSite));
                item.setError(currentSite.getLastError());
                item.setStatus(String.valueOf(currentSite.getStatus()));
                item.setStatusTime(currentSite.getStatusTime().getTime());
            } else {
                item.setPages(0);
                item.setLemmas(0);
                item.setError("none");
                item.setStatus("NOT INDEXED");
                item.setStatusTime(new Date().getTime());
            }
            total.setPages(pageDao.countPages());
            total.setLemmas(lemmaDao.countLemmas());
            detailed.add(item);
        }
        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
