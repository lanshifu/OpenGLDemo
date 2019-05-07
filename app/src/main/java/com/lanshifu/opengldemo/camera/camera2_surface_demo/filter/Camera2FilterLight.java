package com.lanshifu.opengldemo.camera.camera2_surface_demo.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.lanshifu.opengldemo.utils.ShaderManager;

public class Camera2FilterLight extends Camera2BaseFilter {

    private int uTimeHandle = 0;
    private long startTime= 0;

    public Camera2FilterLight(Context context, int textureId) {
        super(context, textureId);

        startTime = System.currentTimeMillis();
        uTimeHandle = GLES20.glGetUniformLocation(mProgram,"uTime");
    }

    @Override
    protected ShaderManager.Param getProgram() {
        return ShaderManager.getParam(ShaderManager.CAMERA_LIGHT_SHADER);
    }

    @Override
    public void draw(float[] transformMatrix) {
        super.draw(transformMatrix);

        //不断更新时间
        GLES20.glUniform1f(uTimeHandle, (System.currentTimeMillis() - startTime));
    }
}
