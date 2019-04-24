package com.lanshifu.opengldemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lanshifu.opengldemo.renderer.DemoRenderer;
import com.lanshifu.opengldemo.renderer.TexttureRenderer;

public class MainActivity extends AppCompatActivity {


    GLSurfaceView mGLSurfaceView;
    private DemoRenderer mRenderer;

    public static final int TYPE_TRANGLE = 0;
    public static final int TYPE_GESTURE = 1;

    private int type;

    public static void start(Activity context, int type){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        type = getIntent().getIntExtra("type",TYPE_TRANGLE);

        mGLSurfaceView = findViewById(R.id.glsurfaceview);

        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.A
            mGLSurfaceView.setEGLContextClientVersion(2);
            setSimpleRender();
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
        }
    }


    private void setSimpleRender() {
        switch (type){
            case TYPE_TRANGLE:
                mRenderer = new DemoRenderer();// 三角形
                mGLSurfaceView.setRenderer(mRenderer);
                break;

            case TYPE_GESTURE:
                TexttureRenderer texttureRenderer = new TexttureRenderer(this);//图片
                mGLSurfaceView.setRenderer(texttureRenderer);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

}
