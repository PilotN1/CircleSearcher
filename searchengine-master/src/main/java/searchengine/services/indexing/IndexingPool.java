package searchengine.services.indexing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;

public class IndexingPool extends RecursiveTask<List<String>> {
    private final String url;
    protected static CopyOnWriteArrayList<String> COPY_LIST = new CopyOnWriteArrayList<>();
    private final String userAgent;
    private final String referrer;

    public IndexingPool(String url, String userAgent, String referrer) {
        this.url = url;
        this.userAgent = userAgent;
        this.referrer = referrer;
    }

    @Override
    public List<String> compute() {
        List<String> pagesList = new CopyOnWriteArrayList<>();
        pagesList.add(url);
        List<IndexingPool> taskList = new ArrayList<>();

        try {
            Thread.sleep(150);
            Document doc;
            if (userAgent == null || userAgent.equals("DEFAULT") || referrer == null || referrer.equals("DEFAULT")) {
                doc = Jsoup.connect(url).get();
            } else {
                doc = Jsoup.connect(url).userAgent(userAgent).referrer(referrer).get();
            }
            Elements elements = doc.select("[href]");

            for (Element element : elements) {
                String link = element.absUrl("href");
                String domen = pagesList.get(0).substring(pagesList.get(0).indexOf("://") + 3);
                if ((link.startsWith(url) ||  link.matches("(http|https)://" + domen + ".+")) &&
                        !link.matches(".+(png|pdf|jpg|bmp|img|json|ico|#)$") &&
                        !COPY_LIST.contains(link)) {

                    IndexingPool task = new IndexingPool(link, userAgent, referrer);
                    task.fork();
                    taskList.add(task);
                    COPY_LIST.add(link);
                }
            }
            for (IndexingPool task : taskList) {
                pagesList.addAll(task.join());
            }
        } catch (IOException | InterruptedException interruptedException) {
            return pagesList;
        }
        return pagesList;
    }
    public void clearCopyList() {
        COPY_LIST = new CopyOnWriteArrayList<>();
    }
}




