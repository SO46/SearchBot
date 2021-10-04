package main.dao;

import main.util.SessionUtil;
import main.model.Field;
import org.hibernate.Session;

import java.util.List;

public class FieldDaoImpl extends SessionUtil implements FieldDao {

    @Override
    public void add(Field field) {
        Session session = openTransactionSession();
        session.save(field);
        closeTransactionSession();
    }

    @Override
    public List<Field> getAll() {
        Session session = openTransactionSession();
        List<Field> fields = session.createQuery("from Field").list();
        closeTransactionSession();
        return fields;
    }

    @Override
    public void update(Field field) {
        Session session = openTransactionSession();
        session.update(field);
        closeTransactionSession();
    }

    @Override
    public void delete(Field field) {
        Session session = openTransactionSession();
        session.remove(field);
        closeTransactionSession();
    }
}
