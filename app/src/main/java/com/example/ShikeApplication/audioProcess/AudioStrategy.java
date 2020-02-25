package com.example.ShikeApplication.audioProcess;

import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;

import java.util.LinkedList;

public class AudioStrategy {
    private static final String TAG = "AudioStrategy";
    private volatile AudioInputThread audioInputThread; //存放音频输入线程类对象的内存指针
    private volatile AudioOutputThread audioOutputThread;
    private LinkedList<byte[]> inputPackageBufferLinkedList;
    private LinkedList<byte[]> outputPackageBufferLinkedList;
    private AudioRecoderBufferThread mAudioRecoderBufferThread;
    private Handler mMainHandler;

    private AudioStrategy() {}

    public AudioStrategy(Handler handler) {
        if (handler != null) {
            mMainHandler = handler;
        }
    }

    private class AudioRecoderBufferThread extends Thread {
        public int iThreadFlag = ThreadEnum.THTREAD_PERPARE.Value();
        @Override
        public void run() {
            this.setPriority(MAX_PRIORITY);
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            int m_i32SamplingRate = 16000;
            byte p_pszi16PcmAudioInputDataFrame[] = new byte[m_i32SamplingRate];
            while (true) {
                if (iThreadFlag == ThreadEnum.THREAD_STOP.Value()) {
                    Log.i( TAG, "音频缓存线程：本线程接收到退出请求，开始准备退出。" );
                    break;
                }
                if (inputPackageBufferLinkedList == null || inputPackageBufferLinkedList.size() <= 0) {
                    continue;
                }
                synchronized (inputPackageBufferLinkedList) {
                    if (inputPackageBufferLinkedList != null) {
                        p_pszi16PcmAudioInputDataFrame = inputPackageBufferLinkedList.getFirst();
                        inputPackageBufferLinkedList.removeFirst();
                    }
                }
                if (outputPackageBufferLinkedList == null) {
                    continue;
                }
                synchronized (outputPackageBufferLinkedList) {
                    if (outputPackageBufferLinkedList != null) {
                        outputPackageBufferLinkedList.addLast(p_pszi16PcmAudioInputDataFrame);
                    }
                }
            }
            Log.i( TAG, "音频缓存线程：本线程已退出。" );
        }
    }

    public void idcAndroidAudioOpen() {
        int m_i32SamplingRate = 16000;
        int m_i32FrameSize = 320;
        int defaultBrandAudioSource = MediaRecorder.AudioSource.MIC;

        if (audioInputThread != null) {
            audioInputThread.TerminalAudioInputProcess();
            audioInputThread = null;
        }

        audioInputThread = new AudioInputThread(mMainHandler);
        audioInputThread.setSamplingRate(m_i32SamplingRate);
        audioInputThread.setFrameSize(m_i32FrameSize);
        audioInputThread.setAudioSource(defaultBrandAudioSource);
        this.inputPackageBufferLinkedList = audioInputThread.outputPackageBufferLinkedList;
        audioInputThread.start();

        if (audioOutputThread != null) {
            audioOutputThread.TerminalAudioOutputProcess();
            audioOutputThread = null;
        }
        audioOutputThread = new AudioOutputThread(mMainHandler);
        audioOutputThread.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        audioOutputThread.setAudioSamplingRate(m_i32SamplingRate);
        audioOutputThread.setAudioChannel(AudioFormat.CHANNEL_CONFIGURATION_MONO);
        audioOutputThread.setAudioFrameSize(m_i32FrameSize);
        this.outputPackageBufferLinkedList = audioOutputThread.outputPackageBufferLinkedList;
        audioOutputThread.start();

        if (mAudioRecoderBufferThread != null) {
            mAudioRecoderBufferThread.iThreadFlag = ThreadEnum.THREAD_STOP.Value();
        }
        mAudioRecoderBufferThread = new AudioRecoderBufferThread();
        mAudioRecoderBufferThread.iThreadFlag = ThreadEnum.THTREAD_START.Value();
        mAudioRecoderBufferThread.start();
    }

    public void idcAndroidAudioClose() {
        if (audioInputThread != null) {
            audioInputThread.TerminalAudioInputProcess();
            audioInputThread = null;
        }
        if (audioOutputThread != null) {
            audioOutputThread.TerminalAudioOutputProcess();
            audioOutputThread = null;
        }
        if (mAudioRecoderBufferThread != null) {
            mAudioRecoderBufferThread.iThreadFlag = ThreadEnum.THREAD_STOP.Value();
            mAudioRecoderBufferThread = null;
        }
        if (inputPackageBufferLinkedList != null) {
            inputPackageBufferLinkedList.clear();
            inputPackageBufferLinkedList = null;
        }
        if (outputPackageBufferLinkedList != null) {
            outputPackageBufferLinkedList.clear();
            outputPackageBufferLinkedList = null;
        }
        Log.e(TAG, "idcAndroidAudioClose.");
    }
}
