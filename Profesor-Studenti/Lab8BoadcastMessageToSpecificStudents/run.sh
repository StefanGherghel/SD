#!/bin/bash

exec java -jar MessageManagerMicroservice/target/MessageManagerMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar &

sleep 1;
exec java -jar FilterMessageMicroservice/target/TeacherMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar &

exec java -jar TeacherMicroservice/target/TeacherMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar &

sleep 1;
for student in 1..4
do
    exec java -jar StudentMicroservice/target/StudentMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar &
done