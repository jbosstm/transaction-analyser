package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;

import java.text.MessageFormat;
import java.util.*;

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

        int totalTx = dao.findAllTopLevelTransactions().size();
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
