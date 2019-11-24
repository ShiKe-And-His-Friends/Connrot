package com.example.ShikeApplication.ndkdemo.CallbackInterface;

public class JavaNdkListener {

    private ThreadErrorListener onErrerListener;

    public void setOnErrerListener(ThreadErrorListener onErrerListener) {
        this.onErrerListener = onErrerListener;
    }

    public void onError(int code, String msg)
    {
        if(onErrerListener != null)
        {
            onErrerListener.onError(code, msg);
        }
    }
}
