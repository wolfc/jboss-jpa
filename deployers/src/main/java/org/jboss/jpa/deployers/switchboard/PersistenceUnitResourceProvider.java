/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jpa.deployers.switchboard;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.jpa.resolvers.PersistenceUnitDependencyResolver;
import org.jboss.logging.Logger;
import org.jboss.switchboard.javaee.environment.PersistenceUnitRefType;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.Resource;

/**
 * {@link MCBasedResourceProvider} for processing PersistenceUnit references in a
 * Java EE component.
 *
 * @see #provide(DeploymentUnit, PersistenceUnitRefType)
 *
 * @author Scott Marlow
 */
public class PersistenceUnitResourceProvider implements MCBasedResourceProvider<PersistenceUnitRefType>
{
   private static final Logger log = Logger.getLogger(PersistenceUnitResourceProvider.class);
   
   /**
    * PU resolver
    */
   private PersistenceUnitDependencyResolver persistenceUnitDependencyResolver;
   
   /**
    *
    * @param resolver For resolving the PU bean name
    */
   public PersistenceUnitResourceProvider(PersistenceUnitDependencyResolver resolver)
   {
      this.persistenceUnitDependencyResolver = resolver;
   }


   @Override
   public Resource provide(DeploymentUnit unit, PersistenceUnitRefType puRef)
   {
      // the DU which depends on this persistence-unit-ref 
      DeploymentUnit dependentDU = unit;
      // the PersistenceUnitDependencyResolver works on non-component deployment units.
      // So if we are currently processing component DUs (like we do for EJBs), then pass the
      // component DUs parent during resolution.
      if (unit.isComponent())
      {
         dependentDU = unit.getParent();
      }
      // resolve the PU supplier for the persistence-unit-ref
      log.debug("Resolving PU supplier for: " + puRef.getPersistenceUnitName() + " in unit " + dependentDU);
      String puSupplier = persistenceUnitDependencyResolver.resolvePersistenceUnitSupplier(dependentDU, puRef.getPersistenceUnitName());
      log.debug("Resolved PU supplier: " + puSupplier + " for persistence-unit-ref: " + puRef.getName() + " in unit " + dependentDU);
      
      // create a PU ref resource
      return new PersistenceUnitRefResource(puSupplier);
   }

   @Override
   public Class<PersistenceUnitRefType> getEnvironmentEntryType()
   {
      return PersistenceUnitRefType.class; 
   }

}
