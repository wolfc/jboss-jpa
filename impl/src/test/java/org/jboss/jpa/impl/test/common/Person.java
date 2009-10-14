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
package org.jboss.jpa.impl.test.common;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A generic entity with an id and a string for testing.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
@Entity
public class Person
{
   @Id
   private int id;
   
   private String name;

   @Override
   public boolean equals(Object obj)
   {
      if(!(obj instanceof Person))
         return false;
      Person other = (Person) obj;
      if(other.id != this.id)
         return false;
      // TODO: name
      return true;
   }
   
   @Override
   public int hashCode()
   {
      return id;
   }
   
   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }
   
   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @param id the id to set
    */
   public void setId(int id)
   {
      this.id = id;
   }
   
   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }
}
