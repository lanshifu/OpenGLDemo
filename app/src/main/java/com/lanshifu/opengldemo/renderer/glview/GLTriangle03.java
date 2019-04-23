package com.lanshifu.opengldemo.renderer.glview;

import android.opengl.GLES20;

import com.lanshifu.opengldemo.utils.GLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 三角形
 * 02基础上
 * 增加颜色
 */
public class GLTriangle03{

    // 顶点着色器的脚本
    String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            " attribute vec4 vPosition;" +      //传入的顶点
            " varying vec4 vColor;" +
            " attribute vec4 aColor;"+
                    " void main() {" +
                        "  gl_Position = uMVPMatrix * vPosition;" +  //顶点着色器，增加矩阵变换
                        "  vColor = aColor " +  //增加了一个aColor（顶点的颜色）作为输入量，传递给了vColor
                    " }";

    // 片元着色器的脚本
    String fragmentShaderCode =
            " precision mediump float;" +  // 声明float类型的精度为中等(精度越高越耗资源)
                    " varying vec4 vColor;" +       // vColor 改为 varying,代表由顶点着色器传过来的
                    " void main() {" +
                    "     gl_FragColor = vColor;" +  // 给此片元的填充色
                    " }";

    private FloatBuffer vertexBuffer;  //顶点坐标数据要转化成FloatBuffer格式
    private FloatBuffer colorBuffer;


    // 数组中每3个值作为一个坐标点
    static final int COORDS_PER_VERTEX = 3;
    //三角形的坐标数组
    static float triangleCoords[] = {
            500f, 200f, 0.0f, // top
            500f, 500f, 0.0f, // bottom left
            600f, 500f, 0.0f  // bottom right
    };


    //顶点个数，计算得出
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //一个顶点有3个float，一个float是4个字节，所以一个顶点要12字节
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per mVertex

    //三角形的颜色数组，rgba
    private float[] mColor_Single = {
            0.0f, 1.0f, 0.0f, 1.0f,
    };

    private float[] mColor = {
            0.0f, 1.0f, 0.0f, 1.0f ,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

    //顶点位置句柄
    private int vPositionHandle;
    //片元着色器颜色句柄
    private int vColorHandle;
    private int aColorHandle;
    //这个可以理解为一个OpenGL程序句柄
    private final int mProgram;
    //变换矩阵句柄
    private int mMVPMatrixHandle;

    //变换矩阵，提供set方法
    private float[] mvpMatrix = new float[16];
    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }


    public GLTriangle03() {
        /** 1、数据转换，顶点坐标数据float类型转换成OpenGL格式FloatBuffer，int和short同理*/
        vertexBuffer = GLUtil.floatArray2FloatBuffer(triangleCoords);

        colorBuffer = GLUtil.floatArray2FloatBuffer(mColor);

        /** 2、加载编译顶点着色器和片元着色器*/
        int vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        /** 3、创建空的OpenGL ES程序，并把着色器添加进去*/
        mProgram = GLES20.glCreateProgram();

        // 添加顶点着色器到程序中
        GLES20.glAttachShader(mProgram, vertexShader);

        // 添加片段着色器到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);

        /** 4、链接程序*/
        GLES20.glLinkProgram(mProgram);

    }


    public void draw() {

        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        /***1.获取句柄*/
        // 获取顶点着色器的位置的句柄（这里可以理解为当前绘制的顶点位置）
        vPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");


        // 获取片段着色器的vColor句柄
//        vColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        aColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        // 获取变换矩阵的句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        /**2.设置数据*/
        // 启用顶点属性，最后对应禁用
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        //准备三角形坐标数据
        GLES20.glVertexAttribPointer(vPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // 设置绘制三角形的颜色，给vColor 这个变量赋值
//        GLES20.glUniform4fv(vColorHandle, 1, mColor_Single, 0);

        GLES20.glEnableVertexAttribArray(aColorHandle);
        GLES20.glVertexAttribPointer(aColorHandle,4,
                GLES20.GL_FLOAT,false,
                0,colorBuffer);

        // 将投影和视图转换传递给着色器，可以理解为给uMVPMatrix这个变量赋值为mvpMatrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        /** 3.绘制三角形，三个顶点*/
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(vPositionHandle);
    }
}
