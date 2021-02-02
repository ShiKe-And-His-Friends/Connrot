package com.example.ShikeApplication.NPaly;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.ShikeApplication.ndkdemo.ndktool;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class XPlayJavaSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = "XPlayJavaSurface";

    public XPlayJavaSurfaceView(Context context , AttributeSet attributeSet) {
        super(context ,attributeSet);
        Log.d(TAG , "XPlaySurfaceView context.");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG , "XPlaySurfaceView surfaceCreated.");
        String path = Environment.getExternalStorageDirectory().getPath() + "/1080test.mp4";
        String yuvPath = Environment.getExternalStorageDirectory().getPath() + "/1080test.yuv";
        File file = new File(yuvPath);
        if (file.exists()) {
            file.delete();
        }
        ndktool.NPlayerInitView(holder.getSurface());
        Log.d(TAG , "XPlaySurfaceView init.");
        setRenderer(this);
        Log.d(TAG , "XPlaySurfaceView init success.");

        postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG , "NPlayerOpenUrl start.");
                ndktool.NPlayerOpenUrl(path);
                Log.d(TAG , "NPlayerOpenUrl success.");
            }
        } ,3000);

        Log.d(TAG , "XPlaySurfaceView surfaceCreated success.");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG , "XPlaySurfaceView render onSurfaceCreated.");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG , "XPlaySurfaceView render onSurfaceChanged.");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG , "XPlaySurfaceView render onDrawFrame.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG , "XPlaySurfaceView render surfaceDestroyed.");
        super.surfaceDestroyed(holder);
    }

}
