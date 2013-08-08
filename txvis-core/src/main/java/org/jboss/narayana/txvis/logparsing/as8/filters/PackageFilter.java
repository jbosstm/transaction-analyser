package org.jboss.narayana.txvis.logparsing.as8.filters;

import org.jboss.narayana.txvis.logparsing.common.Filter;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 19/07/2013
 * Time: 21:12
 */
public class PackageFilter implements Filter {

    public static final String PACKAGE_ARJUNA = "com.arjuna";


    @Override
    public boolean matches(String line) {
        try {
            final int startPos = line.indexOf('[') + 1;
            return !PACKAGE_ARJUNA.equals(line.substring(startPos, startPos + 10));
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
