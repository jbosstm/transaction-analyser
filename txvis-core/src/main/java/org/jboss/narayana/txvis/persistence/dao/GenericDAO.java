package org.jboss.narayana.txvis.persistence.dao;

import javax.ejb.Local;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 07/05/2013
 * Time: 22:35
 */
@Local
public interface GenericDAO extends Serializable {

    <E> void create(E entity);

    <E, K> E retrieve(Class<E> entityClass, K primaryKey);

    @SuppressWarnings("unchecked")
    <E> List<E> retrieveAll(Class<E> entityClass);

    @SuppressWarnings("unchecked")
    <E, V> E retrieveSingleByField(Class<E> entityClass, String field, V value);

    <E> E update(E entity);

    <E> void delete(E entity);

    <E> void deleteAll(Class<E> entityClass);

    void deleteAll();

    <E> E querySingle(Class<E> entityType, String query);

    <E> List<E> queryMultiple(Class<E> entityType, String query);

    <E, V> List<E> retrieveMultipleByField(Class<E> entityClass, String field, V value);
}
