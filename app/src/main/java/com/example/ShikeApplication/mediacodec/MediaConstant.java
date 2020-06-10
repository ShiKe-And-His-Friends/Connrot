package com.example.ShikeApplication.mediacodec;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;
import android.util.Range;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * {@link VideoEncoder}
 * {@link VideoDecoder}
 */
public class MediaConstant {
	private final static String TAG = "MediaConstant";

	enum VideoFormatColorType {
		yuv420sp,
		yuv420p,
		yuv420flexible,
		invalid
	}

	//audio encoder-decoder parameter
	static final String[] MimeTypeAudioList = new String[] {"audio/mp4a-latm"};
	static final int SampleRate = 44100;//44.1[KHz] is only setting guaranteed to be available on all devices.
	static final int BitRate = 64000;
	static final int SamplePerFrame = 1024;
	static final int FramesPerBuffer = 25;

	//video encoder-decoder parameter
	static final String[] MimeTypeVideoList = new String[] {"video/avc"};
	final static float BitPerPixel = 0.25f;
	final static int FrameRate = 25;
	final static int DequeueTimeout = 10000;
	final static int FrameInterval = 2;

	static int Encode_VIDEO_CODEC_COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;//COLOR_FormatYUV420Planar 19  COLOR_FormatYUV420SemiPlanar 21
	static int Decode_VIDEO_CODEC_COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;//COLOR_FormatYUV420Planar 19  COLOR_FormatYUV420SemiPlanar 21

	public static int calcBitRate(int mWidth,int mHeight) {
		int bitrate = (int)(BitPerPixel * FrameRate * mWidth * mHeight);
		Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
		return bitrate;
	}

	private static int VIDEO_WIDTH = 1080;
	private static int VIDEO_HEIGHT = 1920;
	public static void getSupportEncodeFormat() {
		boolean encodeSystemSizeUnsupport = false;
		int maxSupportHeight = -1;
		int maxSupportWidth = -1;
		try {
			int numCodecs = MediaCodecList.getCodecCount();
			for (int i = 0; i < numCodecs; i++) {
				MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
				if (!codecInfo.isEncoder()) {
					continue;
				}
				String[] types = codecInfo.getSupportedTypes();
				int tempMaxSupportHeight = -1;
				int tempMaxSupportWidth = -1;
				for (int j = 0; j < types.length; j++) {
					if (!types[j].equals("video/avc")) {
						continue;
					}
					MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(types[j]);
					MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
					if (videoCapabilities != null && videoCapabilities.getSupportedHeights()!=null) {
						Range<Integer> heightRange = videoCapabilities.getSupportedHeights();
						tempMaxSupportHeight = heightRange.getUpper();
						Log.e(TAG,"i ="+i+",j = "+j+" ,heightRange c= "+heightRange.getLower()+" <-> "+heightRange.getUpper());
					}
					if (videoCapabilities != null && videoCapabilities.getSupportedWidths()!=null) {
						Range<Integer> widthRange = videoCapabilities.getSupportedWidths();
						tempMaxSupportWidth = widthRange.getUpper();
						Log.e(TAG,"i ="+i+",j = "+j+" ,widthRange = "+widthRange.getLower()+" <-> "+widthRange.getUpper());
					}
					encodeSystemSizeUnsupport = true;
				}
				if (tempMaxSupportWidth >= maxSupportWidth && tempMaxSupportHeight > maxSupportHeight) {
					maxSupportWidth = tempMaxSupportWidth;
					maxSupportHeight = tempMaxSupportHeight;
					Log.e(TAG,"maxSupportWidth ="+maxSupportWidth+",maxSupportHeight = "+maxSupportHeight+" ,tempMaxSupportWidth = "+tempMaxSupportWidth+" <-> tempMaxSupportHeight ="+tempMaxSupportHeight);
				}
			}
		} catch (Exception e) {
			encodeSystemSizeUnsupport = false;
			e.printStackTrace();
			CrashReport.postCatchedException(e);
		}
		if (encodeSystemSizeUnsupport) {
			Log.e(TAG,"maxSupportWidth ="+maxSupportWidth+",maxSupportHeight = "+maxSupportHeight+" ,VIDEO_HEIGHT = "+VIDEO_HEIGHT+" <-> VIDEO_WIDTH ="+VIDEO_WIDTH);
			if (maxSupportWidth <= VIDEO_WIDTH && maxSupportHeight <= VIDEO_HEIGHT) {
				VIDEO_WIDTH = maxSupportWidth;
				VIDEO_HEIGHT = maxSupportHeight;
			}
			Log.e(TAG,"maxSupportWidth ="+maxSupportWidth+",maxSupportHeight = "+maxSupportHeight+" ,VIDEO_HEIGHT = "+VIDEO_HEIGHT+" <-> VIDEO_WIDTH ="+VIDEO_WIDTH);
		}
	}

	public static int getEncoderSupportVideoWidth() {
		return VIDEO_WIDTH;
	}

	public static int getEncoderSupportVideoHeight() {
		return VIDEO_HEIGHT;
	}

	public static void setEncoderSupportVideoWidth(int videoWidth) {
		VIDEO_WIDTH = videoWidth;
	}

	public static void setEncoderSupportVideoHeight(int videoheight) {
		VIDEO_HEIGHT = videoheight;
	}

}
