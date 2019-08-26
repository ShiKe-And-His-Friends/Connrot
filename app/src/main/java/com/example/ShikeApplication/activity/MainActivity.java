package com.example.ShikeApplication.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.fragment.HomeFragment;

public class MainActivity extends BaseActivity {

    FrameLayout mFrameLayout = null ;
    HomeFragment homeFragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById( R.id.frame_layout_main ) ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFrameLayout != null ) {
            FragmentManager fragmentManager = getSupportFragmentManager() ;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;
            fragmentTransaction.replace( R.id.frame_layout_main , homeFragment ) ;
            fragmentTransaction.commit() ;
        }
    }
}
