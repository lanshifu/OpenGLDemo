package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Arrays;

/**
 * 封装了Camera2 API，打开相机，打开预览都封装起来
 */
public class CameraV2 {
    public static final String TAG = "CameraV2";

    private Activity mActivity;
    private CameraDevice mCameraDevice;
    private String mCameraId;
    private Size mPreviewSize;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private String[] mCameraIdList;
    boolean mStartPreview = false;

    SurfaceTexture mSurfaceTexture;

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.mSurfaceTexture = surfaceTexture;
    }

    public CameraV2(Activity activity) {
        mActivity = activity;
        //1.启动Camera线程
        startCameraThread();

        //2.准备Camera，获取cameraId，获取Camera预览大小
        setupCamera();

        //打开Camera
        openCamera();
    }

    /**
     * 相机设置
     * @return
     */
    public String setupCamera() {
        CameraManager cameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraIdList = cameraManager.getCameraIdList();
            Log.d(TAG, "setupCamera: mCameraIdList:"+mCameraIdList.length);
            for (String id : mCameraIdList) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
                mCameraId = id;
                Log.d(TAG, "setupCamera: preview width = " + mPreviewSize.getWidth() + ", height = " + mPreviewSize.getHeight() + ", cameraId = " + mCameraId);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return mCameraId;
    }


    /**
     * 转换摄像头的时候调用，设置相机Id
     * @param cameraId
     * @return
     */
    public boolean setCameraId(String cameraId){
        for (String containCamera : mCameraIdList) {
            if (containCamera.equals(cameraId)){
                mCameraId = cameraId;
                return true;
            }
        }
        Log.e(TAG, "setCameraId: camera id not exit" );
        return false;
    }



    public void startCameraThread() {
        mCameraThread = new HandlerThread("CameraThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    public boolean openCamera() {
        CameraManager cameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
            cameraManager.openCamera(mCameraId, mStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeCamea(){
        if (mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }


    public CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    /**
     * 启动预览，需要传 SurfaceTexture
     */
    public void startPreview() {
        if (mStartPreview || mSurfaceTexture == null || mCameraDevice == null){
            return;
        }
        mStartPreview = true;

        //给 SurfaceTexture 设置默认大小，mPreviewSize是相机预览大小
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        //Surface 需要接收一个 SurfaceTexture
        Surface surface = new Surface(mSurfaceTexture);
        try {
            // 通过CameraDevice创建request
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //surface 作为输出的目标，预览的数据会传到 Surface 中
            mCaptureRequestBuilder.addTarget(surface);
            //创建会话， surface 这里传进去，然后只需关心回调
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        CaptureRequest mCaptureRequest = mCaptureRequestBuilder.build();
                        //设置一直重复捕获图像，不设置就只有一帧，没法预览
                        session.setRepeatingRequest(mCaptureRequest, null, mCameraHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview(){

    }


}
