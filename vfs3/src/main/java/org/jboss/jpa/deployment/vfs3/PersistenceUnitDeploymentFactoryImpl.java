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
package org.jboss.jpa.deployment.vfs3;

import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.deployment.PersistenceDeployment;
import org.jboss.jpa.deployment.PersistenceUnitDeployment;
import org.jboss.jpa.deployment.PersistenceUnitDeploymentFactory;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;

/**
 * Implementation of {@link PersistenceUnitDeploymentFactory} that creates {@link PersistenceUnitDeployment} backed with VFS3 roots.
 *  
 * @author John Bailey
 */
public class PersistenceUnitDeploymentFactoryImpl extends PersistenceUnitDeploymentFactory
{

   /** {@inheritdoc} */
   @Override
   public PersistenceUnitDeployment create(InitialContext initialContext, PersistenceDeployment deployment,
         List<String> explicitEntityClasses, PersistenceUnitMetaData metadata, String kernelName,
         VFSDeploymentUnit deploymentUnit, Properties defaultPersistenceProperties)
   {
      return new PersistenceUnitDeploymentImpl(initialContext, deployment, explicitEntityClasses, metadata, kernelName, deploymentUnit, defaultPersistenceProperties);
   }
   
}
