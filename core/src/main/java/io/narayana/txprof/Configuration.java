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

package io.narayana.txprof;

import io.narayana.txprof.logparsing.as8.filters.PackageFilter;
import io.narayana.txprof.logparsing.as8.handlers.ArjunaTransactionImpleHandler;
import io.narayana.txprof.logparsing.as8.handlers.BasicActionHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTACreateResourceRecordHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTAResourceExceptionHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTAResourceRecordHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTSCreateResourceRecordHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTSInterpositionHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTSResourceExceptionHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTSResourceRecordHandler;
import io.narayana.txprof.logparsing.as8.handlers.JTSResourceStatusChangeHandler;
import io.narayana.txprof.logparsing.as8.handlers.TxFinalStatusHandler;
import io.narayana.txprof.logparsing.as8.handlers.TxPrepareFailedHandler;

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
    public static final String LOGFILE_PATH = System.getProperty("jboss.server.log.dir") + File.separator + LOGFILE_NAME;
    /**
     *
     */
    public static final int LOGFILE_POLL_INTERVAL = 50;
    /**
     *
     */
    public static final Class[] LOG_HANDLERS = new Class[]{
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

    public static final Class[] LOG_FILTERS = new Class[]{
            PackageFilter.class,
    };

    /**
     *
     */
    public static final String HANDLER_SERVICE_JNDI_NAME = "java:module/HandlerService";

    public static final String NODEID_SYS_PROP_NAME = "txvis.nodeid";
}
