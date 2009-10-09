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
package org.jboss.jpa.deployment;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.util.ThreadLocalStack;
import org.jboss.logging.Logger;
import org.jboss.tm.TransactionLocal;
import org.jboss.tm.TxUtils;

/**
 * 
 * @author <a href="mailto:gavine@hibernate.org">Gavin King</a>
 * @version $Revision$
 */
// TODO: move functionality to PersistenceUnitDeployment
@Deprecated
public class ManagedEntityManagerFactory
{
   private static final Logger log = Logger.getLogger(ManagedEntityManagerFactory.class);

   protected TransactionLocal session = new TransactionLocal();
   private PersistenceUnit persistenceUnit;

   public static ThreadLocalStack<Map<ManagedEntityManagerFactory, EntityManager>> nonTxStack = new ThreadLocalStack<Map<ManagedEntityManagerFactory, EntityManager>>();

   public EntityManager getNonTxEntityManager()
   {
      Map<ManagedEntityManagerFactory, EntityManager> map = nonTxStack.get();
      
      EntityManager em = null;
      if (map != null)
         em = map.get(this);
      else
      {
         map = new HashMap<ManagedEntityManagerFactory, EntityManager>();
         nonTxStack.push(map);
      }
      
      if (em == null)
      {
         em = getEntityManagerFactory().createEntityManager();
         map.put(this, em);
      }
      return em;
   }

   protected ManagedEntityManagerFactory(PersistenceUnit persistenceUnit)
   {
      this.persistenceUnit = persistenceUnit;
   }

   public EntityManagerFactory getEntityManagerFactory()
   {
      return persistenceUnit.getContainerEntityManagerFactory();
   }

   public String getKernelName()
   {
      return persistenceUnit.getName();
   }

   public void destroy()
   {
      throw new RuntimeException("JPA-15: do not call");
   }

   private static class SessionSynchronization implements Synchronization
   {
      private EntityManager manager;
//      private Transaction tx;
      private boolean closeAtTxCompletion;

      public SessionSynchronization(EntityManager session, Transaction tx, boolean close)
      {
         this.manager = session;
//         this.tx = tx;
         closeAtTxCompletion = close;
      }

      public void beforeCompletion()
      {
         /*  IF THIS GETS REACTIVATED THEN YOU MUST remove the if(closeAtTxCompletion) block in getSession()
         try
         {
            int status = tx.getStatus();
            if (status != Status.STATUS_ROLLEDBACK && status != Status.STATUS_ROLLING_BACK && status != Status.STATUS_MARKED_ROLLBACK)
            {
               if (FlushModeInterceptor.getTxFlushMode() != FlushModeType.NEVER)
               {
                  log.debug("************** flushing.....");
                  manager.flush();
               }
            }
         }
         catch (SystemException e)
         {
            throw new RuntimeException(e);
         }
         */
      }

      public void afterCompletion(int status)
      {
         if (closeAtTxCompletion)
         {
            log.debug("************** closing entity managersession **************");
            manager.close();
         }
      }
   }

   public TransactionLocal getTransactionSession()
   {
      return session;
   }

   public void registerExtendedWithTransaction(EntityManager pc)
   {
      pc.joinTransaction();
      session.set(pc);
   }

   public void verifyInTx()
   {
      Transaction tx = session.getTransaction();
      if (tx == null || !TxUtils.isActive(tx)) throw new TransactionRequiredException("EntityManager must be access within a transaction");
      if (!TxUtils.isActive(tx))
         throw new TransactionRequiredException("Transaction must be active to access EntityManager");
   }
   public boolean isInTx()
   {
      Transaction tx = session.getTransaction();
      if (tx == null || !TxUtils.isActive(tx)) return false;
      return true;
   }

   public EntityManager getTransactionScopedEntityManager()
   {
      Transaction tx = session.getTransaction();
      if (tx == null || !TxUtils.isActive(tx)) return getNonTxEntityManager();

      EntityManager rtnSession = (EntityManager) session.get();
      if (rtnSession == null)
      {
         rtnSession = createEntityManager();
         try
         {
            tx.registerSynchronization(new SessionSynchronization(rtnSession, tx, true));
         }
         catch (RollbackException e)
         {
            throw new RuntimeException(e);  //To change body of catch statement use Options | File Templates.
         }
         catch (SystemException e)
         {
            throw new RuntimeException(e);  //To change body of catch statement use Options | File Templates.
         }
         session.set(rtnSession);
         rtnSession.joinTransaction(); // force registration with TX
      }
      return rtnSession;
   }

   public EntityManager createEntityManager()
   {
      return getEntityManagerFactory().createEntityManager();
   }

   public PersistenceUnit getPersistenceUnit()
   {
      return persistenceUnit;
   }
}
