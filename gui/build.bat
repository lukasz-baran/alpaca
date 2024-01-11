@ECHO OFF
SETLOCAL
rem call mvn clean install

rem Uncomment this if you want to re-generate the list of dependencies
rem 
rem set manual_modules=,javafx.controls,javafx.graphics,javafx.fxml
rem for /f %%i in ('jdeps --multi-release 17 --ignore-missing-deps --print-module-deps --class-path "target/lib/*" target/classes/com/evolve/AlpacaSpringApp.class') do set detected_modules=%%i
rem echo jlink
rem echo %detected_modules%
rem echo %detected_modules%%manual_modules%

set all_modules=%detected_modules%%manual_modules%
set all_modules=java.base,java.compiler,java.instrument,java.management.rmi,java.desktop,java.prefs,java.scripting,java.security.jgss,java.sql.rowset,jdk.crypto.cryptoki,jdk.net,javafx.graphics,javafx.fxml,javafx.controls,javafx.web
rem removed: jdk.httpserver,jdk.jfr,jdk.attach,jdk.jdi,jdk.unsupported

jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path "target/lib" --add-modules %all_modules% --output target/java-runtime
echo jpackage

mkdir input
cp target/gui-1.0.0-SNAPSHOT.jar input/
rem --win-console
jpackage --type msi --dest output/installer --input input --name alpaca --main-class org.springframework.boot.loader.PropertiesLauncher --main-jar gui-1.0.0-SNAPSHOT.jar --runtime-image target/java-runtime --win-menu --win-shortcut --win-console --icon src/main/resources/alpaca.ico
endlocal
@ECHO ON