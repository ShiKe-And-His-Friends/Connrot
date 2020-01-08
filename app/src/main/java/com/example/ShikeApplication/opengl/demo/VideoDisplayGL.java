package com.example.ShikeApplication.opengl.demo;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import java.nio.ByteBuffer;

public class VideoDisplayGL extends OpenGlSurfaceProxy{
	private static final String TAG = "NgnProxyVideoConsumerGL";
	
	private ByteBuffer mVideoFrame;
	private Context mContext;
	private GLELSurfaceRender mPreview;
	private int mWidth;
	private int mHeight;
	
	public boolean mNeedSetView = true;
	
	private static VideoDisplayGL mInstance;
	
	public static VideoDisplayGL getInstance(Context context)
	{
		if(mInstance == null)
		{
			mInstance = new VideoDisplayGL(context);
		}
		return mInstance;
	}
	
	private VideoDisplayGL(Context context){
    	super();
    	if(mContext == null && context != null)
        {
			mContext = context;
        }
    	mWidth = DEFAULT_VIDEO_WIDTH;
    	mHeight = DEFAULT_VIDEO_HEIGHT;
    }
    
    @Override
    public void invalidate(){
    	super.invalidate();
    	mVideoFrame = null;
    	System.gc();
    }
    
    public void setContext(Context context){
    	mContext = context;
    }
    
    /*public static boolean bIsSaveFile = false;
    public static final String TEST_FILE_NAME = "/mnt/sdcard/yuvdataRecv.yuv";
    private static int saveData( byte[] bytes)
    {
    	// byte[] bytes = fromHexString( hexString );  
    	 try{  
	    	 OutputStream os=new FileOutputStream(TEST_FILE_NAME, true);
	    	 os.write(bytes);  
	    	 os.flush();  
	    	 os.close();  
    	 }catch(Exception ex){  
    		 ex.printStackTrace();  
    	 }  
    	 return 0;
    }*/
    
    public interface VideoPlayInterface {
		public int onPlayCallback(byte[] yuvData, int length, int[] widHei);
	}
	
	private VideoPlayInterface mCbInterace;
	
	public void setCbInterface(VideoPlayInterface inf)
	{
		mCbInterace = inf;
	}
	
	public int startPlay(int iwid, int ihei, FrameLayout ly)
	{
		setPreViewBuff(iwid, ihei, true);
		mStarted = true;
		View view = startPreview();
		initView(ly, view);
		
		return 0;
	}
	
	
    private Looper mLooper;
    private View startPreview()
    {
    	if(mPreview == null && mContext != null)
    	{
    		Log.i(TAG, "mContext not null");
			if(mLooper != null){
				mLooper.quit();
				mLooper = null;
			}
			
			final Thread previewThread = new Thread() {
				@Override
				public void run() {
					android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
					Looper.prepare();
					mLooper = Looper.myLooper();
					Log.i(TAG, "thread in");
					
					synchronized (this) {
						if(mPreview == null || mPreview.isDestroyed()){
				    		mPreview = new GLELSurfaceRender(VideoDisplayGL.this, mContext,  mVideoFrame, mWidth, mHeight/*, mFps*/);
				    		mPreview.setBuffer(mVideoFrame, mWidth, mHeight);
				    	}
						notify();
					}
					
					mPreview.onResume();
					
					int iRecvBufLen =  mVideoFrame.capacity();
					byte[] videoBytes = new byte[iRecvBufLen];
					
					int[] recvWindHei = new int[2];
					int iRet = 0;
					while(mStarted)
					{
						if(mCbInterace != null)
						{
							iRet = mCbInterace.onPlayCallback(videoBytes, iRecvBufLen, recvWindHei);
						}
						//recv yuv data
						if(iRet > 0)
						{
							if(mWidth != recvWindHei[0] || mHeight != recvWindHei[1])
							{
								Log.i(TAG, "last width:"+mWidth+",hei:"+mHeight+",new wid:"+recvWindHei[0]+",hei:"+recvWindHei[1]);
								
								setPreViewBuff(recvWindHei[0], recvWindHei[1], true);
								
								iRecvBufLen =  mVideoFrame.capacity();
								videoBytes = new byte[iRecvBufLen];
								
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								continue;
							}
							if(mPreview != null && mPreview.isReady() && mVideoFrame!= null && videoBytes != null)
							{
								try {
									mVideoFrame.rewind();
									mVideoFrame.put(videoBytes);
									
									if(mNeedSetView)
									{
										mNeedSetView = false;
										mPreview.resetViewPort();
									}
									//Log.i(TAG, "will call surface view render");
									mPreview.requestRender();
								} catch (Exception e) {
									e.printStackTrace();
									Log.w(TAG, "draw fram except occuss");
								}
							}
						}
						else
						{
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
									
					Looper.loop();
					Log.d(TAG, "VideoConsumer::Looper::exit");
				}
			};
			previewThread.setPriority(Thread.MAX_PRIORITY);
			previewThread.setName("VideoConsumerThread");
			synchronized(previewThread) {
				previewThread.start();
				try {
					previewThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
	        }
		}
		else{
			Log.e(TAG, "Invalid state");
		}
    	
    	return mPreview;
    }
    
    public View resumePreview(Context context){
    	
    	mContext = context == null ? mContext : context;
    	mPreview = null;

    	Log.i(TAG, "resume Preview in");
    	synchronized (this) {
			if(mPreview == null || mPreview.isDestroyed()){
	    		mPreview = new GLELSurfaceRender(VideoDisplayGL.this, mContext,  mVideoFrame, mWidth, mHeight/*, mFps*/);
	    		mPreview.setBuffer(mVideoFrame, mWidth, mHeight);
	    	}
			notify();
		}
    	
    	mPreview.onResume();
    	
    	return mPreview;
    }
    
    private void setPreViewBuff(int width, int height, boolean isSave)
    {
    	mPrepared = true;
    	
    	if(isSave)
    	{
	    	mWidth = width;
			mHeight = height;
    	}
    	
		mVideoFrame = ByteBuffer.allocateDirect((width * height * 3) >> 1);
		if(mPreview != null){
			mPreview.setBuffer(mVideoFrame, width, height);
		}
    }

    public int pauseCallback(){
    	synchronized(this){
	    	Log.d(TAG, "pauseCallback");
	    	super.mPaused = true;
	    	return 0;
    	}
    }
    
    public synchronized int stopCallback(){
    	synchronized(this){
	    	Log.d(TAG, "stopCallback");
	    	super.mStarted = false;
	    	
	    	mPreview = null;
	    	return 0;
    	}
    }

    public void setViewPause(boolean bPaused)
    {
    	if(mPreview != null)
    	{
    		if(bPaused)
    			mPreview.onPause();
    		else
    			mPreview.onResume();
    	}
    }
	
	/**
	 * OpenGL Surface view
	 */
}
