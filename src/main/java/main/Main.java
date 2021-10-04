package main;

import lombok.SneakyThrows;
import main.parser.Parser;
import main.util.HibernateUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

        HibernateUtil.getSessionFactory().close();

        System.out.println(System.currentTimeMillis() - start + " ms");
    }

    public static List<String> readFile(String path) throws Exception{
        return  Files.readAllLines(Paths.get(path));
    }
}
