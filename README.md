# DCCD

The DCCD software is an online digital archiving system for dendrochronological data. 
A recent version of this software (system) is  deployed as 'Digital Collaboratory for Cultural Dendrochronology' (DCCD) at [http://dendro.dans.knaw.nl](http://dendro.dans.knaw.nl "DCCD website").
More information about the Digital Collaboratory for Cultural Dendrochronology (DCCD) project can be found here: http://vkc.library.uu.nl/vkc/dendrochronology. 

## Building DCCD from Source
There is no binary distribution so you have to build the application from the sources. 
This comes down to downloading the sources from the code repository and building them. 
If you are going to run your own version of the repository you should change the code anyway 
because the user interface contains DCCD specific information and some files are also specific for DCCD/DANS. 

For building you need the Java SDK and Apache Maven. 
Some basic knowledge of the Git code repository is needed because that is the system being used for the DCCD code. 

Setup a local folder where you will place the code to build, from now on called {devpath}. 
Here you could also place your own versions of dependent libs when you want to change them. For instance a local version of TridasJlib and DendroFileIO could be placed in {devpath}/tridas. 


### DCCD web application

The DCCD web application (dccd-webui) and the tools depend on the dccd-lib project, which implements the business logic and data models. 
The dccd-lib in turn depends on the dccd-legacy-libs. 

The code building order therefore is:

1. dccd-legacy-libs
2. dccd-lib
3. dccd-webui (dccd-http, dccd-oai) 

#### Build legacy libs

    # cd {devpath}
    # git clone {dccd-legacy-libs.git}


First you need to install some jar/pom's in your .m2 , because they are difficult to get from a public maven repo. 
Just run the script in the dans-commons project: /dans-commons/repo/install.sh

	# cd dccd-legacy-libs/repo
    # ./install.sh
    
and then build it with maven. 

    # cd ..
	# mvn clean install

#### Build dccd-lib
    
Do a 'git clone' like: 

    # cd {devpath}
    # git clone {dccd-lib.git}

    
and then build it with maven. 

    # cd dccd-lib
    # mvn clean install

#### Build dccd-webui
Next you need the dccd-webui project repo. 


    # cd {devpath}
    # git clone {dccd-webui.git}

    
and again build it with maven. 


    # cd dccd-webui
    # mvn clean install

Notes for Eclipse IDE (Kepler or newer):

Start DCCD via Eclipse with the Jetty engine, 
Select packages src/test/java - nl.knaw.dans.dccd.web - Start.java
Debug as - Java application
Then browse to http://localhost:8081/DCCD/
NOTE: login in or searching won’t work unless you have the services running as described for Installation/deployment. 

### DCCD tools (optional)

The DCCD tools can be useful for maintenance. 
These are the reindexer and the repotools projects. 
Note that the reindexer is not a task inside the repotool, but separate jar!
Both projects need to be build as assembly so it will have all dependend jars in a ‘zip’ file. 
Each ‘task’ has a shell script to run it, and csv file with logs is generated. 

As with the dccd webapp you need to clone with git and build with maven   
Reindexer: 

    # cd {devpath}
    # git clone {dccd-reindex.git}
    # cd dccd-reindexer
    # mvn clean install

Repotools: 

    # cd {devpath}
    # git clone {dccd-repotools.git}
    # cd dccd-repotools
    # mvn clean install

### Other DCCD services
The DCCD archive is used by other services that in turn can be used by external clients. 
There are now only two of those ‘extra’ services under development:

1.	The RESTfull API; dccd-http
2.	The OAI-MPH; dccd-oai (which needs the dccd-http to be deployed) 

Both are Maven projects for web services that need to be build and then deployed on the same server as the DCCD archive. 
For more details read the README files accompanying the source code. 

## Notes for Eclipse IDE:
Install the JiBX plugin 1.2.4 (http://jibx.sourceforge.net/eclipse/) and enable it on the DCCD project.  
Change the JiBX properties: JiBX Mapping Folder: src/main/binding

Start DCCD via Eclipse with the Jetty engine, 
Select packages src/test/java - nl.knaw.dans.dccd.web - Start.java
Debug as - Java application
Then browse to http://localhost:8081/DCCD/
NOTE: login or searching won’t work unless you have the services running as described for deployment. 

## Installing DCCD
Explains how to install and deploy the DCCD on a webserver. 
[DCCD Installation Guide](INSTALL.md)

## Developing DCCD
These instructions explain what is needed to develop on the software. 
[DCCD Development Guide](DEVELOPMENT.md)


### Adapting the web application 
The DCCD web application is not a CMS, instead the code needs to be changed for the application to be adapted to your own needs. What to change when setting up your own repository/archive, not only CSS images and configuration, but also some of the pages like acknowledgment, FAQs etc. Also the standard mails and other messages like the licenses. 

### License & Copyright
The DCCD web application is open-source software and uses the Apache License, version 2.0 
[The application software is primarily being developed by DANS (Data Archiving and Networked Services).] 

Among the open source software projects DCCD depends on are: 
* TRiDaS libraries
* Apache Wicket
* OpenLayers
* jQuery. 


## Manuals
The extra DCCD documentation consist of the following:

* User manual [DCCD manual.docx](docs/Manuals/DCCD manual.docx)
* Member management manual [DCCD member management manual.docx](docs/Manuals/DCCD member management manual.docx)
