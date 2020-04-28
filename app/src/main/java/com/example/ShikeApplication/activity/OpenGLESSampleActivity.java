package com.example.ShikeApplication.activity;

import android.os.Bundle;

import com.example.ShikeApplication.opengl.threedimensionalrender.GLRecoder;
import com.example.ShikeApplication.opengl.threedimensionalrender.ShapeView;

/**
 * Created by shike on 4/20/2020
 **/
public class OpenGLESSampleActivity extends BaseActivity {

    private ShapeView mGLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new ShapeView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onDestroy() {
        GLRecoder.stopEncoder();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}
