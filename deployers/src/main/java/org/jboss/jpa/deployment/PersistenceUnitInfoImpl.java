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
package org.jboss.jpa.deployment;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo
{
   private String entityManagerName;
   private DataSource jtaDataSource;
   private DataSource nonJtaDataSource;
   private List<String> mappingFileNames;
   private List<URL> jarFiles;
   private List<String> entityclassNames;
   private Properties properties;
   private ClassLoader classLoader;
   private String persistenceProviderClassName;
   private PersistenceUnitTransactionType transactionType;
   private URL persistenceUnitRootUrl;
   private boolean excludeClasses;

   public PersistenceUnitInfoImpl()
   {
   }

   public void addTransformer(ClassTransformer transformer)
   {
      //throw new RuntimeException("NOT IMPLEMENTED");
   }

   public ClassLoader getNewTempClassLoader()
   {
      return null;
   }

   public String getPersistenceProviderClassName()
   {
      return persistenceProviderClassName;
   }

   public void setPersistenceProviderClassName(String persistenceProviderClassName)
   {
      this.persistenceProviderClassName = persistenceProviderClassName;
   }

   public String getPersistenceUnitName()
   {
      return entityManagerName;
   }

   public void setPersistenceUnitName(String entityManagerName)
   {
      this.entityManagerName = entityManagerName;
   }

   public DataSource getJtaDataSource()
   {
      return jtaDataSource;
   }

   public void setJtaDataSource(DataSource jtaDataSource)
   {
      this.jtaDataSource = jtaDataSource;
   }

   public DataSource getNonJtaDataSource()
   {
      return nonJtaDataSource;
   }

   public void setNonJtaDataSource(DataSource nonJtaDataSource)
   {
      this.nonJtaDataSource = nonJtaDataSource;
   }

   public List<String> getMappingFileNames()
   {
      return mappingFileNames;
   }

   public void setMappingFileNames(List<String> mappingFileNames)
   {
      this.mappingFileNames = mappingFileNames;
   }

   public List<URL> getJarFileUrls()
   {
      return jarFiles;
   }

   public void setJarFiles(List<URL> jarFiles)
   {
      this.jarFiles = jarFiles;
   }

   public List<String> getManagedClassNames()
   {
      return entityclassNames;
   }

   public void setManagedClassnames(List<String> entityclassNames)
   {
      this.entityclassNames = entityclassNames;
   }

   public Properties getProperties()
   {
      return properties;
   }

   public void setProperties(Properties properties)
   {
      this.properties = properties;
   }

   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

   public void setClassLoader(ClassLoader classLoader)
   {
      this.classLoader = classLoader;
   }

   public PersistenceUnitTransactionType getTransactionType()
   {
      return transactionType;
   }

   public void setTransactionType(PersistenceUnitTransactionType transactionType)
   {
      this.transactionType = transactionType;
   }

   public URL getPersistenceUnitRootUrl()
   {
      return persistenceUnitRootUrl;
   }

   public void setPersistenceUnitRootUrl(URL persistenceUnitRootUrl)
   {
      this.persistenceUnitRootUrl = persistenceUnitRootUrl;
   }

   public boolean excludeUnlistedClasses()
   {
      return excludeClasses;
   }

   public void setExcludeUnlistedClasses(boolean excludeClasses)
   {
      this.excludeClasses = excludeClasses;
   }

}
