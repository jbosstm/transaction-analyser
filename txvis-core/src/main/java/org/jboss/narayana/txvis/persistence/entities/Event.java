package org.jboss.narayana.txvis.persistence.entities;

import org.jboss.narayana.txvis.persistence.enums.EventType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 22:57
 */
@Entity
public class Event implements Serializable, Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Transaction transaction;
    private Long timestamp;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String eventEntity;

    // Restrict default constructor to EJB container
    protected Event() {}

    /**
     *
     * @param transaction
     * @param eventType
     * @param eventEntity
     * @param timestamp
     */
    public Event(Transaction transaction, EventType eventType, String eventEntity, Timestamp timestamp) {
        this.transaction = transaction;
        this.eventType = eventType;
        this.eventEntity = eventEntity;
        setTimestamp(timestamp);
    }

    /**
     *
     * @param eventType
     * @param eventEntity
     * @param timestamp
     */
    public Event(EventType eventType, String eventEntity, Timestamp timestamp) {
        this.eventType = eventType;
        this.eventEntity = eventEntity;
        setTimestamp(timestamp);
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public Timestamp getTimestamp() {
        return new Timestamp(timestamp);
    }

    /**
     *
     * @param timestamp
     */
    private void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp.getTime();
    }

    /**
     *
     * @return
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     *
     * @return
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     *
     * @param transaction
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     *
     * @return
     */
    public String getEventEntity() {
        return eventEntity;
    }
    @Override
    public int compareTo(Object o) {
        // Unchecked cast as implicitly throwing ClassCastException conforms
        // with interface contract.
        Event e = (Event) o;
        final long dif = getTimestamp().getTime() - e.getTimestamp().getTime();
        // Avoids casting from long to int
        return dif == 0 ? 0 : (dif < 0 ? -1 : 1);
    }
}
