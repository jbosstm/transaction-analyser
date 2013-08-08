package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 20:34
 */
public class JTAResourceExceptionHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "prepare\\son.*?tx_uid="+ PATTERN_TXUID +".*?" +
            "jndiName=(?<JNDINAME>java:[\\w/]+).*?failed\\swith\\sexception\\sXAException\\.(?<XAEXCEPTION>[A-Z_]+)";

    /**
     *
     */
    public JTAResourceExceptionHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceFailedToPrepareJTA(matcher.group(TXUID), matcher.group("JNDINAME"), matcher.group("XAEXCEPTION"),
                parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
