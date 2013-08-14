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
    protected Event() {

    }

    /**
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
     * @return
     */
    public Long getId() {

        return id;
    }

    /**
     * @return
     */
    public Timestamp getTimestamp() {

        return new Timestamp(timestamp);
    }

    /**
     * @param timestamp
     */
    private void setTimestamp(Timestamp timestamp) {

        this.timestamp = timestamp.getTime();
    }

    /**
     * @return
     */
    public EventType getEventType() {

        return eventType;
    }

    /**
     * @return
     */
    public Transaction getTransaction() {

        return transaction;
    }

    /**
     * @param transaction
     */
    public void setTransaction(Transaction transaction) {

        this.transaction = transaction;
    }

    /**
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
