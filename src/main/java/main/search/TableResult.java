package main.search;

import lombok.Data;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.parser.Lemmatizator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class TableResult implements Comparable<TableResult>{

    private Page page;

    private String uri;
    private String title;
    private String snippet;
    private String request;

    private List<Index> indexList = new ArrayList<>();
    private double absoluteRelevance;
    private double relativeRelevance;

    public void addIndex(List<Index> indexes){
        indexList.addAll(indexes);
        calculateAbsolute();
    }

    private void calculateAbsolute(){
        absoluteRelevance = indexList.stream().mapToDouble(Index::getRank).sum();
    }

    public void calculateRelative(double relevance){
        relativeRelevance = absoluteRelevance / relevance;
    }

    private void setResultParam(){

        Document doc = Jsoup.parse(page.getContent());
        uri = page.getPath();
        title = doc.title();

        List<String> lemmas = indexList.stream().map(Index::getLemma)
                .map(Lemma::getLemma).collect(Collectors.toList());

        snippet = doc.html();

        char[] chars = doc.text().toLowerCase(Locale.ROOT)
                .replaceAll("[^а-я]", " ")
                .trim().toCharArray();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch != ' ') {
                builder.append(ch);
                continue;
            }
            String word = builder.toString();
            if (!word.isEmpty()) {
                if (checkWord(word, lemmas)) {
                    snippet.replaceAll(word, "<b>" + word + "</b>");
                }
            }
            builder.setLength(0);
        }
    }

    private boolean checkWord(String word, List<String> lemmas){
        Map<String, Integer> map = Lemmatizator.findLemmas(word);
        for (String s: map.keySet()) {
            if (lemmas.contains(s)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(TableResult o) {
        return Double.compare(absoluteRelevance, o.absoluteRelevance);
    }

    @Override
    public String toString() {
        setResultParam();
        return String.format("uri: %s, title: %s, snippet: %s, relevance: %10.2f",
                uri, title, snippet, absoluteRelevance);
    }
}
