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
package org.jboss.jpa.mcint.test.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.spi.XPCResolver;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class DummyPersistenceUnit implements PersistenceUnit
{
   public EntityManagerFactory getContainerEntityManagerFactory()
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class<?> interfaces[] = { EntityManagerFactory.class };
      InvocationHandler handler = new InvocationHandler() {
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
         {
            throw new RuntimeException("NYI");
         }
      };
      return (EntityManagerFactory) Proxy.newProxyInstance(loader, interfaces, handler);
   }

   public String getName()
   {
      throw new RuntimeException("NYI");
   }

   public XPCResolver getXPCResolver()
   {
      throw new RuntimeException("NYI");
   }

   public EntityManager getTransactionScopedEntityManager()
   {
      throw new RuntimeException("NYI");
   }

   public boolean isInTx()
   {
      throw new RuntimeException("NYI");
   }

   public void verifyInTx()
   {
      throw new RuntimeException("NYI");
      
   }

}
