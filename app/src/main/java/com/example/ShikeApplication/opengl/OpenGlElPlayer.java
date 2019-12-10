package com.example.ShikeApplication.opengl;

public class OpenGlElPlayer {
    private OpenGlElSurfaceView mGLSurfaceView;

    public void setGLSLSurfaceView(OpenGlElSurfaceView iGLSurfaceView) {
        this.mGLSurfaceView = iGLSurfaceView;
    }

    public void onCallRenderYUV(int width,int height,byte[] y,byte[] u,byte[] v){
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setYUVData(width, height, y, u, v);
        }
    }
}
