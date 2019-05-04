package com.lanshifu.opengldemo.image;

import android.content.Context;
import android.graphics.Bitmap;

public class GrayFilter extends BaseFilter {

    public GrayFilter(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    @Override
    protected String getFragmentCode() {
        return super.getFragmentCode();
    }

    @Override
    protected String getVertexCode() {
        //返回黑白滤镜的片元着色器
        return "shader/filter/filter_fragment_black_white.glsl";
    }
}
