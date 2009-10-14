/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jpa.impl.test.tx.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.transaction.JBossTransactionManagerLookup;
import org.hsqldb.jdbcDriver;
import org.jboss.jpa.impl.deployment.PersistenceUnitInfoImpl;
import org.jboss.jpa.impl.test.common.MockPersistenceUnit;
import org.jboss.jpa.impl.test.common.Person;
import org.jboss.jpa.impl.tx.TransactionScopedEntityManager;
import org.jboss.jpa.spi.PersistenceUnitRegistry;
import org.jnp.server.SingletonNamingServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.arjuna.ats.jta.utils.JNDIManager;

/**
 * EJB 3.0 Persistence 5.6.1:
 * If the entity manager is invoked outside the scope of a transaction, any entities loaded from the database
 * will immediately become detached at the end of the method call.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class TxScopedTestCase
{
   private static jdbcDriver driver;
   private static SingletonNamingServer namingServer;
   private static TransactionManager transactionManager;
   
   private MockPersistenceUnit persistenceUnit;

   @AfterClass
   public static void afterClass() throws Exception
   {
      new InitialContext().unbind("java:/TransactionManager");
      
      namingServer.destroy();
      
      DriverManager.deregisterDriver(driver);
   }
   
   @BeforeClass
   public static void beforeClass() throws Exception
   {
      driver = new jdbcDriver();
      
      DriverManager.registerDriver(driver);
      
      namingServer = new SingletonNamingServer();
      
      JNDIManager.bindJTAImplementation();
      
      transactionManager = (TransactionManager) new InitialContext().lookup("java:/TransactionManager");
   }
   
   @After
   public void after() throws Exception
   {
      PersistenceUnitRegistry.unregister(persistenceUnit);
      
      persistenceUnit.stop();
   }
   
   @Before
   public void before() throws Exception
   {
      List<String> entityClassNames = new ArrayList<String>();
      entityClassNames.add(Person.class.getName());
      
      PersistenceUnitInfoImpl pui = new PersistenceUnitInfoImpl();
      pui.setClassLoader(Thread.currentThread().getContextClassLoader());
      List<URL> jarFiles = new ArrayList<URL>();
      pui.setJarFiles(jarFiles);
      pui.setManagedClassnames(entityClassNames);
      List<String> mappingFileNames = new ArrayList<String>();
      pui.setMappingFileNames(mappingFileNames);
      pui.setPersistenceProviderClassName(HibernatePersistence.class.getName());
      pui.setPersistenceUnitName("TestPersistenceUnit");
      Properties properties = new Properties();
      properties.put("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
      properties.put("hibernate.connection.user", "sa");
      properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
      properties.put("hibernate.hbm2ddl.auto", "create");
      //properties.put("hibernate.transaction.manager_lookup_class", MockTransactionManagerLookup.class.getName());
      properties.put("hibernate.transaction.manager_lookup_class", JBossTransactionManagerLookup.class.getName());
      pui.setProperties(properties);
      
      MockPersistenceUnit persistenceUnit = new MockPersistenceUnit(pui);
      this.persistenceUnit = persistenceUnit;
      persistenceUnit.start();
      
      PersistenceUnitRegistry.register(persistenceUnit);
      
      EntityManager em = persistenceUnit.getContainerEntityManagerFactory().createEntityManager();
      Person person = new Person();
      person.setId(1);
      person.setName("Test");
      em.persist(person);
      transactionManager.begin();
      em.joinTransaction();
      em.flush();
      transactionManager.commit();      
   }
   
   @Test
   public void test1() throws Exception
   {
      EntityManager em = new TransactionScopedEntityManager(persistenceUnit);
      Person result = em.find(Person.class, 1);
      assertNotNull(result);
      assertFalse("entity should have been detached", em.contains(result));
   }

   @Test
   @Ignore("JPA-2.0 functions are not yet implemented")
   public void test2() throws Exception
   {
      EntityManager em = new TransactionScopedEntityManager(persistenceUnit);
      Map<String, Object> props = new HashMap<String, Object>();
      Person result = em.find(Person.class, 1, props);
      assertNotNull("failed to find person 1", result);
      assertFalse("entity should have been detached", em.contains(result));
   }
}
