package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.lanshifu.opengldemo.R;

public class Camera2Demo_SurfaceView_Activity extends AppCompatActivity {

    private CameraV2GLSurfaceView mCameraV2GLSurfaceView;
    private CameraV2 mCamera;
    private int mWidthPixels;
    private int mHeightPixels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraV2GLSurfaceView = new CameraV2GLSurfaceView(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mCamera = new CameraV2(this);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
        mCamera.setupCamera(mWidthPixels, mHeightPixels);
        if (!mCamera.openCamera()) {
            return;
        }
        mCameraV2GLSurfaceView.init(mCamera, false, this);
        setContentView(mCameraV2GLSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_camera:

                mCamera.closeCamea();
                if (mCamera.getCameraId().equals("0")){
                    mCamera.setCameraId("1");
                }else {
                    mCamera.setCameraId("0");
                }

                if (!mCamera.openCamera()) {
                    return true;
                }
                mCameraV2GLSurfaceView = new CameraV2GLSurfaceView(this);
                mCameraV2GLSurfaceView.init(mCamera, false, this);
                setContentView(mCameraV2GLSurfaceView);

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
    }
}
