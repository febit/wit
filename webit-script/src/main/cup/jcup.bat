
@echo off

cd /d %~dp0

rem set CLPATH=%JAVA_HOME%\lib\classes.zip;%JCUP_HOME%\java-cup-11a.jar
 
%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -jar java_cup_webit-20150308.jar -exception ParseException -destdir ../java/webit/script/core -destresdir ../resources/webit/script/core Parser.cup

pause

