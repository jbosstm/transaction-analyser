package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/06/2013
 * Time: 18:55
 */
public class ArjunaTransactionImpleHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "ArjunaTransactionImple::get_status\\sfor\\s"+PATTERN_TXUID+
            "\\sreturning\\sCosTransactions::StatusCommitted";

    public ArjunaTransactionImpleHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.cleanup(matcher.group(TXUID));
    }
}
