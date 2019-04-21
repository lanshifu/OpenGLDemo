package com.lanshifu.opengldemo.renderer.glview;

import android.view.MotionEvent;

public class GLView {


    
    public void draw(){
        onDraw();
    }
    
    protected void onDraw(){}

    /** 事件开始*/

    public interface OnTouchListener {
        boolean onTouch(MotionEvent event);
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private OnTouchListener mOnTouchListener;

    public boolean onTouchEvent(MotionEvent event) {
        if (mOnTouchListener == null){
            return false;
        }

        return mOnTouchListener.onTouch(event);
    }

    /** 事件结束*/
}
