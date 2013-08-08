package org.jboss.narayana.txvis.logparsing.common;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.logparsing.common.AbstractHandler;
import org.jboss.narayana.txvis.logparsing.common.LogParser;
import org.jboss.narayana.txvis.persistence.HandlerService;

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
                    logger.debug("Successfully loaded log handler: "+c.getSimpleName());
            }
            catch (InstantiationException | IllegalAccessException | ClassCastException e) {
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
                    logger.debug("Successfully loaded filter: "+c.getSimpleName());

            }
            catch (InstantiationException | IllegalAccessException | ClassCastException e) {
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
        }
        catch (NamingException e) {
            logger.fatal("JNDI lookup of HandlerService failed", e);
            throw new IllegalStateException("JNDI lookup of HandlerService failed", e);
        }
        return service;
    }
}