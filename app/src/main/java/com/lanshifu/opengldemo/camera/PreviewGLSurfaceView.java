package com.lanshifu.opengldemo.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

public class PreviewGLSurfaceView extends GLSurfaceView {
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
    }

    public void attachedToWindow() {
        super.onAttachedToWindow();
    }

    public void detachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
