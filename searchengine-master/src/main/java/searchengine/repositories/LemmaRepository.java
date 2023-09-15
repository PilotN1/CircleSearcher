package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query("SELECT l FROM Lemma l WHERE l.lemma = :l")
    List<Lemma> getRowsByLemma(@Param("l") String lemma);
    @Query("SELECT l FROM Lemma l WHERE l.lemma = :name and l.site = :site")
    Lemma getLemmaByNameAndSite(@Param("name") String lemma, @Param("site") Site site);
    @Query("SELECT count(l) FROM Lemma l WHERE l.site = :site")
    int countLemmasBySite(@Param("site") Site site);
    @Modifying
    @Transactional
    @Query("DELETE FROM Lemma l WHERE l.site = :site")
    void deleteLemmasBySite(@Param("site") Site site);

}
