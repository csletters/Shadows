package com.example.shadows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shapes.Cube;
import shapes.Square;

import loaders.RawResourceReader;
import loaders.ShaderHandles;
import loaders.ShaderHelper;
import loaders.TextureHelper;

import android.content.Context;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

public class MyRenderer implements Renderer {

	int widthView,heightView;
	Context mActivityContext;
	Cube rocktexcube;
	Square walls;
	float ratio;
	float[] projection = new float[16];
	float[] view = new float[16];
	float[] model = new float[16];
	float[] rotMatrix = new float[16];
	int[] renderTex,depthRb,framebuffers;
	int bias;
	float shadowMat[] = {
			0.5f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.5f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f
			};
	ArrayList<ShaderHandles> shaderPrograms = new ArrayList<ShaderHandles>();
	
	public MyRenderer(final Context activityContext)
	{
		mActivityContext = activityContext;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// Bind the framebuffer
		long time = SystemClock.uptimeMillis() % 4000L;
		float angle = 0.090f * ((int) time);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffers[0]);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		/*GLES20.glUseProgram(shaderPrograms.get(0).programHandle);
		Matrix.translateM(model, 0, (float)(4.0f*Math.cos(angle* (Math.PI / 180))), 1.0f, -(float)(4.0f*Math.sin(angle* (Math.PI / 180))));
		rocktexcube.draw(projection, view, model, shaderPrograms.get(0));
		Matrix.setIdentityM(model, 0);*/
		
		GLES20.glUseProgram(shaderPrograms.get(3).programHandle);
		Matrix.setIdentityM(projection, 0);
		Matrix.setIdentityM(view, 0);
		Matrix.orthoM(projection, 0, -10, 10, -10, 10, -10, 20);
		Matrix.setLookAtM(view, 0, 0.0f, 4.0f, 8f, 0, 0.0f, 0, 0, 1, 0);
		Matrix.translateM(model, 0, 0.0f, -0.0f, 0.0f);
		Matrix.setRotateM(rotMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
		Matrix.multiplyMM(model, 0, rotMatrix, 0, model, 0);
		Matrix.rotateM( model,0, -90, 1, 0, 0);
		Matrix.scaleM(model, 0, 8.0f, 8.0f, 1.0f);
		Matrix.translateM(model, 0, -0.5f, -0.5f, 0.0f);
		walls.draw(projection, view, model,  shaderPrograms.get(3));
		Matrix.setIdentityM(model, 0);
		Matrix.translateM(model, 0, (float)(4.0f*Math.cos(angle* (Math.PI / 180))), 1.0f, -(float)(4.0f*Math.sin(angle* (Math.PI / 180))));
		rocktexcube.drawPlain(projection, view, model, shaderPrograms.get(3));
		Matrix.setIdentityM(model, 0);
		Matrix.setIdentityM(projection, 0);
		Matrix.setIdentityM(view, 0);
		
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		
		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]); 
			
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		Matrix.perspectiveM(projection, 0, 90, ratio, 0.1f, 100);
		Matrix.setLookAtM(view, 0, 0.0f, 4.0f, 8f, 0, 0.0f, 0, 0, 1, 0);
		
		
		GLES20.glUseProgram(shaderPrograms.get(1).programHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
		GLES20.glUniform1i(shaderPrograms.get(1).mTextureUniformHandle.get(0), 0);
		GLES20.glUniformMatrix4fv(bias, 1, false, shadowMat, 0);
		Matrix.translateM(model, 0, 0.0f, -0.0f, 0.0f);
		Matrix.setRotateM(rotMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
		Matrix.multiplyMM(model, 0, rotMatrix, 0, model, 0);
		Matrix.rotateM( model,0, -90, 1, 0, 0);
		Matrix.scaleM(model, 0, 8.0f, 8.0f, 1.0f);
		Matrix.translateM(model, 0, -0.5f, -0.5f, 0.0f);
		walls.draw(projection, view, model,  shaderPrograms.get(1));
		Matrix.setIdentityM(model, 0);
		Matrix.translateM(model, 0, (float)(4.0f*Math.cos(angle* (Math.PI / 180))), 1.0f, -(float)(4.0f*Math.sin(angle* (Math.PI / 180))));
		rocktexcube.drawPlain(projection, view, model, shaderPrograms.get(1));
		Matrix.setIdentityM(model, 0);
		

		GLES20.glUseProgram(shaderPrograms.get(2).programHandle);
		walls.draw(projection, view, model,  shaderPrograms.get(2),renderTex[0] );
		Matrix.setIdentityM(model, 0);
		//walls.draw(projection, view, model, shaderPrograms.get(1));
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
		ratio = (float)width/height;
		Matrix.perspectiveM(projection, 0, 90, ratio, 0.1f, 100);
		Matrix.setLookAtM(view, 0, 0.0f, 4.0f, 8f, 0, 0.0f, 0, 0, 1, 0);
		Matrix.setIdentityM(model, 0);
		widthView = width;
		heightView = height;
		
		walls = new Square();
		rocktexcube = new Cube();
		framebuffers = new int[1];
		depthRb = new int[1];
		renderTex = new int[1];
		GLES20.glGenFramebuffers(1, framebuffers,0);
		GLES20.glGenRenderbuffers(1, depthRb,0);
		
		GLES20.glGenTextures(1, renderTex, 0);
		// generate texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
		// Give an empty image to OpenGL ( the last "0" )
		IntBuffer texBuffer;
		int[] buf = new int[widthView * heightView];
		texBuffer = ByteBuffer.allocateDirect(buf.length* 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		// Poor filtering. Needed !
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, widthView, heightView, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, texBuffer);
		
		
		/*GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT16, widthView, heightView, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_FLOAT, texBuffer);*/
		 
		
		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, widthView, heightView);
		
		// attach render buffer as depth buffer
		//GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		 GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		 GLES20.glClearDepthf(1.0f);  
		 GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		 GLES20.glDepthFunc( GLES20.GL_LESS);
		 GLES20.glDepthMask( true );
		 
		//create shader program handles and program for display textures
		ShaderHandles shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertextexture,R.raw.fragmenttexture);
		shader.mTextureDataHandle.add(TextureHelper.loadTexture(mActivityContext, R.drawable.normalmappic)[0]);
		shader.mTextureDataHandle.add(TextureHelper.loadTexture(mActivityContext, R.drawable.normalmap)[0]);
		initBasicHandlesWTexture(shader);
		shaderPrograms.add(shader);
		
		
		shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertextexturewall,R.raw.fragmenttexturewall);
		initBasicHandles(shader);
		shaderPrograms.add(shader);
		bias = GLES20.glGetUniformLocation(shader.programHandle, "biasMatrix");
		shader.mTextureUniformHandle.add(GLES20.glGetUniformLocation(shader.programHandle, "uTexture"));
		
		shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertex,R.raw.fragment);
		initBasicTextureHandles(shader);
		shaderPrograms.add(shader);
		
		shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertexshadow,R.raw.fragmentshadow);
		initBasicHandles(shader);
		shaderPrograms.add(shader);

	}
	
	public void initBasicHandles(ShaderHandles handles)
	{
		//attributes
		handles.positionHandle = GLES20.glGetAttribLocation(handles.programHandle, "aPosition");
		handles.normalHandle = GLES20.glGetAttribLocation(handles.programHandle, "normal");
		
		//uniforms
		handles.modelHandle =  GLES20.glGetUniformLocation(handles.programHandle, "model");
		handles.viewHandle =  GLES20.glGetUniformLocation(handles.programHandle, "view");
		handles.projectionHandle =  GLES20.glGetUniformLocation(handles.programHandle, "projection");
	}
	
	public void initBasicTextureHandles(ShaderHandles handles)
	{
		//attributes
		handles.positionHandle = GLES20.glGetAttribLocation(handles.programHandle, "aPosition");
		handles.mTextureCoordinateHandle = GLES20.glGetAttribLocation(handles.programHandle, "aTexCord");
		handles.normalHandle = GLES20.glGetAttribLocation(handles.programHandle, "normal");
		
		//uniforms
		handles.modelHandle =  GLES20.glGetUniformLocation(handles.programHandle, "model");
		handles.viewHandle =  GLES20.glGetUniformLocation(handles.programHandle, "view");
		handles.projectionHandle =  GLES20.glGetUniformLocation(handles.programHandle, "projection");
		handles.mTextureUniformHandle.add(GLES20.glGetUniformLocation(handles.programHandle, "uTexture"));
	}
	
	public void initBasicHandlesWTexture(ShaderHandles shader)
	{
		//attributes
		shader.positionHandle = GLES20.glGetAttribLocation(shader.programHandle, "aPosition");
		shader.mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader.programHandle, "aTexCord");
		shader.normalHandle = GLES20.glGetAttribLocation(shader.programHandle, "normal");
		shader.tangentHandle = GLES20.glGetAttribLocation(shader.programHandle,"tangent");
									
		//uniforms
		shader.modelHandle =  GLES20.glGetUniformLocation(shader.programHandle, "model");
		shader.viewHandle =  GLES20.glGetUniformLocation(shader.programHandle, "view");
		shader.projectionHandle =  GLES20.glGetUniformLocation(shader.programHandle, "projection");
		shader.mTextureUniformHandle.add(GLES20.glGetUniformLocation(shader.programHandle, "uTexture"));
		shader.mTextureUniformHandle.add(GLES20.glGetUniformLocation(shader.programHandle, "nTexture"));
		
	}
	
	public int createShader(int vertex, int fragment) {
		String vertexShaderCode = RawResourceReader
				.readTextFileFromRawResource(mActivityContext, vertex);
		String fragmentShaderCode = RawResourceReader
				.readTextFileFromRawResource(mActivityContext, fragment);

		int vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
			
		int mProgram;
		
		mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle,fragmentShaderHandle);

		return mProgram;
	}

	
}
