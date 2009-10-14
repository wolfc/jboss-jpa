/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jpa.spi;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bdecoste@jboss.com">William DeCoste</a>
 * @version <tt>$Revision$</tt>
 */
public class PersistenceUnitRegistry
{
//   private static final Logger log = Logger.getLogger(PersistenceUnitRegistry.class);

   private static ConcurrentHashMap<String, PersistenceUnit> persistenceUnits = new ConcurrentHashMap<String, PersistenceUnit>();

   public static void register(PersistenceUnit container)
   {
      if (persistenceUnits.containsKey(container.getName())) throw new RuntimeException("Persistence Unit is already registered: " + container.getName());
      persistenceUnits.put(container.getName(), container);
   }

   public static void unregister(PersistenceUnit container)
   {
      persistenceUnits.remove(container.getName());
   }

   public static PersistenceUnit getPersistenceUnit(String name)
   {
      return persistenceUnits.get(name);
   }

   public static Collection<PersistenceUnit> getPersistenceUnits()
   {
      return persistenceUnits.values();
   }

}
