package com.example.ShikeApplication.NPaly

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.example.ShikeApplication.R
import com.example.ShikeApplication.ndkdemo.ndktool
import java.io.File
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class XPlaySurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs),SurfaceHolder.Callback ,GLSurfaceView.Renderer, View.OnClickListener {

    val TAG = "XPlaySurfaceView"

    init {
        Log.d(TAG , "XPlaySurfaceView init.")
        setRenderer(this)
        setOnClickListener(this)
    }

    override fun onDrawFrame(p0: GL10?) {
        Log.d(TAG , "XPlaySurfaceView onDrawFrame.")
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        Log.d(TAG , "XPlaySurfaceView onSurfaceChanged.")
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.d(TAG , "XPlaySurfaceView onSurfaceCreated.")
    }

    override fun onClick(p0: View?) {
        Log.d(TAG , "XPlaySurfaceView onClick.")
        ndktool.NPlayerPauseOrPlay()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        Log.d(TAG , "XPlaySurfaceView surfaceDestory.")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        Log.d(TAG , "XPlaySurfaceView surfaceDestory.")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG , "XPlaySurfaceView surfaceCreated.")
        var path = Environment.getExternalStorageDirectory().path + "/1080test.mp4"
        var yuvPath = Environment.getExternalStorageDirectory().path + "/1080test.yuv"
        if (File(yuvPath).exists()) {
            File(yuvPath).delete();
        }
        ndktool.NPlayerInitView(holder.surface)
        ndktool.NPlayerOpenUrl(path)
        //ndktool.NPlayerOpenUrl(this.resources.getString(R.string.fragment_native_player_remota_src))
        Log.d(TAG , "XPlaySurfaceView surfaceCreated success.")
    }
}