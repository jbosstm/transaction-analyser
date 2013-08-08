package org.jboss.narayana.txvis.persistence;

import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.*;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/07/2013
 * Time: 11:28
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class DataAccessObject implements Serializable {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;


    /*
     * Methods for retrieving objects of type Transaction
     */


    public Transaction findTransaction(Long id) {
        return em.find(Transaction.class, id);
    }

    public Transaction findTransaction(String nodeid, String txuid) {
        try {
            return em.createNamedQuery("Transaction.findNatural", Transaction.class).setParameter("nodeid", nodeid)
                    .setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException nre) {
            return null;
        }
    }
    public Transaction findTopLevelTransaction(String txuid) {
        try {
            return em.createNamedQuery("Transaction.findTopLevel", Transaction.class).setParameter("txuid", txuid)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public Collection<Transaction> findAllTransactions() {
        return em.createNamedQuery("Transaction.findAll", Transaction.class).getResultList();
    }

    public Collection<Transaction> findAllWithStatus(Status status) {
        return em.createNamedQuery("Transaction.findAllWithStatus", Transaction.class).setParameter("status", status)
                .getResultList();
    }

    public Collection<Transaction> findAllTopLevelTransactions() {
        return em.createNamedQuery("Transaction.findAllTopLevel", Transaction.class).getResultList();
    }

    public Collection<Transaction> findAllTopLevelTransactionsWithStatus(Status status) {
        return em.createNamedQuery("Transaction.findAllTopLevelWithStatus", Transaction.class).setParameter("status", status)
                .getResultList();
    }


    /*
     * Methods for retrieving objects of type ParticipantRecord
     */


    public ParticipantRecord findParticipantRecord(Long id) {
        return em.find(ParticipantRecord.class, id);
    }

    public ParticipantRecord findParticipantRecord(String nodeid, String txuid, String jndiName) {
        try {
            return em.createNamedQuery("ParticipantRecord.findNatural", ParticipantRecord.class).setParameter("nodeid", nodeid)
                    .setParameter("txuid", txuid).setParameter("jndiName", jndiName).getSingleResult();
        }
        catch (NoResultException nre) {
            return null;
        }
    }

    public Collection<ParticipantRecord> findAllParticipantRecords() {
        return em.createNamedQuery("ParticipantRecord.findAll", ParticipantRecord.class).getResultList();
    }

    public Collection<ParticipantRecord> findAllParticipantRecordsForTransaction(String txuid) {
        return em.createNamedQuery("ParticipantRecord.findAllForTransaction", ParticipantRecord.class)
                .setParameter("txuid", txuid).getResultList();
    }

    public Collection<ParticipantRecord> findAllParticipantRecordsForProduct(String productName) {
        return em.createNamedQuery("ParticipantRecord.findAllForProduct", ParticipantRecord.class)
                .setParameter("productName", productName).getResultList();
    }

    public Collection<ParticipantRecord> findAllParticipantRecordsForTransactionThrowingAnException(String txuid) {
        return null;
    }


    /*
     * Methods for retrieving objects of type ResourceManager
     */


    public ResourceManager findResourceManager(String jndiName) {
        return em.find(ResourceManager.class, jndiName);
    }

    public Collection<ResourceManager> findAllResourceManagers() {
        return em.createNamedQuery("ResourceManager.findAll", ResourceManager.class).getResultList();
    }



    @AroundInvoke
    private Object intercept(InvocationContext ctx) throws Exception {
        if (em == null || !em.isOpen())
            this.em = emf.createEntityManager();

        Object o = null;

        try {
            o = ctx.proceed();
        }
        finally {
            em.close();
        }
        return o;
    }
}
