package main.dao;

import main.util.SessionUtil;
import main.model.Index;
import org.hibernate.Session;

import java.util.List;

public class IndexDaoImpl extends SessionUtil implements IndexDao {

    @Override
    public void add(Index index) {
        Session session = openTransactionSession();
        session.save(index);
        closeTransactionSession();
    }

    @Override
    public List<Index> getAll() {
        Session session = openTransactionSession();
        List<Index> indexes = session.createQuery("from Index").list();
        closeTransactionSession();
        return indexes;
    }

    @Override
    public void update(Index index) {
        Session session = openTransactionSession();
        session.update(index);
        closeTransactionSession();
    }

    @Override
    public void delete(Index index) {
        Session session = openTransactionSession();
        session.remove(index);
        closeTransactionSession();
    }
}
