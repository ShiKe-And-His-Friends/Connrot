package com.example.ShikeApplication.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ShikeApplication.carmera.CameraPreview;
import com.example.ShikeApplication.carmera.IFramePreviewInterface;
import com.example.ShikeApplication.mediacodec.VideoDecoder;
import com.example.ShikeApplication.mediacodec.VideoEncoder;
import com.example.ShikeApplication.R;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MediaFragment extends Fragment implements IFramePreviewInterface {

    private final static String TAG = "MediaFragment";

    @BindView(R.id.camera)
    TextureView mCameraTexture;
    @BindView(R.id.decode)
    TextureView mDecodeTexture;
    Unbinder unbinder;

    private static volatile MediaFragment mediaFragment;
    private VideoDecoder mVideoDecoder;
    private VideoEncoder mVideoEncoder;
    private CameraPreview mCameraPreview;



    private MediaFragment(){}

    public static MediaFragment getInstance(){
        if(mediaFragment == null){
            synchronized (MediaFragment.class){
                if(mediaFragment == null){
                    mediaFragment = new MediaFragment();
                }
            }
        }
        return mediaFragment;
    }

    private TextureView.SurfaceTextureListener mCameraTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "onSurfaceTextureAvailable");
            mCameraPreview.setSurfaceTexture(surfaceTexture);
            mCameraPreview.cameraOpen();
            mCameraPreview.startCamera();
            mVideoEncoder = new VideoEncoder(mCameraPreview.getPreviewWidth(), mCameraPreview.getPreviewHeight());
            mVideoEncoder.startEncoder();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.e(TAG, "onSurfaceTextureDestroyed");
            if(mVideoEncoder != null){
                mVideoEncoder.release();
            }
            mCameraPreview.cameraClose();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            Log.e(TAG, "onSurfaceTextureUpdated");
        }
    };

    private TextureView.SurfaceTextureListener mDecodeTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "onSurfaceTextureAvailable");
            Log.e(TAG, "----------" + i + " ," + i1);
            mVideoDecoder = new VideoDecoder(new Surface(surfaceTexture));
            mVideoDecoder.setEncoder(mVideoEncoder);
            mVideoDecoder.startDecoder();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.e(TAG, "onSurfaceTextureDestroyed");
            mVideoDecoder.stopDecoder();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            Log.e(TAG, "onSurfaceTextureUpdated");
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media,container,false);
        unbinder = ButterKnife.bind(this,view);
        mCameraPreview = new CameraPreview(this.getContext());
        mCameraPreview.setPreviewFrameHandler(this);
        initView();
        return view;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initView(){
        mCameraTexture.setSurfaceTextureListener(mCameraTextureListener);
        boolean canChangeSurfaceTexture = false;
        WindowManager mWindowManager = (WindowManager)getInstance().getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int machineWidth = displayMetrics.widthPixels;
            int machineHeight = displayMetrics.heightPixels;
            if (machineWidth > 0 && machineHeight > 0) {
                canChangeSurfaceTexture = true;
            }
            Log.e(TAG ,"Measure background image , machineWidth " + machineWidth + " machineHeight " + machineHeight);
            if (canChangeSurfaceTexture) {
                ViewGroup.LayoutParams layoutParams = mDecodeTexture.getLayoutParams();
                layoutParams.width = machineWidth;
                layoutParams.height = (int) (machineWidth * (360/640));
                mDecodeTexture.setLayoutParams(layoutParams);
            }
        } else {
            Log.e(TAG ,"Measure background image , mWindowManager is null.");
        }
        mDecodeTexture.setSurfaceTextureListener(mDecodeTextureListener);
    }

    @Override
    public void handlePreviewFrame(byte[] cameraFrameDate) {
        if (mVideoEncoder != null) {
            mVideoEncoder.inputFrameToEncoder(cameraFrameDate);
        }
    }
}
