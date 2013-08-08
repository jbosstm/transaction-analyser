package org.jboss.narayana.txvis;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.File;

/**
 * This bean ensures that txvis-core is correctly bootstrapped and will begin logging
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
 * As with all EJBs used in Txvis, transaction management MUST be disabled as
 * below. This application is monitoring the transactions produced on the
 * server it's deployed on so, as well as dirtying the data the tool will collect,
 * it can cause a recursive loop, whereby txvis spawns a new transaction for
 * every transaction it reads.
 */

@Singleton
@Startup
@DependsOn("LogMonitorBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StartupServiceBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

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
            logger.info("TxVis: JBoss Transaction Visualization Tool");
            logger.info("Bootstrapping...");
            logger.info("Server Node Id: "+System.getProperty(Configuration.NODEID_SYS_PROP_NAME));
            logger.info("Logfile: "+Configuration.LOGFILE_PATH);

            logger.info("System Properties");
            for (String propName : System.getProperties().stringPropertyNames())
                logger.info(propName + " = " + System.getProperty(propName));
            logger.info("");
        }

        startLogParser();
    }

    public void startLogParser() {
        /*
         * FIXME The logfile is currently hardwired to "server.log" in the Jboss logfile directory.
         * Ideally this should be able to be overridden by giving a system property to the application server.
         */
        logMonitor.setFile(new File(Configuration.LOGFILE_PATH));
        logMonitor.start();
    }

    @PreDestroy
    protected void tearDown() {
        if (logger.isInfoEnabled())
            logger.info("Txvis tool cease monitoring");

        logMonitor.stop();

        if (logger.isInfoEnabled())
            logger.info("Txvis tool shutting down");
    }
}