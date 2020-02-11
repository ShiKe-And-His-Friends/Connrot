package com.example.ShikeApplication.NPaly

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.example.ShikeApplication.ndkdemo.ndktool
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class XPlaySurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs),SurfaceHolder.Callback ,GLSurfaceView.Renderer, View.OnClickListener {

    val TAG = "XPlaySurfaceView"

    init {
        setRenderer(this)
        setOnClickListener(this)
    }

    override fun onDrawFrame(p0: GL10?) {}

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {}

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {}

    override fun onClick(p0: View?) {
        ndktool.NPlayerPauseOrPlay()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder?) {}

    override fun surfaceCreated(holder: SurfaceHolder) {
        ndktool.NPlayerInitView(holder.surface)
        Log.d(TAG , "XPlaySurfaceView surfaceCreated.")
    }
}