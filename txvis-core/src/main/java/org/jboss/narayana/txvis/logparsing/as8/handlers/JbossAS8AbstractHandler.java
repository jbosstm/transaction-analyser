package org.jboss.narayana.txvis.logparsing.as8.handlers;

import org.jboss.narayana.txvis.logparsing.common.AbstractHandler;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 21/06/2013
 * Time: 15:47
 */
public abstract class JbossAS8AbstractHandler extends AbstractHandler {

    /**
     * The back reference group name used to retrieve the log4j
     * timestamp TIMESTAMP_PATTERN - retrieve the timestamp by
     * calling <code>matcher.group(TIMESTAMP)</code>
     */
    public static final String TIMESTAMP = "TIMESTAMP";
    /**
     *
     */
    public static final String LOG_LEVEL = "LOGLEVEL";
    /**
     *
     */
    public static final String LOG_CLASS = "LOGCLASS";
    /**
     *
     */
    public static final String THREAD_ID = "THREADID";
    /**
     *
     */
    public static final String RM_JNDI_NAME = "RMJNDINAME";
    /**
     *
     */
    public static final String RM_PRODUCT_NAME = "RMPRODNAME";
    /**
     *
     */
    public static final String RM_PRODUCT_VERSION = "RMPRODVER";
    /**
     *
     */
    public static final String PATTERN_XARESOURCEWRAPPERIMPL = "XAResourceWrapperImpl@.*?" +
            "productName=(?<"+RM_PRODUCT_NAME+">.*?)\\sproductVersion=(?<"+RM_PRODUCT_VERSION+">.*?)\\s" +
            "jndiName=(?<"+RM_JNDI_NAME+">java:[\\w/]+)";

    public static final String RMUID = "RMUID";
    /**
     *
     */
    public static final String PATTERN_RMUID = "(?<"+RMUID+">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";




    /*
     ***********************************************************************************
     * The following private constants are used to form BASE_REGEX_PREFIX which is     *
     * prefixed to the implementation class' regex when using the one argument         *
     * constructor, or two argument with second argument - dontPrefix = true. These    *
     * are tightly coupled to JBoss' log4j output format and will need to be updated   *
     * should the format ever change.                                                  *
     ***********************************************************************************
     */

    /*
     *
     */
    private static final String PATTERN_TIMESTAMP = "(?<"+TIMESTAMP+">\\d{2}:\\d{2}:\\d{2},\\d{3})";
    /*
     *
     */
    private static final String PATTERN_LOG_LEVEL = "(?<"+LOG_LEVEL+">TRACE|DEBUG|INFO|WARN|ERROR|FATAL)";
    /*
     *
     */
    private static final String PATTERN_LOG_CLASS = "\\[(?<"+LOG_CLASS+">[^\\]]+)\\]";

    /*
     *
     */
    private static final String PATTERN_THREAD_ID = "\\((?<"+THREAD_ID+">[^\\)]+)\\)\\s";
    /*
     *
     */
    private static final String BASE_REGEX_PREFIX = "^"+PATTERN_TIMESTAMP+"\\s+"+PATTERN_LOG_LEVEL+"\\s+"+PATTERN_LOG_CLASS +
            "\\s+"+PATTERN_THREAD_ID+".*?";

    /*
     ***********************************************************************************
     */

    public JbossAS8AbstractHandler(String regex) {
        this(regex, false);
    }

    public JbossAS8AbstractHandler(String regex, boolean dontPrefix) {
        super(dontPrefix ? regex : (BASE_REGEX_PREFIX + regex));
    }

    /**
     * Parses a String time value in <code>HH:mm:ss,SSS</code> format.
     *
     * @param dateTime a String containing the time to parse.
     * @return a <code>java.sql.Timestamp</code> representation of the given time.
     * @throws NullPointerException if time is null.
     * @throws IllegalArgumentException if time is not in the format <code>HH:mm:ss,SSS</code>
     */
    protected final Timestamp parseTimestamp(String dateTime) {
        int hour = -1;
        int minute = -1;
        int second = -1;
        int millis = -1;

        try {
            hour = Integer.parseInt(dateTime.substring(0, 2));
            minute = Integer.parseInt(dateTime.substring(3, 5));
            second = Integer.parseInt(dateTime.substring(6, 8));
            millis = Integer.parseInt(dateTime.substring(9));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Utils.parseTimeStamp could not parse: {0}  / tokens: hour={1}, minute={2}, second={3}, millis={4}",
                    dateTime, hour, minute, second, millis), e);
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, millis);

        return new Timestamp(c.getTimeInMillis());
    }
}
