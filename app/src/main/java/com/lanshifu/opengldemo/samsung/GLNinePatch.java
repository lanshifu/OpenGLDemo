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
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * GLNinePatch class permits drawing a bitmap in nine sections.
 */
public class GLNinePatch extends GLTexture {
    private static final String TAG = "GLNinePatchTexture";

    private static final int VERTEX_LENGTH = 3;
    private static final int COORDINATE_LENGTH = 2;
    private static final int TRIANGLE_INDEX_LENGTH = 3;

    private static final int X_INDEX = 0;
    private static final int Y_INDEX = 1;
    private static final int Z_INDEX = 2;

    private static final int U_INDEX = 0;
    private static final int V_INDEX = 1;

    /**
     * The resource id.
     */
    private int mResId;

    private int[] mDivX;
    private int[] mDivY;

    private int mNinePatchWidth = 0;
    private int mNinePatchHeight = 0;

    /**
     * Instantiates a new GLNinePatch.
     *
     * @param glContext the GL context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param resId     the resource id
     */
    public GLNinePatch(GLContext glContext, float left, float top, float width, float height, int resId) {
        super(glContext, left, top, width, height);

        mResId = resId;

        loadNinePatchResource();
    }

    /**
     * Instantiates a new GLNinePatch.
     *
     * @param glContext the GL context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param resId     the resource id
     * @param alpha     the alpha
     */
    public GLNinePatch(GLContext glContext, float left, float top, float width, float height, int resId, float alpha) {
        super(glContext, left, top, width, height);

        mResId = resId;
        mAlpha = alpha;

        loadNinePatchResource();
    }

    /**
     * Instantiates a new GLNinePatch without width and height.
     *
     * @param glContext the GL context
     * @param left      the left
     * @param top       the top
     * @param resId     the resource id
     */
    public GLNinePatch(GLContext glContext, float left, float top, int resId) {
        super(glContext, left, top);

        mResId = resId;

        loadNinePatchResource();
    }

    @Override
    public synchronized void clear() {

        mDivX = null;
        mDivY = null;

        super.clear();
    }

    /**
     * Gets the intrinsic height.
     *
     * @return the intrinsic height
     */
    public int getIntrinsicHeight() {
        return mNinePatchHeight;
    }

    /**
     * Gets the intrinsic width.
     *
     * @return the intrinsic width
     */
    public int getIntrinsicWidth() {
        return mNinePatchWidth;
    }

    /**
     * Sets a nine-patch resource.
     *
     * @param resId nine-patch resource id.
     */
    public synchronized void setNinePatch(int resId) {
        mResId = resId;

        loadNinePatchResource();
        reLoad();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);

        setVertices();
        initBuffers();

    }

    /**
     * Initialize the buffers.(Vertex, Index, TexCoord and TexFlipCoord buffers)
     */
    @Override
    protected synchronized void initBuffers() {
        clearBuffers();
        mVertexBuffer = GLUtil.getFloatBufferFromFloatArray(mVertices);

        int gridSizeX = mDivX.length + 1;
        int gridSizeY = mDivY.length + 1;

        int vertexSizeX = mDivX.length + 2;
        int vertexSizeY = mDivY.length + 2;

        int offset = -1;
        if (mIndices == null)
            mIndices = new byte[gridSizeX * gridSizeY * 2 * TRIANGLE_INDEX_LENGTH];

        for (int y = 0, n = gridSizeY; y < n; ++y) {
            for (int x = 0, m = gridSizeX; x < m; ++x) {
                mIndices[++offset] = (byte) (y * vertexSizeX + x);
                mIndices[++offset] = (byte) ((y + 1) * vertexSizeX + x);
                mIndices[++offset] = (byte) ((y + 1) * vertexSizeX + (x + 1));

                mIndices[++offset] = (byte) (y * vertexSizeX + x);
                mIndices[++offset] = (byte) ((y + 1) * vertexSizeX + (x + 1));
                mIndices[++offset] = (byte) (y * vertexSizeX + (x + 1));
            }
        }

        mIndexBuffer = GLUtil.getByteBufferFromByteArray(mIndices);

        mTexCoordBuffer = ByteBuffer.allocateDirect(vertexSizeX * vertexSizeY * COORDINATE_LENGTH * Float.SIZE / Byte.SIZE).order(ByteOrder.nativeOrder());
        mTexFlipCoordBuffer = ByteBuffer.allocateDirect(vertexSizeX * vertexSizeY * COORDINATE_LENGTH * Float.SIZE / Byte.SIZE).order(ByteOrder.nativeOrder());

        if (mCoordBuffer == null)
            mCoordBuffer = new float[vertexSizeX * vertexSizeY * COORDINATE_LENGTH];

        initCoordBuffer();
    }

    @Override
    protected synchronized void initCoordBuffer() {

        int vertexSizeX = mDivX.length + 2;
        int vertexSizeY = mDivY.length + 2;

        for (int y = 0, n = vertexSizeY; y < n; ++y) {
            for (int x = 0, m = vertexSizeX; x < m; ++x) {

                if (x == 0 || x == vertexSizeX - 1)
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + U_INDEX] = (x == 0 ? 0.0f : 1.0f);
                else
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + U_INDEX] = (float) mDivX[x - 1] / (float) mNinePatchWidth;

                if (y == 0 || y == vertexSizeY - 1)
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + V_INDEX] = (y == 0 ? 0.0f : 1.0f);
                else
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + V_INDEX] = (float) mDivY[y - 1] / (float) mNinePatchHeight;

            }
        }
        mTexCoordBuffer.asFloatBuffer().put(mCoordBuffer).position(0);

        for (int y = 0, n = vertexSizeY; y < n; ++y) {
            for (int x = 0, m = vertexSizeX; x < m; ++x) {

                if (x == 0 || x == vertexSizeX - 1)
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + U_INDEX] = (x == 0 ? 1.0f : 0.0f);
                else
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + U_INDEX] = 1.0f - ((float) mDivX[x - 1] / (float) mNinePatchWidth);

                if (y == 0 || y == vertexSizeY - 1)
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + V_INDEX] = (y == 0 ? 0.0f : 1.0f);
                else
                    mCoordBuffer[(y * m + x) * COORDINATE_LENGTH + V_INDEX] = (float) mDivY[y - 1] / (float) mNinePatchHeight;

            }
        }
        mTexFlipCoordBuffer.asFloatBuffer().put(mCoordBuffer).position(0);

    }

    @Override
    protected synchronized Bitmap loadBitmap() {

        if (mBitmap == null)
            loadNinePatchResource();

        return mBitmap;

    }

    protected void loadNinePatchResource() {

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //mBitmap = BitmapFactory.decodeResource(getContext().getApplicationContext().getResources(), mResId, options);

        try {
            mBitmap = BitmapFactory.decodeResource(GLContext.getApplicationContext().getResources(), mResId);
        } catch (OutOfMemoryError oom) {
            Log.e(TAG, "ResId : " + mResId);
            return;
        }

        mNinePatchWidth = mBitmap.getWidth();
        mNinePatchHeight = mBitmap.getHeight();

        byte[] chunkData = mBitmap.getNinePatchChunk();
        processNinePatchChunk(chunkData);

    }

    /**
     * Sets the vertices.
     */
    @Override
    protected synchronized void setVertices() {
        if (mDivX == null || mDivY == null) {
            Log.e(TAG, "view was cleared.");
            return;
        }

        int vertexSizeX = mDivX.length + 2;
        int vertexSizeY = mDivY.length + 2;

        float left = getLeft();
        float top = getTop();
        int width = (int) getWidth();
        int height = (int) getHeight();

        int indexerLength = 0;
        for (int i = 0, n = (mDivX.length / 2); i < n; ++i) {
            indexerLength += (mDivX[i * 2 + 1] - mDivX[i * 2]);
        }
        int varSectionsX = (width <= mNinePatchWidth) ? 0 : (width - mNinePatchWidth - indexerLength) / (mDivX.length / 2) + 1;

        indexerLength = 0;
        for (int i = 0, n = (mDivY.length / 2); i < n; ++i) {
            indexerLength += (mDivY[i * 2 + 1] - mDivY[i * 2]);
        }
        int varSectionsY = (height <= mNinePatchHeight) ? 0 : (height - mNinePatchHeight - indexerLength) / (mDivY.length / 2) + 1;

        if (mVertices == null)
            mVertices = new float[vertexSizeX * vertexSizeY * VERTEX_LENGTH];

        for (int y = 0, n = vertexSizeY; y < n; ++y) {
            for (int x = 0, m = vertexSizeX; x < m; ++x) {

                if (x == 0 || x == vertexSizeX - 1)
                    mVertices[(y * m + x) * VERTEX_LENGTH + X_INDEX] = (x == 0 ? left : left + width);
                else
                    mVertices[(y * m + x) * VERTEX_LENGTH + X_INDEX] = left + (float) (mDivX[x - 1] + varSectionsX * Math.ceil((x - 1) / 2.0f));

                if (y == 0 || y == vertexSizeY - 1)
                    mVertices[(y * m + x) * VERTEX_LENGTH + Y_INDEX] = (y == 0 ? top : top + height);
                else
                    mVertices[(y * m + x) * VERTEX_LENGTH + Y_INDEX] = top + (float) (mDivY[y - 1] + varSectionsY * Math.ceil((y - 1) / 2.0f));

                mVertices[(y * m + x) * VERTEX_LENGTH + Z_INDEX] = 0;

            }
        }

    }

    // frameworks/base/include/androidfw/ResourceTypes.h
    /* struct of NinePatchChunk
        int8_t wasDeserialized;
        int8_t numXDivs;
        int8_t numYDivs;
        int8_t numColors;
        uint32_t xDivsOffset;
        uint32_t yDivsOffset;
        int32_t paddingLeft, paddingRight;
        int32_t paddingTop, paddingBottom;
        uint32_t colorsOffset;
        int32_t* xDivs;
        int32_t* yDivs;
        uint32_t* colors;*/
    private synchronized boolean processNinePatchChunk(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());

        byte wasSerialized = byteBuffer.get();
        if (wasSerialized == 0)
            return false;

        int numXDivs = byteBuffer.get();
        int numYDivs = byteBuffer.get();
        int numColors = byteBuffer.get();
        mDivX = new int[numXDivs];
        mDivY = new int[numYDivs];

        // skip
        byteBuffer.getInt();
        byteBuffer.getInt();

        int left = byteBuffer.getInt();
        int right = byteBuffer.getInt();
        int top = byteBuffer.getInt();
        int bottom = byteBuffer.getInt();

        setPaddings(new Rect(left, top, right, bottom));

        // skip
        byteBuffer.getInt();

        for (int i = 0, n = mDivX.length; i < n; ++i) {
            mDivX[i] = byteBuffer.getInt();
        }

        for (int i = 0, n = mDivY.length; i < n; ++i) {
            mDivY[i] = byteBuffer.getInt();
        }

        return true;
    }

}