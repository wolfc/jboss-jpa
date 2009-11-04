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
package org.jboss.jpa.impl;

import java.util.Map;
import java.util.Set;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.metamodel.Metamodel;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public abstract class AbstractEntityManagerFactoryDelegator implements EntityManagerFactory
{
   private static final long serialVersionUID = 1L;
   
   public EntityManager createEntityManager()
   {
      return getDelegate().createEntityManager();
   }

   public EntityManager createEntityManager(@SuppressWarnings("unchecked") Map map)
   {
      return getDelegate().createEntityManager(map);
   }

   public Cache getCache()
   {
      return getDelegate().getCache();
   }

   protected abstract EntityManagerFactory getDelegate();
   
   public Metamodel getMetamodel()
   {
      return getDelegate().getMetamodel();
   }

   public PersistenceUnitUtil getPersistenceUnitUtil()
   {
      return getDelegate().getPersistenceUnitUtil();
   }

   public Map<String, Object> getProperties()
   {
      return getDelegate().getProperties();
   }

   public javax.persistence.criteria.CriteriaBuilder getCriteriaBuilder()
   {
      return getDelegate().getCriteriaBuilder();
   }

   public boolean isOpen()
   {
      return getDelegate().isOpen();
   }
}
