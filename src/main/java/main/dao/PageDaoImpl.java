package main.dao;

import main.util.SessionUtil;
import main.model.Page;
import org.hibernate.Session;

import java.util.List;

public class PageDaoImpl extends SessionUtil implements PageDao {

    @Override
    public void add(Page page) {
        Session session = openTransactionSession();
        session.save(page);
        closeTransactionSession();
    }

    @Override
    public List<Page> getAll() {
        Session session = openTransactionSession();
        List<Page> pages = session.createQuery("from Page").list();
        closeTransactionSession();
        return pages;
    }

    @Override
    public Page findById(int id) {
        Session session = openTransactionSession();
        Page page = (Page) session.createQuery("from Page where id=" + id).getSingleResult();
        closeTransactionSession();
        return page;
    }

    @Override
    public void update(Page page) {
        Session session = openTransactionSession();
        session.update(page);
        closeTransactionSession();
    }

    @Override
    public void delete(Page page) {
        Session session = openTransactionSession();
        session.remove(page);
        closeTransactionSession();
    }

    @Override
    public long countPages() {
        Session session = openTransactionSession();
        long count = (long) session.createQuery("select count(*) from Page").getSingleResult();
        closeTransactionSession();
        return  count;
    }
}
