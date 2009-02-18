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
package org.jboss.jpa.mcint.kernel.plugins.annotations;

import javax.persistence.PersistenceUnit;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.structure.spi.main.MainDeployerStructure;
import org.jboss.jpa.mcint.beans.metadata.plugins.PersistenceUnitValueMetaData;
import org.jboss.jpa.resolvers.PersistenceUnitDependencyResolver;
import org.jboss.kernel.plugins.annotations.PropertyAnnotationPlugin;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class PersistenceUnitAnnotationPlugin extends PropertyAnnotationPlugin<PersistenceUnit>
{
   private PersistenceUnitDependencyResolver resolver;
   
   private MainDeployer mainDeployer;

   private MainDeployerStructure mainDeployerStructure;
   
   public PersistenceUnitAnnotationPlugin()
   {
      super(PersistenceUnit.class);
   }
   
   @Override
   public ValueMetaData createValueMetaData(PersistenceUnit annotation)
   {
      return new PersistenceUnitValueMetaData(mainDeployer, mainDeployerStructure, resolver, annotation.unitName());
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
   
   @Inject
   public void setPersistenceUnitDependencyResolver(PersistenceUnitDependencyResolver resolver)
   {
      this.resolver = resolver;
   }
}
