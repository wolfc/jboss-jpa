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
package org.jboss.jpa.spi.test.registry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.spi.XPCResolver;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class MockPersistenceUnit implements PersistenceUnit
{
   private String name;

   public MockPersistenceUnit(String name)
   {
      this.name = name;
   }
   
   public EntityManagerFactory getContainerEntityManagerFactory()
   {
      // TODO Auto-generated method stub
      throw new RuntimeException("NYI");
   }

   public String getName()
   {
      return name;
   }

   public EntityManager getTransactionScopedEntityManager()
   {
      // TODO Auto-generated method stub
      throw new RuntimeException("NYI");
   }

   public XPCResolver getXPCResolver()
   {
      // TODO Auto-generated method stub
      throw new RuntimeException("NYI");
   }

   public boolean isInTx()
   {
      // TODO Auto-generated method stub
      throw new RuntimeException("NYI");
   }

   public void verifyInTx()
   {
      // TODO Auto-generated method stub
      throw new RuntimeException("NYI");
   }

}
