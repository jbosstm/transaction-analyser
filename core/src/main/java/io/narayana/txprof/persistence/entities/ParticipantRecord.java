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

package io.narayana.txprof.persistence.entities;

import io.narayana.txprof.persistence.enums.EventType;
import io.narayana.txprof.persistence.enums.Vote;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:26
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "ParticipantRecord.findNatural",
                query = "FROM ParticipantRecord p WHERE p.transaction.nodeid=:nodeid AND p.transaction.txuid=:txuid " +
                        "AND p.resourceManager.jndiName=:jndiName"
        ),
        @NamedQuery(name = "ParticipantRecord.findByRmuid",
                query = "FROM ParticipantRecord p WHERE p.rmuid=:rmuid"
        ),
        @NamedQuery(name = "ParticipantRecord.findAll",
                query = "FROM ParticipantRecord p"
        ),
        @NamedQuery(name = "ParticipantRecord.findAllForTransaction",
                query = "FROM ParticipantRecord p WHERE p.transaction.txuid=:txuid"
        ),
        @NamedQuery(name = "ParticipantRecord.findAllForProduct",
                query = "FROM ParticipantRecord p WHERE p.resourceManager.productName=:productName"
        ),

        //TODO Remove need for this query, then remove this query
        @NamedQuery(name = "ParticipantRecord.findByTxUIDandJndiName",
                query = "FROM ParticipantRecord e WHERE e.transaction.txuid=:txuid AND " +
                        "e.resourceManager.jndiName=:jndiName"
        ),
})
public class ParticipantRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String rmuid;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Transaction transaction;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ResourceManager resourceManager;

    private String xaException = null;

    @Enumerated(EnumType.STRING)
    private Vote vote = Vote.UNKNOWN;

    private boolean prepareCalled;


    // Restrict default constructor to EJB container
    protected ParticipantRecord() {

    }

    /**
     * @param transaction
     * @param resourceManager
     * @param timestamp
     * @throws NullPointerException
     */
    public ParticipantRecord(Transaction transaction, ResourceManager resourceManager, Timestamp timestamp)
            throws NullPointerException {

        if (transaction == null)
            throw new NullPointerException("Method called with null parameter: transaction");

        if (resourceManager == null)
            throw new NullPointerException("Method called with null parameter: resourceManager");

        this.transaction = transaction;
        this.resourceManager = resourceManager;
        transaction.addParticipantRecord(this);
        resourceManager.addParticipantRecord(this);
        transaction.addEvent(new Event(EventType.ENLIST, resourceManager.getJndiName(), timestamp));
    }

    /**
     * @return
     */
    public Long getId() {

        return this.id;
    }

    /**
     * @return
     */
    public String getRmuid() {

        return rmuid;
    }

    /**
     * @param branchid
     */
    public void setRmuid(String branchid) {

        this.rmuid = branchid;
    }

    /**
     * @return
     */
    public Transaction getTransaction() {

        return this.transaction;
    }

    /**
     * @return
     */
    public ResourceManager getResourceManager() {

        return this.resourceManager;
    }

    /**
     * @return
     */
    public String getXaException() {

        return xaException;
    }

    /**
     * @param xaException
     */
    public void setXaException(String xaException) {

        this.xaException = xaException;
    }

    /**
     * @return
     */
    public Vote getVote() {

        return this.vote;
    }

    /**
     * @param vote
     * @throws NullPointerException
     */
    public void setVote(Vote vote, Timestamp timestamp) {

        this.vote = vote;
    }

    public boolean isPrepareCalled() {

        return prepareCalled;
    }

    public void setPrepareCalled(boolean prepareCalled) {

        this.prepareCalled = prepareCalled;
    }

    /**
     * @return
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb
                .append("ParticipantRecord: < tx_uid=`").append(transaction.getTxuid())
                .append("`, rm_jndiName=`").append(resourceManager.getJndiName())
                .append("`, rm_uid=`").append(rmuid)
                .append("`, vote=`").append(vote)
                .append("`, xaException=`").append(xaException)
                .append("` >");
        return sb.toString();
    }
}
