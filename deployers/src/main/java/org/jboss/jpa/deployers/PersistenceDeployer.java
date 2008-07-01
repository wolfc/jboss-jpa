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
package org.jboss.jpa.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.deployment.PersistenceDeployment;
import org.jboss.metadata.jpa.spec.PersistenceMetaData;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class PersistenceDeployer extends AbstractSimpleRealDeployer<PersistenceMetaData>
{
   public PersistenceDeployer()
   {
      super(PersistenceMetaData.class);
      addOutput(BeanMetaData.class);
   }

   @Override
   public void deploy(DeploymentUnit unit, PersistenceMetaData deployment) throws DeploymentException
   {
      PersistenceDeployment bean = new PersistenceDeployment((VFSDeploymentUnit) unit, null, deployment);
      BeanMetaDataBuilder bmdb = BeanMetaDataBuilder.createBuilder("PersistenceDeployment", PersistenceDeployment.class.getName());
      bmdb.setConstructorValue(bean);
//      AbstractBeanMetaData bmd = new AbstractBeanMetaData("PersistenceDeployment", PersistenceDeployment.class.getName());
//      AbstractConstructorMetaData constructor = new AbstractConstructorMetaData();
//      constructor.setValueObject(bean);
      unit.addAttachment(BeanMetaData.class, bmdb.getBeanMetaData());
   }
}
