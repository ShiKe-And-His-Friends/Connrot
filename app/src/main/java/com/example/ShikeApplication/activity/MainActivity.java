package com.example.ShikeApplication.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
    Unbinder unbinder;

    private final static String TAG = "MainActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HomeFragment homeFragment;
    NativeRenderFragment nativeRenderFragment;
    MediaFragment mediaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder =  ButterKnife.bind(this);

        if (mFrameLayout != null ) {
            if (fragmentManager == null) {
                fragmentManager = getFragmentManager()  ;
            }
            if (fragmentTransaction == null) {
                fragmentTransaction = fragmentManager.beginTransaction() ;
            }
            fragmentTransaction.replace( R.id.frame_layout_main , homeFragment ) ;
            fragmentTransaction.commit() ;
        }

    }

    @Override
    protected void onResume() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        if (nativeRenderFragment == null) {
            nativeRenderFragment = new NativeRenderFragment();
        }
        if (mediaFragment == null){
            mediaFragment = new MediaFragment();
        }
        // apply permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"Granted");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 23333);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        fragmentManager = null;
        homeFragment = null;
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @OnClick({R.id.tab1 , R.id.tab2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab1 :
                fragmentTransaction.replace( R.id.frame_layout_main , homeFragment ) ;
                fragmentTransaction.commit() ;
                break;
            case R.id.tab2 :
                fragmentTransaction.replace( R.id.frame_layout_main , nativeRenderFragment ) ;
                fragmentTransaction.commit() ;
                break;
            case R.id.tab3 :
                fragmentTransaction.replace( R.id.frame_layout_main , mediaFragment ) ;
                fragmentTransaction.commit() ;
                break;
        }
    }


}
