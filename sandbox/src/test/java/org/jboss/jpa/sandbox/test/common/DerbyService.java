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
package org.jboss.jpa.sandbox.test.common;

import java.sql.Connection;

import javax.naming.InitialContext;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class DerbyService
{
   private static final Logger log = Logger.getLogger(DerbyService.class);
   
   private String className = "org.apache.derby.jdbc.EmbeddedDataSource";
   private String databaseName = "./target/derby.db";
   private boolean createDatabase = true;
   private String jndiName = "java:/DefaultDS";
   
   private EmbeddedDataSource ds;
   private InitialContext ctx;
   
   public void create() throws Exception
   {
      log.info("Creating derby service");
      
      System.setProperty("derby.stream.error.file", "target/derby.log");
      
      Class<? extends EmbeddedDataSource> cls = (Class<? extends EmbeddedDataSource>) Class.forName(className);
      this.ds = cls.newInstance();
      ds.setDatabaseName(databaseName);
      if(createDatabase)
         ds.setCreateDatabase("create");
      
      ctx = new InitialContext();
   }
   
   public void destroy() throws Exception
   {
      log.info("Destroying derby service");
      
      ctx.close();
      ctx = null;
      ds = null;
   }
   
   public void setJndiName(String jndiName)
   {
      this.jndiName = jndiName;
   }
   
   public void start() throws Exception
   {
      log.info("Starting derby service");
      
      // Make sure that database is created before we go on.
      Connection con = ds.getConnection();
      con.close();
      
      ctx.bind(jndiName, ds);
   }
   
   public void stop() throws Exception
   {
      log.info("Stopping derby service");
      
      ctx.unbind(jndiName);
   }
}
