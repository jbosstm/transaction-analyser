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

package io.narayana.nta.logparsing.common;

import io.narayana.nta.persistence.HandlerService;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:56
 */
public abstract class AbstractHandler implements Handler {

    /**
     *
     */
    public static final String TXUID = "TXUID";
    public static final String BRANCHUID = "BRANCHUID";
    public static final String RESUID = "RESUID";
    public static final String EISNAME = "EISNAME";
    /**
     *
     */
    public static final String PATTERN_TXUID = "(?<" + TXUID + ">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";
    public static final String PATTERN_BRANCHUID = "(?<" + BRANCHUID + ">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";
    public static final String PATTERN_EISNAME = "(?<" + EISNAME + ">(.*?))\\s\\>";
    public static final String PATTERN_RESOURCE_UID = "(?<" + RESUID + ">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";


    private final Pattern pattern;
    protected HandlerService service;

    /**
     * @param regex
     * @throws PatternSyntaxException
     */
    public AbstractHandler(String regex) throws PatternSyntaxException {

        this.pattern = Pattern.compile(regex);
    }

    /**
     * @return
     */
    @Override
    public final Pattern getPattern() {

        return this.pattern;
    }

    /**
     * @param service
     * @throws NullPointerException
     */
    final void injectService(HandlerService service) throws NullPointerException {

        if (service == null)
            throw new NullPointerException("Method called with null parameter: service");
        this.service = service;
    }
}