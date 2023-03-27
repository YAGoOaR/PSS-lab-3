@echo off
echo Running...
powershell "& './run.bat' *>&1  | Tee-Object -FilePath './output.txt'"
timeout /t 30
