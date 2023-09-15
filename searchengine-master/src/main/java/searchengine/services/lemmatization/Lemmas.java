package searchengine.services.lemmatization;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
@Component
public class Lemmas {
    private LuceneMorphology luceneMorph;
    private final List<String> particlesWords;
    private final HashMap<String, Integer> result;
    public Lemmas() {
        result  = new HashMap<>();
        particlesWords = List.of(new String[]{"СОЮЗ", "МЕЖД", "ЧАСТ", "ПРЕДЛ"});
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public HashMap<String, Integer> getLemmasFromHtml(String html) {
        Document document = Jsoup.parse(html);
        String text = document.text();
        return getLemmasFromText(text);
    }
    public HashMap<String, Integer> getLemmasFromText(String text) {
        List<String> words = List.of(text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-я\\s])", " ")
                .trim()
                .split("\\s+"));
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }

            if (luceneMorph.getMorphInfo(word).stream().noneMatch(this::isParticleWord)) {
                addWord(luceneMorph.getNormalForms(word));
            }
        }
        return result;
    }
    private void addWord(List<String> words) {
        for (String word : words) {
            if (result.containsKey(word)) {
                result.put(word, result.get(word) + 1);
            } else {
                result.put(word, 1);
            }
        }
    }
    private boolean isParticleWord(String info) {
        for (String property : particlesWords) {
            if (info.contains(property)) {
                return true;
            }
        }
        return false;
    }
}
