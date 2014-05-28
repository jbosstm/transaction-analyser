package io.narayana.nta.restapi.handlers.exceptions;

import io.narayana.nta.restapi.models.Response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 27/05/14
 * Time: 22:51
 * To change this template use File | Settings | File Templates.
 */
@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<Exception>
{
    @Override
    public Response toResponse(Exception exception)
    {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setExceptionClass(exception.getClass());
        errorResponse.setException(exception);
        if(exception.getCause() != null)
        {
            errorResponse.setCause(exception.getCause().toString());
        }

        if(exception instanceof IllegalArgumentException)
        {
            errorResponse.setStatus(Response.Status.BAD_REQUEST);
            return BadRequestResponse(errorResponse);
        }

        errorResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }

    private Response BadRequestResponse(ErrorResponse errorResponse)
    {
        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
