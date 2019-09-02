package com.example.ShikeApplication.Media;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class AuidoRawChannelBuffer {

    private static final String TAG = "AuidoRawChannelBuffer";

    //raw audio buffer process
    short[] getSamplesForChannel(MediaCodec codec , int bufferId ,int channellx){
        ByteBuffer outputBuffer = null ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            outputBuffer = codec.getOutputBuffer(bufferId);
        }else {
            Log.e(TAG,"below Android LOLLIPOP , get mediacode channel error.");
            return null;
        }
        MediaFormat format = codec.getOutputFormat();
        ShortBuffer samples = outputBuffer.order(ByteOrder.nativeOrder()).asShortBuffer();
        int numChannel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        if (channellx < 0 || channellx >= numChannel) {
            Log.e(TAG,"mediacoco channel num wrong.");
            return  null;
        }
        short[] res = new short[samples.remaining() / numChannel];
        for (int i = 0 ; i < res.length ; i++ ){
            res[i] = samples.get(i*numChannel+channellx);
        }
        return  res ;
    }
}
