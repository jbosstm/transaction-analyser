package org.jboss.narayana.txvis.test.utils;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 31/05/2013
 * Time: 18:27
 */
public class DummyXAResourceWrapper implements XAResource {

    private XAResource xaResource;
    private String jndiName;
    private String productName = "Dummy Product";
    private String productVersion = "1.0.0";

    public DummyXAResourceWrapper(XAResource xaResource, String jndiName) {
        this.xaResource = xaResource;
        this.jndiName = jndiName;
    }

    public XAResource getResource() {
        return xaResource;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public String getJndiName() {
        return jndiName;
    }

    @Override
    public void commit(Xid xid, boolean b) throws XAException {
        xaResource.commit(xid, b);
    }

    @Override
    public void end(Xid xid, int i) throws XAException {
        xaResource.end(xid, i);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        xaResource.forget(xid);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return xaResource.getTransactionTimeout();
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {
        return this.xaResource.isSameRM(xaResource);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return xaResource.prepare(xid);
    }

    @Override
    public Xid[] recover(int i) throws XAException {
        return xaResource.recover(i);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        xaResource.rollback(xid);
    }

    @Override
    public boolean setTransactionTimeout(int i) throws XAException {
        return xaResource.setTransactionTimeout(i);
    }

    @Override
    public void start(Xid xid, int i) throws XAException {
        xaResource.start(xid, i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("XAResourceWrapperImpl@").append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("[xaResource=").append(getResource());
        sb.append(" pad=").append("false");
        sb.append(" overrideRmValue=").append("false");
        sb.append(" productName=").append(getProductName());
        sb.append(" productVersion=").append(getProductVersion());
        sb.append(" jndiName=").append(getJndiName());
        sb.append("]");
        return sb.toString();
    }
}
