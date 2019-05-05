package com.lanshifu.opengldemo.image.filter;

import android.content.Context;
import android.graphics.Bitmap;

import com.lanshifu.opengldemo.utils.ShaderManager;

/**
 * 模糊滤镜
 */
public class BuzzyFilter extends BaseFilter {

    public BuzzyFilter(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    @Override
    protected ShaderManager.Param getProgram(){
        return ShaderManager.getParam(ShaderManager.BUZZY_SHADER);
    }
}
