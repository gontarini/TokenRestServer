In order to run application you need to modify api_server.yml - configuration file.
The most important things to update there are:
-mongo connection data
-local path to json schema files which are in source-resources directory in repository

Whatsmore, if you want to add application usage, you need to keep in template showed in configuration file.
For instance, in facebook case it is already look like this:

facebook:
   - appId: 181981548889895
     secret: e86e3149512d6a51fb2630d83ca64476
   - appId: 10923123123
     secret : jdasodoasd
     
Let's assume that you want to add application with the following configs: appId - 123 and secret - secret, so in configuration file, 
you should append the following lines:

facebook:
   - appId: 181981548889895
     secret: e86e3149512d6a51fb2630d83ca64476
   - appId: 10923123123
     secret : jdasodoasd
   - appId: 123
     secret: secret
     
If configurations are updated, then you can build maven project with the following command in prompt: mvn package 
After that: java -jar target/server_api-1.0.1-SNAPSHOT.jar server api_server.yml

And server is running..
