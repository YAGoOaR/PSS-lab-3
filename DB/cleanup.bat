@echo off
py database\drop_db.py
echo DB is dropped. Now stop and delete the docker container. After that, delete the docker image.
