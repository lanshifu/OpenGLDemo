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
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.lanshifu.opengldemo.samsung.GLProgram.NameIndexerObj;

/**
 * GLRectangle draws rectangle.
 */
public class GLRectangle extends GLTexture {
    /**
     * Stroke + Vertex
     */
    public static final int TYPE_STROKE = 0;
    /**
     * Fill inside
     */
    public static final int TYPE_FILL = 1;
    private static final String TAG = "GLRectangle";
    private static final float DEFAULT_THICKNESS = 1.0f;
    private static final int ROUNDDOWN_DIGIT = 100000;
    NameIndexerObj mObjType = null;
    NameIndexerObj mObjSampler = null;
    NameIndexerObj mObjThickness = null;
    NameIndexerObj mObjFillColor = null;
    float[] mColor = new float[4];
    private int mRectangleType = TYPE_STROKE;
    private float mThickness = DEFAULT_THICKNESS;
    private float[] mFillColor;

    /**
     * Instantiates a new GLRectangle.
     *
     * @param glContext The GL context
     * @param left      The left side of the rectangle.
     * @param top       The top side of the rectangle.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     * @param color     The color of the rectangle.
     * @param thickness The thickness of the rectangle.
     */
    public GLRectangle(GLContext glContext, float left, float top, float width, float height, int color, float thickness) {
        super(glContext, 0, 0);

        mRectangleType = TYPE_STROKE;

        setColor(color);

        if (thickness < 1.0f) {
            mThickness = DEFAULT_THICKNESS;
        } else {
            mThickness = thickness;
        }

        translateAbsolute(left, top);
        setSize(width, height);
    }

    /**
     * Instantiates a new GLRectangle.
     *
     * @param glContext     The GL context
     * @param left          The left side of the rectangle.
     * @param top           The top side of the rectangle.
     * @param width         The width of the rectangle.
     * @param height        The height of the rectangle.
     * @param color         The color of the rectangle.
     * @param thickness     The thickness of the rectangle.
     * @param rectangleType The type of the rectangle. One of {@link #TYPE_STROKE} or {@link #TYPE_FILL}
     */
    public GLRectangle(GLContext glContext, float left, float top, float width, float height, int color, float thickness, int rectangleType) {
        super(glContext, 0, 0);

        mRectangleType = rectangleType;

        setColor(color);

        if (thickness < 1.0f) {
            mThickness = DEFAULT_THICKNESS;
        } else {
            mThickness = thickness;
        }

        translateAbsolute(left, top);
        setSize(width, height);
    }

    /**
     * Get the color of the rectangle.
     *
     * @return Color of the rectangle.
     */
    public int getColor() {
        return Color.argb((int) (mColor[0] * 255.0f), (int) (mColor[1] * 255.0f), (int) (mColor[2] * 255.0f), (int) (mColor[3] * 255.0f));
    }

    /**
     * Set the color of the rectangle.
     *
     * @param color The color
     */
    public void setColor(int color) {
        mColor[0] = Color.red(color) / 255.0f;
        mColor[1] = Color.green(color) / 255.0f;
        mColor[2] = Color.blue(color) / 255.0f;
        mColor[3] = Color.alpha(color) / 255.0f;
        if (mFillColor == null) {
            mFillColor = new float[4];
            mFillColor[0] = Color.red(color) / 255.0f;
            mFillColor[1] = Color.green(color) / 255.0f;
            mFillColor[2] = Color.blue(color) / 255.0f;
            mFillColor[3] = Color.alpha(color) / 255.0f;
        }
    }

    /**
     * Get the thickness of the rectangle.
     *
     * @return The thickness.
     */
    public float getThickness() {
        return mThickness;
    }

    /**
     * Set the thickness of the rectangle.
     *
     * @param thickness The thickness.
     */
    public void setThickness(float thickness) {
        mThickness = thickness;
    }

    /**
     * @see GLView#initSize()
     */
    @Override
    public void initSize() {
        setSize(getWidth(), getHeight());
    }

    /**
     * Drawing routing.
     * Caution! : Do not call directly.
     *
     * @see GLView#draw(float[], Rect)
     */
    @Override
    public synchronized void onDraw() {
        if (!mTextureLoaded) {
            return;
        }
        if (mLayoutUpdated || mVertexBuffer == null || mIndexBuffer == null || mTexCoordBuffer == null) {
            setVertices();
            initBuffers();
            mLayoutUpdated = false;
        }

        GLES20.glUseProgram(mProgramID);

        GLES20.glUniform4fv(mObjSampler.mHandle, 1, mColor, 0);
        GLES20.glUniform4fv(mObjFillColor.mHandle, 1, mFillColor, 0);

        Matrix.multiplyMM(mViewMatrix, 0, getContext().getProjMatrix(), 0, getMatrix(), 0);
        GLES20.glUniformMatrix4fv(mObjMVPMatrix.mHandle, 1, false, mViewMatrix, 0);

        GLES20.glUniform1f(mObjAlpha.mHandle, getAlpha());
        GLES20.glUniform1f(mObjThickness.mHandle, ((float) (int) ((1.0f / getWidth() * mThickness) * ROUNDDOWN_DIGIT)) / ROUNDDOWN_DIGIT);
        GLES20.glUniform1f(mObjType.mHandle, (float) mRectangleType);
        GLES20.glUniform1f(mObjParam.mHandle, getWidth() / getHeight());

        GLES20.glEnableVertexAttribArray(mObjPosition.mHandle);
        GLES20.glEnableVertexAttribArray(mObjTextureCoord.mHandle);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        GLES20.glVertexAttribPointer(mObjPosition.mHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glVertexAttribPointer(mObjTextureCoord.mHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);

        if (mTextureReloaded) {
            mTextureReloaded = false;
        }

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(mObjPosition.mHandle);
        GLES20.glDisableVertexAttribArray(mObjTextureCoord.mHandle);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    }

    /**
     * Set the color to fill.
     * Use with {@link #TYPE_FILL}
     *
     * @param color color to fill.(RGBA)
     */
    public void setFillColor(int color) {
        if (mFillColor == null) {
            mFillColor = new float[4];
        }
        mFillColor[0] = Color.red(color) / 255.0f;
        mFillColor[1] = Color.green(color) / 255.0f;
        mFillColor[2] = Color.blue(color) / 255.0f;
        mFillColor[3] = Color.alpha(color) / 255.0f;
    }

    /**
     * Set the rectangle's coordinates to the specified values.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     */
    public void setRect(float left, float top, float width, float height) {
        translateAbsolute(left, top);
        setSize(width, height);

        setVertices();
        initBuffers();
    }

    /**
     * Set the rectangle's coordinates to the specified values.
     *
     * @param newRect The new rectangle
     */
    public void setRect(RectF newRect) {
        setRect(newRect.left, newRect.top, newRect.width(), newRect.height());
    }

    /**
     * GLRectangle does not use bitmap. Do nothing.
     */
    @Override
    protected Bitmap loadBitmap() {
        return null;
    }

    /**
     * @see GLTexture#onLoad()
     */
    @Override
    protected boolean onLoad() {
        initSize();
        setVertices();
        initBuffers();

        GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_RECTANGLE);
        if (program != null) {
            mProgramID = program.getProgramID();
            mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
            mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
            mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
            mObjSampler = program.getNameIndexer(GLProgram.INDEXER_SAMPLER);
            mObjFillColor = program.getNameIndexer(GLProgram.INDEXER_FILL_COLOR);
            mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
            mObjThickness = program.getNameIndexer(GLProgram.INDEXER_THICKNESS);
            mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
            mObjType = program.getNameIndexer(GLProgram.INDEXER_TYPE);
        }

        mTextureLoaded = true;

        return true;
    }
}
