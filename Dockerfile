# get base image
FROM openjdk:8-alpine

COPY ./takeout-1.0-SNAPSHOT.jar /tmp/app.jar


#set entry
ENTRYPOINT java -jar /tmp/app.jar
