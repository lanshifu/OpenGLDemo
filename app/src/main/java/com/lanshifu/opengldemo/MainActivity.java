package com.lanshifu.opengldemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.lanshifu.opengldemo.renderer.DemoRenderer;
import com.lanshifu.opengldemo.renderer.SquareRenderer;
import com.lanshifu.opengldemo.renderer.TexttureRenderer;

public class MainActivity extends AppCompatActivity {


    GLSurfaceView mGLSurfaceView;
    private DemoRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        }else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
        }
    }





    private void setSimpleRender(){
//        SimpleRender simpleRender = new SimpleRender(DemoSurFaceViewActivity.this);
        mRenderer = new DemoRenderer();// 三角形
//        renderer = new Triangle2();  //直角等腰彩色三角形

        TexttureRenderer texttureRenderer = new TexttureRenderer(this);
        mGLSurfaceView.setRenderer(texttureRenderer);
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
