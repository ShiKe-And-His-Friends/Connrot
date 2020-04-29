package com.example.ShikeApplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.ShikeApplication.R
import com.example.ShikeApplication.ndkdemo.ndktool

class VideoPlayActivity : BaseActivity() {

    private var TAG = "VideoPlayActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        Log.i(TAG, "onCreate begin.")
        /*var bundle = this.intent.extras
        var videourl = bundle?.get("videoUrl")
        ndktool.NPlayerOpenUrl(videourl as String?)*/
        Log.i(TAG, "onCreate success.")
    }

    override fun onStop() {
        Log.i(TAG, "onStop.")
        super.onStop()
    }
}
