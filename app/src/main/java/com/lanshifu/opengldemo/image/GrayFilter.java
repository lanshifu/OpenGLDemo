package com.lanshifu.opengldemo.image;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 黑白滤镜比较好处理
 */
public class GrayFilter extends BaseFilter {

    public GrayFilter(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    @Override
    protected String getFragmentCode() {
        return "shader/filter/filter_fragment_gray.glsl";
    }


}
