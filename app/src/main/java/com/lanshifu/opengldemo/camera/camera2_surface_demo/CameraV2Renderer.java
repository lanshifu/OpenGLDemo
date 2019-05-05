package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lanshifu.opengldemo.utils.MatrixUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_STRIP;

public class CameraV2Renderer implements GLSurfaceView.Renderer   {
    public static final String TAG = "Filter_CameraV2Renderer";
    private Context mContext;
    CameraV2GLSurfaceView mCameraV2GLSurfaceView;
    CameraV2 mCamera;
    boolean bIsPreviewStarted;
    private int mOESTextureId = -1;
    private SurfaceTexture mSurfaceTexture;
    private float[] transformMatrix = new float[16];
    private FilterEngine mFilterEngine;
    private FloatBuffer mVertextBuffer;
    private FloatBuffer mFragmentBuffer;
    private int mShaderProgram = -1;
    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;
    private int[] mFBOIds = new int[1];

    public void init(CameraV2GLSurfaceView surfaceView, CameraV2 camera, boolean isPreviewStarted, Context context) {
        mContext = context;
        mCameraV2GLSurfaceView = surfaceView;
        mCamera = camera;
        bIsPreviewStarted = isPreviewStarted;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //1、编译链接
        mOESTextureId = Utils.createOESTextureObject();
        mFilterEngine = new FilterEngine(mOESTextureId, mContext);
        mVertextBuffer = mFilterEngine.getVertexmBuffer();
        mFragmentBuffer = mFilterEngine.getFragmentBuffer();
        mShaderProgram = mFilterEngine.getShaderProgram();

        // TODO: 2019/4/30 什么意思
        glGenFramebuffers(1, mFBOIds, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, mFBOIds[0]);
        Log.i(TAG, "onSurfaceCreated: mFBOId: " + mFBOIds[0]);

        //2、获取句柄
        aPositionLocation = glGetAttribLocation(mShaderProgram, FilterEngine.POSITION_ATTRIBUTE);
        aTextureCoordLocation = glGetAttribLocation(mShaderProgram, FilterEngine.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_SAMPLER_UNIFORM);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Log.i(TAG, "onSurfaceChanged: " + width + ", " + height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Long t1 = System.currentTimeMillis();
        if (mSurfaceTexture != null) {
            //surfaceTexture.updateTexImage()更新预览上的图像
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(transformMatrix);
        }

        //预览在这里启动
        if (!bIsPreviewStarted) {
            bIsPreviewStarted = initSurfaceTexture();
            bIsPreviewStarted = true;
            return;
        }

        //glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);



        glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        glUniform1i(uTextureSamplerLocation, 0);
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        if (mVertextBuffer != null) {
            glEnableVertexAttribArray(aPositionLocation);
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 2*4, mVertextBuffer);

            glEnableVertexAttribArray(aTextureCoordLocation);
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 2*4, mFragmentBuffer);
        }

        //glDrawElements(GL_TRIANGLE_FAN, 6,GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_TRIANGLE_FAN, 0 , 6);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        //glDrawArrays(GL_TRIANGLES, 3, 3);
        glBindFramebuffer(GL_FRAMEBUFFER,
                0);
        long t2 = System.currentTimeMillis();
        long t = t2 - t1;
        Log.i(TAG, "onDrawFrame: time: " + t);
    }

    public boolean initSurfaceTexture() {
        if (mCamera == null || mCameraV2GLSurfaceView == null) {
            Log.i(TAG, "mCamera or mGLSurfaceView is null!");
            return false;
        }
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        //和图片不同的是，图片数据是相同的，而摄像头数据是变换的，所以每当摄像头有新的数据来时，我们需要通过surfaceTexture.updateTexImage()更新预览上的图像
        // updateTexImage 不应该在OnFrameAvailableLister的回调方法中直接调用，而应该在onDrawFrame中执行。而调用requestRender，可以触发onDrawFrame
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "onFrameAvailable: ");
                mCameraV2GLSurfaceView.requestRender();
            }
        });
        mCamera.setPreviewTexture(mSurfaceTexture);
        mCamera.startPreview();
        return true;
    }

}
