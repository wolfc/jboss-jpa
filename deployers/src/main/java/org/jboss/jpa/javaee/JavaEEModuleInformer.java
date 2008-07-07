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
package org.jboss.jpa.javaee;

import org.jboss.deployers.structure.spi.DeploymentUnit;

/**
 * Obtain information about a JavaEE module given a deployment unit.
 * The informer should only use meta data to obtain the information
 * being asked.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public interface JavaEEModuleInformer
{
   public enum ModuleType { APP_CLIENT, EJB, JAVA, WEB };
   
   /**
    * Obtain the name of the JavaEE application this module is part of, for
    * example 'foo.ear'.
    * If the deployment unit is not part of a JavaEE application return null.
    * 
    * @param deployment the deployment unit of the module, application or component
    * @return the name of the JavaEE application or null if none
    */
   String getApplicationName(DeploymentUnit deploymentUnit);
   
   /**
    * Obtain the relative path of the deployment unit within the JavaEE application
    * or base deployment directory.
    * The deployment unit is either a JavaEE module or a component of the JavaEE module.
    * If the JavaEE module is part of a JavaEE application return the relative
    * path within the JavaEE application otherwise it is considered a stand alone deployment
    * and return the relative path within the base deployment directory.
    * Note that the relative path includes the module name, for example 'lib/bar.jar'.
    * 
    * @param deployment the deployment unit of the module or a component
    * @return the relative path of the JavaEE module
    */
   String getModulePath(DeploymentUnit deploymentUnit);
   
   /**
    * Obtain the module type of the deployment unit.
    * Based on the meta data available the informer will output the module type
    * of the JavaEE module of which the deployment unit is part.
    * 
    * @param deploymentUnit the deployment unit of the module or a component
    * @return the JavaEE module type
    */
   ModuleType getModuleType(DeploymentUnit deploymentUnit);
}
