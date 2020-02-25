package com.example.ShikeApplication.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.audioProcess.*;

import java.util.Date;

public class ResoundActivity extends BaseActivity {

    private static final int mRequestCode = 0xabcd;
    private static final String TAG = "MainActivity";
    private boolean showPackageInfo = false;
    private TextView mAudioInputTextView;
    private TextView mAudioOutputTextView;
    private SimpleDateFormat dataformat = new SimpleDateFormat("HH:MM:ss");
    private Date timeStampAudioIn = new Date();
    private Date timeStampAudioOut = new Date();
    private AudioStrategy audioStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mAudioStart = findViewById(R.id.audio_open);
        Button mAudioStop = findViewById(R.id.audio_stop);
        mAudioInputTextView = findViewById(R.id.input_textview);
        mAudioOutputTextView = findViewById(R.id.output_textview);
        mAudioStart.setOnClickListener(tapClick);
        mAudioStop.setOnClickListener(tapClick);
        audioStrategy = new AudioStrategy(mUiHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chechPermission();
    }

    @Override
    protected void onDestroy() {
        if (mUiHandler != null) {
            mUiHandler.removeCallbacksAndMessages(null);
            mUiHandler = null;
        }
        super.onDestroy();
    }

    private View.OnClickListener tapClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.audio_open) {
                audioStrategy.idcAndroidAudioOpen();
                if (mAudioInputTextView != null) {
                    mAudioInputTextView.setText("");
                }
                if (mAudioOutputTextView != null) {
                    mAudioOutputTextView.setText("");
                }
                showPackageInfo = true;
            } else if (v.getId() == R.id.audio_stop) {
                audioStrategy.idcAndroidAudioClose();
                if (mAudioInputTextView != null) {
                    mAudioInputTextView.setText(getResources().getString(R.string.audio_input_initialize_text));
                }
                if (mAudioOutputTextView != null) {
                    mAudioOutputTextView.setText(getResources().getString(R.string.audio_output_initialize_text));
                }
                showPackageInfo = false;
                Log.d(TAG , "click stop.");
            }
        }
    };

    private void chechPermission () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, mRequestCode);//申请权限
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"麦克风权限失败，应用关闭",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public Handler mUiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == HanderBean.OUTPUT_AUDUIO_PACKAGE_NUMBER_HANDLER_FLAG&& (int)msg.arg1 != 0) {
                if (mAudioOutputTextView != null && showPackageInfo) {
                    timeStampAudioOut.setTime((long)msg.obj);
                    mAudioOutputTextView.setText(String.format("%s%s包 (线程: 1  时间 %s)", getResources().getString(R.string.audio_output_text), String.valueOf(msg.arg1), dataformat.format(timeStampAudioOut)));
                }
            } else if (msg.what == HanderBean.INPUT_AUDUIO_PACKAGE_NUMBER_HANDLER_FLAG&& (int)msg.arg1 != 0) {
                if (mAudioInputTextView != null && showPackageInfo) {
                    timeStampAudioIn.setTime((long)msg.obj);
                    mAudioInputTextView.setText(String.format("%s%s包 (线程: 1  时间 %s)", getResources().getString(R.string.audio_input_text), String.valueOf(msg.arg1), dataformat.format(timeStampAudioIn)));
                }
            }
        }
    };
}
