package main.dao;

import main.model.Index;
import main.model.Lemma;

import java.util.List;

public interface LemmaDao {

    void add(Lemma lemma);
    List<Lemma> getAll();
    Lemma findByLemma(String word);
    void update(Lemma lemma);
    void delete(Lemma lemma);
    List<Index> getIndexes(int id);
}
