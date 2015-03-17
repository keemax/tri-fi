tri-fi
======

Project to track locations of employees at Zappos. This is the backend that accepts location updates from a daemon running on their laptop.

INSTALL
=======
'mvn package'
'cp target/tri-fi-<version>.war <tomcat webapp>/'

in the classpath, you need a 'spring.properties' file that has the following properties:

aws.key=<aws key> #for dynamodb
aws.secret=<aws secret> #for dynamodb
google.clientId=<google client id> #for google prediction api
google.secret=<google secret> #for google prediciton api
google.service.email=<email> #for google prediciton api
prediction.model=<prediction model> #for google prediction api


DynamoDB Setup
==============
table: employees{pri:{hash:"username"},secondary-global-index:{hash:"hostname-index"},"realname"}
table: test-locations-2{pri:{hash:"hostname",range:"timestamp"},secondary-global-index:{hash:"hostname",range:N"floor"},N"x",N"y"}
table: router-signature{pri:{hash:"id",range:"hostname"},secondary-global-index:{hash:"id",range:"timestamp"},"routers"}
table: version-sets{pri:{hash:"id",range:"version"},N"floor","location","routerSignature",N"x",N"y"}

