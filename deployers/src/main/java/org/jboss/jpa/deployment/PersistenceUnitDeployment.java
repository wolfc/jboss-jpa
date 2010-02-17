/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jpa.deployment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.builder.CEMFBuilder;
import org.jboss.jpa.impl.deployment.PersistenceUnitInfoImpl;
import org.jboss.jpa.injection.InjectedEntityManagerFactory;
import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.spi.PersistenceUnitRegistry;
import org.jboss.jpa.spi.XPCResolver;
import org.jboss.jpa.tx.TransactionScopedEntityManager;
import org.jboss.logging.Logger;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;
import org.jboss.util.naming.NonSerializableFactory;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public abstract class PersistenceUnitDeployment //extends AbstractJavaEEComponent
   implements PersistenceUnit
{
   private static final Logger log = Logger.getLogger(PersistenceUnitDeployment.class);

   protected InitialContext initialContext;
   protected VFSDeploymentUnit di;
   protected List<String> explicitEntityClasses = new ArrayList<String>();
   protected ManagedEntityManagerFactory managedFactory;
   protected EntityManagerFactory actualFactory;
   protected PersistenceUnitMetaData metaData;
   protected String kernelName;
   protected PersistenceDeployment deployment;
   private Properties defaultPersistenceProperties;
   private CEMFBuilder cemfBuilder;
   private XPCResolver xpcResolver;

   public PersistenceUnitDeployment(InitialContext initialContext, PersistenceDeployment deployment, List<String> explicitEntityClasses, PersistenceUnitMetaData metadata, String kernelName, VFSDeploymentUnit deploymentUnit, Properties defaultPersistenceProperties)
   {
      //super(new SimpleJavaEEModule((deployment.getEar() != null ? deployment.getEar().getShortName() : null), deployment.getDeploymentUnit().getShortName()));
      
      this.deployment = deployment;
      this.initialContext = initialContext;
      this.di = deploymentUnit;
      this.explicitEntityClasses = explicitEntityClasses;
      this.metaData = metadata;
      this.defaultPersistenceProperties = defaultPersistenceProperties;
      
      this.kernelName = kernelName;
   }

   public void create()
   {
      assert xpcResolver != null : "xpcResolver wasn't set on " + this;
      
      // To allow for serializable objects to obtain a reference back
      PersistenceUnitRegistry.register(this);
   }
   
   public void destroy()
   {
      PersistenceUnitRegistry.unregister(this);
   }
   
   public static String getDefaultKernelName(String unitName)
   {
      int hashIndex = unitName.indexOf('#');
      if (hashIndex != -1)
      {
         String relativePath = unitName.substring(3, hashIndex);
         String name = unitName.substring(hashIndex + 1);
         return "persistence.units:jar=" + relativePath + "," + "unitName=" + name;
      }
      return "persistence.units:unitName=" + unitName;
   }

   public PersistenceDeployment getDeployment()
   {
      return deployment;
   }

   protected String getJaccContextId()
   {
      return di.getSimpleName();
   }

   public EntityManagerFactory getActualFactory()
   {
      return actualFactory;
   }

   public PersistenceUnitMetaData getXml()
   {
      return metaData;
   }

   public String getKernelName()
   {
      return kernelName;
   }

   public String getName()
   {
      return getKernelName();
   }

   /**
    * Find the persistence unit URL, which can be the root of a jar
    * or WEB-INF/classes of a war.
    * @return the persistence unit ULR
    */
   protected abstract URL getPersistenceUnitURL();
   
   public EntityManagerFactory getContainerEntityManagerFactory()
   {
      return actualFactory;
   }
   
   public String getEntityManagerName()
   {
      return metaData.getName();
   }

   public ManagedEntityManagerFactory getManagedFactory()
   {
      if(managedFactory == null)
         log.warn("managed factory is null, persistence unit " + kernelName + " has not yet been started");
      return managedFactory;
   }

   protected Map<String, String> getProperties()
   {
      Map<String, String> properties = metaData.getProperties();
      return (properties != null) ? properties : Collections.<String, String>emptyMap();
   }

   protected abstract URL getRelativeURL(String jar);

   
   public EntityManager getTransactionScopedEntityManager()
   {
      return managedFactory.getTransactionScopedEntityManager();
   }
   
   public XPCResolver getXPCResolver()
   {
      assert xpcResolver != null : "xpcResolver is null in " + this;  
      return xpcResolver;
   }
   
   public boolean isInTx()
   {
      return managedFactory.isInTx();
   }
   
   @Inject
   public void setCEMFBuilder(CEMFBuilder builder)
   {
      this.cemfBuilder = builder;
   }
   
   @Inject
   public void setXPCResolver(XPCResolver xpcResolver)
   {
      // Do not check for null, because MC does uninstall with null
      this.xpcResolver = xpcResolver;
   }
   
//   public void addDependencies(DependencyPolicy policy)
//   {
//      Map<String, String> props = getProperties();
//      if (!props.containsKey("jboss.no.implicit.datasource.dependency"))
//      {
//         if (metaData.getJtaDataSource() != null)
//         {
//            String ds = metaData.getJtaDataSource();
//            policy.addDatasource(ds);
//         }
//         if (metaData.getNonJtaDataSource() != null)
//         {
//            String ds = metaData.getNonJtaDataSource();
//            policy.addDatasource(ds);
//         }
//      }
//      for (Object prop : props.keySet())
//      {
//         String property = (String)prop;
//         if (property.startsWith("jboss.depends"))
//         {
//            policy.addDependency(props.get(property));
//         }
//      }
//
//   }

   public void start() throws Exception
   {
      log.info("Starting persistence unit " + kernelName);
      
      Properties props = new Properties();
      props.putAll(defaultPersistenceProperties);
      props.put(HibernatePersistence.JACC_CONTEXT_ID, getJaccContextId());

      List<URL> jarFiles = new ArrayList<URL>();
      Set<String> files = metaData.getJarFiles();
      if (files != null)
      {
         for (String jar : files)
         {
            jarFiles.add(getRelativeURL(jar));
         }
      }

      URL url = getPersistenceUnitURL();
      log.debug("Persistence url: " + url);
      PersistenceUnitInfoImpl pi = new PersistenceUnitInfoImpl(metaData, props, di.getClassLoader(), url, jarFiles, initialContext);

      if (explicitEntityClasses.size() > 0)
      {
         List<String> classes = pi.getManagedClassNames();
         if (classes == null) classes = explicitEntityClasses;
         else classes.addAll(explicitEntityClasses);
         pi.setManagedClassnames(classes);
      }
      
      // EJBTHREE-893
      if(!pi.getProperties().containsKey("hibernate.session_factory_name"))
      {
         pi.getProperties().put("hibernate.session_factory_name", kernelName);
      }
      
      // EJBTHREE-954/JBAS-6111
      // Ensure 2nd level cache entries are segregated from other deployments
      if (pi.getProperties().getProperty("hibernate.cache.region_prefix") == null)
      {
         pi.getProperties().setProperty("hibernate.cache.region_prefix", kernelName);
      }
      
      actualFactory = cemfBuilder.build(di, pi);

      managedFactory = new ManagedEntityManagerFactory(this);

      String entityManagerJndiName = (String) props.get("jboss.entity.manager.jndi.name");
      if (entityManagerJndiName != null)
      {
         EntityManager injectedManager = new TransactionScopedEntityManager(managedFactory);
         NonSerializableFactory.rebind(initialContext, entityManagerJndiName, injectedManager, true);
      }
      String entityManagerFactoryJndiName = (String) props.get("jboss.entity.manager.factory.jndi.name");
      if (entityManagerFactoryJndiName != null)
      {
         EntityManagerFactory injectedFactory = new InjectedEntityManagerFactory(managedFactory);
         NonSerializableFactory.rebind(initialContext, entityManagerFactoryJndiName, injectedFactory, true);
      }
   }

   public void stop() throws Exception
   {
      log.info("Stopping persistence unit " + kernelName);

      String entityManagerJndiName = getProperties().get("jboss.entity.manager.jndi.name");
      if (entityManagerJndiName != null)
      {
         unbind(entityManagerJndiName);
      }
      String entityManagerFactoryJndiName = getProperties().get("jboss.entity.manager.factory.jndi.name");
      if (entityManagerFactoryJndiName != null)
      {
         unbind(entityManagerFactoryJndiName);
      }
      actualFactory.close();
   }
   
   private void unbind(String name) throws NamingException
   {
      NonSerializableFactory.unbind(name);
      initialContext.unbind(name);
   }
   
   public void verifyInTx()
   {
      managedFactory.verifyInTx();
   }
}
