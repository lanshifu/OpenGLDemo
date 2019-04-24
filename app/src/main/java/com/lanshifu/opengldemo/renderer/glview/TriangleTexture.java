package com.lanshifu.opengldemo.renderer.glview;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.lanshifu.opengldemo.utils.GLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TriangleTexture {

    private  String mVertexShader=
            "attribute vec4 aPosition;" + //顶点位置
                    "attribute vec2 aTexCoord;" + //顶点纹理位置
                    "varying vec2 vTextureCoord;" + //用于传递给片元着色器的易变变量
                    "void main(){" +
                    "gl_Position = aPosition;" +
                    "vTextureCoord = aTexCoord;" +
                    "}";
    private String mFragmentShader =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform sampler2D sTexture;" + //纹理采样器，代表一副纹理
                    "void main(){" +
                    "gl_FragColor = texture2D(sTexture,vTextureCoord);" +//进行纹理采样
                    "}";

    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTexCoordBuffer;//顶点纹理坐标数据缓冲

    private int mvCount = 0;//顶点数量

    private int maPositionHandle;
    private int maTexCoordHandle;
    private int mProgram;
    private int mTextureId;

    private Bitmap mBitmap;

    public TriangleTexture(Context context,Bitmap bitmap){
        mBitmap = bitmap;
        initVertext();
        initShder();
        initTexture();
    }

    private void  initVertext(){
        mvCount = 3;
        float vertices[] = new float[]{
                0,1,0,-1,0,0,1,0,0
        };//顶点位置
        mVertexBuffer = GLUtil.floatArray2FloatBuffer(vertices);

        float[] colors = new float[]{
                0.5f,0,
                0,1,
                1,1
        };//顶点颜色数组
        mTexCoordBuffer = GLUtil.floatArray2FloatBuffer(colors);
    }

    private void initShder(){
        mProgram = GLUtil.createProgram(mVertexShader,mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram,"aPosition");
        maTexCoordHandle = GLES20.glGetAttribLocation(mProgram,"aTexCoord");
    }

    private void initTexture(){
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
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        if (mBitmap == null){
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

    public void drawSelf(){

        GLES20.glUseProgram(mProgram);

        GLES20.glVertexAttribPointer(maPositionHandle,3,
                GLES20.GL_FLOAT,false,3*4,mVertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoordHandle,2,
                GLES20.GL_FLOAT,false,2*4,mTexCoordBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId); //绑定指定的纹理id

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,mvCount);


    }
}
