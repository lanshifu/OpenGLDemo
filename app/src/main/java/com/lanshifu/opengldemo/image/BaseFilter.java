package com.lanshifu.opengldemo.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.lanshifu.opengldemo.utils.GLUtil;

import java.nio.FloatBuffer;

/**
 * 滤镜的基类
 * <p>
 * 加载不同的着色器就有不同滤镜效果
 */
public class BaseFilter {
    private static final String TAG = "BaseFilterView";

    // 顶点着色器的代码
    String vertexShaderCode;
    // 片元着色器的代码
    String fragmentShaderCode;

    private FloatBuffer mVertexBuffer;  //顶点坐标数据要转化成FloatBuffer格式
    private FloatBuffer mTexCoordBuffer;//顶点纹理坐标缓存


    // 数组中每3个值作为一个坐标点
    static final int COORDS_PER_VERTEX = 3;

    //一个顶点有3个float，一个float是4个字节，所以一个顶点要12字节
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per mVertex

    //当前绘制的顶点位置句柄
    protected int vPositionHandle;
    //变换矩阵句柄
    protected int mMVPMatrixHandle;
    //这个可以理解为一个OpenGL程序句柄
    protected int mProgram;
    //纹理坐标句柄
    protected int mTexCoordHandle;

    //变换矩阵，提供set方法
    private final float[] mProjectMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    protected int mTextureId;

    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }

    private Context mContext;

    private Bitmap mBitmap;

    public BaseFilter(Context context, Bitmap bitmap) {
        mContext = context;
        this.mBitmap = bitmap;

        initVertext();
        initShder();
        initTexture();
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

    private void initShder() {
        Log.d(TAG, "initShder: start");
        //获取程序，封装了加载、链接等操作
        vertexShaderCode = GLUtil.loadFromAssetsFile(getVertexCode(), mContext.getResources());
        fragmentShaderCode = GLUtil.loadFromAssetsFile(getFragmentCode(), mContext.getResources());
        mProgram = GLUtil.createProgram(vertexShaderCode, fragmentShaderCode);

        /***1.获取句柄*/
        // 获取顶点着色器的位置的句柄（这里可以理解为当前绘制的顶点位置）
        vPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        // 获取变换矩阵的句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //纹理位置句柄
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");

        Log.d(TAG, "initShder: end vPositionHandle= " + mTexCoordHandle + ",mTexCoordHandle=" + mTexCoordHandle + ",mMVPMatrixHandle=" + mMVPMatrixHandle);
    }


    protected int initTexture() {
        Log.d(TAG, "initTexture: start");
        int textures[] = new int[1]; //生成纹理id

        GLES20.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                textures, //纹理id的数组
                0  //偏移量
        );
        mTextureId = textures[0];

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
            return -1;
        }
        //加载图片
        GLUtils.texImage2D( //实际加载纹理进显存
                GLES20.GL_TEXTURE_2D, //纹理类型
                0, //纹理的层次，0表示基本图像层，可以理解为直接贴图
                mBitmap, //纹理图像
                0 //纹理边框尺寸
        );
        Log.d(TAG, "initTexture: end,mTextureId="+textures[0]);

        return textures[0];
    }



    public void draw() {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        /**设置数据*/
        // 启用顶点属性，最后对应禁用
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        //设置三角形坐标数据（一个顶点三个坐标）
        GLES20.glVertexAttribPointer(vPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                3 * 4, mVertexBuffer);
        //设置纹理坐标数据
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2,
                GLES20.GL_FLOAT, false,
                2 * 4, mTexCoordBuffer);

        // 将投影和视图转换传递给着色器，可以理解为给uMVPMatrix这个变量赋值为mvpMatrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        //设置使用的纹理编号
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定指定的纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);


        /** 绘制三角形，三个顶点*/
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 禁用顶点数组（好像不禁用也没啥问题）
        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
    }


    public void onDestroy() {
        GLES20.glDeleteProgram(mProgram);
        mProgram = 0;
    }


    /**
     * 子类可以更改着色器路径，达到不同效果
     *
     * @return
     */
    protected String getVertexCode() {
        return "shader/filter/filter_base_vertex.glsl";
    }

    protected String getFragmentCode() {
        return "shader/filter/filter_base_fragment.glsl";
    }
}
