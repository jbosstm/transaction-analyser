package io.narayana.nta.restapi.models.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 28/05/14
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class PayloadResponse extends BaseResponse
{
    private Object payload;

    public Object getPayload()
    {
        return payload;
    }

    public void setPayload(Object payload)
    {
        this.payload = payload;
    }
}
