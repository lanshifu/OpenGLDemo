package com.lanshifu.opengldemo.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lanshifu.opengldemo.R;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * 先搞个demo
 */
public class Camera2DemoActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 10;
    private static final String TAG = "Camera2DemoActivity";
    SurfaceView mSurfaceView;

    private PreviewGLSurfaceView mPreviewGlSurfaceView;

    private CameraDevice mDevice;
    private CameraManager mCameraManager;
    private String mCameraId;
    private HandlerThread mThread;
    private Handler mHandler;
    private Size mPreviewSize;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;
    private Camera2Renderer mRenderer;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_camera2);

        mFrameLayout = findViewById(R.id.fl_container);
        mSurfaceView = findViewById(R.id.mSurface);

        requestPermission();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("CameraNew", "Lacking privileges to access camera service, please request permission first.");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CAMERA_PERMISSION);
        } else {
            init();
        }
    }

    private void init() {
        Log.d(TAG, "init: ");
        mThread = new HandlerThread("camera2 ");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);


        mPreviewGlSurfaceView = new PreviewGLSurfaceView(this);
        mRenderer = new Camera2Renderer(this, mPreviewGlSurfaceView);
        mPreviewGlSurfaceView.setEGLContextClientVersion(2);
        mPreviewGlSurfaceView.setRenderer(mRenderer);
        mFrameLayout.addView(mPreviewGlSurfaceView,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);


        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated: ");
                getCameraId();
                openCamera();
//                mPreviewGlSurfaceView.surfaceCreated(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: ");
//                mPreviewGlSurfaceView.surfaceChanged(holder,format,width,height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed: ");
//                mPreviewGlSurfaceView.surfaceDestroyed(holder);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Permission denied
                Toast.makeText(this, "User denied permission, can't use take photo feature.",
                        Toast.LENGTH_SHORT).show();
            } else {
                init();
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        mPreviewGlSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreviewGlSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreviewGlSurfaceView.surfaceDestroyed(null);
        mPreviewGlSurfaceView.detachedFromWindow();
    }

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.d(TAG, "onCaptureCompleted: ");
            if (mPreviewGlSurfaceView != null) {
                //刷新
                mPreviewGlSurfaceView.requestRender();
            }
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            Log.d(TAG, "onOpened: ");
            mCameraDevice = cameraDevice;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            Log.d(TAG, "onDisconnected: ");
            mCameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {
            Log.d(TAG, "onError: ");
            mCameraDevice.close();
            mCameraDevice = null;
        }
    };

    private void getCameraId() {
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                mCameraId = cameraId;
                Log.d(TAG, "getCameraId: " + mCameraId);
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            mCameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void createCameraPreview() {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            //自定义规则，选个大小
            Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            mPreviewSize = sizes[0];


            Log.e("CameraActivity", "OptimalSize width: " + mPreviewSize.getWidth() + " height: " + mPreviewSize.getHeight());
            // TODO: 2019/4/29  这个就关联到 TextureView 了，我们要用GLSurfaceView渲染，所以指定一个新的texture
            Surface surface = new Surface(mRenderer.getTexture());
            final CaptureRequest.Builder mBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) {
                        return;
                    }
                    mSession = cameraCaptureSession;
                    mBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
                        mSession.setRepeatingRequest(mBuilder.build(), mSessionCaptureCallback, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.e(TAG, "onConfigureFailed");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



}
