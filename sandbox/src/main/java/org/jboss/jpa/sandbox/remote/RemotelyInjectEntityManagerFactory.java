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
package org.jboss.jpa.sandbox.remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.jpa.deployment.PersistenceUnitInfoImpl;
import org.jboss.logging.Logger;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;
import org.jboss.metadata.jpa.spec.TransactionType;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class RemotelyInjectEntityManagerFactory implements EntityManagerFactory, Serializable
{
   private static final Logger log = Logger.getLogger(RemotelyInjectEntityManagerFactory.class);
   
   private static final long serialVersionUID = 1L;

   private PersistenceUnitMetaData metaData;
   
   private EntityManagerFactory actualFactory;
   private Properties defaultPersistenceProperties = new Properties();
   private List<String> explicitEntityClasses = new ArrayList<String>();
   private String jaccContextId;
   
   /**
    * Assume the data source name has been changed to a remote data source name.
    */
   public RemotelyInjectEntityManagerFactory(PersistenceUnitMetaData metaData, String jaccContextId)
   {
      assert metaData != null : "metaData is null";
      this.metaData = metaData;
      this.jaccContextId = jaccContextId;
   }
   
   public void close()
   {
      throw new RuntimeException("NYI");
   }

   private void createEntityManagerFactory() throws Exception
   {
      Properties props = new Properties();
      props.putAll(defaultPersistenceProperties);
      props.put(HibernatePersistence.JACC_CONTEXT_ID, getJaccContextId());

      PersistenceUnitInfoImpl pi = new PersistenceUnitInfoImpl();
//      log.debug("Using class loader " + di.getClassLoader());
//      pi.setClassLoader(di.getClassLoader());
      pi.setClassLoader(Thread.currentThread().getContextClassLoader());

      ArrayList<URL> jarFiles = new ArrayList<URL>();
      pi.setJarFiles(jarFiles);
      pi.setPersistenceProviderClassName(HibernatePersistence.class.getName());
      log.debug("Found persistence.xml file in EJB3 jar");
      props.putAll(getProperties());
      pi.setManagedClassnames(safeList(metaData.getClasses()));
      pi.setPersistenceUnitName(metaData.getName());
      pi.setMappingFileNames(safeList(metaData.getMappingFiles()));
      pi.setExcludeUnlistedClasses(metaData.isExcludeUnlistedClasses());
      /*
      VirtualFile root = getPersistenceUnitRoot();
      log.debug("Persistence root: " + root);
      // TODO - update this with VFSUtils helper method
      // hack the JPA url
      URL url = root.toURL();
      // is not nested, so direct VFS URL is not an option
      if (VFSUtils.isNestedFile(root) == false)
      {
         String urlString = url.toExternalForm();
         if (urlString.startsWith("vfs"))
         {
            // treat vfszip as file
            if (urlString.startsWith("vfszip"))
               url = new URL("file" + urlString.substring(6));
            else
               url = new URL(urlString.substring(3)); // (vfs)file and (vfs)jar are ok
         }
      }
      */
      URL url = getPersistenceUnitRoot();
      log.debug("Persistence root: " + url);
      pi.setPersistenceUnitRootUrl(url);
      PersistenceUnitTransactionType transactionType = getJPATransactionType();
      pi.setTransactionType(transactionType);

      Set<String> files = metaData.getJarFiles();
      if (files != null)
      {
         for (String jar : files)
         {
            jarFiles.add(getRelativeURL(jar));
         }
      }

      if (metaData.getProvider() != null) pi.setPersistenceProviderClassName(metaData.getProvider());
      if (explicitEntityClasses.size() > 0)
      {
         List<String> classes = pi.getManagedClassNames();
         if (classes == null) classes = explicitEntityClasses;
         else classes.addAll(explicitEntityClasses);
         pi.setManagedClassnames(classes);
      }
      if (metaData.getJtaDataSource() != null)
      {
         pi.setJtaDataSource((javax.sql.DataSource) lookup(metaData.getJtaDataSource()));
      }
      else if (transactionType == PersistenceUnitTransactionType.JTA)
      {
         throw new RuntimeException("Specification violation [EJB3 JPA 6.2.1.2] - "
               + "You have not defined a jta-data-source for a JTA enabled persistence context named: " + metaData.getName());
      }
      if (metaData.getNonJtaDataSource() != null)
      {
         pi.setNonJtaDataSource((javax.sql.DataSource) lookup(metaData.getNonJtaDataSource()));
      }
      else if (transactionType == PersistenceUnitTransactionType.RESOURCE_LOCAL)
      {
         throw new RuntimeException("Specification violation [EJB3 JPA 6.2.1.2] - "
               + "You have not defined a non-jta-data-source for a RESOURCE_LOCAL enabled persistence context named: "
               + metaData.getName());
      }
      pi.setProperties(props);

      if (pi.getPersistenceUnitName() == null)
      {
         throw new RuntimeException("you must specify a name in persistence.xml");
      }

      Class<?> providerClass = Thread.currentThread().getContextClassLoader().loadClass(pi.getPersistenceProviderClassName());

      // EJBTHREE-893
      /* TODO: can this work remotely?
      if(!pi.getProperties().containsKey("hibernate.session_factory_name"))
      {
         pi.getProperties().put("hibernate.session_factory_name", kernelName);
      }
      */
      
      PersistenceProvider pp = (PersistenceProvider) providerClass.newInstance();
      actualFactory = pp.createContainerEntityManagerFactory(pi, null);      
   }
   
   public EntityManager createEntityManager()
   {
      return actualFactory.createEntityManager();
   }

   public EntityManager createEntityManager(@SuppressWarnings("unchecked") Map map)
   {
      return actualFactory.createEntityManager(map);
   }

   protected String getJaccContextId()
   {
      //return di.getSimpleName();
      return jaccContextId;
   }

   protected PersistenceUnitTransactionType getJPATransactionType()
   {
      TransactionType type = metaData.getTransactionType();
      if (type == TransactionType.RESOURCE_LOCAL)
         return PersistenceUnitTransactionType.RESOURCE_LOCAL;
      else // default or actually being JTA
         return PersistenceUnitTransactionType.JTA;
   }

   /**
    * Find the persistence unit root, which can be the root of a jar
    * or WEB-INF/classes of a war.
    * @return the persistence unit root
    */
   protected URL getPersistenceUnitRoot() throws IOException
   {
      // FIXME: What is the correct way to find the persistence unit root?
      List<URL> list = new ArrayList<URL>();
      Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources("META-INF/persistence.xml");
      while(e.hasMoreElements())
         list.add(e.nextElement());
//      if(list.size() > 1)
//         throw new RuntimeException("Can't handle more than 1 persistence unit on the class path, found " + list);
      if(list.size() > 1)
         log.warn("Found multiple persistence units on the classpath, will use the first one of " + list);
      if(list.size() == 0)
         throw new IllegalStateException("Can't find META-INF/persistence.xml");
      URL url = list.get(0);
      String spec = url.toExternalForm();
      spec = spec.substring(0, spec.length() - "META-INF/persistence.xml".length());
      return new URL(spec);
      /*
      try
      {
         VirtualFile metaData = di.getMetaDataFile("persistence.xml");
         assert metaData != null : "Can't find persistence.xml in " + di;
         return metaData.getParent().getParent();
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
      */
   }
   
   protected Map<String, String> getProperties()
   {
      Map<String, String> properties = metaData.getProperties();
      return (properties != null) ? properties : Collections.<String, String>emptyMap();
   }

   private URL getRelativeURL(String jar)
   {
      /*
      try
      {
         return new URL(jar);
      }
      catch (MalformedURLException e)
      {
         try
         {
            URL url = di.getFile("").toURL();
            return new URL(url, jar);
         }
         catch (Exception e1)
         {
            throw new RuntimeException("could not find relative path: " + jar, e1);
         }
      }
      */
      throw new RuntimeException("NYI");
   }
   
   private static Object lookup(String name) throws NamingException
   {
      InitialContext ctx = new InitialContext();
      try
      {
         return ctx.lookup(name);
      }
      finally
      {
         ctx.close();
      }
   }
   
   public boolean isOpen()
   {
      throw new RuntimeException("NYI");
   }
   
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      System.err.println("readObject");
      in.defaultReadObject();
      
      try
      {
         createEntityManagerFactory();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   private static List<String> safeList(Set<String> set)
   {
      return (set == null || set.isEmpty()) ? Collections.<String>emptyList() : new ArrayList<String>(set);
   }

   private void writeObject(ObjectOutputStream out) throws IOException
   {
      System.err.println("writeObject");
      out.defaultWriteObject();
   }
}
