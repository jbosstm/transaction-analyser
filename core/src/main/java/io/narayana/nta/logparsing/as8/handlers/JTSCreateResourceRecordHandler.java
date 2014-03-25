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

package io.narayana.nta.logparsing.as8.handlers;

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
    public static final String REGEX = "enlistResource:" + "\\sresource_trace:\\stxn\\suid=" + PATTERN_TXUID
            + ".*?branch_uid=" + BRANCHUID + ".*?eis_name=" + EISNAME + ".*?"
            + PATTERN_XARESOURCEWRAPPERIMPL + ".*?uid=" + PATTERN_RMUID;

    /**
     *
     */
    public JTSCreateResourceRecordHandler() {

        super(REGEX);
    }

    /**
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {

        service.enlistResourceManagerJTS(matcher.group(TXUID), matcher.group(BRANCHUID), matcher.group(RMUID), matcher.group(RM_JNDI_NAME),
                matcher.group(RM_PRODUCT_NAME), matcher.group(RM_PRODUCT_VERSION), matcher.group(EISNAME), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
