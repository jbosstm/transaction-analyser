package io.narayana.nta.restapi.models.Transaction;

import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;

import javax.ws.rs.core.Link;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 10/05/14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class TransactionInfo
{
    private Long id;
    private String txuid;
    private Status status;
    private String nodeid;
    private Long startTime;
    private Long endTime;
    private Collection<Link> participantRecords;
    private Collection<Link> events;
    private Collection<Link> subordinates;
    private Link parentTransaction;

    public TransactionInfo()
    {
        participantRecords = new HashSet<>();
        events = new LinkedList<>();
        subordinates = new HashSet<>();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTxuid()
    {
        return txuid;
    }

    public void setTxuid(String txuid)
    {
        this.txuid = txuid;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public String getNodeid()
    {
        return nodeid;
    }

    public void setNodeid(String nodeid)
    {
        this.nodeid = nodeid;
    }

    public Long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Long startTime)
    {
        this.startTime = startTime;
    }

    public Long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Long endTime)
    {
        this.endTime = endTime;
    }

    public Collection<Link> getParticipantRecords()
    {
        return participantRecords;
    }

    public void setParticipantRecords(Collection<Link> participantRecords)
    {
        this.participantRecords = participantRecords;
    }

    public Collection<Link> getEvents()
    {
        return events;
    }

    public void setEvents(Collection<Link> events)
    {
        this.events = events;
    }

    public Collection<Link> getSubordinates()
    {
        return subordinates;
    }

    public void setSubordinates(Collection<Link> subordinates)
    {
        this.subordinates = subordinates;
    }

    public Link getParent()
    {
        return parentTransaction;
    }

    public void setParent(Link parentTransaction)
    {
        this.parentTransaction = parentTransaction;
    }
}

