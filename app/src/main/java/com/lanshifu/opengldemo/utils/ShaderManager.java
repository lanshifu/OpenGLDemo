package com.lanshifu.opengldemo.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.util.SparseArray;

/**
 * 着色器管理，初始化一次，以后都从缓存取
 */
public class ShaderManager {

    /**图片部分*/
    public static final int BASE_SHADER = 1;  //默认
    public static final int GRAY_SHADER = 2;  //黑白、灰色
    public static final int WARM_SHADER = 3;  //暖色
    public static final int COOL_SHADER = 4;  //冷色
    public static final int BUZZY_SHADER = 5;  //模糊
    public static final int FOUR_SHADER = 6;  //四分镜
    public static final int ZOOM_SHADER = 7;  //放大
    public static final int LIGHT_SHADER = 8;  //发光,比较复杂一点

    /**相机部分*/
    public static final int CAMERA_BASE_SHADER = 9;  //相机默认
    public static final int CAMERA_GRAY_SHADER = 10;  //
    public static final int CAMERA_COOL_SHADER = 11;  //
    public static final int CAMERA_WARM_SHADER = 12;  //
    public static final int CAMERA_FOUR_SHADER = 13;  //
    public static final int CAMERA_ZOOM_SHADER = 14;  //
    public static final int CAMERA_LIGHT_SHADER = 15;  //
    public static final int CAMERA_BUZZ_SHADER = 16;  //

    private static SparseArray<Param> mParamSparseArray;

    public static void init(Context context) {

        mParamSparseArray = new SparseArray<>();

        insertParam(BASE_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_base.glsl"));

        insertParam(GRAY_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_gray.glsl"));

        insertParam(WARM_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_warm.glsl"));

        insertParam(COOL_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_cool.glsl"));

        insertParam(BUZZY_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_buzzy.glsl"));
        insertParam(FOUR_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_four.glsl"));

        insertParam(ZOOM_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_zoom.glsl"));

        insertParam(LIGHT_SHADER, GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_vertex_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/image/filter/filter_fragment_light.glsl"));

        /**相机部分*/
        insertParam(CAMERA_BASE_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_base.glsl"));
        //黑白
        insertParam(CAMERA_GRAY_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_gray.glsl"));

        insertParam(CAMERA_COOL_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_cool.glsl"));

        insertParam(CAMERA_WARM_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_warm.glsl"));

        insertParam(CAMERA_FOUR_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_four.glsl"));

        insertParam(CAMERA_ZOOM_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_zoom.glsl"));


//        insertParam(CAMERA_BUZZ_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
//                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_buzzy.glsl"));

//        insertParam(CAMERA_LIGHT_SHADER, GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_vertex_shader_base.glsl")
//                , GLUtil.loadFromAssetsFile(context, "shader/camera2/camera2_fragment_shader_light.glsl"));
    }


    public static void insertParam(int key, String vertexShaderCode, String fragmentShaderCode) {
        int program = GLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        // 获取顶点着色器的位置的句柄（这里可以理解为当前绘制的顶点位置）
        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        // 获取变换矩阵的句柄
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        //纹理位置句柄
        int mTexCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");

        //缓存OpenGL程序
        Param param = new Param(program, positionHandle, mMVPMatrixHandle, mTexCoordHandle);
        mParamSparseArray.append(key, param);
    }

    //通过key获取缓存中的OpenGL程序参数
    public static Param getParam(int key) {
        return mParamSparseArray.get(key);
    }

    /**
     * 定义一些要缓存的参数
     */
    public static class Param {
        public Param(int program, int positionHandle, int MVPMatrixHandle, int texCoordHandle) {
            this.program = program;
            this.positionHandle = positionHandle;
            mMVPMatrixHandle = MVPMatrixHandle;
            mTexCoordHandle = texCoordHandle;
        }

        public int program;
        //一些公用的句柄（顶点位置、矩阵、纹理坐标）
        public int positionHandle;
        public int mMVPMatrixHandle;
        public int mTexCoordHandle;
    }
}
