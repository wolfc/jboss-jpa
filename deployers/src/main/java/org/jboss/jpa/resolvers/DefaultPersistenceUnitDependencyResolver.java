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
package org.jboss.jpa.resolvers;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.jpa.javaee.JavaEEModuleInformer;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class DefaultPersistenceUnitDependencyResolver implements PersistenceUnitDependencyResolver
{
   private JavaEEModuleInformer javaEEModuleInformer;
   
   public String createBeanName(DeploymentUnit deploymentUnit, String persistenceUnitName)
   {
      // persistenceUnitName must be a simple name
      assert persistenceUnitName.indexOf('/') == -1;
      assert persistenceUnitName.indexOf('#') == -1;
      
      String appName = javaEEModuleInformer.getApplicationName(deploymentUnit);
      String modulePath = javaEEModuleInformer.getModulePath(deploymentUnit);
      String unitName = (appName != null ? appName + "/" : "") + modulePath + "#" + persistenceUnitName;
      return "persistence.unit:unitName=" + unitName;
   }

   private String findWithinApplication(DeploymentUnit unit, String persistenceUnitName)
   {
      String name = findWithinModule(unit, persistenceUnitName, false);
      if(name != null)
         return name;
      
      for(DeploymentUnit child : unit.getChildren())
      {
         name = findWithinApplication(child, persistenceUnitName);
         if(name != null)
            return name;
      }
      return null;
   }
   
   private String findWithinModule(DeploymentUnit unit, String persistenceUnitName, boolean allowScoped)
   {
      if(!allowScoped && isScoped(unit))
         return null;
      
      PersistenceMetaData persistenceMetaData = unit.getAttachment(PersistenceMetaData.class);
      if(persistenceMetaData == null)
         return null;
      for(PersistenceUnitMetaData persistenceUnit : persistenceMetaData.getPersistenceUnits())
      {
         if(persistenceUnit.getName().equals(persistenceUnitName))
            return createBeanName(unit, persistenceUnitName);
      }
      return null;
   }
   
   private static DeploymentUnit getDeploymentUnit(DeploymentUnit current, String path)
   {
      if(path.startsWith("/"))
         return getDeploymentUnit(current.getTopLevel(), path.substring(1));
      if(path.startsWith("./"))
         return getDeploymentUnit(current, path.substring(2));
      if(path.startsWith("../"))
         return getDeploymentUnit(current.getParent(), path.substring(3));
      int i = path.indexOf('/');
      String name;
      if(i == -1)
         name = path;
      else
         name = path.substring(0, i);
      for(DeploymentUnit child : current.getChildren())
      {
         if(child.getName().equals(name))
            return child;
      }
      throw new IllegalArgumentException("Can't find a deployment unit named " + name + " at " + current);
   }
   
   /*
    * EJB3 JPA 6.2.2: Persistence Unit Scope
    */
   private boolean isScoped(DeploymentUnit unit)
   {
      JavaEEModuleInformer.ModuleType type = javaEEModuleInformer.getModuleType(unit);
      if(type == JavaEEModuleInformer.ModuleType.APP_CLIENT)
         return true;
      if(type == JavaEEModuleInformer.ModuleType.EJB)
         return true;
      if(type == JavaEEModuleInformer.ModuleType.WEB)
         return true;
      return false;
   }
   
   public String resolvePersistenceUnitSupplier(DeploymentUnit deploymentUnit, String persistenceUnitName)
   {
      int i = persistenceUnitName.indexOf('#');
      if(i != -1)
      {
         String path = persistenceUnitName.substring(0, i);
         String unitName = persistenceUnitName.substring(i + 1);
         DeploymentUnit targetDeploymentUnit = getDeploymentUnit(deploymentUnit, path);
         // TODO: verify the existence of PersistenceUnitMetaData?
         return createBeanName(targetDeploymentUnit, unitName);
      }
      else
      {
         String name = findWithinModule(deploymentUnit, persistenceUnitName, true);
         if(name == null)
            name = findWithinApplication(deploymentUnit.getTopLevel(), persistenceUnitName);
         if(name == null)
            throw new IllegalArgumentException("Can't find a persistence unit named '" + persistenceUnitName + "' in " + deploymentUnit);
         return name;
      }
   }
   
   @Inject
   public void setJavaEEModuleInformer(JavaEEModuleInformer informer)
   {
      this.javaEEModuleInformer = informer;
   }
}
