package org.jboss.narayana.txvis.logparsing.common;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 19/07/2013
 * Time: 21:11
 */
public interface Filter {

    boolean matches(String line);

}
