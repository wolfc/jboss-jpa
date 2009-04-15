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

import java.util.List;

import org.jboss.deployers.spi.deployer.helpers.AbstractComponentDeployer;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeploymentVisitor;
import org.jboss.logging.Logger;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision: $
 */
public class PersistenceDeployer extends AbstractComponentDeployer<PersistenceMetaData, PersistenceUnitMetaData>
{
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(PersistenceDeployer.class);
   
   public PersistenceDeployer()
   {
      //setComponentVisitor(new PersistenceUnitDeploymentVisitor());
      // Since we don't set component visitor
      addInput(PersistenceMetaData.class);
      setOutput(PersistenceUnitMetaData.class);
      
      setDeploymentVisitor(new PersistenceDeploymentVisitor());
   }

   private class PersistenceDeploymentVisitor extends AbstractDeploymentVisitor<PersistenceUnitMetaData, PersistenceMetaData>
   {
      @Override
      protected String getComponentName(PersistenceUnitMetaData component)
      {
          // we should be OK with this name, as I don't expect multiple PUMDs with same name on same DU?
          String pumdName = component.getName();
          if (pumdName == null)
             throw new IllegalStateException("Persistence unit is unnamed in " + component);
          
          return PersistenceUnitMetaData.class.getName() + "." + pumdName;
      }
      
      @Override
      protected Class<PersistenceUnitMetaData> getComponentType()
      {
         return PersistenceUnitMetaData.class;
      }
      
      public Class<PersistenceMetaData> getVisitorType()
      {
         return PersistenceMetaData.class;
      }

      @Override
      protected List<PersistenceUnitMetaData> getComponents(PersistenceMetaData deployment)
      {
         return deployment.getPersistenceUnits();
      }
   }
}
