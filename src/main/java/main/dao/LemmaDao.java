package main.dao;

import main.model.Lemma;

import java.util.List;

public interface LemmaDao {

    void add(Lemma lemma);
    List<Lemma> getAll();
    List<Lemma>  findByLemma(String word);
    void update(Lemma lemma);
    void delete(Lemma lemma);
}
