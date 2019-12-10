package com.example.ShikeApplication.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.utils.RenderShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGlElRender implements GLSurfaceView.Renderer {
    private static final String TAG = OpenGlElRender.class.getSimpleName();
    private Context context;

    private final float[] vertexData ={//顶点坐标
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    private final float[] textureData ={//纹理坐标
            0f,1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int yuvProgram;
    private int avPosition;
    private int afPosition;

    private int ySampler;
    private int uSampler;
    private int vSampler;
    private int[] yuvTextureIdArray;

    private int yuvWidth;
    private int yuvHeight;
    private ByteBuffer yData;
    private ByteBuffer uData;
    private ByteBuffer vData;

    public OpenGlElRender(Context context){
        this.context = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)//存储顶点坐标数据
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)//存储纹理坐标
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRenderYUV();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//用黑色清屏
        renderYUV();
    }

    private void initRenderYUV(){
        String vertexSource = RenderShaderUtil.readRawTxt(context, R.raw.vertex_shader01);
        String fragmentSource = RenderShaderUtil.readRawTxt(context,R.raw.fragment_shader01);
        yuvProgram = RenderShaderUtil.createProgram(vertexSource,fragmentSource);

        avPosition = GLES20.glGetAttribLocation(yuvProgram,"av_Position");
        afPosition = GLES20.glGetAttribLocation(yuvProgram,"af_Position");

        ySampler = GLES20.glGetUniformLocation(yuvProgram, "ySampler");
        uSampler = GLES20.glGetUniformLocation(yuvProgram, "uSampler");
        vSampler = GLES20.glGetUniformLocation(yuvProgram, "vSampler");

        yuvTextureIdArray = new int[3];//创建纹理
        GLES20.glGenTextures(3, yuvTextureIdArray, 0);

        for(int i = 0; i < 3; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextureIdArray[i]);//绑定纹理
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);//设置环绕和过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
        Log.d(TAG,"initRenderYUV");
    }

    public void setYUVRenderData(int width, int height, byte[] y, byte[] u, byte[] v) {
        this.yuvWidth = width;
        this.yuvHeight = height;
        this.yData = ByteBuffer.wrap(y);
        this.uData = ByteBuffer.wrap(u);
        this.vData = ByteBuffer.wrap(v);
    }

    private void renderYUV(){
        Log.d(TAG,"render");
        if(yuvWidth > 0 && yuvHeight > 0 && yData != null && uData != null && vData != null){
            GLES20.glUseProgram(yuvProgram);

            GLES20.glEnableVertexAttribArray(avPosition);//使顶点属性数组有效
            GLES20.glVertexAttribPointer(avPosition, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);//为顶点属性赋值

            GLES20.glEnableVertexAttribArray(afPosition);
            GLES20.glVertexAttribPointer(afPosition, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//激活纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextureIdArray[0]);//绑定纹理
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, yuvWidth, yuvHeight, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yData);//

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextureIdArray[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, yuvWidth / 2, yuvHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, uData);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yuvTextureIdArray[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, yuvWidth / 2, yuvHeight / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, vData);

            GLES20.glUniform1i(ySampler, 0);
            GLES20.glUniform1i(uSampler, 1);
            GLES20.glUniform1i(vSampler, 2);

            yData.clear();
            uData.clear();
            vData.clear();
            yData = null;
            uData = null;
            vData = null;

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }
}