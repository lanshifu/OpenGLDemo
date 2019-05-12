package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lanshifu.opengldemo.R;
import com.lanshifu.opengldemo.utils.ShaderManager;

public class Camera2Demo_SurfaceView_Activity extends AppCompatActivity {

    private static final String TAG = "Camera2Demo_SurfaceView";

    private int mWidthPixels;
    private int mHeightPixels;
    private GLSurfaceView mCameraV2GLSurfaceView;
    private CameraV2Renderer mCameraV2Renderer;
    private CameraV2 mCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;

        setContentView(R.layout.camera2_surfaceview_activity);
        initView();
    }

    private void initView() {
        //Camera2 API 进行封装
        mCamera = new CameraV2(this);

        mCameraV2GLSurfaceView = findViewById(R.id.glsurfaceview);
        mCameraV2GLSurfaceView.setEGLContextClientVersion(2);

        mCameraV2Renderer = new CameraV2Renderer();
        mCameraV2Renderer.init(mCameraV2GLSurfaceView, mCamera, this);
        mCameraV2GLSurfaceView.setRenderer(mCameraV2Renderer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_camera:

//                mCamera.closeCamea();
//                if (mCamera.getCameraId().equals("0")) {
//                    mCamera.setCameraId("1");
//                } else {
//                    mCamera.setCameraId("0");
//                }
//
//                if (!mCamera.openCamera()) {
//                    return true;
//                }
//
//                mCamera = new CameraV2(this, mWidthPixels, mHeightPixels);
//                mCameraV2Renderer = new CameraV2Renderer();
//                mCameraV2Renderer.init(mGLSurfaceView, mCamera, false, this);
//                mGLSurfaceView.setRenderer(mCameraV2Renderer);

                break;

            case R.id.mDefault:
                mCameraV2Renderer.setType(ShaderManager.BASE_SHADER);
                break;
            case R.id.mGray:
                mCameraV2Renderer.setType(ShaderManager.GRAY_SHADER);
                break;
            case R.id.mCool:
                mCameraV2Renderer.setType(ShaderManager.COOL_SHADER);
                break;
            case R.id.mWarm:
                mCameraV2Renderer.setType(ShaderManager.WARM_SHADER);
                break;
            case R.id.mBlur:
                mCameraV2Renderer.setType(ShaderManager.BUZZY_SHADER);
                break;
            case R.id.mMagn:
                mCameraV2Renderer.setType(ShaderManager.ZOOM_SHADER);
                break;
            case R.id.mFour:
                mCameraV2Renderer.setType(ShaderManager.FOUR_SHADER);
                break;
            case R.id.light:
                mCameraV2Renderer.setType(ShaderManager.LIGHT_SHADER);
                break;

        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.closeCamea();
        }
    }

    public void takePicture(View view) {
        Log.d(TAG, "onclick takePicture: ");
    }
}
