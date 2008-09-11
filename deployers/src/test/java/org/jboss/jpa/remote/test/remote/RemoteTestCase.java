/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jpa.remote.test.remote;

import java.io.IOException;
import java.rmi.MarshalledObject;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.jpa.deployers.test.common.DerbyService;
import org.jboss.jpa.remote.RemotelyInjectEntityManagerFactory;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;
import org.jboss.metadata.jpa.spec.TransactionType;
import org.jnp.server.SingletonNamingServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Note that this test doesn't really do remote, it uses serialization.
 * After deserialization care has to be taking not to use any of the
 * facilities that are already in the VM.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class RemoteTestCase
{
   private static SingletonNamingServer namingServer;

   private static DerbyService derbyService;
   
   // not generic in Java 5
   @SuppressWarnings("unchecked")
   private MarshalledObject mo;
   
   private InitialContext ctx;
   
   @AfterClass
   public static void afterClass() throws Exception
   {
      if(derbyService != null)
      {
         derbyService.stop();
         derbyService.destroy();
         derbyService = null;
      }
      
      if(namingServer != null)
      {
         namingServer.destroy();
         namingServer = null;
      }
   }

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      namingServer = new SingletonNamingServer();
      
      derbyService = new DerbyService();
      derbyService.setJndiName("remoteDS");
      
      derbyService.create();
      derbyService.start();
   }

   @SuppressWarnings("unchecked")
   @Before
   public void setup() throws IOException
   {
      PersistenceUnitMetaData metaData = new PersistenceUnitMetaData();
      metaData.setName("remotePU");
      metaData.setNonJtaDataSource("remoteDS");
      metaData.setTransactionType(TransactionType.RESOURCE_LOCAL);
      
      RemotelyInjectEntityManagerFactory factory = new RemotelyInjectEntityManagerFactory(metaData, "dummy");
      
      mo = new MarshalledObject(factory);
   }
   
   @Test
   public void test1() throws IOException, ClassNotFoundException
   {
      EntityManagerFactory factory = (EntityManagerFactory) mo.get();
      
      EntityManager em = factory.createEntityManager();
      
      em.close();
   }

   
   @Test
   public void test2() throws IOException, ClassNotFoundException
   {
      EntityManagerFactory factory = (EntityManagerFactory) mo.get();
      
      EntityManager em = factory.createEntityManager();
      
      Person person = new Person();
      em.persist(person);
      
      em.close();
   }
}
