package main.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SessionUtil {

    private Session session;
    private Transaction transaction;

    public Session getSession() {
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Session openSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    public Session openTransactionSession() {
        session = openSession();
        transaction = session.getTransaction();
        if (!transaction.isActive()){
            transaction.begin();
        }
        return session;
    }

    public void closeSession() {
        session.close();
    }

    public void closeTransactionSession() {
        transaction.commit();
        closeSession();
    }
}
