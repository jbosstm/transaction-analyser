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

package io.narayana.nta.restapi.apis;

import io.narayana.nta.persistence.enums.Status;
import io.narayana.nta.restapi.models.response.PayloadResponse;
import io.narayana.nta.restapi.models.transaction.TransactionInfo;
import io.narayana.nta.restapi.services.TransactionService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 04/05/14
 * Time: 17:52
 */

public class TransactionAPIImpl implements TransactionAPI{
    @Inject
    private TransactionService transactionService;

    @Override
    public Response getTransactions(String status) {
        Collection<TransactionInfo> payload;

        if (status == null) {
            payload = transactionService.get();
        } else {
            Status requestedTransactionStatus = Status.valueOf(status.toUpperCase());
            payload = transactionService.getByStatus(requestedTransactionStatus);
        }

        if ((payload == null) || (payload != null && payload.size() == 0)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setStatus(Response.Status.OK);
        payloadResponse.setPayload(payload);
        return Response.ok(payloadResponse).build();
    }

    @Override
    public Response getTransactionById(@NotNull Long id) {
        Object payload = transactionService.getById(id);

        if (payload == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setStatus(Response.Status.OK);
        payloadResponse.setPayload(payload);
        return Response.ok(payloadResponse).build();
    }
}
