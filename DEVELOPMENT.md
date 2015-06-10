
DCCD Development Guide
======================

The VM can be used to run and debug the front-end modules and tools of DCCD.
The Application Under Development (AUD) will typically be run in the Eclipse
IDE. The back-end modules will run inside the VM. The AUD must be configured to
connect to the back-end modules on the VM. (Note by the way, that is different
from the configuration you should use at test and production servers, where
everything is installed on one host.)

Create a VM
-----------

The easiest is to download a complete VM "CentOS-6.5-x86_64-Basic Server.vdi" from https://virtualboximages.com/CentOS
Then add this 'machine' in Virtualbox. 

The network settings should be set to use NAT and port forwarding, 
so you can fix the url's in your properties files on the host OS (where you run the app you test).

The dccd.properties on the guest (VM) need the normal ports, but the dccd.properties on the Host (the develoment OS) need to have the forwarded ports!
 Solr and Fedora run on Tomcat, so have 8080. LDAP uses 389. 

The DCCD Virtual Machine (VM) should run the DCCD back-end services in the development environment. 
See the "DCCD Installation Guide" for details about a complete instance of the full DCCD
system. You will need to perform the installation described in the
Installation Guide yourself:

 * The standard components (except Apache HTTP Server) and back-end modules need to be installed on the VM;
 * The front-end modules and tools will typically be run in your IDE so that you
   can develop them in the customary write-compile-run-loop or run them in a
   debugger, when necessary. 
   Note that it might be handy to put the RESTfull API on the VM (dccd-http)

 
 
Tips: With Fedora commons and Solr you can use the webinterface that is deployed on Tomcat. 
For LDAP the ApacheDirectory Studio is usefull and for Postgress you could use the pgAdmin application. 

