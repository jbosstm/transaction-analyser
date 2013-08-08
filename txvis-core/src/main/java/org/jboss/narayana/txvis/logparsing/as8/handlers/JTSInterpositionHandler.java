package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 05/07/2013
 * Time: 15:04
 */
public class JTSInterpositionHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "Interposition(?:Server|Client)RequestInterceptorImpl::" +
            "(?<METHOD>send_request|receive_request)\\s\\(\\screate\\s\\)\\snodeId=(?<NODEID>.*?)\\srequestId=(?<REQID>\\d+)" +
            ".*?(?<IOR>IOR:[A-Z0-9]+)";

    // InterpositionServerRequestInterceptorImpl::receive_request ( create ) nodeId=server2 requestId=100
    // InterpositionClientRequestInterceptorImpl::send_request ( create ) nodeId=server1 requestId=100

    public JTSInterpositionHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        switch (matcher.group("METHOD")) {
            case "send_request":
                service.checkIfParent(matcher.group("NODEID"), Long.parseLong(matcher.group("REQID")), matcher.group("IOR"));
                break;
            case "receive_request":
                service.associateThreadWithRequestId(matcher.group(THREAD_ID), Long.parseLong(matcher.group("REQID")),
                        matcher.group("IOR"));
                break;
        }
    }
}
