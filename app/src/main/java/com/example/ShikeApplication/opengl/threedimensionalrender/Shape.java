package com.example.ShikeApplication.opengl.threedimensionalrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.ShikeApplication.R;

import org.obj2opengles.smapledemo.Obj2OpenJL;
import org.obj2opengles.v3.model.OpenGLModelData;
import org.obj2opengles.v3.model.RawOpenGLModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by shike on 4/20/2020
 **/
public class Shape {
	private float vertices[];
	private float texures[];
	private float normals[];
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTexureBuffer;
	private FloatBuffer mNormalBuffer;
	private int mVertexCount;
	
	private FloatBuffer mSqureBuffer;
	private int mFrameBufferProgram;
	private int mLoadedTextureId;
	private Context mContext;

	public Shape(Context context) {
		this.mContext = context;
		initVetexData();
	}

	public void initVetexData() {
		float [] squareVertexs = new float[] {
				-1,-1,  0,1,
				-1,1,  0,0,
				1,-1,  1,1,
				1,1,  1,0
		};
		ByteBuffer vbb0 = ByteBuffer.allocateDirect(squareVertexs.length * 4);
		vbb0.order(ByteOrder.nativeOrder());
		mSqureBuffer = vbb0.asFloatBuffer();
		mSqureBuffer.put(squareVertexs);
		mSqureBuffer.position(0);
		InputStream is = null;
		try {
			is = mContext.getAssets().open("3d.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
		RawOpenGLModel openGLModel = new Obj2OpenJL().convert(is);
		OpenGLModelData openGLModelData = openGLModel.normalize().center().getDataForGLDrawArrays();
		vertices = openGLModelData.getVertices();
		mVertexCount = vertices.length / 3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		texures = openGLModelData.getTextureCoordinates();
		ByteBuffer vbb2 = ByteBuffer.allocateDirect(texures.length * 4);
		vbb2.order(ByteOrder.nativeOrder());
		mTexureBuffer = vbb2.asFloatBuffer();
		mTexureBuffer.put(texures);
		mTexureBuffer.position(0);
		normals = openGLModelData.getNormals();
		ByteBuffer vbb3 = ByteBuffer.allocateDirect(normals.length * 4);
		vbb3.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb3.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);
		initTexture(R.drawable.opengeles_texture);
	}
	
	public void initTexture(int res) {
		int [] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		mLoadedTextureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLoadedTextureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}

	public void draw(float[] mvpMatrix, float[] mMatrix) {
		// 生成FrameBuffer
		GLRecoder.beginDraw();

		drawRec(mvpMatrix, mMatrix);

		GLRecoder.endDraw();
	}

	private void drawRec(float[] mvpMatrix, float[] mMatrix) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		int frameBufferVertexShader = loaderShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int frameBufferFagmentShader = loaderShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		mFrameBufferProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mFrameBufferProgram, frameBufferVertexShader);
		GLES20.glAttachShader(mFrameBufferProgram, frameBufferFagmentShader);
		GLES20.glLinkProgram(mFrameBufferProgram);
		int fbPositionHandle = GLES20.glGetAttribLocation(mFrameBufferProgram, "aPosition");
		int fbNormalHandle = GLES20.glGetAttribLocation(mFrameBufferProgram, "aNormal");
		int fbTextureCoordHandle = GLES20.glGetAttribLocation(mFrameBufferProgram, "aTextureCoord");
		int fbuMVPMatrixHandle = GLES20.glGetUniformLocation(mFrameBufferProgram, "uMVPMatrix");
		int fbuMMatrixHandle = GLES20.glGetUniformLocation(mFrameBufferProgram, "uMMatrix");
		int fbuLightLocationHandle = GLES20.glGetUniformLocation(mFrameBufferProgram, "uLightLocation");
		int fbuTextureHandle = GLES20.glGetUniformLocation(mFrameBufferProgram, "uTexture");
		GLES20.glUseProgram(mFrameBufferProgram);
		mVertexBuffer.position(0);
		GLES20.glVertexAttribPointer(fbPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
		mTexureBuffer.position(0);
		GLES20.glVertexAttribPointer(fbTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexureBuffer);
		mTexureBuffer.position(0);
		GLES20.glVertexAttribPointer(fbNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mNormalBuffer);
		GLES20.glEnableVertexAttribArray(fbPositionHandle);
		GLES20.glEnableVertexAttribArray(fbTextureCoordHandle);
		GLES20.glEnableVertexAttribArray(fbNormalHandle);
		GLES20.glUniform3f(fbuLightLocationHandle, 0, 10, 10);
		GLES20.glUniformMatrix4fv(fbuMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLES20.glUniformMatrix4fv(fbuMMatrixHandle, 1, false, mMatrix, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLoadedTextureId);
		GLES20.glUniform1i(fbuTextureHandle, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
	}


	private int loaderShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
	
	private String vertexShaderCode = "uniform mat4 uMVPMatrix;"
			+ "attribute vec2 aTextureCoord;"
			+ "varying vec2 vTextureCoord;"
			+ "uniform mat4 uMMatrix;"
			+ "uniform vec3 uLightLocation;"
			+ "varying vec4 vDiffuse;"
			+ "attribute vec3 aPosition;"
			+ "attribute vec3 aNormal;"
			+ "void main(){"  
			+ "vec3 normalVectorOrigin = aNormal;"
			+ "vec3 normalVector = normalize((uMMatrix*vec4(normalVectorOrigin,1)).xyz);"
			+ "vec3 vectorLight = normalize(uLightLocation - (uMMatrix * vec4(aPosition,1)).xyz);"
			+ "float factor = max(0.0, dot(normalVector, vectorLight));"
			+ "vDiffuse = factor*vec4(1,1,1,1.0);"
			+ "gl_Position = uMVPMatrix * vec4(aPosition,1);"
			+ "vTextureCoord = aTextureCoord;"
			+ "}";

	private String fragmentShaderCode = "precision mediump float;"
			+ "uniform sampler2D uTexture;"
			+ "varying vec2 vTextureCoord;"
			+ "varying  vec4 vColor;"
			+ "varying vec4 vDiffuse;"
			+ "void main(){"
			+ "gl_FragColor = (vDiffuse + vec4(0.6,0.6,0.6,1))*texture2D(uTexture, vec2(vTextureCoord.s,vTextureCoord.t));"
			+ "}";
}

