package searchengine.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.Site;

import java.util.List;

@Component
public class SiteDao {
    @Autowired
    private SiteRepository siteRepository;
    public void saveSite(Site site) {
        siteRepository.save(site);
    }
    public void saveAllSites(List<Site> sites) {
        siteRepository.saveAll(sites);
//        StringBuilder builder = new StringBuilder();
//        for (Site site : sites) {
//            builder.append(builder.length() == 0 ? "" : ",")
//                    .append(" ('").append(site.getLastError()).append("', '")
//                    .append(site.getName()).append("', '")
//                    .append(site.getStatus()).append("', '")
//                    .append(site.getStatusTime()).append("', '")
//                    .append(site.getUrl()).append("', '")
//                    .append("')");
//        }
//        String values = builder.toString();
    }
    public Site getSiteByUrl(String url) {
        return siteRepository.getSiteByUrl(url);
    }
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }
    public void deleteSite(Site site) {
        siteRepository.delete(site);
    }
    public int countSites() {
        return (int) siteRepository.count();
    }
}
