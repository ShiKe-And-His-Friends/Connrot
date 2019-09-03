package com.example.ShikeApplication.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.fragment.HomeFragment;
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

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HomeFragment homeFragment;
    NativeRenderFragment nativeRenderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder =  ButterKnife.bind(this);

        if (mFrameLayout != null ) {
            if (fragmentManager == null) {
                fragmentManager =  ;
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
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        fragmentManager = null;
        homeFragment = null;
        unbinder.unbind();
        super.onDestroy();
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
        }
    }


}
