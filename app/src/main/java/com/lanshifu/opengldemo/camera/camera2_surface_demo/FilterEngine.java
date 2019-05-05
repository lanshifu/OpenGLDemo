package com.lanshifu.opengldemo.camera.camera2_surface_demo;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.lanshifu.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.lanshifu.opengldemo.utils.GLUtil.loadShader;


public class FilterEngine {

    private static FilterEngine filterEngine = null;

    private Context mContext;
    private FloatBuffer mVertexmBuffer;
    private FloatBuffer mFragmentBuffer;
    private int mOESTextureId = -1;
    private int vertexShader = -1;
    private int fragmentShader = -1;

    private int mShaderProgram = -1;

    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;

    public FilterEngine(int OESTextureId, Context context) {
        mContext = context;
        mOESTextureId = OESTextureId;
        mVertexmBuffer = createBuffer(vertexData);
        mFragmentBuffer = createBuffer(fragmentData);

        vertexShader = loadShader(GL_VERTEX_SHADER, Utils.readShaderFromResource(mContext, R.raw.base_vertex_shader));
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, Utils.readShaderFromResource(mContext, R.raw.base_fragment_shader));
        mShaderProgram = linkProgram(vertexShader, fragmentShader);

//        mShaderProgram = GLUtil.createProgram(
//                GLUtil.loadFromAssetsFile("shader/camera2/base_vertex_shader.glsl",mContext.getResources()),
//                GLUtil.loadFromAssetsFile("shader/camera2/base_fragment_shader.glsl",mContext.getResources()));
    }

    public int linkProgram(int verShader, int fragShader) {
        int program = glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Create Program Failed!" + glGetError());
        }
        glAttachShader(program, verShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);

        glUseProgram(program);
        return program;
    }

    /*public static FilterEngine getInstance() {
        if (filterEngine == null) {
            synchronized (FilterEngine.class) {
                if (filterEngine == null)
                    filterEngine = new FilterEngine();
            }
        }
        return filterEngine;
    }*/

    //两个三角形顶点
    private static final float[] vertexData = {
            1f, 1f,
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            -1f, -1f,
            1f, -1f,
    };

    //对应纹理坐标
    private static final float[] fragmentData = {
            1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    public FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    public void drawTexture(float[] transformMatrix) {
        aPositionLocation = glGetAttribLocation(mShaderProgram, FilterEngine.POSITION_ATTRIBUTE);
        aTextureCoordLocation = glGetAttribLocation(mShaderProgram, FilterEngine.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_SAMPLER_UNIFORM);

        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        glUniform1i(uTextureSamplerLocation, 0);
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        if (mVertexmBuffer != null) {
            glEnableVertexAttribArray(aPositionLocation);
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mVertexmBuffer);

            glEnableVertexAttribArray(aTextureCoordLocation);
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, mFragmentBuffer);

            glDrawArrays(GL_TRIANGLES, 0, 6);
        }
    }

    public int getShaderProgram() {
        return mShaderProgram;
    }

    public FloatBuffer getVertexmBuffer() {
        return mVertexmBuffer;
    }

    public FloatBuffer getFragmentBuffer() {
        return mFragmentBuffer;
    }

    public int getOESTextureId() {
        return mOESTextureId;
    }

    public void setOESTextureId(int OESTextureId) {
        mOESTextureId = OESTextureId;
    }

}
