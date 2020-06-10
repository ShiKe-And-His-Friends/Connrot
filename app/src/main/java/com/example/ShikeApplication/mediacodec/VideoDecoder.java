package com.example.ShikeApplication.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecoder {
    private final static String TAG = "VideoDecoder";
    private final static int CONFIGURE_FLAG_DECODE = 0;

    private MediaCodec  mMediaCodec;
    private MediaFormat mMediaFormat;
    private Surface     mSurface;

    private VideoEncoder mVideoEncoder;
    private Handler mVideoDecoderHandler;
    private HandlerThread mVideoDecoderHandlerThread = new HandlerThread("VideoDecoder");

    private MediaCodec.Callback mCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int id) {
            Log.d(TAG, "------> onInputBufferAvailable");
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(id);
            inputBuffer.clear();

            byte [] dataSources = null;
            if(mVideoEncoder != null) {
                dataSources = mVideoEncoder.pollFrameFromEncoder();
            }
            int length = 0;
            if(dataSources != null) {
                inputBuffer.put(dataSources);
                length = dataSources.length;
            }
//            String yuvPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera_record_main_stream_ut12.h264";
            mediaCodec.queueInputBuffer(id,0, length,0,0);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int id, @NonNull MediaCodec.BufferInfo bufferInfo) {
            Log.d(TAG, "------> onOutputBufferAvailable");
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(id);
            MediaFormat outputFormat = mMediaCodec.getOutputFormat(id);
            Log.i(TAG,"show decode info fprmat is " + outputFormat.toString());
            if(mMediaFormat == outputFormat && outputBuffer != null && bufferInfo.size > 0){
                byte [] buffer = new byte[outputBuffer.remaining()];
                outputBuffer.get(buffer);
            }
            mMediaCodec.releaseOutputBuffer(id, true);
        }

        @Override
        public void onError(@NonNull MediaCodec mediaCodec, @NonNull MediaCodec.CodecException e) {
            Log.d(TAG, "------> onError");
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
            Log.d(TAG, "------> onOutputFormatChanged");
        }
    };

    public VideoDecoder(Surface surface){
        try {
            mMediaCodec = MediaCodec.createDecoderByType(MediaConstant.MimeTypeVideoList[0]);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mMediaCodec = null;
            return;
        }

        if(surface == null){
            return;
        }
        this.mSurface = surface;

        mVideoDecoderHandlerThread.start();
        mVideoDecoderHandler = new Handler(mVideoDecoderHandlerThread.getLooper());

        mMediaFormat = MediaFormat.createVideoFormat(MediaConstant.MimeTypeVideoList[0], MediaConstant.getEncoderSupportVideoWidth(), MediaConstant.getEncoderSupportVideoHeight());
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaConstant.Decode_VIDEO_CODEC_COLOR_FORMAT);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, MediaConstant.calcBitRate( MediaConstant.getEncoderSupportVideoWidth(), MediaConstant.getEncoderSupportVideoHeight() ));
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, MediaConstant.FrameRate);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL ,MediaConstant.FrameInterval);
    }

    public void setEncoder(VideoEncoder videoEncoder){
        this.mVideoEncoder = videoEncoder;
    }

    public void startDecoder(){
        if(mMediaCodec != null && mSurface != null){
            mMediaCodec.setCallback(mCallback, mVideoDecoderHandler);
            mMediaCodec.configure(mMediaFormat, mSurface,null,CONFIGURE_FLAG_DECODE);
            mMediaCodec.start();
        }else{
            throw new IllegalArgumentException("startDecoder failed, please check the MediaCodec is init correct");
        }
    }

    public void stopDecoder(){
        if(mMediaCodec != null){
            mMediaCodec.stop();
        }
    }

    /**
     * release all resource that used in Encoder
     */
    public void release(){
        if(mMediaCodec != null){
            mMediaCodec.release();
            mMediaCodec = null;
        }
    }

}
