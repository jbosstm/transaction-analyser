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
