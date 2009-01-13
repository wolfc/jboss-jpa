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

import java.util.Collection;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.structure.spi.main.MainDeployerStructure;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class InterApplicationPersistenceUnitDependencyResolver extends DefaultPersistenceUnitDependencyResolver
{
   private MainDeployer mainDeployer;
   
   private MainDeployerStructure mainDeployerStructure;
   
   protected String findWithinMainDeployer(String persistenceUnitName)
   {
      Collection<Deployment> topLevelDeployments = mainDeployer.getTopLevel();
      if(topLevelDeployments == null)
         return null;
      
      for(Deployment deployment : topLevelDeployments)
      {
         String name = deployment.getName();
         DeploymentUnit deploymentUnit = mainDeployerStructure.getDeploymentUnit(name);
         if(deploymentUnit == null)
            continue;
         String beanName = findWithinApplication(deploymentUnit, persistenceUnitName);
         if(beanName != null)
            return beanName;
      }
      
      return null;
   }
   
   // TODO: consilidate with DefaultPersistenceUnitDependencyResolver.resolvePersistenceUnitSupplier
   public String resolvePersistenceUnitSupplier(DeploymentUnit deploymentUnit, String persistenceUnitName)
   {
      int i = (persistenceUnitName == null ? -1 : persistenceUnitName.indexOf('#'));
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
            name = findWithinMainDeployer(persistenceUnitName);
         if(name == null)
            throw new IllegalArgumentException("Can't find a persistence unit named '" + persistenceUnitName + "' in " + deploymentUnit);
         return name;
      }
   }
   
   @Inject
   public void setMainDeployer(MainDeployer mainDeployer)
   {
      this.mainDeployer = mainDeployer;
   }
   
   @Inject
   public void setMainDeployerStructure(MainDeployerStructure mainDeployerStructure)
   {
      this.mainDeployerStructure = mainDeployerStructure;
   }
}
