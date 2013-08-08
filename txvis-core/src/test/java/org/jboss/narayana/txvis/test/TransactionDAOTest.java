package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

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
                .addPackages(true, "org.jboss.narayana.txvis.persistence", "org.jboss.narayana.txvis.test.utils",
                        "org.jboss.narayana.txvis.interceptors")
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
