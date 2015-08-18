
DCCD Installation and Deployment Guide
=======================

1 Introduction
--------------

This document will guide you through the steps of deploying the DCCD software on a server.
As described below, DCCD builds upon several open source software components so several configurations on different platforms should be possible. However, this Guide describes a simple one-server set-up, on a CentOS 6.5 or a
RedHat Linux 6 server, the configuration currently in use at DANS. 
So far, no other configurations have been tested.

__Note that you should NOT put this application on a production server 'as-is' 
because it contains static content and links specific to the DCCD that is already publicly deployed at http://dendro.dans.knaw.nl__


1.1	Overview of DCCD
---------------------

DCCD makes use of several services or components that need to be installed. 
Some of these components could in principle be replaced by different components.
If only a standard protocol is mentioned in the interface, a different
implementation of that protocol could possibly be used. 
 
Standard

* Postgresql
* Java
* Tomcat
 ...

Backend
 
* LDAP Directory --- another LDAP implementation could be used;
* Fedora  Commons Repository --- needs to be a version of Fedora Commons;
* SOLR Search Index --- needs to be a version of Apache SOLR.

Frontend
...

*However, it is important to remember that only the configuration discussed in
this document has been tested.*


1.2	Installation packages
-------------------------
Before you continue, please make sure you have the following required
packages (war files etc).  For information regarding the building of DCCD packages see https://github.com/petebrew/dccd-webui/blob/master/README.md


* dccd etc.

...

The dccd-lib project contains extra files needed for installation:
* dccd-lib/ldap
* dccd-lib/solr

...




1.3	Passwords
-------------

During the installation you will be asked several times to provide a password. 
Please, ensure that you create safe passwords.  Prefer randomly generated
passwords over human readable ones.  Store your passwords in a central,
encrypted database that you secure with a passphrase you can remember.  

The passwords you generate have to be specified later in the instruction. For
your convenience we provide the table below that you can copy and fill in before
you start the installation.  Where in the text it says "fill in
password:fedora_db_admin" look up the corresponding password here.

<table>
	<th>Name</th>				<th>Password</th>
<tr><td>fedora_db_admin</td>	<td></td></tr>
<tr><td>fedoraAdmin</td>		<td></td></tr>
<tr><td>fedoraIntCallUser</td>	<td></td></tr>
<tr><td>ldapadmin</td>			<td></td></tr>
<tr><td>dccduseradmin</td>		<td></td></tr>
</table>
 

1.4	Conventions
---------------

The remainder of this Guide consists of a step-by-step instruction. We use the
following conventions:

* To indicate input and output on the command line, code blocks are used;
* *input* is preceded by a dollar-sign prompt.

		$ ls -l
		total 0
		drwx------+  2 janvanmansum  staff   374 Sep 10 13:24 Desktop
		drwx------+ 14 janvanmansum  staff   544 Aug 29 14:36 Documents
		drwx------+  3 janvanmansum  staff   442 Oct 15 09:19 Downloads
		drwx------@ 11 janvanmansum  staff   476 Oct 15 08:49 Dropbox  

* the prompt must *not* be typed on the command line (it, or a different prompt
  such as #, should already be there);
* what follows a line with a prompt is expected output. Note however that the
  output might be slightly different on your system;
* if the contents of a configuration file must be changed the relevant section is
  displayed in the courier font with the changed parts in **bold**;
* some commands are included in order to check the results of previous commands 
  (e.g., <code>sudo chkconfig --list slapd</code>); it should be obvious which ones are.
  
  
2 Standard Software Components
==============================

The following industry standard software components need to be installed first. 
See subsections for comments about alternatives and additional configuration. 
The items in this section can typically be performed by the IT department.  


2.1 Redhat 6 or CentOS 6
------------------------

We recommend that you run the operation system in SELinux “enforcing mode.”  This is the default mode for Centos so should not require any additional configuration.  You can confirm this by typing:

	$ sestatus
  	SELinux status:                 enabled
  	SELinuxfs mount:                /selinux
  	Current mode:                   enforcing
  	Mode from config file:          enforcing
  	Policy version:                 21
  	Policy from config file:        targeted


2.2 Oracle Java SE 7 SDK (CentOS)
---------------------------------

If you are working on RedHat, skip to [2.3 Oracle Java SE 7 SDK
(RedHat)](#oracle-java-se-7-sdk-redhat) for an easier installation.


### 2.2.1 Download the JDK

Download
[“jdk-7uXX-linux-x64.rpm”](http://www.oracle.com/technetwork/java/javase/
downloads/jdk7-downloads-1880260.html) from the Oracle website (where XX is the
latest update number).


### 2.2.2 Run installer
Upload the rpm-file to your server with scp or sftp and run the installer:

	$ sudo rpm -i jdk-7uXX-linux-x64.rpm
	Unpacking JAR files...
		rt.jar...
		jsse.jar...
		charsets.jar...
		tools.jar...
		localedata.jar...
		jfxrt.jar...


### 2.2.3 Add the JAVA_HOME environment variable

Copy the file $DCCD-LIB/util/java.sh to /etc/profile.d and run it:

	$ sudo cp java.sh /etc/profile.d/
	$ exit

Now, log off and on to add the JAVA_HOME variable to your environment.

	$ echo $JAVA_HOME
	/usr/java/default/


### 2.2.4 Add java to alternatives

CentOS comes default with OpenJDK. Add Oracle JDK to alternatives and activate it:

	$ sudo alternatives --install /usr/bin/java java /usr/java/default/bin/java 2
	$ sudo alternatives --config java
	
	There are 4 programs which provide 'java'.
	
	  Selection    Command
	-----------------------------------------------
	   1           /usr/lib/jvm/jre-1.5.0-gcj/bin/java
	*  2           /usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java
	   3           /usr/lib/jvm/jre-1.6.0-openjdk.x86_64/bin/java
	 + 4           /usr/java/default/bin/java
	
	Enter to keep the current selection[+], or type selection number: 4
	[root@localhost ~]# java -version
	java version "1.7.0_79"
	Java(TM) SE Runtime Environment (build 1.7.0_79-b15)
	Java HotSpot(TM) 64-Bit Server VM (build 24.79-b02, mixed mode)


Make sure the output does not mention “OpenJDK”.


### 2.2.5 Notes

* Version 6 will work as well but is now well beyond it's end of life
* OpenJDK might work as well, but has not been tested.
* Work has begun to migrate to Java 8 as it's the currently supported version of Java.  There are currently some minor issues that are blocking migration. (August 2015)


### 2.3 Oracle Java SE 7 SDK (RedHat)

*to do*


2.4 Tomcat 6
------------

### 2.4.1 Install Tomcat 6
Execute the following command:

	$ sudo yum install tomcat6 tomcat6-webapps tomcat6-admin-webapps

Answer yes to the prompts and eventually you should get the message that the installation has been successfully completed.

### 2.4.2 Give the Tomcat 6 jvm more memory to work with

Add the following line to /etc/tomcat6/tomcat6.conf (just below the line that
starts with “JAVA_OPTS=”):

	$ sudo vi /etc/tomcat6/tomcat6.conf
	JAVA_OPTS="${JAVA_OPTS} -Xmx2048m -Xms2048m -server -XX:PermSize=256m \
	  -XX:MaxPermSize=256m -XX:+AggressiveHeap"

Look out when copy-pasting the above, the backslash seems to confuse Tomcat, so
you had better put everything on one line.


### 2.4.3 Configure Tomcat 6 to expect UTF-8 in percent-encoded bytes

Configure all the connectors you specify in /etc/tomcat6/server.xml to use the
UTF-8 encoding, by means of the attribute:  URIEncoding="UTF-8".  When adding an
AJP-connector to connect Tomcat to Apache HTTP Server (see next step) don’t
forget to also configure it.

```xml
$ sudo vi /etc/tomcat6/server.xml
...
<Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" 
               URIEncoding="UTF-8"/>
...
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" URIEncoding="UTF-8"/>
```


### 2.4.4	Configure the Tomcat daemon to start automatically

Configure the Tomcat daemon to start automatically at system startup	:

	$ sudo chkconfig tomcat6 on
	$ sudo chkconfig --list tomcat6
	tomcat6        	0:off	1:off	2:on	3:on	4:on	5:on	6:off

*Do not start the Tomcat daemon yet.  We need to configure our web applications before 
they are deployed.*


2.5 Apache HTTP Server 2.2.15
-----------------------------

### 2.5.1 Install Apache HTTP Server


	$ yum install httpd

first configure it so it serves as a tomcat proxy

	$ cd /etc/httpd

I want the dccd conf in /etc/httpd/conf.d/dendro.dans.knaw.nl.conf

	$ sudo vi /etc/httpd/conf.d/dendro.dans.knaw.nl.conf

And insert

```xml
	NameVirtualHost *:80

	<VirtualHost *:80>
    	ServerAdmin info@dans.knaw.nl
    	ServerName dendro.dans.knaw.nl
    	CustomLog "/var/log/httpd/dendro.dans.knaw.nl.log" combined
	
    	RewriteEngine  on

    	# The DCCD RESTfull API
		# Comment out the next line to disable requests from external clients
		#RewriteRule ^/dccd-rest/(.*)$ ajp://localhost:8009/dccd-rest/$1 [P]
		
		# The DCCD OAI-MPH
		# Comment out the next line to disable requests from external clients
		RewriteRule ^/oai/(.*)$ ajp://localhost:8009/dccd-oai/$1 [P]
		
		# The DCCD webapplication (GUI)
		RewriteRule ^/dccd/(.*)$ http://dendro.dans.knaw.nl/$1
		
		RewriteRule ^/(.*)$ ajp://localhost:8009/dccd/$1 [P]
		ProxyPassReverse / http://dendro.dans.knaw.nl/dccd/
		ProxyPassReverseCookiePath /dccd /
	</VirtualHost>
	
	# Direct access restricted on a production machine
	<VirtualHost *:80>
    	ServerAdmin info@dans.knaw.nl
    	ServerName dendro.dans.knaw.nl
    	CustomLog "/var/log/httpd/dendro.dans.knaw.nl.log" combined
	
    	<Proxy *>
		 Order Deny,Allow
		 Deny from all
		 # localhost
		 Allow from 127.0.0.1
		 # add others here
    	</Proxy>

    	ProxyPass / ajp://localhost:8009/dccd/
    	ProxyPassReverse / http://dendro.dans.knaw.nl/dccd/
    	ProxyPassReverseCookiePath /dccd /
	</VirtualHost>
```

Make sure you edit this so that the server URL matches that of your own server and not dans.knaw.nl.  

Next start httpd

	$ sudo service httpd start
	$ sudo chkconfig --level 3 httpd on

OK, but is it reachable from the outside (non localhost)?

	$ sudo iptables --list
	$ iptables -A INPUT -p tcp -m tcp --dport 80 -j ACCEPT
	$ service iptables save

Note: after changing the config always restart the service
	
	$ sudo apachectl -k graceful

2.6 PostGreSQL 8.4
------------------

### 2.6.1 Install PostGreSQL

Execute the following command:

	$ sudo yum install postgresql-server.x86_64

Answer yes to the prompts and eventually you will get a message confirming completion.


### 2.6.2 Initialize the database

Initialize the database after installation:

	$ sudo service postgresql initdb
	Initializing database:                                     [  OK  ]


### 2.6.3 Configure auto-vacuum (optional)

PostGreSQL by default doesn’t automatically garbage collect deleted rows. A DBA
can start a garbage collect session (known as “vacuum”) manually. However, it is
also possible to have PostGreSQL do this automatically. 

Open the file /var/lib/pgsql/data/postgresql.conf and change the corresponding
lines to look like below: 

	$ sudo vi /var/lib/pgsql/data/postgresql.conf

	# ..
	# - Query/Index Statistics Collector -

	#track_activities = on
	track_counts = on
	#track_functions = none                 # none, pl, all
	#track_activity_query_size = 1024
	#update_process_title = on
	#stats_temp_directory = 'pg_stat_tmp'


	# - Statistics Monitoring -

	#log_parser_stats = off
	#log_planner_stats = off
	#log_executor_stats = off
	#log_statement_stats = off


	#-----------------------------------------------------------------------
	# AUTOVACUUM PARAMETERS
	#-----------------------------------------------------------------------

	autovacuum = on                   # Enable autovacuum subprocess?  'on'
	                                  # requires track_counts to also be on
	#log_autovacuum_min_duration = -1 # -1 disables, 0 logs all actions and
	                                  # their durations, > 0 logs only
	                                  # actions running at least this
	                                  # number of milliseconds.
	#autovacuum_max_workers = 3       # max number of autovacuum sub-
                                      # processes
	#autovacuum_naptime = 1min        # time between autovacuum runs
	#autovacuum_vacuum_threshold = 50 # min number of row updates before
                                      # vacuum
                                        

### 2.6.4 Configure database to accept user/password credentials

Configure the database to accept local connections based on username/password
credentials by editing the file /var/lib/pgsql/data/pg_hba.conf.  The “postgres”
user (super user) will keep using the “ident” method for Unix domain sockets
which means that the requesting process must be run by the “postgres” operating
system user.  

	$ sudo vi /var/lib/pgsql/data/pg_hba.conf

Change the lines at the bottom of the file to look like this:

	# TYPE  DATABASE    USER        CIDR-ADDRESS          METHOD

	# "local" is for Unix domain socket connections only
	local   all         postgres                          ident
	local   all         all                               md5
	# IPv4 local connections:
	host    all         all         127.0.0.1/32          md5
	# IPv6 local connections:
	host    all         all         ::1/128               md5


### 2.6.5	Start the daemon

Make the PostGreSQL daemon start by default:

	$ sudo chkconfig postgresql on
	$ sudo chkconfig --list postgresql
	postgresql     	0:off	1:off	2:on	3:on	4:on	5:on	6:off

	
Start the daemon now:

	$ sudo service postgresql start
	Starting postgresql service:                               [  OK  ]


2.7 OpenLDAP 2.4
----------------

### 2.7.1 Install OpenLDAP servers and clients

Execute the following command:

	$ sudo yum install openldap-servers openldap-clients

Answer yes to the prompts and eventually you will get a message confirming completion.


### 2.7.2 Remove the “default” database (optional)

The OpenLDAP installer configures a default user database.  Since we are not
going to use it, we will remove it.  There does not seem to be a clean way (i.e.
through the LDAP protocol) to do this yet, so we will remove the appropriate
file from the config directory:

	$ sudo rm /etc/openldap/slapd.d/cn\=config/olcDatabase\=\{2\}bdb.ldif


### 2.7.3 Start the daemon

Make the OpenLDAP daemon start by default:

	$ sudo chkconfig slapd on
	$ sudo chkconfig --list slapd
	slapd          	0:off	1:off	2:on	3:on	4:on	5:on	6:off

Start the daemon now:

	$ sudo service slapd start
	Starting slapd:                                            [  OK  ]


3 DCCD Back-end Modules
=======================

Now that we have the standard software in place we turn to the set-up and
configuration of the back-end modules that support DCCD.  The items in this
section should typically be performed by the technical support staff for your
repository.


3.1 DCCD Fedora Commons Repository
----------------------------------

The core component of DCCD is the respository that stores the actual scientific
research datasets.  The repository is implemented using the Fedora Commons
repository software.  There are no standard (yum- or rpm-based) installation
packages for Fedora Commons.  The following steps are based on the instructions
on the Fedora Commons website. 


### 3.1.1 Create a database for Fedora Commons in PostGreSQL

Use the file

	$DCCD-LIB/dccd-fedora-commons-repository/create-fedora-db.sql

On the command line execute the following command:

	$ sudo -u postgres psql -U postgres < create-fedora-db.sql
	CREATE ROLE
	CREATE DATABASE          

(Note: if you are in a directory that is inaccessible to the postgres user you
may get a warning ‘could not change directory to “…”’ but this does not seem to
prevent the database from being created.)


### 3.1.2 Set the fedora_db_admin password

Set the password of the fedora_db_admin  postgres user:

	$ sudo -u postgres psql -U postgres

And then in postgres:

	postgres# \password fedora_db_admin
	Enter new password: 
	Enter it again:
	postgres# \q

(\q to quit psql) and fill in password:fedora_db_admin from Table 1 Passwords.


### 3.1.3 Set the FEDORA_HOME environment variable

Copy the file $DCCD-LIB/dccd-fedora-commons-repository/fedora.sh to /etc/profile.d 

	$ sudo cp fedora.sh /etc/profile.d/

and log off and on again.  The FEDORA_HOME environment variable should now point
to /opt/fedora.

	$ echo $FEDORA_HOME
	/opt/fedora


### 3.1.4 Run the Fedora Commons installer

Download the Fedora Commons installer (fcrepo-installer-3.5.jar) from the [Fedora
Commons Sourceforge](http://sourceforge.net/projects/fedora-commons/files/fedora/) website.  Note that v3.5 of Fedora Commons is an older release.  Work is underway to upgrade support for a more recent version.

Edit install.properties:

	$ sudo vi $DCCD-LIB/dccd-fedora-commons-repository/install.properties

* for database.password fill in password:fedora_db_admin
* for fedora.admin.pass fill in password:fedoraAdmin

Then execute the following command:

	$ sudo java -jar fcrepo-installer-3.5.jar install.properties
	WARNING: The environment variable, CATALINA_HOME, is not defined
	WARNING: Remember to define the CATALINA_HOME environment variable
	WARNING: before starting Fedora.
	WARNING: The environment variable, FEDORA_HOME, is not defined
	WARNING: Remember to define the FEDORA_HOME environment variable
	WARNING: before starting Fedora.
	Preparing FEDORA_HOME...
		Configuring fedora.fcfg
		Installing beSecurity
	Will not overwrite existing /usr/share/tomcat6/conf/server.xml.
	Wrote example server.xml to: 
		/opt/fedora-3.5/install/server.xml
	Preparing fedora.war...
	Deploying fedora.war...
	Installation complete.

	----------------------------------------------------------------------
	Before starting Fedora, please ensure that any required environment
	variables are correctly defined
		(e.g. FEDORA_HOME, JAVA_HOME, JAVA_OPTS, CATALINA_HOME).
	For more information, please consult the Installation & Configuration
	Guide in the online documentation.
	----------------------------------------------------------------------

where “install.properties” is your edited copy of the install.properties files
mentioned above.

You can safely ignore the warnings above.

After the installation change the ownership of installation directory to tomcat:

	$ sudo chown -R tomcat:tomcat /opt/fedora-3.5


### 3.1.5 Create a symbolic link to the fedora installation

Create a symbolic link to the /opt/fedora-3.5:

	$ sudo ln -s /opt/fedora-3.5 /opt/fedora
	$ ls -l /opt/
	totaal 8
	lrwxrwxrwx. 1 root   root     15 mrt  2 01:43 fedora -> /opt/fedora-3.5
	drwxr-xr-x. 7 tomcat tomcat 4096 mrt  2 01:24 fedora-3.5

Now, if you want to switch to another installed version of Fedora Commons you
will only need to point this link to the appropriate directory; the FEDORA_HOME
environment variable will automatically point to the same directory.


### 3.1.6 Create and configure location of data store and resource index

In this example we will assume that the Fedora objects and datastreams will be
located in /data/fedora/objects and /data/fedora/datastreams respectively and
that the resoure index will store its data in /data/fedora/resourceIndex. 

First, make sure the target locations exist, if they don’t, create them and
change ownership to the tomcat user:

	$ sudo mkdir -p /data/fedora/objects /data/fedora/datastreams \
	 /data/fedora/fedora-xacml-policies/repository-policies/default \
	 /data/fedora/resourceIndex 
	$ sudo chown -R tomcat:tomcat /data/fedora
	$ ls -l /data/fedora/
	totaal 16
	drwxr-xr-x. 2 tomcat tomcat 4096 mrt  2 01:45 datastreams
	drwxr-xr-x. 3 tomcat tomcat 4096 mrt  2 01:45 fedora-xacml-policies
	drwxr-xr-x. 2 tomcat tomcat 4096 mrt  2 01:45 objects
	drwxr-xr-x. 2 tomcat tomcat 4096 mrt  2 01:45 resourceIndex

Note that the policies directory does need to exist, even though we don’t
customize the policy mechanism.

Edit the file /opt/fedora/server/config/fedora.fcfg, and change the following items:

* In the module with the attribute
  role="org.fcrepo.server.storage.lowlevel.ILowlevelStorage", change the value of
  the “object_store_base” param to “/data/fedora/objects” and change the value of
  the param “datastream_store_base” to “/data/fedora/datastreams”
* In the datastore with the attribute id="localMulgaraTriplestore", change the
  value of the “path” param to “/data/fedora/resourceIndex”

<!-- There is also a /data/fedora-xacml-policies... path in there.  Shouldn't this be changed to data/fedora/fedora-xacml-policies too? -->

		$ sudo vi /opt/fedora/server/config/fedora.fcfg

```xml
<module role="org.fcrepo.server.storage.lowlevel.ILowlevelStorage"
class="org.fcrepo.server.storage.lowlevel.DefaultLowlevelStorageModule">
 <param name="path_algorithm" 
   value="org.fcrepo.server.storage.lowlevel.TimestampPathAlgorithm">
   <comment>The java class used to determine the path algorithm;
            default is org.fcrepo.server.storage.lowlevel.
            TimestampPathAlgorithm.</comment>
 </param>
 <param name="object_store_base" 
   value="/data/fedora/objects" isFilePath="true">
   <comment>The root directory for the internal storage of Fedora 
            objects. 
            This value should be adjusted based on your installation 
            environment. This value should not point to the same 
            location as
            datastream_store_base.</comment>
 </param>
 <param name="backslash_is_escape" value="true">
   <comment>Whether the escape character (i.e. (the token beginning an
            escape sequence) for the backing database (which includes
            registry tables) is the backslash character. This is 
            needed to
            correctly store and retrieve filepaths from the registry
            tables, if running under Windows/DOS. (Set to true for 
            MySQL and
            Postgresql, false for Derby and Oracle)</comment>
 </param>
 <param name="datastream_store_base" value="/data/fedora/datastreams" 
   isFilePath="true">
  <comment>The root directory for the internal storage of Managed
            Content datastreams. This value should be adjusted based 
            on your
            installation environment. This value should not point to 
            the same
            location as object_store_base.</comment>
...
<datastore id="localMulgaraTriplestore">
  <comment>local Mulgara Triplestore used by the Resource 
  Index</comment>
  <param name="poolInitialSize" value="3">
  <comment>The initial size of the session pool used for queries.
           Note: A value of 0 will cause the Resource Index to operate 
           in
           synchronized mode: concurrent read/write requests are put 
           in a queue
           and handled in FIFO order; this will severely impair 
           performance and
           is only intended for debugging.</comment>
  </param>
... more params ...
  <param name="path" value="/data/fedora/resourceIndex" 
  isFilePath="true">
    <comment>The local path to the main triplestore directory.</comment>
  </param>
``` 
  
  
### 3.1.7 Add Fedora Commons users

So far we only have fedoraAdmin user. We will use different users for different
services connecting to Fedora Commons.  Edit the file
/opt/fedora/server/config/fedora-users.xml and add user elements for users

__NOT Sure we have this__

dccd_webui, dccd_rest, dccd_oai.  

Give them the role administrator and fill in the password from Table 1 Passwords.

	$ sudo vi /opt/fedora/server/config/fedora-users.xml

```xml
<?xml version='1.0' ?>
  <users>
    <user name="fedoraAdmin" password="password:fedoraAdmin">
      <attribute name="fedoraRole">
        <value>administrator</value>
      </attribute>
    </user>
    <user name="dccd_webui" password="password:dccd_webui">
      <attribute name="fedoraRole">
        <value>administrator</value>
      </attribute>
    </user>
    <user name="dccd_rest" password="password:dccd_rest">
      <attribute name="fedoraRole">
        <value>administrator</value>
      </attribute>
    </user>
    <user name="dccd_oai" password="password:dccd_oai">
      <attribute name="fedoraRole">
        <value>administrator</value>
      </attribute>
    </user>
    <user name="fedoraIntCallUser" 
      password="password:fedoraIntCallUser">
      <attribute name="fedoraRole">
        <value>fedoraInternalCall-1</value>
        <value>fedoraInternalCall-2</value>
      </attribute>
    </user>
  </users>
```

It may seem useless to create extra users if they are all going to be admins
anyway.  However, in the future we assign different roles to these users with
more restricted privileges.


### 3.1.8 Change password of fedoraIntCallUser

The fedoraIntCallUser is a user that Fedora Commons uses internally to make
calls to itself.  By default it has the unsafe password “changeme”.  We will
change it to a safe password. 

We have already edited the file /opt/fedora/server/config/fedora-users.xml.

For fedoraIntCallUser we also need to edit
$FEDORA_HOME/server/config/beSecurity.xml to assign the same password from Table
1 Passwords.

	$ sudo vi /opt/fedora/server/config/beSecurity.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<serviceSecurityDescription 
  xmlns="info:fedora/fedora-system:def/beSecurity#"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="info:fedora/fedora-system:def/beSecurity#    
     http://www.fedora.info/definitions/1/0/api/beSecurity.xsd"
     role="default">
<serviceSecurityDescription 
role="fedoraInternalCall-1" 
callSSL="false" 
callBasicAuth="false" 
callUsername="fedoraIntCallUser" 
callPassword="password:fedoraIntCallUser" 
callbackSSL="false" 
callbackBasicAuth="false" 
iplist="127.0.0.1"/>
<serviceSecurityDescription 
role="fedoraInternalCall-2" 
callSSL="false" 
callBasicAuth="false" 
callbackSSL="false" 
callbackBasicAuth="false" 
iplist="127.0.0.1"/>
</serviceSecurityDescription>
```


### 3.1.9 Limit access to passwords

Several configuration files contain passwords.  We need to limit read rights for
security:

	$ sudo chmod 0600 /opt/fedora/server/config/fedora-users.xml
	$ sudo chmod 0600 /opt/fedora/server/config/fedora.fcfg
	$ sudo chmod 0600 /opt/fedora/server/config/beSecurity.xml
	$ ls -l /opt/fedora/server/config/
	totaal 124
	-rw-r--r--. 1 tomcat tomcat  1403 mrt  2 07:24 activemq.xml
	-rw-------. 1 tomcat tomcat   805 mrt  2 07:24 beSecurity.xml
	-rw-r--r--. 1 tomcat tomcat  4757 mrt  2 07:24 config-melcoe-pep-mapping.xml
	-rw-r--r--. 1 tomcat tomcat 16670 mrt  2 07:24 config-melcoe-pep.xml
	-rw-------. 1 tomcat tomcat 55854 mrt  2  2014 fedora.fcfg
	-rw-------. 1 tomcat tomcat  1183 mrt  2 07:24 fedora-users.xml
	-rw-r--r--. 1 tomcat tomcat  1266 mrt  2 07:24 jaas.conf
	-rw-r--r--. 1 tomcat tomcat  2163 mrt  2 07:24 logback.xml
	-rw-r--r--. 1 tomcat tomcat 15082 mrt  2 07:24 mime-to-extensions.xml
	drwxr-xr-x. 3 tomcat tomcat  4096 mrt  2 07:24 spring


It seems that chmod 0400 (only read-access to owner) is too restrictive. I am
not sure why, but read/write access by the owner (0600) is safe enough.


### 3.1.10 Ensure that Fedora "upload" directory has enough disk space

Files that are uploaded to Fedora through the API-M services are initially
written as temporary files to the Fedora "upload" directory. By default this
directory is located at /opt/fedora/server/management/upload. If /opt/fedora/
is located on a drive with limited space this may cause problems. We currently
do not know how to configure a different location. As a work-around you may
replace /opt/fedora/server/management/upload with a symbolic link to a
directory on a drive with sufficient space. For example:

	$ sudo mv /opt/fedora/server/management/upload /var/fedora-uploads
	$ sudo ln -s /var/fedora-uploads /opt/fedora/server/management/upload

Assuming of course that the disk mounted at /var (or /) has enough space for the
temporary files.


### 3.1.11 Deploy Saxon (Optional)

__I don't think DCCD needs it__


### 3.1.12 Start Tomcat 6

Finally we are ready to start up Tomcat 6 (we will tail the Tomcat log file to
see if everything goes well):

	$ sudo service tomcat6 start; tail -f /var/log/tomcat6/catalina.out
	Starting tomcat6:                                          [  OK  ]
	Mar 02, 2014 8:11:12 AM org.apache.catalina.startup.HostConfig deployDirectory
	INFO: Deploying web application directory sample
	Mar 02, 2014 8:11:12 AM org.apache.coyote.http11.Http11Protocol start
	INFO: Starting Coyote HTTP/1.1 on http-8080
	Mar 02, 2014 8:11:12 AM org.apache.jk.common.ChannelSocket init
	INFO: JK: ajp13 listening on /0.0.0.0:8009
	Mar 02, 2014 8:11:12 AM org.apache.jk.server.JkMain start
	INFO: Jk running ID=0 time=0/63  config=null
	Mar 02, 2014 8:11:12 AM org.apache.catalina.startup.Catalina start
	INFO: Server startup in 12985 ms


<!--### 3.1.13 Add the basic DCCD digital objects

__NOT sure we need this.__

In order to run, DCCD needs a minimal set of Fedora Commons digital objects. 
These are provided in:

$DCCD-LIB/dccd-fedora-commons-repository/basic-digital-objects

Change directory to this folder and execute the following command, replacing
&lt;password:fedoraAdmin>  the corresponding entry from Table 1 Passwords.

Look out: if the password contains dollar signs it must be in single quotes, and
any dollar signs in it must be escaped with a backslash (this may also be true
for other “special” characters in the password)

	$ fedora-batch-ingest.sh . ~/ingest.log text \
	   info:fedora/fedora-system:FOXML-1.1 \
	   localhost:8080 fedoraAdmin <password:fedoraAdmin> http
	ingest succeeded for: easy-collection:1.xml
	ingest succeeded for: easy-discipline:18.xml
	# .. more output
	ingest succeeded for: easy-discipline:27.xml
	ingest succeeded for: easy-data:oai-repository1.xml

	Batch Ingest Summary

	74 files processed in this batch
		74 objects successfully ingested into Fedora
		0 objects failed
		0 unexpected files in directory
		0 files ignored after error
-->


3.2 DCCD LDAP Directory 
-----------------------

The DCCD LDAP Directory component, apart from an LDAP daemon, consists of some
DCCD-specific schemas and a few basic entries.  We will add those here, using
the standard LDAP client tools.

### 3.2.1 Create a separate directory folder for DCCD

To keep things neat and tidy, we will give DCCD its own directory:

	$ sudo mkdir /var/lib/ldap/dccd; sudo chown ldap:ldap /var/lib/ldap/dccd
	$ sudo ls /var/lib/ldap/ -l
	totaal 4
	drwxr-xr-x. 2 ldap ldap 4096 mrt  2 08:51 dccd


### 3.2.2 Add DANS and DCCD schemas

__ADAPT for DCCD__

The schemas are added using LDIF files that can be found in:
$DCCD-LIB/ldap

Execute the following commands:

	$ sudo ldapadd -v -Y EXTERNAL -H ldapi:/// -f dans-schema.ldif
	ldap_initialize( ldapi:///??base )
	SASL/EXTERNAL authentication started
	SASL username: gidNumber=0+uidNumber=0,cn=peercred,cn=external,cn=auth
	SASL SSF: 0
	add objectClass:
		olcSchemaConfig
	add cn:
		dans
	add olcAttributeTypes:
		{0}( 1.3.6.1.4.1.33188.0.1.1 NAME 'dansState' DESC \
		 'The state of an entity' 
	# .. more simliar output
	adding new entry "cn=dans,cn=schema,cn=config"
	modify complete


### 3.2.3 Add DCCD database
__ADDAPT for DCCD__

First we add the DCCD database configuration to the config directory:

	$ sudo ldapadd -v -Y EXTERNAL -H ldapi:/// -f dccd-db.ldif
	ldap_initialize( ldapi:///??base )
	SASL/EXTERNAL authentication started
	SASL username: gidNumber=0+uidNumber=0,cn=peercred,cn=external,cn=auth
	SASL SSF: 0
	add objectClass:
		olcDatabaseConfig
	# .. more similar output
	adding new entry "olcDatabase=bdb,cn=config"
	modify complete


### 3.2.4 Add basic entries to the DCCD database
__ADDAPT for DCCD__

To run DCCD needs a minimal set of entries in its LDAP directory.  Those entries
are provided in the dccd-basis.ldif file.  

Before running the following command replace the string
“FILL.IN.YOUR@VALID-EMAIL.NL” with the e-mail address of the use that is going
to be the administrator of the EASY installation, for example your own e-mail
address.

	$ sudo vi dccd-basis.ldif
	...


Then execute the following command:

	$ sudo ldapadd -W -D cn=ldapadmin,dc=dans,dc=knaw,dc=nl -f dccd-basis.ldif
	Enter LDAP Password: secret
    ...


We are using the OpenLDAP user “cn=ldapadmin,dc=dans,dc=knaw,dc=nl”.  This is
the administrator of the DCCD LDAP Directory.  The default password of this user
is “secret” (we will change that in a moment, but you need it to complete this
command).


### 3.2.5 Change the ldapadmin password

The default password of the ldapadmin user is of course completely
non-self-describing, so we will change it here.  First, generate a safe
password, then execute the following command

	$ slappasswd -h {SSHA}

and enter &lt;password:ldapadmin> from Table 1 Passwords when prompted to do so.
 Copy the resulting hash and replace the hash in the file
“change-ldapadmin-pw.ldif” (the part in bold):

	$ sudo vi change-ldapadmin-pw.ldif

	dn: olcDatabase={2}bdb,cn=config
	changetype: modify
	replace: olcRootPW
	olcRootPW: {SSHA}ZrVZQ66Y7qzCKGg1I5iX4Qq//s7oosHw

Then, execute this command:

	$ sudo ldapadd -v -Y EXTERNAL -H ldapi:/// -f change-ldapadmin-pw.ldif
	ldap_initialize( ldapi:///??base )
	SASL/EXTERNAL authentication started
	SASL username: gidNumber=0+uidNumber=0,cn=peercred,cn=external,cn=auth
	SASL SSF: 0
	replace olcRootPW:
		{SSHA}9dQ07izka8farPzfFJHQg4YTSgjDAwVN 
	modifying entry "olcDatabase={2}bdb,cn=config"
	modify complete


### 3.2.6 Change the easyadmin user’s application password
__ADDAPT for DCCD__

The file “dccd-basis.ldif,” which we added earlier, added the administrator user
for the DCCD application: dccdadmin.  The default password for this user is also
“easyadmin.”  This needs to be replaced by a safe password.

Execute:

	$ slappasswd -h {SSHA}

and enter &lt;password:dccdadmin> from Table 1 Passwords when prompted to do so.
Edit the file “change-dccdadmin-user-pw.ldif” and replace the password hash with
the one calculated by slappasswd:

	$ sudo vi change-dccdadmin-user-pw.ldif

	dn: uid=dccdadmin,ou=users,ou=dccd,dc=dans,dc=knaw,dc=nl
	changetype: modify
	replace: userPassword
	userPassword: {SSHA}VzBuoiJKS46ZIiTmvAHkj4C92qE749YR

Then execute this command:

	$ sudo ldapadd -W -D cn=ldapadmin,dc=dans,dc=knaw,dc=nl -f change-dccdadmin-user-pw.ldif
	Enter LDAP Password: 
	modifying entry "uid=dccdadmin,ou=users,ou=dccd,dc=dans,dc=knaw,dc=nl"

Don’t forget that you have to use your new ldapadmin-password now!




3.5	DCCD SOLR Search Index
--------------------------

### 3.5.1 Install Apache SOLR 3.5

Download [Apache SOLR 3.5] and unzip it to /opt:

	$ sudo tar -xzf apache-solr-3.5.0.tgz -C /opt

This will create the directory /opt/apache-solr-3.5.0.

After the installation change the ownership of installation directory to tomcat:

	$ sudo chown -R tomcat:tomcat /opt/apache-solr-3.5.0
	drwxr-xr-x. 7 tomcat tomcat 4096 mrt  2 09:40 apache-solr-3.5.0
	lrwxrwxrwx. 1 root   root     15 mrt  2 07:43 fedora -> /opt/fedora-3.5
	drwxr-xr-x. 8 tomcat tomcat 4096 mrt  2 08:11 fedora-3.5
	drwxr-xr-x. 2 root   root   4096 jun 22  2012 rh
	drwxr-xr-x. 3 tomcat tomcat 4096 mrt  2 07:37 saxon


### 3.5.2 Create a symbolic link to the SOLR installation and war

As we did for fedora, we will create a symbolic link to the current installation:

	$ sudo ln -s /opt/apache-solr-3.5.0 /opt/apache-solr
	$ ls -l /opt
	totaal 16
	lrwxrwxrwx. 1 root   root     22 mrt  2 09:42 apache-solr ->\
	   /opt/apache-solr-3.5.0
	drwxr-xr-x. 7 tomcat tomcat 4096 mrt  2 09:40 apache-solr-3.5.0
	lrwxrwxrwx. 1 root   root     15 mrt  2 07:43 fedora -> /opt/fedora-3.5
	drwxr-xr-x. 8 tomcat tomcat 4096 mrt  2 08:11 fedora-3.5
	drwxr-xr-x. 2 root   root   4096 jun 22  2012 rh
	drwxr-xr-x. 3 tomcat tomcat 4096 mrt  2 07:37 saxon

	$ sudo ln -s /opt/apache-solr-3.5.0/dist/apache-solr-3.5.0.war\
	   /opt/apache-solr/solr.war
	$ ls -l /opt/apache-solr/
	totaal 284
	-rw-r--r--.  1 tomcat tomcat 156267 nov 22  2011 CHANGES.txt
	drwxr-xr-x.  3 tomcat tomcat   4096 mrt  2 09:40 client
	drwxr-xr-x.  9 tomcat tomcat   4096 nov 22  2011 contrib
	drwxr-xr-x.  3 tomcat tomcat   4096 mrt  2 09:40 dist
	drwxr-xr-x.  5 tomcat tomcat   4096 mrt  2 09:40 docs
	drwxr-xr-x. 11 tomcat tomcat   4096 mrt  2 09:40 example
	-rw-r--r--.  1 tomcat tomcat  80058 nov 22  2011 LICENSE.txt
	-rw-r--r--.  1 tomcat tomcat  17111 nov 22  2011 NOTICE.txt
	-rw-r--r--.  1 tomcat tomcat   4917 nov 22  2011 README.txt
	lrwxrwxrwx.  1 root   root       49 mrt  2 09:43 solr.war ->\
	  /opt/apache-solr-3.5.0/dist/apache-solr-3.5.0.war


### 3.5.3 Create and the solr.home-directory

__ADDAPT for DCCD__

Now we create the directory were SOLR will store its index:

	$ sudo mkdir -p /data/solr/cores/dendro/data \
	    /data/solr/cores/dendro/conf

Copy the file solr.xml in

	$EASY_BACKEND/easy-solr-search-index/config-all 
	
to the /data/solr directory:

	$ sudo cp solr.xml /data/solr

Copy the files schema.xml, solrconfig.xml, stopwords.txt, synonyms.txt, 
protwords.txt in 

	$EASY_BACKEND/easy-solr-search-index/config-datasets 

to the /data/solr/cores/dendro/conf directory:

	$ sudo cp schema.xml solrconfig.xml stopwords.txt \
	    synonyms.txt protwords.txt /data/solr/cores/dendro/conf

Now set ownerschip of the whole directory tree to the tomcat user:

	$ sudo chown -R tomcat:tomcat /data/solr
	$ ls -l /data/solr/
	totaal 8
	drwxr-xr-x. 3 tomcat tomcat 4096 mrt  2 09:43 cores
	-rw-r--r--. 1 tomcat tomcat 1386 mrt  2 09:47 solr.xml
	$ ls -l /data/solr/cores/dendro/
	totaal 8
	drwxr-xr-x. 2 tomcat tomcat 4096 mrt  2 09:48 conf
	drwxr-xr-x. 2 tomcat tomcat 4096 mrt  2 09:43 data
	$ ls -l /data/solr/cores/dendro/conf/
	totaal 40
	-rw-r--r--. 1 tomcat tomcat   873 mrt  2 09:49 protwords.txt
	-rw-r--r--. 1 tomcat tomcat 15919 mrt  2 09:49 schema.xml
	-rw-r--r--. 1 tomcat tomcat 10693 mrt  2 09:49 solrconfig.xml
	-rw-r--r--. 1 tomcat tomcat   781 mrt  2 09:49 stopwords.txt
	-rw-r--r--. 1 tomcat tomcat  1133 mrt  2 09:49 synonyms.txt


### 3.5.4 Copy the Tomcat 6 context container

Copy the solr.xml in 

	$EASY_BACKEND/easy-solr-search-index/config-tomcat 
	
(*don’t confuse with the previous file of the same name*) to the directory 

	/etc/tomcat6/Catalina/localhost

Execute:

	$ sudo cp solr.xml /etc/tomcat6/Catalina/localhost; \
	   tail -f /var/log/tomcat6/catalina.out
	INFO: Deploying configuration descriptor solr.xml
	Mar 02, 2014 9:52:03 AM org.apache.solr.core.SolrResourceLoader \
	   locateSolrHome
	INFO: Using JNDI solr.home: /data/solr
	Mar 02, 2014 9:52:03 AM org.apache.solr.core.SolrResourceLoader <init>
	INFO: Solr home set to '/data/solr/'
	# .. more output
	Mar 02, 2014 9:52:28 AM org.apache.solr.servlet.SolrServlet init
	INFO: SolrServlet.init() done

Again, we are tailing the Tomcat 6 log to see if the deployment goes well.
 

4 DCCD Frond-end Modules
========================

Now that we have the back-end services up and running, we can deploy the
front-end services.


4.1 DCCD Web-UI Application
---------------------------
__Addapt for DCCD__

The principal service is the Web User Inferace (Web-UI) application.


### 4.1.1 Create the dccd-home dir

	$ sudo mkdir /opt/dccd-home
	
And place the dccd.properties and maintenance.properties file in it. 
Edit the contents so properties match your configuration. 

TODO where to get them from (dccd)

...

Add the following to the tomcat configuration in /usr/share/tomcat6/conf/tomcat6.conf below the last JAVA_OPTS line: 
	
	# DCCD home directory
	JAVA_OPTS="$JAVA_OPTS -Ddccd.home=/opt/dccd-home"




### 4.1.7 Limit access to passwords

As the application.properties file contains password you should make sure that
access is limited to root and tomcat:

	$ sudo chmod 0600 application.properties


### 4.1.8 Deploy the webapp

Create the dccd application directory (Maybe the dccd-home should  just be there?)
	# sudo mkdir /opt/dccd
	# sudo cp DCCD.war /opt/dccd
	# sudo chown -R tomcat:tomcat /opt/dccd

Configure tomcat to deploy this war by creating an xml file 
/usr/share/tomcat6/conf/Catalina/localhost/DCCD.xml specifying the location of the war to deploy. 

	# sudo vi /usr/share/tomcat6/conf/Catalina/localhost/DCCD.xml 
	<?xml version="1.0" encoding="UTF-8"?>
    <Context docBase="/opt/dccd/DCCD.war" debug="0" crossContext="true" />
	
Now reload the Tomcat environment (i.e. stop and start it):

	$ sudo service tomcat6 force-reload
	Stopping tomcat6:                                          [  OK  ]
	Starting tomcat6:                                          [  OK  ]



4.3 DCCD RESTful Module 
-----------------------

The DCCD RESTful Module, “DCCD REST” for short, is a (limited)
machine-machine interface to the DCCD archive.

The source code of the RESTfull API; dccd-http can be found at 

develop.dans.knaw.nl:/home/blessed/git/service/dccd/dccd-http 

This is a webservice wich should be deployed similar to how the dccd.war is deployed. For more details read the README file accompanying the source code. 
Place the war in the same directory as the dccd.war

	# sudo cp dccd-rest.war /opt/dccd/
	
Configure tomcat to deploy this war by creating an xml file 
/usr/share/tomcat6/conf/Catalina/localhost/dccd-rest.xml specifying the location of the war to deploy. 
	
	# sudo vi /usr/share/tomcat6/conf/Catalina/localhost/dccd-rest.xml
	<?xml version="1.0" encoding="UTF-8"?>
    <Context docBase="/opt/dccd/dccd-rest.war" debug="0" crossContext="true" />
	
This service is used by the OAI-MPH and Cron jobs described in the next sections. 
Note that it is wise to not expose the rest interface to the outside world while we only have basic authentication and no https!
	
4.4 DCCD OAI Module (Optional)
------------------------------

DCCD can function as an OAI-PMH data provider.  In order for it to do so you
need to install the OAI service. 
Note that this module depends on the dccd-http module for its RESTful API. 

The OAI-MPH; dccd-oai (which needs the dccd-http to be deployed) source code can be found at 
develop.dans.knaw.nl:/home/blessed/git/service/dccd/dccd-oai.git
This is also a webservice wich should be deployed similar to how the dccd.war is deployed. It also needs some configuration. For more details read the README file accompanying the source code. 

Place the war in the same directory as the dccd.war

	# sudo cp dccd-oai.war /opt/dccd/
	
Configure tomcat to deploy this war by creating an xml file 
/usr/share/tomcat6/conf/Catalina/localhost/dccd-oai.xml specifying the location of the war to deploy. 
	
	# sudo vi /usr/share/tomcat6/conf/Catalina/localhost/dccd-oai.xml
	<?xml version="1.0" encoding="UTF-8"?>
    <Context docBase="/opt/dccd/dccd-oai.war" debug="0" crossContext="true" />
	
4.5 2.3 Cron jobs
-----------------

DCCD now uses a cron job to generate a json file with geolocations of the member organisations. 
The script uses the DCCD RESTfull API (dccd-http) to retrieve the city and country information of the organisations. 
Check that the API is working.  

	# sudo curl http://localhost:8080/dccd-rest/rest/organistion
 
Should output XML with the organisation information. 
The script is written in Python, which is a great tool anyway to have available on your server. 
Check that you have python version 2.6.6 or higher
 
 	# python -V
 	
If needed install python packages :
 
	# sudo yum install python-pip
	# sudo pip install requests docopt
 
Get the python script(s) from the source code GIT repository: 
develop.dans.knaw.nl:/home/blessed/git/service/dccd/dccd.git
under cronjobs/
Copy it to the server and make sure you can execute it. 
The script writes the json data to a file, the location is hardcoded in the python script to: /opt/dccd-home/data/geolocation_organisations.json
Make sure that the /opt/dccd-home/data directory is writable by the same user that executes the script. 
Check that the DCCD webapplication knows where it can find the script. 
It will need the geolocation.organisations set in the properties file of the application; /opt/dccd-home/dccd.properties. 

# geolocation markers (json) file paths
geolocation.organisations=/opt/dccd-home/data/geolocation_organisations.json

Create a cron job for regular updating of the json file

	# sudo crontab -e
	
And then edit it so it will look like this, for updating every day at 2 AM. 
	
	PATH=/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin
	# uncomment next line and change the email if you want notifications
	#MAILTO=user@domain
	PYTHONIOENCODING=utf8
	# Minute   Hour   Day of Month       Month          Day of Week        Command
	# (0-59)  (0-23)     (1-31)    (1-12 or Jan-Dec)  (0-6 or Sun-Sat)
	0        2          *             *               * /wherever_the_file_is/geolocate_organisations.py
	
Also every day the standard output of the script (print statements) are sent to the MAILTO email address so it will keep you informed.  

Note that if it works the webapplication shows the organisations on the map and updates the timestamp above it. 
If somehow does not have a json file, the DCCD webapplication will show an empty map for the organisations and no timestamp. 

5 EASY Tools For Data Managers
==============================

While the EASY front-end modules are intended primarily for use by customers of
the archive the tools discussed in this chapter are meant to be used by data
managers (archivists) working at the archive.

...

