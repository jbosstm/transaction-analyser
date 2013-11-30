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
