@echo off

echo Use this data to log in:
echo user: root
echo PASSWORD: mysecretpass
echo Now the page will open
timeout /t 7
start "" http://localhost:8080/?server=db&username=root
timeout /t 60
