#!/bin/sh

DIR="$(dirname "$0")"
cd "$DIR" || exit 1

set -e

echo "Start project build"
./mvnw clean compile

echo "Start image build"
# Note: AppImage compression seems to work better than jlink one
./mvnw -Djlink.compress=0 javafx:jlink -Pforce-modular

echo "Start packaging"
jpackage -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App \
             --runtime-image target/image \
             --name 'Structured File Viewer' \
             --dest target/ \
             --type app-image \
             --icon src/main/resources/com/github/giamgiammi/StructuredFileViewer/logo.png \
             --java-options "--enable-native-access=javafx.graphics"


echo "Copying license files"
cp -r target/legal "target/Structured File Viewer/"
cp target/classes/LICENSE.txt "target/Structured File Viewer/legal/LICENSE.txt"

if ! [ -f "cache/appimagetool-x86_64.AppImage" ]; then
  echo "Downloading appimagetool"
  wget https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage -O "cache/appimagetool-x86_64.AppImage"
  chmod +x "cache/appimagetool-x86_64.AppImage"
fi

echo "Preparing AppImage package"
mv "target/Structured File Viewer" "target/Structured File Viewer.AppDir"
(cd "target/Structured File Viewer.AppDir" && ln -s "bin/Structured File Viewer" AppRun)
cp src/main/resources/com/github/giamgiammi/StructuredFileViewer/logo.png "target/Structured File Viewer.AppDir/logo.png"
echo "[Desktop Entry]" >> "target/Structured File Viewer.AppDir/app.desktop"
echo "Name=Structured File Viewer" >> "target/Structured File Viewer.AppDir/app.desktop"
echo "Name[it]=Visualizzatore di file strutturato" >> "target/Structured File Viewer.AppDir/app.desktop"
echo "Exec=AppRun" >> "target/Structured File Viewer.AppDir/app.desktop"
echo "Icon=logo" >> "target/Structured File Viewer.AppDir/app.desktop"
echo "Type=Application" >> "target/Structured File Viewer.AppDir/app.desktop"
echo "Categories=Utility;" >> "target/Structured File Viewer.AppDir/app.desktop"

echo "Packaging AppImage"
"cache/appimagetool-x86_64.AppImage" "target/Structured File Viewer.AppDir" "target/Structured File Viewer"
