package searchengine.services.indexing;

public class UrlFormatter {
    public static String getMainFormattedUrl(String url) {
        if (!url.matches("(http|https)://(www\\.)?.*")) {
            return null;
        }
        if (url.contains("www.")) {
            url = url.replace("www.", "");
        }
        url = "http" + url.substring(url.indexOf("://"));
        if (url.matches("^http://.+/.*")) {
            return url.substring(0, url.indexOf("/", 8) + 1);
        } else if (url.matches("^http://[^/]+")) {
            url = url.concat("/");
            return url;
        }
        return null;
    }
    public static String getFormattedUrl(String url) {
        if (!url.matches("(http|https)://(www\\.)?.*")) {
            return null;
        }
        if (url.contains("www.")) {
            url = url.replace("www.", "");
        }
        url = "http" + url.substring(url.indexOf("://"));
        url = url.concat(url.indexOf("/", 8) == -1 ? "/" : "");
        return url;
    }
}
