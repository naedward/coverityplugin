package com.naedward.gradle.coverityplugin.tasks

import org.gradle.api.*
import org.gradle.api.tasks.*

class CommitDefectsTask extends DefaultTask {
   
   String streamName;
   File xmlConfigFile; 
   File intermediateDir;
   
   public CommitDefectsTask() {
      group = "Coverity"
      description = "Pushes analysis to covbuild:8080"
   }
   
   @TaskAction 
   public void commitDefects() {
      project.task('covCommitDefects', type:Exec) {
         commandLine('cov-commit-defects.exe', '--dir', intermediateDir.absolutePath,
               '--stream', streamName, '-c', xmlConfigFile.absolutePath)
      }
      project.tasks.covCommitDefects.execute()
   }
}
