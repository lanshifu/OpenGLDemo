package com.lanshifu.opengldemo.camera;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.lanshifu.opengldemo.utils.EasyGlUtils;
import com.lanshifu.opengldemo.utils.MatrixUtils;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.CAMERA_SERVICE;

public class Camera2Renderer implements GLSurfaceView.Renderer {
    private static final String TAG = "Camera2Renderer";
    private final GLSurfaceView mGLSurfaceView;
    private Context mContext;
    private CameraDevice mDevice;
    private CameraManager mCameraManager;
    private String mCameraId;
    private HandlerThread mThread;
    private Handler mHandler;
    private Size mPreviewSize;
    private CameraDevice mCameraDevice;
    private int[] mCameraTexture = new int[1];  //相机的纹理
    private SurfaceTexture mSurfaceTexture;
    private CameraPreview mCameraPreview;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    private CameraCaptureSession mSession;
    private Point mWindowSize = new Point();      //输出视图的大小
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式
    //变换矩阵，提供set方法
    private float[] mvpMatrix = new float[16];
    private int[] fTexture = new int[1];
    private Point mDataSize = new Point();

    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }



    Camera2Renderer(Context context, GLSurfaceView glSurfaceView) {
        mCameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        mContext = context;
        mGLSurfaceView = glSurfaceView;
        mThread = new HandlerThread("camera2 ");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        mDataSize = new Point(720, 1280);

        mWindowSize = new Point(720, 1280);

        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);

    }

    public SurfaceTexture getTexture(){
        return mSurfaceTexture;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: ");


        int mOESTextureId = createOESTextureObject();
        initSurfaceTexture(mOESTextureId);

        mCameraPreview = new CameraPreview(mContext);


    }

    /**
     * 之后根据OES纹理Id创建SurfaceTexture，用来接收Camera2的预览数据。
     * @param mOESTextureId
     * @return
     */
    public boolean initSurfaceTexture(int mOESTextureId) {
        //根据OES纹理ID实例化SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        //当SurfaceTexture接收到一帧数据时，请求OpenGL ES进行渲染
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGLSurfaceView.requestRender();
            }
        });
        return true;
    }

    /**
     * 创建一个OES纹理
     * @return
     */
    public static int createOESTextureObject() {
        int[] tex = new int[1];
        GLES20.glGenTextures(1, tex, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mWindowSize.x = width;
        this.mWindowSize.y = height;

        MatrixUtils.getMatrix(mvpMatrix, mShowType, mDataSize.x, mDataSize.y, width, height);
        //todo
        EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);

        GLES20.glViewport(0,0,width,height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        //显示传入的texture上，一般是显示在屏幕上

        if (mSurfaceTexture != null) {
            //更新数据，其实也是消耗数据，将上一帧的数据处理或者抛弃掉，要不然SurfaceTexture是接收不到最新数据
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mvpMatrix);
        }


        mCameraPreview.setMvpMatrix(mvpMatrix);
        mCameraPreview.setTextureId(mCameraTexture[0]);
        mCameraPreview.setSurfaceTexture(mSurfaceTexture);
        mCameraPreview.draw();
    }




    //数据的大小
    //在Surface创建前，应该被调用
    public void setDataSize(int width, int height) {
        mDataSize.x = width;
        mDataSize.y = height;
    }


    protected void onResume() {
    }

    protected void onPause() {
        if (mDevice != null) {
            mDevice.close();
        }
    }

    protected void onDestroy() {
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
    }
}
