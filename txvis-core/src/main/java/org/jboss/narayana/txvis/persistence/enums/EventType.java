package org.jboss.narayana.txvis.persistence.enums;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 23:02
 */
public enum EventType {
    BEGIN,
    ENLIST,
    PREPARE,
    PREPARE_FAILED,
    PREPARE_OK,
    COMMIT,
    ABORT,
    HEURISTIC_COMMIT,
    HEURISTIC_ROLLBACK,
    HEURISTIC_MIXED,
    HEURISTIC_HAZARD,
    FINISH_OK,
    FINISH_ERROR,
    REPLAY_PHASE2;
}
