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

package io.narayana.nta.plugins;

import io.narayana.nta.persistence.DataAccessObject;
import io.narayana.nta.persistence.entities.ParticipantRecord;
import io.narayana.nta.persistence.entities.ResourceManager;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 05/08/2013
 * Time: 12:51
 */
public class RMRollbackPlugin implements Plugin {

    private DataAccessObject dao;

    private Set<Issue> issues = new HashSet<>();

    /**
     * Percentage threshold of transaction rollbacks caused by a resource manager at which
     * to report an issue.
     */
    public static final int THRESHOLD = 5;

    public static final int MIN_NUMBER = 3;

    private static final String SUBJECT = "Possible Problem With Resource Manager";


    @Override
    public void setup() {

    }

    @Override
    public void tearDown() {

    }

    @Override
    public Set<Issue> getIssues() {

        return Collections.unmodifiableSet(issues);
    }

    @Override
    public void findIssues() {

        Set<Issue> newIssues = new HashSet<>();

        int totalTx = dao.countAllTopLevelTransactions();
        int absoluteThreshold = totalTx / 100 * THRESHOLD;

        Collection<ResourceManager> rms = dao.findAllResourceManagers();

        for (ResourceManager rm : rms) {
            List<ParticipantRecord> causedRollbackList = causedRollback(rm);

            if (causedRollbackList.size() > MIN_NUMBER && causedRollbackList.size() >= absoluteThreshold)
                newIssues.add(composeIssue(rm, causedRollbackList));
        }

        // The below operations ensure that 1) we don't create duplicate issues and
        // 2) We don't overwrite any existing issues which may have been parsed already.
        issues.retainAll(newIssues);
        issues.addAll(newIssues);
    }

    private List<ParticipantRecord> causedRollback(ResourceManager rm) {

        List<ParticipantRecord> causedRollbackList = new LinkedList<>();

        for (ParticipantRecord rec : rm.getParticipantRecords())
            if (rec.getXaException() != null)
                causedRollbackList.add(rec);

        return causedRollbackList;
    }

    private Issue composeIssue(ResourceManager rm, List<ParticipantRecord> causedRollBackList) {

        final Issue issue = new Issue();
        issue.setCause(causedRollBackList.get(0).getTransaction());
        issue.setTitle(MessageFormat.format(SUBJECT, rm.getJndiName()));

        final StringBuilder sb = new StringBuilder();
        sb.append("Product: ").append(rm.getProductName()).append("<br/>Version: ").append(rm.getProductVersion())
                .append("<br/>JNDI: ").append(rm.getJndiName()).append("<p>This resource manager has caused more than ")
                .append(THRESHOLD).append("% of total monitored transactions to rollback, which could " +
                "indicate it has been misconfigured. Rollbacks were caused in transactions: ")
                .append(causedRollBackList.get(0).getTransaction().getTxuid()).append(", ")
                .append(causedRollBackList.get(1).getTransaction().getTxuid()).append(", ")
                .append(causedRollBackList.get(2).getTransaction().getTxuid()).append(" ...");
        issue.setBody(sb.toString());

        issue.addTag(rm.getProductName());
        issue.addTag(rm.getProductVersion());
        issue.addTag(causedRollBackList.get(0).getXaException());

        return issue;
    }

    @Override
    public void injectDAO(DataAccessObject dao) {

        this.dao = dao;
    }
}
