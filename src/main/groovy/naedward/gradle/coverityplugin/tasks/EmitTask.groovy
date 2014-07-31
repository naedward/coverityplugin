package naedward.gradle.coverityplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.*;

class EmitTask extends DefaultTask {
   File intermediateDir;
   
   public EmitTask() {
      group = "Coverity";
      description = "Processes all the compiled files ready to be analyzed";
   }
   
   @TaskAction
   protected void emit() {
      project.task('emit', type:Exec) {
         FileCollection coverityClasspath;
         StringBuilder sourcePaths = new StringBuilder();
         StringBuilder outputPaths = new StringBuilder();

         coverityClasspath = project.sourceSets.main.runtimeClasspath
         if (project.coverity.includeTestSource) {
            FileCollection diff = project.sourceSets.main.runtimeClasspath.minus(project.sourceSets.test.runtimeClasspath);
            coverityClasspath.plus(diff)
         }

         Set<File> coveritySrc = project.sourceSets.main.allJava.srcDirs

         if (project.coverity.includeTestSource) {
            coveritySrc.addAll(project.sourceSets.test.allJava.srcDirs)
         }

         String pathSep = File.pathSeparator
         for (File dir: coveritySrc) {
            if (!project.coverity.includeAutogenSource) {
               if (dir.getPath().contains("autogen")) {
                  continue
               }
            }
            if (!dir.exists()) continue
            sourcePaths.append(dir.getCanonicalPath());
            sourcePaths.append(pathSep);
         }
         sourcePaths.deleteCharAt(sourcePaths.lastIndexOf(File.pathSeparator));

         outputPaths.append( project.sourceSets.main.output.classesDir.getPath());
         if (project.coverity.includeTestSource) {
            outputPaths.append( pathSep);
            outputPaths.append( project.sourceSets.test.output.classesDir.getPath());
         }

         commandLine 'cov-emit-java', '--dir', intermediateDir, '--classpath', coverityClasspath.getAsPath(), '--findsource', sourcePaths.toString(), '--compiler-outputs', outputPaths.toString()
      }
      project.tasks.emit.execute()
   }

}
