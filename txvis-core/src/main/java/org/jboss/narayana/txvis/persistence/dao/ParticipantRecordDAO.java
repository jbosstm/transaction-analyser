package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.NoResultException;
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
     *
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

            return (ParticipantRecord) em.createQuery("FROM "+ParticipantRecord.class.getSimpleName()+" e " +
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
