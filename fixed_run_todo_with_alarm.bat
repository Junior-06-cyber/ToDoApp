@echo off
set JAVAFX="C:\Users\junio\Downloads\openjfx-17.0.15_windows-x64_bin-sdk\javafx-sdk-17.0.15\lib"

echo Cleaning previous build...
rd /s /q out
mkdir out

echo Compiling Java files...
javac --module-path %JAVAFX% --add-modules javafx.controls,javafx.media -d out src\*.java

echo Copying resources...
xcopy /Y /Q resources\* out\
xcopy /Y /Q resources\*.png out\resources\
copy /Y alarm.wav out\

echo Running application...
cd out
java --module-path %JAVAFX% --add-modules javafx.controls,javafx.media Main
cd ..

pause
