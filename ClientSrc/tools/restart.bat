@echo off
if not "%~1"=="p" start /min cmd.exe /c %0 p&exit
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" ^| findstr java.exe') do taskkill /pid %%a
echo "Successfully closed Java ptoject!"


START "Window Title" /B src\tools\re1.bat

C:\Users\86158\.jdks\corretto-1.8.0_392\bin\java.exe "-javaagent:A:\IntelliJ IDEA 2023.2.3\lib\idea_rt.jar=51051:A:\IntelliJ IDEA 2023.2.3\bin" -Dfile.encoding=UTF-8 -classpath C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\charsets.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\access-bridge-64.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\cldrdata.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\dnsns.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\jaccess.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\jfxrt.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\localedata.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\nashorn.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\sunec.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\sunjce_provider.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\sunmscapi.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\sunpkcs11.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\ext\zipfs.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\jce.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\jfr.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\jfxswt.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\jsse.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\management-agent.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\resources.jar;C:\Users\86158\.jdks\corretto-1.8.0_392\jre\lib\rt.jar;E:\Desktop\server\src\main\resources\sqlite-jdbc-3.39.4.1.jar;E:\Desktop\client\out\production\client src.main.java.window.Main

pause