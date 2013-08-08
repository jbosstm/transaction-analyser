package org.jboss.narayana.txvis.logparsing.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 16:54
 */
public interface Handler {
    /**
     *
     * @param matcher
     * @param line
     */
    void handle(Matcher matcher, String line);

    /**
     *
     * @return
     */
    Pattern getPattern();
}
