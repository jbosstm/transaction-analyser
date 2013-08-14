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

import io.narayana.txprof.persistence.enums.EventType;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/07/2013
 * Time: 22:25
 */
public class JTSResourceRecordHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "ExtendedResourceRecord::topLevel(?<ACTION>Prepare|Commit|Abort)\\(\\)\\sfor\\s"
            + PATTERN_RMUID;

    public JTSResourceRecordHandler() {

        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

        EventType eventType = null;
        switch (matcher.group("ACTION")) {
            case "Prepare":
                eventType = EventType.PREPARE;
                break;
            case "Commit":
                eventType = EventType.COMMIT;
                break;
            case "Abort":
                eventType = EventType.ABORT;
                break;
        }
        service.resourceStatusBeginJTS(matcher.group(RMUID), eventType, parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
