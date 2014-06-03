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

package io.narayana.nta.restapi.services;

import io.narayana.nta.persistence.DataAccessObject;
import io.narayana.nta.persistence.entities.Event;
import io.narayana.nta.persistence.entities.ParticipantRecord;
import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;
import io.narayana.nta.restapi.helpers.LinkGenerator;
import io.narayana.nta.restapi.models.Transaction.TransactionInfo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 13/05/14
 * Time: 23:46
 */
public class TransactionServiceImpl implements TransactionService
{
    @Inject
    DataAccessObject dao;

    @Override
    public Collection<TransactionInfo> getTransactions()
    {
        Collection<Transaction> transactions = dao.findAllTopLevelTransactions();

        Collection<TransactionInfo> transactionInfos = processDaoTransactions(transactions);

        return transactionInfos;
    }

    @Override
    public Collection<TransactionInfo> getTransactions(Status status)
    {
        if(status == null)
        {
            throw new IllegalArgumentException("Transaction status cannot be null.");
        }

        Collection<Transaction> transactions = dao.findAllTopLevelTransactionsWithStatus(status);

        Collection<TransactionInfo> transactionInfos = processDaoTransactions(transactions);

        return transactionInfos;
    }

    @Override
    public TransactionInfo getTransaction(Long id)
    {
        if(id == null)
        {
            throw new IllegalArgumentException("Transaction id cannot be null.");
        }

        Transaction transaction = dao.findTransaction(id);

        return processDaoTransaction(transaction);
    }


    private TransactionInfo processDaoTransaction(Transaction daoTransaction)
    {
        if(daoTransaction != null)
        {
            Collection<String> participantRecordLinks = new ArrayList<>();

            for(ParticipantRecord participantRecord : daoTransaction.getParticipantRecords())
            {
                participantRecordLinks.add(LinkGenerator.participantRecordURI(participantRecord.getId()));
            }

            Collection<String> eventLinks = new ArrayList<>();
            for(Event event : daoTransaction.getEvents())
            {
                eventLinks.add(LinkGenerator.eventURI(event.getId()));
            }

            Collection<String> subordinateLinks = new ArrayList<>();
            for(Transaction subordinateTransaction : daoTransaction.getSubordinates())
            {
                subordinateLinks.add(LinkGenerator.transactionURI(subordinateTransaction.getId()));
            }

            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setId(daoTransaction.getId());
            transactionInfo.setEndTime(daoTransaction.getEndTime().getTime());
            transactionInfo.setNodeid(daoTransaction.getNodeid());
            transactionInfo.setStartTime(daoTransaction.getStartTime().getTime());
            transactionInfo.setTxuid(daoTransaction.getTxuid());
            transactionInfo.setStatus(daoTransaction.getStatus());
            transactionInfo.setEvents(eventLinks);
            transactionInfo.setParticipantRecords(participantRecordLinks);
            transactionInfo.setSubordinates(subordinateLinks);

            if(daoTransaction.getParent() != null)
                transactionInfo.setParent(LinkGenerator.transactionURI(daoTransaction.getParent().getId()));

            return transactionInfo;
        }

        return null;
    }

    private Collection<TransactionInfo> processDaoTransactions(Collection<Transaction> daoTransactions)
    {
        Collection<TransactionInfo> transactionInfos = new ArrayList<>();

        for(Transaction transaction : daoTransactions)
        {
            transactionInfos.add(processDaoTransaction(transaction));
        }

        return transactionInfos;
    }
}
