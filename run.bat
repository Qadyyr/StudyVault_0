@echo off
echo Compiling StudyVault...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.6.0.jar" -d bin src\DBConnection.java src\MainApp.java src\LoginScreen.java src\RegisterScreen.java src\HomeScreen.java src\CoursesScreen.java src\MaterialsScreen.java src\NotesScreen.java src\TodoScreen.java
if %errorlevel% neq 0 (
    echo Compile failed. Check errors above.
    pause
    exit
)
echo Running StudyVault...
java -cp "bin;lib\mysql-connector-j-9.6.0.jar" MainApp
pause