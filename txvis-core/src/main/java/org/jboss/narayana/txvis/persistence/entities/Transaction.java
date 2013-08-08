package org.jboss.narayana.txvis.persistence.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 14:09
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Transaction.findNatural",
                query = "FROM Transaction t WHERE t.nodeid=:nodeid AND t.txuid=:txuid"
    ),
    @NamedQuery(name = "Transaction.findTopLevel",
                query = "FROM Transaction t WHERE t.txuid=:txuid AND t.parent IS EMPTY"
    ),
    @NamedQuery(name = "Transaction.findAll",
                query = "FROM Transaction t ORDER BY t.startTime"
    ),
    @NamedQuery(name = "Transaction.findAllWithStatus",
                query = "FROM Transaction t WHERE t.status=:status ORDER BY t.startTime"
    ),
    @NamedQuery(name = "Transaction.findAllTopLevel",
                query = "FROM Transaction t WHERE t.parent IS EMPTY ORDER BY t.startTime"
    ),
    @NamedQuery(name = "Transaction.findAllTopLevelWithStatus",
                query = "FROM Transaction t WHERE t.parent IS EMPTY AND t.status=:status ORDER BY t.startTime"
    ),
})
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txuid;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_FLIGHT;

    private String nodeid;
    private Long startTime;
    private Long endTime;


    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
               fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Event> events = new LinkedList<>();


    /*
     * A join table is used to implement the parent / subordinate relationship
     * rather than having a foreign key field on the subordinate, as it eliminates
     * the need for any kind of database locking while monitoring a distributed
     * transaction across multiple nodes.
     */

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable (
            name = "Transaction_Hierarchy",
            joinColumns = {@JoinColumn(name="Parent_id")},
            inverseJoinColumns = {@JoinColumn(name = "Subordinate_id")}
    )
    @Fetch(value = FetchMode.SUBSELECT) // Hibernate
    private Collection<Transaction> subordinates = new HashSet<>();

    @ManyToOne
    @JoinTable (
            name = "Transaction_Hierarchy",
            joinColumns = {@JoinColumn(name="Subordinate_id", insertable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "Parent_id", insertable = false, updatable = false)}
    )
    private Transaction parent = null;

    // Restrict default constructor to EJB container
    protected Transaction() {}

    /**
     *
     * @param txuid
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Transaction(String txuid) throws NullPointerException, IllegalArgumentException {
        this.txuid = txuid;
    }

    /**
     *
     * @param txuid
     * @param timestamp
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Transaction(String txuid, Timestamp timestamp) throws NullPointerException, IllegalArgumentException {
        this.txuid = txuid;
        setStartTime(timestamp);
        events.add(new Event(this, EventType.BEGIN, nodeid, timestamp));
    }

    public Transaction(String txuid, String nodeid, Timestamp timestamp) {
        this.txuid = txuid;
        this.nodeid = nodeid;
        setStartTime(timestamp);
        events.add(new Event(this, EventType.BEGIN, nodeid, timestamp));
    }

    public Long getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getTxuid() {
        return this.txuid;
    }

    /**
     *
     * @return
     */
    public boolean isTopLevel() {
        return parent == null;
    }



    /**
     *
     * @return
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     *
     * @param status
     * @param timestamp
     */
    public void setStatus(Status status, Timestamp timestamp) {
        this.status = status;

        Event e = null;
        switch (status) {
            case PREPARE:
                e = new Event(this, EventType.PREPARE, nodeid, timestamp);
                break;
            case COMMIT: case ONE_PHASE_COMMIT:
                e = new Event(this, EventType.COMMIT, nodeid, timestamp);
                setEndTime(timestamp);
                break;
            case PHASE_ONE_ABORT: case PHASE_TWO_ABORT:
                e = new Event(this, EventType.ABORT, nodeid, timestamp);
                setEndTime(timestamp);
                break;
        }
        events.add(e);
    }

    public void setStatusWithoutCreatingEvent(Status status, Timestamp timestamp) {
        this.status = status;
    }

    /**
     *
     * @return
     */
    public boolean isDistributed() {
        return parent != null || !subordinates.isEmpty();
    }

    /**
     *
     * @return
     */
    public String getNodeid() {
        return nodeid;
    }

    /**
     *
     * @param nodeId
     */
    public void setNodeid(String nodeId) {
        this.nodeid = nodeId;
    }

    /**
     *
     * @return
     */
    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    private void setStartTime(Timestamp timestamp) {
        this.startTime = timestamp.getTime();
    }

    /**
     *
     * @return
     */
    public Timestamp getEndTime() {
        return (endTime != null) ? new Timestamp(endTime) : null;
    }

    /**
     *
     * @param timestamp
     */
    private void setEndTime(Timestamp timestamp) {
        this.endTime = timestamp.getTime();
    }

    /**
     *
     * @return
     */
    public Collection<ParticipantRecord> getParticipantRecords() {
        return participantRecords;
    }

    /**
     *
     * @param participantRecord
     */
    void addParticipantRecord(ParticipantRecord participantRecord) {
        this.participantRecords.add(participantRecord);
    }

    /**
     *
     * @return
     */
    public Collection<Transaction> getSubordinates() {
        return subordinates;
    }

    /**
     *
     * @param tx
     */
    public void addSubordinate(Transaction tx) {
        this.subordinates.add(tx);
        tx.parent = this;
    }

    /**
     *
     * @return
     */
    public Transaction getParent() {
        return parent;
    }

    /**
     *
     * @param tx
     */
    public void setParent(Transaction tx) {
        this.parent = tx;
        tx.subordinates.add(this);
    }

    /**
     *
     * @return
     */
    public Collection<Event> getEvents() {
        return events;
    }

    /**
     *
     * @return
     */
    public Collection<Event> getEventsInTemporalOrder() {
        //FIXME - Problem with hibernate compatibilty with JPA2.0 OrderBy annotation, this hack will probably
        //FIXME - incur a performance penalty
        Collections.sort((List<Event>) events);
        return events;
    }

    /**
     *
     * @param event
     */
    public void addEvent(Event event) {
        this.events.add(event);
        event.setTransaction(this);
    }

    /**
     *
     * @return
     */
    public long getDuration() {
        return endTime == null ? System.currentTimeMillis() - startTime : endTime - startTime;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction: < tx_uid=`").append(txuid)
                .append("`, nodeid=`").append(nodeid)
                .append("`, parentNodeId=`").append(parent != null ? parent.nodeid : "null")
                .append("`, status=`").append(status)
                .append("`, subordinateNodeIds=`");

        for (Transaction tx : subordinates)
            sb.append(tx.nodeid).append(", ");

        return sb.append("` >").toString();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + txuid.hashCode();
        result = 31 * result + nodeid.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction))
            return false;
        Transaction tx = (Transaction) obj;
        return txuid.equals(tx.txuid) && nodeid.equals(tx.nodeid);
    }
}