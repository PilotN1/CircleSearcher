package searchengine.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.Index;
import searchengine.model.Page;

import java.util.List;


@Component
public class IndexDao {
    @Autowired
    private IndexRepository indexRepository;

    public void saveIndex(Index index) {
        indexRepository.save(index);
    }

    public void saveAllIndexes(List<Index> indexes) {
        indexRepository.saveAll(indexes);
    }

    public Index getIndexById(int id) {
        return indexRepository.findById(id).orElse(null);
    }

    public List<Index> getAllIndexes() {
        return indexRepository.findAll();
    }

    public void deleteIndexById(int id) {
        indexRepository.deleteById(id);
    }

    public void deleteIndex(Index index) {
        indexRepository.delete(index);
    }

    public void deleteAllIndexes(List<Index> indexes) {
        indexRepository.deleteAll(indexes);
    }

    public int countIndexes() {
        return (int) indexRepository.count();
    }

    public List<Index> getIndexesByPage(Page page) {
        return indexRepository.getIndexesByPage(page);
    }

    public List<Page> getPagesByLemma(String lemma) {
        return indexRepository.getPagesByLemma(lemma);
    }

    public List<Page> getPagesByLemmaFromSite(String lemma, String site) {
        return indexRepository.getPagesByLemmaFromSite(lemma, site);
    }
}
