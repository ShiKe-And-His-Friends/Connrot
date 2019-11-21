package com.example.ShikeApplication.utils;

import android.os.Environment;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUtil {
    private static final String TAG = "CameraTwoUtils";

    public static String getNowDateTime() {
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        Date d_date = new Date();
        String s_date = "";
        s_date = s_format.format(d_date);
        return s_date;
    }

    public static String getNowDateTimeFull() {
        String format = "yyyyMMddHHmmssSSS";
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        Date d_date = new Date();
        String s_date = "";
        s_date = s_format.format(d_date);
        return s_date;
    }

    public static String getNowDateTimeFormat() {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        Date d_date = new Date();
        String s_date = "";
        s_date = s_format.format(d_date);
        return s_date;
    }

    public static String getNowTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss");
        return s_format.format(new Date());
    }

    public static String getRecordFilePath(String dir_name, String extend_name) {
        String path = "";
        File recordDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + dir_name + "/");
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        try {
            File recordFile = File.createTempFile(CameraUtil.getNowDateTime(), extend_name, recordDir);
            path = recordFile.getAbsolutePath();
            Log.d(TAG, "dir_name="+dir_name+", extend_name="+extend_name+", path="+path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 将YUV420SP数据顺时针旋转90度
     *
     * @param data        要旋转的数据
     * @param imageWidth  要旋转的图片宽度
     * @param imageHeight 要旋转的图片高度
     * @return 旋转后的数据
     */
    public static byte[] rotateNV21Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    /**
     * 保存数据到本地
     *
     * @param buffer 要保存的数据
     * @param offset 要保存数据的起始位置
     * @param length 要保存数据长度
     * @param path   保存路径
     * @param append 是否追加
     * CameraUtil.save(bytes,0,bytes.length, Environment.getExternalStorageDirectory().getAbsolutePath() + "/record_video.h264",true);
     */
    public static void save(byte[] buffer, int offset, int length, String path, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path, append);
            fos.write(buffer, offset, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            CrashReport.postCatchedException( e ) ;
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.postCatchedException( e ) ;
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException( e ) ;
                }
            }
        }
    }

    public static byte[] Mirror(byte[] src, int w, int h) { //src是原始yuv数组
        int i;
        int index;
        byte temp;
        int a, b;
        //mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = src[a];
                src[a] = src[b];
                src[b] = temp;
                a++;
                b--;
            }
        }
        // mirror u and v
        index = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w;
            b = (i + 1) * w - 2;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;

                temp = src[a + index + 1];
                src[a + index + 1] = src[b + index + 1];
                src[b + index + 1] = temp;
                a+=2;
                b-=2;
            }
        }
        return src;
    }

}
