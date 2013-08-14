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

package io.narayana.txprof.logparsing.common;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 16:49
 */
public final class LogParser implements TailerListener {

    private static final Logger logger = Logger.getLogger(LogParser.class.getName());
    private final List<Handler> handlers = new LinkedList<>();
    private final List<Filter> filters = new LinkedList<>();
    private Tailer tailer;

    // Enforce package-private constructor
    LogParser() {

    }

    /**
     * @param handler
     * @throws NullPointerException
     */
    void addHandler(Handler handler) throws NullPointerException {

        if (handler == null)
            throw new NullPointerException("Method called with null parameter: handler");
        handlers.add(handler);
    }

    void addFilter(Filter filter) throws NullPointerException {

        if (filter == null)
            throw new NullPointerException("Method called with null parameter: filter");
        filters.add(filter);
    }


    /**
     * @param line
     */
    @Override
    public void handle(String line) {

        // First check the line against all loaded matches, if there is a positive match skip
        // processing.
        for (Filter filter : filters) {
            if (filter.matches(line))
                return;
        }

        // If there are no filter matches, test against all handlers.
        for (Handler handler : handlers) {
            final Matcher matcher = handler.getPattern().matcher(line);

            if (matcher.find()) {
                if (logger.isDebugEnabled())
                    logger.debug(logFormat(handler, matcher));

                handler.handle(matcher, line);
                break;
            }
        }
    }

    /**
     * @param tailer
     */
    @Override
    public void init(Tailer tailer) {

        this.tailer = tailer;
    }

    /**
     *
     */
    @Override
    public void fileNotFound() {

        logger.fatal("Log file not found: " + tailer.getFile());
        throw new IllegalStateException("Log file not found: " + tailer.getFile());
    }

    /**
     *
     */
    @Override
    public void fileRotated() {

        if (logger.isInfoEnabled())
            logger.info("Log file has been rotated");
    }

    /**
     * @param ex
     */
    @Override
    public void handle(Exception ex) {

        logger.error("Exception caught: ", ex);
    }

    /*
     *
     */
    private String logFormat(Handler handler, Matcher matcher) {

        final StringBuilder sb =
                new StringBuilder(this + " Parser match: handler=`").append(handler.getClass().getSimpleName());

        for (int i = 1; i <= matcher.groupCount(); i++)
            sb.append("`, matcher.group(").append(i).append(")=`").append(matcher.group(i));

        return sb.append("`").toString();
    }
}