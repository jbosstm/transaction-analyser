package io.narayana.nta.restapi.services;

import io.narayana.nta.persistence.enums.Status;
import io.narayana.nta.restapi.models.Transaction.TransactionInfo;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 10/05/14
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionService
{
    public Collection<TransactionInfo> getTransactions();
    public Collection<TransactionInfo> getTransactions(Status status);
    public TransactionInfo getTransaction(Long id);
}
