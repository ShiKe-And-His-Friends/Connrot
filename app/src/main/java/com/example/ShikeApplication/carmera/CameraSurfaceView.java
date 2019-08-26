package com.example.ShikeApplication.carmera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.ShikeApplication.carmera.utils.BitmapUtils;
import com.example.ShikeApplication.carmera.utils.MetricsUtil;

public class CameraSurfaceView extends SurfaceView {

    private static final String TAG = "CameraSurfaceView" ;
    private static final int CAMERA_BEHIND = 0 ;
    private static final int CAMERA_AHEAD = 1 ;
    private Context mContext ;
    private SurfaceHolder mHolder ;
    private String mPhotoPath ;
    private boolean isPreviewing;
    private Camera mCamera = null ;
    private Point mCameraSize = null ;
    private int mCameraType = CAMERA_BEHIND ;


    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context ;
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(mSurefaceCallback);
    }

    public String getPhotoPath(){
        return mPhotoPath ;
    }

    //外部调用方法拍照
    public void doTakePhotoPath(){
        if( isPreviewing && mCamera!= null){
            mCamera.takePicture(mShutterCallback,null,mPictureCallback);
        }
    }

    //快门按下的回调
    private android.hardware.Camera.ShutterCallback mShutterCallback = new android.hardware.Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.e(TAG,"cacaca");
        }
    };

    //保存图片
    private android.hardware.Camera.PictureCallback mPictureCallback  =  new android.hardware.Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            Log.e(TAG,"save picture") ;
            Bitmap raw = null ;
            if ( data != null ) {
                raw = BitmapFactory.decodeByteArray(data,0,data.length);
                mCamera.stopPreview();
            }
            Bitmap bitmap = BitmapUtils.rotateBimap((mCameraType==CAMERA_BEHIND)?90:-90,raw);
            mPhotoPath = String.format("%s%s.jpg",BitmapUtils.getCachePath(mContext),BitmapUtils.getNewDateTime());
            BitmapUtils.saveBitmap(mPhotoPath,bitmap,"jpg",80);
            Log.d(TAG,"bitmap.size= "+(bitmap.getByteCount()/1024) + "K" +",path = "+mPhotoPath );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
            isPreviewing = true ;
        }
    } ;

    private SurfaceHolder.Callback mSurefaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            try {
                //创建预览视图，打开相机
                mCamera = Camera.open(mCameraType);
                mCamera.startPreview();
                mCameraSize = MetricsUtil.getCameraSize(mCamera.getParameters(),MetricsUtil.getSize(mContext));
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mCameraSize.x,mCameraSize.y);
                parameters.setPictureSize(mCameraSize.x,mCameraSize.y);
                parameters.setPictureFormat(ImageFormat.JPEG);
                //auto Focus
                if(mCameraType== CameraSurfaceView.CAMERA_BEHIND){
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                mCamera.setParameters(parameters);
            }catch (Exception e){
                e.printStackTrace();
                mCamera.release();
                mCamera = null ;
            }
            return;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            isPreviewing = true ;
            //TODO 1
            //mCamera.autoFocus(null);
            //连拍setPreviewCallback
            //mCamera.setPreviewCallback(mPreviewCallback);

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //TODO 2
           // mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null ;
        }
    };

}
