/*
 * Copyright (C) 2010 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * IT & Mobile Communications,
 * Mobile Communications Business, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.
 * No part of the software and documents may be copied, reproduced, transmitted,
 * translated, or reduced to any electronic medium or machine-readable form
 * without the prior written consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the software and
 * documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.lanshifu.opengldemo.samsung;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.lanshifu.opengldemo.samsung.GLProgram.NameIndexerObj;

import java.nio.FloatBuffer;

public class GLTriangle extends GLTexture {
    public static final int TYPE_TRIANGLE_STROKE = 1;
    public static final int TYPE_TRIANGLE_CORRECTION_STROKE = 2;
    public static final int TYPE_TRIANGLE_FILL = 3;
    private static final String TAG = "GLTriangle";
    private static final float DEFAULT_THICKNESS = 1.0f;
    private int mTriangleType = TYPE_TRIANGLE_STROKE;
    private int mDirection = 0;
    private float[] mThickness = new float[3];
    private FloatBuffer mThicknessBuffer;
    private NameIndexerObj mObjSampler = null;
    private NameIndexerObj mObjPointSize = null;
    private float[] mColor = new float[4];

    public GLTriangle(GLContext glContext, float left, float top, float width, float height, int direction, int color, float thickness) {
        super(glContext, 0, 0);

        mTriangleType = TYPE_TRIANGLE_CORRECTION_STROKE;

        mDirection = -direction;

        setColor(color);

        if (thickness < 1.0f) {
            mThickness[0] = DEFAULT_THICKNESS;
            mThickness[1] = DEFAULT_THICKNESS;
            mThickness[2] = DEFAULT_THICKNESS;
        } else {
            mThickness[0] = thickness;
            mThickness[1] = thickness;
            mThickness[2] = thickness;
        }

        translateAbsolute(left, top);
        setSize(width, height);
    }

    public GLTriangle(GLContext glContext, float left, float top, float width, float height, int direction, int color, float thickness, int rectangletype) {
        super(glContext, 0, 0);

        mTriangleType = rectangletype;

        mDirection = -direction;

        setColor(color);

        if (thickness < 1.0f) {
            mThickness[0] = DEFAULT_THICKNESS;
            mThickness[1] = DEFAULT_THICKNESS;
            mThickness[2] = DEFAULT_THICKNESS;
        } else {
            mThickness[0] = thickness;
            mThickness[1] = thickness;
            mThickness[2] = thickness;
        }

        translateAbsolute(left, top);
        setSize(width, height);
    }

    @Override
    public synchronized boolean contains(float x, float y) {
        return false;
    }

    @Override
    public synchronized GLView findViewByCoordinate(float x, float y) {
        return null;
    }

    public int getColor() {
        return Color.argb((int) (mColor[0] * 255.0f), (int) (mColor[1] * 255.0f), (int) (mColor[2] * 255.0f), (int) (mColor[3] * 255.0f));
    }

    public void setColor(int color) {
        mColor[0] = Color.red(color) / 255.0f;
        mColor[1] = Color.green(color) / 255.0f;
        mColor[2] = Color.blue(color) / 255.0f;
        mColor[3] = Color.alpha(color) / 255.0f;
    }

    public float getThickness() {
        return mThickness[0];
    }

    public void setThickness(float thickness) {
        mThickness[0] = thickness;
        mThickness[1] = thickness;
        mThickness[2] = thickness;
        initBuffers();
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#initSize()
     */
    @Override
    public synchronized void initSize() {

        setSize(getWidth(), getHeight());

    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onDraw(javax.microedition.khronos.opengles.GL11)
     */
    @Override
    public synchronized void onDraw() {
        if (!mTextureLoaded) {
            return;
        }
        if (mLayoutUpdated) {
            setVertices();
            if (mVertexBuffer != null) {
                mVertexBuffer.clear();
            }
            mVertexBuffer = GLUtil.getFloatBufferFromFloatArray(mVertices);
            mLayoutUpdated = false;
        } else {
            if (mVertexBuffer == null || mIndexBuffer == null || mThicknessBuffer == null) {
                Log.e(TAG, "init buffers on onDraw");
                setVertices();
                initBuffers();
            }
        }

        GLES20.glLineWidth(mThickness[0]);

        GLES20.glUseProgram(mProgramID);

        GLES20.glUniform4fv(mObjSampler.mHandle, 1, mColor, 0);

        Matrix.multiplyMM(mViewMatrix, 0, getContext().getProjMatrix(), 0, getMatrix(), 0);

        GLES20.glUniformMatrix4fv(mObjMVPMatrix.mHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform1f(mObjAlpha.mHandle, getAlpha());

        GLES20.glEnableVertexAttribArray(mObjPointSize.mHandle);
        GLES20.glEnableVertexAttribArray(mObjPosition.mHandle);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // GLES20.glFrontFace(GLES20.GL_CW);
        GLES20.glVertexAttribPointer(mObjPointSize.mHandle, 1, GLES20.GL_FLOAT, false, 0, mThicknessBuffer);
        GLES20.glVertexAttribPointer(mObjPosition.mHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        if (mTextureReloaded) {
            mTextureReloaded = false;
        }

        if (mTriangleType == TYPE_TRIANGLE_CORRECTION_STROKE)
            GLES20.glDrawElements(GLES20.GL_POINTS, mIndices.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

        if (mTriangleType == TYPE_TRIANGLE_FILL)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
        else
            GLES20.glDrawElements(GLES20.GL_LINE_LOOP, mIndices.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisableVertexAttribArray(mObjPointSize.mHandle);
        GLES20.glDisableVertexAttribArray(mObjPosition.mHandle);

    }

    /**
     * {@link GLTriangle} does not support shader program.
     */
    @Override
    public void setShaderProgram(int type) {
    }

    /**
     * {@link GLTriangle} does not support tint.
     */
    @Override
    public void setTint(int color) {
    }

    /**
     * Clear buffers.
     */
    @Override
    protected synchronized void clearBuffers() {
        if (mThicknessBuffer != null) {
            mThicknessBuffer.clear();
        }
        super.clearBuffers();
    }

    /**
     * Inits the buffers.
     */
    @Override
    protected synchronized void initBuffers() {
        clearBuffers();
        mVertexBuffer = GLUtil.getFloatBufferFromFloatArray(mVertices);
        mThicknessBuffer = GLUtil.getFloatBufferFromFloatArray(mThickness);

        int offset = 0;
        if (mIndices == null)
            mIndices = new byte[3];
        mIndices[offset++] = 0;
        mIndices[offset++] = 1;
        mIndices[offset++] = 2;

        mIndexBuffer = GLUtil.getByteBufferFromByteArray(mIndices);
    }

    @Override
    protected Bitmap loadBitmap() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onLoad(javax.microedition.khronos.opengles.GL11)
     */
    @Override
    protected synchronized boolean onLoad() {
        initSize();
        setVertices();
        initBuffers();

        GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_LINE);
        if (program != null) {
            mProgramID = program.getProgramID();
            mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
            mObjPointSize = program.getNameIndexer(GLProgram.INDEXER_POINTSIZE);
            mObjSampler = program.getNameIndexer(GLProgram.INDEXER_SAMPLER);
            mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
            mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
        }

        mTextureLoaded = true;

        return true;
    }

    @Override
    protected synchronized void setVertices() {

        if (mVertices == null)
            mVertices = new float[9];

        mVertices[0] = getLeft();
        mVertices[1] = getTop();
        mVertices[2] = 0;

        mVertices[3] = getLeft();
        mVertices[4] = getBottom();
        mVertices[5] = 0;

        mVertices[6] = getRight();
        mVertices[7] = (getTop() + getBottom()) / 2.0f;
        mVertices[8] = 0;

        rotateDegree(mDirection);
    }

}