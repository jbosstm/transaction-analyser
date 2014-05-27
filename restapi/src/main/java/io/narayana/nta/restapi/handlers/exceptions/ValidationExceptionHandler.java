package io.narayana.nta.restapi.handlers.exceptions;

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Message : " + exception.getMessage());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Class : " + exception.getClass());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Cause : " + exception.getCause());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Violations : " + exception.getConstraintViolations());

        return Response.status(Response.Status.BAD_REQUEST).entity(stringBuilder.toString()).type(MediaType.APPLICATION_JSON).build();
    }
}
