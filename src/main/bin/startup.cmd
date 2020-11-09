@echo off
rem Copyright 1999-2018 Alibaba Group Holding Ltd.
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.
if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

rem the swagger address, which can be online or local
set SWAGGER_LOCATION=http://localhost:18083/v2/api-docs

rem File directory for JMeter script output
set JMX_FILE_DIR=D:/jmeter-script/

set SERVER=swagger2jmx-plugin

rem set exec options
set "EXEC_OPTS=-Dfile.encoding=utf-8"
set "EXEC_OPTS=%EXEC_OPTS% -jar %BASE_DIR%\%SERVER%.jar"
set "EXEC_OPTS=%EXEC_OPTS% --i=%SWAGGER_LOCATION% --o=%JMX_FILE_DIR%"

set COMMAND="%JAVA%" %EXEC_OPTS%

rem start generate command
%COMMAND%

pause