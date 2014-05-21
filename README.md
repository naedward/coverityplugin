# Coverity Gradle Plugin

The plugin provides tasks required for analyzing & reporting defects in Java code using [Coverity](http://www.coverity.com/).

## Usage

To use the Coverity plugin, add the following in your root project's build script:

    apply plugin: 'clover'

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
    }

Alternatively, the Coverity Connect details can be specified using a configuration file too:

    coverity {
        intermediateDir = file("${buildDir}/coverity")
        coverityHome = System.getProperty("COVERITY_HOME")
        commitDefectsXmlConfig = file("/absolute/path/to/config/file")
        commitDefectsStreamName = System.getProperty("COVERITY_STREAM_NAME")
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


If the Coverity plugin is applied in the root project, it'll recurse through all the sub-projects & include the sources
and libraries while executing `coverity-emit-java` command. If you want to apply Coverity only to selected projects,
apply the plugin to those projects & disable including sub-projects:

    coverity {
        ...
        includeSubProjects = false
        ...
    }

## Tasks

**Before running the analysis tasks, remember to run `compileJava` task as covEmit task expects the .class files to be present.**

The Coverity plugin defines the following tasks:

* `covEmit`: Runs `cov-emit-java` Coverity command
* `covAnalyze`: Runs `cov-analyze-java` Coverity command. This depends on `covEmit` task.
* `covCommit`: Runs `cov-commit-defects` Coverity command. This depends on `covAnalyze` task.
* `covClean`: Deletes `intermediateDir` directory.

## Configuring Coverity Properties

Coverity Gradle Plugin can be configured by passing a closure to `coverity` extension.

* `intermediateDir`: The directory, where all the coverity files will be written. Defaults to `intDir`
* `commitDefectsStreamName`: The stream name to be used while committing the defects to Coverity Connect.
* `commitDefectsXmlConfig`: The configuration file containing the details of Coverity Connect.
* `includeTestSource`: Specifies whether test sources should be analyzed by Coverity.
* `includeTestSource`: Specifies whether test sources should be analyzed by Coverity. Defaults to `false`.
* `includeAutogenSource`: Specifies whether source files under `autogen` directories should be analyzed by Coverity.
 Defaults to `false`.
* `includeSubProjects`: Specifies whether to recursively include source & library files from sub-projects.
 Defaults to `true`.
* `coverityHome`: The location of cov-analysis installation. If cov-analysis/bin directory is in the PATH,
 this can be left null.
* `bootClasspath`: The bootclasspath to be used during `cov-emit-java`. If the default models included in
 coverity is enough, leave this null.
* `covConnectHost`: The hostname/IP Address of Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `covConnectDataPort`: The datport of Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `covConnectUser`: The username in Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.
* `covConnectPassword`: The password of `covConnectUser` in Coverity Connect server. Used only if `commitDefectsXmlConfig` isn't specified.

