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

import org.apache.commons.io.input.Tailer;
import org.apache.log4j.Logger;
import io.narayana.txprof.logparsing.common.LogParser;
import io.narayana.txprof.logparsing.common.LogParserFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */

/*
 * This bean is a singleton purely to let us change the locktype and
 * allow concurrent access to the bean. We need this as the start()
 * method is an asynchronous method that will run for the lifetime of the
 * application, without this we would not be able to invoke the stop()
 * method from the StartupService which prevents graceful shutdown of txvis
 * but also has the potential to prevent the application sever from
 * shutting down!
 *
 * As with all EJBs used in Txvis, transaction management MUST be disabled as
 * below. This application is monitoring the transactions produced on the
 * server it's deployed on so as well as dirtying the data the tool will collect,
 * it can cause a recursive loop, which will rapidly result in the JVM running
 * out of memory!
 */
@Singleton
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Lock(LockType.READ)
public class LogMonitorBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private File logFile;
    private Tailer tailer;
    public LogParser logParser;

    @Resource
    private SessionContext sessionContext;

    @EJB
    private StartupServiceBean startupServiceBean;

    /**
     * Starts asynchronously monitoring the logfile which must have been set by invoking the
     * setFile method. This method is non-blocking and control will be returned immediately to
     * the invoking method. The method will run continuously until the stop() method is invoked.
     *
     * @throws IllegalStateException if setFile has not been previous invoked to set the file to monitor
     */
    @Asynchronous
    public void start() throws IllegalStateException {

        if (logFile == null)
            throw new IllegalStateException("setFile must be called before invoking start()");

        if (tailer == null) {
            try {
                tailer = new Tailer(logFile, logParser, Configuration.LOGFILE_POLL_INTERVAL, true);
                tailer.run();
            } catch (Exception e) {
                logger.error("Unhandled exception, shutting down...", e);
                // Not shutting down can cause problems when trying to undeploy the application if the
                // unhandled exception cannot be recovered from simply catching it, which appears to
                // be a large majority of cases. The stop method should be invoked through the
                // containers proxies.
                sessionContext.getBusinessObject(LogMonitorBean.class).stop();

                // Attempt to restart the logger.
                startupServiceBean.startLogParser();
            }
        }
    }

    /**
     * Ceases monitoring of the log file, if the log monitor is presently running.
     */
    public void stop() {

        if (tailer != null) {
            tailer.stop();
            tailer = null;
        }
    }

    /**
     * Sets the file that will be monitored. This must be called before invoking the start()
     * method and must not be invoked while monitoring is in progress, this should be checked
     * using the isRunning() method.
     *
     * @param file A File object representing the log file to be monitored.
     * @throws NullPointerException  if null is given instead of a file.
     * @throws IllegalStateException if the method is invoked while monitoring is in progress.
     */
    public void setFile(File file) throws NullPointerException, IllegalStateException {

        if (file == null)
            throw new NullPointerException("Method called with null parameter: file");

        if (tailer != null)
            throw new IllegalStateException("Cannot call setFile while LogMonitor is running");

        logFile = file;
        logParser = LogParserFactory.getInstance();
    }

    /**
     * Reveals whether log monitoring is currently in progress.
     *
     * @return <code>true</code> if the a log file is being monitored <code>false</code> if not.
     */
    public boolean isRunning() {

        return tailer != null;
    }

}