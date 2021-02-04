package com.example.ShikeApplication.ndkdemo;

import android.util.Log;

public class ndktool {

    private static final String TAG = "ndktool";
    public static String iStreamUrl = "";

    public native static String getSomeDumpTextFromNDK();
    public native static String getNativeCompileVersion();
    public native static String getNativeLibraryVersion();
    public native static void setNormalThread();
    public native static void setMutexThread();
    public native static void setCallbackFromC();
    public native static void encoderMP4VideoStart(String videoLoaclPath ,int videoInStreamWidth ,int videoInStreamHeight);
    public native static void encoderMP4VideoEnd();
    public native static void encoderMP4VideoOnPrevireFrame(byte[] RawYuvDate ,int videoInStreamWidth ,int videoInStreamHeight);

    public native static void NPlayerInitView(Object surface);
    public native static void NPlayerOpenUrl(String url);
    public native static double NPlayerGetPos();
    public native static void NPlayerPauseOrPlay();
    public native static void NPlayerSeek(double pos);

    public native static void deocdeVideoMethod(String localVideoPath ,String saveVideoPath);

    public static void setUrl (String url) {
        iStreamUrl = url;
    }

    public static void setMyOpenUrl() {
        Log.i(TAG ,"set my open url " + iStreamUrl);
        NPlayerOpenUrl(iStreamUrl);
    }
}
