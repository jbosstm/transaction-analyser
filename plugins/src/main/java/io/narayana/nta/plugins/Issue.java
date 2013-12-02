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

package io.narayana.nta.plugins;

import io.narayana.nta.persistence.entities.Transaction;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 17:53
 */
public class Issue implements Serializable {

    private String title;
    private String body;
    private boolean read;
    private Transaction cause;

    private Set<String> tags = new HashSet<>();

    public Issue() {

    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getBody() {

        return body;
    }

    public void setBody(String body) {

        this.body = body;
    }

    public boolean isRead() {

        return read;
    }

    public void markAsRead(boolean read) {

        this.read = read;
    }

    public Transaction getCause() {

        return cause;
    }

    public void setCause(Transaction cause) {

        this.cause = cause;
    }

    public void addTag(String tag) {

        tags.add(tag);
    }

    public Set<String> getTags() {

        return Collections.unmodifiableSet(tags);
    }


    @Override
    public int hashCode() {

        int result = 17;
        result = result + 37 * title.hashCode();
        result = result + 37 * cause.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (!(obj instanceof Issue))
            return false;

        final Issue i = (Issue) obj;
        return title.equals(i.title) && cause.equals(i.cause);
    }
}
