package io.narayana.nta.restapi.services;

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
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 14/05/14
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
@SessionScoped
@Named
public class TraceLoggingServiceImpl implements TraceLoggingService, Serializable
{
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

    @Override
    public boolean getTraceLoggingEnable()
    {
        return traceLoggingEnable;
    }

    @Override
    public void setTraceLoggingEnable(boolean enable)
    {
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
