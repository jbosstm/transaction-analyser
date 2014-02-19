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
package io.narayana.nta.webapp.models;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

/**
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
@SessionScoped
@Named
public class TraceLoggingBean implements Serializable {
    private boolean traceLoggingEnable = false;
    private ModelControllerClient client = null;
    private String rotatingFileLogging;
    private String arjunaLogging;

    @PostConstruct
    public void init() {
        try {
            client = ModelControllerClient.Factory.create(InetAddress.getByName("localhost"), 9999);
            ModelNode op = new ModelNode();
            op.get("operation").set("read-attribute");
            op.get("address").add("subsystem", "logging").add("periodic-rotating-file-handler", "FILE");
            op.get("name").set("level");

            ModelNode ret;
            try {
                ret = client.execute(op);
            } catch (java.io.IOException e) {
                client.close();
                client = ModelControllerClient.Factory.create("http-remoting", InetAddress.getByName("localhost"), 9990);
                ret = client.execute(op);
            }
            rotatingFileLogging = ret.get("result").toString().replaceAll("\"", "");

            op = new ModelNode();
            op.get("operation").set("read-attribute");
            op.get("address").add("subsystem", "logging").add("logger", "com.arjuna");
            op.get("name").set("level");

            ret = client.execute(op);
            arjunaLogging = ret.get("result").toString().replaceAll("\"", "");

            traceLoggingEnable = rotatingFileLogging.equals("TRACE") && arjunaLogging.equals("TRACE");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        if(client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStatus() {
        return traceLoggingEnable ? "Stop" : "Start";
    }

    public boolean getTraceLoggingEnable() {
        return ! traceLoggingEnable;
    }

    public void setTraceLoggingEnable(boolean enable) {
        try {
            setTraceLogging(enable);
            traceLoggingEnable = enable;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTraceLogging(boolean enable) throws Exception {
        ModelNode op = new ModelNode();

        op.get("operation").set("write-attribute");
        op.get("address").add("subsystem", "logging").add("periodic-rotating-file-handler", "FILE");
        op.get("name").set("level");
        if(!enable) {
            op.get("value").set(rotatingFileLogging.equals("TRACE") ? "INFO" : rotatingFileLogging);
        } else {
            op.get("value").set("TRACE");
        }
        client.execute(op);

        op = new ModelNode();
        op.get("operation").set("write-attribute");
        op.get("address").add("subsystem", "logging").add("logger", "com.arjuna");
        op.get("name").set("level");
        if(!enable) {
            op.get("value").set(arjunaLogging.equals("TRACE") ? "INFO" : arjunaLogging);
        } else {
            op.get("value").set("TRACE");
        }
        client.execute(op);
    }

}
