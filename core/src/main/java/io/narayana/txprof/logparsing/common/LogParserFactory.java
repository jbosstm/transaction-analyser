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

package io.narayana.txprof.logparsing.common;

import io.narayana.txprof.Configuration;
import io.narayana.txprof.persistence.HandlerService;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 14:26
 */
public final class LogParserFactory {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");

    public static LogParser getInstance() throws NullPointerException, IllegalStateException {

        if (Configuration.LOG_HANDLERS.length == 0)
            throw new IllegalStateException("Cannot instantiate LogParser: Configuration.LOG_HANDLERS is empty");

        final HandlerService service = getService();
        final LogParser logParser = new LogParser();
        // Instantiate Handler classes listed in Configuration utility class and
        // add them to the the LogParser
        for (Class c : Configuration.LOG_HANDLERS) {
            try {
                AbstractHandler h = (AbstractHandler) c.newInstance();
                h.injectService(service);
                logParser.addHandler(h);

                if (logger.isDebugEnabled())
                    logger.debug("Successfully loaded log handler: " + c.getSimpleName());
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                logger.fatal("Unable to load log handler: " + c, e);
                throw new IllegalStateException(e);
            }
        }

        // Instantiate Filter classes listed in Configuration utility class and
        // add them to the LogParser
        for (Class c : Configuration.LOG_FILTERS) {
            try {
                Filter f = (Filter) c.newInstance();
                logParser.addFilter(f);

                if (logger.isDebugEnabled())
                    logger.debug("Successfully loaded filter: " + c.getSimpleName());

            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                logger.fatal("Unable to load log filter: " + c, e);
                throw new IllegalStateException(e);
            }
        }

        return logParser;
    }

    private static HandlerService getService() {

        HandlerService service;
        try {
            Context context = new InitialContext();
            service = (HandlerService) context.lookup(Configuration.HANDLER_SERVICE_JNDI_NAME);
        } catch (NamingException e) {
            logger.fatal("JNDI lookup of HandlerService failed", e);
            throw new IllegalStateException("JNDI lookup of HandlerService failed", e);
        }
        return service;
    }
}