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

package io.narayana.nta.test;

import io.narayana.nta.Configuration;
import io.narayana.nta.persistence.HandlerService;
import io.narayana.nta.persistence.dao.GenericDAO;
import io.narayana.nta.persistence.dao.ParticipantRecordDAO;
import io.narayana.nta.persistence.dao.ResourceManagerDAO;
import io.narayana.nta.persistence.dao.TransactionDAO;
import io.narayana.nta.persistence.entities.Event;
import io.narayana.nta.persistence.entities.ResourceManager;
import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.EventType;
import io.narayana.nta.persistence.enums.Status;
import io.narayana.nta.persistence.enums.Vote;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import io.narayana.nta.test.utils.UniqueIdGenerator;
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
import java.sql.Timestamp;
import java.util.Collection;

import static org.junit.Assert.*;

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

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("commons-io:commons-io")
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "io.narayana.nta")
                .addPackages(true, "org.jboss.narayana.nta.test")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")),
                        "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/nta-test-ds.xml")), "nta-test-ds.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/filter.properties")), "filter.properties")
                .addAsLibraries(libs)
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
        service.prepare(txuid, timestamp);
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
        service.prepare(txuid, timestamp);
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
        final String branchId1 = idGen.getUniqueBranchId();
        final String jndiName1 = idGen.getUniqueJndiName();
        service.enlistResourceManagerJTA(txuid, branchId1, jndiName1, null, null, null, timestamp, "1");
        final ResourceManager rm1 = resourceManagerDAO.retrieve(branchId1);
        assertNotNull("ResourceManager not created", rm1);
        assertEquals("ResourceManager contained incorrect Jndi name", jndiName1, rm1.getJndiName());

        // Test that the service functions correctly if the ResourceManager already exists
        final String branchId2 = idGen.getUniqueBranchId();
        final String jndiName2 = idGen.getUniqueJndiName();
        ResourceManager rm2 = new ResourceManager(branchId2, jndiName2, null, null, null);
        resourceManagerDAO.create(rm2);
        assertNotNull("ResourceManager not created", resourceManagerDAO.retrieve(branchId2));
        service.enlistResourceManagerJTA(txuid, branchId2, jndiName2, null, null, null, timestamp, "2");
        rm2 = resourceManagerDAO.retrieve(branchId2);
        assertNotNull("ResourceManager not created", rm2);
        assertEquals("ResourceManager contained incorrect Jndi name", jndiName2, rm2.getJndiName());

        assertEquals("Incorrect number of ParticipantRecords created", 2,
                transactionDAO.retrieve(jbossNodeId, txuid).getParticipantRecords().size());
    }

    @Test
    public void resourcePreparedTest() throws Exception {

        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);

        final String branchId = idGen.getUniqueBranchId();
        final String jndiName = idGen.getUniqueJndiName();
        service.enlistResourceManagerJTA(txuid, branchId, jndiName, null, null, null, timestamp, "3");
        service.resourcePreparedJTA(txuid, jndiName, timestamp);

        assertEquals("ParticipantRecord contained incorrect vote", Vote.COMMIT,
                participantRecordDAO.retrieve(txuid, jndiName).getVote());
    }

    @Test
    public void resourceFailedToPrepareTest() throws Exception {

        final String xaException = "XAER_RMERR";

        final String txuid = idGen.getUniqueTxId();
        service.begin(txuid, timestamp, null);

        final String branchId = idGen.getUniqueBranchId();
        final String jndiName = idGen.getUniqueJndiName();
        service.enlistResourceManagerJTA(txuid, branchId, jndiName, null, null, null, timestamp, "4");
        service.resourceFailedToPrepareJTA(txuid, jndiName, xaException, timestamp);

        assertEquals("ParticipantRecord contained incorrect vote", Vote.ABORT,
                participantRecordDAO.retrieve(txuid, jndiName).getVote());

        assertEquals("ParticipantRecord contained incorrect XAException", xaException,
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
