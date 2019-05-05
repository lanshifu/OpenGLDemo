package com.lanshifu.opengldemo.image.filter;

import android.content.Context;
import android.graphics.Bitmap;

import com.lanshifu.opengldemo.utils.ShaderManager;

/**
 * 黑白滤镜比较好处理
 */
public class GrayFilter extends BaseFilter {

    public GrayFilter(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    @Override
    protected ShaderManager.Param getProgram(){
        return ShaderManager.getParam(ShaderManager.GRAY_SHADER);
    }
}
