@echo off
if not exist bin\MetroMind.class (
    echo Binaries not found. Running build.bat first...
    call build.bat
)
echo Launching Metro Mind Console Application...
java -cp bin MetroMind
