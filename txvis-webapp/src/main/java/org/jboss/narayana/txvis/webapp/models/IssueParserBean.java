package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.plugins.Issue;
import org.jboss.narayana.txvis.plugins.PluginService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
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
        pluginService.scanForIssues();
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
