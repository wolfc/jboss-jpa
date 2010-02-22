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
package org.jboss.jpa.deployment.vfs3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jpa.deployment.PersistenceDeployment;
import org.jboss.jpa.deployment.PersistenceUnitDeployment;
import org.jboss.metadata.jpa.spec.PersistenceUnitMetaData;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileVisitor;
import org.jboss.vfs.VisitorAttributes;

/**
 * Instance of PersistenceUnitDeployment that uses VFS2 roots 
 * 
 * @author John Bailey
 */
public class PersistenceUnitDeploymentImpl extends PersistenceUnitDeployment
{

   /**
    * Constructs a new PersistenceUnitDeploymentImpl
    * 
    * @param initialContext
    * @param deployment
    * @param explicitEntityClasses
    * @param metadata
    * @param kernelName
    * @param deploymentUnit
    * @param defaultPersistenceProperties
    */
   public PersistenceUnitDeploymentImpl(InitialContext initialContext, PersistenceDeployment deployment,
         List<String> explicitEntityClasses, PersistenceUnitMetaData metadata, String kernelName,
         VFSDeploymentUnit deploymentUnit, Properties defaultPersistenceProperties)
   {
      super(initialContext, deployment, explicitEntityClasses, metadata, kernelName, deploymentUnit, defaultPersistenceProperties);
   }

   /** {@inheritdoc} */
   @Override
   protected URL getPersistenceUnitURL()
   {
      try {
         VirtualFile metaData = di.getMetaDataFile("persistence.xml");
         assert metaData != null : "Can't find persistence.xml in " + di;
         return getJarFileUrl(metaData.getParent().getParent());
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /** {@inheritdoc} */
   @Override
   protected URL getRelativeURL(String jar)
   {
      try {
         return new URL(jar);
      }
      catch (MalformedURLException e) {
         try {
            VirtualFile deploymentUnitFile = di.getFile("");
            VirtualFile parent = deploymentUnitFile.getParent();
            VirtualFile baseDir = (parent != null ? parent : deploymentUnitFile);
            VirtualFile jarFile = baseDir.getChild(jar);
            if (jarFile == null)
               throw new RuntimeException("could not find child '" + jar + "' on '" + baseDir + "'");
            return getJarFileUrl(jarFile);
         }
         catch (Exception e1) {
            throw new RuntimeException("could not find relative path: " + jar, e1);
         }
      }
   }
   
   /**
    * This method is a hack.  This should be replace with a hook in VFS to fully explode a 
    * zip filesystem.  Better yet, this should be removed once there is a way to inject a VFS 
    * based JarScanner into hibernate.
    */
   private URL getJarFileUrl(VirtualFile virtualFile) throws MalformedURLException, IOException {
      if(virtualFile.isDirectory()) 
      {
         VirtualFileVisitor visitor = new VirtualFileVisitor() 
         {
            public void visit(VirtualFile virtualFile)
            {
               try 
               {
                  virtualFile.getPhysicalFile();
               }
               catch (IOException e) 
               {
                  throw new RuntimeException("Failed to force explosion of VirtualFile: " + virtualFile, e);
               }
            }
            
            public VisitorAttributes getAttributes()
            {
               return VisitorAttributes.RECURSE_LEAVES_ONLY;
            }
         };
         virtualFile.visit(visitor);
         return virtualFile.getPhysicalFile().toURI().toURL();
      }
      return virtualFile.toURL();
   }

}
