package com.example.ShikeApplication.carmera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import java.util.List;

public class CameraPreview implements Camera.PreviewCallback {
    private static final String TAG = "CameraPreview";
    public static int PREVIEW_DEFAULT_HEIGHT = 960;
    public static int PREVIEW_DEFAULT_WIDTH = 1280;
    private static final int MY_TEXTURE_ID = 123321;

    private int previewHeight;
    private int previewWidth;
    private int mBufferNum = 2;
    private volatile int mBufferFlop = 0;
    private int cameraStatus = CameraStatus.CAMERA_STATUS_NOCONFIG;
    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private byte mBuffer[][];

    private Context mContext;
    private IFramePreviewInterface iFramePreviewInterface;

    private CameraPreview(){}

    public CameraPreview(Context context){
        if (context == null) {
            return;
        }
        this.mContext = context;
        mSurfaceTexture = new SurfaceTexture(MY_TEXTURE_ID);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    public void setPreviewFrameHandler(IFramePreviewInterface iFramehandler) {
        iFramePreviewInterface = iFramehandler;
    }

    private void cameraConfig() {
        Log.e(TAG,"cameraConfig");
        if (mCamera == null) {
            return;
        }
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        List<Camera.Size> mCameraSize = mCameraParameters.getSupportedPreviewSizes();
        previewWidth = mCameraSize.get(0).width;
        previewHeight = mCameraSize.get(0).height;
        for (int i = 1 ; i < mCameraSize.size() ; i++) {
            int sizeSimilarity = Math.abs(mCameraSize.get(i).height - PREVIEW_DEFAULT_HEIGHT)
                    + Math.abs(mCameraSize.get(i).width - PREVIEW_DEFAULT_WIDTH);
            if (sizeSimilarity <= 0) {
                previewHeight = mCameraSize.get(i).height;
                previewWidth = mCameraSize.get(i).width;
                break;
            } else if (sizeSimilarity < ( Math.abs(mCameraSize.get(i).height - PREVIEW_DEFAULT_HEIGHT)
                    + Math.abs(mCameraSize.get(i).width - PREVIEW_DEFAULT_WIDTH ) )){
                previewHeight = mCameraSize.get(i).height;
                previewWidth = mCameraSize.get(i).width;
            }
        }
        mCameraParameters.setPictureSize(previewWidth,previewHeight);
        mCamera.setParameters(mCameraParameters);

        int mBufferSize = previewHeight * previewWidth;
        mBufferSize = mBufferSize * ImageFormat.getBitsPerPixel(mCameraParameters.getPreviewFormat()) / 8 ;


        byte[] buffer1 = new byte[mBufferSize];
        byte[] buffer2 = new byte[mBufferSize];
        mCamera.addCallbackBuffer(buffer2);

        mBuffer = new byte[mBufferNum][];
        mBuffer[0] = buffer1;
        mBuffer[1] = buffer2;

        mBufferFlop = 0;

        mCamera.setPreviewCallbackWithBuffer(this);
    }

    public boolean cameraOpen() {
        Log.e(TAG,"cameraOpen");
        if (mCamera != null) {
            return true;
        }
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
        if (mCamera == null) {
            cameraStatus = CameraStatus.CAMERA_STATUS_NOCONFIG;
            return false;
        }
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (Exception e) {
            e.printStackTrace();
            cameraStatus = CameraStatus.CAMERA_STATUS_FAILED;
            return false;
        }
        try {
            cameraConfig();
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            cameraStatus = CameraStatus.CAMERA_STATUS_FAILED;
            return false;
        }
        return true;
    }

    public boolean startCamera() {
        cameraStatus = CameraStatus.CAMERA_STATUS_START;
        try {
            mCamera.startPreview();
        } catch (Exception e) {
            cameraStatus = CameraStatus.CAMERA_STATUS_FAILED;
            return false;
        }
        return true;
    }

    public boolean cameraClose() {
        Log.e(TAG,"cameraClose");
        //Todo recycle unusage resource
        cameraStatus = CameraStatus.CAMERA_STATUS_STOP;
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    public int getCameraStatus() {
        return cameraStatus;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.d(TAG,"time1 =" + System.currentTimeMillis());
        //Log.d(TAG, "onPreviewFrame thread id:" + android.os.Process.myTid());

        Log.e(TAG,"data = " + data.length);

        //from YV20 TO i420
        byte[] i420bytes = new byte[data.length];
        System.arraycopy(data, 0, i420bytes, 0, previewWidth * previewHeight);
        System.arraycopy(data, previewWidth * previewHeight + previewWidth * previewHeight / 4, i420bytes, previewWidth * previewHeight, previewWidth * previewHeight / 4);
        System.arraycopy(data, previewWidth * previewHeight, i420bytes, previewWidth * previewHeight + previewWidth * previewHeight / 4, previewWidth * previewHeight / 4);

        camera.addCallbackBuffer(mBuffer[mBufferFlop]);
        mBufferFlop = (mBufferFlop + 1) % mBufferNum;

        if (iFramePreviewInterface != null) {
            iFramePreviewInterface.handlePreviewFrame(data);
        }
    }
}
