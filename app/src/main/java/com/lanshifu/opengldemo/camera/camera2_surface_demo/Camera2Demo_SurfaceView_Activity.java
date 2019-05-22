package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lanshifu.opengldemo.R;
import com.lanshifu.opengldemo.utils.ShaderManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public class Camera2Demo_SurfaceView_Activity extends AppCompatActivity implements CameraV2Renderer.CameraCallback {

    private static final String TAG = "Camera2Demo_SurfaceView";

    private int mWidthPixels;
    private int mHeightPixels;
    private GLSurfaceView mCameraV2GLSurfaceView;
    private CameraV2Renderer mCameraV2Renderer;
    private CameraV2 mCamera;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidthPixels = dm.widthPixels;
        mHeightPixels = dm.heightPixels;
        Log.d(TAG, "onCreate: mWidthPixels = " + mWidthPixels + ", mHeightPixels= " + mHeightPixels);

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
        mCameraV2Renderer.setCameraCallback(this);
        mCameraV2GLSurfaceView.setRenderer(mCameraV2Renderer);

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

//                mCamera.closeCamea();
//                if (mCamera.getCameraId().equals("0")) {
//                    mCamera.setCameraId("1");
//                } else {
//                    mCamera.setCameraId("0");
//                }
//                mCamera.openCamera();
//
//                mCameraV2Renderer.setSwitchCamera(true);

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

            case R.id.soul_out:
                mCameraV2Renderer.setType(ShaderManager.CAMERA_BUZZ_DOUYIN_OUT);
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
        mCameraV2Renderer.takePicture();
    }


    //拍照帧数据回调
    @Override
    public void onFrameCallBack(final int[] bytes) {

        final int width = mCameraV2Renderer.mFrameCallbackWidth;
        final int height = mCameraV2Renderer.mFrameCallbackHeight;
        Log.d(TAG, "onFrameCallBack: bitmap width = " + width);
        Log.d(TAG, "onFrameCallBack: bitmap height = " + height);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                IntBuffer byteBuffer = IntBuffer.wrap(bytes);
                bitmap.copyPixelsFromBuffer(byteBuffer);
                saveBitmap(bitmap);
                bitmap.recycle();
            }
        }).start();
    }


    //图片保存
    public void saveBitmap(Bitmap bitmap) {
        Log.d(TAG, "saveBitmap: ");
        String path = getSD() + "/OpenGLDemo/photo/";
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            Log.e(TAG, "saveBitmap: 无法保存照片");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Camera2Demo_SurfaceView_Activity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + "_" + dataTake + ".jpg";
        try {
            FileOutputStream fos = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final long takeTime = System.currentTimeMillis() - dataTake;
        Log.w(TAG, "saveBitmap: 保存成功,耗时：" + takeTime);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Camera2Demo_SurfaceView_Activity.this, "保存成功,路径：" + jpegName + "，耗时：" + takeTime, Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected String getSD() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

}
