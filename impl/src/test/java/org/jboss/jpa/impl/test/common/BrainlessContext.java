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
package org.jboss.jpa.impl.test.common;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

/**
 * A naming context which can't do anything.
 *
 * @author carlo
 */
public class BrainlessContext implements Context
{
   public BrainlessContext()
   {
      super();
   }

   public Object lookup(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Object lookup(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void bind(Name name, Object obj) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void bind(String name, Object obj) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void rebind(Name name, Object obj) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void rebind(String name, Object obj) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void unbind(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void unbind(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void rename(Name oldName, Name newName) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void rename(String oldName, String newName) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public NamingEnumeration<NameClassPair> list(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public NamingEnumeration<NameClassPair> list(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public NamingEnumeration<Binding> listBindings(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public NamingEnumeration<Binding> listBindings(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void destroySubcontext(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void destroySubcontext(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Context createSubcontext(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Context createSubcontext(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Object lookupLink(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Object lookupLink(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public NameParser getNameParser(Name name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public NameParser getNameParser(String name) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Name composeName(Name name, Name prefix) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public String composeName(String name, String prefix) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Object addToEnvironment(String propName, Object propVal) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Object removeFromEnvironment(String propName) throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public Hashtable<?, ?> getEnvironment() throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public void close() throws NamingException
   {
      throw new OperationNotSupportedException();
   }

   public String getNameInNamespace() throws NamingException
   {
      throw new OperationNotSupportedException();
   }
}
