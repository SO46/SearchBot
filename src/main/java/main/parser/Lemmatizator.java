package main.parser;

import lombok.SneakyThrows;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.util.*;

public class Lemmatizator {

    private static final List<String> SERVICE_PARTS = Arrays.asList("ПРЕДЛ", "СОЮЗ", "ЧАСТ", "МЕЖД");

    @SneakyThrows
    public static Map<String, Integer> findLemmas(String text) {
        String[] split = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^а-я]", " ")
                .trim().split("[ ]+");
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        Map<String, Integer> wordMap = new TreeMap<>(Comparator.naturalOrder());
        for (String s: split) {
            if (!checkMorph(luceneMorph.getMorphInfo(s))){
                String normalForm = luceneMorph.getNormalForms(s).get(0);
                int count = wordMap.getOrDefault(normalForm, 0);
                wordMap.put(normalForm, count + 1);
            }
        }
        return wordMap;
    }

    public static void printMap(Map<String, Integer> map){
        for (String s: map.keySet()) {
            System.out.println(s + " - " + map.get(s));
        }
    }

    private static boolean checkMorph(List<String> lines){
        for (String s: lines) {
            if (SERVICE_PARTS.contains(s)){
                return true;
            }
        }
        return false;
    }
}
