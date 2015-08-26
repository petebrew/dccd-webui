#!/usr/bin/env bash

#
# This postinstall script performs many of the steps necessary to install and configure
# the instructure for the DCCD web server software.
# The numbered steps match the manual installation steps described in the Github INSTALL.md file:
# https://github.com/DANS-KNAW/dccd-webui/blob/master/INSTALL.md
#
# @author Peter Brewer (p.brewer@ltrr.arizona.edu)


printf "\nCONFIGURING DCCD WEB APPLICATION FRONTEND...\n\n"


# 4.1.8 Deploy the webapp
# DCCD.xml file is created by maven-rpm-plugin
# Reload tomcat to start it
service tomcat6 force-reload