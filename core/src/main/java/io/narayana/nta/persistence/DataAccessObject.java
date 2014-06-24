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

package io.narayana.nta.persistence;

import io.narayana.nta.interceptors.LoggingInterceptor;
import io.narayana.nta.persistence.entities.Event;
import io.narayana.nta.persistence.entities.ParticipantRecord;
import io.narayana.nta.persistence.entities.ResourceManager;
import io.narayana.nta.persistence.entities.Transaction;
import io.narayana.nta.persistence.enums.Status;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/07/2013
 * Time: 11:28
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class DataAccessObject implements Serializable {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;

    @PostConstruct
    public void init() {
        em = emf.createEntityManager();
    }

    @PreDestroy
    public void shutdown() {
        em.close();
    }
    /*
     * Methods for retrieving objects of type Transaction
     */


    public Transaction findTransaction(Long id) {

        return em.find(Transaction.class, id);
    }

    public Transaction findTransaction(String nodeid, String txuid) {

        try {
            return em.createNamedQuery("Transaction.findNatural", Transaction.class).setParameter("nodeid", nodeid)
                    .setParameter("txuid", txuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Transaction findTopLevelTransaction(String txuid) {

        try {
            return em.createNamedQuery("Transaction.findTopLevel", Transaction.class).setParameter("txuid", txuid)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public Collection<Transaction> findAllTransactions() {

        return em.createNamedQuery("Transaction.findAll", Transaction.class).getResultList();
    }

    public Collection<Transaction> findAllWithStatus(Status status) {

        return em.createNamedQuery("Transaction.findAllWithStatus", Transaction.class).setParameter("status", status)
                .getResultList();
    }

    public Collection<Transaction> findAllTopLevelTransactions() {

        return em.createNamedQuery("Transaction.findAllTopLevel", Transaction.class).getResultList();
    }

    public Collection<Transaction> findAllTopLevelTransactions(int start, int offset) {

        return em.createNamedQuery("Transaction.findAllTopLevel", Transaction.class).setFirstResult(start)
                .setMaxResults(offset).getResultList();
    }

    public Collection<Transaction> findAllTopLevelTransactionsWithStatus(Status status) {

        return em.createNamedQuery("Transaction.findAllTopLevelWithStatus", Transaction.class).setParameter("status", status)
                .getResultList();
    }

    public Collection<Transaction> findAllTopLevelTransactionsWithStatus(Status status, int start, int offset) {

        return em.createNamedQuery("Transaction.findAllTopLevelWithStatus", Transaction.class).setParameter("status", status)
                .setFirstResult(start).setMaxResults(offset).getResultList();
    }

    public int countAllTopLevelTransactions() {

        return ((Long)em.createNamedQuery("Transaction.countAllTopLevel").getSingleResult()).intValue();
    }

    public int countAllTopLevelTransactionsWithStatus(Status status) {

        return ((Long)em.createNamedQuery("Transaction.countAllTopLevelWithStatus").setParameter("status", status).getSingleResult()).intValue();
    }


    /*
     * Methods for retrieving objects of type ParticipantRecord
     */


    public ParticipantRecord findParticipantRecord(Long id) {

        return em.find(ParticipantRecord.class, id);
    }

    public ParticipantRecord findParticipantRecord(String nodeid, String txuid, String jndiName) {

        try {
            return em.createNamedQuery("ParticipantRecord.findNatural", ParticipantRecord.class).setParameter("nodeid", nodeid)
                    .setParameter("txuid", txuid).setParameter("jndiName", jndiName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Collection<ParticipantRecord> findAllParticipantRecords() {

        return em.createNamedQuery("ParticipantRecord.findAll", ParticipantRecord.class).getResultList();
    }

    public Collection<ParticipantRecord> findAllParticipantRecordsForTransaction(String txuid) {

        return em.createNamedQuery("ParticipantRecord.findAllForTransaction", ParticipantRecord.class)
                .setParameter("txuid", txuid).getResultList();
    }

    public Collection<ParticipantRecord> findAllParticipantRecordsForProduct(String productName) {

        return em.createNamedQuery("ParticipantRecord.findAllForProduct", ParticipantRecord.class)
                .setParameter("productName", productName).getResultList();
    }

    public Collection<ParticipantRecord> findAllParticipantRecordsForTransactionThrowingAnException(String txuid) {

        return null;
    }


    /*
     * Methods for retrieving objects of type ResourceManager
     */


    public ResourceManager findResourceManager(String jndiName) {

        return em.find(ResourceManager.class, jndiName);
    }

    public Collection<ResourceManager> findAllResourceManagers() {

        return em.createNamedQuery("ResourceManager.findAll", ResourceManager.class).getResultList();
    }

    public ResourceManager findResourceManagerByBranchId(String branchId)
    {
        try
        {
            return em.createNamedQuery("ResourceManager.findByBranchId", ResourceManager.class)
                    .setParameter("branchId", branchId).getSingleResult();
        }
        catch(NoResultException nre)
        {
            return null;
        }
    }

    /*
    * Method for retrieving objects of type Events
    */

    public Event findEvent(Long id) {

        return em.find(Event.class, id);
    }

    public Collection<Event> findAllEvents(){

        return em.createNamedQuery("Event.findAll", Event.class).getResultList();
    }

    /*
    @AroundInvoke
    private Object intercept(InvocationContext ctx) throws Exception {

        if (em == null || !em.isOpen())
            this.em = emf.createEntityManager();

        Object o = null;

        try {
            o = ctx.proceed();
        } finally {
            em.close();
        }
        return o;
    }
    */
}
