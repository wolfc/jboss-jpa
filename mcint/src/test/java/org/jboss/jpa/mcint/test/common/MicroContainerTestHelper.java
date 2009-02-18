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
package org.jboss.jpa.mcint.test.common;

import java.net.URL;

import org.jboss.dependency.spi.ControllerState;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.kernel.spi.deployment.KernelDeployment;
import org.jboss.test.kernel.junit.MicrocontainerTestDelegate;

/**
 * TODO: use JBoss Bootstrap
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class MicroContainerTestHelper extends MicrocontainerTestDelegate
{
   private MainDeployer mainDeployer;

   public MicroContainerTestHelper(Class<?> clazz) throws Exception
   {
      super(clazz);
   }
   
   protected KernelDeployment deploy(String path) throws Exception
   {
      URL url = MicroContainerTestHelper.class.getResource(path);
      KernelDeployment deployment = deploy(url);
      validate(deployment);
      return deployment;
   }
   
   public <T> T getBean(Object name, Class<T> expectedType)
   {
      return getBean(name, ControllerState.INSTALLED, expectedType);
   }
   
   public MainDeployer getMainDeployer()
   {
      return mainDeployer;
   }
   
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      
      deploy("/conf/bootstrap/annotationhandler.xml");
      
      deploy("/conf/bootstrap/maindeployer.xml");
      
      deploy("/conf/bootstrap/jpa-annotations.xml");
      
      mainDeployer = getBean("MainDeployer", ControllerState.INSTALLED, MainDeployer.class);
   }
   
   @Override
   public void validate() throws Exception
   {
      super.validate();
   }
   
   protected void validate(KernelDeployment deployment) throws Exception
   {
      try
      {
         deployer.validate(deployment);
      }
      catch(Error e)
      {
         throw e;
      }
      catch(RuntimeException e)
      {
         throw e;
      }
      catch(Exception e)
      {
         throw e;
      }
      catch(Throwable t)
      {
         throw new RuntimeException(t);
      }
   }
}
