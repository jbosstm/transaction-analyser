package io.narayana.nta.restapi.services;

import io.narayana.nta.persistence.DataAccessObject;
import io.narayana.nta.persistence.entities.Event;
import io.narayana.nta.persistence.entities.ParticipantRecord;
import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;
import io.narayana.nta.restapi.helpers.LinkBuilder;
import io.narayana.nta.restapi.models.Transaction.TransactionInfo;

import javax.inject.Inject;
import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 13/05/14
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class TransactionServiceImpl implements TransactionService
{
    @Inject
    DataAccessObject dao;

    @Override
    public Collection<TransactionInfo> getTransactions()
    {
        Collection<Transaction> transactions = dao.findAllTopLevelTransactions();

        Collection<TransactionInfo> transactionInfos = processDaoTransasctions(transactions);

        return transactionInfos;
    }

    @Override
    public Collection<TransactionInfo> getTransactions(Status status)
    {
        Collection<Transaction> transactions = dao.findAllTopLevelTransactionsWithStatus(status);

        Collection<TransactionInfo> transactionInfos = processDaoTransasctions(transactions);

        return transactionInfos;
    }

    private Collection<TransactionInfo> processDaoTransasctions(Collection<Transaction> daoTransactions)
    {
        Collection<TransactionInfo> transactionInfos = new ArrayList<>();

        for(Transaction transaction : daoTransactions)
        {
            Collection<Link> participantRecordLinks = new ArrayList<>();
            for(ParticipantRecord participantRecord : transaction.getParticipantRecords())
            {
                participantRecordLinks.add(LinkBuilder.participantRecordLinkBuilder(participantRecord.getId()));
            }

            Collection<Link> eventLinks = new ArrayList<>();
            for(Event event : transaction.getEvents())
            {
                eventLinks.add(LinkBuilder.eventLinkBuilder(event.getId()));
            }

            Collection<Link> subordinateLinks = new ArrayList<>();
            for(Transaction subordinateTransaction : transaction.getSubordinates())
            {
                subordinateLinks.add(LinkBuilder.transactionLinkBuilder(subordinateTransaction.getId()));
            }

            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setId(transaction.getId());
            transactionInfo.setEndTime(transaction.getEndTime().getTime());
            transactionInfo.setNodeid(transaction.getNodeid());
            transactionInfo.setStartTime(transaction.getStartTime().getTime());
            transactionInfo.setTxuid(transaction.getTxuid());
            transactionInfo.setStatus(transaction.getStatus());
            transactionInfo.setEvents(eventLinks);
            transactionInfo.setParticipantRecords(participantRecordLinks);
            transactionInfo.setSubordinates(subordinateLinks);

            if(transaction.getParent() != null)
                transactionInfo.setParent(LinkBuilder.transactionLinkBuilder(transaction.getParent().getId()));

            transactionInfos.add(transactionInfo);
        }

        return transactionInfos;
    }
}
