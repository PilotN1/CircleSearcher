package searchengine.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;

@Component
public class LemmaDao {
    @Autowired
    private LemmaRepository lemmaRepository;
    public void saveLemma(Lemma lemma) {
        lemmaRepository.save(lemma);
    }
    public void saveAllLemmas(List<Lemma> lemmas) {
        lemmaRepository.saveAll(lemmas);
    }
    public List<Lemma> getAllLemmas() {
        return lemmaRepository.findAll();
    }
    public void deleteLemma(Lemma lemma) {
        lemmaRepository.delete(lemma);
    }
    public void deleteAllLemmas(List<Lemma> lemmas) {
        lemmaRepository.deleteAll(lemmas);
    }
    public int countLemmas() {
        return (int) lemmaRepository.count();
    }
    public void clear() {
        lemmaRepository.deleteAll();
    }
    public Lemma getLemmaByNameAndSite(String name, Site site) {
        return lemmaRepository.getLemmaByNameAndSite(name, site);
    }
    public int countLemmasBySite(Site site) {
        return lemmaRepository.countLemmasBySite(site);
    }
    public void deleteLemmasBySite(Site site) {
        lemmaRepository.deleteLemmasBySite(site);
    }
}
