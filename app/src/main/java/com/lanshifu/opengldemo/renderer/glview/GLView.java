package com.lanshifu.opengldemo.renderer.glview;

import android.graphics.RectF;
import android.view.MotionEvent;

import com.lanshifu.opengldemo.utils.GLUtil;

import java.nio.FloatBuffer;

public abstract class GLView {


    protected RectF mBound;

    protected float[] mVertex; //顶点数据
    protected FloatBuffer mVertexBuffer;  //顶点坐标数据要转化成FloatBuffer格式

    public GLView(float left, float top) {
        mBound = new RectF();
        mBound.left = left;
        mBound.top = top;

        initVertex();
        initBuffer();
    }

    public GLView(float left, float top, float width, float height) {
        mBound = new RectF(left, top, left + width, top + height);

        initVertex();
        initBuffer();
    }

    /**
     * 初始化顶点数据
     */
    protected void initVertex(){
        mVertex = new float[12];
        mVertex[0] = mBound.left;
        mVertex[1] = mBound.top;
        mVertex[2] = 0;

        mVertex[3] = mBound.left;
        mVertex[4] = mBound.bottom;
        mVertex[5] = 0;

        mVertex[6] = mBound.right;
        mVertex[7] = mBound.top;
        mVertex[8] = 0;

        mVertex[9] = mBound.right;
        mVertex[10] = mBound.bottom;
        mVertex[11] = 0;
    }

    /**
     * 数据转换
     */
    protected void initBuffer(){
        /** 1、数据转换，顶点坐标数据float类型转换成OpenGL格式FloatBuffer，int和short同理*/
        mVertexBuffer = GLUtil.floatArray2FloatBuffer(mVertex);
    }

    public void draw() {
        onDraw();
    }

    abstract void onDraw();

    /**
     * 事件开始
     */

    public interface OnTouchListener {
        boolean onTouch(MotionEvent event);
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private OnTouchListener mOnTouchListener;

    public boolean onTouchEvent(MotionEvent event) {
        if (mOnTouchListener == null) {
            return false;
        }

        return mOnTouchListener.onTouch(event);
    }

    /** 事件结束*/
}
