package com.example.ShikeApplication;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class coreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "8398b7067f", false);
    }
}
