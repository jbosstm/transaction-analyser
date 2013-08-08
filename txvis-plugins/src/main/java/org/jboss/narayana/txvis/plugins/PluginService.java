package org.jboss.narayana.txvis.plugins;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.DataAccessObject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import java.io.Serializable;
import java.util.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 10:35
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PluginService implements Serializable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private Collection<Plugin> plugins = new LinkedList<>();

    @Resource
    private SessionContext ctx;

    public static final long POLL_INTERVAL = 20000;

    @EJB
    private DataAccessObject dao;

    @PostConstruct
    protected void setup() {

        for (Class<?> c : PluginConfig.PLUGINS) {
            try {
                Plugin p = (Plugin) c.newInstance();
                p.injectDAO(dao);
                plugins.add(p);
            }
            catch (InstantiationException | IllegalAccessException | ClassCastException e ) {
                logger.error("PluginService.setup - unable to load plugin: "+c.getSimpleName(), e);
            }
        }
    }

    @PreDestroy
    protected void tearDown() {
        for (Timer timer : ctx.getTimerService().getTimers())
            timer.cancel();
    }

    //@Schedule(minute = "*/2", hour = "*", persistent = true)
    public void scanForIssues() {

        for (Plugin p : plugins)
            p.findIssues();

    }

    public Set<Issue> getIssues() {
        Set<Issue> issues = new HashSet<>();

        for (Plugin p : plugins)
            issues.addAll(p.getIssues());

        return issues;
    }

}
