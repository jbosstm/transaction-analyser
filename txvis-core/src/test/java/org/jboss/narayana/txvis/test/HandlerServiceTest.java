package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.HandlerService;
import org.jboss.narayana.txvis.persistence.dao.ParticipantRecordDAO;
import org.jboss.narayana.txvis.persistence.dao.ResourceManagerDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Vote;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/06/2013
 * Time: 14:08
 */
@RunWith(Arquillian.class)
public class HandlerServiceTest {

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

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private ParticipantRecordDAO participantRecordDAO;

    @EJB
    private HandlerService service;

    private final UniqueIdGenerator idGen = new UniqueIdGenerator();
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private String jbossNodeId;

    @Before
    public void setup() throws Exception {
        jbossNodeId = System.getProperty(Configuration.NODEID_SYS_PROP_NAME);

        if (jbossNodeId == null)
            fail("JBoss node name is not set, unable to complete test. Start jboss with command line param: " +
                    "-Djboss.node.name=<unique node name>");
    }


    @Test
    public void beginTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();

        service.begin(txuid, timestamp, null);

        final Transaction tx = transactionDAO.retrieve(jbossNodeId, txuid);

        assertNotNull("Transaction not created", tx);
        assertEquals("Incorrect txuid", txuid, tx.getTxuid());
        assertEquals("Incorrect jbossNodeId", jbossNodeId, tx.getNodeid());
        assertEquals("Incorrect startTime", timestamp, tx.getStartTime());
    }

    @Test
    public void prepareTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);
        service.prepare(txuid, timestamp);
        final Transaction tx = transactionDAO.retrieve(jbossNodeId, txuid);

        assertTrue("Could not find event record with type PREPARE", eventExists(tx.getEvents(), EventType.PREPARE));
    }

    @Test
    public void phase2CommitTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);
        service.phase2Commit(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(jbossNodeId, txuid);

        assertEquals("Transaction record shows incorrect status", Status.COMMIT, tx.getStatus());
        assertTrue("Could not find event record with type COMMIT", eventExists(tx.getEvents(), EventType.COMMIT));
    }

    @Test
    public void commitTx1PhaseTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);
        service.onePhaseCommit(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(jbossNodeId, txuid);

        assertEquals("Transaction record shows incorrect status", Status.ONE_PHASE_COMMIT, tx.getStatus());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.COMMIT));
    }

    @Test
    public void topLevelAbortTxTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);
        service.abort(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(jbossNodeId, txuid);

        assertEquals("Transaction record shows incorrect status", Status.PHASE_ONE_ABORT, tx.getStatus());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.ABORT));
    }

    @Test
    public void resourceDrivenAbortTxTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);
        service.phase2Abort(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(jbossNodeId, txuid);

        assertEquals("Transaction record shows incorrect status", Status.PHASE_TWO_ABORT, tx.getStatus());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.ABORT));
    }

    @Test
    public void enlistResourceManagerTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);

        // Test that the service creates a new ResourceManager if it does not already exist
        final String jndiName1 = idGen.getUniqueJndiName();
        service.enlistResourceManagerJTA(txuid, jndiName1, null, null, timestamp);
        final ResourceManager rm1 = resourceManagerDAO.retrieve(jndiName1);
        assertNotNull("ResourceManager not created", rm1);
        assertEquals("ResourceManager contained incorrect Jndi name", jndiName1, rm1.getJndiName());

        // Test that the service functions correctly if the ResourceManager already exists
        final String jndiName2 = idGen.getUniqueJndiName();
        ResourceManager rm2 = new ResourceManager(jndiName2, null, null);
        resourceManagerDAO.create(rm2);
        assertNotNull("ResourceManager not created",resourceManagerDAO.retrieve(jndiName2));
        service.enlistResourceManagerJTA(txuid, jndiName2, null, null, timestamp);
        rm2 = resourceManagerDAO.retrieve(jndiName2);
        assertNotNull("ResourceManager not created", rm2);
        assertEquals("ResourceManager contained incorrect Jndi name", jndiName2, rm2.getJndiName());

        assertEquals("Incorrect number of ParticipantRecords created", 2,
                transactionDAO.retrieve(jbossNodeId, txuid).getParticipantRecords().size());
    }

    @Test
    public void resourcePreparedTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);

        final String jndiName = idGen.getUniqueJndiName();
        service.enlistResourceManagerJTA(txuid, jndiName, null, null, timestamp);
        service.resourcePreparedJTA(txuid, jndiName, timestamp);

        assertEquals("ParticipantRecord contained incorrect vote" , Vote.COMMIT,
                participantRecordDAO.retrieve(txuid, jndiName).getVote());
    }

    @Test
    public void resourceFailedToPrepareTest() throws Exception {
        final String xaException = "XAER_RMERR";

        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);

        final String jndiName = idGen.getUniqueJndiName();
        service.enlistResourceManagerJTA(txuid, jndiName, null, null, timestamp);
        service.resourceFailedToPrepareJTA(txuid, jndiName, xaException, timestamp);

        assertEquals("ParticipantRecord contained incorrect vote" , Vote.ABORT,
                participantRecordDAO.retrieve(txuid, jndiName).getVote());

        assertEquals("ParticipantRecord contained incorrect XAException" , xaException,
                participantRecordDAO.retrieve(txuid, jndiName).getXaException());
    }

    private boolean eventExists(Collection<Event> events, EventType type) {

        boolean exists = false;
        for (Event e : events) {
            if (e.getEventType().equals(type))
                exists = true;
        }
        return exists;
    }
}
