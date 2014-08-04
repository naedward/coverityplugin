package naedward.gradle.coverityplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.*;

class AnalyzeTask extends DefaultTask {

   File intermediateDir;
   int numWorkers;
   File coverityHome;

   public AnalyzeTask() {
      group = "Coverity"
      description = "Performs a coverity analysis on the intermediate directory."
   }
   
   
   @TaskAction
   public void analyze() {
      def String workerArg;
      if ((numWorkers == null) || (numWorkers <= 0)) {
         workerArg = "auto"   
      } else {
         workerArg = numWorkers.toString()
      }
       
      project.task('analyze', type:Exec) {
          String binDir = coverityHome == null ? '' : "${coverityHome}/bin/"
          commandLine "${binDir}cov-analyze-java", "-j", workerArg, "--dir", intermediateDir.absolutePath, "--all"
      }
      project.tasks.analyze.execute()
   }
}
