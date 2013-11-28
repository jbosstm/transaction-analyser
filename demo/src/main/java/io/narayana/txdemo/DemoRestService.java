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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.TransactionManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
@Path("/demos")
@Stateless
public class DemoRestService {

    private ArrayList<Demo> demos = new ArrayList<>();

    @EJB
    private DemoDao dao;

    @Resource(lookup = "java:jboss/TransactionManager")
    private TransactionManager tm;

    public DemoRestService() {

        demos.add(new SuccessTransactionDemo());
        demos.add(new TimeoutTransactionDemo());
        demos.add(new PrepareFailDemo());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Demo> listAllDemos() {

        return demos;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(value = TransactionAttributeType.NEVER)
    public DemoResult getDemo(@PathParam("id") int id) {

        for (Demo demo : demos) {
            if (demo.getId() == id) {
                try {
                    return demo.run(tm, dao);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new DemoResult(-2, "exception " + e);
                }
            }
        }
        return new DemoResult(-1, "no " + id + " demo");
    }
}
