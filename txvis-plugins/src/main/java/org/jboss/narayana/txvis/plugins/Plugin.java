package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.DataAccessObject;

import java.util.Set;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 13:16
 */
public interface Plugin {

    void setup();

    void tearDown();

    Set<Issue> getIssues();

    void findIssues();

    void injectDAO(DataAccessObject dao);
}
