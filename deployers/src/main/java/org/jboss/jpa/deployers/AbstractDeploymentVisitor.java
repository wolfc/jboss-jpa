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

import java.util.ArrayList;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.DeploymentVisitor;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public abstract class AbstractDeploymentVisitor<T, C> implements DeploymentVisitor<T>
{
   private static final Logger log = Logger.getLogger(AbstractDeploymentVisitor.class);
   
   /**
    * Add component.
    *
    * @param unit the deployment unit
    * @param componentMD the component metadata
    */
   protected void addComponent(DeploymentUnit unit, C componentMD)
   {
      String name = getName(unit, componentMD);
      DeploymentUnit component = unit.addComponent(name);
      // TODO: determine proper component meta data class
      component.addAttachment(componentMD.getClass().getName(), componentMD);
   }

   public void deploy(DeploymentUnit unit, T deployment) throws DeploymentException
   {
      List<C> components = getComponents(deployment);
      if (components != null && components.isEmpty() == false)
      {
         List<C> visited = new ArrayList<C>();
         try
         {
            for (C component : components)
            {
               addComponent(unit, component);
               visited.add(component);
            }
         }
         catch (Throwable t)
         {
            for (int i = visited.size()-1; i >= 0; --i)
            {
               safeRemoveComponent(unit, visited.get(i));
            }
            throw DeploymentException.rethrowAsDeploymentException("Error deploying: " + unit.getName(), t);
         }
      }
   }

   protected abstract List<C> getComponents(T deployment);
   
   protected abstract String getName(DeploymentUnit unit, C component);
   
   /**
    * Remove bean component.
    *
    * @param unit the deployment unit
    * @param component the component metadata
    */

   protected void removeComponent(DeploymentUnit unit, C component)
   {
      String name = getName(unit, component);
      unit.removeComponent(name);
   }

   /**
    * Ignore all error during component removal.
    *
    * @param unit the deployment unit
    * @param component the component metadata
    */
   protected void safeRemoveComponent(DeploymentUnit unit, C component)
   {
      try
      {
         removeComponent(unit, component);
      }
      catch (Throwable ignored)
      {
         log.warn("Error during component removal: " + unit.getName(), ignored);
      }
   }

   public void undeploy(DeploymentUnit unit, T deployment)
   {
      List<C> components = getComponents(deployment);
      if (components != null && components.isEmpty() == false)
      {
         for (C component : components)
         {
            safeRemoveComponent(unit, component);
         }
      }
   }

}
