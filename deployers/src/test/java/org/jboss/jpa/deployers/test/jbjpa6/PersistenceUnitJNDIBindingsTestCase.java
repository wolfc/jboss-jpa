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
package org.jboss.jpa.deployers.test.jbjpa6;

import static org.junit.Assert.fail;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

import org.hibernate.SessionFactory;
import org.jboss.jpa.deployers.test.common.PersistenceTestCase;
import org.jboss.jpa.deployers.test.common.Person;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for JBJPA-6 compliance.
 * 
 * Tests that Persistence Unit related JNDI bindings
 * work and auto-create sub-contexts if they don't exist.
 *  
 * @author Matt Carter
 * 
 * @version $Revision: $
 */
public class PersistenceUnitJNDIBindingsTestCase extends PersistenceTestCase
{
   /** One new subcontext and EM binding test */
   public static final String EM_PATH = "em-path1/JndiEM";

   /** One existing subcontext and EMF binding test */
   public static final String EMF_PATH = "existing-path/JndiEMF";

   /** Two new subcontexts and SF binding test */
   public static final String SF_PATH = "sf-path1/sf-path2/JndiSF";

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      PersistenceTestCase.beforeClass();

      InitialContext ctx = new InitialContext();
      ctx.createSubcontext("existing-path");

      deploy(PersistenceUnitJNDIBindingsTestCase.class.getResource("/org/jboss/jpa/deployers/test/jbjpa6"));
   }

   @Test
   public void testEM() throws Exception
   {
      InitialContext ctx = new InitialContext();
      TransactionManager tm = (TransactionManager) ctx.lookup("java:/TransactionManager");
      EntityManager em = null;
      try
      {
         em = (EntityManager) ctx.lookup(EM_PATH);
      }
      catch (Exception e)
      {
         fail("EntityManager binding failed");
      }
      try
      {
         tm.begin();
         try
         {
            Person p = new Person();
            p.setName("Emma");
            em.persist(p);
         }
         finally
         {
            tm.rollback();
         }
      }
      catch (Exception e)
      {
         fail("EntityManager test invocation failed");
      }
   }

   @Test
   public void testEMF() throws Exception
   {
      InitialContext ctx = new InitialContext();
      EntityManagerFactory emf = null;
      try
      {
         emf = (EntityManagerFactory) ctx.lookup(EMF_PATH);
      }
      catch (Exception e)
      {
         fail("EntityManagerFactory binding failed");
      }
      try
      {
         EntityManager em = emf.createEntityManager();
         Person p = new Person();
         p.setName("Emma");
         em.persist(p);
      }
      catch (Exception e)
      {
         fail("EntityManagerFactory test invocation failed");
      }
   }

   @Test
   public void testSF() throws Exception
   {
      InitialContext ctx = new InitialContext();
      SessionFactory sf = null;
      try
      {
         sf = (SessionFactory) ctx.lookup(SF_PATH);
      }
      catch (Exception e)
      {
         fail("SessionFactory binding failed");
      }
      try
      {
         sf.isClosed();
      }
      catch (Exception e)
      {
         fail("SessionFactory test invocation failed");
      }

   }

}
