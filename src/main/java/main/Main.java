package main;

import lombok.SneakyThrows;
import main.search.Search;
import main.util.HibernateUtil;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static final String FILE_SRC = "src/data/links.txt";

    @SneakyThrows
    public static void main(String[] args) {

        long start = System.currentTimeMillis();
//        HibernateUtil.getSessionFactory().close();
//        List<String> links = readFile(FILE_SRC);
//        for (String link: links) {
//            new ForkJoinPool().invoke(new Parser(link));
//        }


        String request = "чехлы, защитные пленки";

        Search.find(request);

        HibernateUtil.getSessionFactory().close();

        System.out.println(System.currentTimeMillis() - start + " ms");
    }

    public static List<String> readFile(String path) throws Exception{
        return  Files.readAllLines(Paths.get(path));
    }
}
