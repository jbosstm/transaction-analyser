package org.jboss.narayana.txvis.plugins;

import org.jboss.narayana.txvis.persistence.entities.Transaction;

import javax.persistence.*;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Issue() {}

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

        if(!(obj instanceof Issue))
            return false;

        final Issue i = (Issue) obj;
        return title.equals(i.title) && cause.equals(i.cause);
    }
}
