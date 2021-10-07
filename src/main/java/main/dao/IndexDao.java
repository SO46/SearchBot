package main.dao;

import main.model.Index;

import java.util.List;

public interface IndexDao {

    void add(Index index);
    List<Index> getAll();
    List<Index> findByLemma(int id);
    void update(Index index);
    void delete(Index index);
}
