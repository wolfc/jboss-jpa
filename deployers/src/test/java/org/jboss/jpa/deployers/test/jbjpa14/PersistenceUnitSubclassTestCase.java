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
package org.jboss.jpa.deployers.test.jbjpa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.jpa.deployers.PersistenceDeployer;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;
import org.junit.Test;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class PersistenceUnitSubclassTestCase
{
   private static void test(String name, PersistenceUnitMetaData metaData) throws DeploymentException
   {
      PersistenceDeployer deployer = new PersistenceDeployer();
      MockDeploymentContext deploymentContext = new MockDeploymentContext(name);
      
      List<PersistenceUnitMetaData> persistenceUnits = new ArrayList<PersistenceUnitMetaData>();
      persistenceUnits.add(metaData);
      
      PersistenceMetaData attachment = new PersistenceMetaData();
      attachment.setPersistenceUnits(persistenceUnits);
      
      deploymentContext.getTransientAttachments().addAttachment(PersistenceMetaData.class, attachment);
      DeploymentUnit unit = deploymentContext.getDeploymentUnit();
      deployer.deploy(unit);
      
      String componentName = "org.jboss.metadata.jpa.spec.PersistenceUnitMetaData." + metaData.getName();
      DeploymentUnit component = unit.getComponent(componentName);
      assertNotNull("can't find component " + componentName, component);
      assertEquals("expected PersistenceMetaData and PersistenceUnitMetaData attachments", 2, component.getAttachments().size());
      PersistenceUnitMetaData pu = component.getAttachment(PersistenceUnitMetaData.class);
      assertNotNull("can't find PersistenceUnitMetaData attachment", pu);
   }
   
   private static MockPersistenceUnitMetaData mockUnit(String name)
   {
      MockPersistenceUnitMetaData pu = new MockPersistenceUnitMetaData();
      pu.setName(name);
      return pu;
   }
   
   private static PersistenceUnitMetaData unit(String name)
   {
      PersistenceUnitMetaData pu = new PersistenceUnitMetaData();
      pu.setName(name);
      return pu;
   }
   
   @Test
   public void testNormal() throws DeploymentException
   {
      test("testNormal", unit("normalunit"));
   }
   
   @Test
   public void testSubclass() throws DeploymentException
   {
      test("testSubclass", mockUnit("mockunit"));
   }
}
