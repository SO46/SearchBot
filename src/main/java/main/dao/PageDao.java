package main.dao;

import main.model.Page;

import java.util.List;

public interface PageDao {

    void add(Page page);
    List<Page> getAll();
    Page findById(int id);
    void update(Page page);
    void delete(Page page);
}
