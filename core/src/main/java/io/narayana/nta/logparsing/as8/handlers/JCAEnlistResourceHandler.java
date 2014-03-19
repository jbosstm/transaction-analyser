/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
public class JCAEnlistResourceHandler extends JbossAS8AbstractHandler {
    public static final String REGEX = "Enlisted resource.*?xaResource=LocalXAResourceImpl.*?" + "tx_uid=" + PATTERN_TXUID +
            ".*?productName=(?<" + RM_PRODUCT_NAME + ">.*?)\\sproductVersion=(?<" + RM_PRODUCT_VERSION + ">.*?)" +
            "\\sjndiName=(?<" + RM_JNDI_NAME + ">java:[\\w/]+)\\].*";

    public static final String LAST_RESOURCE_ID = "0:0:0:0:1";

    public JCAEnlistResourceHandler() {
        super(REGEX);
    }


    @Override
    public void handle(Matcher matcher, String line) {
        service.enlistResourceManagerJTA(matcher.group(TXUID), matcher.group(RM_JNDI_NAME), matcher.group(RM_PRODUCT_NAME),
                matcher.group(RM_PRODUCT_VERSION), parseTimestamp(matcher.group(TIMESTAMP)), LAST_RESOURCE_ID);
    }
}
