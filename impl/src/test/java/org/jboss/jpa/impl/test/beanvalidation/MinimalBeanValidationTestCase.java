package org.jboss.jpa.impl.test.beanvalidation;

import java.sql.DriverManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.net.URL;
import javax.transaction.TransactionManager;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.hsqldb.jdbcDriver;
import org.jnp.server.SingletonNamingServer;
import org.jboss.jpa.impl.test.common.MockPersistenceUnit;
import org.jboss.jpa.impl.test.common.Person;
import org.jboss.jpa.impl.deployment.PersistenceUnitInfoImpl;
import org.jboss.jpa.spi.PersistenceUnitRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.transaction.JBossTransactionManagerLookup;
import com.arjuna.ats.jta.utils.JNDIManager;

/**
 * @author Emmanuel Bernard
 */
public class MinimalBeanValidationTestCase
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
   }

   @Test
   //This does *not* test the proper creation and injection of BV as we use a mock persistenceunit
   //in fact BV is initialized by Hibernate in this case
   public void testBeanValidation() throws Exception
   {
      transactionManager.begin();
      EntityManager em = persistenceUnit.getContainerEntityManagerFactory().createEntityManager();
      try
      {
         Person p = new Person();
         p.setId(2);
         p.setName("Caroline");
         p.setNickname("C"); //invalid nickname
         em.persist(p);
         em.joinTransaction();
         em.flush();
         fail("Bean validation should have been triggered");
      }
      catch (ConstraintViolationException e)
      {
         //success
      }
      catch (PersistenceException e)
      {
         assertEquals("Should be a validation exception", e.getCause().getClass(), ConstraintViolationException.class);
      }
      finally
      {
         transactionManager.rollback();
         em.close();
      }
   }

}
