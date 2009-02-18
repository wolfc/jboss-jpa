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
package org.jboss.jpa.mcint.beans.metadata.plugins;

import org.jboss.beans.metadata.plugins.AbstractValueMetaData;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.MetaDataVisitor;
import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.structure.spi.main.MainDeployerStructure;
import org.jboss.jpa.resolvers.PersistenceUnitDependencyResolver;
import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.reflect.spi.TypeInfo;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class PersistenceUnitValueMetaData extends AbstractValueMetaData
{
   // TODO: this is not really serializable
   private static final long serialVersionUID = 1L;
   
   private MainDeployer mainDeployer;
   private MainDeployerStructure mainDeployerStructure;
   private PersistenceUnitDependencyResolver resolver;
   private String persistenceUnitName;
   
   private KernelControllerContext context;

   public PersistenceUnitValueMetaData(MainDeployer mainDeployer, MainDeployerStructure mainDeployerStructure, PersistenceUnitDependencyResolver resolver, String persistenceUnitName)
   {
      assert mainDeployer != null : "mainDeployer is null";
      assert mainDeployerStructure != null : "mainDeployerStructure is null";
      assert resolver != null : "resolver is null";
      
      this.mainDeployer = mainDeployer;
      this.mainDeployerStructure = mainDeployerStructure;
      this.resolver = resolver;
      this.persistenceUnitName = persistenceUnitName;
   }
   
   @Override
   public void describeVisit(MetaDataVisitor visitor)
   {
      Object name = context.getName();
      //Object iDependOn = getUnderlyingValue();
      Object iDependOn = getPersistenceUnitBeanName();
      
      log.info("iDependOn " + iDependOn);

      ControllerState whenRequired = visitor.getContextState();
      ControllerState dependentState = ControllerState.INSTALLED;

      AbstractDependencyItem dependency = new AbstractDependencyItem(name, iDependOn, whenRequired, dependentState);
      visitor.addDependency(dependency);
      
      super.describeVisit(visitor);
   }
   
   private DeploymentUnit findBean(DeploymentUnit deploymentUnit, String contextName)
   {
      if(deploymentUnit == null)
         return null;
      /*
      Set<Object> controllerContextNames = deploymentUnit.getControllerContextNames();
      if(controllerContextNames != null)
      {
         for(Object name : controllerContextNames)
         {
            if(name.equals(contextName))
               return deploymentUnit;
         }
      }
      */
      BeanMetaData bmd = deploymentUnit.getAttachment(BeanMetaData.class);
      if(bmd != null && bmd.getName().equals(contextName))
         return deploymentUnit;
      DeploymentUnit result;
      for(DeploymentUnit component : deploymentUnit.getComponents())
      {
         result = findBean(component, contextName);
         if(result != null)
            return deploymentUnit;
      }
      for(DeploymentUnit child : deploymentUnit.getChildren())
      {
         result = findBean(child, contextName);
         if(result != null)
            return result;
      }
      return null;
   }
   
   private String getPersistenceUnitBeanName()
   {
      // TODO: check assumption earlier
      String contextName = (String) context.getName();
      DeploymentUnit deploymentUnit = null;
      for(Deployment deployment : mainDeployer.getTopLevel())
      {
         String name = deployment.getName();
         deploymentUnit = mainDeployerStructure.getDeploymentUnit(name);
         if(deploymentUnit == null)
            continue;
         deploymentUnit = findBean(deploymentUnit, contextName);
         if(deploymentUnit != null)
            break;
      }
      // TODO: this requires a bit more explaining
      if(deploymentUnit == null)
         throw new IllegalStateException("@PersistenceUnit can only be used within a regular deployment unit");
      String persistenceUnitBeanName = resolver.resolvePersistenceUnitSupplier(deploymentUnit, persistenceUnitName);
      return persistenceUnitBeanName;
   }
   
   @Override
   public Object getValue(TypeInfo info, ClassLoader cl) throws Throwable
   {
      String persistenceUnitBeanName = getPersistenceUnitBeanName();
      PersistenceUnit pu = (PersistenceUnit) context.getController().getContext(persistenceUnitBeanName, ControllerState.INSTALLED).getTarget();
      return pu.getContainerEntityManagerFactory();
   }
   
   @Override
   public void initialVisit(MetaDataVisitor visitor)
   {
      context = visitor.getControllerContext();
      
      super.initialVisit(visitor);
   }
}
