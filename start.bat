@ECHO OFF
SET BINDIR=%~dp0
CD /D "%BINDIR%"
"%ProgramFiles(x86)%\Java\jre7\bin\java.exe" -Xmx1024M -Xms1024M -jar diveb0mb.jar
PAUSE