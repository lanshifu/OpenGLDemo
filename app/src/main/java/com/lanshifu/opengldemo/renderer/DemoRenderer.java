package com.lanshifu.opengldemo.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.lanshifu.opengldemo.renderer.glview.Ball;
import com.lanshifu.opengldemo.renderer.glview.Cube;
import com.lanshifu.opengldemo.renderer.glview.GLTriangle03;
import com.lanshifu.opengldemo.renderer.glview.GLTriangle01;
import com.lanshifu.opengldemo.renderer.glview.GLTriangle02;
import com.lanshifu.opengldemo.renderer.glview.Square;
import com.lanshifu.opengldemo.utils.VaryTools;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 官方demo，画一个三角形
 */
public class DemoRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "DemoRenderer";

    private GLTriangle01 mGlTriangle01;
    private GLTriangle02 mGlTriangle02;
    private GLTriangle03 mGlTrangle03;
    private Cube mCube;

    private Square mSquare;

    private Ball mBall;

    /**投影和相机视图相关**/
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private int mWidth;
    private int mHeight;

    /**
     * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
     * @param gl 1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
     * @param config egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGlTriangle01 = new GLTriangle01();
        mGlTriangle02 = new GLTriangle02();
//        mGlTrangle03 = new GLTriangle03();
        mCube = new Cube();
//        mSquare = new Square();
//        mBall = new Ball();

        // 设置默认背景颜色，其实试了下可以在onDrawFrame中重新设置
        GLES20.glClearColor(1.0f, 0.0f, 0, 1.0f);
    }


    /**
     * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
     * @param gl 同onSurfaceCreated()
     * @param width Surface的宽度
     * @param height Surface的高度
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
        GLES20.glViewport(0,0,width,height);

        mWidth = width;
        mHeight = height;
        /**投影和相机视图相关**/
        float ratio = (float) width / height;


        //设置正交投影
        /***
         * 投影物体的大小不会随观察点的远近而发生变化，我们可以使用下面方法来执行正交投影
         *
         *     Matrix.orthoM (float[] m,       //接收正交投影的变换矩阵
         *                 int mOffset,        //变换矩阵的起始位置（偏移量）
         *                 float left,         //相对观察点近面的左边距
         *                 float right,        //相对观察点近面的右边距
         *                 float bottom,       //相对观察点近面的下边距
         *                 float top,          //相对观察点近面的上边距
         *                 float near,         //相对观察点近面距离
         *                 float far)          //相对观察点远面距离
         */

        Matrix.orthoM (mProjectionMatrix, 0,-ratio*6,ratio*6,-6,6,3,20);
//        Matrix.orthoM (mProjectionMatrix, 0,0,width, height, 0, -width, width);//这个投影会跟屏幕坐标关联上
        Log.d(TAG, "onSurfaceChanged: width=" + width + ",height="+height);

        //设置透视投影（观察点越远，视图越小），这个投影矩阵被应用于对象坐标在onDrawFrame（）方法中
        /***
         *     Matrix.frustumM (float[] m,      //接收透视投影的变换矩阵
         *                 int mOffset,        //变换矩阵的起始位置（偏移量）
         *                 float left,         //相对观察点近面的左边距
         *                 float right,        //相对观察点近面的右边距
         *                 float bottom,       //相对观察点近面的下边距
         *                 float top,          //相对观察点近面的上边距
         *                 float near,         //相对观察点近面距离
         *                 float far)          //相对观察点远面距离
         */
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);

        //设置相机位置
        /***
         *     Matrix.setLookAtM (float[] rm,      //接收相机变换矩阵
         *                 int rmOffset,       //变换矩阵的起始位置（偏移量）
         *                 float eyeX,float eyeY, float eyeZ,   //相机位置
         *                 float centerX,float centerY,float centerZ,  //观察点位置
         *                 float upX,float upY,float upZ)  //up向量在xyz上的分量
         */
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mViewMatrix,0);


    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Redraw background color 重绘背景
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);// 开启深度测试
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        mGlTriangle01.draw();

        mGlTriangle02.setMvpMatrix(mMVPMatrix);
        mGlTriangle02.draw();

//        mGlTrangle03.setMvpMatrix(mMVPMatrix);
//        mGlTrangle03.draw();


//        mSquare.setMvpMatrix(mMVPMatrix);
//        mSquare.draw();

//        mBall.setMvpMatrix(mMVPMatrix);
//        mBall.draw();

        mCube.setMVPMatrix(mMVPMatrix);
        mCube.draw();
    }




}
