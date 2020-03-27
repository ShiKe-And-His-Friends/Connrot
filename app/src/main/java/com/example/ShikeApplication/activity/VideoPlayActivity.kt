package com.example.ShikeApplication.activity

import android.content.Intent
import android.os.Bundle
import com.example.ShikeApplication.R
import com.example.ShikeApplication.ndkdemo.ndktool

class VideoPlayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        var bundle = this.intent.extras
        var videourl = bundle?.get("videoUrl")
        ndktool.NPlayerOpenUrl(videourl as String?)
    }

}
