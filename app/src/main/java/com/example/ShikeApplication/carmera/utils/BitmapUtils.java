package com.example.ShikeApplication.carmera.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.TextUtils;

/**
 * Bitmap工具类，获取Bitmap对象
 */

public class BitmapUtils {

    private BitmapUtils(){}

    /**
     * 根据资源id获取指定大小的Bitmap对象
     * @param context	应用程序上下文
     * @param id		资源id
     * @param height	高度
     * @param width		宽度
     * @return
     */

    public static Bitmap getBitmapFromResource(Context context, int id, int height, int width){
        Options options = new Options();
        options.inJustDecodeBounds = true;//只读取图片，不加载到内存中
        BitmapFactory.decodeResource(context.getResources(), id, options);
        options.inSampleSize = calculateSampleSize(height, width, options);
        options.inJustDecodeBounds = false;//加载到内存中
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id, options);
        return bitmap;
    }

    /**
     * 根据文件路径获取指定大小的Bitmap对象
     * @param path		文件路径
     * @param height	高度
     * @param width		宽度
     * @return
     */

    public static Bitmap getBitmapFromFile(String path, int height, int width){
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("参数为空，请检查你选择的路径:" + path);
        }
        Options options = new Options();
        options.inJustDecodeBounds = true;//只读取图片，不加载到内存中
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateSampleSize(height, width, options);
        options.inJustDecodeBounds = false;//加载到内存中
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 获取指定大小的Bitmap对象
     * @param bitmap	Bitmap对象
     * @param height	高度
     * @param width		宽度
     * @return
     */

    public static Bitmap getThumbnailsBitmap(Bitmap bitmap, int height, int width){
        if (bitmap == null) {
            throw new IllegalArgumentException("图片为空，请检查你的参数");
        }
        return ThumbnailUtils.extractThumbnail(bitmap, width, height);
    }

    /**
     * 将Bitmap对象转换成Drawable对象
     * @param context	应用程序上下文
     * @param bitmap	Bitmap对象
     * @return	返回转换后的Drawable对象
     */

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap){

        if (context == null || bitmap == null) {
            throw new IllegalArgumentException("参数不合法，请检查你的参数");
        }
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        return drawable;

    }

    /**
     * 将Drawable对象转换成Bitmap对象
     * @param drawable	Drawable对象
     * @return	返回转换后的Bitmap对象
     */

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable为空，请检查你的参数");
        }
        Bitmap bitmap =
                Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将Bitmap对象转换为byte[]数组
     * @param bitmap	Bitmap对象
     * @return		返回转换后的数组
     */

    public static byte[] bitmapToByte(Bitmap bitmap){
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap为空，请检查你的参数");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();

    }



    /**
     * 计算所需图片的缩放比例
     * @param height	高度
     * @param width		宽度
     * @param options	options选项
     * @return
     */

    private static int calculateSampleSize(int height, int width, Options options){
        int realHeight = options.outHeight;
        int realWidth = options.outWidth;
        int heigthScale = realHeight / height;
        int widthScale = realWidth / width;
        if(widthScale > heigthScale){
            return widthScale;
        }else{
            return heigthScale;
        }

    }

    public static Bitmap rotateBimap(float degree,Bitmap srcBitmap) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.getWidth(),srcBitmap.getHeight()
                ,matrix,true);
        return bitmap;
    }

    public static void saveBitmap(String path, Bitmap bitmap, String format, int quality) {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        if (format.toUpperCase(Locale.getDefault()).equals("PNG") == true) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bitmap.compress(compressFormat, quality, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(String path, ByteBuffer buffer,
                                  int sample_size, String format, int quality) {
        // File file = new File(path);
        // FileOutputStream outputStream = null;
        // outputStream = new FileOutputStream(file);
        try {
            byte[] buff = new byte[buffer.remaining()];
            buffer.get(buff);
            BitmapFactory.Options ontain = new BitmapFactory.Options();
            ontain.inSampleSize = sample_size;
            Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length, ontain);
            saveBitmap(path, bitmap, format, quality);
            // outputStream.write(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap openBitmap(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    public static String getCachePath(Context context) {
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
        return path;
    }

    public static Bitmap zoomImage(Bitmap origImage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = origImage.getWidth();
        float height = origImage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newImage = Bitmap.createBitmap(origImage, 0, 0, (int) width, (int) height, matrix, true);
        return newImage;
    }

    public static String getNewDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH_MM_SS");
        Date date = new Date( System.currentTimeMillis() );
        return  simpleDateFormat.format(date);
    }
}
