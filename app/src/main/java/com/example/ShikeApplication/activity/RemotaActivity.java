package com.example.ShikeApplication.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.example.ShikeApplication.R;

public class RemotaActivity extends BaseActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remota);
        imageView = (ImageView)findViewById(R.id.tree_image);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        rotate.setFillAfter(true);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                //imageView.setBackground(getResources().getDrawable(R.drawable.tree_ex));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        imageView.startAnimation(rotate);
    }
}
