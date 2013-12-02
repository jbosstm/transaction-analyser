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

import io.narayana.nta.persistence.entities.ParticipantRecord;

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
 * Time: 15:31
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ParticipantRecordDAO implements Serializable {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @EJB
    private GenericDAO dao;

    public void create(ParticipantRecord rec) throws NullPointerException {
        // update used instead of create to ensure that the changes instigated in the
        // Transaction and ResourceManager entities are cascaded by the
        // container (CascadeType.MERGE).
        dao.update(rec);
    }

    public ParticipantRecord retrieve(Long id) {

        return dao.retrieve(ParticipantRecord.class, id);
    }

    /**
     * @param txuid
     * @param rmJndiName
     * @return
     */
    public ParticipantRecord retrieve(String txuid, String rmJndiName) throws NullPointerException {

        if (txuid == null)
            throw new NullPointerException("Method called with null parameter: txuid");
        if (rmJndiName == null)
            throw new NullPointerException("Method called with null parameter: rmJndiName");

        final EntityManager em = emf.createEntityManager();
        try {

            return (ParticipantRecord) em.createQuery("FROM " + ParticipantRecord.class.getSimpleName() + " e " +
                    "WHERE e.transaction.txuid=:txuid AND e.resourceManager.jndiName=:jndiName")
                    .setParameter("txuid", txuid).setParameter("jndiName", rmJndiName).getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public ParticipantRecord retrieveByUID(String rmuid) throws NullPointerException {

        return dao.retrieveSingleByField(ParticipantRecord.class, "rmuid", rmuid);
    }

    public List<ParticipantRecord> retrieveAll() {

        return dao.retrieveAll(ParticipantRecord.class);
    }

    public ParticipantRecord update(ParticipantRecord rec) throws NullPointerException {

        return dao.update(rec);
    }

    public void delete(ParticipantRecord rec) throws NullPointerException {

        dao.delete(rec);
    }

    public void deleteAll() {

        dao.deleteAll(ParticipantRecord.class);
    }
}
