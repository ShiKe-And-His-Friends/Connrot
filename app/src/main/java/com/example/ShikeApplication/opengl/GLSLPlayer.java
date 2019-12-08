package com.example.ShikeApplication.opengl;

import android.util.Log;

public class GLSLPlayer {
    private GLSLSurfaceView mGLSurfaceView;

    public void setGLSLSurfaceView(GLSLSurfaceView iGLSurfaceView) {
        this.mGLSurfaceView = iGLSurfaceView;
    }

    public void onCallRenderYUV(int width,int height,byte[] y,byte[] u,byte[] v){
        Log.d("","获取到视频的数据");
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setYUVData(width, height, y, u, v);
        }
    }
}
