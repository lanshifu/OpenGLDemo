/*
 *
 * Camera2Activity.java
 *
 * Created by Wuwang on 2017/3/6
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.lanshifu.opengldemo.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.lanshifu.opengldemo.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;


/**
 * Description:
 */
public class Camera2Activity extends AppCompatActivity implements FrameCallback {

    private SurfaceView mSurfaceView;
    private TextureController mController;
    private Renderer mRenderer;
    private int cameraId = 1;

    private static final int REQUEST_PERMISSION_CODE = 315;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest
                    .permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }

    }


    protected void onFilterSet(TextureController controller) {
//        ZipPkmAnimationFilter mAniFilter=new ZipPkmAnimationFilter(getResources());
//        mAniFilter.setAnimation("assets/etczip/cc.zip");
//        controller.addFilter(mAniFilter);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_camera2);
    }

    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {

            //设置数据源
            mRenderer = new Camera2Renderer();
            setContentView();
            mSurfaceView = (SurfaceView) findViewById(R.id.mSurface);
            mController = new TextureController(Camera2Activity.this);
//            WaterMarkFilter filter=new WaterMarkFilter(getResources());
//            filter.setWaterMark(BitmapFactory.decodeResource(getResources(),R.mipmap.logo));
//            filter.setPosition(300,50,300,150);
//            mController.addFilter(filter);
            onFilterSet(mController);
            mController.setFrameCallback(720, 1280, Camera2Activity.this);
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    //将回调交给 TextureController
                    mController.surfaceCreated(holder);
                    mController.setRenderer(mRenderer);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mController.surfaceChanged(width, height);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mController.surfaceDestroyed();
                }
            });

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        runOnUiThread(initViewRunnable);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mShutter:
                mController.takePhoto();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mController != null) {
            mController.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mController != null) {
            mController.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.destroy();
        }
    }

    @Override
    public void onFrame(final byte[] bytes, long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
                ByteBuffer b = ByteBuffer.wrap(bytes);
                bitmap.copyPixelsFromBuffer(b);
                saveBitmap(bitmap);
                bitmap.recycle();
            }
        }).start();
    }

    protected String getSD() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //图片保存
    public void saveBitmap(Bitmap b) {
        String path = getSD() + "/OpenGLDemo/photo/";
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Camera2Activity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Camera2Activity.this, "保存成功->" + jpegName, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class Camera2Renderer implements Renderer {

        CameraDevice mDevice;
        CameraManager mCameraManager;
        private HandlerThread mThread;
        private Handler mHandler;
        private Size mPreviewSize;

        Camera2Renderer() {
            mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            mThread = new HandlerThread("camera2 ");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
        }

        @Override
        public void onDestroy() {
            if (mDevice != null) {
                mDevice.close();
                mDevice = null;
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try {
                if (mDevice != null) {
                    mDevice.close();
                    mDevice = null;
                }
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
                StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
                //自定义规则，选个大小
                mPreviewSize = sizes[0];
                mController.setDataSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {
                        mDevice = camera;
                        try {
                            Surface surface = new Surface(mController.getTexture());
                            final CaptureRequest.Builder builder = mDevice.createCaptureRequest(TEMPLATE_PREVIEW);
                            builder.addTarget(surface);
                            mController.getTexture().setDefaultBufferSize(
                                    mPreviewSize.getWidth(), mPreviewSize.getHeight());
                            mDevice.createCaptureSession(Arrays.asList(surface), new
                                    CameraCaptureSession.StateCallback() {
                                        @Override
                                        public void onConfigured(CameraCaptureSession session) {
                                            try {
                                                session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                                                    @Override
                                                    public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                                        super.onCaptureProgressed(session, request, partialResult);
                                                    }

                                                    @Override
                                                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                        super.onCaptureCompleted(session, request, result);
                                                        mController.requestRender();
                                                    }
                                                }, mHandler);
                                            } catch (CameraAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onConfigureFailed(CameraCaptureSession session) {

                                        }
                                    }, mHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {
                        mDevice = null;
                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {

                    }
                }, mHandler);
            } catch (SecurityException | CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }
}
