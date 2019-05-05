package com.lanshifu.opengldemo.image.filter;

import android.content.Context;
import android.graphics.Bitmap;

import com.lanshifu.opengldemo.utils.ShaderManager;

/**
 * 放大滤镜
 */
public class ZoomFilter extends BaseFilter {

    public ZoomFilter(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    @Override
    protected ShaderManager.Param getProgram(){
        return ShaderManager.getParam(ShaderManager.ZOOM_SHADER);
    }
}
