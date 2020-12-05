package com.example.ShikeApplication.carmera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import static android.content.Context.CAMERA_SERVICE;

public class DisplayCameraPreview {

    private static final String TAG = "PreviewDisplay";

    private static final int CAMERA_PREVEW_DISPLAY = 48512;
    private boolean open = false;
    private Activity homeActivity;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private TextureView cameraPreviewTextureView;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private HomeViewMode homeViewMode = HomeViewMode.CLOSE;
    private Surface cameraSurface;


    public void setOpenMode(HomeViewMode mode) {
        this.homeViewMode = mode;
        Log.i(TAG, "set mode " + homeViewMode.ordinal());
    }

    public void setTextureView(Activity activity , TextureView textureView) {
        Log.i(TAG, "set texture ,activity not null " + (activity!=null) + " textureview not null " + (textureView!=null));
        if (activity == null) {
            return;
        }
        if (textureView == null) {
            return;
        }
        homeActivity = activity;
        cameraPreviewTextureView = textureView;
        if (cameraPreviewTextureView.isAvailable()) {
            Log.i(TAG, "surface is avaible");
            checkCameraPermission();
        } else {
            Log.i(TAG, "surface set listener");
            cameraPreviewTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    Log.i(TAG, "surface available");
                    checkCameraPermission();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    Log.i(TAG, "surface size change w=" +width + ",height=" + height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    Log.i(TAG, "surface destory");
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
            });
            if (cameraPreviewTextureView != null) {
                cameraPreviewTextureView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkCameraPermission() {
        if (homeActivity != null) {
            if (ContextCompat.checkSelfPermission(homeActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                Log.i(TAG, "apply camera permission ");
                ActivityCompat.requestPermissions(((Activity) homeActivity), new String[] {Manifest.permission.CAMERA}, CAMERA_PREVEW_DISPLAY);
            } else {
                Log.i(TAG, "has camera permission ");
                startLocalPreview();
            }
        } else {
            Log.i(TAG, "check camera permission ,activity not null " + (homeActivity!=null));
        }

    }

    public void permission(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions != null && permissions.length > 0) {
            for (int i = 0 ; i < permissions.length ; i++) {
                String permission = permissions[i];
                if (!TextUtils.isEmpty(permission) && permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResults != null && grantResults.length > i) {
                        int result = grantResults[i];
                        Log.i(TAG ,"result is " + result + " ,index is " + i);
                        startLocalPreview();
                    }
                }
            }
        }
    }

    private void startLocalPreview() {
        if (homeActivity == null) {
            Log.e(TAG, "start camera perview failure , no activity");
            return;
        }
        if (open) {
            Log.e(TAG, "start camera perview failure , already open");
            return;
        }
        backgroundThread = new HandlerThread("CameraPreview");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        String mCameraId = "";

        CameraManager cameraManager = (CameraManager) homeActivity.getSystemService(CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                String[] cameraIdList = cameraManager.getCameraIdList();
                if (cameraIdList != null) {
                    for (int i = 0; i < cameraIdList.length; i++) {
                        String cameraId = cameraIdList[i];
                        if (!TextUtils.isEmpty(cameraId)) {
                            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                            if (characteristics != null) {
                                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                                if (facing == null) {
                                    continue;
                                }
                                Log.i(TAG , "cameraId = " + cameraId + "  homeViewMode = " + homeViewMode.ordinal() + " ,facing = " + facing);

                                if (this.homeViewMode == HomeViewMode.CAMERA_BACK_OPEN) {
                                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                                        continue;
                                    }
                                } else if (this.homeViewMode == HomeViewMode.CAMERA_FRONT_OPEN) {
                                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                                        continue;
                                    }
                                }
                                if (facing != null && facing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
                                    continue;
                                }
                                StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                                if (configurationMap == null) {
                                    continue;
                                }

                            }
                        }
                        mCameraId = cameraId;
                    }
                }
            } catch (CameraAccessException e) {
                Log.e(TAG ,"camera2 find access exception.");
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(mCameraId)) {
                if (ActivityCompat.checkSelfPermission(homeActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    if(!TextUtils.isEmpty(mCameraId) && mStateCallBack != null && backgroundHandler != null) {
                        Log.i(TAG ,"camera2 preview start " + mCameraId);
                        open = true;
                        if (cameraPreviewTextureView != null) {
                            cameraPreviewTextureView.setVisibility(View.VISIBLE);
                        }
                        cameraManager.openCamera(mCameraId, mStateCallBack, backgroundHandler);
                    } else {
                        Log.i(TAG ,"camera2 preview start " + mCameraId + " failure ,callback not null is " + (mStateCallBack!=null) + " handler not null is " + (backgroundHandler!=null));
                    }
                } catch (CameraAccessException e) {
                    Log.e(TAG ,"camera2 open access exception.");
                    e.printStackTrace();
                }
            }
        }

    }

    private final CameraDevice.StateCallback mStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            createCameraPreviewSession();
        }
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.e(TAG ,"camera2 disconnect.");
        }
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG ,"camera2 error.");
        }
    };

    CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback(){

    };

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = cameraPreviewTextureView.getSurfaceTexture();
            cameraSurface = new Surface(surfaceTexture);

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(cameraSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(cameraSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        if (mCameraDevice == null)
                            return;
                        mCameraCaptureSession = session;
                        mCaptureRequest = mPreviewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, mCaptureCallback, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG ,"camera2 preview access exception.");
            e.printStackTrace();
        }
    }

    public void closeCamera(){
        Log.i(TAG ,"close camera");
        if (mCameraCaptureSession != null){
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if ( mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (cameraSurface != null) {
            cameraSurface.release();
            cameraSurface = null;
        }
        if (cameraPreviewTextureView != null) {
            cameraPreviewTextureView.setVisibility(View.INVISIBLE);
        }
        open = false;
        Log.i(TAG ,"close camera success");
    }

    public void stopBackgroundThread(){
        Log.i(TAG ,"stop background thread");
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
        }
        try {
            if (backgroundThread != null) {
                backgroundThread.join();
            }
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG ,"stop background thread success");
    }

}
