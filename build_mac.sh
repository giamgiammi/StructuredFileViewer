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

ARGS="$(cat target/classes/launcher-args.txt)"
echo "Detected args: $ARGS"

ARGS="$ARGS -Dapp.deploy=DMG -Dapp.os=MAC -Dapp.arch=$ARCH"
echo "Extended args: $ARGS"

echo "Start packaging"
jpackage -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App \
             --runtime-image target/image \
             --name "StructuredFileViewer" \
             --app-version "$VERSION" \
             --dest target/ \
             --type dmg \
             --icon build-resources/mac/logo.icns \
             --java-options "$ARGS" \
             --mac-dmg-content target/legal,target/classes/LICENSE.txt
mv "target/StructuredFileViewer-$VERSION.dmg" "target/StructuredFileViewer-$VERSION-$ARCH.dmg"

