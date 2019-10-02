package com.example.ShikeApplication.mediaSmaple.imagePaint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

public class ImagePaint {

    public boolean setImageView(ImageView imageView , String bitmapUrl){
        if(imageView == null || bitmapUrl.length() ==0){
            return false;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + bitmapUrl + ".jpg");
        imageView.setImageBitmap(bitmap);
        return true;
    }



}
