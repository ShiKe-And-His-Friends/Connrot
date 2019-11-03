package com.example.ShikeApplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.example.ShikeApplication.R;

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation alphaAnimation = new AlphaAnimation(1,0);
        alphaAnimation.setDuration(2000);
        Interpolator overshootInterpolator = new OvershootInterpolator();
        alphaAnimation.setInterpolator(overshootInterpolator);
        alphaAnimation.start();
        startActivity(new Intent(this, MainActivity.class));
    }
}
