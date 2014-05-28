package io.narayana.nta.restapi.models.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 28/05/14
 * Time: 20:39
 * To change this template use File | Settings | File Templates.
 */
public class ErrorResponse extends BaseResponse
{
    private Exception exception;
    private String cause;
    private Class exceptionClass;
    private String violations;

    public Exception getException()
    {
        return exception;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }

    public String getCause()
    {
        return cause;
    }

    public void setCause(String cause)
    {
        this.cause = cause;
    }

    public Class getExceptionClass()
    {
        return exceptionClass;
    }

    public void setExceptionClass(Class exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }

    public String getViolations()
    {
        return violations;
    }

    public void setViolations(String violations)
    {
        this.violations = violations;
    }
}
