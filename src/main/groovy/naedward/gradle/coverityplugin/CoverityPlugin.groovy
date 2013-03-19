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

package naedward.gradle.coverityplugin;

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.execution.TaskExecutionGraph
import naedward.gradle.coverityplugin.tasks.*

import org.gradle.api.tasks.*

/**
 * Adds tasks
 * covEmit
 * covAnalyze
 * covCommit
 * 
 * Each task utilizes a configure intermediate directory.  Defaults to './intDir'
 * and may be configured outside the plugin by
 * {@code 
 *  coverity {
 *       File intermediateDir = <path>  //This is the intermediate directory to store coverity custom files
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

      project.task('covEmit', type:EmitTask) {
         project.tasks.covEmit.dependsOn(project.tasks.compileJava);
         doFirst {
            intermediateDir = project.coverity.intermediateDir;
         }
      }

      project.task('covAnalyze', type:AnalyzeTask)  {
         project.tasks.covAnalyze.dependsOn(project.tasks.covEmit);
         doFirst {
            intermediateDir = project.coverity.intermediateDir;
         }
      }

      project.task('covCommit', type:CommitDefectsTask) {
         project.tasks.covCommit.dependsOn(project.tasks.covAnalyze)
         doFirst {
            streamName = project.coverity.commitDefectsStreamName
            xmlConfigFile = project.coverity.commitDefectsXmlConfig;
            intermediateDir = project.coverity.intermediateDir;
         }
      }
      project.task('covClean', type: Delete) {
         doFirst {
            delete project.coverity.intermediateDir;
         }
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