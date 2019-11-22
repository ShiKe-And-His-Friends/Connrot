package com.example.ShikeApplication.mediacodec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ShikeApplication.utils.CameraUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class VideoEncoder {
    private final static String TAG = "VideoEncoder";
    private final static int CONFIGURE_FLAG_ENCODE = MediaCodec.CONFIGURE_FLAG_ENCODE;

    private MediaCodec  mMediaCodec;
    private MediaFormat mMediaFormat;

    private Handler mVideoEncoderHandler;
    private HandlerThread mVideoEncoderHandlerThread = new HandlerThread("VideoEncoder");

    //This video stream format must be I420
    private final static ArrayBlockingQueue<byte []> mInputDatasQueue = new ArrayBlockingQueue<byte []>(8);
    //Cachhe video stream which has been encoded.
    private final static ArrayBlockingQueue<byte []> mOutputDatasQueue = new ArrayBlockingQueue<byte[]>(8);

    private MediaCodec.Callback mCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int id) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(id);
            inputBuffer.clear();
            byte [] dataSources = mInputDatasQueue.poll();
            int length = 0;
            if(dataSources != null) {
                inputBuffer.put(dataSources);
                length = dataSources.length;
            }
            mediaCodec.queueInputBuffer(id,0, length,0,0);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int id, @NonNull MediaCodec.BufferInfo bufferInfo) {
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(id);
            MediaFormat outputFormat = mMediaCodec.getOutputFormat(id);
            if(outputBuffer != null && bufferInfo.size > 0){
                byte [] buffer = new byte[outputBuffer.remaining()];
                outputBuffer.get(buffer);
                boolean result = mOutputDatasQueue.offer(buffer);
                CameraUtil.save(buffer,0,buffer.length, Environment.getExternalStorageDirectory().getAbsolutePath() + "/record_video.h264",true);
                Log.d(TAG, "Offer to queue success.");
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

    public VideoEncoder(int viewwidth, int viewheight){
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaConstant.MimeTypeVideoList[0]);
            MediaConstant.setEncoderSupportVideoWidth(viewwidth);
            MediaConstant.setEncoderSupportVideoHeight(viewheight);
            MediaConstant.getSupportEncodeFormat();
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mMediaCodec = null;
            return;
        }

        mVideoEncoderHandlerThread.start();
        mVideoEncoderHandler = new Handler(mVideoEncoderHandlerThread.getLooper());

        mMediaFormat = MediaFormat.createVideoFormat(MediaConstant.MimeTypeVideoList[0], MediaConstant.getEncoderSupportVideoWidth(), MediaConstant.getEncoderSupportVideoHeight());
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaConstant.VIDEO_CODEC_COLOR_FORMAT);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, MediaConstant.calcBitRate( MediaConstant.getEncoderSupportVideoWidth(), MediaConstant.getEncoderSupportVideoHeight() ));
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, MediaConstant.FrameRate);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL ,MediaConstant.FrameInterval);
    }

    /**
     * Input Video stream which need encode to Queue
     * @param needEncodeData I420 format stream
     */
    public void inputFrameToEncoder(byte [] needEncodeData){
        boolean inputResult = mInputDatasQueue.offer(needEncodeData);
        Log.d(TAG, "-----> inputEncoder queue result = " + inputResult + " queue current size = " + mInputDatasQueue.size());
    }

    /**
     * Get Encoded frame from queue
     * @return a encoded frame; it would be null when the queue is empty.
     */
    public byte [] pollFrameFromEncoder(){
        return mOutputDatasQueue.poll();
    }

    /**
     * start the MediaCodec to encode video data
     */
    public void startEncoder(){
        if(mMediaCodec != null){
            mMediaCodec.setCallback(mCallback, mVideoEncoderHandler);
            mMediaCodec.configure(mMediaFormat, null, null, CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
        }else{
            throw new IllegalArgumentException("startEncoder failed,is the MediaCodec has been init correct?");
        }
    }

    /**
     * stop encode the video data
     */
    public void stopEncoder(){
        if(mMediaCodec != null){
            mMediaCodec.stop();
            mMediaCodec.setCallback(null);
        }
    }

    /**
     * release all resource that used in Encoder
     */
    public void release(){
        if(mMediaCodec != null){
            mInputDatasQueue.clear();
            mOutputDatasQueue.clear();
            mMediaCodec.release();
        }
    }
}
