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
import io.narayana.txprof.persistence.entities.Transaction;
import io.narayana.txprof.persistence.enums.Status;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/07/2013
 * Time: 19:53
 */
public class WedgedTxPlugin implements Plugin {

    public static final int THRESHOLD = 60000;

    public static final String TITLE = "Possible Wedged Transaction";

    public static final String BODY = "Transaction: {0} has been in status prepare longer than the default timeout value of " +
            "{1}ms this may indicate that the transaction is wedged (stuck)";

    public static final String[] TAGS = new String[]{
            "Wedged"
    };

    private Set<Issue> issues = new HashSet<>();

    private DataAccessObject dao;

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

        for (Transaction tx : dao.findAllTopLevelTransactionsWithStatus(Status.PREPARE)) {
            if (tx.getDuration() > THRESHOLD) {
                Issue issue = new Issue();
                issue.setCause(tx);
                issue.setTitle(TITLE);
                issue.setBody(MessageFormat.format(BODY, tx.getTxuid(), THRESHOLD));
                for (String tag : TAGS)
                    issue.addTag(tag);

                newIssues.add(issue);
            }
        }
        // The below operations ensure that 1) we don't create duplicate issues and
        // 2) We don't overwrite any existing issues which may have been parsed already.
        issues.retainAll(newIssues);
        issues.addAll(newIssues);
    }

    @Override
    public void injectDAO(DataAccessObject dao) {

        this.dao = dao;
    }
}
