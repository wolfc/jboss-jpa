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
package org.jboss.jpa.spi;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.jpa.util.ThreadLocalStack;

/**
 * XPC aware components use this registry to subscribe to XPCs.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class XPCAware
{
   /*
    * I would rather call out on some SPI to ask for the current context, but
    * since this is mostly static stuff there is no real way to configure this class.
    */
   
   private static ThreadLocalStack<Map<String, EntityManager>> currentXPCs = new ThreadLocalStack<Map<String, EntityManager>>();
   
   /**
    * Return the identified XPC, if it is active.
    * 
    * @param id the id of the XPC
    * @return the XPC or null if none is found
    */
   public static EntityManager getExtendedPersistenceContext(String id)
   {
      List<Map<String, EntityManager>> list = currentXPCs.getList();
      if(list == null)
         return null;
      for(Map<String, EntityManager> xpcs : list)
      {
         EntityManager em = xpcs.get(id);
         if(em != null)
            return em;
      }
      return null;
   }
   
   public static Map<String, EntityManager> popXPCs()
   {
      return currentXPCs.pop();
   }
   
   /**
    * Called by an XPC consumer to make the call stack aware of XPCs.
    * @param xpcs the XPCs initiated by the XPC consumer
    */
   public static void pushXPCs(Map<String, EntityManager> xpcs)
   {
      assert xpcs != null : "xpcs is null";
      
      currentXPCs.push(xpcs);
   }
}
