#!/bin/sh

DIR="$(dirname "$0")"
cd "$DIR" || exit 1

set -e

echo "Start image build"
./mvnw clean javafx:jlink

echo "Start packaging"
jpackage -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App \
             --runtime-image target/image \
             --name 'Structured File Viewer' \
             --dest target/ \
             --type dmg \
             --icon logo.icns \
             --java-options "--enable-native-access=javafx.graphics" \
             --mac-dmg-content target/legal,target/classes/LICENSE.txt

