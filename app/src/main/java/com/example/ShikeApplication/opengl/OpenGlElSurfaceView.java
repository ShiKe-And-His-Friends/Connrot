package com.example.ShikeApplication.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class OpenGlElSurfaceView extends GLSurfaceView {

    private OpenGlElRender mOpenGlElRender;

    public OpenGlElSurfaceView(Context context) {
        this(context,null);
    }

    public OpenGlElSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mOpenGlElRender = new OpenGlElRender(context);
        setRenderer(mOpenGlElRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//requestRender()时不会重复渲染
    }

    public void setYUVData(int width,int height,byte[] y,byte[] u,byte[] v){
        if (mOpenGlElRender != null) {
            mOpenGlElRender.setYUVRenderData(width, height, y, u, v);
            requestRender();
        }
    }

}