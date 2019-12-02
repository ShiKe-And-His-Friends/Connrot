package com.example.ShikeApplication.ndkdemo;

public class ndktool {
    static {
        System.loadLibrary("connrot-jni");
//        System.loadLibrary("swresample");
//        System.loadLibrary("avcodec");
//        System.loadLibrary("avformat");
//        System.loadLibrary("swscale");
//        System.loadLibrary("postproc");
//        System.loadLibrary("avfilter");
//        System.loadLibrary("avdevice");
//        System.loadLibrary("ijkffmpeg");
    }

    public native static String getSomeDumpTextFromNDK();
    public native static String getNativeCompileVersion();
    public native static String getNativeLibraryVersion();
    public native static void setNormalThread();
    public native static void setMutexThread();
    public native static void setCallbackFromC();
}
