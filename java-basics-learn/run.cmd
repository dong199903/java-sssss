@echo off
setlocal
cd /d "%~dp0"
if "%~1"=="learn" shift
set ARGS=%*
.\mvnw.cmd -q compile exec:java -Dexec.mainClass=org.example.learn.Learn -Dexec.args="%ARGS%"
