package org.jboss.narayana.txvis.interceptors;

import org.apache.log4j.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/06/2013
 * Time: 15:37
 */
public class LoggingInterceptor implements Serializable {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        final Logger logger = Logger.getLogger(ctx.getMethod().getDeclaringClass().getName());

        if (logger.isTraceEnabled()) {
            final StringBuilder sb = new StringBuilder();

            sb.append(ctx.getMethod().getDeclaringClass().getSimpleName()).append(".")
                    .append(ctx.getMethod().getName()).append("(");

            for (Object param : ctx.getParameters()) {
                sb.append(" `").append(param).append("`,");
            }
            logger.trace(sb.append(" )").toString());
        }

        return ctx.proceed();
    }

}
