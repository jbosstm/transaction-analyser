package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
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

import static junit.framework.Assert.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 17:31
 */
@RunWith(Arquillian.class)
public class GenericDAOTest {

    @Deployment
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.narayana.txvis.persistence", "org.jboss.narayana.txvis.test.utils" ,
                        "org.jboss.narayana.txvis.interceptors")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")),
                        "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .setManifest(new StringAsset(ManifestMF));
    }

    UniqueIdGenerator idGen;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @EJB
    GenericDAO dao;

    @Before
    public void setup() throws Exception {
        idGen = new UniqueIdGenerator();
    }

    @Test
    public void createAndRetrieveTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();
        Transaction t = new Transaction(txUID);
        dao.create(t);

        assertNotNull("Entity did not contain an ID after attempting to persist", t.getId());

        assertNotNull("Unable to retrieve persisted Entity", dao.retrieve(Transaction.class, t.getId()));
    }

    @Test
    public void UpdateTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();

        Transaction t = new Transaction(txUID);
        dao.create(t);

        t = dao.retrieve(Transaction.class, t.getId());
        t.setStatus(Status.COMMIT, timestamp);
        dao.update(t);

        t = dao.retrieve(Transaction.class, t.getId());
        assertEquals("Retrieved transaction entity did not report correct status", Status.COMMIT, t.getStatus());
    }

    @Test
    public void retrieveAllTest() throws Exception {

    }

    @Test
    public void deleteTest() throws Exception {

    }

    @Test
    public void deleteAllTest() throws Exception {

    }
}
