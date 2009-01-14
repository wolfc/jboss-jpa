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

import org.jboss.aop.microcontainer.aspects.jmx.JMX;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.jpa.resolvers.strategy.JBossSearchStrategy;
import org.jboss.jpa.resolvers.strategy.SpecCompliantSearchStrategy;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
@JMX(exposedInterface=DymanicPersistenceUnitDependencyResolverMBean.class)
public class DynamicPersistenceUnitDependencyResolver extends BasePersistenceUnitDependencyResolver
   implements DymanicPersistenceUnitDependencyResolverMBean
{
   private boolean specCompliant = false;
   
   private JBossSearchStrategy jbossSearchStrategy;
   
   private SpecCompliantSearchStrategy specCompliantStrategy;
   
   public boolean getSpecCompliant()
   {
      return specCompliant;
   }
   
   @Inject
   public void setJBossSearchStrategy(JBossSearchStrategy strategy)
   {
      this.jbossSearchStrategy = strategy;
   }
   
   public void setSpecCompliant(boolean specCompliant)
   {
      this.specCompliant = specCompliant;
      if(specCompliant)
         setSearchStrategy(specCompliantStrategy);
      else
         setSearchStrategy(jbossSearchStrategy);
   }
   
   @Inject
   public void setSpecCompliantSearchStrategy(SpecCompliantSearchStrategy strategy)
   {
      this.specCompliantStrategy = strategy;
   }
   
   @Start
   public void start()
   {
      // make sure we actually obey any overrides
      setSpecCompliant(this.specCompliant);
   }
}
