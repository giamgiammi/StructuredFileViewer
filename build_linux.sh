#!/bin/sh

DIR="$(dirname "$0")"
cd "$DIR" || exit 1

set -e

echo "Start project build"
./mvnw clean compile

echo "Start image build"
# Note: AppImage compression seems to work better than jlink one
./mvnw -Djlink.compress=0 javafx:jlink -Pforce-modular

VERSION="$(cat target/classes/version.txt)"
echo "Detected version: $VERSION"

ARCH="$(uname -m)"
echo "Detected architecture: $ARCH"

ARGS="$(cat target/classes/launcher-args.txt)"
echo "Detected args: $ARGS"

ARGS="$ARGS -Dapp.deploy=APPIMAGE -Dapp.os=LINUX -Dapp.arch=$ARCH"
echo "Extended args: $ARGS"

echo "Start packaging"
jpackage -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App \
             --runtime-image target/image \
             --name 'StructuredFileViewer' \
             --app-version "$VERSION" \
             --dest target/ \
             --type app-image \
             --icon src/main/resources/com/github/giamgiammi/StructuredFileViewer/logo.png \
             --java-options "$ARGS"


echo "Copying license files"
cp -r target/legal "target/StructuredFileViewer/"
cp target/classes/LICENSE.txt "target/StructuredFileViewer/legal/LICENSE.txt"

mkdir -p cache
if ! [ -f "cache/appimagetool-$ARCH.AppImage" ]; then
  APPIMAGE_TOOL_URL="https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-$ARCH.AppImage"
  echo "Downloading appimagetool: $APPIMAGE_TOOL_URL"
  wget "$APPIMAGE_TOOL_URL" -O "cache/appimagetool-$ARCH.AppImage"
  chmod +x "cache/appimagetool-$ARCH.AppImage"
fi

echo "Preparing AppImage package"
mv "target/StructuredFileViewer" "target/StructuredFileViewer.AppDir"
cp build-resources/linux/appimage_run.sh target/StructuredFileViewer.AppDir/AppRun
chmod +x target/StructuredFileViewer.AppDir/AppRun
cp src/main/resources/com/github/giamgiammi/StructuredFileViewer/logo.png "target/StructuredFileViewer.AppDir/logo.png"

cp build-resources/linux/appimage.desktop target/StructuredFileViewer.AppDir/StructuredFileViewer.desktop


echo "Packaging AppImage"
"cache/appimagetool-$ARCH.AppImage" "target/StructuredFileViewer.AppDir" "target/StructuredFileViewer-$VERSION-$ARCH.AppImage"
