package org.jboss.narayana.txvis.test.utils;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * @author paul.robinson@redhat.com 21/01/2013
 */
public class DummyXAResource implements XAResource {

    private int timeout = 0;

    String resourceName;

    boolean voteToPrepare = true;

    public DummyXAResource(String resourceName) {
        this.resourceName = resourceName;
    }

    public DummyXAResource(String resourceName, boolean voteToPrepare) {
        this.resourceName = resourceName;
        this.voteToPrepare = voteToPrepare;
    }

    public void commit(Xid xid, boolean b) throws XAException {
        System.out.println(resourceName + ":commit:" + xid + ":" + b);
    }

    public void end(Xid xid, int i) throws XAException {
        System.out.println(resourceName + ":end:" + xid + ":" + i);
    }

    public void forget(Xid xid) throws XAException {
        System.out.println(resourceName + ":forget:" + xid);
    }

    public int getTransactionTimeout() throws XAException {
        System.out.println(resourceName + ":getTransactionTimeout");
        return timeout;
    }

    public boolean isSameRM(XAResource xaResource) throws XAException {
        System.out.println(resourceName + ":isSameRM");
        return false;
    }

    public int prepare(Xid xid) throws XAException {
        System.out.println(resourceName + ":prepare:" + xid);
        if (voteToPrepare) {
            return XA_OK;
        } else {
            throw new XAException(XAException.XAER_RMERR);
        }
    }

    public Xid[] recover(int i) throws XAException {
        System.out.println(resourceName + ":recover:" + i);
        return new Xid[0];
    }

    public void rollback(Xid xid) throws XAException {
        System.out.println(resourceName + ":rollback:" + xid);
    }

    public boolean setTransactionTimeout(int i) throws XAException {
        System.out.println(resourceName + ":setTransactionTimeout:" + i);
        timeout = i;
        return true;
    }

    public void start(Xid xid, int i) throws XAException {
        System.out.println(resourceName + ":start:" + xid + ":" + i);
    }

    @Override
    public String toString() {

        return null;
    }


}
