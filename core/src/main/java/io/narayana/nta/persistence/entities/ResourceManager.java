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

package io.narayana.nta.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 22:29
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "ResourceManager.findAll",
                query = "FROM ResourceManager r"
        ),
        @NamedQuery(name = "ResourceManager.findByBranchId",
                query = "FROM ResourceManager r WHERE r.branchId=:branchId"
        )
})
public class ResourceManager implements Serializable {

    @OneToMany(mappedBy = "resourceManager", fetch = FetchType.EAGER)
    private Collection<ParticipantRecord> participantRecords = new HashSet<>();

    @Id
    private String branchId;
    private String jndiName;
    private String productName;
    private String productVersion;
    private String eisName;

    // Restrict default constructor to EJB container
    protected ResourceManager() {

    }

    /**
     * @param jndiName
     * @param productName
     * @param productVersion
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public ResourceManager(String branchId, String jndiName, String productName, String productVersion, String eisName)
            throws IllegalArgumentException, NullPointerException {

        if (branchId.trim().isEmpty())
            throw new IllegalArgumentException("Method called with empty parameter: branchId");
        this.branchId = branchId;

        this.jndiName = jndiName != null ? jndiName : "Unknown";
        this.productName = productName != null ? productName : "Unknown";
        this.productVersion = productVersion != null ? productVersion : "Unknown";
        this.eisName = eisName != null ? eisName : "Unknown";
    }

    /**
     * @return
     */
    public String getId() {

        return getBranchId();
    }

    /**
     * @return
     */
    public String getBranchId() {

        return branchId;
    }

    /**
     * @return
     */
    public String getJndiName() {

        return jndiName;
    }

    /**
     * @return
     */
    public String getProductVersion() {

        return productVersion;
    }

    /**
     * @param productVersion
     */
    public void setProductVersion(String productVersion) {

        this.productVersion = productVersion;
    }

    /**
     * @return
     */
    public String getProductName() {

        return productName;
    }

    /**
     * @param productName
     */
    public void setProductName(String productName) {

        this.productName = productName;
    }

    /**
     * @return
     */
    public String getEisName() {

        return eisName;
    }

    /**
     * @param rec
     */
    void addParticipantRecord(ParticipantRecord rec) {

        if (rec == null)
            throw new NullPointerException("Method called with null parameter: rec");
        participantRecords.add(rec);
    }

    /**
     * @return
     */
    public Collection<ParticipantRecord> getParticipantRecords() {

        return Collections.unmodifiableCollection(participantRecords);
    }


    @Override
    public int hashCode() {

        return 31 * 17 + branchId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (!(obj instanceof ResourceManager))
            return false;

        ResourceManager rm = (ResourceManager) obj;
        return rm.branchId.equals(branchId);
    }

    /**
     * @return
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb
                .append("ResourceManager: < branchId=`").append(branchId)
                .append("`, jndiName=`").append(jndiName)
                .append("`, productName=`").append(productName)
                .append("`, productVersion=`").append(productVersion)
                .append("`, eisName=`").append(eisName)
                .append("` >");
        return sb.toString();
    }

}
