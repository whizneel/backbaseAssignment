**KALAH GAME**

This repo implements 6 stone kalah game.
It is made using spring boot application in java.
It uses embedded H2 as database and tomcat as embedded application server.

**API Requests** 

_API to create game_

curl -X POST \
  http://127.0.0.1:8090/games \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  
  
 _API to play game_
 
 curl -X PUT \
   http://127.0.0.1:8090/games/kalah-8054273056690486637/pits/10 \
   -H 'Content-Type: application/x-www-form-urlencoded' \
   
   **postman link**
   https://www.getpostman.com/collections/12e319bb8d90c76fe3f6
 
 base_url: http://127.0.0.1:8090
 