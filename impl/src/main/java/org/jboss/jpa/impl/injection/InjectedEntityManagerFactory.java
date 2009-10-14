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
package org.jboss.jpa.impl.injection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.EntityManagerFactory;

import org.jboss.jpa.impl.AbstractEntityManagerFactoryDelegator;
import org.jboss.jpa.spi.PersistenceUnit;
import org.jboss.jpa.spi.PersistenceUnitRegistry;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class InjectedEntityManagerFactory extends AbstractEntityManagerFactoryDelegator implements EntityManagerFactory, Externalizable
{
   private static final long serialVersionUID = 1L;
   
   private transient EntityManagerFactory delegate;
   private transient PersistenceUnit persistenceUnit;
   
   public InjectedEntityManagerFactory() {}

   public InjectedEntityManagerFactory(PersistenceUnit persistenceUnit)
   {
      assert persistenceUnit != null : "persistenceUnit is null";
      
      this.delegate = persistenceUnit.getContainerEntityManagerFactory();
      this.persistenceUnit = persistenceUnit;
   }

   @Override
   public EntityManagerFactory getDelegate()
   {
      return delegate;
   }

   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeUTF(persistenceUnit.getName());
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      String kernelName = in.readUTF();
      persistenceUnit = PersistenceUnitRegistry.getPersistenceUnit(kernelName);
      if(persistenceUnit == null)
         throw new IOException("Unable to find persistence unit in registry: " + kernelName);
      delegate = persistenceUnit.getContainerEntityManagerFactory();
   }

   public void close()
   {
      throw new IllegalStateException("It is illegal to close an injected EntityManagerFactory");
   }
}
