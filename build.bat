@echo off
echo ===================================================
echo   Compiling Metro Mind Java Project...
echo ===================================================
if not exist bin mkdir bin
javac -d bin -sourcepath src src/MetroMind.java src/model/*.java src/service/*.java src/util/*.java src/test/*.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    exit /b %errorlevel%
)
echo [SUCCESS] Build succeeded! Bytecode generated in bin/
