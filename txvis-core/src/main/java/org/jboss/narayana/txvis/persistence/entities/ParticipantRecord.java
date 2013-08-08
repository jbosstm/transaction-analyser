package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.persistence.*;
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
    protected ParticipantRecord() {}

    /**
     *
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
     *
     * @return
     */
    public Long getId() {
        return this.id;
    }

    /**
     *
     * @return
     */
    public String getRmuid() {
        return rmuid;
    }

    /**
     *
     * @param branchid
     */
    public void setRmuid(String branchid) {
        this.rmuid = branchid;
    }

    /**
     *
     * @return
     */
    public Transaction getTransaction() {
        return this.transaction;
    }

    /**
     *
     * @return
     */
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    /**
     *
     * @return
     */
    public String getXaException() {
        return xaException;
    }

    /**
     *
     * @param xaException
     */
    public void setXaException(String xaException) {
        this.xaException = xaException;
    }

    /**
     *
     * @return
     */
    public Vote getVote() {
        return this.vote;
    }

    /**
     *
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
     *
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
