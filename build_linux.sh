#!/bin/sh

DIR="$(dirname "$0")"
cd "$DIR" || exit 1

set -e

echo "Start project build"
./mvnw clean compile

echo "Start image build"
./mvnw javafx:jlink -Pforce-modular

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
