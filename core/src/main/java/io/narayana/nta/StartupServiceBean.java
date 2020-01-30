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

package io.narayana.nta;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.File;

/**
 * This bean ensures that nta-core is correctly bootstrapped and will begin logging
 * once it is deployed in the application container. It contains no user invokable methods
 * as these should only be called by the EJB container.
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 31/05/2013
 * Time: 11:33
 */


/*
 * The bean is a startup singleton which causes the application container to invoke
 * the PostConstruct method as soon as the application is deployed.
 *
 * As with all EJBs used in NTA, transaction management MUST be disabled as
 * below. This application is monitoring the transactions produced on the
 * server it's deployed on so, as well as dirtying the data the tool will collect,
 * it can cause a recursive loop, whereby nta spawns a new transaction for
 * every transaction it reads.
 */

@Singleton
@Startup
@DependsOn("LogMonitorBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StartupServiceBean {

    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    @EJB
    private LogMonitorBean logMonitor;

    @PostConstruct
    protected void setup() {
        /*
         * Get the ID of the server we're running on, for the tool to function correctly when
         * monitoring JTS transactions this must be unique as it is essential to identifying a
         * transaction. Uniqueness is dependent on the user correctly configuring their application
         * server!
         */
        System.setProperty(Configuration.NODEID_SYS_PROP_NAME,
                arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier());

        System.setProperty("ARJ_DEFAULT_TIMEOUT", String.valueOf(
                arjPropertyManager.getCoordinatorEnvironmentBean().getDefaultTimeout()));

        if (logger.isInfoEnabled()) {
            logger.info("Narayana Transaction Analyser");
            logger.info("Bootstrapping...");
            logger.info("Server Node Id: " + System.getProperty(Configuration.NODEID_SYS_PROP_NAME));
            logger.info("Logfile: " + Configuration.LOGFILE_PATH);
        }

        startLogParser();
    }

    public void startLogParser() {
        /*
         * todo: The logfile is currently hardwired to "server.log" in the Jboss logfile directory.
         * Ideally this should be able to be overridden by giving a system property to the application server.
         */
        logMonitor.setFile(new File(Configuration.LOGFILE_PATH));
        logMonitor.start();
    }

    @PreDestroy
    protected void tearDown() {

        if (logger.isInfoEnabled())
            logger.info("NTA tool cease monitoring");

        logMonitor.stop();

        if (logger.isInfoEnabled())
            logger.info("NTA tool shutting down");
    }
}