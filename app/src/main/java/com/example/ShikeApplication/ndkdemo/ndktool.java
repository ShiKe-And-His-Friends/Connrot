package com.example.ShikeApplication.ndkdemo;

public class ndktool {
    static {
        System.loadLibrary("connrot-jni");
//        System.loadLibrary("ijkffmpeg");
    }

    public native static String getSomeDumpTextFromNDK();
    public native static String getNativeCompileVersion();
    public native static String getNativeLibraryVersion();
    public native static void setNormalThread();
    public native static void setMutexThread();
    public native static void setCallbackFromC();
    public native static void encoderMP4VideoStart(String videoLoaclPath ,int videoInStreamWidth ,int videoInStreamHeight);
    public native static void encoderMP4VideoEnd();
    public native static void encoderMP4VideoOnPrevireFrame(byte[] RawYuvDate ,int videoInStreamWidth ,int videoInStreamHeight);
}
