package io.narayana.nta.restapi.services;

import io.narayana.nta.persistence.enums.Status;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 10/05/14
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionService
{
    public void getTopLevelTransactions();
    public void getTopLevelTransactions(Status status);
    public void getLevelTransaction(String txID);
    public void getTopLevelTransaction(String txUID);
}
