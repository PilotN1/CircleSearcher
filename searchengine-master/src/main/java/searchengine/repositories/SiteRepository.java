package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;


@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {
    @Query("select s from Site s where s.url = :u")
    Site getSiteByUrl(@Param("u") String url);
}
