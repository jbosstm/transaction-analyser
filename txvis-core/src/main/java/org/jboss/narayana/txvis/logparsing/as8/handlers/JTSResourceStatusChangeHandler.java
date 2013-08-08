package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/08/2013
 * Time: 00:28
 */
public class JTSResourceStatusChangeHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "BasicAction::do(?<METHOD>Prepare|Commit|Abort)\\(\\)\\sresult\\sfor\\saction-id\\s\\("
            + PATTERN_TXUID+"\\)\\son\\srecord\\sid:\\s\\("+PATTERN_RMUID+"\\)\\sis\\s\\(TwoPhaseOutcome\\.(?<OUTCOME>[A-Z_]+)\\)"
            + "\\snode\\sid:\\s\\((?<NODE>[^\\)]+)\\)";


    public JTSResourceStatusChangeHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceStatusOutcomeJTS(matcher.group(RMUID), matcher.group("OUTCOME"),
                parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
