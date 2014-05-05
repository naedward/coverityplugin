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
package naedward.gradle.coverityplugin.tasks

import org.gradle.api.*
import org.gradle.api.tasks.*

class CommitDefectsTask extends DefaultTask {
   
   String streamName;
   File xmlConfigFile; 
   File intermediateDir;
   File coverityHome;
   String covConnectHost
   String covConnectDataPort
   String covConnectUser
   String covConnectPassword

   public CommitDefectsTask() {
      group = "Coverity"
      description = "Pushes analysis to covbuild:8080"
   }
   
   @TaskAction 
   public void commitDefects() {
      project.task('covCommitDefects', type:Exec) {
          String binDir = coverityHome == null ? '' : "${coverityHome}/bin/"
          def command = ["${binDir}cov-commit-defects", '--dir', intermediateDir.absolutePath,
                         '--stream', streamName]
          if (xmlConfigFile != null) {
             command << '-c' << xmlConfigFile.absolutePath
          } else {
             command << '--host' << covConnectHost
             command << '--dataport' << covConnectDataPort
             command << '--user' << covConnectUser
             command << '--password' << covConnectPassword
          }
          commandLine(command)
      }
      project.tasks.covCommitDefects.execute()
   }
}
