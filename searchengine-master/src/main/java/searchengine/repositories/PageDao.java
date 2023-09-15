package searchengine.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;


@Component
public class PageDao {
    @Autowired
    private PageRepository pageRepository;
    public void savePage(Page page) {
        pageRepository.save(page);
    }
    public void saveAllPages(Iterable<Page> pages) {
        pageRepository.saveAll(pages);
    }
    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }
    public void deletePagesBySite(Site site) {
        pageRepository.deletePagesBySite(site);
    }
    public void deletePage(Page page) {
        pageRepository.delete(page);
    }
    public int countPages() {
        return Math.toIntExact(pageRepository.count());
    }
    public int countPagesBySite(Site site) {
        return pageRepository.countPagesBySite(site);
    }
    public Page getPageByUrl(String url) {
        return pageRepository.getPageByUrl(url);
    }
    public List<Page> getPagesBySite(Site site) {
        return pageRepository.getPagesBySite(site);
    }
}
