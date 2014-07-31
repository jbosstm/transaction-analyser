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
package io.narayana.nta.restapi.test.unit.helpers;

import io.narayana.nta.restapi.helpers.LinkGenerator;
import io.narayana.nta.restapi.models.URIConstants;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 7/16/2014
 * Time: 11:22 PM
 */

public class LinkGeneratorTest {

    private static final String ASSERTURIFAILURE = "Failure - The URI's are not equal.";

    @Test
    public void testParticipantRecordURIGenerator() {

        long id = 1;
        String participantRecordURI = LinkGenerator.participantRecordURI(id);
        String expectedURI = URIConstants.RootURI + URIConstants.ParticipantRecordURI + "/1";
        Assert.assertEquals(ASSERTURIFAILURE, expectedURI, participantRecordURI);

    }

    @Test
    public void testEventURIGenerator() {

        long id = 1;
        String eventURI = LinkGenerator.eventURI(id);
        String expectedURI = URIConstants.RootURI + URIConstants.EventURI + "/1";
        Assert.assertEquals(ASSERTURIFAILURE, expectedURI, eventURI);
    }

    @Test
    public void testTransactionURIGenerator() {

        long id = 1;
        String transactionURI = LinkGenerator.transactionURI(id);
        String expectedURI = URIConstants.RootURI + URIConstants.TransactionURI + "/1";
        Assert.assertEquals(ASSERTURIFAILURE, expectedURI, transactionURI);
    }

    @Test
    public void testResourceManagerURIGenerator() {

        String branchId = "testBranch";
        String resourceManagerURI = LinkGenerator.resourceManagerURI(branchId);
        String expectedURI = URIConstants.RootURI + URIConstants.ResourceManagerURI + "/testBranch";
        Assert.assertEquals(ASSERTURIFAILURE, expectedURI, resourceManagerURI);
    }
}
