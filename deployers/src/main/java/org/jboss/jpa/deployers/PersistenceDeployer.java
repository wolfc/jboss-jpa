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
package org.jboss.jpa.deployers;

import java.util.Collections;
import java.util.List;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.deployers.spi.deployer.helpers.AbstractComponentDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.jpa.resolvers.PersistenceUnitDependencyResolver;
import org.jboss.logging.Logger;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class PersistenceDeployer extends AbstractComponentDeployer<PersistenceMetaData, PersistenceUnitMetaData>
{
   private static final Logger log = Logger.getLogger(PersistenceDeployer.class);
   
   private PersistenceUnitDependencyResolver persistenceUnitDependencyResolver;
   
   public PersistenceDeployer()
   {
      //setComponentVisitor(new PersistenceUnitDeploymentVisitor());
      // Since we don't set component visitor
      addInput(PersistenceMetaData.class);
      setOutput(PersistenceUnitMetaData.class);
      
      setDeploymentVisitor(new PersistenceDeploymentVisitor());
   }

   private class PersistenceDeploymentVisitor extends AbstractDeploymentVisitor<PersistenceMetaData, PersistenceUnitMetaData>
   {
      public Class<PersistenceMetaData> getVisitorType()
      {
         return PersistenceMetaData.class;
      }

      @Override
      protected List<PersistenceUnitMetaData> getComponents(PersistenceMetaData deployment)
      {
         return deployment.getPersistenceUnits();
      }

      @Override
      protected String getName(DeploymentUnit unit, PersistenceUnitMetaData component)
      {
         return persistenceUnitDependencyResolver.createBeanName(unit, component.getName());
      }
   }
   
   private class PersistenceUnitDeploymentVisitor extends AbstractDeploymentVisitor<PersistenceUnitMetaData, PersistenceUnitMetaData>
   {
      public Class<PersistenceUnitMetaData> getVisitorType()
      {
         return PersistenceUnitMetaData.class;
      }

      @Override
      protected List<PersistenceUnitMetaData> getComponents(PersistenceUnitMetaData deployment)
      {
         return Collections.singletonList(deployment);
      }

      @Override
      protected String getName(DeploymentUnit unit, PersistenceUnitMetaData component)
      {
         return persistenceUnitDependencyResolver.createBeanName(unit, component.getName());
      }
   }
   
   @Inject
   public void setPersistenceUnitDependencyResolver(PersistenceUnitDependencyResolver resolver)
   {
      this.persistenceUnitDependencyResolver = resolver;
   }
}
