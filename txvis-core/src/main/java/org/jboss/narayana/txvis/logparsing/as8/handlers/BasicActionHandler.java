package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 *
 * An example log line is as follows:
 * <code>>18:15:54,161 TRACE [com.arjuna.ats.arjuna] (default task-14) BasicAction::Begin() for
 * action-id 0:ffffac1182da:579fdb56:51a46622:e77</code>
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 19:34
 */
public class BasicActionHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "BasicAction::" +
            "(?<BASICACTION>Begin|prepare|Abort|phase2Abort|phase2Commit|onePhaseCommit)" +
            ".*?action.*?"+PATTERN_TXUID;


    //13:19:25,765 TRACE [com.arjuna.ats.arjuna] (RequestProcessor-9) BasicAction::addChildThread () action 0:ffff0597491d:226c89eb:51cf87cd:3804 adding Thread[RequestProcessor-9,10,main] result = true


    /**
     *
     */
    public BasicActionHandler() {
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
        final Timestamp timestamp =  parseTimestamp(matcher.group(TIMESTAMP));

        switch (matcher.group("BASICACTION")) {
            case "Begin":
                service.begin(txuid, timestamp, matcher.group(THREAD_ID));
                break;
            case "prepare":
                service.prepare(txuid, timestamp);
                break;
            case "Abort":
                service.abort(txuid, timestamp);
                break;
            case "phase2Abort":
                service.phase2Abort(txuid, timestamp);
                break;
            case "phase2Commit":
                service.phase2Commit(txuid, timestamp);
                break;
            case "onePhaseCommit":
                service.onePhaseCommit(txuid, timestamp);
                break;
        }
    }
}
