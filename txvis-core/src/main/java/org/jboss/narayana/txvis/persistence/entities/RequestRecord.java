package org.jboss.narayana.txvis.persistence.entities;

import javax.persistence.*;
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


    protected RequestRecord() {}

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
        return "RequestRecord: nodeId="+nodeid+", requestId="+requestid+", ior="+ior+", txuid="+txuid;
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


        public CompositePK() {}

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
