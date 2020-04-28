package com.example.ShikeApplication.opengl.threedimensionalrender;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shike on 4/20/2020
 **/
public class ShapeView extends GLSurfaceView {
	private static final String TAG = "ShapeView";
	public static int sScreenWidth;
	public static int sScreenHeight;
	
	private float mPreviousY;
	private float mPreviousX;
	MyRender mMyRender;
	public ShapeView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(GLRecoder.getEGLConfigChooser());
		mMyRender = new MyRender(context);
		setRenderer(mMyRender);
	}
	
	public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
			float dy = y - mPreviousY;
            float dx = x - mPreviousX;
            mMyRender.yAngle += dx;
            mMyRender.xAngle+= dy;
            requestRender();
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }
	
	class MyRender implements Renderer {
		private EGLConfig mEglConfig;
		private Shape mRectangle;
		float yAngle;
    	float xAngle;
		private Context mContext;
    	public MyRender(Context context) {
    		mContext = context;
    	}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			mEglConfig = config;
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1);
			mRectangle = new Shape(mContext);
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
    		int screenWidth = 1080;
    		int screenHeight = 1920;
			GLRecoder.init(screenWidth, screenHeight,mEglConfig);
			try {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/glOutput.mp4";
				File file = new File(path);
				Log.i(TAG, "file path is " + path + " ,exits is " + file.exists() + " ,width=" + width + " ,height=" + height + " ,screenWidth=" + screenWidth + " ,screenHeight=" + screenHeight);
				if (file.exists()) {
					file.delete();
				}
				GLRecoder.startEncoder(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			sScreenWidth = screenWidth;
			sScreenHeight = screenHeight;
			GLES20.glViewport(0, 0, screenWidth, screenHeight);
			Matrix.perspectiveM(mProjectionMatrix, 0, 45, (float)screenWidth/screenHeight, 2, 5);
			Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3,  0, 0, 0, 0, 1, 0);
		}
	    private final float[] mProjectionMatrix = new float[16];
	    private final float[] mViewMatrix = new float[16];
	    private final float[] mModuleMatrix = new float[16];
	    private final float[] mViewProjectionMatrix = new float[16];
	    private final float[] mMVPMatrix = new float[16];
	    
		@Override
		public void onDrawFrame(GL10 gl) {
			Matrix.setIdentityM(mModuleMatrix, 0);
			Matrix.rotateM(mModuleMatrix, 0, xAngle, 1, 0, 0);
			Matrix.rotateM(mModuleMatrix, 0, yAngle, 0, 1, 0);
			Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModuleMatrix, 0);
			mRectangle.draw(mMVPMatrix, mModuleMatrix);
		}
	}
}