coverityplugin
==============

Coverity Gradle Plugin

This plugin is designed to enable Coverity coverage while using gradle as a build tool.

Integrating coverity plugin with gradle
=======================================
Building the jar manually
-------------------------
* Clone the repository 
* Run gradlew jar
* Copy build/libs/coverityplugin-<version>.jar to your favorite artifact repo

Use artifact from github
------------------------

    buildscript {
      repositories {
      }
      dependencies { 
        classpath 'naedward.gradle:coverityplugin:0.6.0'
      }
    }
    apply plugin: 'coverity'



Configure Coverity 
------------------
Use the coverity closure in your gradle build script

    coverity {
       intermediateDir                //Define the intermediate directory. Default [false]
       commitDefectsStreamName        //Name of stream, Required
       commitDefectsXmlConfig         //File object, coverity configuration xml file, used to define the coverity host server and username/password for pushing defects, Required 
       analyzeNumWorkers              //Number of worker threads to use when using analyze task, Default [auto] VM's have issues with --auto
       includeTestSource              //Set to true to detect defects in your test source.  Default [false]
    }
    

Coverity Tasks 
--------------
* covAnalyze - Performs a coverity analysis on the intermediate directory.
* covCommit - Pushes analysis to coverity server
* covEmit - Processes all the compiled files ready to be analyzed
