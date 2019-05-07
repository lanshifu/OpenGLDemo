package com.lanshifu.opengldemo.camera.camera2_surface_demo.filter;

import android.content.Context;

import com.lanshifu.opengldemo.utils.ShaderManager;

public class Camera2FilterZoom extends Camera2BaseFilter {

    public Camera2FilterZoom(Context context, int textureId) {
        super(context, textureId);
    }

    @Override
    protected ShaderManager.Param getProgram() {
        return ShaderManager.getParam(ShaderManager.CAMERA_ZOOM_SHADER);
    }
}
