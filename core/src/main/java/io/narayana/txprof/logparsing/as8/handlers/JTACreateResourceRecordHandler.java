/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package io.narayana.txprof.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class JTACreateResourceRecordHandler extends JbossAS8AbstractHandler {

    /**
     *
     */
    public static final String REGEX = "XAResourceRecord\\.XAResourceRecord.*?" + "tx_uid=" + PATTERN_TXUID + ".*?" +
            PATTERN_XARESOURCEWRAPPERIMPL;

    /**
     *
     */
    public JTACreateResourceRecordHandler() {

        super(REGEX);
    }

    /**
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {

        service.enlistResourceManagerJTA(matcher.group(TXUID), matcher.group(RM_JNDI_NAME), matcher.group(RM_PRODUCT_NAME),
                matcher.group(RM_PRODUCT_VERSION), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
