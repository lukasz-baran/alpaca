@ECHO OFF
SETLOCAL

set MAIN_JAR=%FINAL_NAME%

rem ------ SETUP DIRECTORIES AND FILES ----------------------------------------
rem Remove previously generated java runtime and installers. Copy all required jar files into the input/libs folder.

IF EXIST target\java-runtime rmdir /S /Q  .\target\java-runtime
IF EXIST target\installer rmdir /S /Q target\installer

xcopy /S /Q target\lib\* target\installer\input\libs\
rem copy target\%MAIN_JAR% target\installer\input\libs\


rem Uncomment this if you want to re-generate the list of dependencies
rem
rem set manual_modules=,javafx.controls,javafx.graphics,javafx.fxml
rem for /f %%i in ('jdeps --multi-release 17 --ignore-missing-deps --print-module-deps --class-path "target/lib/*" target/classes/com/evolve/AlpacaSpringApp.class') do set detected_modules=%%i
rem echo jlink
rem echo %detected_modules%
rem echo %detected_modules%%manual_modules%

set all_modules=%detected_modules%%manual_modules%
set all_modules=java.base,java.compiler,java.instrument,java.management.rmi,java.desktop,java.prefs,java.scripting,java.security.jgss,java.sql.rowset,jdk.crypto.cryptoki,jdk.net,javafx.graphics,javafx.fxml,javafx.controls
rem removed: jdk.httpserver,jdk.jfr,jdk.attach,jdk.jdi,jdk.unsupported

echo executing "jlink"

call "jlink" ^
    --no-header-files ^
    --no-man-pages ^
    --compress=2 ^
    --strip-debug ^
    --module-path "target/lib" ^
    --add-modules %all_modules% ^
    --output target/java-runtime

echo executing "jpackage"

mkdir input
cp target/%MAIN_JAR% input/
rem --win-console
rem   --input input ^
rem    --input target/installer/input/libs ^

rem  --win-console ^
call "jpackage" ^
    --type msi ^
    --dest target/installer ^
    --input input ^
    --name alpaca ^
    --main-class org.springframework.boot.loader.PropertiesLauncher ^
    --main-jar %MAIN_JAR% ^
    --runtime-image target/java-runtime ^
    --win-menu ^
    --win-shortcut ^
    --win-dir-chooser ^
    --win-per-user-install ^
    --icon src/main/resources/alpaca.ico ^
    --vendor "Evolve" ^
    --copyright "Copyright © 2023-24 Evolve"
endlocal
@ECHO ON