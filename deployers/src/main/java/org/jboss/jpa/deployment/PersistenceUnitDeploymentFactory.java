/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.util.ServiceLoader;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;

/**
 * Factory used to create {@link PersistenceUnitDeployment} instances.
 * 
 * @author John Bailey
 */
public abstract class PersistenceUnitDeploymentFactory
{
   /**
    * Get a factory instance.  
    * 
    * @return factory instance
    */
   public static PersistenceUnitDeploymentFactory getInstance() {
      ServiceLoader<PersistenceUnitDeploymentFactory> factoryLoader = ServiceLoader.load(PersistenceUnitDeploymentFactory.class);
      
      Iterator<PersistenceUnitDeploymentFactory> factoryIterator = factoryLoader.iterator();
      
      PersistenceUnitDeploymentFactory factory = null;
      if(factoryIterator.hasNext())
      {
         factory = factoryIterator.next();
         if(factoryIterator.hasNext())
         {
            throw new RuntimeException("More the one factories found, please ensure only one factory implementation is found on the classpath.");
         }
      }
      else 
      {
         throw new RuntimeException("No factories found");
      }
      
      return factory;
   }
   
   /**
    * Template method for factory implementations to create {@link PersistenceUnitDeployment} instances. 
    *  
    * @param initialContext
    * @param deployment
    * @param explicitEntityClasses
    * @param metadata
    * @param kernelName
    * @param deploymentUnit
    * @param defaultPersistenceProperties
    * @return a {@link PersistenceUnitDeployment} instance
    */
   public abstract PersistenceUnitDeployment create(InitialContext initialContext, PersistenceDeployment deployment, List<String> explicitEntityClasses, PersistenceUnitMetaData metadata, String kernelName, VFSDeploymentUnit deploymentUnit, Properties defaultPersistenceProperties);
}
