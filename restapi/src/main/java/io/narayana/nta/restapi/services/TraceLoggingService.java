package io.narayana.nta.restapi.services;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 14/05/14
 * Time: 23:08
 * To change this template use File | Settings | File Templates.
 */
public interface TraceLoggingService
{
    public boolean getTraceLoggingEnable();
    public void setTraceLoggingEnable(boolean enable);
}
