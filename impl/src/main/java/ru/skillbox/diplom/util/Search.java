package ru.skillbox.diplom.util;

import ru.skillbox.diplom.model.Index;
import ru.skillbox.diplom.model.Lemma;
import ru.skillbox.diplom.model.Page;
import ru.skillbox.diplom.repository.LemmaRepository;
import ru.skillbox.diplom.repository.PageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class Search {

    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;

    private final float PAGE_PERCENT = 1;
    private float maxFreq;

    public Search(PageRepository pageRepository, LemmaRepository lemmaRepository) {
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
    }

    public void find(String request){

        maxFreq = pageRepository.count() * PAGE_PERCENT;

        // 1, 2 разбить на слова, отфильровать
        Map<String, Integer> words = Lemmatizator.findLemmas(request);

        // 3 упорядоченный по частоте список
        Map<Lemma, List<Index>> sortedMap = sortByFreq(words.keySet());

        // 4 поиск лемм по страницам
        List<Page> pages = null;
        for (Map.Entry<Lemma, List<Index>> entry: sortedMap.entrySet()) {
            if (pages == null){
                pages = entry.getValue().stream().map(Index::getPage).collect(Collectors.toList());
                continue;
            } else if (pages.isEmpty()){
                break;
            }
            pages = entry.getValue().stream().map(Index::getPage)
                    .filter(pages::contains).collect(Collectors.toList());
        }
        // 5 проверка списка сраниц
        if (pages == null || pages.isEmpty()){
            System.out.println("result not found");
        } else {
            // 6 рассчет релевантности
            List<TableResult> results = new ArrayList<>();
            for (Page page: pages) {
                TableResult table = new TableResult();
                table.setPage(page);
                table.setRequest(request);
                for (List<Index> indexes: sortedMap.values()) {
                    table.addIndex(indexes.stream()
                            .filter(i -> i.getPage().equals(page))
                            .collect(Collectors.toList()));
                }
                results.add(table);
            }
            double maxRelevance = results.stream()
                    .mapToDouble(TableResult::getAbsoluteRelevance)
                    .max().getAsDouble();
            results.forEach(r -> r.calculateRelative(maxRelevance));
            // 7 сортировка по убыванию
            results.sort(Collections.reverseOrder());
            results.forEach(System.out::println);
        }
    }

    private Map<Lemma, List<Index>> sortByFreq(Set<String> words){
        Map<Lemma, List<Index>> map = new TreeMap<>(Comparator.comparingInt(Lemma::getFrequency));
        for (String s: words) {
            Lemma lemma = lemmaRepository.findByLemma(s);
            if(lemma != null){
                List<Index> indexList = lemma.getIndexes();
                if (indexList.size() >= maxFreq){ // 2 исключить часто встречающиеся леммы
                    continue;
                }
                map.put(lemma, indexList);
            }
        }
        return map;
    }
}
