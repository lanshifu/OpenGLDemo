package com.lanshifu.opengldemo.image.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.lanshifu.opengldemo.utils.ShaderManager;

/**
 * 发光滤镜
 */
public class LightFilter extends BaseFilter {

    private int uTimeHandle = 0;
    private long startTime= 0;

    public LightFilter(Context context, Bitmap bitmap) {
        super(context, bitmap);

        startTime = System.currentTimeMillis();
        uTimeHandle = GLES20.glGetUniformLocation(mProgram,"uTime");
    }

    @Override
    protected ShaderManager.Param getProgram(){
        return ShaderManager.getParam(ShaderManager.LIGHT_SHADER);
    }

    @Override
    public void draw() {
        super.draw();

        //不断更新时间
        GLES20.glUniform1f(uTimeHandle, (System.currentTimeMillis() - startTime));
    }
}
