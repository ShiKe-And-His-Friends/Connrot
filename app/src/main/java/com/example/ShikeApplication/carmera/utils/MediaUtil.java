package com.example.ShikeApplication.carmera.utils;

import java.io.File;

import android.os.Environment;
import android.util.Log;

import com.example.ShikeApplication.utils.CameraTwoUtils;

public class MediaUtil {
	private final static String TAG = "MediaUtil";

	public static String getRecordFilePath(String dir_name, String extend_name) {
		String path = "";
		File recordDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + dir_name + "/");
		if (!recordDir.exists()) {
			recordDir.mkdirs();
		}
		try {
			File recordFile = File.createTempFile(CameraTwoUtils.getNowDateTime(), extend_name, recordDir);
			path = recordFile.getAbsolutePath();
			Log.d(TAG, "dir_name="+dir_name+", extend_name="+extend_name+", path="+path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}
}
