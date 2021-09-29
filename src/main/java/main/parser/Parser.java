package main.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import main.dao.HibernateSessionFactory;
import main.dao.Link;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

@EqualsAndHashCode(callSuper = true)
@Data
public class Parser extends RecursiveAction {

    public static volatile Set<String> filter = new HashSet<>();

    private final static int MIN_TIME = 150;
    private final static int MAX_TIME = 500;
    private final static int DIFF_TIME = MAX_TIME - MIN_TIME;

    private String link;

    public Parser(String link) {
        this.link = link;
    }

    @SneakyThrows
    @Override
    protected void compute() {
        List<String> links = parse(link);
        for (String nestedLink: links) {
            Thread.sleep(getRandomTime());
            Parser task = new Parser(nestedLink);
            task.fork();
            task.join();
        }
    }

    private List<String> parse(String link){
        List<String> links = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect(link);
            Connection.Response response = connection.execute();
            Document doc = connection.get();
            String host = response.url().getHost();

            Link page = new Link();
            page.setPath(response.url().getPath());
            page.setCode(response.statusCode());
            page.setContent(doc.outerHtml());
            savePage(page);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return links;
    }

    private void savePage(Link link){
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(link);
        transaction.commit();
    }

    private long getRandomTime(){
        return (long) (Math.random() * DIFF_TIME) + MIN_TIME;
    }
}
