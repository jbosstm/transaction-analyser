package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.plugins.Issue;
import org.jboss.narayana.txvis.plugins.PluginService;

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
