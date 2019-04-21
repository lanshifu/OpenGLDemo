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

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.lanshifu.opengldemo.samsung.GLProgram.NameIndexerObj;

/**
 * The Class GLTexture.
 */
public abstract class GLTexture extends GLView {
    private static final String TAG = "GLTexture";

    /**
     * The m vertex buffer.
     */
    protected FloatBuffer mVertexBuffer;
    /**
     * The m index buffer.
     */
    protected ByteBuffer mIndexBuffer;
    /**
     * The m tex coord buffer.
     */
    protected ByteBuffer mTexCoordBuffer;
    /**
     * The m tex flip coord buffer.
     */
    protected ByteBuffer mTexFlipCoordBuffer;
    /**
     * The m textures.
     */
    protected int[] mTextures;
    /**
     * The m coord buffer.
     */
    protected float[] mCoordBuffer;
    /**
     * The m bitmap.
     */
    protected Bitmap mBitmap;
    protected boolean mFlip = false;
    protected boolean mTextureReloaded = false;
    protected int mProgramID = 0;
    protected int mProgramType = GLProgramStorage.TYPE_PROGRAM_BASIC;
    protected int mNewProgramType = GLProgramStorage.TYPE_PROGRAM_BASIC;
    protected NameIndexerObj mObjPosition = null;
    protected NameIndexerObj mObjTextureCoord = null;
    protected NameIndexerObj mObjMVPMatrix = null;
    protected NameIndexerObj mObjAlpha = null;
    protected NameIndexerObj mObjStep = null;
    protected NameIndexerObj mObjParam = null;
    protected NameIndexerObj mObjTintColor = null;
    protected NameIndexerObj mObjFadingPos = null;
    protected NameIndexerObj mObjFadingOffset = null;
    protected NameIndexerObj mObjSideFadingPos = null;
    protected NameIndexerObj mObjFadingOrientation = null;
    protected float[] mViewMatrix = new float[16];
    /**
     * The m texture loaded.
     */
    protected boolean mTextureLoaded = false;
    protected boolean mTextureSharing = false;
    /**
     * The m vertices.
     */
    float[] mVertices;
    /**
     * The m indices.
     */
    byte[] mIndices;
    private boolean mAsyncLoadingInProgress = false;
    private boolean mBitmapUpdated = false;
    private boolean mBitmapSizeChanged = false;

    /**
     * Instantiates a new gl texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     */
    public GLTexture(GLContext glContext, float left, float top) {
        super(glContext, left, top);
    }

    /**
     * Instantiates a new gl texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     */
    public GLTexture(GLContext glContext, float left, float top, float width, float height) {
        super(glContext, left, top, width, height);
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#clear()
     */
    @SuppressLint("NewApi")
    @Override
    public synchronized void clear() {
        super.clear();

        mCoordBuffer = null;
        clearBuffers();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mTextures != null) {
            getContext().addTextureToDelete(this);
        }
    }

    public void clearTexture() {
        if (mTextures != null) {
            GLES20.glDeleteTextures(1, new int[]{mTextures[0]}, 0);
            mTextures = null;
        }
    }

    /* (non-Javadoc)
     * @see com.samsung.android.glview.GLView#getLoaded()
     */
    @Override
    public boolean getLoaded() {
        return mTextureLoaded;
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#initSize()
     */
    @Override
    public synchronized void initSize() {
        if (mBitmap == null)
            mBitmap = loadBitmap();

        if (mBitmap != null) {
            if (!getSizeSpecified())
                setSize(mBitmap.getWidth(), mBitmap.getHeight());
            else
                setSize(getWidth(), getHeight());
        }
    }

    @Override
    public void onAlphaUpdated() {
        super.onAlphaUpdated();
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onDraw()
     */
    @Override
    public void onDraw() {
        if (mTextures == null || !mTextureLoaded) {
            return;
        }
        if (mLayoutUpdated) {
            setVertices();
            if (mVertexBuffer != null) {
                mVertexBuffer.clear();
            }
            mVertexBuffer = GLUtil.getFloatBufferFromFloatArray(mVertices);
            if (mVertexBuffer == null || mTexFlipCoordBuffer == null) {
                return;
            }
            mLayoutUpdated = false;
        } else {
            if (mVertexBuffer == null || mTexFlipCoordBuffer == null || mTexCoordBuffer == null || mIndexBuffer == null) {
                Log.w(TAG, "init buffers on onDraw");
                setVertices();
                initBuffers();
            }
        }

        if (mNewProgramType != mProgramType) {
            mProgramType = mNewProgramType;
            loadProgram();
        }

        GLES20.glUseProgram(mProgramID);

        int err = 0;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.v(TAG, "Error [" + getTag() + "] : glActiveTexture - " + err + " : " + GLU.gluErrorString(err));
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.v(TAG, "Error [" + getTag() + "] : glBindTexture - " + err + " : " + GLU.gluErrorString(err));
        }

        if (mObjTintColor != null) {
            GLES20.glUniform4fv(mObjTintColor.mHandle, 1, mTintColor, 0);
        }

        GLUtil.multiplyMM(mViewMatrix, getContext().getProjMatrix(), getMatrix());
        GLES20.glUniformMatrix4fv(mObjMVPMatrix.mHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform1f(mObjAlpha.mHandle, getAlpha());
        if (mObjStep != null) {
            GLES20.glUniform1f(mObjStep.mHandle, mShaderStep);
        }
        if (mObjParam != null) {
            GLES20.glUniform1f(mObjParam.mHandle, mShaderParameter);
        }
        if (mObjFadingPos != null) {
            GLES20.glUniform2fv(mObjFadingPos.mHandle, 1, mShaderFadingPos, 0);
        }
        if (mObjFadingOffset != null) {
            GLES20.glUniform2fv(mObjFadingOffset.mHandle, 1, mShaderFadingOffset, 0);
        }
        if (mObjSideFadingPos != null) {
            GLES20.glUniform2fv(mObjSideFadingPos.mHandle, 1, mShaderSideFadingPos, 0);
        }
        if (mObjFadingOrientation != null) {
            GLES20.glUniform1i(mObjFadingOrientation.mHandle, mShaderFadingOrientation);
        }
        GLES20.glEnableVertexAttribArray(mObjPosition.mHandle);
        GLES20.glEnableVertexAttribArray(mObjTextureCoord.mHandle);

        //GLES20.glFrontFace(GLES20.GL_CW);

        GLES20.glVertexAttribPointer(mObjPosition.mHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        if (mFlip)
            GLES20.glVertexAttribPointer(mObjTextureCoord.mHandle, 2, GLES20.GL_FLOAT, false, 0, mTexFlipCoordBuffer);
        else
            GLES20.glVertexAttribPointer(mObjTextureCoord.mHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);

        if (mTextureReloaded) {
            loadGLTexture();
            mTextureReloaded = false;
        }
        if (mBitmapUpdated) {
            doUpdate();
        }
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
        err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.v(TAG, "Error [" + getTag() + "] : glDrawElements  - " + err + " : " + GLU.gluErrorString(err));
        }
        GLES20.glDisableVertexAttribArray(mObjPosition.mHandle);
        GLES20.glDisableVertexAttribArray(mObjTextureCoord.mHandle);
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onLayoutMove()
     */
    @Override
    public void onLayoutUpdated() {
        super.onLayoutUpdated();
        mLayoutUpdated = true;
    }

    @Override
    public synchronized void onReset() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mTextureLoaded = false;
    }

    /**
     * Reset tint color
     */
    public void resetTint() {
        super.setTint(0);
        // FIXME : Need to consider default program type
        setShaderProgram(GLProgramStorage.TYPE_PROGRAM_BASIC);
    }

    public void setFlip(boolean flip) {
        mFlip = flip;
    }

    /**
     * Sets the shader program. Refer to {@link GLProgramStorage} for more information
     *
     * @param type the shader program type
     */
    @Override
    public void setShaderProgram(int type) {
        mNewProgramType = type;
    }

    @Override
    public void setTint(int color) {
        super.setTint(color);
        setShaderProgram(GLProgramStorage.TYPE_PROGRAM_TINT_BASIC);
    }

    public synchronized boolean updateTexture(Bitmap bitmap, boolean sizeChanged) {
        if (bitmap == null || bitmap.getWidth() != getWidth() || bitmap.getHeight() != getHeight()) {
            return false;
        }
        mBitmapUpdated = true;
        mBitmapSizeChanged = sizeChanged;

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mBitmap = Bitmap.createBitmap(bitmap);

        getContext().setDirty(true);
        return true;
    }

    protected void clearBitmap() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    /**
     * Clear buffers.
     */
    protected void clearBuffers() {
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
        }
        mVertexBuffer = null;
        if (mIndexBuffer != null) {
            mIndexBuffer.clear();
        }
        mIndexBuffer = null;
        if (mTexCoordBuffer != null) {
            mTexCoordBuffer.clear();
        }
        mTexCoordBuffer = null;
        if (mTexFlipCoordBuffer != null) {
            mTexFlipCoordBuffer.clear();
        }
        mTexFlipCoordBuffer = null;
    }

    protected void generateTexture() {
        if (mTextures == null)
            mTextures = new int[1];

        GLES20.glGenTextures(1, mTextures, 0);
    }

    /**
     * Inits the buffers.
     */
    protected synchronized void initBuffers() {
        clearBuffers();
        mVertexBuffer = GLUtil.getFloatBufferFromFloatArray(mVertices);

        int offset = 0;
        if (mIndices == null) {
            mIndices = new byte[6];
            mIndices[offset++] = 0;
            mIndices[offset++] = 1;
            mIndices[offset++] = 3;
            mIndices[offset++] = 0;
            mIndices[offset++] = 3;
            mIndices[offset] = 2;
        }

        mIndexBuffer = GLUtil.getByteBufferFromByteArray(mIndices);

        mTexCoordBuffer = ByteBuffer.allocateDirect(8 * Float.SIZE / Byte.SIZE).order(ByteOrder.nativeOrder());
        mTexFlipCoordBuffer = ByteBuffer.allocateDirect(8 * Float.SIZE / Byte.SIZE).order(ByteOrder.nativeOrder());

        if (mCoordBuffer == null)
            mCoordBuffer = new float[8];

        initCoordBuffer();
    }

    /**
     * Initialize the CoordBuffer.
     */
    protected void initCoordBuffer() {
        int offset = 0;
        mCoordBuffer[offset] = 0;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 1.0f;
        mCoordBuffer[++offset] = 1.0f;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 1.0f;
        mCoordBuffer[++offset] = 1.0f;

        mTexCoordBuffer.asFloatBuffer().put(mCoordBuffer).position(0);

        offset = 0;
        mCoordBuffer[offset] = 1.0f;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 1.0f;
        mCoordBuffer[++offset] = 1.0f;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 0;
        mCoordBuffer[++offset] = 1.0f;

        mTexFlipCoordBuffer.asFloatBuffer().put(mCoordBuffer).position(0);

    }

    /**
     * Load bitmap.
     *
     * @return the bitmap
     */
    protected abstract Bitmap loadBitmap();

    /**
     * Load texture.
     */
    protected synchronized void loadGLTexture() {
        if (mBitmap == null) {
            return;
        }

        int err = 0;
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "Error [" + getTag() + "] : texImage2D - " + err + " : " + GLU.gluErrorString(err));
        }

        clearBitmap();

        //initCoordBuffer();
    }

    protected void loadProgram() {
        switch (mProgramType) {
            case GLProgramStorage.TYPE_PROGRAM_ROUND_RECT: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_ROUND_RECT);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = program.getNameIndexer(GLProgram.INDEXER_STEP);
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = null;
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;
            case GLProgramStorage.TYPE_PROGRAM_CIRCULAR_CLIP: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_CIRCULAR_CLIP);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = program.getNameIndexer(GLProgram.INDEXER_STEP);
                    mObjParam = null;
                    mObjTintColor = null;
                    mObjFadingPos = null;
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_FADE: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_FADE);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = program.getNameIndexer(GLProgram.INDEXER_STEP);
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = null;
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_SCALE_CIRCLE_TEXTURE: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_SCALE_CIRCLE_TEXTURE);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = null;
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_TINT_BASIC: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_TINT_BASIC);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = null;
                    mObjTintColor = program.getNameIndexer(GLProgram.INDEXER_TINT_COLOR);
                    mObjFadingPos = null;
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_ITEM_COLOR_CHANGE: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_ITEM_COLOR_CHANGE);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = program.getNameIndexer(GLProgram.INDEXER_FADING_POS);
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = program.getNameIndexer(GLProgram.INDEXER_FADING_POS);
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION_LAND: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION_LAND);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = program.getNameIndexer(GLProgram.INDEXER_FADING_POS);
                    mObjFadingOffset = program.getNameIndexer(GLProgram.INDEXER_FADING_OFFSET);
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;

            case GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION_AND_COLOR_CHANGE: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION_AND_COLOR_CHANGE);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = program.getNameIndexer(GLProgram.INDEXER_PARAMETER);
                    mObjTintColor = null;
                    mObjFadingPos = program.getNameIndexer(GLProgram.INDEXER_FADING_POS);
                    mObjFadingOffset = program.getNameIndexer(GLProgram.INDEXER_FADING_OFFSET);
                    mObjSideFadingPos = program.getNameIndexer(GLProgram.INDEXER_SIDE_FADING_POS);
                    mObjFadingOrientation = program.getNameIndexer(GLProgram.INDEXER_FADING_ORIENTATION);
                }
            }
            break;

            default: {
                GLProgram program = getContext().getProgramStorage().getProgram(GLProgramStorage.TYPE_PROGRAM_BASIC);
                if (program != null) {
                    mProgramID = program.getProgramID();
                    mObjPosition = program.getNameIndexer(GLProgram.INDEXER_VERTEX);
                    mObjTextureCoord = program.getNameIndexer(GLProgram.INDEXER_TEXCOORD);
                    mObjMVPMatrix = program.getNameIndexer(GLProgram.INDEXER_MVPMATRIX);
                    mObjAlpha = program.getNameIndexer(GLProgram.INDEXER_ALPHA);
                    mObjStep = null;
                    mObjParam = null;
                    mObjTintColor = null;
                    mObjFadingPos = null;
                    mObjFadingOffset = null;
                    mObjSideFadingPos = null;
                    mObjFadingOrientation = null;
                }
            }
            break;
        }
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onLoad()
     */
    @Override
    protected boolean onLoad() {
        if (mAsyncLoad) {
            if (!mAsyncLoadingInProgress) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mAsyncLoadingInProgress = true;
                        initSize();
                        if (mBitmap == null)
                            return;
                        setVertices();
                        initBuffers();

                        generateTexture();
                        int err = 0;
                        err = GLES20.glGetError();
                        if (err != GLES20.GL_NO_ERROR) {
                            Log.e(TAG, "Error [" + getTag() + "] : glGenTexture - " + err + " : " + GLU.gluErrorString(err));
                        }
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
                        err = GLES20.glGetError();
                        if (err != GLES20.GL_NO_ERROR) {
                            Log.e(TAG, "Error [" + getTag() + "] : glBindTexture - " + err + " : " + GLU.gluErrorString(err));
                        }
                        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

                        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                        if (mNewProgramType != mProgramType)
                            mProgramType = mNewProgramType;

                        loadProgram();
                        loadGLTexture();

                        mTextureLoaded = true;
                        mAsyncLoadingInProgress = false;
                    }
                });
                t.setName("AsyncLoadingThread");
                t.start();
                return mTextureLoaded && !mAsyncLoadingInProgress;
            }
        } else {
            initSize();
            generateTexture();
            if (mBitmap == null && !mTextureSharing) { // Shared texture does not make bitmap.
                return false;
            }

            setVertices();
            initBuffers();

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            if (mNewProgramType != mProgramType)
                mProgramType = mNewProgramType;

            loadProgram();
            loadGLTexture();

            mTextureLoaded = true;
        }
        return true;
    }

    protected synchronized boolean reLoad() {
        if (!mTextureLoaded) {
            return false;
        }
        mTextureLoaded = false;

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = loadBitmap();

        setVertices();
        initBuffers();

        mTextureLoaded = true;
        mTextureReloaded = true;
        getContext().setDirty(true);
        return true;
    }

    /**
     * Sets the vertices.
     */
    protected void setVertices() {
        if (mVertices == null)
            mVertices = new float[12];

        //float glCoordinate[] = new float[2];
        //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getLeft(), getTop());
        if (getContext().getAlignToPixel()) {
            mVertices[0] = ((int) (getLeft() + 0.5f));
            mVertices[1] = ((int) (getTop() + 0.5f));
            mVertices[2] = 0;

            //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getLeft(), getBottom());
            mVertices[3] = ((int) (getLeft() + 0.5f));
            mVertices[4] = ((int) (getBottom() + 0.5f));
            mVertices[5] = 0;

            //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getRight(), getTop());
            mVertices[6] = ((int) (getRight() + 0.5f));
            mVertices[7] = ((int) (getTop() + 0.5f));
            mVertices[8] = 0;

            //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getRight(), getBottom());
            mVertices[9] = ((int) (getRight() + 0.5f));
            mVertices[10] = ((int) (getBottom() + 0.5f));
            mVertices[11] = 0;
//            mVertices[11] = getDepth();
        } else {
            mVertices[0] = getLeft();
            mVertices[1] = getTop();
            mVertices[2] = 0;

            //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getLeft(), getBottom());
            mVertices[3] = getLeft();
            mVertices[4] = getBottom();
            mVertices[5] = 0;

            //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getRight(), getTop());
            mVertices[6] = getRight();
            mVertices[7] = getTop();
            mVertices[8] = 0;

            //GLUtil.getGLCoordinateFromScreenCoordinate(getContext(), glCoordinate, getRight(), getBottom());
            mVertices[9] = getRight();
            mVertices[10] = getBottom();
            mVertices[11] = 0;
        }
    }

    private void doUpdate() {
        if (mBitmap != null) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            if (mBitmapSizeChanged) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            } else {
                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mBitmap);
            }
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
