package com.example.ShikeApplication.carmera

import android.app.Activity
import android.graphics.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraHelper (activity: Activity, surfaceView: SurfaceView) : Camera.PreviewCallback {
    private var mCamera: Camera? = null                   //Camera对象
    private lateinit var mParameters: Camera.Parameters   //Camera对象的参数
    private var mSurfaceView: SurfaceView = surfaceView   //用于预览的SurfaceView对象
    var mSurfaceHolder: SurfaceHolder                     //SurfaceHolder对象

    private var mActivity: Activity = activity
    private var mCallBack: CallBack? = null   //自定义的回调

    var mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK  //摄像头方向
    var mDisplayOrientation: Int = 0    //预览旋转的角度

    private var picWidth = 2160        //保存图片的宽
    private var picHeight = 3840       //保存图片的高

}