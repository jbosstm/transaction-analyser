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
package io.narayana.txdemo;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.io.Serializable;

/**
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
public class DemoDummyXAResource implements XAResource, Serializable {

    public enum faultType {TIMEOUT, PREPARE_FAIL, NONE}

    ;

    private String name;

    private faultType fault = faultType.NONE;

    public DemoDummyXAResource(String name) {

        this(name, faultType.NONE);
    }

    public DemoDummyXAResource(String name, faultType fault) {

        this.name = name;
        this.fault = fault;
    }

    @Override
    public void commit(Xid xid, boolean b) throws XAException {

        if (fault == faultType.TIMEOUT) throw new XAException(XAException.XA_RBTIMEOUT);
    }

    @Override
    public void end(Xid xid, int i) throws XAException {

    }

    @Override
    public void forget(Xid xid) throws XAException {

    }

    @Override
    public int getTransactionTimeout() throws XAException {

        return 0;
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {

        return this.equals(xaResource);
    }

    @Override
    public int prepare(Xid xid) throws XAException {

        if (fault == faultType.PREPARE_FAIL) {
            throw new XAException(XAException.XAER_RMFAIL);
        }
        return XAResource.XA_OK;
    }

    @Override
    public Xid[] recover(int i) throws XAException {

        return null;
    }

    @Override
    public void rollback(Xid xid) throws XAException {

    }

    @Override
    public boolean setTransactionTimeout(int timeout) throws XAException {

        return false;
    }

    @Override
    public void start(Xid xid, int i) throws XAException {

    }

    @Override
    public String toString() {

        return "XAResourceWrapperImpl@[xaResource=" + super.toString() + " pad=false overrideRmValue=null productName=" + name + " productVersion=1.0 jndiName=java:jboss/" + name + "]";
    }
}
