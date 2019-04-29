package com.lanshifu.opengldemo.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class PreviewGLSurfaceView extends GLSurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder holder;
    private Context mContext;
    private static final String TAG = "PreviewGLSurfaceView";

    public PreviewGLSurfaceView(Context context) {
        super(context);
        this.mContext = context;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        this.holder = holder;
        init();
    }

    private void init() {
        Log.d(TAG, "init: ");
//        //这句是必要的，避免GLSurfaceView自带的Surface影响渲染
//        getHolder().addCallback(null);
//        //指定外部传入的surface为渲染的window surface
//        setEGLWindowSurfaceFactory(new GLSurfaceView.EGLWindowSurfaceFactory() {
//            @Override
//            public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig
//                    config, Object window) {
//                //这里的surface由外部传入，可以为Surface、SurfaceTexture或者SurfaceHolder
//                return egl.eglCreateWindowSurface(display,config,holder,null);
//            }
//
//            @Override
//            public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
//                egl.eglDestroySurface(display, surface);
//            }
//        });
//
//        setEGLContextClientVersion(2);
//        setRenderMode(RENDERMODE_WHEN_DIRTY);
//        setPreserveEGLContextOnPause(true);
    }

    public void attachedToWindow() {
        super.onAttachedToWindow();
    }

    public void detachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
