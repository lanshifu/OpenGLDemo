package com.lanshifu.opengldemo.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.lanshifu.opengldemo.image.FilterRenderer;
import com.lanshifu.opengldemo.utils.EasyGlUtils;
import com.lanshifu.opengldemo.utils.GLUtil;
import com.lanshifu.opengldemo.utils.MatrixUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class CameraPreview {

    private static final String TAG = "CameraPreview";
    // 顶点着色器的代码
    String vertexShaderCode;

    // 片元着色器的代码
    String fragmentShaderCode;

    private FloatBuffer mVertexBuffer;  //顶点坐标数据要转化成FloatBuffer格式
    private FloatBuffer mTexCoordBuffer;//顶点纹理坐标缓存


    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    private float[] mCoordOM=new float[16];
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];


    //变换矩阵，提供set方法
    private float[] mvpMatrix = new float[16];
    private FilterRenderer.Filter mFilter;

    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    private int textureType=0;      //默认使用Texture2D0
    private int mTextureId;

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    private SurfaceTexture mSurfaceTexture;

    public void setFilter(FilterRenderer.Filter filter) {
        this.mFilter = filter;
    }

    private float mxy;
    public void setMxy(float mxy) {
        this.mxy = mxy;
    }

    private Context mContext;
    private Bitmap mBitmap;

    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }

    public CameraPreview(Context context){

        this.mContext = context;
        initVertext();
        initShder();
        onBindTexture();
//        initTexture();
    }
    private void initShder() {
        //获取程序，封装了加载、链接等操作
        vertexShaderCode = GLUtil.loadFromAssetsFile("shader/oes_base_vertex.glsl", mContext.getResources());
        fragmentShaderCode = GLUtil.loadFromAssetsFile("shader/oes_base_fragment.glsl", mContext.getResources());


        mProgram = GLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        /***1.获取句柄*/
//        // 获取顶点着色器的位置的句柄（这里可以理解为当前绘制的顶点位置）
//        vPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//        // 获取变换矩阵的句柄
//        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//        //纹理位置句柄
//        maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");

        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord=GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
        Log.d(TAG, "initShder: mHPosition="+mHPosition);
        Log.d(TAG, "initShder: mHCoord="+mHCoord);
        Log.d(TAG, "initShder: mHMatrix="+mHMatrix);
        Log.d(TAG, "initShder: mHTexture="+mHTexture);

    }

    private void initVertext() {
        float vertices[] = new float[]{
                -1, 1, 0,
                -1, -1, 0,
                1, 1, 0,
                1, -1, 0,

        };//顶点位置

        float[] colors = new float[]{
                0, 0,
                0, 1,
                1, 0,
                1, 1,

        };//纹理顶点数组
        mVertexBuffer = GLUtil.floatArray2FloatBuffer(vertices);
        mTexCoordBuffer = GLUtil.floatArray2FloatBuffer(colors);
    }



    public void draw() {
        boolean a=GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if(a){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if(mSurfaceTexture!=null){
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mCoordOM);
        }
        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);



        onClear();
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    private int createTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    private void initTexture() {
//        int textures[] = new int[1]; //生成纹理id
//
//        GLES20.glGenTextures(  //创建纹理对象
//                1, //产生纹理id的数量
//                textures, //纹理id的数组
//                0  //偏移量
//        );
//        mTextureId = textures[0];

        //绑定纹理id，将对象绑定到环境的纹理单元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);//设置MIN 采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);//设置MAG采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        if (mBitmap == null) {
            Log.e("lxb", "initTexture: mBitmap == null");
            return;
        }
        //加载图片
        GLUtils.texImage2D( //实际加载纹理进显存
                GLES20.GL_TEXTURE_2D, //纹理类型
                0, //纹理的层次，0表示基本图像层，可以理解为直接贴图
                mBitmap, //纹理图像
                0 //纹理边框尺寸
        );
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture,textureType);
    }

    public final int getTextureId(){
        return mTextureId;
    }
}
