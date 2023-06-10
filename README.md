# Take-Out-App

# Introduction
A simple restaurant takeaway ordering software, mainly divided into the user's cell phone side and the back office management side.

Mobile users can register an account and login, browse dishes, set delivery address, order, checkout and view history orders.

The back-end management page can manage staff accounts, manage dishes and flavors, and manage orders.
# get the test demo
This is client page, you should open it use mobile or set your browse size to phone size

The test account is test, password is 123456

http://ec2-16-50-171-207.ap-southeast-4.compute.amazonaws.com/takeout/index.html

# How to use by local server
- copy all file to your local machine

- download java8, mysql, redis

- create local MySql database and import takeout.sql
- set your own application.yml, I give an example in /src/main/java/resources/application.yml.exmaple
- use maven to package projects
- run command in Take-Out-App path:  java -jar .\target\takeout-1.0-SNAPSHOT.jar

- Then open frontend by mobile: localhost:8080/takeout/index.html

- backend website: localhost:8080/backend/index.html

# how to run in cloud host:
- use maven to package the project
- login your host
- download docker and docker compose # do not forget to close firewall
- put takeout-1.0-SNAPSHOT.jar, Dockerfile and docker-compose.yml into same path.
- run command: docker-compose up -d
- then you can view you page
- frontend: IPv4/takeout/index.html
- backend: IPv4/takeout-admin/index.html



