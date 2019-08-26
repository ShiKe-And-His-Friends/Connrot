package com.example.ShikeApplication.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.fragment.HomeFragment;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
