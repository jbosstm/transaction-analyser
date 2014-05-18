package io.narayana.nta.restapi.apis;

import io.narayana.nta.restapi.models.URIConstants;
import io.narayana.nta.restapi.services.TraceLoggingService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 14/05/14
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
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

        return Response.ok(tracingStatus).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response setTransactionStatus(@QueryParam("enable") boolean enable)
    {
        traceLoggingService.setTraceLoggingEnable(enable);

        String status = enable ? "Enabled" : "Disabled";

        return Response.ok("Trace logging has been " + status).build();
    }
}
