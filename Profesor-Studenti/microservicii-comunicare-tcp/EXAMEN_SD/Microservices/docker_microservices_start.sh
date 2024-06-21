sudo service docker restart
docker pull registry:2
docker run --restart=always -d -p 5000:5000 --name registru_docker registry:2

docker build -t localhost:5000/heartbeat_microservice:v1 HeartBeatMicroservice/.
docker build -t localhost:5000/message_microservice:v1 MessageManagerMicroservice/.
docker build -t localhost:5000/student_microservice:tip1 StudentMicroservice/.
docker build -t localhost:5000/student_microservice:tip2 StudentMicroservice/.
docker build -t localhost:5000/teacher_microservice:v1 TeacherMicroservice/.

docker run -d -p 1500:1500 --name message_manager --network=ms-net localhost:5000/message_microservice:v1
docker run -d -p 1600:1600 -e MESSAGE_MANAGER_HOST='message_manager' --name teacher_microservice --network=ms-net localhost:5000/teacher_microservice:v1
docker run -d -p 2000:2000 -e MESSAGE_MANAGER_HOST='message_manager' --name heartbeat_microservice --network=ms-net localhost:5000/heartbeat_microservice:v1


docker run -d -p 1700:1700 -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_1 --network=ms-net localhost:5000/student_microservice:tip1
docker run -d -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_2 --network=ms-net localhost:5000/student_microservice:tip2




