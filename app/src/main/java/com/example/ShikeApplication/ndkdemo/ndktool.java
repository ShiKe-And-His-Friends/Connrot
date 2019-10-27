package com.example.ShikeApplication.ndkdemo;

import com.example.ShikeApplication.BuildConfig;

public class ndktool {
    static {
        if (("connrotCmake-lib").equals(BuildConfig.So_Library_name)){
            System.loadLibrary("connrotCmake-jni");
        } else if (("connrotCmake-jni").equals(BuildConfig.So_Library_name)) {
            System.loadLibrary("connrot-jni");
        } else {
            throw new RuntimeException();
        }
    }
    public native static String getSomeDumpTextFromNDK();
    public native static String getNativeCompileVersion();
    public native static String getNativeLibraryVersion();
}
