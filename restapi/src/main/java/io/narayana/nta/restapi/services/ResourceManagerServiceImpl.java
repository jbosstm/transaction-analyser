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

package io.narayana.nta.restapi.services;

import io.narayana.nta.persistence.DataAccessObject;
import io.narayana.nta.persistence.entities.ParticipantRecord;
import io.narayana.nta.persistence.entities.ResourceManager;
import io.narayana.nta.restapi.helpers.LinkGenerator;
import io.narayana.nta.restapi.models.ResourceManager.ResourceManagerInfo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 02/06/14
 * Time: 05:30
 */
public class ResourceManagerServiceImpl implements ResourceManagerService {
    @Inject
    DataAccessObject dao;

    @Override
    public Collection<ResourceManagerInfo> getResourceManagers() {
        Collection<ResourceManager> resourceManagers = dao.findAllResourceManagers();

        return processDaoResourceManagers(resourceManagers);
    }

    @Override
    public ResourceManagerInfo getResourceManager(String branchId) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch Id cannot be null.");
        }

        ResourceManager resourceManager = dao.findResourceManagerByBranchId(branchId);

        return processDaoResourceManager(resourceManager);
    }

    private Collection<ResourceManagerInfo> processDaoResourceManagers(Collection<ResourceManager> daoResourceManagers) {
        if (daoResourceManagers != null) {
            Collection<ResourceManagerInfo> resourceManagers = new ArrayList<>();
            for (ResourceManager resourceManager : daoResourceManagers) {
                resourceManagers.add(processDaoResourceManager(resourceManager));
            }

            return resourceManagers;
        }
        return null;
    }

    private ResourceManagerInfo processDaoResourceManager(ResourceManager daoResourceManager) {
        if (daoResourceManager != null) {
            Collection<String> participantRecords = new ArrayList<>();

            for (ParticipantRecord participantRecord : daoResourceManager.getParticipantRecords()) {
                participantRecords.add(LinkGenerator.participantRecordURI(participantRecord.getId()));
            }

            ResourceManagerInfo resourceManagerInfo = new ResourceManagerInfo();
            resourceManagerInfo.setBranchId(daoResourceManager.getBranchId());
            resourceManagerInfo.setEisName(daoResourceManager.getEisName());
            resourceManagerInfo.setJndiName(daoResourceManager.getJndiName());
            resourceManagerInfo.setProductName(daoResourceManager.getProductName());
            resourceManagerInfo.setProductVersion(daoResourceManager.getProductVersion());
            resourceManagerInfo.setParticipantsRecords(participantRecords);

            return resourceManagerInfo;
        }
        return null;
    }
}
