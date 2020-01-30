/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package io.narayana.nta.interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        final Logger logger = LogManager.getLogger(ctx.getMethod().getDeclaringClass().getName());

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
