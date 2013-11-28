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

package org.jboss.narayana.txvis.test;

import io.narayana.txprof.logparsing.as8.handlers.JbossAS8AbstractHandler;
import io.narayana.txprof.logparsing.common.Handler;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertTrue;

public class LogParserTest {

    @Test
    public void timestampTest() {

        Handler handler = new TestHandler();

        String timestamp = "21:21:59,910";
        String log1 = timestamp + " TRACE [com.arjuna.ats.arjuna] (Transaction Expired Entry Monitor) InputObjectState::InputObjectState()";
        String log2 = "2013-10-22 " + timestamp + " TRACE [com.arjuna.ats.arjuna] (Transaction Expired Entry Monitor) InputObjectState::InputObjectState()";

        Matcher matcher = handler.getPattern().matcher(log1);
        assertTrue(matcher.find());
        handler.handle(matcher, timestamp);

        matcher = handler.getPattern().matcher(log2);
        assertTrue(matcher.find());
        handler.handle(matcher, timestamp);
    }
}

class TestHandler extends JbossAS8AbstractHandler {

    public TestHandler() {

        super("");
    }

    @Override
    public void handle(Matcher matcher, String line) {

        assertTrue(matcher.group(TIMESTAMP).equals(line));
    }
}