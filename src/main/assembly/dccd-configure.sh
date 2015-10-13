#!/usr/bin/env bash

#
# This postinstall script performs many of the steps necessary to install and configure
# the instructure for the DCCD web server software.
# The numbered steps match the manual installation steps described in the Github INSTALL.md file:
# https://github.com/DANS-KNAW/dccd-webui/blob/master/INSTALL.md
#
# @author Peter Brewer (p.brewer@ltrr.arizona.edu)


################################
# Functions
################################

# Helper function that asks user to define a password, then checks it with a repeat
# Call it with the name of the variable that you would like the new password stored
# inside e.g.:
# Param 1 = Variable into which the password should go
# Param 2 = The question to ask
#   
function getNewPwd()
{
	local __resultvar=$1
	
	pwd1=$(whiptail --passwordbox "$2" 8 70 --backtitle "DCCD Server Configuration Wizard" --title "$1" 3>&1 1>&2 2>&3)                                                                 
	exitstatus=$?
	if [ $exitstatus = 0 ]; then
		pwd2=$(whiptail --passwordbox "Confirm password for $1" 8 70 --backtitle "DCCD Server Configuration Wizard" --title "$1" 3>&1 1>&2 2>&3)
		if [ $exitstatus != 0 ]; then
			clear
			exit 1
		fi
	else
		clear
	    exit 1
	fi
   
    # Check both passwords match
    if [ $pwd1 != $pwd2 ]; then    	
       showMessage "Error - passwords do not match!  Please try again..."
       getNewPwd "$1" "$2"
    else
       eval $__resultvar="'$pwd1'"
    fi
}

#
# Function to get a plain text response from user
# Param 1 = Question to ask
# Param 2 = Variable into which to put the response
#
function getInput()
{
	local __resultvar=$2
	VALUE=$(whiptail --inputbox "$1" 8 70 --backtitle "DCCD Server Configuration Wizard" 3>&1 1>&2 2>&3)
	                                                                       
	exitstatus=$?
	if [ $exitstatus = 0 ]; then
	     eval $__resultvar="'$VALUE'"
	else
	    clear
	    exit 1
	fi
	
}

#
# Downloads the file at the URL specified to the specified file
# Param 1 = URL
# Param 2 = file to download to
function downloadFile()
{
	wget -O "$2" "$1"  2>&1 | \
 	stdbuf -o0 awk '/[.] +[0-9][0-9]?[0-9]?%/ { print substr($0,63,3) }' | \
	whiptail --backtitle "DCCD Server Configuration Wizard" --gauge "$3" 6 70 0
	clear
}

#
# Simply display message
# Param 1 = Message
#
function showMessage()
{
	whiptail --backtitle "DCCD Server Configuration Wizard" --msgbox "$1" 10 70
	clear
}

#
# Ask the user a yes/no question.  Returns 1 for yes or 0 for no.
# Param 1 = Question
# Param 2 = The variable into which to put the response 
#
function getYesNo()
{
	local __resultvar=$2

	if (whiptail --backtitle "DCCD Server Configuration Wizard" --yesno "$1" 14 70) then
	    eval $__resultvar="1"
	else
	    eval $__resultvar="0"
	fi
}


#########################################
# Check if we're being run by root/sudo 
#########################################
if [ "$(id -u)" != "0" ]; then
	echo "This script must be run by root or with sudo privileges"
	exit 1
else
	showMessage "This wizard will collect the information required to configure and deploy your new DCCD Server, however, first we need to download installers for dependencies that are not available in the standard software repositories."
fi



###################################################
# Download binary installers not available in repos
###################################################

## Download Apache Solr
if [ ! -f /opt/dccd/apache-solr-3.5.0.tgz ]; then
	downloadFile "http://archive.apache.org/dist/lucene/solr/3.5.0/apache-solr-3.5.0.tgz" "/opt/dccd/apache-solr-3.5.0.tgz" "Downloading Apache Solr v3.5.0 installer..."
fi
# Check the file downloaded correctly
if [ ! -s /opt/dccd/apache-solr-3.5.0.tgz ]; then
	showMessage "ERROR - Failed to download Apache SOLR installer.  DCCD configuration cannot continue. Download manually and place in:\n\n - /opt/dccd/apache-solr-3.5.0.tgz\n\n ...then rerun this script."
	clear
	exit 1
fi

## Download Fedora Commons
if [ ! -f /opt/dccd/dccd-fedora-commons-repository/fcrepo-installer-3.5.jar ]; then
	downloadFile http://sourceforge.net/projects/fedora-commons/files/fedora/3.5/fcrepo-installer-3.5.jar/download /opt/dccd/dccd-fedora-commons-repository/fcrepo-installer-3.5.jar "Downloading Fedora Commons Repository v3.5 installer..."
fi
# Check the file downloaded correctly
if [ ! -s /opt/dccd/dccd-fedora-commons-repository/fcrepo-installer-3.5.jar ]; then
	showMessage "ERROR - Failed to download Fedora Commons installer.  DCCD configuration cannot continue.\nDownload manually and place in /opt/dccd/dccd-fedora-commons-repository/fcrepo-installer-3.5.jar then rerun this script"
	clear
	exit 1
fi


#########################################
# Get passwords and other input from user
#########################################

getNewPwd fedora_db_admin "Create new password for fedora_db_admin:"
getNewPwd fedoraAdmin "Create new password for fedoraAdmin:"
getNewPwd fedoraIntCallUser "Create new password for fedoraIntCallUser:"
getNewPwd ldapadmin "Create new password for ldapadmin:"
getNewPwd dccduseradmin "Create new password for dccduseradmin:"
getNewPwd dccd_webui "Create new password for dccd_webui:"
#getNewPwd dccd_oai "Create new password for dccd_oai:"
#getNewPwd dccd_rest "Create new password for dccd_rest:"
getInput "Enter email address for the system administrator: " adminEmail
getInput "Enter SMTP host for sending emails: " smtpHost
getInput "Enter domain name of this server: " serverDomain
getYesNo "DCCD needs to set the client credential configuration for PostgreSQL.  If you have already configured your pg_hba.conf file we recommend you do this manually after this script has run.  Only continue if PostGreSQL is not used for any other applications on this machine.\n\nShould DCCD Server edit your pg_hba.conf file?" editpghba
getYesNo "This is all the information needed to configure your DCCD server.  Would you like to run the configuration now?" continueConfig

clear
if [[ $continueConfig == "0" ]]
then
	exit 1
fi

# We need to hash some of these passwords
ldapadminsha=`slappasswd -h "{SSHA}" -s "$ldapadmin"`
dccduseradminsha=`slappasswd -h "{SSHA}" -s "$dccduseradmin"`


#########################################
# Store output of the remainder in logs
#########################################

# Store output in log file 
exec > >(tee /var/log/dccd-lib-postinstall.log)
exec 2>&1

printf "Configuring DCCD Server...\n"


################################
# Java JDK
################################

printf "Configuring Java environment for DCCD:\n"

# 2.2.1 Download JDK
# User should do this manually if they don't want OpenJDK

# 2.2.2 Install JDK
# User should do this manually if they don't want OpenJDK

# 2.2.3 Add the JAVA_HOME environment variable 
# Use source so we don't need to log out and in again
cp /opt/dccd/util/java.sh /etc/profile.d/
source /opt/dccd/util/java.sh 


# 2.2.4 Add java to alternatives
# User should do this manually if they don't want OpenJDK


################################
# Apache Tomcat
################################

printf "Configuring Apache Tomcat environment for DCCD:\n"

# 2.4.1 Install Tomcat 6
# DONE by rpm-maven-plugin

# 2.4.2 Give the Tomcat 6 jvm more memory to work with
echo -e '\n# Increase JVM memory size\nJAVA_OPTS="${JAVA_OPTS} -Xmx2048m -Xms2048m -server -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+AggressiveHeap"' >> /etc/tomcat6/tomcat6.conf

# 2.4.3 Configure Tomcat 6 to expect UTF-8 in percent-encoded bytes
cp /etc/tomcat6/server.xml /etc/tomcat6/server.xml.bak
sed -i -e 's/redirectPort=\"8443\" \/>/redirectPort=\"8443\" URIEncoding=\"UTF-8\" \/>/' /etc/tomcat6/server.xml

# 2.4.4	Configure the Tomcat daemon to start automatically
chkconfig tomcat6 on


################################
# Apache HTTP Server
################################

printf "Configuring Apache HTTP Server for DCCD:\n"

# 2.5.1 Install Apache HTTP Server
# DONE by rpm-maven-plugin

# 2.5.2 Configure it so it serves as a tomcat proxy
cp /opt/dccd/httpd/dccd.conf.orig /etc/httpd/conf.d/dccd.conf
sed -i -e 's/FILL.IN.YOUR@VALID-EMAIL/'$adminEmail'/' /etc/httpd/conf.d/dccd.conf
sed -i -e 's/%%%SERVER_DOMAIN%%%/'$serverDomain'/' /etc/httpd/conf.d/dccd.conf

# 2.5.3 Set up IPTables
service httpd start
chkconfig --level 3 httpd on
iptables -I INPUT 5 -p tcp -m state --state NEW,ESTABLISHED --dport 80 -j ACCEPT
service iptables save
apachectl -k graceful



################################
# PostgreSQL
################################

printf "Configuring PostgreSQL for DCCD:\n"

# 2.6.1 Install PostGreSQL
# DONE by rpm-maven-plugin

# 2.6.2 Initialize the database
service postgresql initdb

# 2.6.3 Configure auto-vacuum (optional)
# Only do this if it's not been done already!
if [ ! -f /var/lib/pgsql/data/postgresql.conf.dccd.bak ]; then
   cp /var/lib/pgsql/data/postgresql.conf /var/lib/pgsql/data/postgresql.conf.dccd.bak
   sed -i -e 's/#track_counts = on/track_counts = on/' /var/lib/pgsql/data/postgresql.conf
   sed -i -e 's/#autovacuum = on/autovacuum = on/' /var/lib/pgsql/data/postgresql.conf
fi

# 2.6.4 Configure database to accept user/password credentials
if [[ $editpghba = "1" ]]
then
	cp /var/lib/pgsql/data/pg_hba.conf /var/lib/pgsql/data/pg_hba.conf.bak
	cp /opt/dccd/postgresql/pg_hba.conf /var/lib/pgsql/data/pg_hba.conf
fi


# 2.6.5	Start the daemon
chkconfig postgresql on
service postgresql start



################################
# OpenLDAP
################################

printf "Configuring OpenLDAP for DCCD:\n"

# 2.7.1 Install OpenLDAP servers and clients
# DONE by rpm-maven-plugin

# 2.7.2 Remove the “default” OpenLDAP database (optional)
rm /etc/openldap/slapd.d/cn\=config/olcDatabase\=\{2\}bdb.ldif

# 2.7.3 Start the OpenLDAP daemon
chkconfig slapd on
service slapd start



################################
# DCCD Fedora Commons Repository
################################

printf "Configuring Fedora Commons Repository for DCCD:\n"

# 3.1.1 Create a database for Fedora Commons in PostGreSQL
cp /opt/dccd/dccd-fedora-commons-repository/create-fedora-db.sql.orig /opt/dccd/dccd-fedora-commons-repository/create-fedora-db.sql
sed -i -e 's/CHANGEME/'$fedora_db_admin'/' /opt/dccd/dccd-fedora-commons-repository/create-fedora-db.sql
su - postgres -c "psql -U postgres < /opt/dccd/dccd-fedora-commons-repository/create-fedora-db.sql"
#rm /opt/dccd/dccd-fedora-commons-repository/create-fedora-db.sql

# 3.1.2 Set the fedora_db_admin password
# This is now done during the previous step through the edited SQL file
#su - postgres -c "psql -U postgres -d postgres -c \"alter user fedora_db_admin with password '$fedora_db_admin';\""

# 3.1.3 Set the FEDORA_HOME environment variable and run it now with source so we don't need to log out and in
cp /opt/dccd/dccd-fedora-commons-repository/fedora.sh /etc/profile.d/
source /opt/dccd/dccd-fedora-commons-repository/fedora.sh

# 3.1.4 Run the Fedora Commons installer
cp /opt/dccd/dccd-fedora-commons-repository/install.properties.orig /opt/dccd/dccd-fedora-commons-repository/install.properties
sed -i -e 's/database.password=/database.password='$fedora_db_admin'/' /opt/dccd/dccd-fedora-commons-repository/install.properties
sed -i -e 's/fedora.admin.pass=/fedora.admin.pass='$fedoraAdmin'/' /opt/dccd/dccd-fedora-commons-repository/install.properties
java -jar /opt/dccd/dccd-fedora-commons-repository/fcrepo-installer-3.5.jar /opt/dccd/dccd-fedora-commons-repository/install.properties
#rm /opt/dccd/dccd-fedora-commons-repository/install.properties
chown -R tomcat:tomcat /opt/fedora-3.5

# 3.1.5 Create a symbolic link to the fedora installation
ln -s /opt/fedora-3.5 /opt/fedora

# 3.1.6 Create and configure location of data store and resource index
mkdir -p /data/fedora/objects 
mkdir -p /data/fedora/datastreams
mkdir -p /data/fedora/fedora-xacml-policies/repository-policies/default
mkdir -p /data/fedora/resourceIndex
chown -R tomcat:tomcat /data/fedora
cp /opt/fedora/server/config/fedora.fcfg /opt/fedora/server/config/fedora.fcfg.bak
sed -i -e 's/data\/objects/\/data\/fedora\/objects/' /opt/fedora/server/config/fedora.fcfg
sed -i -e 's/data\/datastreams/\/data\/fedora\/datastreams/' /opt/fedora/server/config/fedora.fcfg
sed -i -e 's/data\/resourceIndex/\/data\/fedora\/resourceIndex/' /opt/fedora/server/config/fedora.fcfg

# 3.1.7 Add Fedora Commons users
cp /opt/dccd/dccd-fedora-commons-repository/fedora-users.xml.orig /opt/fedora/server/config/fedora-users.xml
sed -i -e 's/password:fedoraAdmin/'$fedoraAdmin'/' /opt/fedora/server/config/fedora-users.xml
sed -i -e 's/password:dccd_webui/'$dccd_webui'/' /opt/fedora/server/config/fedora-users.xml
sed -i -e 's/password:dccd_rest/'$dccd_rest'/' /opt/fedora/server/config/fedora-users.xml
sed -i -e 's/password:dccd_oai/'$dccd_oai'/' /opt/fedora/server/config/fedora-users.xml
sed -i -e 's/password:fedoraIntCallUser/'$fedoraIntCallUser'/' /opt/fedora/server/config/fedora-users.xml

# 3.1.8 Change password of fedoraIntCallUser
sed -i -e 's/changeme/'$fedoraIntCallUser'/' /opt/fedora/server/config/beSecurity.xml

# 3.1.9 Limit access to passwords
chmod 0600 /opt/fedora/server/config/fedora-users.xml
chmod 0600 /opt/fedora/server/config/fedora.fcfg
chmod 0600 /opt/fedora/server/config/beSecurity.xml

# 3.1.10 Ensure that Fedora "upload" directory has enough disk space
# This optional step should be done by users if necessary

# 3.1.11 Deploy Saxon (Optional)
# Not necessary for DCCD

# 3.1.12 Start Tomcat 6
service tomcat6 start


################################
# DCCD LDAP Directory 
################################

printf "Configuring LDAP environment for DCCD:\n"

# 3.2.1 Create a separate directory folder for DCCD
mkdir /var/lib/ldap/dccd
chown ldap:ldap /var/lib/ldap/dccd

# 3.2.2 Add DANS and DCCD schemas
printf "Adding DANS schema...\n"
ldapadd -v -Y EXTERNAL -H ldapi:/// -f /opt/dccd/ldap/dans-schema.ldif
printf "Adding DCCD schema...\n"
ldapadd -v -Y EXTERNAL -H ldapi:/// -f /opt/dccd/ldap/dccd-schema.ldif

# 3.2.3 Add DCCD database
printf "Adding DCCD database...\n"
ldapadd -v -Y EXTERNAL -H ldapi:/// -f /opt/dccd/ldap/dccd-db.ldif

# 3.2.4 Add basic entries to the DCCD database
printf "Adding basic entries to the DCCD database...\n"
cp /opt/dccd/ldap/dccd-basis.ldif.orig /opt/dccd/ldap/dccd-basis.ldif
sed -i -e 's/FILL_IN@YOUR_EMAIL/'$adminEmail'/' /opt/dccd/ldap/dccd-basis.ldif
ldapadd -w secret -D cn=ldapadmin,dc=dans,dc=knaw,dc=nl -f /opt/dccd/ldap/dccd-basis.ldif
#rm /opt/dccd/ldap/dccd-basis.ldif

# 3.2.5 Change the ldapadmin password
printf "Changing the ldapadmin password...\n"
cp /opt/dccd/ldap/change-ldapadmin-pw.ldif.orig /opt/dccd/ldap/change-ldapadmin-pw.ldif
sed -i -e "s?CHANGEME?$ldapadminsha?" /opt/dccd/ldap/change-ldapadmin-pw.ldif
ldapadd -v -Y EXTERNAL -H ldapi:/// -f /opt/dccd/ldap/change-ldapadmin-pw.ldif
#rm /opt/dccd/ldap/change-ldapadmin-pw.ldif

# 3.2.6 Change the dccdadmin user’s application password
printf "Changing the dccdadmin user’s application password...\n"
cp /opt/dccd/ldap/change-dccdadmin-user-pw.ldif.orig /opt/dccd/ldap/change-dccdadmin-user-pw.ldif
sed -i -e "s?CHANGEME?$dccduseradminsha?" /opt/dccd/ldap/change-dccdadmin-user-pw.ldif
ldapadd -w "$ldapadmin" -D cn=ldapadmin,dc=dans,dc=knaw,dc=nl -f /opt/dccd/ldap/change-dccdadmin-user-pw.ldif
#rm /opt/dccd/ldap/change-dccdadmin-user-pw.ldif

################################
# DCCD SOLR Search Index 
################################

printf "Configuring Apache SOLR environment for DCCD:\n"

# 3.5.1 Install Apache SOLR 3.5
tar -xzf /opt/dccd/apache-solr-3.5.0.tgz -C /opt
#rm /opt/dccd/apache-solr-3.5.0.tgz
chown -R tomcat:tomcat /opt/apache-solr-3.5.0

# 3.5.2 Create a symbolic link to the SOLR installation and war
ln -s /opt/apache-solr-3.5.0 /opt/apache-solr
ln -s /opt/apache-solr-3.5.0/dist/apache-solr-3.5.0.war /opt/apache-solr/solr.war

# 3.5.3 Create and the solr.home-directory
mkdir -p /data/solr/cores/dendro/data
mkdir -p /data/solr/cores/dendro/conf
cp /opt/dccd/solr/config-all/solr.xml /data/solr
cp /opt/dccd/solr/protwords.txt  /data/solr/cores/dendro/conf
cp /opt/dccd/solr/schema.xml     /data/solr/cores/dendro/conf
cp /opt/dccd/solr/solrconfig.xml /data/solr/cores/dendro/conf
cp /opt/dccd/solr/stopwords.txt  /data/solr/cores/dendro/conf
cp /opt/dccd/solr/synonyms.txt   /data/solr/cores/dendro/conf
chown -R tomcat:tomcat /data/solr

# 3.5.4 Copy the Tomcat 6 context container
# n.b. don’t confuse this solr.xml with the file from the previous step with the same name
cp /opt/dccd/solr/config-tomcat/solr.xml /etc/tomcat6/Catalina/localhost
chown -R tomcat:tomcat /etc/tomcat6/Catalina/localhost
chmod -R a+x /etc/tomcat6/Catalina/localhost



################################
# DCCD Web frontend 
################################

# 4.1.1 Create the dccd-home dir
cp -R /opt/dccd/dccd-home /opt/
cp /opt/dccd-home/dccd.properties.orig /opt/dccd-home/dccd.properties
sed -i -e 's?###Fill-In-fedoraAdmin-password###?'$fedora_db_admin'?' /opt/dccd-home/dccd.properties
sed -i -e 's?###Fill-In-ldapadmin-password###?'$ldapadminsha'?' /opt/dccd-home/dccd.properties
sed -i -e 's?###Fill-In-email###?'$adminEmail'?' /opt/dccd-home/dccd.properties
sed -i -e 's?###Fill-In-host###?'$smtpHost'?' /opt/dccd-home/dccd.properties
chown -R tomcat:tomcat /opt/dccd-home
chmod -R 755 /opt/dccd-home
echo -e '\n# DCCD home directory\nJAVA_OPTS="${JAVA_OPTS} -Ddccd.home=/opt/dccd-home"' >> /etc/tomcat6/tomcat6.conf

# 4.1.7 Limit access to passwords
chmod 0600 /opt/dccd-home/dccd.properties

# 4.1.8 Deploy the webapp
cp /opt/dccd/dccd.xml /usr/share/tomcat6/conf/Catalina/localhost/
# Reload tomcat to start it
service tomcat6 force-reload


################################
# DCCD RESTful interface
################################

# 4.3 All handled by dccd-http rpm-maven-plugin


################################
# DCCD OAI Module 
################################

# 4.4 All handled by dccd-oai rpm-maven-plugin

################################
# Cron jobs 
################################


printf "\n\nDCCD backend configuration complete!\n\n";