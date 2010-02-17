/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.jpa.util;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;


/**
 * Service loader following the API from http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html which is compatible with JDK 5.
 * 
 * @author John Bailey
 * @param <S> the service 
 */
public class ServiceLoader<S> implements Iterable<S>
{

   /**
    * @See <a href="http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html#load%28java.lang.Class%29">ServiceLoader.load(Class)</a> 
    */
   public static <S> ServiceLoader<S> load(Class<S> serviceClass)
   {
      return load(serviceClass, Thread.currentThread().getContextClassLoader());
   }

   /**
    * @See <a href="http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html#load%28java.lang.Class,%20java.lang.ClassLoader%29">ServiceLoader.load(Class, ClassL oader)</a> 
    */
   public static <S> ServiceLoader<S> load(Class<S> service, ClassLoader loader)
   {
      return new ServiceLoader<S>(service, loader);
   }
   
   private final Class<S> serviceClass;
   private final ClassLoader loader;
   private Iterator<S> serviceIterator;
   
   
   /**
    * ServiceLoader constructor
    * @param serviceClass the service class
    * @param loader classloader to load service from
    */
   private ServiceLoader(Class<S> serviceClass, ClassLoader loader) 
   {
      this.serviceClass = serviceClass;
      this.loader = loader;
      reload();
   }

   /**
    * @See <a href="http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html#iterator%28%29">ServiceLoader.iterator</a> 
    */
   public Iterator<S> iterator()
   {
      return serviceIterator;
   }
   
   /**
    * @See <a href="http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html#reload%29">ServiceLoader.reload</a>
    */
   public void reload() 
   {
      serviceIterator = ServiceRegistry.lookupProviders(serviceClass, loader);  
   }
}
