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

import com.google.gson.Gson;
import io.narayana.txprof.persistence.DataAccessObject;
import io.narayana.txprof.persistence.entities.ParticipantRecord;
import io.narayana.txprof.persistence.entities.Transaction;

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

    @ManagedProperty(value = "#{param.txid}")
    private String txID;

    @ManagedProperty(value = "#{param.txuid}")
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


        private Node() {

        }

        private Node(String id, String name) {

            this.id = id;
            this.name = name;
        }


        public static Node getInstance(Transaction tx) {

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
