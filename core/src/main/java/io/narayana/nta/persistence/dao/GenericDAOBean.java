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

import io.narayana.nta.interceptors.LoggingInterceptor;

import javax.ejb.NoSuchEntityException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceUnit;
import java.util.Collections;
import java.util.List;

/**
 * This bean provides some common code for DAOs to use. It is inherently not type safe
 * and as such caution should be exercised if calling its methods directly from
 * business logic.
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:57
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class GenericDAOBean implements GenericDAO {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;

    private boolean close_em_at_finally = true;

    /**
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void create(E entity) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        em.persist(entity);

        if (notActive)
            em.getTransaction().commit();
    }

    /**
     * @param entityClass
     * @param primaryKey
     * @param <E>
     * @param <K>
     * @return
     */
    @Override
    public <E, K> E retrieve(Class<E> entityClass, K primaryKey) {

        try {
            close_em_at_finally = false;
            return em.find(entityClass, primaryKey);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param entityClass
     * @param <E>
     * @return
     */
    @Override
    public <E> List<E> retrieveAll(Class<E> entityClass) {

        try {
            close_em_at_finally = false;
            return em.createQuery("FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    /**
     * @param entityClass
     * @param field
     * @param value
     * @param <E>
     * @param <V>
     * @return
     * @throws NonUniqueResultException
     * @throws NoSuchEntityException
     */
    @Override
    public <E, V> E retrieveSingleByField(Class<E> entityClass, String field, V value)
            throws NonUniqueResultException, NoSuchEntityException {

        try {
            close_em_at_finally = false;
            return em.createQuery("FROM " + entityClass.getSimpleName() + " e WHERE e." + field + "=:value", entityClass)
                    .setParameter("value", value).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <E, V> List<E> retrieveMultipleByField(Class<E> entityClass, String field, V value) {

        try {
            close_em_at_finally = false;
            return em.createQuery("FROM " + entityClass.getSimpleName() + " e WHERE e." + field + "=:value", entityClass)
                    .setParameter("value", value).getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    /**
     * @param entity
     * @param <E>
     */
    @Override
    public <E> E update(E entity) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        final E merged = em.merge(entity);

        if (notActive)
            em.getTransaction().commit();

        return merged;
    }

    /**
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void delete(E entity) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        em.remove(em.merge(entity));

        if (notActive)
            em.getTransaction().commit();
    }

    /**
     * @param entityClass
     * @param <E>
     */
    @Override
    public <E> void deleteAll(Class<E> entityClass) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        for (E e : em.createQuery("FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList())
            em.remove(e);

        if (notActive)
            em.getTransaction().commit();
    }

    /**
     *
     */
    @Override
    public void deleteAll() {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        em.createQuery("DELETE FROM Event").executeUpdate();
        em.createQuery("DELETE FROM ParticipantRecord").executeUpdate();
        em.createQuery("DELETE FROM ResourceManager").executeUpdate();
        em.createQuery("DELETE FROM Transaction").executeUpdate();

        if (notActive)
            em.getTransaction().commit();
    }

    @Override
    public <E> E querySingle(Class<E> entityType, String query) {

        try {
            close_em_at_finally = false;
            return em.createQuery(query, entityType).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <E> List<E> queryMultiple(Class<E> entityType, String query) {

        try {
            close_em_at_finally = false;
            return em.createQuery(query, entityType).getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }


    /*
     * This interceptor will populate the bean with an EntityManager before
     * method invocation. This produces cleaner code and allows calling other
     * internal methods without the error prone problem of multiple EntityManagers.
     * This wouldn't be necessary if we were able to let the container manage the
     * transactions, but this isn't possible as we would then end up monitoring
     * our own transactions, resulting in a recursive loop.
     */
    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {

        if (em == null || !em.isOpen())
            this.em = emf.createEntityManager();

        Object o = null;

        try {
            o = ctx.proceed();
        } finally {
            if(close_em_at_finally == true) {
                em.close();
            }
        }
        return o;
    }
}
