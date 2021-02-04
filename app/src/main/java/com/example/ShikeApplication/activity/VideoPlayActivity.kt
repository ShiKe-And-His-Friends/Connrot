package com.example.ShikeApplication.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.example.ShikeApplication.R
import com.example.ShikeApplication.ndkdemo.ndktool

class VideoPlayActivity : BaseActivity() {

    private var TAG = "VideoPlayActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //去掉标题栏
        //全屏，隐藏状态
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        var bundle = this.intent.extras
        var videourl = bundle?.get("videoUrl")
        ndktool.setUrl(videourl as String?)
        setContentView(R.layout.activity_video_play)
        Log.i(TAG, "onCreate begin.")

        Log.i(TAG, "onCreate success.")
    }

    override fun onStop() {
        Log.i(TAG, "onStop.")
        super.onStop()
    }
}
