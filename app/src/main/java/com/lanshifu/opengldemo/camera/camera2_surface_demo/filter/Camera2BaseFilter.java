package com.lanshifu.opengldemo.camera.camera2_surface_demo.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.lanshifu.opengldemo.utils.GLUtil;
import com.lanshifu.opengldemo.utils.ShaderManager;

import java.nio.FloatBuffer;

import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;

/**
 * 滤镜的基类
 * <p>
 * 加载不同的着色器就有不同滤镜效果
 */
public class Camera2BaseFilter {
    private static final String TAG = "Camera2BaseFilter";

    private FloatBuffer mVertexBuffer;  //顶点坐标数据要转化成FloatBuffer格式
    private FloatBuffer mTexCoordBuffer;//顶点纹理坐标缓存

    //当前绘制的顶点位置句柄
    protected int vPositionHandle;
    //变换矩阵句柄
    protected int mMVPMatrixHandle;
    //这个可以理解为一个OpenGL程序句柄
    protected int mProgram;
    //纹理坐标句柄
    protected int mTexCoordHandle;

    //变换矩阵，提供set方法
    private float[] mvpMatrix = new float[16];

    //纹理id
    protected int mTextureId;
    private int uTextureSamplerLocation;

    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }

    private Context mContext;


    public Camera2BaseFilter(Context context, int textureId) {
        this.mContext = context;
        this.mTextureId = textureId;
        //初始化Buffer、Shader、纹理
        initBuffer();
        initShader();
    }

    //数据转换成Buffer
    private void initBuffer() {
        float vertices[] = new float[]{
                -1, 1, 0,
                -1, -1, 0,
                1, 1, 0,
                1, -1, 0,

        };//顶点位置

        float[] colors = new float[]{
                0, 1,
                0, 0,
                1, 1,
                1, 0,

        };//纹理顶点数组

        mVertexBuffer = GLUtil.floatArray2FloatBuffer(vertices);
        mTexCoordBuffer = GLUtil.floatArray2FloatBuffer(colors);
    }

    /**
     * 着色器
     */
    private void initShader() {
        //获取程序，封装了加载、链接等操作
        ShaderManager.Param param = getProgram();
        mProgram = param.program;
        vPositionHandle = param.positionHandle;
        // 获取变换矩阵的句柄
        mMVPMatrixHandle = param.mMVPMatrixHandle;
        //纹理位置句柄
        mTexCoordHandle = param.mTexCoordHandle;

        uTextureSamplerLocation = glGetUniformLocation(mProgram,"uTextureSampler");

        Log.d(TAG, "initShader: mProgram = " + mProgram + ",vPositionHandle="+vPositionHandle +",mMVPMatrixHandle="+mMVPMatrixHandle+
                ",mTexCoordHandle="+mTexCoordHandle+",uTextureSamplerLocation="+uTextureSamplerLocation);
    }

    /**
     * 滤镜子类重写这个方法，加载不同的OpenGL程序
     *
     * @return
     */
    protected ShaderManager.Param getProgram() {
        return ShaderManager.getParam(ShaderManager.CAMERA_BASE_SHADER);
    }


    public void draw( float[] transformMatrix) {
//        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);
//
//        /**设置数据*/
//        // 启用顶点属性，最后对应禁用
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
//
//        //设置三角形坐标数据（一个顶点三个坐标）
//        GLES20.glVertexAttribPointer(vPositionHandle, 3,
//                GLES20.GL_FLOAT, false,
//                3 * 4, mVertexBuffer);
//        //设置纹理坐标数据
//        GLES20.glVertexAttribPointer(mTexCoordHandle, 2,
//                GLES20.GL_FLOAT, false,
//                2 * 4, mTexCoordBuffer);
//
//        // 将投影和视图转换传递给着色器，可以理解为给uMVPMatrix这个变量赋值为mvpMatrix
//        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
//
//
//        glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
//        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
//        glUniform1i(uTextureSamplerLocation, 0);
//
//
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        /** 绘制三角形，4个顶点*/
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//
//        // 禁用顶点数组（好像不禁用也没啥问题）
//        GLES20.glDisableVertexAttribArray(vPositionHandle);
//        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
        glUniform1i(uTextureSamplerLocation, 0);
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, transformMatrix, 0);
        //设置三角形坐标数据（一个顶点三个坐标）
        GLES20.glVertexAttribPointer(vPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                3 * 4, mVertexBuffer);
        //设置纹理坐标数据
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2,
                GLES20.GL_FLOAT, false,
                2 * 4, mTexCoordBuffer);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void onDestroy() {
        GLES20.glDeleteProgram(mProgram);
        mProgram = 0;
    }
}
