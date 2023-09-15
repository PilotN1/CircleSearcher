package searchengine.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "indexing-settings")
public class SitesList {
    private List<Site> sites;

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

}
