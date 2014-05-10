package io.narayana.nta.restapi.apis;

import io.narayana.nta.restapi.services.TransactionService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 04/05/14
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
@Path("/transaction")
public class TransactionAPI
{
    @Inject
    private TransactionService transactionService;

    @GET
    @Produces("application/json")
    public Response getTransactions()
    {
        return Response.ok().build();
    }

   /* @GET
    @Produces("application/json")
    public Response getTransactionById(Long id)
    {
        Transaction transaction = dao.findTransaction(id);

        return Response.ok(transaction).build();
    }*/
}
