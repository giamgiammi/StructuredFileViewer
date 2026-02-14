Write-Host "Start project build"
.\mvnw clean compile

Write-Host "Start image build"
.\mvnw javafx:jlink -Pforce-modular

Write-Host "Start packaging"

$version = Get-Content "target/classes/version.txt"
Write-Host "Detected version: $version"

$arch = [System.Runtime.InteropServices.RuntimeInformation]::OSArchitecture
Write-Host "Detected architecture: $arch"

$args = Get-Content "target/classes/launcher-args.txt"
Write-Host "Detected args: $args"

$args = "$args -Dapp.DEPLOY=ZIP -Dapp.os=WIN -Dapp.arch=$arch"
Write-Host "Extended args: $args"

jpackage `
  -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App `
  --runtime-image target/image `
  --name "StructuredFileViewer" `
  --dest target/ `
  --type app-image `
  --icon build-resources/windows/logo.ico `
  --java-options "$args"

Write-Host "Copying license files"
robocopy "target\legal" "target\StructuredFileViewer\legal" /E
Copy-Item "target\classes\LICENSE.txt" "target\StructuredFileViewer\legal\LICENSE.txt" -Force

Write-Host "Compress runtime"
Compress-Archive -Path "target\StructuredFileViewer" -DestinationPath "target/StructuredFileViewer-$version-$arch-win.zip" -Force

