package io.narayana.nta.restapi.apis;

import io.narayana.nta.persistence.enums.Status;
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

        if(status.isEmpty() || status == null)
        {
            return Response.ok(transactionService.getTransactions()).build();
        }

        Status requestedTransactionStatus = Status.valueOf(status.toUpperCase());
        return Response.ok(transactionService.getTransactions(requestedTransactionStatus)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionById(
            @PathParam("id")
            @NotNull
            Long id)
    {
        return Response.ok(transactionService.getTransaction(id)).build();
    }
}
