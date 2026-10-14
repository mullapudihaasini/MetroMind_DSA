@echo off
if not exist bin\test\TestRunner.class (
    echo Binaries not found. Running build.bat first...
    call build.bat
)
echo Executing Automated DSA Verification Test Runner...
java -cp bin test.TestRunner
