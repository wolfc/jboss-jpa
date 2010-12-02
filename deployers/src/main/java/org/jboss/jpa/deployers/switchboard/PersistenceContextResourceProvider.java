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
import org.jboss.switchboard.javaee.environment.PersistenceContextRefType;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.Resource;

import javax.persistence.PersistenceContextType;

/**
 * {@link org.jboss.switchboard.mc.spi.MCBasedResourceProvider} for processing PersistenceUnit references in a
 * Java EE component.
 *
 * @see #provide(org.jboss.deployers.structure.spi.DeploymentUnit, org.jboss.switchboard.javaee.environment.PersistenceContextRefType)
 *
 * @author Scott Marlow
 */
public class PersistenceContextResourceProvider implements MCBasedResourceProvider<PersistenceContextRefType>
{
   private static final Logger log = Logger.getLogger(PersistenceContextResourceProvider.class);

   /**
    * PU resolver
    */
   private PersistenceUnitDependencyResolver persistenceUnitDependencyResolver;

   /**
    *
    * @param resolver For resolving the PU bean name
    */
   public PersistenceContextResourceProvider(PersistenceUnitDependencyResolver resolver)
   {
      this.persistenceUnitDependencyResolver = resolver;
   }


   @Override
   public Resource provide(DeploymentUnit unit, PersistenceContextRefType pcRef)
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
      log.debug("Resolving PU supplier for: " + pcRef.getPersistenceUnitName() + " in unit " + dependentDU);
      String puSupplier = persistenceUnitDependencyResolver.resolvePersistenceUnitSupplier(dependentDU, pcRef.getPersistenceUnitName());
      boolean extendedPc =PersistenceContextType.EXTENDED.equals(pcRef.getPersistenceContextType());
      log.debug("Resolved PU supplier: " + puSupplier + " for persistence-context-ref: " + pcRef.getName() +
         " in unit " + dependentDU + (extendedPc ? " (extended pc)":""));
      
      // create a PC ref resource
      return new PersistenceContextRefResource(puSupplier, pcRef);
   }

   @Override
   public Class<PersistenceContextRefType> getEnvironmentEntryType()
   {
      return PersistenceContextRefType.class;
   }

}
