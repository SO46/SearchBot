package ru.skillbox.diplom.util;

import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.skillbox.diplom.model.Index;
import ru.skillbox.diplom.model.Lemma;
import ru.skillbox.diplom.model.Page;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class TableResult implements Comparable<TableResult>{

    private final int SNIPPET_LENGTH = 50;
    private Page page;

    private String uri;
    private String title;
    private List<String> snippets = new ArrayList<>();
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
        setResultParam();
    }

    private void setResultParam(){

        Document doc = Jsoup.parse(page.getContent());
        uri = page.getPath();
        title = doc.title();

        List<String> lemmas = indexList.stream().map(Index::getLemma)
                .map(Lemma::getLemma).collect(Collectors.toList());
        String pageText = doc.text();

        String regex = "[а-яА-Я]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pageText);

        while (matcher.find()){
            int textLength = pageText.length();
            String word = matcher.group();
            if (checkWord(word, lemmas)) {
                int start = matcher.start() < SNIPPET_LENGTH ? SNIPPET_LENGTH - matcher.start() :
                        matcher.start() - SNIPPET_LENGTH;
                int end = textLength - matcher.end() <  SNIPPET_LENGTH ?
                        textLength : matcher.end() + SNIPPET_LENGTH;
                String snippet = pageText.substring(start, end)
                        .replaceAll(word, "<b>" + word + "</b>");
                snippets.add(snippet);
            }
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
        return String.format("uri: %s, title: %s, snippet: %s, relevance: %10.2f",
                uri, title, snippets, absoluteRelevance);
    }
}
