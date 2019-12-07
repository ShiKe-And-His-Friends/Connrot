package com.example.ShikeApplication.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLSLSurfaceView extends GLSurfaceView {

    private GLELRender mGLELRender;
    public GLSLSurfaceView(Context context) {
        this(context,null);
    }

    public GLSLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mGLELRender = new GLELRender(context);
        setRenderer(mGLELRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//requestRender()时不会重复渲染
    }

    public void setYUVData(int width,int height,byte[] y,byte[] u,byte[] v){
        if (mGLELRender != null) {
            mGLELRender.setYUVRenderData(width, height, y, u, v);
            requestRender();
        }
    }
}