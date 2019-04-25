package com.lanshifu.opengldemo.image;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lanshifu.opengldemo.R;

public class ImageFilterActivity extends AppCompatActivity {

    GLSurfaceView mGLSurfaceView;

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
        getMenuInflater().inflate(R.menu.menu_filter,menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.mDeal:
//
////                mGLView.getRender().getFilter().setHalf(isHalf);
////                mGLView.requestRender();
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

    private void setSimpleRender() {
        FilterRenderer filterRenderer = new FilterRenderer(this);//图片
        mGLSurfaceView.setRenderer(filterRenderer);
    }
}
