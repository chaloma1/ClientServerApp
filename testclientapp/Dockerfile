FROM openjdk:8-jdk-alpine
EXPOSE 8080
ADD target/testclientapp.jar testclientapp.jar
ENTRYPOINT ["java","-jar","testclientapp.jar"]