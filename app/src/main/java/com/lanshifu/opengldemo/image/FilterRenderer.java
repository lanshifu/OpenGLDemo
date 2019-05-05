package com.lanshifu.opengldemo.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.lanshifu.opengldemo.BaseRenderer;
import com.lanshifu.opengldemo.renderer.glview.Square03;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 纹理贴图
 */
public class FilterRenderer extends BaseRenderer {

    private static final String TAG = "DemoRenderer";


    private Square03 mSquare03;

    BaseFilter mFilterView;

    public void setFilterView(BaseFilter filterView) {
        mFilterView = filterView;
    }

    /**
     * 投影和相机视图相关矩阵
     **/
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private Context mContext;
    private Bitmap mBitmap;
    private float mWH;

    public FilterRenderer(Context context) {
        mContext = context;
    }

    /**
     * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
     *
     * @param gl     1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
     * @param config egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

//        mSquare03 = new Square03(mContext, mBitmap);

        try {
            mBitmap = BitmapFactory.decodeStream(mContext.getResources().getAssets().open("picture.png"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("lxb", "onSurfaceCreated: " + e.getMessage());
        }
        if (mBitmap == null) {
            Log.e("lxb", "initTexture: mBitmap == null");
        }



        mFilterView = new GrayFilter(mContext, mBitmap);

        // 设置默认背景颜色，可以在onDrawFrame中重新设置
        GLES20.glClearColor(1.0f, 0.0f, 0, 1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

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

        //通过投影设置，适配横屏
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        mWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            //横屏
            if (mWH > sWidthHeight) { //图片太大要压缩
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * mWH, sWidthHeight * mWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / mWH, sWidthHeight / mWH, -1, 1, 3, 7);
            }
        } else {
            if (mWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * mWH, 1 / sWidthHeight * mWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -mWH / sWidthHeight, mWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if (filterChange) {
            updateFilterView();
        }
        // Redraw background color 重绘背景
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//        mTriangleTexture.drawSelf();

//        mGLTriangle04.setMvpMatrix(mMVPMatrix);
//        mGLTriangle04.draw();

//        mSquare03.setMvpMatrix(mMVPMatrix);
//        mSquare03.setMxy(mWH);
//        mSquare03.setFilter(Filter.NONE);
//        mSquare03.draw();

        if (mFilterView == null) {
            Log.e(TAG, "onDrawFrame: mFilterView == null");
            return;
        }

        mFilterView.setMvpMatrix(mMVPMatrix);
        mFilterView.draw();

    }



    boolean filterChange = false;
    Filter filter;

    public void setType(Filter filter) {
        if (this.filter == filter){
            Log.d(TAG, "setType: this.filter == filter");
            return;
        }
        this.filter = filter;
        filterChange = true;

    }

    void updateFilterView() {

        mFilterView = null;
        switch (this.filter) {
            case GRAY:
                mFilterView = new GrayFilter(mContext, mBitmap);
                break;
            case NONE:
                mFilterView = new BaseFilter(mContext, mBitmap);
                break;
            default:
                mFilterView = new BaseFilter(mContext, mBitmap);
                break;


        }

        filterChange = false;
    }

    @Override
    public void onDestroy() {
        if (mFilterView != null){
            mFilterView.onDestroy();
        }
    }


    public enum Filter {

        NONE(0, new float[]{0.0f, 0.0f, 0.0f}),
        GRAY(1, new float[]{0.299f, 0.587f, 0.114f}),
        COOL(2, new float[]{0.0f, 0.0f, 0.5f}), //冷就是多加点蓝
        WARM(2, new float[]{0.2f, 0.2f, 0.0f}), //暖就是多加点红跟绿
        BLUR(3, new float[]{0.002f, 0.002f, 0.002f}), //距离越大越模糊
        MAGN(4, new float[]{0.0f, 0.0f, 0.4f}),
        FOUR(5, new float[]{0.0f, 0.0f, 0.0f}); //四分镜

        private int vChangeType;
        private float[] data;

        Filter(int vChangeType, float[] data) {
            this.vChangeType = vChangeType;
            this.data = data;
        }

        public int getType() {
            return vChangeType;
        }

        public float[] data() {
            return data;
        }

    }

}
