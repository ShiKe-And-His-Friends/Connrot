package com.example.ShikeApplication.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class AssetsUtil {
    public static String initAssets(String fileName, Context mContext) {
        String str = null;
        try {
            if (mContext != null) {
                InputStream inputStream = mContext.getAssets().open(fileName);
                str = getString(inputStream);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return str;
    }
    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();

        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
