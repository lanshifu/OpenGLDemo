package com.lanshifu.opengldemo.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.lanshifu.opengldemo.renderer.glview.Cube;
import com.lanshifu.opengldemo.utils.VaryTools;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 演示动画：平移、旋转、缩放，拿长方体Cube来演示
 */
public class VaryRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "DemoRenderer";
    private Cube mCube;

    /**
     * 投影和相机视图相关
     **/
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    VaryTools mVaryTools;

    /**
     * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
     *
     * @param gl     1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
     * @param config egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCube = new Cube();
        mVaryTools = new VaryTools();

        // 设置默认背景颜色，其实试了下可以在onDrawFrame中重新设置
        GLES20.glClearColor(1.0f, 0.0f, 0, 1.0f);

        translateThread.start();

    }


    /**
     * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
     *
     * @param gl     同onSurfaceCreated()
     * @param width  Surface的宽度
     * @param height Surface的高度
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
        GLES20.glViewport(0, 0, width, height);

        /**投影和相机视图相关**/
        float ratio = (float) width / height;


        //设置正交投影
        Matrix.orthoM(mProjectionMatrix, 0, -ratio * 6, ratio * 6, -6, 6, 3, 20);
//        Matrix.orthoM (mProjectionMatrix, 0,0,width, height, 0, -width, width);//这个投影会跟屏幕坐标关联上
        Log.d(TAG, "onSurfaceChanged: width=" + width + ",height=" + height);

        //设置透视投影（观察点越远，视图越小），这个投影矩阵被应用于对象坐标在onDrawFrame（）方法中
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);

        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        //设置矩阵给工具类
        mVaryTools.setMatrixCamera(mViewMatrix);
        mVaryTools.setMatrixProjection(mProjectionMatrix);

    }

    /**
     * 用一个线程不断改变旋转的角度，达到动画效果
     */
    float mCurrentTranslate = 0;
    float mCurrentRotate = 0;
    float mScale = 0;
    boolean mDestroy = false;
    boolean mTranslateAdd = true;
    boolean mScaleAdd = true;
    Thread translateThread = new Thread() {
        @Override
        public void run() {
            while (!mDestroy) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mCurrentRotate += 10;
                mCurrentRotate = mCurrentRotate % 360;

                if (mTranslateAdd){
                    mCurrentTranslate += 0.2;
                }else {
                    mCurrentTranslate -= 0.2;
                }

                if (mCurrentTranslate > 5) {
                    mTranslateAdd =false;
                }
                if (mCurrentTranslate < -5) {
                    mTranslateAdd = true;
                }

                if (mScaleAdd){
                    mScale +=0.1;
                }else {
                    mScale-=0.1;
                }
                if (mScale<0.1){
                    mScaleAdd = true;
                }

                if (mScale>2){
                    mScaleAdd = false;
                }



                Log.d(TAG, "run: mCurrentRotate=" + mCurrentRotate +" ,mCurrentTranslate="+mCurrentTranslate+" ,mScale="+mScale);
            }

        }
    };

    @Override
    public void onDrawFrame(GL10 gl) {

        // Redraw background color 重绘背景
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);// 开启深度测试
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        mCube.setMVPMatrix(mMVPMatrix);
        mCube.draw();

        //y轴正方形平移
        mVaryTools.pushMatrix();
        mVaryTools.translate(0, mCurrentTranslate, 0);
        mCube.setMVPMatrix(mVaryTools.getFinalMatrix());
        mCube.draw();
        mVaryTools.popMatrix();

        //y轴负方向平移，然后按xyz->(0,0,0)到(1,1,1)旋转30度
        mVaryTools.pushMatrix();
        mVaryTools.translate(0, -3, 0);
        mVaryTools.rotate(mCurrentRotate, 1, 1, 1);
        mCube.setMVPMatrix(mVaryTools.getFinalMatrix());
        mCube.draw();
        mVaryTools.popMatrix();

        //x轴负方向平移，然后按xyz->(0,0,0)到(1,-1,1)旋转120度，在放大到0.5倍
        mVaryTools.pushMatrix();
        mVaryTools.translate(-3, 0, 0);
        mVaryTools.scale(0.5f, 0.5f, 0.5f);

        //在以上变换的基础上再进行变换
        mVaryTools.pushMatrix();
        mVaryTools.translate(12, 0, 0);
        mVaryTools.scale(mScale, mScale, mScale);
        mVaryTools.rotate(mCurrentRotate, 1, 2, 1);
        mCube.setMVPMatrix(mVaryTools.getFinalMatrix());
        mCube.draw();
        mVaryTools.popMatrix();

        //接着被中断的地方执行
        mVaryTools.rotate(mCurrentRotate, -1, -1, 1);
        mCube.setMVPMatrix(mVaryTools.getFinalMatrix());
        mCube.draw();
        mVaryTools.popMatrix();
    }


    public void onDestroy() {
        mDestroy = true;
    }
}
