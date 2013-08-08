package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 *
 *
 * Example Log lines: <br/>
 * <code>11:58:34,955 TRACE [com.arjuna.ats.arjuna] (default task-14) TransactionReaper::remove (
 * BasicAction: 0:ffff05974e31:-60d4f33f:519c9c7d:d2 status: ActionStatus.COMMITTED )</code>
 * <br/>
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 17:20
 * //10:45:31,753 TRACE [com.arjuna.ats.arjuna] (default task-2) TransactionReaper::remove (
 * BasicAction: 0:ffffac1182da:1f1d981b:51a5c3b5:127 status: ActionStatus.COMMITTED )
 */
public class TransactionReaperHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "TransactionReaper::remove.*?BasicAction:\\s"+ PATTERN_TXUID +
            ".*?ActionStatus\\.(?<ACTIONSTATUS>[A-Z]+)";

    /**
     *
     */
    public TransactionReaperHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        final String txuid = matcher.group(TXUID);
        final Timestamp timestamp = parseTimestamp(matcher.group(TIMESTAMP));

        switch(matcher.group("ACTIONSTATUS")) {
            case "COMMITTED":
                break;
            case "ABORT":
                break;
            default:
                break;
        }
    }
}
