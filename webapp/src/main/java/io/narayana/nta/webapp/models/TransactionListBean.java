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

package io.narayana.nta.webapp.models;

import io.narayana.nta.persistence.DataAccessObject;
import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/05/2013
 * Time: 16:13
 */
@Named
@SessionScoped
public class TransactionListBean implements Serializable {

    @Inject
    private DataAccessObject dao;

    private Collection<Transaction> transactionsList;

    private Status filterByStatus;
    private long filterByDuration;


    public Collection<Transaction> getTransactionsList() {

        filter();
        return transactionsList;
    }

    public void filter() {

        transactionsList = filterByStatus == null
                ? dao.findAllTopLevelTransactions()
                : dao.findAllTopLevelTransactionsWithStatus(filterByStatus);
    }

    public void setFilterByStatus(Status status) {

        filterByStatus = status;
    }

    public Status getFilterByStatus() {

        return filterByStatus;
    }

    public long getFilterByDuration() {

        return filterByDuration;
    }

    public void setFilterByDuration(long filterByDuration) {

        this.filterByDuration = filterByDuration;
    }

    public Status[] getStatuses() {

        return Status.values();
    }
}
