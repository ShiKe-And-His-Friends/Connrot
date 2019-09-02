package com.example.ShikeApplication.Media;

import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Encoder extends Thread{

    private final String TAG = "Encoder";
    private final String mime_type = MediaFormat.MIMETYPE_VIDEO_AVC;
    private static MediaCodec mEncoder;
    private static VirtualDisplay virtualDisplay;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private EGLRender eglRender;
    private Surface surface;

    private int screen_width;
    private int screen_height;
    private int screen_dpi;
    private int frame_bit = 2000000;//2MB
    private int frame_rate = 10;
    private int frame_internal = 2000;//关键帧间隔 一组加一个关键帧
    private final int TIMEOUT_US = 10000;
    private int video_fps = 10;
    private byte[] sps = null;
    private byte[] pps = null;


    private OnScreenCallBack onScreenCallBack;

    public void setOnScreenCallBack(OnScreenCallBack onScreenCallBack) {
        this.onScreenCallBack = onScreenCallBack;
    }

    public interface OnScreenCallBack {
        void onScreenInfo(byte[] bytes);

        void onCutScreen(Bitmap bitmap);
    }

    public Encoder(int screen_width, int screen_height, int screen_dpi) {
        initScreenInfo(screen_width, screen_height, screen_dpi);
    }

    private void initScreenInfo(int screen_width, int screen_height, int screen_dpi) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        this.screen_dpi = screen_dpi;
    }


    public Encoder setVideoFPS(int fps) {
        video_fps = fps;
        return this;
    }

    /**
     * 设置视屏编码采样率
     * @param bit
     */
    public Encoder setVideoBit(int bit) {
        frame_bit = bit;
        return this;
    }

    @Override
    public void run() {
        super.run();
        try {
            prepareEncoder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startRecordScreen();
        release();
    }


    /**
     * 初始化编码器
     */
    private void prepareEncoder() throws IOException {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(mime_type, screen_width, screen_height);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, frame_bit);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frame_rate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, frame_internal);
        mEncoder = MediaCodec.createEncoderByType(mime_type);
        mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        surface = mEncoder.createInputSurface();
        eglRender = new EGLRender(surface, screen_width, screen_height, video_fps);
        eglRender.setCallBack(new EGLRender.onFrameCallBack() {
            @Override
            public void onUpdate() {
                startEncode();
            }

            @Override
            public void onCutScreen(Bitmap bitmap) {
                onScreenCallBack.onCutScreen(bitmap);
            }
        });
        mEncoder.start();
    }

    /**
     * 开始录屏
     */
    private void startRecordScreen() {
        eglRender.start();
        release();
    }

    private void startEncode() {
        ByteBuffer[] byteBuffers = null;
        int index = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
//        int index = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
        if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            resetOutputFormat();
        } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
//            Log.d("---", "retrieving buffers time out!");
//            try {
//                // wait 10ms
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//            }
        } else if (index >= 0) {
            encodeToVideoTrack(mEncoder.getOutputBuffer(index));
            mEncoder.releaseOutputBuffer(index, false);
        }
    }

    private void encodeToVideoTrack(ByteBuffer encodeData) {
//        ByteBuffer encodeData = mEncoder.getOutputBuffer(index);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodeData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodeData != null) {
            encodeData.position(mBufferInfo.offset);
            encodeData.limit(mBufferInfo.offset + mBufferInfo.size);
//            muxer.writeSampleData(mVideoTrackIndex, encodeData, mBufferInfo);//写入文件
            byte[] bytes;
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                bytes = new byte[mBufferInfo.size + sps.length + pps.length];
                System.arraycopy(sps, 0, bytes, 0, sps.length);
                System.arraycopy(pps, 0, bytes, sps.length, pps.length);
                encodeData.get(bytes, sps.length + pps.length, mBufferInfo.size);
                LogUtils.i(TAG,"BUFFER_FLAG_KEY_FRAME");
            } else {
                bytes = new byte[mBufferInfo.size];
                encodeData.get(bytes, 0, mBufferInfo.size);
            }
            LogUtils.i(TAG, "send:" + mBufferInfo.size + "\tflag:" + mBufferInfo.flags);
        }
    }

    private int mVideoTrackIndex;

    private void resetOutputFormat() {
        MediaFormat newFormat = mEncoder.getOutputFormat();
        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
        getSpsPpsByteBuffer(newFormat);
        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }

    public void forceIFrameRequest() {
        Bundle param = new Bundle();
        param.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
        mEncoder.setParameters(param);
    }


    /**
     * 获取编码SPS和PPS信息
     */
    private void getSpsPpsByteBuffer(MediaFormat newFormat) {
        sps = newFormat.getByteBuffer("csd-0").array();
        pps = newFormat.getByteBuffer("csd-1").array();
//        EventBus.getDefault().post(new EventLogBean("编码器初始化完成"));
    }

    public void stopScreen() {
        if (eglRender != null) {
            eglRender.stop();
        }
    }

    public void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
    }

    public void cutScreen() {
        eglRender.cutScreen();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if ( eglRender != null ) {
            eglRender.stop();
            eglRender = null;
        }
    }
}
