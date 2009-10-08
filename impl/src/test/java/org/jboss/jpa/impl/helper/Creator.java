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
package org.jboss.jpa.impl.helper;

import java.io.PrintStream;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

/**
 * Creator of abstract class source code. Not really a test. :-)
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class Creator
{

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      PrintStream out = System.out;
      
      out.println("// Generated using " + Creator.class.getName() + ", DO NOT EDIT");
      out.println("package org.jboss.jpa.impl.tx;");
      out.println("protected class AbstractTransactionScopedEntityManager implements EntityManager");
      out.println("{");
      
      Class<EntityManager> cls = EntityManager.class;
      
      Method methods[] = cls.getMethods();
      for(int i = 0; i < methods.length; i++)
      {
         out.println("\t" + methods[i]);
         String returnType = methods[i].getReturnType().getName();
         String methodName = methods[i].getName();
         out.print("   public " + returnType + " " + methodName + "(");
         Class<?> parameterTypes[] = methods[i].getParameterTypes();
         for(int j = 0; j < parameterTypes.length; j++)
         {
            out.print(parameterTypes[j].getName());
            out.print(" arg" + j);
            if((j + 1) < parameterTypes.length)
               out.print(", ");
         }
         out.println(")");
         out.println("   {");
         System.out.println(methods[i].getExceptionTypes());
         out.println("   }");
      }
   }

}
