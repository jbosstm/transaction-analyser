package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 23/06/2013
 * Time: 18:22
 */
public class JTSCreateResourceRecordHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "enlistResource:"+"\\sresource_trace:\\stxn\\suid="+PATTERN_TXUID+".*?"
            +PATTERN_XARESOURCEWRAPPERIMPL+".*?uid="+PATTERN_RMUID;
    /**
     *
     */
    public JTSCreateResourceRecordHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.enlistResourceManagerJTS(matcher.group(TXUID), matcher.group(RMUID), matcher.group(RM_JNDI_NAME),
                matcher.group(RM_PRODUCT_NAME), matcher.group(RM_PRODUCT_VERSION), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
