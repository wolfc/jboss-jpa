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
package org.jboss.jpa.impl.remote;

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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.jpa.impl.deployment.PersistenceUnitInfoImpl;
import org.jboss.logging.Logger;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;
import org.jboss.metadata.jpa.spec.TransactionType;

/**
 * EXPERIMENTAL
 * 
 * Allows a persistence unit to go over the wire. It is assumed that the same
 * persistence unit jar is available locally. The jar is then used to boot
 * up JPA locally.
 * 
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
      log.debug("Booting up the entity manager factory");
      
      Properties props = new Properties();
      props.putAll(defaultPersistenceProperties);
      props.put(HibernatePersistence.JACC_CONTEXT_ID, getJaccContextId());

      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      
      // TODO:
      List<URL> jarFiles = new ArrayList<URL>();
      
      InitialContext ctx = new InitialContext();
      
      PersistenceUnitInfoImpl pi = new PersistenceUnitInfoImpl(metaData, props, classLoader, getPersistenceUnitRoot(), jarFiles, ctx);

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
   
   public boolean isOpen()
   {
      throw new RuntimeException("NYI");
   }
   
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
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
      log.trace("writeObject");
      out.defaultWriteObject();
   }
}
