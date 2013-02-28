/******************************************************************************
* Copyright 2013 Neal Edwards
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
******************************************************************************/

package com.naedward.gradle.coverityplugin;

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.execution.TaskExecutionGraph
import com.naedward.gradle.coverityplugin.tasks.*

import org.gradle.api.tasks.*

/**
 * Adds tasks
 * covEmit
 * covAnalyze
 * covCommitDefects
 * 
 * Each task utilizes a configure intermediate directory.  Defaults to './intDir'
 * and may be configured outside the plugin by
 * {@code 
 *  coverity {
 *       intDir = <path>  //This is the intermediate directory to store coverity custom files
 *       String commitDefectsStreamName //When using covCommitDefects, this determines which stream to push to
 *       String commitDefectsXmlConfig //When using covCommitDefects, this let's you specify the host/port, user/pass in an xml file
 *     }
 *   }
 * 
 * Example commitDefectsXmlConfig...
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE coverity SYSTEM "coverity_config.dtd">
 * <coverity>
 * <config>
 *    <cim>
 *       <host>covbuild</host>                                
 *       <client_security>
 *           <user>name</user>                     
 *           <password>password</password>            
 *       </client_security>
 *       <commit>
 *           <port>9090</port>  
 *       </commit>
 *   </cim>
 * </config>
 * </coverity>
 * }
 * 
 * If an xml config file is not specified the user will be prompted to enter a username and password.
 * 
 * When running this through bamboo the -PwithBamboo property must be specified this will cause the 
 * covCommitDefects to ignore the commitDefectsXmlConfig and use a hardcoded one in the ateapp home
 * drive.
 * 
 * The property coverityStreamName can be used to override the commitDefectsStreamName configuration.
 * 
 * This plugin currently assumes that cov-emit-java, cov-analyze, cov-commit-defects
 * are available on the command line of your execution environment.
 * 
 * 
 * @author nedwards
 *
 */
class CoverityPlugin implements Plugin<Project> {

   @Override
   public void apply(Project project) {
      project.extensions.create("coverity", CoverityPluginExtension)

      project.afterEvaluate {
         configureEmitTask(project);
         configureAnalyzeTask(project);
         project.task('covCommit', type:CommitDefectsTask) {

            project.tasks.covCommit.dependsOn(project.tasks.covAnalyze)
            streamName = project.coverity.commitDefectsStreamName
            xmlConfigFile = project.coverity.commitDefectsXmlConfig;
            intermediateDir = project.coverity.intermediateDir;
         }
      }
   }

   protected void configureAnalyzeTask(Project project) {
      project.task('covAnalyze', type:Exec) {
         project.tasks.covAnalyze.dependsOn(project.tasks.covEmit)
         logger.debug("Cov Analysis Start")
         group = "Coverity"
         description = "Performs the coverity analysis step."
         commandLine 'cov-analyze-java', "-j", "auto", "--dir", project.coverity.intermediateDir, "--all"
      }
   }

   protected void configureEmitTask(Project project) {
      project.task('covEmit', type:Exec) {
         group = "Coverity"
         description = "Processes all the compiled files ready to be analyzed"

         project.tasks.covEmit.dependsOn(project.tasks.build)
         FileCollection coverityClasspath;
         StringBuilder sourcePaths = new StringBuilder();
         StringBuilder outputPaths = new StringBuilder();

         coverityClasspath = project.sourceSets.main.runtimeClasspath
         if (project.coverity.includeTestSource) {
            FileCollection diff = project.sourceSets.main.runtimeClasspath.minus(project.sourceSets.test.runtimeClasspath);
            coverityClasspath.plus(diff)
         }

         Set<File> coveritySrc = project.sourceSets.main.java.srcDirs

         if (project.coverity.includeTestSource) {
            coveritySrc.addAll(project.sourceSets.test.java.srcDirs)
         }

         String pathSep = File.pathSeparator
         for (File dir: coveritySrc) {
            if (!project.coverity.includeAutogenSource) {
               if (dir.getPath().contains("autogen")) {
                  continue
               }
            }
            sourcePaths.append(dir.getCanonicalPath());
            sourcePaths.append(pathSep);
         }
         sourcePaths.deleteCharAt(sourcePaths.lastIndexOf(File.pathSeparator));

         outputPaths.append( project.sourceSets.main.output.classesDir.getPath());
         if (project.coverity.includeTestSource) {
            outputPaths.append( pathSep);
            outputPaths.append( project.sourceSets.test.output.classesDir.getPath());
         }

         commandLine 'cov-emit-java', '--dir', project.coverity.intermediateDir, '--classpath', coverityClasspath.getAsPath(), '--findsource', sourcePaths.toString(), '--compiler-outputs', outputPaths.toString()
      }
   }
}

class CoverityPluginExtension {
   File intermediateDir = new File('intDir')
   String commitDefectsStreamName
   File commitDefectsXmlConfig
   boolean includeTestSource = false
   boolean includeAutogenSource = false
}