package com.github.giamgiammi.StructuredFileViewer.utils;

public class AppProperty {
    public static final String IS_MAIN = "app.main";
    public static final String VERSION = "app.version";
    public static final String URL = "app.url";
    public static final String TMP_DIR = "app.tmpdir";
    public static final String LOG_DIR = "app.logdir";

    //The following properties are set in the build script, are not guarantee to be not null
    //(and they are mpt used, for now)
    public static final String DEPLOY = "app.deploy";
    public static final String OS = "app.os";
    public static final String ARCH = "app.arch";
}
