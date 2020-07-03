package com.example.ShikeApplication.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.activity.OpenGLESSampleActivity;
import com.example.ShikeApplication.audioProcess.AudioClass;
import com.example.ShikeApplication.ndkdemo.CallbackInterface.JavaNdkListener;
import com.example.ShikeApplication.ndkdemo.CallbackInterface.ThreadErrorListener;
import com.example.ShikeApplication.ndkdemo.ndktool;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.UUID;

public class NativeRenderFragment extends Fragment {

    private static volatile NativeRenderFragment nativeRenderFragment;
    private static final String TAG = "NativeRenderFragment";

    public static NativeRenderFragment getInstance(){
        if(nativeRenderFragment == null){
            synchronized (NativeRenderFragment.class){
                if(nativeRenderFragment == null){
                    nativeRenderFragment = new NativeRenderFragment();
                }
            }
        }
        return nativeRenderFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this.getActivity(),"UUID = " + getUUID(),Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nativerendler,container,false);
        Button ijkPlayerButton = (Button)view.findViewById(R.id.ijkplayer_button);
        Button decodeThreadButton = (Button)view.findViewById(R.id.native_thread_start_button);
        Button setNormalThreadButton = (Button)view.findViewById(R.id.native_thread_set_normal_thread_button);
        Button setMutexThreadButton = (Button)view.findViewById(R.id.native_thread_set_mutex_thread_button);
        Button callBackCMethodsButton = (Button)view.findViewById(R.id.native_thread_call_back_from_c_button);
        Button startAudioThreadButton = (Button)view.findViewById(R.id.audio_service_start);
        Button stopAudioThreadButton = (Button)view.findViewById(R.id.audio_service_stop);
        Button startOpenGLESButton = (Button)view.findViewById(R.id.start_opengles_button);
        ijkPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(this,FileExplorerActivity.class));
            }
        });
        decodeThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File readFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sugar.mp4");
                File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/record_video.h264");
                if (saveFile.exists()) {
                    saveFile.delete();
                }
                if (!readFile.exists()) {
                    Toast.makeText(getActivity() , R.string.native_fragment_no_input_video_file_text ,Toast.LENGTH_LONG).show();
                } else {
                    ndktool.deocdeVideoMethod(readFile.getAbsolutePath(), saveFile.getAbsolutePath());
                }
            }
        });
        setNormalThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ndktool.setNormalThread();
            }
        });
        setMutexThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ndktool.setMutexThread();
            }
        });
        callBackCMethodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ndktool.setCallbackFromC();
            }
        });
        startAudioThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioClass.getInstance().prepareSystemAudioRecod()) {
                    AudioClass.getInstance().startSystemAudioRecod();
                } else {
                    Log.e("AudioClass","Prepare System Audio Recod Fail.");
                }
            }
        });
        stopAudioThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioClass.getInstance().stopSystemAudioRecod();
            }
        });

        JavaNdkListener javaNdkListener = new JavaNdkListener();
        javaNdkListener.setOnErrerListener(new ThreadErrorListener() {
            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "code = " + code + " msg = " + msg);
            }
        });
        startOpenGLESButton.setOnClickListener(v->{
            Log.d(TAG, "start activity opengles.");
            startActivity(new Intent(getActivity(), OpenGLESSampleActivity.class));
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @SuppressLint("MissingPermission")
    public String getUUID(){
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10;//13 位
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                serial = Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "null";
            exception.printStackTrace();
            CrashReport.postCatchedException(exception);
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
