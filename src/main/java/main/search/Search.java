package main.search;

import lombok.Data;
import main.dao.IndexDaoImpl;
import main.dao.LemmaDaoImpl;
import main.dao.PageDaoImpl;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.parser.Lemmatizator;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Search {

    private static final float PAGE_PERCENT = 1;
    private static float maxFreq;
    private static PageDaoImpl pageDao = new PageDaoImpl();
    private static LemmaDaoImpl lemmaDao = new LemmaDaoImpl();
    private static IndexDaoImpl indexDao = new IndexDaoImpl();

    public static void find(String request){

        maxFreq = (float) pageDao.countPages() * PAGE_PERCENT;

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

    private static  Map<Lemma, List<Index>> sortByFreq(Set<String> words){
        Map<Lemma, List<Index>> map = new TreeMap<>(Comparator.comparingInt(Lemma::getFrequency));
        for (String s: words) {
            Lemma lemma = lemmaDao.findByLemma(s);
            if(lemma != null){
                List<Index> indexList = lemmaDao.getIndexes(lemma.getId());
                if (indexList.size() >= maxFreq){ // 2 исключить часто встречающиеся леммы
                    continue;
                }
                map.put(lemma, indexList);
            }
        }
        return map;
    }
}
