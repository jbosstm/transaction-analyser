package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/08/2013
 * Time: 20:03
 */
public class TxPrepareFailedHandler extends JbossAS8AbstractHandler {

    public static final String REGEX = "BasicAction\\.End\\(\\)\\s-\\sprepare\\sphase\\sof\\saction-id\\s"
            +PATTERN_TXUID+"\\sfailed";

            //BasicAction.End() - prepare phase of action-id 0:ffffac1182da:1b4a00d6:51fa3f1a:6e failed.

    public TxPrepareFailedHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.txPrepareFailed(matcher.group(TXUID), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
