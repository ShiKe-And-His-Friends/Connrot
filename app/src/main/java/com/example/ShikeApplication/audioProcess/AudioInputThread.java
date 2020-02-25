package com.example.ShikeApplication.audioProcess;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.LinkedList;

public class AudioInputThread extends Thread {
    private static final String TAG = "AudioInputThread";

    private int iThreadFlag = ThreadEnum.THTREAD_PERPARE.Value();

    private int iAudioSamplingRate = 16000;
    private int iAudioFrameSize = 320;
    private int iAudioInputDelay = 100;
    private int iAudioRecordType = MediaRecorder.AudioSource.MIC;
    private AudioRecord idcAudioInputRecord;
    LinkedList<byte []> outputPackageBufferLinkedList = new LinkedList<byte []>();
    private volatile int iPackageNum = 0;
    private Handler mMainHandler;

    private AudioInputThread() {}

    AudioInputThread(Handler handler) {
        if (handler != null) {
            mMainHandler = handler;
        }
    }

    public void run() {
        this.setPriority(MAX_PRIORITY);
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
        int iReadDataLen = -1;
        iThreadFlag =  PrepareAudioResource();
        if (mTimerHandler != null) {
            mTimerHandler.postDelayed(mTimerRunnable, 1000);
        }
        if (mTimerRunnable != null) {
            mTimerRunnable.run();
        }

        while (true) {
            if (iThreadFlag == ThreadEnum.THREAD_STOP.Value()) {
                Log.i(TAG, "音频输入线程：本线程接收到退出请求，开始准备退出。");
                break ;
            }
            byte[] mAudioInputData = new byte[iAudioFrameSize];
            if (idcAudioInputRecord != null) {
                iReadDataLen = idcAudioInputRecord.read(mAudioInputData,0, mAudioInputData.length);
            }
            synchronized (outputPackageBufferLinkedList) {
                if (outputPackageBufferLinkedList != null) {
                    outputPackageBufferLinkedList.add(mAudioInputData);
                    Log.d(TAG,"outputPackageBufferLinkedList size = " + outputPackageBufferLinkedList.size());
                    Log.d(TAG,"idcAudioInputThread audioInput length = " + mAudioInputData.length + ", iPackageNum = " + iPackageNum);
                }
            }
            iPackageNum ++;
        }
        FinalizeAudioResource();
        Log.i(TAG, "音频输入线程：本线程已退出。");
    }

    void TerminalAudioInputProcess () {
        iThreadFlag = ThreadEnum.THREAD_STOP.Value();
    }

    private int PrepareAudioResource () {
        int iPrepareState;
        try {
            if (idcAudioInputRecord != null) {
                Log.i(TAG, "音频处理线程：初始化AudioRecord类,上次未关闭。");
                FinalizeAudioResource();
            }
            idcAudioInputRecord = new AudioRecord(
                    this.iAudioRecordType,
                    this.iAudioSamplingRate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(iAudioSamplingRate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            if (idcAudioInputRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                Log.i(TAG, "音频处理线程：初始化AudioRecord类对象成功。");
                iPrepareState = ThreadEnum.THTREAD_START.Value();
                idcAudioInputRecord.startRecording();
            } else {
                Log.e(TAG, "音频处理线程：初始化AudioRecord类对象失败。");
                iPrepareState = ThreadEnum.THREAD_FAIL.Value();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "音频处理线程：初始化AudioRecord类对象失败。原因：" + e.getMessage());
            iPrepareState = ThreadEnum.THREAD_FAIL.Value();
        }
        return iPrepareState;
    }

    private void FinalizeAudioResource() {
        if (idcAudioInputRecord != null) {
            if (idcAudioInputRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                idcAudioInputRecord.stop();
            }
            idcAudioInputRecord.release();
            idcAudioInputRecord = null;
            Log.i(TAG, "音频处理线程：退出，释放AudioRecord类。");
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

    void setSamplingRate(int i32SamplingRateInThread) {
        this.iAudioSamplingRate = i32SamplingRateInThread;
    }

    void setFrameSize(int i32FrameSizeInThread) {
        this.iAudioFrameSize = i32FrameSizeInThread;
    }

    void setAudioSource(int idcAudioRecordType) {
        this.iAudioRecordType = idcAudioRecordType;
    }

    private Handler mTimerHandler = new Handler(Looper.getMainLooper());
    Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTimerHandler != null) {
                mTimerHandler.postDelayed(this, 1000);
            }
            if (mMainHandler != null) {
                Message msg = Message.obtain();
                msg.what =  HanderBean.INPUT_AUDUIO_PACKAGE_NUMBER_HANDLER_FLAG;
                msg.arg1 = iPackageNum;
                msg.obj = System.currentTimeMillis();
                Log.d(TAG ,"idcAudioInputThread iPackageNum = " + iPackageNum + ",msg.arg1 = " + msg.arg1 + ",msg.obj = " + msg.obj);
                mMainHandler.sendMessage(msg);
                iPackageNum = 0;
            }
        }
    };

}