package com.lanshifu.opengldemo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lanshifu.opengldemo.image.FilterRenderer;
import com.lanshifu.opengldemo.renderer.VaryRenderer;

public class VaryActivity extends AppCompatActivity {

    GLSurfaceView mGLSurfaceView;
    private VaryRenderer mFilterRenderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsurfaceview);

        mGLSurfaceView.setEGLContextClientVersion(2);
        setSimpleRender();
    }




    private void setSimpleRender() {
        //图片
        mFilterRenderer = new VaryRenderer();
        mGLSurfaceView.setRenderer(mFilterRenderer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFilterRenderer.onDestroy();
    }
}
