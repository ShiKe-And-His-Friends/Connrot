package com.example.ShikeApplication.opengl;

public class OpenGlElPlayer {
    private OpenGlElSurfaceView mGLSurfaceView;

    private int yuvBufferDataWidth;
    private int yuvBufferDateHeight;
    private byte[] yBuffer ;
    private byte[] uBuffer ;
    private byte[] vBuffer ;

    public void setGLSLSurfaceView(OpenGlElSurfaceView iGLSurfaceView) {
        this.mGLSurfaceView = iGLSurfaceView;
    }

    public void onCallRenderYUV(byte[] yuvRawDate,int yuvDataWidth,int yuvDateHeight){
        if (yuvRawDate == null || yuvRawDate.length <= 0 || yuvDataWidth <= 0 || yuvDateHeight <= 0) {
            return;
        }
        if (yuvRawDate.length != yuvDataWidth * yuvDateHeight * 3 >> 2) {
            return;
        }

        if (yuvDataWidth != yuvBufferDataWidth || yuvDateHeight != yuvBufferDateHeight) {
            yuvBufferDataWidth = yuvDataWidth;
            yuvBufferDateHeight = yuvDateHeight;
            yBuffer = new byte[yuvBufferDataWidth * yuvBufferDateHeight];
            uBuffer = new byte[yuvBufferDataWidth * yuvBufferDateHeight >> 2];
            vBuffer = new byte[yuvBufferDataWidth * yuvBufferDateHeight >> 2];
        }
        if (mGLSurfaceView != null) {
            System.arraycopy(yuvRawDate, 0, yBuffer, 0, yuvDataWidth * yuvDateHeight);
            System.arraycopy(yuvRawDate, yuvDataWidth * yuvDateHeight, uBuffer, 0, yuvDataWidth * yuvDateHeight >> 2);
            System.arraycopy(yuvRawDate, yuvDataWidth * yuvDateHeight+ yuvDataWidth * yuvDateHeight >> 2, vBuffer, 0, yuvDataWidth * yuvDateHeight >> 2);
            onCallRenderYUV(yuvDataWidth ,yuvDateHeight ,yBuffer ,uBuffer ,vBuffer);
        }
    }

    public void onCallRenderYUV(int width,int height,byte[] yData,byte[] uData,byte[] vData){
        if (mGLSurfaceView != null) {
            mGLSurfaceView.setYUVData(width, height, yData, uData, vData);
        }
    }
}
