package io.narayana.nta.restapi.apis;

import io.narayana.nta.persistence.enums.Status;
import io.narayana.nta.restapi.models.Response.PayloadResponse;
import io.narayana.nta.restapi.models.URIConstants;
import io.narayana.nta.restapi.services.TransactionService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 04/05/14
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
@Path(URIConstants.TransactionURI)
public class TransactionAPI
{
    @Inject
    private TransactionService transactionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(
            @QueryParam("status")
            String status)
    {
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setStatus(Response.Status.OK);

        if(status == null)
        {
            payloadResponse.setPayload(transactionService.getTransactions());
            return Response.ok(payloadResponse).build();
        }

        Status requestedTransactionStatus = Status.valueOf(status.toUpperCase());
        payloadResponse.setPayload(transactionService.getTransactions(requestedTransactionStatus));
        return Response.ok(payloadResponse).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionById(
            @PathParam("id")
            @NotNull
            Long id)
    {
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setStatus(Response.Status.OK);
        payloadResponse.setPayload(transactionService.getTransaction(id));
        return Response.ok(payloadResponse).build();
    }
}
