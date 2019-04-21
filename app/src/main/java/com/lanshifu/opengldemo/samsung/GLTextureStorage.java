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

import android.util.Log;
import android.util.SparseArray;

/**
 * This class provides access to a centralized storage of the texture information for {@link GLResourceTexture}.
 * By using centralized texture information, you can save memories for the texture and reduce bitmap decoding time.
 */
class GLTextureStorage {
    /**
     * Array to hold resource and texture information.
     * Key : Resource ID.
     * Value : {@link TextureInfo}
     */
    private SparseArray<TextureInfo> mTextureMap = new SparseArray<>();

    @Deprecated
    public void dump() {
        for (int i = 0; i < mTextureMap.size(); i++) {
            TextureInfo info = mTextureMap.get(mTextureMap.keyAt(i));
            if (info != null) {
                Log.e("GLTextureStorage", "Info(" + i + ") res = " + mTextureMap.keyAt(i) + ", id = " + info.mTextureID + ", counter = " + info.mCounter + ", width = " + info.mWidth + ", height = " + info.mHeight);
            } else {
                Log.e("GLTextureStorage", "no info at key : " + mTextureMap.keyAt(i));
            }
        }
    }

    /**
     * Clear texture information map.
     */
    protected void clear() {
        mTextureMap.clear();
    }

    /**
     * Adds texture information to the texture information array {@link #mTextureMap}.
     * If the texture information for the resource is already exists, just increase reference counter for the resource.
     * If the reference counter for the resource is 1, texture information for the resource will be deleted.
     *
     * @param resourceId resource id to remove.
     */
    void addTexture(int resourceId, int textureId, float width, float height) {
        TextureInfo info = mTextureMap.get(resourceId);
        if (info == null) {
            mTextureMap.put(resourceId, new TextureInfo(textureId, 1, width, height));
        } else {
            mTextureMap.put(resourceId, new TextureInfo(textureId, info.mCounter + 1, width, height));
        }
    }

    /**
     * Gets resource texture information from storage.
     * This is only for {@link GLResourceTexture}
     *
     * @param resourceId resource ID
     * @return texture information ({@link TextureInfo})
     */
    TextureInfo getTextureInfo(int resourceId) {
        TextureInfo info = mTextureMap.get(resourceId);
        if (info != null) {
            return info;
        } else {
            return null;
        }
    }

    /**
     * Removes texture information from the texture information array {@link #mTextureMap}.
     * If the reference counter for the resource is greater than 1, just decrease reference counter for the resource.
     * If the reference counter for the resource is 1, texture information for the resource will be deleted.
     *
     * @param resourceId resource id to remove.
     * @return reference counter for the resource after remove.
     */
    int removeTexture(int resourceId) {
        TextureInfo info = mTextureMap.get(resourceId);
        if (info == null) {
            return 0;
        } else {
            if (info.mCounter == 1) {
                mTextureMap.delete(resourceId);
                return 0;
            } else {
                mTextureMap.put(resourceId, new TextureInfo(info.mTextureID, info.mCounter - 1, info.mWidth, info.mHeight));
                return info.mCounter - 1;
            }
        }
    }

    /**
     * Texture information class
     */
    static class TextureInfo {
        /**
         * Texture ID
         */
        int mTextureID;
        /**
         * Reference counter
         */
        int mCounter;
        /**
         * Resource width
         */
        float mWidth;
        /**
         * Resource height
         */
        float mHeight;

        /**
         * Constructor
         *
         * @param textureId texture id
         * @param counter   reference counter
         * @param width     resource width
         * @param height    resource height
         */
        TextureInfo(int textureId, int counter, float width, float height) {
            mTextureID = textureId;
            mCounter = counter;
            mWidth = width;
            mHeight = height;
        }
    }
}
