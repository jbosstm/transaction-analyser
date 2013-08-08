package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:45
 */
public class JTAResourceRecordHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "XAResourceRecord.topLevelPrepare.*?jndiName=(?<JNDINAME>java:[\\w/]+).*?" +
            "tx_uid="+PATTERN_TXUID;

    /**
     *
     */
    public JTAResourceRecordHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.resourcePreparedJTA(matcher.group(TXUID), matcher.group("JNDINAME"), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
