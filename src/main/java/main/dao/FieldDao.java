package main.dao;

import main.model.Field;

import java.util.List;

public interface FieldDao {

    void add(Field field);
    List<Field> getAll();
    void update(Field field);
    void delete(Field field);
}
