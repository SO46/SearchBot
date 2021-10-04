package main.dao;

import main.util.SessionUtil;
import main.model.Lemma;
import org.hibernate.Session;

import java.util.List;

public class LemmaDaoImpl extends SessionUtil implements LemmaDao {

    @Override
    public void add(Lemma lemma) {
        Session session = openTransactionSession();
        session.save(lemma);
        closeTransactionSession();
    }

    @Override
    public List<Lemma> getAll() {
        Session session = openTransactionSession();
        List<Lemma> lemmas = session.createQuery("from Lemma").list();
        closeTransactionSession();
        return lemmas;
    }

    @Override
    public List<Lemma> findByLemma(String word) {
        Session session = openTransactionSession();
        List<Lemma> lemmas = session.createQuery("from Lemma where lemma='" + word + "'").list();
        closeTransactionSession();
        return lemmas;
    }

    @Override
    public void update(Lemma lemma) {
        Session session = openTransactionSession();
        session.update(lemma);
        closeTransactionSession();
    }

    @Override
    public void delete(Lemma lemma) {
        Session session = openTransactionSession();
        session.remove(lemma);
        closeTransactionSession();
    }
}
