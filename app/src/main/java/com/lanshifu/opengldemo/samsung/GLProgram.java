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

import android.opengl.GLES20;

import java.util.Hashtable;

public class GLProgram {

    public static final int QUALIFIER_CONST = 101;
    public static final int QUALIFIER_ATTRIBUTE = 102;
    public static final int QUALIFIER_UNIFORM = 103;
    public static final int QUALIFIER_VARYING = 104;

    public static final int TYPE_VOID = 201;
    public static final int TYPE_BOOL = 202;
    public static final int TYPE_INT = 203;
    public static final int TYPE_FLOAT = 204;

    public static final int TYPE_VEC2 = 205;
    public static final int TYPE_VEC3 = 206;
    public static final int TYPE_VEC4 = 207;

    public static final int TYPE_BVEC2 = 208;
    public static final int TYPE_BVEC3 = 209;
    public static final int TYPE_BVEC4 = 210;

    public static final int TYPE_IVEC2 = 211;
    public static final int TYPE_IVEC3 = 212;
    public static final int TYPE_IVEC4 = 213;

    public static final int TYPE_MAT2 = 214;
    public static final int TYPE_MAT3 = 215;
    public static final int TYPE_MAT4 = 216;

    public static final int TYPE_SAMPLER2D = 217;
    public static final int TYPE_SAMPLER_EXTERNAL = 218;
    public static final int TYPE_SAMPLERCUBE = 219;

    public static final String INDEXER_VERTEX = "a_position";
    public static final String INDEXER_TEXCOORD = "a_texcoord";
    public static final String INDEXER_MVPMATRIX = "u_MVPMatrix";
    public static final String INDEXER_POINTSIZE = "a_pointsize";
    public static final String INDEXER_ALPHA = "u_alpha";
    public static final String INDEXER_THICKNESS = "u_thickness";
    public static final String INDEXER_TYPE = "u_type";
    public static final String INDEXER_STEP = "u_step";
    public static final String INDEXER_PARAMETER = "u_param";
    public static final String INDEXER_SAMPLER = "tex_sampler";
    public static final String INDEXER_FILL_COLOR = "fill_color";
    public static final String INDEXER_TINT_COLOR = "u_tint_color";
    public static final String INDEXER_FADING_POS = "u_fading_pos";
    public static final String INDEXER_FADING_OFFSET = "u_fading_offset";
    public static final String INDEXER_SIDE_FADING_POS = "u_side_fading_pos";
    public static final String INDEXER_FADING_ORIENTATION = "u_fading_orientation";

    private final Hashtable<String, NameIndexerObj> mNameIndexerObjMap = new Hashtable<>();

    private int mProgram = 0;

    public GLProgram(String strVSource, String strFSource) {
        mProgram = loadProgram(strVSource, strFSource);
    }

    public boolean addNameIndexer(String name, int qualifier, int type) {

        if (mProgram != 0) {
            NameIndexerObj obj = new NameIndexerObj();
//            obj.mQualifier = qualifier;
//            obj.mType = type;

            switch (qualifier) {
                case QUALIFIER_ATTRIBUTE:
                    obj.mHandle = GLES20.glGetAttribLocation(mProgram, name);
                    break;
                case QUALIFIER_UNIFORM:
                    obj.mHandle = GLES20.glGetUniformLocation(mProgram, name);
                    break;
            }
            mNameIndexerObjMap.put(name, obj);
            return true;
        }
        return false;
    }

    public NameIndexerObj getNameIndexer(String name) {
        return mNameIndexerObjMap.get(name);
    }

    public int getProgramID() {
        return mProgram;
    }

    public void release() {
        if (mProgram != 0) {
            //GLES20.glDetachShader();
            GLES20.glDeleteProgram(mProgram);
        }
        mNameIndexerObjMap.clear();
    }

    private void checkGlError(String op) {
        /*int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(op + ": glError " + error);
        }*/
    }

    private int loadProgram(String strVSource, String strFSource) {
        int iVShader = loadShader(GLES20.GL_VERTEX_SHADER, strVSource);
        if (iVShader == 0) {
            return 0;
        }
        int iFShader = loadShader(GLES20.GL_FRAGMENT_SHADER, strFSource);
        if (iFShader == 0) {
            return 0;
        }

        int iProgId = GLES20.glCreateProgram();
        if (iProgId != 0) {
            GLES20.glAttachShader(iProgId, iVShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(iProgId, iFShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(iProgId);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String info = GLES20.glGetProgramInfoLog(iProgId);
                GLES20.glDeleteProgram(iProgId);
                iProgId = 0;
                throw new RuntimeException("Could not link program: " + info);
            }
        }

        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);

        return iProgId;
    }

    private int loadShader(int iShaderType, String strSource) {
        int iShader = GLES20.glCreateShader(iShaderType);
        if (iShader != 0) {
            GLES20.glShaderSource(iShader, strSource);
            GLES20.glCompileShader(iShader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                String info = GLES20.glGetShaderInfoLog(iShader);
                GLES20.glDeleteShader(iShader);
                iShader = 0;
                throw new RuntimeException("Could not compile shader " + iShaderType + ":" + info);
            }
        }
        return iShader;
    }

    public static class NameIndexerObj {
        // Not using, For later use.
//        public int mQualifier = QUALIFIER_ATTRIBUTE;
//        public int mType = TYPE_VEC4;
        public int mHandle = 0;
    }
}
