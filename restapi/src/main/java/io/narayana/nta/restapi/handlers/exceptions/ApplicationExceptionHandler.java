package io.narayana.nta.restapi.handlers.exceptions;

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Message : " + exception.getMessage());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Cause : " + exception.getCause());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Class : " + exception.getClass());

        if(exception instanceof IllegalArgumentException)
        {
            return BadRequestResponse(stringBuilder.toString());
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(stringBuilder.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    public Response BadRequestResponse(String errorMessage)
    {
        return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
    }
}
