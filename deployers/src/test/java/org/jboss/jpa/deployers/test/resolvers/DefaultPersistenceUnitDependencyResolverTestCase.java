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
package org.jboss.jpa.deployers.test.resolvers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Collections;

import org.jboss.deployers.vfs.plugins.structure.AbstractVFSDeploymentContext;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSDeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentContext;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.deployers.PersistenceParsingDeployer;
import org.jboss.jpa.deployers.test.common.SimpleJavaEEModuleInformer;
import org.jboss.jpa.resolvers.DefaultPersistenceUnitDependencyResolver;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.junit.Test;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class DefaultPersistenceUnitDependencyResolverTestCase
{
   @Test
   public void resolveDefaultPersistenceUnit() throws Exception
   {
      DefaultPersistenceUnitDependencyResolver resolver = new DefaultPersistenceUnitDependencyResolver();
      resolver.setJavaEEModuleInformer(new SimpleJavaEEModuleInformer());
      
      String common = "/org/jboss/jpa/deployers/test/parsing";
      URL url = getClass().getResource(common);
      assertNotNull(url);
      VirtualFile root = VFS.getRoot(url);
      assertNotNull(root);
      
      // Create some PersistenceMetaData
      PersistenceParsingDeployer deployer = new PersistenceParsingDeployer();
      // TODO: Use MC?
      deployer.create();
      deployer.start();
      
      VFSDeploymentContext deploymentContext = new AbstractVFSDeploymentContext(root, "");
      deploymentContext.setMetaDataLocations(Collections.singletonList(root));
      VFSDeploymentUnit deploymentUnit = new AbstractVFSDeploymentUnit(deploymentContext);
      
      deployer.deploy(deploymentUnit);
      PersistenceMetaData metaData = deploymentUnit.getAttachment(PersistenceMetaData.class);
      assertNotNull(metaData);
      
      // We want the default persistence unit
      String persistenceUnitName = null;
      String beanName = resolver.resolvePersistenceUnitSupplier(deploymentUnit, persistenceUnitName);
      assertEquals("persistence.unit:unitName=#dummy", beanName);
      
      // We want the default persistence unit
      persistenceUnitName = "";
      beanName = resolver.resolvePersistenceUnitSupplier(deploymentUnit, persistenceUnitName);
      assertEquals("persistence.unit:unitName=#dummy", beanName);
   }
}
