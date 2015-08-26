#!/usr/bin/env bash

#
# This postinstall script performs many of the steps necessary to install and configure
# the instructure for the DCCD web server software.
# The numbered steps match the manual installation steps described in the Github INSTALL.md file:
# https://github.com/DANS-KNAW/dccd-webui/blob/master/INSTALL.md
#
# @author Peter Brewer (p.brewer@ltrr.arizona.edu)


printf "\nCONFIGURING DCCD WEB APPLICATION FRONTEND...\n\n"

################################
# DCCD Web frontend 
################################

# 4.1.1 Create the dccd-home dir
# The directory is already created by rpm-maven-plugin, but we need to configure the .properties files
sed -i -e "s?###Fill-In-fedoraAdmin-password###?$fedora_db_admin?" /opt/dccd/dccd-home/dccd.properties
sed -i -e "s?###Fill-In-ldapadmin-password###?$ldapadminsha?" /opt/dccd/dccd-home/dccd.properties
sed -i -e "s?###Fill-In-email###?$adminEmail?" /opt/dccd/dccd-home/dccd.properties
sed -i -e "s?###Fill-In-host###?$smtpHost?" /opt/dccd/dccd-home/dccd.properties
echo -e '\n# DCCD home directory\nJAVA_OPTS="${JAVA_OPTS} -Ddccd.home=/opt/dccd/dccd-home"' >> /etc/tomcat6/tomcat6.conf

# 4.1.7 Limit access to passwords
chmod 0600 /opt/dccd/dccd-home/dccd.properties

# 4.1.8 Deploy the webapp
# DCCD.xml file is created by maven-rpm-plugin
# Reload tomcat to start it
service tomcat6 force-reload


################################
# DCCD RESTful interface
################################



################################
# DCCD OAI Module 
################################
