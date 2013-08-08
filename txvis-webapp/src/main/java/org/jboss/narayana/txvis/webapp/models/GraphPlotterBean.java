package org.jboss.narayana.txvis.webapp.models;

import com.google.gson.Gson;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/07/2013
 * Time: 22:06
 */
@Named
@RequestScoped
public class GraphPlotterBean implements Serializable {

    @ManagedProperty(value="#{param.txid}")
    private String txID;

    @ManagedProperty(value="#{param.txuid}")
    private String txUID;

    @Inject
    private DataAccessObject dao;

    private Transaction tx;

    private Gson gson = new Gson();

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

    public void init() {
        if (getTxUID() != null)
            tx = dao.findTopLevelTransaction(getTxUID());
        else if (getTxID() != null) {
        tx = dao.findTransaction(Long.parseLong(getTxID()));
        // Ensure we have the root transaction
        if (tx.getParent() != null)
            tx = dao.findTopLevelTransaction(tx.getTxuid());
        }
    }

    public String getJsonGraph() {
        return gson.toJson(Node.getInstance(tx));
    }



    public static class Node implements Serializable {

        private String id;
        private String name;
        private Map<String, String> data = new HashMap<>();
        private List<Node> children = new LinkedList<>();


        private Node() {}

        private Node(String id, String name) {
            this.id = id;
            this.name = name;
        }


        public static Node getInstance(Transaction tx)  {
            Node parent = new Node();
            populate(parent, tx);
            return parent;
        }

        private static void populate(Node parent, Transaction tx) {
            Node current;

            if (tx.getParent() == null)
                current = parent;
            else {
                current = new Node();
                parent.children.add(current);
            }

            current.id = String.valueOf(tx.getId());
            current.name = tx.getNodeid();
            current.data.put("isResource", "false");

            // Populate this transaction's participants
            for (ParticipantRecord rec : tx.getParticipantRecords()) {
                Node participant = new Node(rec.getResourceManager().getJndiName(),
                        rec.getResourceManager().getProductName());

                participant.data.put("isResource", "true");
                participant.data.put("vote", rec.getVote().toString());
                participant.data.put("xaException", rec.getXaException());

                current.children.add(participant);
            }

            // Recursively populate the tree with subordinates.
            for (Transaction subordinate : tx.getSubordinates())
                populate(current, subordinate);
        }
    }
}
