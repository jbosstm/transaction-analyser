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

package io.narayana.nta.test.utils;

import java.text.MessageFormat;
import java.util.Random;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 02/05/2013
 * Time: 13:13
 */
public class UniqueIdGenerator {

    private static final String HEX_CHARS = "0123456789abcdef";

    private final String txIdBase;
    private int txCounter = 0;

    private static final String RESOURCE_ID_BASE = "io.narayana.nta.test@8aff";
    private int resourceCounter = 0;

    private static final String JNDI_NAME_BASE = "java:jboss/";
    private int jndiCounter = 0;


    public UniqueIdGenerator() {

        this.txIdBase = MessageFormat.format("0:{0}:{1}:{2}:", randomHexString(12),
                randomHexString(8), randomHexString(8));
    }

    public String getUniqueTxId() {

        return txIdBase + txCounter++;
    }

    public String getUniqueResourceId() {

        return RESOURCE_ID_BASE + resourceCounter++;
    }

    public String getUniqueJndiName() {

        return JNDI_NAME_BASE + randomHexString(8) + "/fakeJndiName" + jndiCounter++;
    }

    private String randomHexString(int length) {

        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++)
            sb.append(HEX_CHARS.charAt(rand.nextInt(16)));
        return sb.toString();
    }
}
