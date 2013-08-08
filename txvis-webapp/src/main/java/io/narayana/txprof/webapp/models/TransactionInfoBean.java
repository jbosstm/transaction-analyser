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

package io.narayana.txprof.webapp.models;

import io.narayana.txprof.persistence.DataAccessObject;
import io.narayana.txprof.persistence.entities.Transaction;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 21:55
 */
@Named
@RequestScoped
public class TransactionInfoBean implements Serializable {

    @ManagedProperty(value = "#{param.txid}")
    private String txID;

    @ManagedProperty(value = "#{param.txuid}")
    private String txUID;

    @Inject
    private DataAccessObject dao;

    private Transaction tx;

    public String getTxID() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.txID = facesContext.getExternalContext().
                getRequestParameterMap().get("txid");
        return this.txID;
    }

    public String getTxUID() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        this.txUID = facesContext.getExternalContext().
                getRequestParameterMap().get("txuid");
        return this.txUID;
    }

    public void setTxID(String txID) {

        this.txID = txID;
    }

    public void setTxUID(String txUID) {

        this.txUID = txUID;
    }

    public Transaction getTransaction() {

        return tx;
    }

    public List<Transaction> getReverseHierarchy() {

        List<Transaction> result = new LinkedList<>();

        for (Transaction tx = this.tx.getParent(); tx != null; tx = tx.getParent())
            result.add(0, tx);

        return result;
    }

    public void init() {

        if (getTxID() != null) {
            tx = dao.findTransaction(Long.parseLong(getTxID()));
        } else if (getTxUID() != null) {
            tx = dao.findTopLevelTransaction(getTxUID());
        }
    }
}
