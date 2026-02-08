#!/bin/sh

DIR="$(dirname "$0")"
cd "$DIR" || exit 1

set -e

echo "Start project build"
./mvnw clean compile

echo "Start image build"
./mvnw javafx:jlink -Pforce-modular

VERSION="$(cat target/classes/version.txt)"
echo "Detected version: $VERSION"

ARCH="$(uname -m)"
echo "Detected architecture: $ARCH"

echo "Start packaging"
jpackage -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App \
             --runtime-image target/image \
             --name "StructuredFileViewer" \
             --app-version "$VERSION" \
             --dest target/ \
             --type dmg \
             --icon logo.icns \
             --java-options "--enable-native-access=javafx.graphics" \
             --mac-dmg-content target/legal,target/classes/LICENSE.txt
mv "target/StructuredFileViewer-$VERSION.dmg" "target/StructuredFileViewer-$VERSION-$ARCH.dmg"

