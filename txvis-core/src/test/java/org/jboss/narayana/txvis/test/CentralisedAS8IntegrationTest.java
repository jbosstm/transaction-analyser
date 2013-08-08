package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.LogMonitorBean;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Vote;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.test.utils.TransactionUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 04/05/2013
 * Time: 14:41
 */
@RunWith(Arquillian.class)
public class CentralisedAS8IntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("commons-io:commons-io")
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "CentralisedAS8IntegrationTest.war")
                .addPackages(true, "org.jboss.narayana.txvis")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")),
                        "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .addAsLibraries(libs)
                .setManifest(new StringAsset(ManifestMF));
    }

    private static final int NO_OF_TX = 2;
    private static final int NO_OF_PARTICIPANTS = 3;
    private static final int INTRO_DELAY = 0;
    private static final int OUTRO_DELAY = 1000;
    private static final int EXPECTED_NO_OF_EVENTS = 3 + (2 * NO_OF_PARTICIPANTS);


    @EJB
    private GenericDAO dao;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private LogMonitorBean mon;

    private TransactionUtil txUtil;

    private String nodeid = System.getProperty(Configuration.NODEID_SYS_PROP_NAME);
    
    @Before
    public void setup() throws Exception {
        txUtil = new TransactionUtil();
        dao.deleteAll();
    }

    @Test
    public void parseTransactionTest() throws Exception {
        createAndLogTransactions(Status.COMMIT);
        assertEquals("Incorrect number of transaction parsed", NO_OF_TX,
                transactionDAO.retrieveAll().size());
    }

    @Test
    public void parseEnlistResourceManagerTest() throws Exception {
        createAndLogTransactions(Status.COMMIT);
        for (Transaction tx : transactionDAO.retrieveAll()) {
            assertEquals("Incorrect number of ParticipantRecords parsed for Transaction: "+tx.getTxuid(), NO_OF_PARTICIPANTS,
                    tx.getParticipantRecords().size());
            assertEquals("Incorrect number of Events created for Transaction: "+tx.getTxuid(), EXPECTED_NO_OF_EVENTS,
                    tx.getEvents().size());
        }

    }

    @Test
    public void clientDrivenCommitTest() throws Exception {
        createAndLogTransactions(Status.COMMIT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, transactionDAO.retrieveAll().size());

        for (Transaction tx : transactionDAO.retrieveAll()) {
            assertEquals("Transaction "+tx.getTxuid()+" did not report the correct status", Status.COMMIT,
                    transactionDAO.retrieve(nodeid, tx.getTxuid()).getStatus());

            assertEquals("Incorrect number of Events created for Transaction: "+tx.getTxuid(), EXPECTED_NO_OF_EVENTS,
                    tx.getEvents().size());

            for (ParticipantRecord rec : tx.getParticipantRecords())
                assertEquals("ParticipantRecord did not report the correct vote: Transaction: "+rec.getTransaction().getTxuid()+
                        ", ResourceManager: "+rec.getResourceManager().getJndiName(), Vote.COMMIT, rec.getVote());
        }
    }

    @Test
    public void clientDrivenRollbackTest() throws Exception {
        createAndLogTransactions(Status.PHASE_ONE_ABORT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, transactionDAO.retrieveAll().size());

        for (Transaction tx : transactionDAO.retrieveAll()) {
            assertEquals("Transaction "+tx.getTxuid()+" did not report the correct status", Status.PHASE_ONE_ABORT,
                    transactionDAO.retrieve(nodeid, tx.getTxuid()).getStatus());

            // Expected number of events is four less as the client will initiate a rollback without asking
            // the participants to prepare, therefore the transaction and the participants will not have a prepare event.
            assertEquals("Incorrect number of Events created for Transaction: "+tx.getTxuid(),
                    (EXPECTED_NO_OF_EVENTS - 4), tx.getEvents().size());
        }
    }

    @Test
    public void resourceDrivenRollbackTest() throws Exception {
        createAndLogTransactions(Status.PHASE_TWO_ABORT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, transactionDAO.retrieveAll().size());

        for (Transaction tx : transactionDAO.retrieveAll()) {
            assertEquals("Transaction "+tx.getTxuid()+" did not report the correct status",
                    Status.PHASE_TWO_ABORT, transactionDAO.retrieve(nodeid, tx.getTxuid()).getStatus());

            int abortVotes = 0;
            for (ParticipantRecord rec : tx.getParticipantRecords()) {
                if (rec.getVote().equals(Vote.ABORT))
                    abortVotes++;
            }

            assertEquals("Participants of transaction: "+tx.getTxuid()+" did not report correct number of votes: "
                    + Vote.ABORT, 1, abortVotes);

            assertEquals("Incorrect number of Events created for Transaction: "+tx.getTxuid(),
                    (EXPECTED_NO_OF_EVENTS - 1), tx.getEvents().size());
        }
    }

    private void createAndLogTransactions(Status outcome) throws Exception {
        createAndLogTransactions(INTRO_DELAY, OUTRO_DELAY, NO_OF_TX, NO_OF_PARTICIPANTS, outcome);
    }

    private void createAndLogTransactions(int introSleepDelay, int outroSleepDelay, int noOfTx,
                                          int noOfParticipantsPerTx, Status outcome) throws Exception {
        Thread.sleep(introSleepDelay);
        txUtil.createTx(noOfTx, noOfParticipantsPerTx, outcome);
        Thread.sleep(outroSleepDelay);
    }
}
