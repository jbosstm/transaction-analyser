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

package io.narayana.nta.restapi.apis;

import io.narayana.nta.restapi.models.ResourceManager.ResourceManagerInfo;
import io.narayana.nta.restapi.models.Response.PayloadResponse;
import io.narayana.nta.restapi.models.URIConstants;
import io.narayana.nta.restapi.services.ResourceManagerService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 02/06/14
 * Time: 05:14
 */
@Path(URIConstants.ResourceManagerURI)
public class ResourceManagerAPI {
    @Inject
    ResourceManagerService resourceManagerService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResourceManagers() {
        Collection<ResourceManagerInfo> payload;

        payload = resourceManagerService.getResourceManagers();

        if (payload != null && payload.size() == 0) {
            return Response.noContent().build();
        }

        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setPayload(payload);
        payloadResponse.setStatus(Response.Status.OK);

        return Response.ok(payloadResponse).build();
    }

    @GET
    @Path("/{branchId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResourceManagerByBranchId(
            @PathParam("branchId")
            @NotNull
            String branchId
    ) {
        Object payload = resourceManagerService.getResourceManager(branchId);

        if (payload == null) {
            return Response.noContent().build();
        }

        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setPayload(payload);
        payloadResponse.setStatus(Response.Status.OK);
        return Response.ok(payloadResponse).build();
    }
}
