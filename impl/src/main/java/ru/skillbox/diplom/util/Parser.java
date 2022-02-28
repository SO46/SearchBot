package ru.skillbox.diplom.util;

import ru.skillbox.diplom.model.Field;
import ru.skillbox.diplom.model.Index;
import ru.skillbox.diplom.model.Lemma;
import ru.skillbox.diplom.model.Page;
import ru.skillbox.diplom.repository.FieldRepository;
import ru.skillbox.diplom.repository.IndexRepository;
import ru.skillbox.diplom.repository.LemmaRepository;
import ru.skillbox.diplom.repository.PageRepository;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;

public class Parser extends RecursiveAction {

    private final PageRepository pageRepository;
    private final FieldRepository fieldRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);
    private static final Marker INFO_HISTORY_MARKER = MarkerFactory.getMarker("INFO_HISTORY");
    private static final Set<String> filter = new HashSet<>();

    private final String link;

    public Parser(PageRepository pageRepository, FieldRepository fieldRepository, LemmaRepository lemmaRepository, IndexRepository indexRepository, String link) {
        this.pageRepository = pageRepository;
        this.fieldRepository = fieldRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.link = link;
        filter.add(link);
    }

    @SneakyThrows
    @Override
    protected void compute() {
        LOGGER.info(INFO_HISTORY_MARKER, "поток создан " + link);
        List<String> links = parse(link);
        for (String nestedLink: links) {
            Parser task = new Parser(pageRepository, fieldRepository,
                    lemmaRepository, indexRepository, nestedLink);
            task.fork();
            task.join();
        }
        LOGGER.info(INFO_HISTORY_MARKER, "поток закрыт " + link);
    }

    private List<String> parse(String link) throws IOException {
        List<String> links = new ArrayList<>();

        Connection connection = Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("https://www.google.com");
        Connection.Response response = connection.execute();
        Document doc = connection.get();
        String host = response.url().getHost();

        Page page = new Page();
        page.setPath(response.url().getPath());
        page.setCode(response.statusCode());
        page.setContent(doc.outerHtml());
        pageRepository.save(page);
        checkPage(page);

        LOGGER.info(INFO_HISTORY_MARKER, "сохранено в бд " + link);

        Elements elements = doc.select("a[href^=/]");
        elements.stream()
                .map(l -> l.absUrl("href"))
                .filter(l -> !l.contains("jpg") && l.contains(host))
                .forEach(l -> {
                    if (!filter.contains(l)) {
                        links.add(l);
                        filter.add(l);
                    }
                });

        return links;
    }

    private void checkPage(Page page){

        if (page.getCode() != 200) {
            return;
        }

        Document doc = Jsoup.parse(page.getContent());
        List<Field> fields = fieldRepository.findAll();

        Map<String, Map<String, Integer>> fieldsMap = new HashMap<>();

        for (Field field : fields) {
            String name = field.getName();
            Elements elements = doc.getElementsByTag(name);
            String text = elements.text();
            if (!text.isEmpty()) {
                fieldsMap.put(name, Lemmatizator.findLemmas(text));
            }
        }

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String name = field.getName();
            Map<String, Integer> map = fieldsMap.get(name);
            if (map == null){
                continue;
            }
            float rank;
            if (i < fieldsMap.size() - 1){
                Field secondField = fields.get(i+1);
                String secondName = secondField.getName();
                Map<String, Integer> secondMap = fieldsMap.get(secondName);
                for (String word: map.keySet()) {
                    if (secondMap.containsKey(word)){
                        Integer count = secondMap.get(word);
                        rank = field.getWeight()*map.get(word)
                                + secondField.getWeight()*secondMap.get(word);
                        secondMap.remove(word);
                        indexingLemmas(word, map.get(word) + count, page, rank);
                    } else {
                        rank = field.getWeight()*map.get(word);
                        indexingLemmas(word, map.get(word), page, rank);
                    }
                }
            } else {
                for (String word: map.keySet()) {
                    rank = field.getWeight() * map.get(word);
                    indexingLemmas(word, map.get(word), page, rank);
                }
            }
        }
    }

    private void indexingLemmas(String word, Integer freq, Page page, float rank){

        Lemma lemma = lemmaRepository.findByLemma(word);
        if (lemma!=null) {
            lemma.setFrequency(lemma.getFrequency() + freq);
        } else {
            lemma = new Lemma();
            lemma.setLemma(word);
            lemma.setFrequency(freq);
        }
        lemmaRepository.save(lemma);

        Index index = new Index();
        index.setPage(page);
        index.setLemma(lemma);
        index.setRank(rank);
        indexRepository.save(index);
    }

}
