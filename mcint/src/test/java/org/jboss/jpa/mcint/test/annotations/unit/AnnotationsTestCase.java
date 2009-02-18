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
package org.jboss.jpa.mcint.test.annotations.unit;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeploymentFactory;
import org.jboss.jpa.mcint.test.annotations.MyMCBean;
import org.jboss.jpa.mcint.test.common.MicroContainerTestHelper;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class AnnotationsTestCase
{
   private static MicroContainerTestHelper delegate;
   
   @AfterClass
   public static void afterClass() throws Exception
   {
      if(delegate != null)
         delegate.tearDown();
      delegate = null;
   }
   
   @BeforeClass
   public static void beforeClass() throws Exception
   {
      delegate = new MicroContainerTestHelper(AnnotationsTestCase.class);
      
      delegate.setUp();
      
      delegate.validate();
      
      deploy("/org/jboss/jpa/mcint/test/annotations");
   }
   
   protected static void deploy(URL url) throws DeploymentException, IOException
   {
      VirtualFile file = VFS.getRoot(url);
      VFSDeployment deployment = VFSDeploymentFactory.getInstance().createVFSDeployment(file);
      delegate.getMainDeployer().deploy(deployment);      
   }
   
   protected static void deploy(String spec) throws DeploymentException, IOException 
   {
      URL url = AnnotationsTestCase.class.getResource(spec);
      if(url == null)
         throw new IllegalArgumentException("Can't find resource '" + spec + "'");
      deploy(url);
   }
   
   @Test
   public void test1()
   {
      MyMCBean bean = delegate.getBean("MyMCBean", MyMCBean.class);
      assertNotNull(bean);
      assertNotNull("entity manager factory was not injected", bean.getEntityManagerFactory());
   }
}
