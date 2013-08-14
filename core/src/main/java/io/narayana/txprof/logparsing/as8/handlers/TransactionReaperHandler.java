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

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 * Example Log lines: <br/>
 * <code>11:58:34,955 TRACE [com.arjuna.ats.arjuna] (default task-14) TransactionReaper::remove (
 * BasicAction: 0:ffff05974e31:-60d4f33f:519c9c7d:d2 status: ActionStatus.COMMITTED )</code>
 * <br/>
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 17:20
 * //10:45:31,753 TRACE [com.arjuna.ats.arjuna] (default task-2) TransactionReaper::remove (
 * BasicAction: 0:ffffac1182da:1f1d981b:51a5c3b5:127 status: ActionStatus.COMMITTED )
 */
public class TransactionReaperHandler extends JbossAS8AbstractHandler {

    /**
     *
     */
    public static final String REGEX = "TransactionReaper::remove.*?BasicAction:\\s" + PATTERN_TXUID +
            ".*?ActionStatus\\.(?<ACTIONSTATUS>[A-Z]+)";

    /**
     *
     */
    public TransactionReaperHandler() {

        super(REGEX);
    }

    /**
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {

        final String txuid = matcher.group(TXUID);
        final Timestamp timestamp = parseTimestamp(matcher.group(TIMESTAMP));

        switch (matcher.group("ACTIONSTATUS")) {
            case "COMMITTED":
                break;
            case "ABORT":
                break;
            default:
                break;
        }
    }
}
