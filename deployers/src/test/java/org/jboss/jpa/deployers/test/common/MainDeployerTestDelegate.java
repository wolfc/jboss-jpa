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

import java.net.URL;

import org.jboss.dependency.spi.ControllerState;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.kernel.spi.deployment.KernelDeployment;
import org.jboss.test.kernel.junit.MicrocontainerTestDelegate;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class MainDeployerTestDelegate extends MicrocontainerTestDelegate
{
   private MainDeployer mainDeployer;

   public MainDeployerTestDelegate(Class<?> clazz) throws Exception
   {
      super(clazz);
   }
   
   public KernelDeployment deploy(String resource) throws Exception
   {
      URL url = clazz.getResource(resource);
      if(url == null)
         throw new IllegalArgumentException("Can't find resource '" + resource + "'");
      try
      {
         return deploy(url);
      }
      finally
      {
         validate();
      }
   }
   
   public KernelDeployment deploy(URL url) throws Exception
   {
      return super.deploy(url);
   }
   
   public <T> T getBean(Object name, Class<T> expectedType)
   {
      return getBean(name, ControllerState.INSTALLED, expectedType);
   }
   
   public MainDeployer getMainDeployer()
   {
      return mainDeployer;
   }
   
   public void setUp() throws Exception
   {
      super.setUp();
      
      URL url = MainDeployerTestDelegate.class.getResource("MainDeployer-beans.xml");
      deploy(url);
      validate();
      
      mainDeployer = getBean("MainDeployer", ControllerState.INSTALLED, MainDeployer.class);
   }
}
