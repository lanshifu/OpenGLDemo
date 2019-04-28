package com.lanshifu.opengldemo.preview;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lanshifu.opengldemo.R;
import com.lanshifu.opengldemo.image.FilterRenderer;

public class PreviewActivity extends AppCompatActivity {

    GLSurfaceView mGLSurfaceView;
    private FilterRenderer mFilterRenderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsurfaceview);

        mGLSurfaceView.setEGLContextClientVersion(2);
        setSimpleRender();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mDefault:
                mFilterRenderer.setType(FilterRenderer.Filter.NONE);
                break;
            case R.id.mGray:
                mFilterRenderer.setType(FilterRenderer.Filter.GRAY);
                break;
            case R.id.mCool:
                mFilterRenderer.setType(FilterRenderer.Filter.COOL);
                break;
            case R.id.mWarm:
                mFilterRenderer.setType(FilterRenderer.Filter.WARM);
                break;
            case R.id.mBlur:
                mFilterRenderer.setType(FilterRenderer.Filter.BLUR);
                break;
            case R.id.mMagn:
                mFilterRenderer.setType(FilterRenderer.Filter.MAGN);
                break;
            case R.id.mFour:
                mFilterRenderer.setType(FilterRenderer.Filter.FOUR);
                break;

//                mGLView.getRender().getFilter().setHalf(isHalf);
//                mGLView.requestRender();
        }
        return super.onOptionsItemSelected(item);

    }

    private void setSimpleRender() {
        //图片
        mFilterRenderer = new FilterRenderer(this);
        mFilterRenderer.setType(FilterRenderer.Filter.NONE);
        mGLSurfaceView.setRenderer(mFilterRenderer);
    }
}
