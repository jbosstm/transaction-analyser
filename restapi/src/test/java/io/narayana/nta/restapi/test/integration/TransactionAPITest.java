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

package io.narayana.nta.restapi.test.integration;


import io.narayana.nta.restapi.apis.TransactionAPI;
import io.narayana.nta.restapi.models.transaction.TransactionInfo;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.Collection;

/**
 * @Author Palahepitiya Gamage Amila Prabandhika &lt;amila_fiz@hotmail.com$gt;
 * Date: 8/2/2014
 * Time: 12:08 AM
 */
@WarpTest
@RunWith(Arquillian.class)
public class TransactionAPITest {

    @ArquillianResource
    private URL contextPath;

    private TransactionAPI transactionAPI;

    @Deployment
    @OverProtocol("Servlet 3.0")
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts, org.jboss.as.controller-client, org.jboss.dmr\n";

        return ShrinkWrap.create(WebArchive.class, "testapi.war")
                .addPackages(true, "io.narayana.nta")
                .addPackages(true, "io.narayana.nta.restapi")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")),
                        "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/nta-test-ds.xml")), "nta-test-ds.xml")
                .addAsLibraries(new File("D:/Projects/TransactionAnalyser/transaction-analyser/etc/lib/commons-io-2.5-SNAPSHOT.jar"))
                .setManifest(new StringAsset(ManifestMF));
    }

    @BeforeClass
    public static void setUpClass() {

        // initializes the rest easy client framework
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @Before
    public void setUp() {

/*        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(contextPath + "rest");

        transactionAPI = target.proxy(TransactionAPI.class);*/

        transactionAPI = ProxyFactory.create(TransactionAPI.class, contextPath + "api/v1/transaction");
    }

    @Test
    @RunAsClient
    public void testTransactionGetByStatusWarp(){

        Warp.initiate(new Activity(){

            @Override
            public void perform(){

                Response response = transactionAPI.getTransactions("COMMIT");
                Collection<TransactionInfo> result = response.readEntity(Collection.class);
                Assert.assertNotNull(result);
            }

        }).inspect(new Inspection() {

            @ArquillianResource
            private RestContext restContext;

            @AfterServlet
        public void testTransactionGetByStatus(){
                Assert.assertEquals(HttpMethod.GET, restContext.getHttpRequest().getMethod());
            }

        });

    }

}
