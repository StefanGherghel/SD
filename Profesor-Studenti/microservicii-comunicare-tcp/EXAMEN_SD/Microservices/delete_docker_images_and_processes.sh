
docker stop $(docker ps -a | awk 'NR>1 {print $1}')
docker rm $(docker ps -a | awk 'NR>1 {print $1}')
docker rmi -f $(docker images -a | grep -v "my_docker" | awk 'NR>1 {print $3}')

