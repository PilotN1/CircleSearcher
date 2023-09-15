package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;


@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    @Query("select p from Page p where p.path = :u")
    Page getPageByUrl(@Param("u") String url);
    @Query("select count(p) from Page p where p.site = :site")
    int countPagesBySite(@Param("site") Site site);
    @Modifying
    @Transactional
    @Query("DELETE FROM Page p WHERE p.site = :site")
    void deletePagesBySite(@Param("site") Site site);
    @Query("SELECT p FROM Page p WHERE p.site = :site")
    List<Page> getPagesBySite(@Param("site") Site site);
}
