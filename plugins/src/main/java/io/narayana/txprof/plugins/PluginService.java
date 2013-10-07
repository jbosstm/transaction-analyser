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

package io.narayana.txprof.plugins;

import io.narayana.txprof.persistence.DataAccessObject;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                logger.error("PluginService.setup - unable to load plugin: " + c.getSimpleName(), e);
            }
        }
    }

    @PreDestroy
    protected void tearDown() {

        for (Timer timer : ctx.getTimerService().getTimers())
            timer.cancel();
    }

    @Schedule(minute = "*/2", hour = "*", persistent = false)
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
