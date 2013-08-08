package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import java.text.MessageFormat;
import java.util.*;

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

    public static final String[] TAGS = new String[] {
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
