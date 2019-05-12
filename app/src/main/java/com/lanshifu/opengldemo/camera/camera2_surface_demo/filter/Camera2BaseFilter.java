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
 * 滤镜的基类，默认没有滤镜效果
 * <p>
 * 加载不同的着色器就有不同滤镜效果
 */
public abstract class Camera2BaseFilter {
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
    //纹理id
    protected int mTextureId;

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

        //这里经过测试，左下角才是纹理坐标的原点，右上角是（1，1）
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


        Log.d(TAG, "initShader: mProgram = " + mProgram + ",vPositionHandle="+vPositionHandle +",mMVPMatrixHandle="+mMVPMatrixHandle+
                ",mTexCoordHandle="+mTexCoordHandle);
    }

    /**
     * 滤镜子类重写这个方法，加载不同的OpenGL程序
     *
     * @return
     */
    protected abstract ShaderManager.Param getProgram();


    public void draw( float[] transformMatrix) {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        // 启用顶点属性，最后对应禁用
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        //绑定纹理，跟图片不同的是，这里是扩展纹理
        glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);

        //设置转换矩阵数据
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
