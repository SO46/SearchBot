package main;

import lombok.SneakyThrows;
import main.dao.HibernateSessionFactory;
import main.parser.Parser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static final String FILE_SRC = "src/data/links.txt";

    @SneakyThrows
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        List<String> links = readFile(FILE_SRC);
        for (String link: links) {
            new ForkJoinPool().invoke(new Parser(link));
        }

        HibernateSessionFactory.closeSession();

        System.out.println(System.currentTimeMillis() - start + " ms");
    }

    @SneakyThrows
    public static List<String> readFile(String path){
        return  Files.readAllLines(Paths.get(path));
    }
}
