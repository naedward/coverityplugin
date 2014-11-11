# Coverity Gradle Plugin

The plugin provides tasks required for analyzing & reporting defects in Java code using [Coverity](http://www.coverity.com/).
This plugin wraps the coverity provided cov-emit-java, cov-analyze-java, and cov-commit-defects command line calls.

## Integrating coverity plugin with gradle
Building the jar manually
-------------------------
* Clone the repository
* Run gradlew jar
* Copy build/libs/coverityplugin-<version>.jar to your favorite artifact repo

Use artifact from github
------------------------

    buildscript {
      repositories {
        url //Path to local artifact
      }
      dependencies {
        classpath 'naedward.gradle:coverityplugin:0.7.0'
      }
    }
    apply plugin: 'coverity'

## Using the plugin with your project

To use the Coverity plugin, copy the coverityplugin-xxx.jar to `lib/plugins` directory under the root project. Then, add the following in your root project's build script:

    apply plugin: 'coverity'

    buildscript {
        repositories {
            flatDir { dirs rootProject.projectDir.toString() + '/lib/plugins' }
        }

        dependencies {
            classpath ':coverityplugin-0.5.0'
        }
    }


Coverity tasks can be configured using `coverity` extension. Typical usage will be:

    coverity {
        intermediateDir = file("${buildDir}/coverity")
        coverityHome = System.getProperty("COVERITY_HOME")
        covConnectHost = System.getProperty("COVERITY_HOST")
        covConnectDataPort = System.getProperty("COVERITY_PORT")
        covConnectUser = System.getProperty("COVERITY_USER")
        covConnectPassword = System.getProperty("COVERITY_PASSWORD")
        commitDefectsStreamName = System.getProperty("COVERITY_STREAM_NAME")
        classDirectories = files("${buildDir}/classes")
        sourceDirectories = files("src")
        coverityClasspath = project.sourceSets.main.compileClasspath + project.sourceSets.test.compileClasspath
    }

Alternatively, the Coverity Connect details can be specified using a configuration file too:

    coverity {
        intermediateDir = file("${buildDir}/coverity")
        coverityHome = System.getProperty("COVERITY_HOME")
        commitDefectsXmlConfig = file("/absolute/path/to/config/file")
        commitDefectsStreamName = System.getProperty("COVERITY_STREAM_NAME")
        classDirectories = files("${buildDir}/classes")
        sourceDirectories = files("src")
        coverityClasspath = project.sourceSets.main.compileClasspath + project.sourceSets.test.compileClasspath
    }

A sample XML file is:

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE coverity SYSTEM "coverity_config.dtd">
    <coverity>
    <config>
       <cim>
          <host>coverity.example.com</host>
          <client_security>
              <user>my-user</user>
              <password>myPassword</password>
          </client_security>
          <commit>
              <port>9090</port>
          </commit>
      </cim>
    </config>
    </coverity>


## Tasks

**Before running the analysis tasks, remember to build your class files as covEmit task expects the .class files to be present.**

The Coverity plugin defines the following tasks:

* `covEmit`: Runs `cov-emit-java` Coverity command
* `covAnalyze`: Runs `cov-analyze-java` Coverity command. This depends on `covEmit` task.
* `covCommit`: Runs `cov-commit-defects` Coverity command. This depends on `covAnalyze` task.
* `covManageEmit`: Manages intermediate directory.
* `covClean`: Deletes `intermediateDir` directory.

## Configuring Coverity Properties

Coverity Gradle Plugin can be configured by passing a closure to `coverity` extension.

* `intermediateDir`: The directory, where all the coverity files will be written. Defaults to `intDir`
* `commitDefectsStreamName`: The stream name to be used while committing the defects to Coverity Connect.
* `commitDefectsXmlConfig`: The configuration file containing the details of Coverity Connect.
* `excludes`: Specify regex pattern to exclude files from being analyzed. Defaults to `""` (empty string).
* `coverityHome`: The location of cov-analysis installation. If cov-analysis/bin directory is in the PATH,
 this can be left null.
* `bootClasspath`: The bootclasspath to be used during `cov-emit-java`. If the default models included in
 coverity is enough, leave this null.
* `covConnectHost`: The hostname/IP Address of Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `covConnectDataPort`: The datport of Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `covConnectUser`: The username in Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `covConnectPassword`: The password of `covConnectUser` in Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `analyzeNumWorkers`: Number of worker threads to use when using analyze task. Defaults to `auto`. Note: VM's have issues with --auto
* `sourceDirectories`: FileCollection of all directories where coverity should look for source files
* `classDirectories`: FileCollection of all directories where class files should be found
* `coverityClasspath`: FileCollection of all directories where coverity should look for additional symbols.  Often project.sourceSets.main.compileClasspath


