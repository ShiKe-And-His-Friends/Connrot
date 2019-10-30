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
    }

    @Override
    protected void onResume() {
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

}
