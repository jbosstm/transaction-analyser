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
import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class JTACreateResourceRecordHandler extends JbossAS8AbstractHandler {

    /**
     *
     */
    public static final String REGEX = "XAResourceRecord\\.XAResourceRecord.*?" + "tx_uid=" + PATTERN_TXUID + ".*?branch_uid=" + PATTERN_BRANCHUID + ".*?eis_name=" + PATTERN_EISNAME + ".*?(" + PATTERN_XARESOURCEWRAPPERIMPL + ")?\\] \\), record id=" + PATTERN_RESOURCE_UID + "$";

    public static final String LINE = "XAResourceRecord.XAResourceRecord ( < formatId=131077, gtrid_length=29, " +
            "bqual_length=36, tx_uid=0:ffff7f000001:-3713c968:5243fb29:25, node_name=1, branch_uid=0:ffff7f000001:-3713c968:5243fb29:2c, " +
            "subordinatenodename=null, eis_name=unknown eis name >, XAResourceWrapperImpl@2c12bd7e[xaResource=null pad=false " +
            "overrideRmValue=false productName=Dummy Product productVersion=1.0.0 jndiName=java:jboss/a30c016d/fakeJndiName2] ), record id=0:ffff7f000001:-3713c968:5243fb29:2d";

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

        service.enlistResourceManagerJTA(matcher.group(TXUID), matcher.group(BRANCHUID), matcher.group(RM_JNDI_NAME), matcher.group(RM_PRODUCT_NAME),
                matcher.group(RM_PRODUCT_VERSION), matcher.group(EISNAME), parseTimestamp(matcher.group(TIMESTAMP)), matcher.group(RESUID));
    }

    public static void main(String[] args) {

        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(LINE);
        if (matcher.find()) {
            System.out.println("Found");
            System.out.println(matcher.group(RESUID));
        } else {
            System.out.println("Not found");
        }
    }
}
