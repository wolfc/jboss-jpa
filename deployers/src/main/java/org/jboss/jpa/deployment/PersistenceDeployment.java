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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractConstructorMetaData;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.spi.PersistenceUnitRegistry;
import org.jboss.logging.Logger;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;

/**
 * An EjbModule represents a collection of beans that are deployed as a unit.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author adrian@jboss.org
 * @version $Revision$
 */
@Deprecated
public class PersistenceDeployment //extends ServiceMBeanSupport
//  implements JavaEEModule, Ejb3DeploymentMBean
{
   private static final Logger log = Logger.getLogger(PersistenceDeployment.class);

//   public static final String ACTUAL_ENTITY_MANAGER_FACTORY_CONTEXT = "java:/ActualEntityManagerFactories";
//
//   public static final String MANAGED_ENTITY_FACTORY_CONTEXT = "java:/managedEntityFactories";

   private PersistenceMetaData metaData;
   
   protected VFSDeploymentUnit unit;

//   protected LinkedHashMap<ObjectName, Container> ejbContainers = new LinkedHashMap<ObjectName, Container>();

   protected boolean hasEntities;

   protected List<String> explicitEntityClasses = new ArrayList<String>();

   protected List<PersistenceUnitDeployment> persistenceUnitDeployments = new ArrayList<PersistenceUnitDeployment>();

   protected InitialContext initialContext;

   private String earName;

   public PersistenceDeployment(VFSDeploymentUnit unit, String earName, PersistenceMetaData metaData)
   {
      assert unit != null : "unit is null";
      assert metaData != null : "metaData is null";
      
      this.unit = unit;
      this.earName = earName;
      this.metaData = metaData;
   }

//   public JavaEEApplication getApplication()
//   {
//      return deploymentScope;
//   }

   public VFSDeploymentUnit getDeploymentUnit()
   {
      return unit;
   }

   /**
    * Returns a partial MBean attribute name of the form
    * ",ear=foo.ear,jar=foo.jar"
    *
    * @return
    */
   public String getScopeKernelName()
   {
      String scopedKernelName = "";
      if (earName != null)
         scopedKernelName += ",ear=" + earName;
      scopedKernelName += ",jar=" + unit.getSimpleName();
      return scopedKernelName;
   }

   protected String getJaccContextId()
   {
      return unit.getSimpleName();
   }

   public List<PersistenceUnitDeployment> getPersistenceUnitDeployments()
   {
      return persistenceUnitDeployments;
   }

//   protected abstract PolicyConfiguration createPolicyConfiguration() throws Exception;
//
//   protected abstract void putJaccInService(PolicyConfiguration pc, DeploymentUnit unit);

   /**
    * Return the container injection handler collection. If not specified(null)
    * a default handler collection will be created.
    * @return the injection handler collection to use, null if the container
    *    should use a default setup.
    */
//   protected Collection<InjectionHandler<Environment>> getHandlers()
//   {
//      return null;
//   }

   /**
    * Create all EJB containers and Persistence Units
    * The only things that should be initialized is metadata that does not need access to any
    * other deployment.  This is because we want the entire EAR to be initialized so that we do not
    * have to guess on dependencies MBean names.  This is because of the silly scoping rules for persistence units
    * and EJBs.
    *
    * @throws Exception
    */
   public void create() throws Exception
   {
      long start = System.currentTimeMillis();

      //pc = createPolicyConfiguration();

      initializePersistenceUnits();
      
      log.debug("Persistence deployment time took: " + (System.currentTimeMillis() - start));
   }
   
   public void destroy()
   {
      persistenceUnitDeployments.clear();
   }
   
   public void start() throws Exception
   {
      try
      {
         startPersistenceUnits();
      }
      catch (Exception ex)
      {
         try
         {
            stop();
         }
         catch (Exception ignored)
         {
         }
         throw ex;
      }
   }
   
   public void stop() //throws Exception
   {
      stopPersistenceUnits();
   }

   protected void initializePersistenceUnits() throws Exception
   {
      // TODO: What is the meaning of this piece of code?
//      if (unit.getClasses() != null)
//      {
//         for (Class<?> explicit : unit.getClasses())
//         {
//            if (explicit.isAnnotationPresent(Entity.class))
//            {
//               explicitEntityClasses.add(explicit.getName());
//            }
//         }
//      }

      List<PersistenceUnitMetaData> pumds = metaData.getPersistenceUnits();
      for (PersistenceUnitMetaData metaData : pumds)
      {
         // FIXME: determine scoping
//         boolean isScoped = ejbContainers.size() > 0;
         boolean isScoped = false;

         Map<String, String> properties = metaData.getProperties();
         if (properties == null)
         {
            properties = new HashMap<String, String>();
            metaData.setProperties(properties);
         }
         // FIXME: reinstate
//         // Ensure 2nd level cache entries are segregated from other deployments
//         String cache_prefix = properties.get(SecondLevelCacheUtil.HIBERNATE_CACHE_REGION_PREFIX);
//         if (cache_prefix == null)
//         {
//            // Create a region_prefix for the 2nd level cache to ensure
//            // deployments are segregated
//            String jarName = isScoped ? unit.getShortName() : null;
//            cache_prefix = SecondLevelCacheUtil.createCacheRegionPrefix(earShortName, jarName, metaData.getName());
//            properties.put(SecondLevelCacheUtil.HIBERNATE_CACHE_REGION_PREFIX, cache_prefix);
//         }
         PersistenceUnitDeployment deployment = new PersistenceUnitDeployment(initialContext, this, explicitEntityClasses, metaData, "wrong-kernel-name", getDeploymentUnit(), null);
         PersistenceUnitRegistry.register(deployment);
         persistenceUnitDeployments.add(deployment);
      }
   }

//   public abstract DependencyPolicy createDependencyPolicy(JavaEEComponent component);

   protected void startPersistenceUnits()
   {
      if (persistenceUnitDeployments == null)
         return;

      for (PersistenceUnitDeployment entityDeployment : persistenceUnitDeployments)
      {
         if (entityDeployment != null)
         {
//            DependencyPolicy policy = createDependencyPolicy(entityDeployment);
//            entityDeployment.addDependencies(policy);
            AbstractBeanMetaData bmd = new AbstractBeanMetaData("ToDo");
            AbstractConstructorMetaData constructor = new AbstractConstructorMetaData();
            constructor.setValueObject(entityDeployment);
            bmd.setConstructor(constructor);
            unit.addAttachment(BeanMetaData.class, bmd);
//            kernelAbstraction.install(entityDeployment.getKernelName(), policy, unit, entityDeployment);
         }
      }
   }

   protected void stopPersistenceUnits()
   {
      if (persistenceUnitDeployments == null)
         return;

//      for (PersistenceUnitDeployment entityDeployment : persistenceUnitDeployments)
//      {
//         try
//         {
//            PersistenceUnitRegistry.unregister(entityDeployment);
//            if (entityDeployment != null)
//            {
//               kernelAbstraction.uninstall(entityDeployment.getKernelName());
//            }
//         }
//         catch (Exception e)
//         {
//            log.debug("error trying to shut down persistence unit", e);
//         }
//      }
//      
//      persistenceUnitDeployments = new ArrayList<PersistenceUnitDeployment>();
   }

   /**
    * Get the meta data associated with this deployment or null if none.
    * 
    * @return   meta data or null
    */
   public PersistenceMetaData getMetaData()
   {
      return metaData;
   }

   public String getName()
   {
      return unit.getSimpleName();
   }
}
