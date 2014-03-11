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

package io.narayana.nta.persistence.dao;

import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 20/06/2013
 * Time: 13:14
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransactionDAO implements Serializable {

    @EJB
    private GenericDAO dao;

    @PersistenceUnit
    private EntityManagerFactory emf;

    public void create(Transaction tx) throws NullPointerException {

        dao.create(tx);
    }

    public Transaction retrieve(Long primaryKeyId) throws NullPointerException {

        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Transaction.class, primaryKeyId);
        } finally {
            //em.close();
        }
    }

    public Transaction retrieve(String nodeid, String txuid) {

        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Transaction.findNatural", Transaction.class).setParameter("nodeid", nodeid)
                    .setParameter("txuid", txuid).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            //em.close();
        }
    }

    public List<Transaction> retrieveAll() {

        return dao.queryMultiple(Transaction.class, "FROM Transaction t ORDER BY t.startTime");
    }

    public void deleteAll() {

        dao.deleteAll(Transaction.class);
    }

    public List<Transaction> retrieveAllWithStatus(Status status) {

        return dao.queryMultiple(Transaction.class, "FROM " + Transaction.class.getSimpleName() + " e WHERE status='"
                + status + "'");
    }

    public List<Transaction> findAllTopLevel() {

        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Transaction.findAllTopLevel", Transaction.class).getResultList();
        } finally {
            //em.close();
        }
    }

    public List<Transaction> findAllTopLevelWithStatus(Status status) {

        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Transaction.findAllTopLevelWithStatus", Transaction.class)
                    .setParameter("status", status).getResultList();
        } finally {
            //em.close();
        }
    }

}
