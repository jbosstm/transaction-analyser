package org.jboss.narayana.txvis;

import org.jboss.narayana.txvis.logparsing.as8.filters.PackageFilter;
import org.jboss.narayana.txvis.logparsing.as8.handlers.*;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 15:56
 */
public final class Configuration {
    /**
     *
     */
    public static final String LOGFILE_NAME = "server.log";
    /**
     *
     */
    public static final String LOGFILE_PATH = System.getProperty("jboss.server.log.dir") + File.separator  + LOGFILE_NAME;
    /**
     *
     */
    public static final int LOGFILE_POLL_INTERVAL = 50;
    /**
     *
     */
    public static final Class[] LOG_HANDLERS = new Class[] {
            BasicActionHandler.class,
            JTACreateResourceRecordHandler.class,
            JTAResourceExceptionHandler.class,
            JTAResourceRecordHandler.class,
            JTSCreateResourceRecordHandler.class,
            JTSResourceExceptionHandler.class,
            JTSResourceRecordHandler.class,
            JTSInterpositionHandler.class,
            JTSResourceStatusChangeHandler.class,
            ArjunaTransactionImpleHandler.class,
            TxPrepareFailedHandler.class,
            TxFinalStatusHandler.class,
    };

    public static final Class[] LOG_FILTERS = new Class[] {
            PackageFilter.class,
    };

    /**
     *
     */
    public static final String HANDLER_SERVICE_JNDI_NAME = "java:module/HandlerService";

    public static final String NODEID_SYS_PROP_NAME = "txvis.nodeid";
}
