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

package io.narayana.nta.restapi.models.Transaction;

import io.narayana.nta.persistence.enums.Status;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 10/05/14
 * Time: 12:01
 */
public class TransactionInfo {
    private Long id;
    private String txuid;
    private Status status;
    private String nodeid;
    private Long startTime;
    private Long endTime;
    private Collection<String> participantRecords;
    private Collection<String> events;
    private Collection<String> subordinates;
    private String parentTransaction;

    public TransactionInfo() {
        participantRecords = new ArrayList<>();
        events = new ArrayList<>();
        subordinates = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxuid() {
        return txuid;
    }

    public void setTxuid(String txuid) {
        this.txuid = txuid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Collection<String> getParticipantRecords() {
        return participantRecords;
    }

    public void setParticipantRecords(Collection<String> participantRecords) {
        this.participantRecords = participantRecords;
    }

    public Collection<String> getEvents() {
        return events;
    }

    public void setEvents(Collection<String> events) {
        this.events = events;
    }

    public Collection<String> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(Collection<String> subordinates) {
        this.subordinates = subordinates;
    }

    public String getParent() {
        return parentTransaction;
    }

    public void setParent(String parentTransaction) {
        this.parentTransaction = parentTransaction;
    }
}

