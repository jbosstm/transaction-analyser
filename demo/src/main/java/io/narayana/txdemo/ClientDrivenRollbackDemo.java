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

import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
public class ClientDrivenRollbackDemo extends Demo {

    public ClientDrivenRollbackDemo() {

        super(4, "Client-driven Rollback", "This demo enlists three resources. Two are dummies and the third is a database. " +
                "The business logic calls rollback and the transaction outcome is PHASE_ONE_ABORT");
    }

    @Override
    public DemoResult run(TransactionManager tm, EntityManager em) throws Exception {

        tm.begin();

        tm.getTransaction().enlistResource(new DemoDummyXAResource("demo1"));
        tm.getTransaction().enlistResource(new DemoDummyXAResource("demo2"));
        create(em, "client_rollback");

        tm.rollback();

        return new DemoResult(0, "client rollback");
    }
}
