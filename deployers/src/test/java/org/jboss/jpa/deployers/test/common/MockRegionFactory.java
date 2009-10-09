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

package org.jboss.jpa.deployers.test.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cache.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.QueryResultsRegion;
import org.hibernate.cache.RegionFactory;
import org.hibernate.cache.TimestampsRegion;
import org.hibernate.cfg.Settings;

/**
 * Mock impl of Hibernate RegionFactory used as a mechanism to validate
 * the properties used to configure a Hibernate SessionFactory. The intent
 * is not really to have a meaningful RegionFactory or second level cache; 
 * it's just that the RegionFactory plugin mechanism provides a hook to examine 
 * the properties associated with the Hibernate SessionFactory.
 * 
 * @see org.jboss.jpa.deployers.test.ejbthree893.SessionFactoryNameTestCase
 * @see org.jboss.jpa.deployers.test.jbas6111.SecondLevelCacheRegionPrefixTestCase
 * 
 * @author Brian Stansberry
 */
public class MockRegionFactory implements RegionFactory
{
   /** List of Properties passed to {@link #start(Settings, Properties)} */
   public static final List<Properties> SESSION_FACTORY_PROPERTIES = new ArrayList<Properties>();
   
   public MockRegionFactory()
   {      
   }

   public MockRegionFactory(Properties properties)
   {      
   }
   
   /**
    * This is the relevant bit; we store a ref to the properties.
    */
   public void start(Settings arg0, Properties arg1) throws CacheException
   {
      SESSION_FACTORY_PROPERTIES.add(arg1);
   }
   
   public CollectionRegion buildCollectionRegion(String arg0, Properties arg1, CacheDataDescription arg2)
         throws CacheException
   {
      throw new UnsupportedOperationException("MockRegionFactory doesn't support collection regions");
   }

   public EntityRegion buildEntityRegion(String arg0, Properties arg1, CacheDataDescription arg2) throws CacheException
   {
      throw new UnsupportedOperationException("MockRegionFactory doesn't support entity regions");
   }

   public QueryResultsRegion buildQueryResultsRegion(String arg0, Properties arg1) throws CacheException
   {
      return new MockGeneralDataRegion(arg0);
   }

   public TimestampsRegion buildTimestampsRegion(String arg0, Properties arg1) throws CacheException
   {
      return new MockGeneralDataRegion(arg0);
   }

   public boolean isMinimalPutsEnabledByDefault()
   {
      return false;
   }

   public long nextTimestamp()
   {
      return 0;
   }

   public void stop()
   {
      // no-op
   }
   
   public class MockGeneralDataRegion implements QueryResultsRegion, TimestampsRegion
   {
      private final String name;
      
      public MockGeneralDataRegion(String name)
      {
         this.name = name;
      }
      
      // new since JPA-2
      public boolean contains(Object arg0)
      {
         // no-op
         return false;
      }
      
      public void evict(Object arg0) throws CacheException
      {
         // no-op
      }

      public void evictAll() throws CacheException
      {
         // no-op
      }

      public Object get(Object arg0) throws CacheException
      {
         return null;
      }

      public void put(Object arg0, Object arg1) throws CacheException
      {
         // no-op
      }

      public void destroy() throws CacheException
      {
         // no-op
      }

      public long getElementCountInMemory()
      {
         return 0;
      }

      public long getElementCountOnDisk()
      {
         return 0;
      }

      public String getName()
      {
         return name;
      }

      public long getSizeInMemory()
      {
         return 0;
      }

      public int getTimeout()
      {
         return 0;
      }

      public long nextTimestamp()
      {
         return 0;
      }

      public Map toMap()
      {      
         return Collections.EMPTY_MAP;
      }
      
   }

}
