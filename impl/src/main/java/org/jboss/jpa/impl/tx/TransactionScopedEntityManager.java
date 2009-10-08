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
package org.jboss.jpa.impl.tx;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.spi.PersistenceUnitRegistry;
import org.jboss.jpa.spi.XPCResolver;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class TransactionScopedEntityManager implements EntityManager, Externalizable
{
   private static final long serialVersionUID = 1L;
   
   private static final Logger log = Logger.getLogger(TransactionScopedEntityManager.class);
   
   private transient PersistenceUnit persistenceUnit;

   public Session getHibernateSession()
   {
      EntityManager em = persistenceUnit.getTransactionScopedEntityManager();
      if (em instanceof HibernateEntityManager)
      {
         return ((HibernateEntityManager) em).getSession();
      }
      throw new RuntimeException("ILLEGAL ACTION:  Not a Hibernate pe" +
              "rsistence provider");
   }

   public TransactionScopedEntityManager(PersistenceUnit persistenceUnit)
   {
      if (persistenceUnit == null) throw new NullPointerException("persistenceUnit must not be null");
      this.persistenceUnit = persistenceUnit;
   }

   public TransactionScopedEntityManager()
   {
   }

   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeUTF(persistenceUnit.getName());
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      String kernelName = in.readUTF();
      persistenceUnit = PersistenceUnitRegistry.getPersistenceUnit(kernelName);
      if ( persistenceUnit == null ) throw new IOException( "Unable to find persistence unit in registry: " + kernelName );
   }

   public Object getDelegate()
   {
      return getEntityManager().getDelegate();
   }

   public void joinTransaction()
   {
      persistenceUnit.verifyInTx();
      getEntityManager().joinTransaction();
   }

   public void clear()
   {
      getEntityManager().clear();
   }

   public FlushModeType getFlushMode()
   {
      return getEntityManager().getFlushMode();
   }

   public void lock(Object entity, LockModeType lockMode)
   {
      persistenceUnit.verifyInTx();
      getEntityManager().lock(entity, lockMode);
   }

   public <T> T getReference(Class<T> entityClass, Object primaryKey)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      try
      {
         return em.getReference(entityClass, primaryKey);
      }
      finally
      {
         if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      }
   }

   public void setFlushMode(FlushModeType flushMode)
   {
      getEntityManager().setFlushMode(flushMode);
   }

   public Query createQuery(String ejbqlString)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      return em.createQuery(ejbqlString);
   }

   public Query createNamedQuery(String name)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      return em.createNamedQuery(name);
   }

   public Query createNativeQuery(String sqlString)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      return em.createNativeQuery(sqlString);
   }

   public Query createNativeQuery(String sqlString, Class resultClass)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      return em.createNativeQuery(sqlString, resultClass);
   }

   public Query createNativeQuery(String sqlString, String resultSetMapping)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      return em.createNativeQuery(sqlString, resultSetMapping);
   }

   public <A> A find(Class<A> entityClass, Object primaryKey)
   {
      EntityManager em = getEntityManager();
      if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      try
      {
         return em.find(entityClass, primaryKey);
      }
      finally
      {
         if (!persistenceUnit.isInTx()) em.clear(); // em will be closed by interceptor
      }
   }

   public void persist(Object entity)
   {
      persistenceUnit.verifyInTx();
      getEntityManager().persist(entity);
   }

   public <A> A merge(A entity)
   {
      persistenceUnit.verifyInTx();
      return (A) getEntityManager().merge(entity);
   }

   public void remove(Object entity)
   {
      persistenceUnit.verifyInTx();
      getEntityManager().remove(entity);
   }

   public void refresh(Object entity)
   {
      persistenceUnit.verifyInTx();
      getEntityManager().refresh(entity);
   }

   public boolean contains(Object entity)
   {
      return getEntityManager().contains(entity);
   }

   public void flush()
   {
      persistenceUnit.verifyInTx();
      getEntityManager().flush();
   }

   public void close()
   {
      throw new IllegalStateException("Illegal to call this method from injected, managed EntityManager");
   }

   public boolean isOpen()
   {
      throw new IllegalStateException("Illegal to call this method from injected, managed EntityManager");
   }

   public EntityTransaction getTransaction()
   {
      throw new IllegalStateException("Illegal to call this method from injected, managed EntityManager");
   }
   
   protected EntityManager getEntityManager()
   {
      String kernelName = persistenceUnit.getName();
      PersistenceUnit pu = PersistenceUnitRegistry.getPersistenceUnit(kernelName);
      XPCResolver xpcResolver = pu.getXPCResolver();
      EntityManager em = xpcResolver.getExtendedPersistenceContext(kernelName);
      if(em != null)
         return em;
      
      return persistenceUnit.getTransactionScopedEntityManager();
   }

}
