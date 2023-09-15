package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Page;

import java.util.List;


@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {
    @Query("SELECT i FROM Index i WHERE i.page = :page")
    List<Index> getIndexesByPage(Page page);

    @Query("SELECT i.page FROM Index i WHERE i.lemma.lemma = :lemma")
    List<Page> getPagesByLemma(String lemma);

    @Query("SELECT i.page FROM Index i WHERE i.lemma.lemma = :lemma and i.lemma.site.url = :site")
    List<Page> getPagesByLemmaFromSite(String lemma, String site);
}
