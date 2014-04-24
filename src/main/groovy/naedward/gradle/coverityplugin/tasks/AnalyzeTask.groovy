package naedward.gradle.coverityplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.*;

class AnalyzeTask extends DefaultTask {

   File intermediateDir;
   int numberOfWorkers = 2;
   File coverityHome;

   public AnalyzeTask() {
      group = "Coverity"
      description = "Performs a coverity analysis on the intermediate directory."
   }
   
   
   @TaskAction
   public void analyze() {
      project.task('analyze', type:Exec) {
          String binDir = coverityHome == null ? '' : "${coverityHome}/bin/"
          commandLine "${binDir}cov-analyze-java", "-j", numberOfWorkers, "--dir", intermediateDir.absolutePath, "--all"
      }
      project.tasks.analyze.execute()
   }
}
