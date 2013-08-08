package org.jboss.narayana.txvis.plugins;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 17:56
 */
public final class PluginConfig {

    public static final Collection<Class<?>> PLUGINS = new LinkedList<>();

    static {
        PLUGINS.add(WedgedTxPlugin.class);
        PLUGINS.add(RMRollbackPlugin.class);
    }

}
