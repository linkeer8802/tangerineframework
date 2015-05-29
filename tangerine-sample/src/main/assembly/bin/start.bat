@echo off & setlocal enabledelayedexpansion

set LIB_JARS=""
cd ..\lib
for %%i in (*) do set LIB_JARS=!LIB_JARS!;..\lib\%%i
cd ..\bin


java -server -Xms1g -Xmx1g -Xmn600m -XX:PermSize=128m -XX:SurvivorRatio=8 -XX:+UseParallelGC -classpath ../;%LIB_JARS% org.tangerine.sample.ServerSample
goto end

:end
pause