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
   boolean includeSubProjects;
   String bootClasspath;
   
   public EmitTask() {
      group = "Coverity";
      description = "Processes all the compiled files ready to be analyzed";
   }
   
   @TaskAction
   protected void emit() {
      project.task('emit', type:Exec) {
         FileCollection coverityClasspathList;
         StringBuilder sourcePaths = new StringBuilder();
         StringBuilder outputPaths = new StringBuilder();
         Set<String> processedProjects = new HashSet<>();

         coverityClasspathList = EmitTask.collectProjectPaths(project, includeSubProjects, sourcePaths, outputPaths, processedProjects)

         sourcePaths.deleteCharAt(sourcePaths.lastIndexOf(File.pathSeparator));
         outputPaths.deleteCharAt(outputPaths.lastIndexOf(File.pathSeparator));

         def coverityClasspath = coverityClasspathList.getAsPath()
         logger.debug('coverityClasspath={}', coverityClasspath)
         String binDir = coverityHome == null ? '' : "${coverityHome}/bin/"
         def command = ["${binDir}cov-emit-java", '--dir', intermediateDir, '--classpath', coverityClasspath, '--findsource', sourcePaths.toString(), '--compiler-outputs', outputPaths.toString()]
         if (bootClasspath != null) {
            command << '--bootclasspath' << bootClasspath
         }
         logger.debug('cov-emit-java command={}', command)
         commandLine command
      }
      project.tasks.emit.execute()
   }

   private static FileCollection collectProjectPaths(Project project, boolean includeSubProjects, StringBuilder sourcePaths, StringBuilder outputPaths, Set<String> processedProjects) {
      processedProjects.add(project.name)
      FileCollection coverityClasspath = null
      if (project.plugins.hasPlugin('java')) {
         coverityClasspath = collectPaths(project, sourcePaths, outputPaths)
      }

      if (includeSubProjects) {
         project.subprojects.findAll({ !processedProjects.contains(it.name) }).each {subProject ->

            def projectPaths = collectProjectPaths(subProject, includeSubProjects, sourcePaths, outputPaths, processedProjects)
            coverityClasspath = coverityClasspath == null ? projectPaths : projectPaths == null ? coverityClasspath : coverityClasspath + projectPaths
         }
      }
      return coverityClasspath;
   }

   private static FileCollection collectPaths(Project project, StringBuilder sourcePaths, StringBuilder outputPaths) {
      FileCollection coverityClasspath = project.sourceSets.main.compileClasspath + project.sourceSets.main.runtimeClasspath
      if (project.coverity.includeTestSource) {
         coverityClasspath += project.sourceSets.test.compileClasspath + project.sourceSets.main.runtimeClasspath
      }

      Set<File> coveritySrc = project.sourceSets.main.java.srcDirs

      if (project.coverity.includeTestSource) {
         coveritySrc.addAll(project.sourceSets.test.java.srcDirs)
      }

      String pathSep = File.pathSeparator
      for (File dir : coveritySrc) {
         if (!project.coverity.includeAutogenSource) {
            if (dir.getPath().contains("autogen")) {
               continue
            }
         }
         sourcePaths.append(dir.getCanonicalPath());
         sourcePaths.append(pathSep);
      }

      outputPaths.append(project.sourceSets.main.output.classesDir.getPath());
      if (project.coverity.includeTestSource) {
         outputPaths.append(pathSep);
         outputPaths.append(project.sourceSets.test.output.classesDir.getPath());
      }
      outputPaths.append(pathSep);
      coverityClasspath
   }

}
