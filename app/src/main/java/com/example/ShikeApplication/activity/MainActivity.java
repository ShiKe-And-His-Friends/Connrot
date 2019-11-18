package com.example.ShikeApplication.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.fragment.HomeFragment;
import com.example.ShikeApplication.fragment.MediaFragment;
import com.example.ShikeApplication.fragment.NativeRenderFragment;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Arrays;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {

    @BindView(R.id.frame_layout_main)
    FrameLayout mFrameLayout;
    @BindView(R.id.tab1)
    ImageButton tab1ImageButton;
    @BindView(R.id.tab2)
    ImageButton tab2ImageButton;
    @BindView(R.id.tab3)
    ImageButton tab3ImageButton;
    Unbinder unbinder;

    private final static String TAG = "MainActivity";
    private HomeFragment homeFragment;
    private NativeRenderFragment nativeRenderFragment;
    private MediaFragment mediaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder =  ButterKnife.bind(this);
        CrashReport.setUserSceneTag(this,102 );

        try {
            new NullPointerException();
        } catch (Exception e){
            e.printStackTrace();
            CrashReport.setUserId(this,"shikeshike");
            CrashReport.putUserData(this,"shike1","network");
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    protected void onResume() {
        getSupportColorFormat();
        showFragment(R.id.tab1);
        // apply permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"Granted");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 23333);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        CrashReport.testJav\
//
//        aCrash();
    }

    @Override
    protected void onDestroy() {
        homeFragment = null;
        nativeRenderFragment = null;
        mediaFragment = null;
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick({R.id.tab1, R.id.tab2, R.id.tab3 })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab1 :
                showFragment(R.id.tab1);
                break;
            case R.id.tab2 :
                showFragment(R.id.tab2);
                break;
            case R.id.tab3 :
                showFragment(R.id.tab3);
                break;
        }
    }

    private void showFragment (int index) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getFragmentManager().beginTransaction() ;
        hideFragment(fragmentTransaction);
        switch (index) {
            case R.id.tab1 :
                if (homeFragment == null) {
                    homeFragment = HomeFragment.getInstance();
                    fragmentTransaction.add( R.id.frame_layout_main , homeFragment ) ;
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                break;
            case R.id.tab2 :
                hideFragment(fragmentTransaction);
                if (nativeRenderFragment == null) {
                    nativeRenderFragment = NativeRenderFragment.getInstance();
                    fragmentTransaction.add(R.id.frame_layout_main , nativeRenderFragment);
                } else {
                    fragmentTransaction.show(nativeRenderFragment);

                }
                break;
            case R.id.tab3 :
                hideFragment(fragmentTransaction);
                if (mediaFragment == null){
                    mediaFragment = MediaFragment.getInstance();
                    fragmentTransaction.add(R.id.frame_layout_main , mediaFragment);
                } else {
                    fragmentTransaction.show(mediaFragment);

                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction){
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (nativeRenderFragment != null) {
            transaction.hide(nativeRenderFragment);
        }
        if (mediaFragment != null) {
            transaction.hide(mediaFragment);
        }
    }

    private int getSupportColorFormat() {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (!types[j].equals("video/avc")) {
                        continue;
                }
                MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(types[j]);
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
//                if (videoCapabilities != null && videoCapabilities.getBitrateRange()!=null) {
//                    Range<Integer> bitrateRange = videoCapabilities.getBitrateRange();
//                    Log.e(TAG,"BitrateRange = "+bitrateRange.getLower()+" <-> "+bitrateRange.getUpper());
//                }
                if (videoCapabilities != null && videoCapabilities.getSupportedHeights()!=null) {
                    Range<Integer> heightRange = videoCapabilities.getSupportedHeights();
                    Log.e(TAG,"heightRange = "+heightRange.getLower()+" <-> "+heightRange.getUpper());
                }
                if (videoCapabilities != null && videoCapabilities.getSupportedWidths()!=null) {
                    Range<Integer> widthRange = videoCapabilities.getSupportedWidths();
                    Log.e(TAG,"widthRange = "+widthRange.getLower()+" <-> "+widthRange.getUpper());
                }
            }
        }
        return  0;
    }
//        Log.e(TAG,"=========== start ==========");
//        int numCodecs = MediaCodecList.getCodecCount();
//        MediaCodecInfo codecInfo = null;
//
//        for (int i = 0; i < numCodecs; i++) {
//            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
//            if (!info.isEncoder()) {
//                MediaCodecInfo.VideoCapabilities videoCapabilities = info.getVideoCapabilities();
//            }
//            Log.e(TAG,"item = " +(i+1) );
//            String[] types = info.getSupportedTypes();
//            boolean found = false;
//            for (int j = 0; j < types.length ; j++) {
//                Log.e(TAG,"\t item = " + types[j] );
//                if (types[j].equals("video/avc")) {
//                    System.out.println("found");
//                    Log.e(TAG,"found item = " +(j+1) );
//                    found = true;
//                }
//            }
////            if (!found)
////                continue;
//            codecInfo = info;
//        }
//        Log.e(TAG, "Found " + codecInfo.getName() + " supporting " + "video/avc");
//        // Find a color profile that the codec supports
//        Log.e(TAG,"=========== end ==========");
//        //return capabilities.colorFormats[i];
//        return 0;

}
