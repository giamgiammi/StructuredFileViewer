echo Start image build
call mvnw clean javafx:jlink

echo Start packaging

call jpackage -m com.github.giamgiammi.StructuredFileViewer/com.github.giamgiammi.StructuredFileViewer.App --runtime-image target/image --name "Structured File Viewer" --dest target/ --type app-image --icon logo.ico --java-options "--enable-native-access=javafx.graphics"