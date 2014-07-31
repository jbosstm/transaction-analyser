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
import io.narayana.nta.restapi.helpers.LinkGenerator;
import io.narayana.nta.restapi.models.participantRecord.ParticipantRecordInfo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 04/06/14
 * Time: 12:14
 */
public class ParticipantRecordServiceImpl implements CommonService<ParticipantRecordInfo> {

    @Inject
    DataAccessObject dao;

    @Override
    public Collection<ParticipantRecordInfo> get() {

        Collection<ParticipantRecord> participantRecords = dao.findAllParticipantRecords();

        return processDaoParticipantRecords(participantRecords);
    }

    @Override
    public ParticipantRecordInfo getById(Long id){

        if (id == null) {
            throw new IllegalArgumentException("The passed in parameter Id cannot be null.");
        }
        ParticipantRecord daoParticipantRecord = dao.findParticipantRecord(id);
        return processDaoParticipantRecord(daoParticipantRecord);
    }

    private Collection<ParticipantRecordInfo> processDaoParticipantRecords(Collection<ParticipantRecord> daoParticipantRecords) {
        if (daoParticipantRecords != null) {
            Collection<ParticipantRecordInfo> participantRecords = new ArrayList<>();
            for (ParticipantRecord participantRecord : daoParticipantRecords) {
                participantRecords.add(processDaoParticipantRecord(participantRecord));
            }

            return participantRecords;
        }
        return null;
    }

    private ParticipantRecordInfo processDaoParticipantRecord(ParticipantRecord daoParticipantRecord) {
        if (daoParticipantRecord != null) {

            ParticipantRecordInfo participantRecordInfo = new ParticipantRecordInfo();
            participantRecordInfo.setId(daoParticipantRecord.getId());
            participantRecordInfo.setRmuid(daoParticipantRecord.getRmuid());
            participantRecordInfo.setPrepareCalled(daoParticipantRecord.isPrepareCalled());
            participantRecordInfo.setVote(daoParticipantRecord.getVote());
            participantRecordInfo.setXaException(daoParticipantRecord.getXaException());
            participantRecordInfo.setResourceManager(LinkGenerator.resourceManagerURI(daoParticipantRecord.getResourceManager().getBranchId()));
            participantRecordInfo.setTransaction(LinkGenerator.transactionURI(daoParticipantRecord.getTransaction().getId()));

            return participantRecordInfo;
        }
        return null;
    }
}
