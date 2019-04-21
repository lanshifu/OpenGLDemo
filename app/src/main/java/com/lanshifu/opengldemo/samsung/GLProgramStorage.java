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

import java.util.Collection;
import java.util.Hashtable;

public class GLProgramStorage {

    public static final int TYPE_PROGRAM_BASIC = 1001;
    public static final int TYPE_PROGRAM_TINT_BASIC = 1002;
    public static final int TYPE_PROGRAM_LINE = 1003;
    public static final int TYPE_PROGRAM_CIRCLE = 1004;
    public static final int TYPE_PROGRAM_ROUND_RECT = 1005;
    public static final int TYPE_PROGRAM_CIRCULAR_CLIP = 1006;
    public static final int TYPE_PROGRAM_FADE = 1007;
    public static final int TYPE_PROGRAM_RECTANGLE = 1008;
    public static final int TYPE_PROGRAM_SCALE_CIRCLE_TEXTURE = 1009;
    public static final int TYPE_PROGRAM_ITEM_COLOR_CHANGE = 1010;
    public static final int TYPE_PROGRAM_GRADIENT_WITH_POSITION = 1011;
    public static final int TYPE_PROGRAM_GRADIENT_RECTANGLE = 1012;
    public static final int TYPE_PROGRAM_GRADIENT_WITH_POSITION_LAND = 1013;
    public static final int TYPE_PROGRAM_GRADIENT_COLOR = 1014;
    public static final int TYPE_PROGRAM_GRADIENT_WITH_POSITION_AND_COLOR_CHANGE = 1015;

    //    @formatter:off
    public static final String BASE_VERTEX_SHADER =
            "precision highp float;\n" +
            "uniform mat4 u_MVPMatrix;\n" +
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texcoord;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  gl_Position = u_MVPMatrix * a_position;\n" +
            "  v_texcoord = a_texcoord;\n" +
            "}\n";

    public static final String BASE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(tex_sampler, v_texcoord) * u_alpha;\n" +
            "}\n";

    // Default Tint mode is 'PorterDuff.Mode.SRC_IN (Resulting color =  Dc * Sa)'
    public static final String BASE_TINT_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "uniform lowp vec4  u_tint_color;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  lowp vec4 color = texture2D(tex_sampler, v_texcoord);\n" +
            "  gl_FragColor = (u_tint_color * color.a) * u_alpha;\n" +
            "}\n";

    public static final String LINE_VERTEX_SHADER =
            "precision highp float;\n" +
            "uniform mat4 u_MVPMatrix;\n" +
            "attribute vec4 a_position;\n" +
            "attribute float a_pointsize;\n" +
            "void main() {\n" +
            "  gl_Position = u_MVPMatrix * a_position;\n" +
            "  gl_PointSize = a_pointsize;\n" +
            "}\n";

    public static final String LINE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp vec4 tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "  gl_FragColor = vec4(tex_sampler.rgb, 1.0) * u_alpha * tex_sampler.a;\n" +
            "}\n";

    public static final String RECTANGLE_FRAGMENT_SHADER =
            "precision highp float;\n" +
            "uniform lowp vec4 tex_sampler;\n" +
            "uniform lowp vec4 fill_color;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying vec2 v_texcoord;\n" +
            "uniform float u_thickness;\n" +
            "uniform float u_type;\n" +
            "uniform float u_param;\n" +
            "void main() {\n" +
            "  if (v_texcoord.x <= u_thickness || v_texcoord.x >= (1.0 - u_thickness) || v_texcoord.y <= u_thickness * u_param || v_texcoord.y >= (1.0 - u_thickness * u_param)) {\n" +
            "     gl_FragColor = vec4(tex_sampler.rgb, 1.0) * u_alpha * tex_sampler.a;\n" +
            "  } else if (u_type == 1.0) {\n" +
            "     gl_FragColor = vec4(fill_color.rgb, 1.0) * u_alpha * fill_color.a;\n" +
            "  } else {\n" +
            "     discard;\n" +
            "  }\n" +
            "}\n";

    // u_step   radius (0.03 ~ 0.3)
    // u_param  ratio = (width / height)
    public static final String ROUND_RECT_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp vec4 tex_sampler;\n" +
                    "uniform lowp vec4 fill_color;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying vec2 v_texcoord;\n" +
            "uniform float u_thickness;\n" +
                    "uniform float u_type;\n" +
            "uniform float u_step;\n" +
            "uniform float u_param;\n" +
            "const float center = 0.5;\n" +
            "void main() {\n" +
            "  float offset = 0.01 * abs(u_param);\n" +
            "  float smooth_delta = 0.05;\n" +
            "  vec2 new_center = vec2(abs(center * min(1.0, u_param)), center * max(1.0, u_param));\n" +
            "  vec2 new_texcoord = v_texcoord * vec2(abs(min(1.0, u_param)), max(1.0, u_param));\n" +
            "  vec2 size = new_center - vec2(offset + u_step * abs(u_param));\n"+
            "  float dist = length(max(abs(new_center - new_texcoord), size) - size) - u_step * abs(u_param);\n"+
                    "  float intensity;\n" +
                    "  if (u_type == 1.0) {\n" +
                    "     intensity = smoothstep(0.5 + smooth_delta, 0.5 - smooth_delta, dist / u_thickness * 10.0 / abs(u_param));\n" +
                    "     gl_FragColor = vec4(fill_color.rgb, 1.0) * intensity * u_alpha * fill_color.a;\n" +
                    "  } else {\n" +
                    "     intensity = smoothstep(0.5 + smooth_delta, 0.5 - smooth_delta, abs(dist / u_thickness) * 10.0 / abs(u_param));\n" +
                    "     gl_FragColor = vec4(tex_sampler.rgb, 1.0) * intensity * u_alpha * tex_sampler.a;\n" +
                    "  }\n" +
            "}\n";

    public static final String CIRCLE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp vec4 tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying vec2 v_texcoord;\n" +
            "uniform float u_thickness;\n" +
            "uniform float u_type;\n" +
            "uniform float u_param;\n" +
            "const float center = 0.5;\n" +

            "void main() {\n" +
            "  if (abs(distance(v_texcoord, vec2(center, center))) <= center) {\n" +
            "     if (u_type == 1.0 || pow(v_texcoord.x - center, 2.0) / pow(center - u_thickness, 2.0) + pow((1.0 - v_texcoord.y - center), 2.0) / pow(center - u_thickness * u_param, 2.0) >= 1.0) {\n" +
            "         gl_FragColor = vec4(tex_sampler.rgb, 1.0) * u_alpha * tex_sampler.a;\n" +
            "     } else {\n" +
            "      discard;\n" +
            "     }\n" +
            "  } else {\n" +
            "      discard;\n" +
            "  }\n" +
            "}\n";

    public static final String SCALE_CIRCLE_TEXTURE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying vec2 v_texcoord;\n" +
            "uniform float u_param;\n" +
            "const float center = 0.5;\n" +
            "void main() {\n" +
            "  float dist = distance(vec2(center, center), v_texcoord);\n" +
            "  float delta = 0.009;\n" +
            "  float alpha = smoothstep(0.5 - delta, 0.5, dist);\n" +
            "  vec2 new_texcoord = v_texcoord - vec2(center);\n" +
            "  new_texcoord = new_texcoord * u_param;\n" +
            "  new_texcoord = new_texcoord + vec2(center);\n" +
            "  gl_FragColor = mix(texture2D(tex_sampler, new_texcoord), vec4(0.0, 0.0, 0.0, 0.0), alpha) * u_alpha;\n" +
            "}\n";

    // u_step  0.0 ~ 1.0
    public static final String CIRCULAR_CLIP_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "varying lowp vec2 v_texcoord;\n" +
            "uniform float u_step;\n" +
            "const float diameter = 0.9999;\n" +
            "const float center = 0.5;\n" +

            "void main() {\n" +
            "  vec2 coord = v_texcoord - vec2(center, center);\n" +
            "  float dist = length(coord / diameter);\n" +
            "  if ((dist < center) && (dist > center * u_step)) {\n" +
            "      gl_FragColor = texture2D(tex_sampler, v_texcoord) * u_alpha;\n" +
            "  } else {\n" +
            "      gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);\n" +
            "  }\n" +
            "}\n";

    // u_step  top to bottom   0.0 ~  1.0
    //         bottom to top   1.0 ~  2.0
    //         left to right   0.0 ~ -1.0
    //         right to left  -1.0 ~ -2.0
    // u_param fade length
    public static final String FADE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "varying lowp vec2 v_texcoord;\n" +
            "uniform float u_step;\n" +
            "uniform float u_param;\n" +
            "uniform float u_alpha;\n" +
            "const float accel_pos = 0.2;\n" +
            "void main() {\n" +
            "    float orientation_pos = sign(u_step);\n" +
            "    float direction = sign(1.0 - abs(u_step));\n" +
            "    float alpha = 1.0;\n" +
            "    float pos = ((1.0 - direction) + direction * abs(u_step)) * (1.0 + u_param);\n" +
            "    if (pos < accel_pos) {\n" +
            "        pos = sin(radians(90.0 * (1.0 / accel_pos) * pos)) * accel_pos;\n" +
            "    }\n" +
            "    orientation_pos = v_texcoord.x * sign(1.0 - orientation_pos) + v_texcoord.y * sign(1.0 + orientation_pos);\n" +
            "    if (orientation_pos < pos) {\n" +
            "        alpha = max(0.0, (orientation_pos - (pos - u_param)) / u_param);\n" +
            "    }\n" +
            "    direction = sign(direction + 0.5);\n" +
            "    alpha = sign(1.0 - direction) + direction * alpha;\n" +
            "    gl_FragColor = texture2D(tex_sampler, v_texcoord) * (alpha * u_alpha);\n" +
            "}\n";

    public static final String ITEM_COLOR_CHANGE_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "uniform float u_param;\n" +
            "uniform vec2 u_fading_pos;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
                    "    float x_pos = (gl_FragCoord.x / u_param);\n" +
            "    float start = (u_fading_pos.x / u_param);\n" +
            "    float end = (u_fading_pos.y / u_param);\n" +
                    "    vec4 color = texture2D(tex_sampler, v_texcoord) * u_alpha;\n" +
                    "    if (x_pos > start && x_pos < end) {\n" +
                    "       color = vec4(0.0, 0.0, 0.0, color.a) * u_alpha;\n" +
                    "    }\n" +
                    "    gl_FragColor = color;\n" +
            "}\n";

    public static final String GRADIENT_WITH_POSITION_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "uniform float u_param;\n" +
            "uniform vec2 u_fading_pos;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "    float x_pos = (gl_FragCoord.x / u_param);\n" +
            "    float start = (u_fading_pos.x / u_param);\n" +
            "    float end = (u_fading_pos.y / u_param);\n" +
            "    float new_start = (u_param - u_fading_pos.y) / u_param;\n" +
            "    float new_end = (u_param - u_fading_pos.x) / u_param;\n" +
            "    if (x_pos > start && x_pos < end) {\n" +
            "       float offset = smoothstep(start, end, x_pos);\n" +
            "       gl_FragColor = mix(texture2D(tex_sampler, v_texcoord), vec4(0.0, 0.0, 0.0, 0.0), 1.0 - offset) * u_alpha;\n" +
            "    } else if (x_pos >= new_start && x_pos <= new_end) {\n" +
            "       float offset = smoothstep(new_end, new_start, x_pos);\n" +
            "       gl_FragColor = mix(texture2D(tex_sampler, v_texcoord), vec4(0.0, 0.0, 0.0, 0.0), 1.0 - offset) * u_alpha;\n" +
            "    } else {\n" +
            "       gl_FragColor = texture2D(tex_sampler, v_texcoord) * u_alpha;\n" +
            "    }\n" +
            "}\n";

    public static final String GRADIENT_WITH_POSITION_SHADER_LAND =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "uniform float u_param;\n" +
            "uniform vec2 u_fading_pos;\n" +
            "uniform vec2 u_fading_offset;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            "   float y_pos = (gl_FragCoord.y / u_param);\n" +
            "   float start = ((u_fading_pos.x + u_fading_offset.x) / u_param);\n" +
            "   float end = ((u_fading_pos.y + u_fading_offset.x) / u_param);\n" +
            "   float new_start = (u_param - (u_fading_pos.y + u_fading_offset.x)) / u_param;\n" +
            "   float new_end = (u_param - (u_fading_pos.x  + u_fading_offset.x)) / u_param;\n" +
            "   if (y_pos > start && y_pos < end) {\n" +
            "       float offset = smoothstep(start, end, y_pos);\n" +
            "       gl_FragColor = mix(texture2D(tex_sampler, v_texcoord), vec4(0.0, 0.0, 0.0, 0.0), 1.0 - offset) * u_alpha;\n" +
            "   } else if (y_pos >= new_start && y_pos <= new_end) {\n" +
            "       float offset = smoothstep(new_end, new_start, y_pos);\n" +
            "       gl_FragColor = mix(texture2D(tex_sampler, v_texcoord), vec4(0.0, 0.0, 0.0, 0.0), 1.0 - offset) * u_alpha;\n" +
            "   } else {\n" +
            "       gl_FragColor = texture2D(tex_sampler, v_texcoord) * u_alpha;\n" +
            "   }\n" +
            "}\n";

    public static final String GRADIENT_WITH_POSITION_AND_COLOR_CHANGE_SHADER =
            "precision mediump float;\n" +
            "uniform lowp sampler2D tex_sampler;\n" +
            "uniform lowp float u_alpha;\n" +
            "uniform float u_param;\n" +
            "uniform vec2 u_fading_pos;\n" +
            "uniform vec2 u_fading_offset;\n" +
            "varying vec2 v_texcoord;\n" +
            "uniform vec2 u_side_fading_pos;\n" +
            "uniform int u_fading_orientation;\n" +
            "void main() {\n" +
            "   float pos, side_new_start, side_new_end;\n" +
            "   float side_start = (u_side_fading_pos.x / u_param);\n" +
            "   float side_end = (u_side_fading_pos.y / u_param);\n" +
            "   float color_start = ((u_fading_pos.x + u_fading_offset.y) / u_param);\n" +
            "   float color_end = ((u_fading_pos.y + u_fading_offset.y) / u_param);\n" +
            "   if (u_fading_orientation == 0) {\n" +
            "       pos = (gl_FragCoord.x / u_param);\n" +
            "       side_new_start = (u_param - u_side_fading_pos.y) / u_param;\n" +
            "       side_new_end = (u_param - u_side_fading_pos.x) / u_param;\n" +
            "   } else {\n" +
            "       pos = (gl_FragCoord.y / u_param);\n" +
            "       side_new_start = (u_param - (u_side_fading_pos.y + u_fading_offset.x)) / u_param;\n" +
            "       side_new_end = (u_param - (u_side_fading_pos.x  + u_fading_offset.x)) / u_param;\n" +
            "   } \n" +
            "   if (pos > side_start && pos < side_end) {\n" +
            "       float offset = smoothstep(side_start, side_end, pos);\n" +
            "       gl_FragColor = mix(texture2D(tex_sampler, v_texcoord), vec4(0.0, 0.0, 0.0, 0.0), 1.0 - offset) * u_alpha;\n" +
            "   } else if (pos >= side_new_start && pos <= side_new_end) {\n" +
            "       float offset = smoothstep(side_new_end, side_new_start, pos);\n" +
            "       gl_FragColor = mix(texture2D(tex_sampler, v_texcoord), vec4(0.0, 0.0, 0.0, 0.0), 1.0 - offset) * u_alpha;\n" +
            "   } else {\n" +
            "       vec4 color = texture2D(tex_sampler, v_texcoord) * u_alpha;\n" +
            "       if (pos > color_start && pos < color_end) {\n" +
            "          color = vec4(0.0, 0.0, 0.0, color.a) * u_alpha;\n" +
            "       }\n" +
            "       gl_FragColor = color;\n" +
            "   }\n" +
            "}\n";

    public static final String GRADIENT_RECTANGLE_SHADER =
            "precision mediump float;\n" +
                    "uniform lowp vec4 fill_color;\n" +
                    "uniform float u_step;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = vec4(fill_color.rgb, v_texcoord.y * u_step);\n" +
                    "}\n";
//    @formatter:on

    private final Hashtable<Integer, GLProgram> mProgramObjMap = new Hashtable<>();

    private GLProgramStorage() {
    }

    public static GLProgramStorage getInstance() {
        return new GLProgramStorage();
    }

    public static void releaseInstance(GLProgramStorage storage) {
        if (storage != null) {
            storage.deleteStorage();
            storage = null;
        }
    }

    public synchronized boolean addProgram(int type) {

        if (mProgramObjMap.containsKey(Integer.valueOf(type)))
            return false;

        GLProgram program = null;

        switch (type) {
            case TYPE_PROGRAM_BASIC:
                program = new GLProgram(BASE_VERTEX_SHADER, BASE_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                break;

            case TYPE_PROGRAM_TINT_BASIC:
                program = new GLProgram(BASE_VERTEX_SHADER, BASE_TINT_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_TINT_COLOR, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                break;

            case TYPE_PROGRAM_LINE:
                program = new GLProgram(LINE_VERTEX_SHADER, LINE_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_POINTSIZE, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_SAMPLER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                break;

            case TYPE_PROGRAM_RECTANGLE:
                program = new GLProgram(BASE_VERTEX_SHADER, RECTANGLE_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_SAMPLER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_THICKNESS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_TYPE, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FILL_COLOR, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                break;

            case TYPE_PROGRAM_CIRCLE:
                program = new GLProgram(BASE_VERTEX_SHADER, CIRCLE_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_SAMPLER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_THICKNESS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_TYPE, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                break;

            case TYPE_PROGRAM_SCALE_CIRCLE_TEXTURE:
                program = new GLProgram(BASE_VERTEX_SHADER, SCALE_CIRCLE_TEXTURE_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                break;

            case TYPE_PROGRAM_ROUND_RECT:
                program = new GLProgram(BASE_VERTEX_SHADER, ROUND_RECT_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_SAMPLER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_STEP, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_THICKNESS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_TYPE, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FILL_COLOR, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                break;

            case TYPE_PROGRAM_CIRCULAR_CLIP:
                program = new GLProgram(BASE_VERTEX_SHADER, CIRCULAR_CLIP_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_STEP, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                break;

            case TYPE_PROGRAM_FADE:
                program = new GLProgram(BASE_VERTEX_SHADER, FADE_FRAGMENT_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_STEP, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                break;

            case TYPE_PROGRAM_ITEM_COLOR_CHANGE:
                program = new GLProgram(BASE_VERTEX_SHADER, ITEM_COLOR_CHANGE_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FADING_POS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                break;

            case TYPE_PROGRAM_GRADIENT_RECTANGLE:
                program = new GLProgram(BASE_VERTEX_SHADER, GRADIENT_RECTANGLE_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_STEP, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FILL_COLOR, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC4);
                break;

            case TYPE_PROGRAM_GRADIENT_WITH_POSITION:
                program = new GLProgram(BASE_VERTEX_SHADER, GRADIENT_WITH_POSITION_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FADING_POS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                break;

            case TYPE_PROGRAM_GRADIENT_WITH_POSITION_LAND:
                program = new GLProgram(BASE_VERTEX_SHADER, GRADIENT_WITH_POSITION_SHADER_LAND);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FADING_POS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_FADING_OFFSET, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                break;

            case TYPE_PROGRAM_GRADIENT_WITH_POSITION_AND_COLOR_CHANGE:
                program = new GLProgram(BASE_VERTEX_SHADER, GRADIENT_WITH_POSITION_AND_COLOR_CHANGE_SHADER);
                program.addNameIndexer(GLProgram.INDEXER_VERTEX, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC4);
                program.addNameIndexer(GLProgram.INDEXER_TEXCOORD, GLProgram.QUALIFIER_ATTRIBUTE, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_MVPMATRIX, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_MAT4);
                program.addNameIndexer(GLProgram.INDEXER_ALPHA, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_PARAMETER, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_FLOAT);
                program.addNameIndexer(GLProgram.INDEXER_FADING_POS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_FADING_OFFSET, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_SIDE_FADING_POS, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_VEC2);
                program.addNameIndexer(GLProgram.INDEXER_FADING_ORIENTATION, GLProgram.QUALIFIER_UNIFORM, GLProgram.TYPE_INT);
                break;
        }

        if (program != null)
            mProgramObjMap.put(Integer.valueOf(type), program);

        return true;
    }

    public synchronized GLProgram getProgram(int type) {

        return mProgramObjMap.get(Integer.valueOf(type));

    }

    private void deleteStorage() {
        Collection<GLProgram> collection = mProgramObjMap.values();
        for (GLProgram obj : collection) {
            if (obj instanceof GLProgram) {
                obj.release();
            }
        }
        mProgramObjMap.clear();
    }
}
