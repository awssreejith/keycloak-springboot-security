# keycloak-springboot-security
This repository contains sample implementation for how to secure API using springboot security and keycloak

In this implementation the microservice IPL-Microservice will expose 4 endpoints viz 

/ipl/all
/ipl/playerOnly
/ipl/umpireOnly
/ipl/matchRefereeOnly

This microservice defines 3 roles viz

player

umpire

matchreferee

Thus 
 - any user under "player" role can access /ipl/all and /ipl/playerOnly
 - any user under "umpire" role can access /ipl/all and /ipl/umpireOnly
 - any user under "matchreferee" role can access ipl/all and /ipl/umpireOnly as well as /ipl/matchRefereeOnly
 
 

The same notes can be found under Notes.txt file as well


VERY VERY IMPORTANT:- IF YOU ARE INSTALLING KEYCLOAK IN A DIFFERENT SERVER AND APPLICATIONS RUNNING IN ANOTHER SERVER
THEN KEYCLOAK REQUIRES HTTPS. IF YOU WANT TO CONFIGURE HTTPS THE FIRST FOLLOW THE BELOW LINK TO ENABLE HTTPShttps://wjw465150.gitbooks.io/keycloak-documentation/content/server_installation/topics/network/https.html

This tutorial uses keycloak server and springboot application both running in localhost

This not shows the following:-

0) How to install and configure keycloak
1) How to secure APIs written in springboot using keycloak

##########################################################

Step-1:- Installing and starting keycloak
=========================================
Note: For installation follow the belo link
https://medium.com/@hasnat.saeed/setup-keycloak-server-on-ubuntu-18-04-ed8c7c79a2d9

0) start an ec-2 instance
1) cd /opt
2) sudo wget https://github.com/keycloak/keycloak/releases/download/15.0.2/keycloak-15.0.2.tar.gz
3) sudo tar -xvzf keycloak-15.0.2.tar.gz
4) sudo mv keycloak-15.0.2 keycloak

Note: In the above link there are steps to make keycloak as demon and register as a service
as well as creating a seperate user and group for keycloak. I'm not doing those. I'm just running
keycloak as a stand alone program in a shell and when shell exits, keocloak exits

VERY VERY IMPORTANT:-
IF WE DON;T FOLLOW THE BELOW TWO STEPS, WE CANNOT ACCESS KEYCLOAK SERVER OUTSIDE LOCAL HOST PUBLICALLY
******************************************************************************************************************************
5) chnage the default port of keycloak server from 8080 to something else
	sudo vi /opt/keycloak/standalone/configuration/standalone.xml
	<socket-binding name="http" port="${jboss.http.port:8888}"/>
	
6) Check the below ports are not used in this server. If so chnage all the below ports accordignly

   <socket-binding-group name="standard-sockets" default-interface="public" port-offset="${jboss.socket.binding.port-offset:0}">
        <socket-binding name="ajp" port="${jboss.ajp.port:8009}"/>
        <socket-binding name="http" port="${jboss.http.port:8888}"/>
        <socket-binding name="https" port="${jboss.https.port:8443}"/>
        <socket-binding name="management-http" interface="management" port="${jboss.management.http.port:9990}"/>
        <socket-binding name="management-https" interface="management" port="${jboss.management.https.port:9993}"/>
        <socket-binding name="txn-recovery-environment" port="4712"/>
        <socket-binding name="txn-status-manager" port="4713"/>
        <outbound-socket-binding name="mail-smtp">
            <remote-destination host="${jboss.mail.server.host:localhost}" port="${jboss.mail.server.port:25}"/>
        </outbound-socket-binding>
    </socket-binding-group>


7) sudo vi /opt/keycloak/standalone/configuration/standalone.xml

Replace the below stuff
-----------------------
<interface name="management">
    <inet-address value="${jboss.bind.address.management:127.0.0.1}"/>
</interface>
<interface name="public">
    <inet-address value="${jboss.bind.address:0.0.0.0}"/>
</interface>

with this stuff
---------------
<interface name="management">
    <any-address/>
</interface>
<interface name="public">
    <any-address/>
</interface>

8) save the file

9) cd /opt/keycloak/bin
10)sudo ./add-user-keycloak.sh -u admin -p admin

After step 10 only we can access the admin dashboard

****************************************************************************************************************************** 
11) cd /opt/keycloak/bin
12) sudo ./standalone.sh

13) Access the management endpoint as below from another system [my public IP was 13.126.195.126]

	http://13.126.195.126:8888

###########################################################################################################

Step-2:- Configure keycloak
===========================
Follow below link to configure springboot application with keycloak

https://medium.com/devops-dudes/securing-spring-boot-rest-apis-with-keycloak-1d760b2004e

0) create a realm [eg:- IPL-Realm]

1) Under the new real created create a client [eg:- IPL-Microservice]

	client id- IPL-Microservice
	client protocol - openid-connect
	client url:- http://localhost:8181
	
	the client url is the base URL of the microservice

2) add additional details as mentioned in the link

3) Under roles tab for the client created, create three roles [player, umpire, matchreferee]

4) Now create realm role which maps to the roles created in step#3.
Note: Check the above link to see how to do that. I had cerated 3 roles [app-player, app-umpire, app-matchreferee]

5)Under manage->user create 3 users 
[viratkohli -> app-player]
[kumardharmasena -> app-umpire]
[simontauffel -> app-matchreferee]

6) copy the token endpoint from Realm tab
http://localhost:8080/auth/realms/IPL-Realm/protocol/openid-connect/token

7) go to clients tab and go to credentials and copy the secret key
eg:- ca1c9dcb-63c0-448c-8f20-d8737b8dcc70

8) Go to Realm settings tab and Tokens. By default the access token lifespan is only 5 minutes. chnage this to a greater value accordigly

9) Generate access tokens for all users generated as below

Example 
For windows issue the below command

curl -X POST "http://localhost:8080/auth/realms/IPL-Realm/protocol/openid-connect/token" --header "Content-Type: application/x-www-form-urlencoded" --data-urlencode "grant_type=password" --data-urlencode "client_id=IPL-Microservice" --data-urlencode "client_secret=ca1c9dcb-63c0-448c-8f20-d8737b8dcc70" --data-urlencode "username=viratkohli" --data-urlencode "password=p@mypassword"

and Linux issue the below command 

curl -X POST 'http://localhost:8080/auth/realms/IPL-Realm/protocol/openid-connect/token' \
 --header 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'grant_type=password' \
 --data-urlencode 'client_id=IPL-Microservice' \
 --data-urlencode 'client_secret=ca1c9dcb-63c0-448c-8f20-d8737b8dcc70' \
 --data-urlencode 'username=viratkohli' \
 --data-urlencode 'password=mypassword'
 
 Copy the access token returned
 
 Likewise do it for all users created
 
#########################################################################################################################################

Step-3) Create springboot application
Note: This application consists of a microservice called IPL-Microservice with 4 REST endpoints where

/ipl/player --> only accesible to players 
/ipl/umpire --> only accesible to umpires and match referee
/ipl/matchreferee --> only accesible to match referee
/ipl/all --> accesible to all


0)Dependencies: 
Add Spring Web, 
Spring Security
Spring Boot DevTools

1) update pom.xml to add the below

a) 

<properties>
   <keycloak.version>15.0.2</keycloak.version>
</properties>

b)

   <dependency>
      <groupId>org.keycloak</groupId>
      <artifactId>keycloak-spring-boot-starter</artifactId>
      <version>${keycloak.version}</version>
   </dependency>
   
   	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-web</artifactId>
		<version>5.5.0</version>
	</dependency>
	
	
	<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-config</artifactId>
		<version>5.5.1</version>
	</dependency>
   
   
c) Add Dependency Management
Below the <dependencies> section add below section.
<dependencyManagement>
   <dependencies>
      <dependency>
         <groupId>org.keycloak.bom</groupId>
         <artifactId>keycloak-adapter-bom</artifactId>
         <version>${keycloak.version}</version>
         <type>pom</type>
         <scope>import</scope>
      </dependency>
   </dependencies>
</dependencyManagement>


2) add the below in application.properties file

server.port                         = 8099
keycloak.realm                      = IPL-Realm
keycloak.auth-server-url            = http://localhost:8080/auth
keycloak.ssl-required               = external
keycloak.resource                   = IPL-Microservice
keycloak.credentials.secret         = ca1c9dcb-63c0-448c-8f20-d8737b8dcc70
keycloak.use-resource-role-mappings = true
keycloak.bearer-only                = true


3) implement configuration class and rest controller [check the source code in git]

4) ensure the endpoints are not accesible simply through GET request 

5) issue the proper curl command with the token as below

For player
==========

curl -X GET "http://localhost:8099/ipl/playerOnly" --header "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDcml0dndkNTdVSFcyaHFleDNDaVR4b2tlcDNTem84RlNJNENrUGlJUmhNIn0.eyJleHAiOjE2MzU1MjU2NDcsImlhdCI6MTYzNTUwNzY0NywianRpIjoiZGJkMjVkMDItOGQ0ZS00Y2JmLTgxNjYtZWRkMTNkZTdkOGUxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL0lQTC1SZWFsbSIsInN1YiI6IjU0ZTczOGZhLTkwYmItNDNiYi1iNjRhLWY1MWY0OWNjNjFkNyIsInR5cCI6IkJlYXJlciIsImF6cCI6IklQTC1NaWNyb3NlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiZjdmZTY3ZDktMGQ4Ni00NjJhLWE4ZjMtYjNmNTAyZTg2N2ZmIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgxODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImFwcC1wbGF5ZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJJUEwtTWljcm9zZXJ2aWNlIjp7InJvbGVzIjpbInBsYXllciJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImY3ZmU2N2Q5LTBkODYtNDYyYS1hOGYzLWIzZjUwMmU4NjdmZiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoidmlyYXQga29obGkiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ2aXJhdGtvaGxpIiwiZ2l2ZW5fbmFtZSI6InZpcmF0IiwiZmFtaWx5X25hbWUiOiJrb2hsaSJ9.PM4rlQLGUav98XQsoEGvRylx7NymLBvIM0uWq4119L5IAIZ6-nDNcRH6xk7lzJXyAC4JClWCLXDBfL9tRHurCgFjKE5TFMGr8bqxVNao_GG8OmSjfgjTdU3GAL53MzetlT9P-egvSVerzZ8NF8FkwDmkHCza2g1P0A1uSj1ChitUZ_zynwqabm9f_yce4-Q12HvAYfldgqGhOmYSK8s-UiS46O5PKu15crTg4rFHoxAkAJmLkthMlaIwdE_FOZssTGIyXwrJRHIUfr5zxEjKIBg3PgjhHwjueauX2EctB7gmTupHZMus2dRQoJrWGjh0WRWmnWONpzze7aiQd8ImEQ"


For Umpire
==========

curl -X GET "http://localhost:8099/ipl/umpireOnly" --header "Authorization: bearer "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDcml0dndkNTdVSFcyaHFleDNDaVR4b2tlcDNTem84RlNJNENrUGlJUmhNIn0.eyJleHAiOjE2MzU1MjU3NzUsImlhdCI6MTYzNTUwNzc3NSwianRpIjoiODkwMzdiYzAtOTRjMC00NDdlLWI0ZTItMTYxOTJmNGY4Zjg3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL0lQTC1SZWFsbSIsInN1YiI6ImVkMjNlNjIxLTEyMGUtNGNhNS1hMWIwLWViNGEzNjczYzQyOCIsInR5cCI6IkJlYXJlciIsImF6cCI6IklQTC1NaWNyb3NlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiN2YzOWQzMjktNDAyMi00ODJkLTk4ZmItNzg0NWQ1ZDE5N2NmIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgxODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImFwcC11bXBpcmUiXX0sInJlc291cmNlX2FjY2VzcyI6eyJJUEwtTWljcm9zZXJ2aWNlIjp7InJvbGVzIjpbInVtcGlyZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjdmMzlkMzI5LTQwMjItNDgyZC05OGZiLTc4NDVkNWQxOTdjZiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJrdW1hcmRoYXJtYXNlbmEifQ.ROoY72w4Josv3l3F7AuDG5oWMy9eDEw9-A_xoAn1oFzM0_diMTsOcpCc-wbOsALlH5VcTHm7fMZw0s66LS9VgYeEo7I3dCQgwEssoDT9Vu3-fSjZqo8txiH3Vki4vX9IC0pnrS0bAhOP3e0Gf9nFfCYnqbzJx1OuusDOyH6fSjZ-UQFf_ScvPagX8-0lWUSL3fRHGXUkPBcC9m94dAIDYfSzYvUZTzWCu93VGO5I-xOtIV4h0yWpCdRWzoKTNFP0YrlOlvZ_QJ15e1d7rjHMb_MABaBt5xFzLdJUx6MNbNl2sf8SZU2opAaSGhjwiREzqh-C8M7pHl2iaMJSefbo9Q"


For Math referee
================

curl -X GET "http://localhost:8099/ipl/matchRefereeOnly" --header "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDcml0dndkNTdVSFcyaHFleDNDaVR4b2tlcDNTem84RlNJNENrUGlJUmhNIn0.eyJleHAiOjE2MzU1MjU5NTksImlhdCI6MTYzNTUwNzk1OSwianRpIjoiZDU2NWRjMWUtZDhkNC00ZmFiLWE0NDEtNjU5ZTk4OTFhOTNjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL0lQTC1SZWFsbSIsInN1YiI6IjU0Y2M4YmMyLTg5MDctNDE0MC1hMzhhLWVjZWY5YjFhNWE5ZCIsInR5cCI6IkJlYXJlciIsImF6cCI6IklQTC1NaWNyb3NlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiMzg3OWI0NzktMTVlOC00OGI3LTgxMDctMWE2YWFjYjIzZjQ4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgxODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImFwcC1tYXRjaHJlZmVyZWUiLCJhcHAtdW1waXJlIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiSVBMLU1pY3Jvc2VydmljZSI6eyJyb2xlcyI6WyJtYXRjaHJlZmVyZWUiLCJ1bXBpcmUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiIzODc5YjQ3OS0xNWU4LTQ4YjctODEwNy0xYTZhYWNiMjNmNDgiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2ltb250YXVmZmVsIn0.c1Qjggh64O7Wfa1aDxVMtD0X_Ok0AK5ZpGZdmB4B93YWcFCeLQEw0zGIZG7HzSYO0jJh1RCVshHYrKDH04uFJ6LMKbqJF6tK45ztHcJamSMVtXBrckukf5GCHl7VsnlTMx2YKqzODMsJg2FeVJugVzvJ0MxA60yDLL9KxvnmkYVGEftg3lYACExiYX-vSGMch74P0YhUmvsSNCdTczdIeoy7AEeW43P9GgbVMtso1uEArFErJBrXpdOfBcsBidV1NQ9BfQa30O0gwf9qTBfzmcA6CsGyvA4nvvBJTSw2V5VBQr5Gdqf7tTyar_QdlLfr84m_b88RQ7YimGma9u74lw"
