package com.lanshifu.opengldemo.camera.camera2_surface_demo.filter;

import android.content.Context;

import com.lanshifu.opengldemo.utils.ShaderManager;

/**
 * 没有滤镜效果
 */
public class Camera2FilterNone extends Camera2BaseFilter {

    public Camera2FilterNone(Context context, int textureId) {
        super(context, textureId);
    }

    @Override
    protected ShaderManager.Param getProgram() {
        return ShaderManager.getParam(ShaderManager.CAMERA_BASE_SHADER);
    }
}
