package naedward.gradle.coverityplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.*

class ManageEmitTask extends DefaultTask {
   public ManageEmitTask() {
      group = "Coverity"
      description = "Manage Java intermediate directory"
   }

   @TaskAction
   protected void manageEmit() {
      for (def exclude : project.coverity.excludes) {
         project.exec {
            commandLine "cov-manage-emit",
                        "--java",
                        "--dir",
                        project.coverity.intermediateDir.absolutePath,
                        "--tu-pattern",
                        "file('" + exclude + "')",
                        "delete"
         }
      }
   }
}
