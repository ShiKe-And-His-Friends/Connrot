package com.example.ShikeApplication.ndkdemo;

public class ndktool {
    static {
        System.loadLibrary("connrot-jni");
    }
    public native static String getSomeDumpTextFromNDK();
    public native static String getNativeCompileVersion();
}
