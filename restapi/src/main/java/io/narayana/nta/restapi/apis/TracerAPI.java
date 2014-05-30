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

import io.narayana.nta.restapi.models.Response.BaseResponse;
import io.narayana.nta.restapi.models.URIConstants;
import io.narayana.nta.restapi.services.TraceLoggingService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 14/05/14
 * Time: 23:01
 */
@Path(URIConstants.TracerURI)
public class TracerAPI
{
    @Inject
    private TraceLoggingService traceLoggingService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTraceStatus()
    {
        boolean tracingStatus = traceLoggingService.getTraceLoggingEnable();

        BaseResponse baseResponse  = new BaseResponse();
        baseResponse.setMessage(String.valueOf(tracingStatus));
        baseResponse.setStatus(Response.Status.OK);
        return Response.ok(baseResponse).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response setTransactionStatus(@QueryParam("enable") boolean enable)
    {
        traceLoggingService.setTraceLoggingEnable(enable);

        String status = enable ? "Enabled" : "Disabled";
        BaseResponse baseResponse  = new BaseResponse();
        baseResponse.setMessage("Trace logging has been " + status);
        baseResponse.setStatus(Response.Status.OK);

        return Response.ok(baseResponse).build();
    }
}
