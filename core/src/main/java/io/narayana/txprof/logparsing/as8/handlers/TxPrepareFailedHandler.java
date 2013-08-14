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
 * Date: 01/08/2013
 * Time: 20:03
 */
public class TxPrepareFailedHandler extends JbossAS8AbstractHandler {

    public static final String REGEX = "BasicAction\\.End\\(\\)\\s-\\sprepare\\sphase\\sof\\saction-id\\s"
            + PATTERN_TXUID + "\\sfailed";

    //BasicAction.End() - prepare phase of action-id 0:ffffac1182da:1b4a00d6:51fa3f1a:6e failed.

    public TxPrepareFailedHandler() {

        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

        service.txPrepareFailed(matcher.group(TXUID), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
