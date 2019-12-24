# Workflow Engine
![Logo](https://github.com/crossminer/scava/raw/crossflow/crossflow/crossflow_96dpi.png)

Crossflow is a distributed data processing framework that supports dispensation of work across multiple opinionated and low-commitment workers.

## Docker Quick Start

Pull container image from Docker Hub:

`docker pull crossminer/crossflow`

Startup container:

`docker run -it --rm -d --name crossflow -p 80:8080 -p 61616:61616 -p 61614:61614 -p 5672:5672 -p 61613:61613 -p 1883:1883 -p 8161:8161 -p 1099:1099 crossminer/crossflow:latest`

Access Crossflow web application:
http://localhost/org.eclipse.scava.crossflow.web/

More details on running Crossflow with Docker are available [here](https://github.com/crossminer/scava/tree/crossflow/crossflow/org.eclipse.scava.crossflow.web.docker/README.md).

## Running from source
To run Crossflow from source you will need Eclipse, Apache Tomcat and Apache Thrift. Brief instructions are provided below.

### Eclipse
- Start with a J2EE distribution, from https://www.eclipse.org/downloads/packages/release/2019-09/r/eclipse-ide-enterprise-java-developers
- Install Emfatic, from http://download.eclipse.org/emfatic/update/ (Untick the "Group items by category" check box)
- Install the Graphical Modelling Framework (GMF) Tooling SDK, from http://download.eclipse.org/modeling/gmp/gmf-tooling/updates/releases/ 
(choose keep my installation the same option)
- Install the following Epsilon features, from http://download.eclipse.org/epsilon/interim/
	- Epsilon Core
	- Epsilon Core Develoment Tools
	- Epsilon EMF Integration
	- Epsilon GMF Integration
	(choose modify current installation option)
- Install Eclipse Xtext for textual workflow modelling support, from https://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/ update site and select the following:
	- Xtext Complete SDK
	- Xtext Runtime
	(choose keep my installation the same option)

### Update site
- The update site for Crossflow can be found in the folder named 'org.eclipse.scava.crossflow.updatesite' in the 'crossflow' folder of the repository
- For example, to access it online, for the 'crossflow' branch, the update site is at: https://github.com/crossminer/scava/raw/crossflow/crossflow/org.eclipse.scava.crossflow.updatesite (a.k.a pointing eclipse to that url in 'install new software') 
- NB: the following (8) steps are not required if Crossflow is installed using this method, and running the Web UI needs to be done through docker

### Tomcat
- Download a copy of Tomcat, from http://archive.apache.org/dist/tomcat/tomcat-9/v9.0.27/bin/apache-tomcat-9.0.27.zip
- Set up Tomcat in your Eclipse through the Servers view

### ActveMQ
- Download a copy of ActiveMQ, from https://activemq.apache.org/components/classic/download/

### Thrift
- Install Apache Thrift (http://thrift.apache.org/)
	- Standalone executable for Windows
	- Homebrew for Mac

### Source Code (Git)
- Clone the https://github.com/crossminer/scava/ repository
- Switch to the crossflow branch
- Import all projects from the crossflow and the restmule folders

### Ivy
We're using Apache Ivy for dependency management (i.e. so that we don't need to store jars in the repo)
- Install the Ivy Eclipse plugin: http://www.apache.org/dist/ant/ivyde/updatesite
- The main project requiring resolution is 'runtime.dependencies'. To resolve the dependencies you now select the retrieve option "Retrieve 'dependencies'", from the context menu of Ivy (found by right-clicking on the eclipse project itself). 
- For the remainder of the projects with similar requirements, if Ivy doesn't run automatically, perform the same step as above.

### Generating stuff
You will need to run the ANT build-files below to generate stuff after you import all the crossflow and restmule projects (if you wish to use the web, tests or examples project(s)).

- org.eclipse.scava.crossflow.tests/generate-all-tests.xml runs the Crossflow code generator against all models under /org.eclipse.scava.crossflow.tests/models
- org.eclipse.scava.crossflow.web/build-war.xml runs the Thrift code generator against crossflow.thrift to produce Java and JavaScript source code and builds a Tomcat WAR file from org.eclipse.scava.crossflow.web
- org.eclipse.scava.crossflow.examples/generate-all-examples.xml runs the Crossflow code generator against all models under /org.eclipse.scava.crossflow.examples/models

### Tests
- JUnit tests can be ran through the CrossflowTests test suite in org.eclipse.scava.crossflow.tests

### Web application
- Before running the web application, start ActiveMQ from a terminal using 'activemq start'. If your path is not configured properly, move to the ActiveMQ directory on your machine and run the command in the 'bin' folder.
- To run the web application (port: 8080) right-click on org.eclipse.scava.crossflow.web and select Run as -> Run on Server
- The web app should be running on http://localhost:8080/org.eclipse.scava.crossflow.web/

### Screenshots

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/index.png)
**Figure**: Main page listing available workflows and *Upload New Workflow* button.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/calculator-advanced.png)
**Figure**: Calculator experiment page *Advanced* tab listing Calculator workflow configuration.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/calculator-calculations.png)
**Figure**: Calculator experiment page *Calculations* tab listing Calculator workflow input calculations obtained from CSV source.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/calculator-model.png)
**Figure**: Calculator experiment page *Model* tab listing Calculator workflow model.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/calculator-log.png)
**Figure**: Calculator experiment page *Log* tab listing Calculator workflow log after experiment completion.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/wordcount-model.png)
**Figure**: Word Count experiment page *Model* tab listing Word Count workflow model before execution.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/wordcount-model-running.png)
**Figure**: Word Count experiment page *Model* tab listing Word Count workflow model during execution visualizing task status and queue size by means of color and rounded number, respectively. **Task status (color)**: STARTED (lightcyan), WAITING (skyblue), INPROGRESS (palegreen), BLOCKED (salmon), and FINISHED (slategrey).  

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/wordcount-model-running-tooltip.png)
**Figure**: Word Count experiment page *Model* tab listing Word Count workflow model during execution with mouse hovering over initial queue depicting (queue) size, in-flight count, and subscriber count.

![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/wordcount-model-clear-all.png)
**Figure**: Word Count experiment page *Model* tab listing Word Count workflow model during execution with mouse click inside empty model area, i.e. not on a particular task or queue, displaying context menu popup to clear the cache of all queues involved in the Word Count workflow.

 ![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/wordcount-model-clear-specific.png)
**Figure**: Word Count experiment page *Model* tab listing Word Count workflow model during execution with mouse click inside boundaries of *WordFrequencies* queue displaying context menu popup to clear the cache of all queues involved in the Word Count workflow.

 ![Screenshot](https://github.com/crossminer/scava/raw/crossflow/crossflow/images/upload.png)
**Figure**: Upload New Workflow page allowing the upload and deployment of new experiments.
