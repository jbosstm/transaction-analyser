package io.narayana.nta.restapi.models.Response;

import javax.ws.rs.core.Response;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 28/05/14
 * Time: 20:35
 * To change this template use File | Settings | File Templates.
 */
public class BaseResponse
{
    private String timeStamp;
    private Response.Status status;
    private String message;

    public BaseResponse()
    {
        this.timeStamp = Calendar.getInstance().getTime().toString();
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Response.Status getStatus()
    {
        return status;
    }

    public void setStatus(Response.Status status)
    {
        this.status = status;
    }
}
