package naedward.gradle.coverityplugin.tasks

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.*;

class EmitTask extends DefaultTask {
   File intermediateDir;
   File coverityHome;
   FileCollection bootClasspath;
   FileCollection sourceDirectories;
   FileCollection classDirectories;
   FileCollection coverityClasspath;
   
   public EmitTask() {
      group = "Coverity";
      description = "Processes all the compiled files ready to be analyzed";
   }
   
   @TaskAction
   protected void emit() {
      project.task('emit', type:Exec) {
         String binDir = coverityHome == null ? '' : "${coverityHome}/bin/"
         def command = ["${binDir}cov-emit-java", 
            '--dir', intermediateDir, 
            '--findsource', sourceDirectories.getAsPath(), 
            '--compiler-outputs', classDirectories.getAsPath()]
         
         if (coverityClasspath != null && !coverityClasspath.isEmpty()) {
            command << '--classpath' << coverityClasspath.getAsPath()
         }
         if (bootClasspath != null) {
            command << '--bootclasspath' << bootClasspath.getAsPath()
         }
         logger.debug('cov-emit-java command={}', command)
         commandLine command
      }
      project.tasks.emit.execute()
   }
}
