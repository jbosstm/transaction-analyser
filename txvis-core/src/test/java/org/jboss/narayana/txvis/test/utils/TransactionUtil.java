package org.jboss.narayana.txvis.test.utils;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;
import java.util.UUID;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 31/05/2013
 * Time: 19:18
 */
public class TransactionUtil {

    private UniqueIdGenerator idGen = new UniqueIdGenerator();

    public void createTx(int noOfTx, int noOfParticipantsPerTx, Status outcome) throws Exception {
        for (int i = 0; i < noOfTx; i++)
            createTx(noOfParticipantsPerTx, outcome);
    }

    public void createTx(int noOfParticipantsPerTx, Status outcome) throws Exception {
        TransactionManager.transactionManager().begin();

        if (outcome.equals(Status.PHASE_TWO_ABORT)) {
            TransactionManager.transactionManager().getTransaction().enlistResource(
                    createDummyResource(false));
            noOfParticipantsPerTx--;
        }

        for (int i = 0; i < noOfParticipantsPerTx; i++)
            TransactionManager.transactionManager().getTransaction().enlistResource(
                    createDummyResource());

        if (outcome.equals(Status.PHASE_ONE_ABORT))
            TransactionManager.transactionManager().rollback();
        else
            try {
                TransactionManager.transactionManager().commit();
            }
            catch (RollbackException e) {}
    }

    public void createSuspendTransaction() throws Exception {
        TransactionManager.transactionManager().begin();
        TransactionManager.transactionManager().getTransaction().enlistResource(new DummyXAResourceWrapper(
                new DummyXAResource(UUID.randomUUID().toString(), true), "java://suspend/resume"));
        Transaction t = TransactionManager.transactionManager().suspend();
        TransactionManager.transactionManager().resume(t);
        t.commit();
    }

    private XAResource createDummyResource() {
        return createDummyResource(true);
    }

    private XAResource createDummyResource(boolean voteCommit) {
        return new DummyXAResourceWrapper(new DummyXAResource(UUID.randomUUID().toString(), voteCommit),
                idGen.getUniqueJndiName());
    }
}
