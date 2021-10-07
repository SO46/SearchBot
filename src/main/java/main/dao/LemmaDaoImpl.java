package main.dao;

import main.model.Index;
import main.util.SessionUtil;
import main.model.Lemma;
import org.hibernate.Session;

import javax.persistence.NoResultException;
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
    public Lemma findByLemma(String word) {
        Session session = openTransactionSession();
        Lemma lemma;
        try {
            lemma = (Lemma) session.createQuery("from Lemma where lemma='" + word + "'").getSingleResult();
        } catch (NoResultException e){
            return null;
        }
        closeTransactionSession();
        return lemma;
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

    @Override
    public List<Index> getIndexes(int id) {
        Session session = openTransactionSession();
        List<Index> indexList = session.createQuery("from Index where lemma_id=" + id).list();
        closeTransactionSession();
        return indexList;
    }

}
