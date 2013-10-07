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

package io.narayana.txprof.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 05/07/2013
 * Time: 17:11
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "RequestRecord.findByRequestId",
                query = "FROM RequestRecord r WHERE r.requestid=:requestid"
        ),
        @NamedQuery(name = "RequestRecord.findByNodeIdAndTxUID",
                query = "FROM RequestRecord r WHERE r.nodeid=:nodeid AND r.txuid=:txuid"
        ),
        @NamedQuery(name = "RequestRecord.findByRequestIdAndTxUID",
                query = "FROM RequestRecord r WHERE r.requestid=:requestid AND r.txuid=:txuid"
        ),
        @NamedQuery(name = "RequestRecord.findByRequestIdAndIOR",
                query = "FROM RequestRecord r where r.requestid=:requestid AND r.ior=:ior"
        )
})
@IdClass(RequestRecord.CompositePK.class)
public class RequestRecord implements Serializable {

    @Id
    private Long requestid;

    @Id
    @Column(columnDefinition = "varchar(750)")
    private String ior;

    @Column(nullable = false)
    private String nodeid;

    @Column(nullable = true)
    private String txuid;


    protected RequestRecord() {

    }

    public RequestRecord(Long requestid, String nodeid, String ior) {

        this.nodeid = nodeid;
        this.requestid = requestid;
        this.ior = ior;
    }

    public RequestRecord(Long requestid, String nodeid, String ior, String txuid) {

        this.requestid = requestid;
        this.nodeid = nodeid;
        this.txuid = txuid;
        this.ior = ior;
    }


    public String getNodeid() {

        return nodeid;
    }

    public Long getRequestid() {

        return requestid;
    }

    public String getTxuid() {

        return txuid;
    }

    public void setTxuid(String txuid) {

        this.txuid = txuid;
    }

    public String getIor() {

        return ior;
    }

    @Override
    public String toString() {

        return "RequestRecord: nodeId=" + nodeid + ", requestId=" + requestid + ", ior=" + ior + ", txuid=" + txuid;
    }


    /*
     * Implements the composite key for RequestRecord. This is used to enforce
     * the uniqueness constraint that no two RequestRecords should have identical
     * requestid and ior. This allows us to handle the race condition where
     * multiple
     */
    public static class CompositePK implements Serializable {

        private Long requestid;
        private String ior;


        public CompositePK() {

        }

        public CompositePK(Long requestid, String ior) {

            this.requestid = requestid;
            this.ior = ior;
        }

        public Long getRequestId() {

            return requestid;
        }

        public void setRequestId(Long requestId) {

            this.requestid = requestId;
        }

        public String getIor() {

            return ior;
        }

        public void setIor(String ior) {

            this.ior = ior;
        }

        @Override
        public boolean equals(Object o) {

            if (o == this)
                return true;

            if (!(o instanceof CompositePK))
                return false;

            final CompositePK rec = (CompositePK) o;

            return this.requestid.equals(rec.requestid) && this.ior.equals(rec.ior);
        }

        @Override
        public int hashCode() {

            int result = 17 + requestid.hashCode();
            result = 31 * result + ior.hashCode();
            return result;
        }
    }
}
