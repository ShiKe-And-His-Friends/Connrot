package com.example.ShikeApplication.activity;

import android.app.Activity;
import android.os.Bundle;
import com.example.ShikeApplication.R;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
}
