package com.example.ShikeApplication.audioProcess;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.LinkedList;

public class AudioOutputThread extends Thread {
    private static final String TAG = "AudioOutputThread";

    private int iThreadFlag = ThreadEnum.THTREAD_PERPARE.Value();

    private int iAudioStreamType = AudioManager.STREAM_VOICE_CALL;
    private int iAudioSamplingRate = 16000;
    private int iAudioChannel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int iAudioFrameSize = 320;
    private AudioTrack idcAudioOutputTrack;
    LinkedList<byte []> outputPackageBufferLinkedList = new LinkedList<byte []>();
    private volatile int iPackageNum = 0;
    private Handler mMainHandler;

    private AudioOutputThread() {}
    AudioOutputThread(Handler context) {
        if (context != null) {
            mMainHandler = context;
        }
    }

    public void run() {
        this.setPriority( MAX_PRIORITY );
        Process.setThreadPriority( Process.THREAD_PRIORITY_URGENT_AUDIO );

        int iRet = -1;
        iThreadFlag = PrepareAudioResource();
        if (mTimerHandler != null) {
            mTimerHandler.postDelayed(mTimerRunnable, 1000);
        }
        if (mTimerRunnable != null) {
            mTimerRunnable.run();
        }
        byte[] mAudioOutputData = new byte[iAudioFrameSize];;

        while( true ) {
            if (iThreadFlag == ThreadEnum.THREAD_STOP.Value()) {
                Log.i( TAG, "音频输出线程：本线程接收到退出请求，开始准备退出。" );
                break;
            }
            /*
            if (this.iOutputCastAudio) {
                iRet = fvpsipjni.FVPhoneAudioCastPcmGet(mAudioOutputData, mAudioOutputData.length );
            }
            */
            if (outputPackageBufferLinkedList != null && outputPackageBufferLinkedList.size() > 0) {
                synchronized (outputPackageBufferLinkedList) {
                    mAudioOutputData = outputPackageBufferLinkedList.getFirst();
                    outputPackageBufferLinkedList.removeFirst();
                    Log.d(TAG,"outputPackageBufferLinkedList size = " + outputPackageBufferLinkedList.size());
                }
            }
            if (idcAudioOutputTrack != null) {
                idcAudioOutputTrack.write( mAudioOutputData, 0, mAudioOutputData.length);
            }
            iPackageNum ++;
            Log.d(TAG ,"idcAudioOutputThread audioOutput length = " + mAudioOutputData.length + ", iPackageNum = " + iPackageNum);
        }
        FinalizeAudioResource();
        Log.i( TAG, "音频输出线程：本线程已退出。" );
    }

    void TerminalAudioOutputProcess () {
        iThreadFlag = ThreadEnum.THREAD_STOP.Value();
    }

    private int PrepareAudioResource () {
        int iPrepareState;
        try {
            idcAudioOutputTrack = new AudioTrack(
                iAudioStreamType,
                iAudioSamplingRate,
                iAudioChannel,
                AudioFormat.ENCODING_PCM_16BIT,
                iAudioFrameSize,
                AudioTrack.MODE_STREAM);
            if (idcAudioOutputTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                Log.i(TAG, "音频处理线程：初始化AudioTrack类对象成功。");
                iPrepareState = ThreadEnum.THTREAD_START.Value();
                idcAudioOutputTrack.play();
            } else {
                Log.e(TAG, "音频处理线程：初始化AudioTrack类对象失败。");
                iPrepareState = ThreadEnum.THREAD_FAIL.Value();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "音频处理线程：初始化AudioTrack类对象失败。原因：" + e.getMessage());
            iPrepareState = ThreadEnum.THREAD_FAIL.Value();
        }
        return iPrepareState;
    }

    private void FinalizeAudioResource () {
        if (idcAudioOutputTrack != null) {
            if (idcAudioOutputTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                idcAudioOutputTrack.stop();
            }
            idcAudioOutputTrack.release();
            idcAudioOutputTrack = null;
        }
        if (mTimerHandler != null) {
            if (mTimerRunnable != null) {
                mTimerHandler.removeCallbacksAndMessages(mTimerRunnable);
            } else {
                mTimerHandler.removeCallbacksAndMessages(null);
            }
            mTimerRunnable = null;
            mTimerHandler = null;
        }
        if (outputPackageBufferLinkedList != null) {
            outputPackageBufferLinkedList.clear();
            outputPackageBufferLinkedList = null;
        }
        mMainHandler = null;
    }

    void setAudioStreamType(int iAudioStreamType) {
        this.iAudioStreamType = iAudioStreamType;
    }

    void setAudioSamplingRate(int iAudioSamplingRate) {
        this.iAudioSamplingRate = iAudioSamplingRate;
    }

    void setAudioChannel(int iAudioChannel) {
        this.iAudioChannel = iAudioChannel;
    }

    void setAudioFrameSize(int iAudioFrameSize) {
        this.iAudioFrameSize = iAudioFrameSize;
    }

    private Handler mTimerHandler = new Handler(Looper.myLooper());
    Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTimerHandler != null) {
                mTimerHandler.postDelayed(this, 1000);
            }
            if (mMainHandler != null) {
                Message msg = Message.obtain();
                msg.what =  HanderBean.OUTPUT_AUDUIO_PACKAGE_NUMBER_HANDLER_FLAG;
                msg.arg1 = iPackageNum;
                msg.obj = System.currentTimeMillis();
                mMainHandler.sendMessage(msg);
                Log.d(TAG ,"idcAudioOutThread iPackageNum = " + iPackageNum + ",msg.arg1 = " + msg.arg1 + ",msg.obj = " + msg.obj);
                iPackageNum = 0;
            }
        }
    };

}