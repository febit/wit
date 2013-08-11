
@echo off

cd /d %~dp0

rem set CLPATH=%JAVA_HOME%\lib\classes.zip;%JCUP_HOME%\java-cup-11a.jar

rem %JAVA_HOME%\bin\java -classpath %CLPATH% java_cup.Main -parser Parser -symbols Tokens -interface -noscanner .\Parser.cup

%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -jar java_cup-0.12zqq.jar -parser Parser -symbols Tokens -runtimepackage webit.script.core.java_cup.runtime -interface -noscanner -nopositions -destdir ../java/webit/script/core Parser.cup

pause

