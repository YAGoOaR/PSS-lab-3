@echo off
echo Stopping all Docker containers...
FOR /f "tokens=*" %%i IN ('docker ps -q') DO docker stop %%i

echo Deleting all Docker containers...
FOR /f "tokens=*" %%i IN ('docker ps -aq') DO docker rm %%i

echo Deleting all Docker images...
FOR /f "tokens=*" %%i IN ('docker images -aq') DO docker rmi -f %%i

echo Done.
