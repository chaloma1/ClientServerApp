FROM openjdk:8-jdk-alpine
EXPOSE 8443
ADD target/testserverapp.jar testserverapp.jar
ENTRYPOINT ["java","-jar","testserverapp.jar"]