package com.lanshifu.opengldemo.camera.camera2_surface_demo.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.lanshifu.opengldemo.utils.ShaderManager;

import static android.opengl.GLES10.glBlendFunc;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DST_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;

//抖音灵魂出窍滤镜
public class Camera2FilterDouyinOut extends Camera2BaseFilter {

    private static final String TAG = "Camera2FilterDouyinOut";

    //透明度句柄
    private int uAlphaHandle;

    //放大矩阵
    private float[] mMvpMatrix = new float[16];

    public Camera2FilterDouyinOut(Context context, int textureId) {
        super(context, textureId);

        uAlphaHandle = GLES20.glGetUniformLocation(mProgram,"uAlpha");
    }

    @Override
    protected ShaderManager.Param getProgram() {
        return ShaderManager.getParam(ShaderManager.CAMERA_BUZZ_DOUYIN_OUT);
    }

    //当前动画进度
    private float mProgress = 1.0f;
    //当前的帧数
    private int mFrames = 0;
    //动画最大帧数
    private static final int mMaxFrames = 15;
    //动画完成后跳过的帧数
    private static final int mSkipFrames = 15;

    float alpha = 0f;
    float scale = 0.5F;
    @Override
    public void draw(float[] transformMatrix) {
        super.draw(transformMatrix);
        //因为这里是两个图层，所以开启混合模式
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);


        mProgress = (float) mFrames / mMaxFrames; //0-1
        if (mProgress > 1f) {
            mProgress = 0f;
        }
        mFrames ++;

        if (mFrames > mFrames +  mSkipFrames) {
            mFrames = 0;
        }


        mMvpMatrix = transformMatrix;//初始化矩阵

        //控制透明度在0-0.5之间
        if (mProgress > 0f) {
            alpha = 0.5f - mProgress * 0.2f;
        }


        //画一个出窍图形，使用缩放矩阵，不断改变 mProgress
        if (mProgress > 0f) {
            scale = 1 + mProgress;
            Log.d(TAG, "draw: scale= "+scale + " ,alpha="+alpha);
            Matrix.scaleM(mMvpMatrix, 0, scale, scale, scale);
            glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMvpMatrix, 0);
            glUniform1f(uAlphaHandle, alpha);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }


        //设置super.draw 原图不透明
        glUniform1f(uAlphaHandle, 1);



    }
}
