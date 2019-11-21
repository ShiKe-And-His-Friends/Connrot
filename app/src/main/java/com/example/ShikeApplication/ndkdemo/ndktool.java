package com.example.ShikeApplication.ndkdemo;

public class ndktool {
    static {
        System.loadLibrary("connrot-jni");
//        System.loadLibrary("ijkffmpeg");
    }
    public native static String getSomeDumpTextFromNDK();
    public native static String getNativeCompileVersion();
    public native static String getNativeLibraryVersion();
    public native static void setNavitThreadDemoOne();
}
