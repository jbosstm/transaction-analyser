/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package io.narayana.nta.test.utils;

import com.arjuna.ats.jta.TransactionManager;
import io.narayana.nta.persistence.enums.Status;

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
            } catch (RollbackException e) {
            }
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
