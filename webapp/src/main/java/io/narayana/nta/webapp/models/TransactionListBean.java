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

import io.narayana.nta.Configuration;
import io.narayana.nta.persistence.DataAccessObject;
import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
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

    private int currentPage;
    private int itemsPerPage = Configuration.DEFAULT_ITEMS_PER_PAGE;
    private int totalPage;
    private Integer[] pages;


    public Collection<Transaction> getTransactionsList() {

        filter();
        return transactionsList;
    }

    public void filter() {

        transactionsList = filterByStatus == null
                ? dao.findAllTopLevelTransactions(currentPage * itemsPerPage, itemsPerPage)
                : dao.findAllTopLevelTransactionsWithStatus(filterByStatus, currentPage * itemsPerPage, itemsPerPage);
    }

    public void prevPage() {

        if(currentPage > 0) {
            currentPage -= 1;
        }
    }

    public void nextPage() {

        currentPage += 1;
    }

    public void selectPage(ActionEvent event) {
        currentPage = ((Integer) ((UICommand) event.getComponent()).getValue() - 1);
    }

    public void selectPage(int page) {

        currentPage = page;
    }

    public void setFilterByStatus(Status status) {

        currentPage = 0;
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

    public void setCurrentPage(int page) {

        this.currentPage = page;
    }

    public int getCurrentPage() {

        return currentPage;
    }

    public int getTotalPage() {

        int count;

        if(filterByStatus == null) {
            count = dao.countAllTopLevelTransactions();
        } else {
            count = dao.countAllTopLevelTransactionsWithStatus(filterByStatus);
        }

        if(count == 0) {
            totalPage = 1;
        } else {
            totalPage = count % itemsPerPage == 0 ? count / itemsPerPage : count / itemsPerPage + 1;
        }

        return totalPage;
    }

    public Integer[] getPages() {

        int n = Math.min(Configuration.MAX_PAGE_SIZE, totalPage);
        pages = new Integer[n];

        if(n == totalPage) {
            for(int i = 0; i < n; i++) {
                pages[i] = i + 1;
            }
        } else {
            pages[0] = 1;
            pages[n - 1] = totalPage;
            for(int i = 1; i < n - 1; i++) {
                pages[i] = (currentPage < Configuration.MAX_PAGE_SIZE / 2) ? i + 1 :
                        (currentPage > (totalPage - Configuration.MAX_PAGE_SIZE / 2 - 1)) ?
                                (i + totalPage - Configuration.MAX_PAGE_SIZE + 1)
                                :(i + currentPage - Configuration.MAX_PAGE_SIZE / 2 + 1);
            }
        }
        return pages;
    }

}
