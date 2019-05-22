package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2BaseFilter;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterBuzz;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterCool;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterDouyinOut;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterFour;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterGray;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterLight;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterNone;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterWarm;
import com.lanshifu.opengldemo.camera.camera2_surface_demo.filter.Camera2FilterZoom;
import com.lanshifu.opengldemo.utils.GLUtil;
import com.lanshifu.opengldemo.utils.ShaderManager;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES20.glViewport;

public class CameraV2Renderer implements GLSurfaceView.Renderer {
    public static final String TAG = "CameraV2Renderer";
    private Context mContext;
    GLSurfaceView mGLSurfaceView;
    CameraV2 mCamera;
    private int mTextureId = -1;
    private SurfaceTexture mSurfaceTexture;
    private float[] mTransformMatrix = new float[16];

    private boolean mSwitchCamera;


    //回调数据的宽，init 赋值
    public int mFrameCallbackWidth = 720;
    public int mFrameCallbackHeight = 1080;
    //用于存储拍照回调数据的buffer，大小是宽乘以高
    private IntBuffer mOutPutIntBuffer;


    Camera2BaseFilter mCamera2BaseFilter;

    public void init(GLSurfaceView surfaceView, CameraV2 camera, Context context) {
        mContext = context;
        mGLSurfaceView = surfaceView;
        mCamera = camera;
        //预览大小
        mFrameCallbackWidth = camera.mPreviewSize.getWidth();
        mFrameCallbackHeight = camera.mPreviewSize.getHeight();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ShaderManager.init(mContext);
        initSurfaceTexture();
        mCamera2BaseFilter = new Camera2FilterNone(mContext, mTextureId);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Log.i(TAG, "onSurfaceChanged: " + width + ", " + height);
        mFrameCallbackWidth = width;
        mFrameCallbackHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //切换相机，需要重新绑定纹理id
        if (mSwitchCamera) {
            mSwitchCamera = false;
            initSurfaceTexture();
            updateFilterView();
        }

        //mSurfaceTexture.updateTexImage()更新预览上的图像
        mSurfaceTexture.updateTexImage();
        //直接通过 SurfaceTexture 获取变换矩阵
        mSurfaceTexture.getTransformMatrix(mTransformMatrix);

        if (mFilterChange) {
            updateFilterView();
        }

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        mCamera2BaseFilter.draw(mTransformMatrix);

        if (mTakePicture) {
            doTakePicture();
        }

    }

    //创建 SurfaceTexture，CameraV2.startPreview()方法需要SurfaceTexture
    public void initSurfaceTexture() {
        //1、获取一个纹理id
        mTextureId = GLUtil.getOESTextureId();
        //2、纹理id设置到 SurfaceTexture 中，
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        //图片数据固定的，而摄像头数据是变换的，所以每当摄像头有新的数据来时，我们需要通过surfaceTexture.updateTexImage()更新预览上的图像
        // updateTexImage 不应该在OnFrameAvailableLister的回调方法中直接调用，而应该在onDrawFrame中执行。而调用requestRender，可以触发onDrawFrame
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.d(TAG, "onFrameAvailable: ");
                mGLSurfaceView.requestRender();
            }
        });

        //将 SurfaceTexture 设置给CameraV2,然后调用startPreview
        mCamera.setSurfaceTexture(mSurfaceTexture);
        mCamera.startPreview();
    }


    boolean mFilterChange = false;
    int mFilterType;

    public void setType(int filterType) {
        if (this.mFilterType == filterType) {
            Log.d(TAG, "setType: this.mFilterType == mFilterType");
            return;
        }
        this.mFilterType = filterType;
        mFilterChange = true;
    }

    void updateFilterView() {
        Log.d(TAG, "updateFilterView: ");
        mCamera2BaseFilter = null;
        switch (this.mFilterType) {
            case ShaderManager.GRAY_SHADER:
                mCamera2BaseFilter = new Camera2FilterGray(mContext, mTextureId);
                break;
            case ShaderManager.BASE_SHADER:
                mCamera2BaseFilter = new Camera2FilterNone(mContext, mTextureId);
                break;
            case ShaderManager.WARM_SHADER:
                mCamera2BaseFilter = new Camera2FilterWarm(mContext, mTextureId);
                break;
            case ShaderManager.COOL_SHADER:
                mCamera2BaseFilter = new Camera2FilterCool(mContext, mTextureId);
                break;
            case ShaderManager.BUZZY_SHADER:
                mCamera2BaseFilter = new Camera2FilterBuzz(mContext, mTextureId);
                break;
            case ShaderManager.FOUR_SHADER:
                mCamera2BaseFilter = new Camera2FilterFour(mContext, mTextureId);
                break;
            case ShaderManager.ZOOM_SHADER:
                mCamera2BaseFilter = new Camera2FilterZoom(mContext, mTextureId);
                break;
            case ShaderManager.LIGHT_SHADER:
                mCamera2BaseFilter = new Camera2FilterLight(mContext, mTextureId);
                break;
            case ShaderManager.CAMERA_BUZZ_DOUYIN_OUT:
                mCamera2BaseFilter = new Camera2FilterDouyinOut(mContext, mTextureId);
                break;
            default:
                mCamera2BaseFilter = new Camera2FilterNone(mContext, mTextureId);
                break;
        }
        mFilterChange = false;
    }


    /**
     * 拍照部分
     */
    private boolean mTakePicture = false;

    public void takePicture() {
        mTakePicture = true;
    }

    private void doTakePicture() {
        Log.d(TAG, "doTakePicture: mFrameCallbackWidth= "+ mFrameCallbackWidth + " ,mFrameCallbackHeight=" +mFrameCallbackHeight);
        mTakePicture = false;
        if (mOutPutIntBuffer == null) {
            mOutPutIntBuffer = IntBuffer.allocate(mFrameCallbackWidth *
                    mFrameCallbackHeight);
        }

        //todo 这里可以用离屏渲染的方式，增加水印，后面再加
//        mCamera2BaseFilter.draw(mTransformMatrix);

        //通过这个api获取画的这一帧数据
        GLES20.glReadPixels(0, 0, mFrameCallbackWidth, mFrameCallbackHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mOutPutIntBuffer);

        //获取到图片数据了
        int[] origin = mOutPutIntBuffer.array();
        int[] result = new int[mFrameCallbackWidth * mFrameCallbackHeight];
        //解决方向颠倒问题
        for (int i = 0; i < mFrameCallbackHeight; i++) {
            for (int j = 0; j < mFrameCallbackWidth; j++) {
                int pix = origin[i * mFrameCallbackWidth + j];
                int pb = (pix >> 16) & 0xff;
                int pr = (pix << 16) & 0x00ff0000;
                int pix1 = (pix & 0xff00ff00) | pr | pb;
                result[(mFrameCallbackHeight - i - 1) * mFrameCallbackWidth + j] = pix1;
            }
        }

        //帧数据回调, Activity中做保存图片操作
        if (mCameraCallback != null) {
            mCameraCallback.onFrameCallBack(result);
        }
    }


    CameraCallback mCameraCallback;

    public void setCameraCallback(CameraCallback cameraCallback) {
        mCameraCallback = cameraCallback;
    }

    public interface CameraCallback {

        //帧数据回调
        void onFrameCallBack(int[] bytes);
    }
}
