proxy-soap
==========

Proxy Soap
[![Build Status](https://travis-ci.org/jlmwork/proxy-soap.svg?branch=master)](https://travis-ci.org/jlmwork/proxy-soap)


Trello : https://trello.com/b/eSqIxqmG/proxy-soap


Tests on WebLogic
-----------------
The weblogic-maven-plugin (available from WLS 10.3.3) is required.

To get this plugin :

* A local installation of WebLogic 10.3.3 is required. Go to <INSTALLATION>/wlserver_10.3/server/lib/
* Launch : `java -jar wljarbuilder.jar -profile weblogic-maven-plugin`
* Extract the pom from the newly created jar weblogic-maven-plugin.jar!/META-INF/maven/com.oracle.weblogic/weblogic-maven-plugin/pom.xml
* launch the Maven install command : `mvn install:install-file -Dfile=weblogic-maven-plugin.jar -DpomFile=pom.xml`

See [Official Documentation](https://docs.oracle.com/cd/E17904_01/web.1111/e13702/maven_deployer.htm#DEPGD386) for full details

Launch the tests

Your target WebLogic instance needs to be started.
Launch the integration-test phase with weblogic profile with weblogic params :

`mvn integration-test -Pweblogic -Dwlhost=localhost -Dwlport=7001 -Dwluser=weblogic -Dwlpwd=xxxx`

