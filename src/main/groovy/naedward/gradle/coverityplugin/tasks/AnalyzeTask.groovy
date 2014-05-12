package naedward.gradle.coverityplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.*;

class AnalyzeTask extends DefaultTask {

   File intermediateDir;
   int numWorkers;
   
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
         commandLine 'cov-analyze-java', "-j", workerArg, "--dir", intermediateDir.absolutePath, "--all"
      }
      project.tasks.analyze.execute()
   }
}
