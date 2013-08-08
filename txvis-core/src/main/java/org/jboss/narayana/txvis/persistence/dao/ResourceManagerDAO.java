package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.entities.ResourceManager;

import javax.ejb.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 20/06/2013
 * Time: 14:45
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ResourceManagerDAO implements Serializable{

    @EJB
    private GenericDAO dao;

    @PersistenceUnit
    private EntityManagerFactory emf;

    public void create(ResourceManager rm) throws NullPointerException {
        dao.create(rm);
    }

    public ResourceManager retrieve(String jndiName) throws NullPointerException {
        return dao.retrieve(ResourceManager.class, jndiName);
    }

    public List<ResourceManager> retrieveAll() {
        return dao.retrieveAll(ResourceManager.class);
    }

    public void update(ResourceManager rm) throws NullPointerException {
        dao.update(rm);
    }

    public void delete(ResourceManager rm) throws NullPointerException {
        dao.delete(rm);
    }

    public void deleteAll() {
        dao.deleteAll(ResourceManager.class);
    }
}
