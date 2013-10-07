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

package org.jboss.narayana.txvis.test;

import io.narayana.txprof.persistence.dao.GenericDAO;
import io.narayana.txprof.persistence.dao.TransactionDAO;
import io.narayana.txprof.persistence.entities.Transaction;
import io.narayana.txprof.persistence.enums.Status;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;
import java.sql.Timestamp;

import static junit.framework.Assert.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 20/06/2013
 * Time: 16:03
 */
@RunWith(Arquillian.class)
public class TransactionDAOTest {

    @Deployment
    public static WebArchive createDeployment() {

        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "io.narayana.txprof.persistence", "org.jboss.narayana.txvis.test.utils",
                        "io.narayana.txprof.interceptors")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")),
                        "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .setManifest(new StringAsset(ManifestMF));
    }

    @EJB
    private GenericDAO dao;

    @EJB
    private TransactionDAO transactionDAO;

    private UniqueIdGenerator idGen;

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private String jbossNodeId = "TXVIS";

    @Before
    public void setup() throws Exception {

        idGen = new UniqueIdGenerator();
    }

    @Test
    public void retrieveByNodeIdAndUIDTest() throws Exception {

        final String txUID = idGen.getUniqueTxId();
        Transaction tx = new Transaction(txUID, jbossNodeId, timestamp);
        dao.create(tx);

        assertNotNull("", transactionDAO.retrieve(jbossNodeId, txUID));
        assertNull("", transactionDAO.retrieve(jbossNodeId, idGen.getUniqueTxId()));
        assertNull("", transactionDAO.retrieve("DIFFERENTNODE", txUID));
    }

    @Test
    public void retrieveAllWithStatusTest() throws Exception {

        transactionDAO.deleteAll();

        final String[] txUIDs = new String[4];

        final Transaction[] txs = new Transaction[4];

        for (int i = 0; i < txUIDs.length; i++) {
            txUIDs[i] = idGen.getUniqueTxId();
            txs[i] = new Transaction(txUIDs[i], jbossNodeId, timestamp);
        }

        txs[0].setStatus(Status.COMMIT, timestamp);
        txs[1].setStatus(Status.COMMIT, timestamp);
        txs[2].setStatus(Status.PHASE_TWO_ABORT, timestamp);
        txs[3].setStatus(Status.PHASE_TWO_ABORT, timestamp);

        for (Transaction tx : txs)
            dao.create(tx);

        assertEquals("Incorrect number of Transaction objects with Status.COMMIT", 2,
                transactionDAO.retrieveAllWithStatus(Status.COMMIT).size());
        assertEquals("Incorrect number of Transaction objects with Status.PHASE_TWO_ABORT", 2,
                transactionDAO.retrieveAllWithStatus(Status.PHASE_TWO_ABORT).size());
    }
}
