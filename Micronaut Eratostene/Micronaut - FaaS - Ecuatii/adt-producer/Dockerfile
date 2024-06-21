FROM openjdk:8-jdk-alpine
ADD target/adt-producer-0.1-jar-with-dependencies.jar adt-producer-0.1.jar
ADD src/main/resources/ .
ENTRYPOINT ["java","-jar", "adt-producer-0.1.jar"]

