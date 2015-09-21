
DCCD Compilation, Installation and Deployment Guide
=======================

1 Introduction
--------------

This document will guide you through the steps of downloading, building and deploying the DCCD software on a server.
As described below, DCCD builds upon several open source software components so several configurations on different platforms should be possible. However, this Guide describes a simple one-server set-up, on a CentOS 6.5 or a
RedHat Linux 6 server, the configuration currently in use at DANS. 
So far, no other configurations have been tested.

__Note that you should NOT put this application on a production server 'as-is' 
because it contains static content and links specific to the DCCD that is already publicly deployed at http://dendro.dans.knaw.nl__

The general process for getting up and running is:

 - Get source code
 - Make changes to code to fit your installation (change logos etc)
 - Build RPM files and copy to server
 - Install RPM files on server


1.1	Overview of DCCD
---------------------

The DCCD software is made up of 7 packages, each of which are stored as separate projects within Github:

 * **dccd-webui** - This is the web user interface to the DCCD repository and as such is the front end that the majority of users see when accessing a DCCD server.  
 * **dccd-lib** - This contains the core backend to the DCCD server as is required by all other DCCD packages.  
 * **dccd-legacy-libs** - This contains a number of hard to find Java dependencies required by the rest of the DCCD software.
 * **dccd-http** - This is a RESTful (limited) machine-machine interface to the DCCD archive.  It is used internally with DCCD software (e.g. dccd-oai) but may also be useful for power users wishing to query and extract data from the DCCD repository.
 * **dccd-oai** - This enables the DCCD server to function as an OAI-PMH data provider.  It relies upon the dccd-http RESTful interface.
 * **dccd-reindex** - 
 * **dccd-repotools** - 
 
In addition to a variety of internal dependencies (which are handled at compile time by Maven), these packages rely on a number of other widely-used open source packages when they are deployed.  The following packages must therefore be installed and configured to successfully run the DCCD server software:

 * PostGreSQL 
 * Java
 * Tomcat
 * OpenLDAP 
 * Fedora Commons Repository
 * Apache Solr
 * Apache Webserver
 
 
Although this documentation is stored within the dccd-webui project, it covers the installation and deployment for all the projects as they integrate together to provide the complete DCCD server software.
 
 
1.2 Who is this documentation for?
-----------

This document is designed to be read by both developers of DCCD and system administrators wanting to deploy the software to a server.  It is important to note that the DCCD software is not a Content Management System (CMS) and so the source code of the web applications must be altered and compiled before they can be deployed.  For example, logos, email addresses, links, introductory text, data use licenses etc must be changed from the default values which apply to the original installation of [DCCD at DANS](http://dendro.dans.knaw.nl/).  There are therefore no standard binary installation packages available to download to enable an 'off-the-shelf' instance of the DCCD software.  With that said we have attempted to make the compilation and deployment of DCCD as painless as possible.


2 Compiling DCCD packages
------

The following instructions assume you are using the Eclipse IDE as this is the tool used by the core DCCD development team.  If you choose to use another IDE then hopefully these instructions will give you enough guidance.


2.1 Installing build prerequisites
----

The following packages are required on your development machine to build the DCCD packages:
 
 * **Java JDK 7**  - Currently the development team is using Java 7 but are aiming to move to Java 8 soon.  Testing has not begun with Java 8 yet.  Both Oracle and OpenJDK versions have been used successfully
 * **Eclipse** - Currently the development team is using v4.4 - Luna but others are likely to work fine too. Within Eclipse you will also need the following add-ons
    * m2e - Extensions Development Support 
    * Eclipse GitHub integration with task focused interface	
 * **RPM** - Redhat Package Manager  
 
### 2.1.1 Java JDK

We assume you either already have the Java JDK installed or are able to install it yourself.  For Linux users, OpenJDK should be available in your standard repositories, so too may Oracle versions.  If not, then download and install the latest version from the [Oracle website](http://www.oracle.com/technetwork/java/javase/).  
 
### 2.1.2 Eclipse

Download the latest version of Eclipse IDE for Java Developers from the [Eclipse website](http://www.eclipse.org/) or install from your operating system's software repositories.  

Once installed launch Eclipse and then go to ``Help > Install New Software``.  In the 'work with' box select the main update site (e.g. June, Luna, Mars depending on your Eclipse version) then locate the following plugins under the 'Collaboration' section:
  
  * m2e - Extensions Development Support 
  * Eclipse GitHub integration with task focused interface
  
Once installed, Eclipse will need to be restarted to complete the installation process.

### 2.1.3 RPM

The DCCD projects are configured to use the rpm-maven-plugin to generate RPM installation packages to make deploying the packages on your server a very simple task.  If you are developing on a Linux computer this should be simple as rpm should either already be installed or available in your standard software repositories.  

On OSX the easiest way to get rpm is through the [Homebrew](http://brew.sh/) software repository.  With Homebrew installed you should be able to do:

```
brew install rpm
```

If your development machine is running Windows then you should be able to get access to rpm through [Cygwin](https://cygwin.com/). 
 
2.2 Downloading and compiling DCCD source code
---

Once you have your development environment set up you are ready to download the DCCD source code and compile into RPM packages ready to be deployed on your server.

### 2.2.1 Download source code

The source code for DCCD is hosted on [Github](https://github.com/DANS-KNAW).  On each of the DCCD project pages there is a HTTPS URL for access in the code:

 * https://github.com/DANS-KNAW/dccd-legacy-libs.git
 * https://github.com/DANS-KNAW/dccd-libs.git
 * https://github.com/DANS-KNAW/dccd-webui.git
 * https://github.com/DANS-KNAW/dccd-http.git
 * https://github.com/DANS-KNAW/dccd-oai.git
 * https://github.com/DANS-KNAW/dccd-reindex.git
 * https://github.com/DANS-KNAW/dccd-repotools.git
 
Within Eclipse, for each project go to ``File > Import > Git > Projects from Git > Clone URI``.  Within the dialog then enter the Git URL and check out the project as a 'General Project'.  Alternatively if you choose not to use Eclipse you can check out the code via the command line: ``git clone {project url}``.

### 2.2.2 Compiling source code

Before proceeding any further we need to make the hard-to-find legacy libraries stored within dccd-legacy-libs available to the other DCCD projects.  Locate the pom.xml file in the dccd-legacy-libs project, right click and do ``Run as > Maven install``.  This will install the libraries to your local Maven repostory.  This step need only be done once.  Alternatively from the command line, within the dccd-legacy-libs project folder you can do ``mvn install``.

The source code for DCCD is compiled into RPM installer files which can then be deployed on a CentOS or Redhat server.  The development team uses CentOS 6 so we recommend for ease of deployment you use this too.  

RPM files can be generated for each of the four remaining DCCD projects by right clicking on each pom.xml file and selecting ``Run as > Maven build...``.  In the dialog set the goal to 'package'.  Once you have packaged your projects you will find your RPM files in the ``target/rpm/.../RPMS/noarch/``folders of each project.  You can then transfer these rpm files to your server ready for deployment.  Again this can also be done via the command line ``mvn package``.


3 Deploying DCCD
---

3.1 Installing Oracle Java
---

By default CentOS is configured to use OpenJDK for it's Java.  We recommend that you install Oracle Java on your server.  First of all Download [“jdk-7uXX-linux-x64.rpm”](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) from the Oracle website (where XX is the latest update number).  The install:

```
sudo yum install jdk-7uXX-linux-x64.rpm
```

Next you will need to configure CentOS to use Oracle by default:
```
sudo alternatives --install /usr/bin/java java /usr/java/default/bin/java 2
sudo alternatives --config java
```
Choose Oracle Java from the list presented.   


3.2 Install packages
--- 

Once you have your four rpm files on your CentOS server then they can be installed with yum as normal:

```
sudo yum install dccd-lib dccd-webgui dccd-http dccd-oai
```

Most of the software components that DCCD relies upon will be installed by yum.  Two of them though (Fedora Commons Repository, and Apache Solr) are not available in standard software repositories.  These are installed as part of the configuration processed described below.


3.3 Configuration
-----

Once all the rpm packages have been installed, you then need to run the configuration script to complete the deployment.  This can be done as follows:

```
sudo /opt/dccd/dccd-server-post-install.sh
```

The script will ask for a great number of passwords to be configured.  We strongly recommend you fill out the following table with these passwords and keep it in a secure place.  

<table>
<th>Name</th>		<th>Description</th>		<th>Password</th>
<tr><td>fedora_db_admin</td> <td></td>	<td></td></tr>
<tr><td>fedoraAdmin</td>	<td></td>	<td></td></tr>
<tr><td>fedoraIntCallUser</td>	<td></td><td></td></tr>
<tr><td>ldapadmin</td>		<td></td>	<td></td></tr>
<tr><td>dccduseradmin</td>	<td></td>	<td></td></tr>
<tr><td>dccd_webui</td>	<td></td>	<td></td></tr>
<tr><td>dccd_oai</td>	<td></td>	<td></td></tr>
<tr><td>dccd_rest</td>	<td></td>	<td></td></tr>
</table>

You will also need the domain name for your server, a valid SMTP host name for sending emails and the email address of the system administrator who will be running the server.
 

4 Developing DCCD
-----

If you have followed the steps in section 2 you will already have a functioning Eclipse development environment for developing DCCD.  This is the basic setup for developing DCCD, however, below are a number of tips for making this process simpler. 


4.1 Virtual machine for backend
-----------

A common method for developing DCCD is to run all the backend services (i.e. dccd-libs) within a virtual machine, and then run the front-end module (i.e. dccd-webui) within Eclipse.  The dccd-webui must be configured to connect to the back-end modules on the VM.  This is of course different from the configuration you should use at test and production servers, where everything is installed on one host.

The easiest way to set up a virtual machine is to install [VirtualBox](https://www.virtualbox.org) then download a complete VM "CentOS-6.5-x86_64-Basic Server.vdi" from https://virtualboximages.com/CentOS.  You can then import this VDI file into Virtualbox. 

The network settings should be set to use NAT and port forwarding, 
so you can fix the url's in your properties files on the host OS (where you run the app you test).

The dccd.properties on the guest (VM) need the normal ports, but the dccd.properties on the Host (the develoment OS) need to have the forwarded ports!
 Solr and Fedora run on Tomcat, so have 8080. LDAP uses 389. 
 
Tips: With Fedora commons and Solr you can use the webinterface that is deployed on Tomcat. 
For LDAP the ApacheDirectory Studio is usefull and for Postgress you could use the pgAdmin application. 

