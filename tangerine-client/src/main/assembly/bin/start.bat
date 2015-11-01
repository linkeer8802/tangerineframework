@echo off & setlocal enabledelayedexpansion

set LIB_JARS=""
cd ..\lib
for %%i in (*) do set LIB_JARS=!LIB_JARS!;..\lib\%%i
cd ..\bin


java -Xms1g -Xmx1g -XX:MaxPermSize=64M -classpath ../;%LIB_JARS% org.tangerine.PressureTest
goto end

:end
pause