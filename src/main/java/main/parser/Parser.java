package main.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import main.dao.FieldDaoImpl;
import main.dao.IndexDaoImpl;
import main.dao.LemmaDaoImpl;
import main.dao.PageDaoImpl;
import main.model.Field;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
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

@EqualsAndHashCode(callSuper = true)
@Data
public class Parser extends RecursiveAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    private static final Marker INFO_HISTORY_MARKER = MarkerFactory.getMarker("INFO_HISTORY");

    private static volatile Set<String> filter = new HashSet<>();

    private static volatile PageDaoImpl pageDaoImpl = new PageDaoImpl();
    private static volatile FieldDaoImpl fieldDaoImpl = new FieldDaoImpl();
    private static volatile LemmaDaoImpl lemmaDaoImpl = new LemmaDaoImpl();
    private static volatile IndexDaoImpl indexDaoImpl = new IndexDaoImpl();

    private final static int MIN_TIME = 150;
    private final static int MAX_TIME = 500;
    private final static int DIFF_TIME = MAX_TIME - MIN_TIME;


    private String link;

    public Parser(String link) {
        this.link = link;
        filter.add(link);
    }

    @SneakyThrows
    @Override
    protected void compute() {
        LOGGER.info(INFO_HISTORY_MARKER, "поток создан " + link);
        List<String> links = parse(link);
        for (String nestedLink: links) {
            Thread.sleep(getRandomTime());
            Parser task = new Parser(nestedLink);
            task.fork();
            task.join();
        }
        LOGGER.info(INFO_HISTORY_MARKER, "поток закрыт " + link);
    }

    private List<String> parse(String link) throws IOException {
        List<String> links = new ArrayList<>();

        Connection connection = Jsoup.connect(link);
        Connection.Response response = connection.execute();
        Document doc = connection.get();
        String host = response.url().getHost();

        Page page = new Page();
        page.setPath(response.url().getPath());
        page.setCode(response.statusCode());
        page.setContent(doc.outerHtml());
        pageDaoImpl.add(page);
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
        List<Field> fields = fieldDaoImpl.getAll();

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
                        float firstWeight = secondField.getWeight();
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

        Lemma lemma = lemmaDaoImpl.findByLemma(word);
        if (lemma!=null) {
            lemma.setFrequency(lemma.getFrequency() + freq);
            lemmaDaoImpl.update(lemma);
        } else {
            lemma = new Lemma();
            lemma.setLemma(word);
            lemma.setFrequency(freq);
            lemmaDaoImpl.add(lemma);
        }

        Index index = new Index();
        index.setPage(page);
        index.setLemma(lemma);
        index.setRank(rank);

        indexDaoImpl.add(index);
    }

    private long getRandomTime(){
        return (long) (Math.random() * DIFF_TIME) + MIN_TIME;
    }
}
