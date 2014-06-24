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
import io.narayana.nta.persistence.entities.Event;
import io.narayana.nta.restapi.helpers.LinkGenerator;
import io.narayana.nta.restapi.models.event.EventInfo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 6/24/2014
 * Time: 11:04 PM
 */
public class EventServiceImpl implements EventService {

    @Inject
    DataAccessObject dao;

    @Override
    public Collection<EventInfo> getEvents() {
        Collection<Event> events = dao.findAllEvents();

        Collection<EventInfo> eventInfos = processDaoEvents(events);

        return eventInfos;
    }

    @Override
    public EventInfo getEvent(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("event id cannot be null.");
        }

        Event event = dao.findEvent(id);

        return processDoaEvent(event);
    }

    private Collection<EventInfo> processDaoEvents(Collection<Event> events) {

        Collection<EventInfo> eventInfos = new ArrayList<EventInfo>();

        for(Event event : events){
            eventInfos.add(processDoaEvent(event));
        }
        return null;
    }

    private EventInfo processDoaEvent(Event event) {

        if(event != null){

            EventInfo eventInfo = new EventInfo();
            eventInfo.setId(event.getId());
            eventInfo.setTimestamp(event.getTimestamp());
            eventInfo.setEventType(event.getEventType());
            eventInfo.setEventEntity(event.getEventEntity());
            eventInfo.setTransaction(LinkGenerator.eventURI(event.getId()));

            return eventInfo;
        }

        return null;
    }
}
