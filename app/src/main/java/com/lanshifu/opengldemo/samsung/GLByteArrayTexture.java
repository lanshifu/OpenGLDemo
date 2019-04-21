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

/**
 * The Class GLByteArrayTexture.
 */
public class GLByteArrayTexture extends GLTexture {

    /**
     * The m bitmap data.
     */
    private byte[] mBitmapData;
    private int mSampleSize = 2;

    /**
     * Instantiates a new gl byte array texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param data      the data
     */
    public GLByteArrayTexture(GLContext glContext, float left, float top, byte[] data) {
        super(glContext, left, top);

        mBitmapData = data;
        if (mBitmapData == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Instantiates a new gl byte array texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param data      the data
     */
    public GLByteArrayTexture(GLContext glContext, float left, float top, float width, float height, byte[] data) {
        super(glContext, left, top, width, height);

        mBitmapData = data;
        if (mBitmapData == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Instantiates a new gl byte array texture.
     *
     * @param glContext    the gl context
     * @param left         the left
     * @param top          the top
     * @param width        the width
     * @param height       the height
     * @param data         the data
     * @param highCompress the high compress
     */
    public GLByteArrayTexture(GLContext glContext, float left, float top, float width, float height, byte[] data, boolean highCompress) {
        super(glContext, left, top, width, height);

        mBitmapData = data;
        if (mBitmapData == null) {
            throw new IllegalArgumentException();
        }
        //mHighCompress = highCompress;
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLTexture#clear()
     */
    @Override
    public synchronized void clear() {
        super.clear();

        mBitmapData = null;
    }

    public void setSampleSize(int sampleSize) {
        mSampleSize = sampleSize;
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLTexture#loadBitmap()
     */
    @Override
    protected synchronized Bitmap loadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = mSampleSize;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = null;
        if (mBitmapData != null) {
            bitmap = BitmapFactory.decodeByteArray(mBitmapData, 0, mBitmapData.length, options);
        }
        mBitmapData = null;

        return bitmap;
    }
}
