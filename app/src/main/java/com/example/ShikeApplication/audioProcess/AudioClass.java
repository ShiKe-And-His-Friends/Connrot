package com.example.ShikeApplication.audioProcess;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.ShikeApplication.utils.CameraUtil;

import java.io.File;

public class AudioClass {

    private static final String TAG = "AudioClass";
    private String SAVEURL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/system_audio_record.pcm";
    private static AudioClass audioClass;

    private Context mContext;
    private AudioManager mAudioManager;
    private volatile boolean mThreadRunning = false;
    private int sampleRateInHz = 44100;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mRecordBufSize = -1;
    private byte[] mPcmData = null;
    private AudioRecord mAudioRecord = null;

    private AudioClass() {
    }

    public static AudioClass getInstance() {
        if (audioClass == null) {
            synchronized (AudioClass.class) {
                if (audioClass == null) {
                    audioClass = new AudioClass();
                }
            }
        }
        return audioClass;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public boolean prepareSystemAudioRecod() {
        File file = new File(SAVEURL);
        if (file.isFile() && file.exists()) {
            if (file.delete()) {
                //TODO delete exists file success
            }
        }
        if (mContext != null) {
            mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        }

        mThreadRunning = false;
        mRecordBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, mRecordBufSize);
        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            mAudioRecord = null;
            mRecordBufSize = 0;
            return false;
        }else {
            mPcmData = new byte[mRecordBufSize];
            return true;
        }
    }

    public void startSystemAudioRecod() {
        Log.e("AudioClass","Start System Audio Recod success.");
        mThreadRunning = true;
        mAudioRecord.startRecording();
        mReadDataThread.start();
    }

    public void stopSystemAudioRecod() {
        Log.e("AudioClass","Stop System Audio Recod success.");
        mThreadRunning = false;
        if (mAudioRecord != null){
            mAudioRecord.stop();
            mAudioRecord.release();
        }
        mReadDataThread = null;
    }

    private Thread mReadDataThread = new Thread() {
        @Override
        public void run() {
            int read;
            while (mThreadRunning) {
                read = mAudioRecord.read(mPcmData, 0, mRecordBufSize);
                //如果读取音频数据没有出现错误 ===> read 大于0
                if (read >= AudioRecord.SUCCESS) {
                    //sychonized Callback
                    Log.e(TAG,"\trunning.");
                    CameraUtil.save(mPcmData,0,mRecordBufSize,SAVEURL,true);
                }
            }
        }
    };

    public void getValidSampleRates() {
        for (int rate : new int[]{8000, 11025, 16000, 22050, 44100}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                // buffer size is valid, Sample rate supported
            }
        }
    }

}