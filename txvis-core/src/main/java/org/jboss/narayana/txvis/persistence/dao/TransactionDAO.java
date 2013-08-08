package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 20/06/2013
 * Time: 13:14
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransactionDAO implements Serializable{

    @EJB
    private GenericDAO dao;

    @PersistenceUnit
    private EntityManagerFactory emf;

    public void create(Transaction tx) throws NullPointerException {
        dao.create(tx);
    }

    public Transaction retrieve(Long primaryKeyId) throws NullPointerException {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Transaction.class, primaryKeyId);
        }
        finally {
            em.close();
        }
    }

    public Transaction retrieve(String nodeid, String txuid) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Transaction.findNatural", Transaction.class).setParameter("nodeid", nodeid)
                    .setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
        finally {
            em.close();
        }
    }

    public List<Transaction> retrieveAll() {
        return dao.queryMultiple(Transaction.class, "FROM Transaction t ORDER BY t.startTime");
    }

    public void deleteAll() {
        dao.deleteAll(Transaction.class);
    }

    public List<Transaction> retrieveAllWithStatus(Status status) {
        return dao.queryMultiple(Transaction.class, "FROM " + Transaction.class.getSimpleName() + " e WHERE status='"
                + status + "'");
    }

    public List<Transaction> findAllTopLevel() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Transaction.findAllTopLevel", Transaction.class).getResultList();
        }
        finally {
            em.close();
        }
    }

    public List<Transaction> findAllTopLevelWithStatus(Status status) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Transaction.findAllTopLevelWithStatus", Transaction.class)
                    .setParameter("status", status).getResultList();
        }
        finally {
            em.close();
        }
    }

}
