package com.lanshifu.opengldemo.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lanshifu.opengldemo.R;
import com.lanshifu.opengldemo.utils.ShaderManager;

import java.io.IOException;

public class ImageFilterActivity extends AppCompatActivity {

    GLSurfaceView mGLSurfaceView;
    private FilterRenderer mFilterRenderer;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.glsurfaceview);

        mGLSurfaceView.setEGLContextClientVersion(2);

        initBitmap();

        setSimpleRender();


    }

    private void initBitmap() {
        try {
            mBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("picture.png"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("lxb", "onSurfaceCreated: " + e.getMessage());
        }
        if (mBitmap == null) {
            Log.e("lxb", "initTexture: mBitmap == null");
        }
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
                mFilterRenderer.setType(ShaderManager.BASE_SHADER);
                break;
            case R.id.mGray:
                mFilterRenderer.setType(ShaderManager.GRAY_SHADER);
                break;
            case R.id.mCool:
                mFilterRenderer.setType(ShaderManager.COOL_SHADER);
                break;
            case R.id.mWarm:
                mFilterRenderer.setType(ShaderManager.WARM_SHADER);
                break;
            case R.id.mBlur:
                mFilterRenderer.setType(ShaderManager.BUZZY_SHADER);
                break;
            case R.id.mMagn:
                mFilterRenderer.setType(ShaderManager.ZOOM_SHADER);
                break;
            case R.id.mFour:
                mFilterRenderer.setType(ShaderManager.FOUR_SHADER);
                break;
            case R.id.light:
                mFilterRenderer.setType(ShaderManager.LIGHT_SHADER);
                break;

//                mGLView.getRender().getFilter().setHalf(isHalf);
//                mGLView.requestRender();
        }
        return super.onOptionsItemSelected(item);

    }

    private void setSimpleRender() {
        //图片
        mFilterRenderer = new FilterRenderer(this);
        mGLSurfaceView.setRenderer(mFilterRenderer);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFilterRenderer.onDestroy();
    }
}
