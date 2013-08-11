@echo off

cd /d %~dp0

%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -jar %JFLEX_HOME%\lib\JFlex.jar -d ../java/webit/script/core Lexer.jflex

pause
