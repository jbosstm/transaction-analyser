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

package io.narayana.txprof.webapp.models;

import io.narayana.txprof.plugins.Issue;
import io.narayana.txprof.plugins.PluginService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/07/2013
 * Time: 16:48
 */
@SessionScoped
@Named
public class IssueParserBean implements Serializable {

    public static final String TXUID_LINK_FORMAT = "<a href=\"/txvis/txinfo.jsf?includeViewParams=true&amp;txuid={0}\">{1}</a>";
    public static final String FORUM_LINK_FORMAT = "<a href=\"https://community.jboss.org/search.jspa?q={0}&containerType=14" +
            "&container=2040\">Search JBoss forums for help</a>";


    @Inject
    private PluginService pluginService;

    private Set<Issue> issues = new HashSet<>();

    public Collection<Issue> getIssues() {

        updateIssues();

        // facelets don't support iterating over a set
        return new LinkedList<>(issues);
    }

    private void updateIssues() {

        Set<Issue> latestIssues = pluginService.getIssues();

        // Ditch any issues which are no longer valid
        issues.retainAll(latestIssues);
        // Don't parse any issues a second time.
        latestIssues.removeAll(issues);

        for (Issue issue : latestIssues)
            parseIssue(issue);

        issues.addAll(latestIssues);
    }

    private void parseIssue(Issue issue) {

        final String pattern = "(?:-?[0-9a-f]+:){4}-?[0-9a-f]+";


        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(issue.getBody());
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            final String txuid = m.group(0);
            final String shortTxuid = MessageFormat.format("...{0}", txuid.substring(txuid.length() - 5, txuid.length()));
            m.appendReplacement(sb, MessageFormat.format(TXUID_LINK_FORMAT, txuid, shortTxuid));
        }
        m.appendTail(sb);

        sb.append("<p>- ").append(produceForumLink(issue)).append("</p>");
        issue.setBody(sb.toString());
    }

    private String produceForumLink(Issue issue) {

        final StringBuilder sb = new StringBuilder();

        for (String tag : issue.getTags())
            sb.append(tag).append("%20");

        return MessageFormat.format(FORUM_LINK_FORMAT, sb.toString());
    }
}
