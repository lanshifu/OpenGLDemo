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

public class GLPreviewData {

    private static final Object mLock = new Object();
    private static GLPreviewData sGLPreview = null;
    /**
     * The m gl context.
     */
    private final GLContext mGLContext;
    private byte[] mPreviewData = null;
    private int mWidth;
    private int mHeight;
    private boolean mFrameAvailable = false;

    private GLPreviewData(GLContext glContext) {
        mGLContext = glContext;
        mWidth = 0;
        mHeight = 0;
    }

    public static GLPreviewData getInstance(GLContext glContext) {
        synchronized (mLock) {
            if (sGLPreview == null) {
                sGLPreview = new GLPreviewData(glContext);
            }
            return sGLPreview;
        }
    }

    public static void releaseInstance() {
        synchronized (mLock) {
            if (sGLPreview != null) {
                sGLPreview.release();
                sGLPreview = null;
            }
        }
    }

    public void clearPreviewFrame() {
        mFrameAvailable = false;
    }

    public boolean getFrameAvailable() {
        if (mPreviewData == null) {
            return false;
        }
        return mFrameAvailable;
    }

    public int getHeight() {
        return mHeight;
    }

    public byte[] getPreviewDataByte() {
        return mPreviewData;
    }

    public float getSurfaceCoordXOffset() {
        return ((float) mHeight / (float) mWidth) * 1.0f; // 1.0f = thumbnailbutton width/height
    }

    public synchronized int getWidth() {
        return mWidth;
    }

    public synchronized void setPreviewData(int width, int height, byte[] data) {
        mWidth = width;
        mHeight = height;
        mPreviewData = data;
        mFrameAvailable = true;
        setNewFrame();
    }

    private void release() {
        synchronized (mLock) {
            mPreviewData = null;
            sGLPreview = null;
        }
    }

    @SuppressLint("NewApi")
    private void setNewFrame() {
        mGLContext.setDirty(true);
    }
}
