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
 
 

