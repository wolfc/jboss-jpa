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
package org.jboss.jpa.impl.test.common;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.spi.XPCResolver;
import org.jboss.tm.TxUtils;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class MockPersistenceUnit implements PersistenceUnit
{
   private PersistenceUnitInfo persistenceUnitInfo;
   private EntityManagerFactory entityManagerFactory;
   private XPCResolver xpcResolver;
   
   public MockPersistenceUnit(PersistenceUnitInfo pui)
   {
      this.persistenceUnitInfo = pui;
      this.xpcResolver = new XPCResolver()
      {
         public EntityManager getExtendedPersistenceContext(String kernelName)
         {
            // TODO
            return null;
         }
      };
   }
   
   public EntityManagerFactory getContainerEntityManagerFactory()
   {
      return entityManagerFactory;
   }

   public String getName()
   {
      return persistenceUnitInfo.getPersistenceUnitName();
   }

   public EntityManager getTransactionScopedEntityManager()
   {
      // TODO
      EntityManager em = entityManagerFactory.createEntityManager();
      return em;
   }

   public XPCResolver getXPCResolver()
   {
      return xpcResolver;
   }

   public boolean isInTx()
   {
      return TxUtils.isActive();
   }

   public void start() throws Exception
   {
      Class<?> providerClass = Thread.currentThread().getContextClassLoader().loadClass(persistenceUnitInfo.getPersistenceProviderClassName());
      PersistenceProvider pp = (PersistenceProvider) providerClass.newInstance();
      Map<?, ?> properties = new HashMap<Object, Object>();
      entityManagerFactory = pp.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
   }
   
   public void stop()
   {
      
   }
   
   public void verifyInTx()
   {
      if (!TxUtils.isActive())
         throw new TransactionRequiredException("Transaction must be active to access EntityManager");
   }

}
