FROM openjdk:8-jdk-alpine
ADD target/TeacherMicroservice-1.0.0-jar-with-dependencies.jar TeacherMicroservice.jar
ADD questions_database.txt questions_database.txt
ENTRYPOINT ["java","-jar", "TeacherMicroservice.jar"]