#In order to run application you need to modify api_server.yml - configuration file.
To customize:
-mongoDB connector
-local path to json schema files which are in source-resources directory in repository
     
#To build: 
mvn package 
#To run: 
After that: java -jar target/server_api-1.0.1-SNAPSHOT.jar server api_server.yml
