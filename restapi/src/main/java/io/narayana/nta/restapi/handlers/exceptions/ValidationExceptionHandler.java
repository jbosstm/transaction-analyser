package io.narayana.nta.restapi.handlers.exceptions;

import io.narayana.nta.restapi.models.Response.ErrorResponse;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 27/05/14
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException>
{
    @Override
    public Response toResponse(ConstraintViolationException exception)
    {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setExceptionClass(exception.getClass());
        errorResponse.setException(exception);
        errorResponse.setViolations(exception.getConstraintViolations().toString());
        if(exception.getCause() != null)
        {
            errorResponse.setCause(exception.getCause().toString());
        }
        errorResponse.setStatus(Response.Status.BAD_REQUEST);

        return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
