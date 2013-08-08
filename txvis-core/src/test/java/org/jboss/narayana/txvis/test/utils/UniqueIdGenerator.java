package org.jboss.narayana.txvis.test.utils;

import java.text.MessageFormat;
import java.util.Random;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 02/05/2013
 * Time: 13:13
 */
public class UniqueIdGenerator {

    private static final String HEX_CHARS = "0123456789abcdef";

    private final String txIdBase;
    private int txCounter = 0;

    private static final String RESOURCE_ID_BASE = "org.jboss.narayana.txvis.test@8aff";
    private int resourceCounter = 0;

    private static final String JNDI_NAME_BASE = "java:jboss/";
    private int jndiCounter = 0;


    public UniqueIdGenerator() {
        this.txIdBase = MessageFormat.format("0:{0}:{1}:{2}:", randomHexString(12),
                randomHexString(8), randomHexString(8));
    }

    public String getUniqueTxId() {
        return txIdBase + txCounter++;
    }

    public String getUniqueResourceId() {
        return RESOURCE_ID_BASE + resourceCounter++;
    }

    public String getUniqueJndiName() {
        return JNDI_NAME_BASE + randomHexString(8) + "/fakeJndiName" + jndiCounter++;
    }

    private String randomHexString(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i ++)
            sb.append(HEX_CHARS.charAt(rand.nextInt(16)));
        return sb.toString();
    }
}
