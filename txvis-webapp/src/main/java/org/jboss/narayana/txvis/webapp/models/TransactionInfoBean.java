package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.Transaction;

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

    @ManagedProperty(value="#{param.txid}")
    private String txID;

    @ManagedProperty(value="#{param.txuid}")
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
        }
        else if (getTxUID() != null) {
            tx = dao.findTopLevelTransaction(getTxUID());
        }
    }
}
