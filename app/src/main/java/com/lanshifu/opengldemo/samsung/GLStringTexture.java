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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;

import com.lanshifu.opengldemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class GLStringTexture.
 */
public class GLStringTexture extends GLTexture {

    /**
     * The Constant TAG.
     */
    private static final String TAG = "GLStringTexture";
    private static final float DEFAULT_SHADOW_OFFSET_X_DIP = 1f;
    private static final float DEFAULT_SHADOW_OFFSET_Y_DIP = 1f;
    private static final float DEFAULT_SHADOW_RADIUS_DIP = 1f;
    private static final float DEFAULT_STROKE_WIDTH = 1f;
    private static final float DEFAULT_FADING_EDGE_WIDTH_DIP = 20;
    private static final int DEFAULT_TEXT_COLOR = GLContext.getColor(R.color.default_text_color);
    private static final int DEFAULT_SHADOW_COLOR = GLContext.getColor(R.color.default_black_color);
    private static final int DEFAULT_STROKE_COLOR = GLContext.getColor(R.color.default_black_color);
    /**
     * The default padding.
     */
    private static final int DEFAULT_PADDING = 1;
    /**
     * The default line space.
     */
    private static final int DEFAULT_LINE_SPACE = 5;
    private static final int NUM_OF_ELLIPSIS_CHARACTER = 3;
    /**
     * The m paint.
     */
    private Paint mPaint;

    /**
     * The m metrics.
     */
    private FontMetricsInt mMetrics;

    /**
     * The m text.
     */
    private String mText;

    /**
     * The m width.
     */
    private int mWidth = 0;

    /**
     * The m height.
     */
    private int mHeight = 0;

    /**
     * The m string width.
     */
    private int mStringWidth = 0;

    /**
     * The m string height.
     */
    private int mStringHeight = 0;

    /**
     * The m h align.
     */
    private int mHAlign = H_ALIGN_LEFT;

    /**
     * The m v align.
     */
    private int mVAlign = V_ALIGN_TOP;

    /**
     * The m color.
     */
    private int mColor = DEFAULT_TEXT_COLOR;

    private boolean mShadow = true;

    /**
     * The m stroke. - Outline stroke
     */
    private boolean mStroke = false;

    private boolean mBold = false;

    private float mSize = 0;

    private float mFadingEdgeWidth = 0;

    private boolean mFadingEdge = true;

    // Shadow related variables.
    private int mShadowColor = DEFAULT_SHADOW_COLOR;
    private float mShadowOffsetX;
    private float mShadowOffsetY;
    private float mShadowRadius;

    private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private int mStrokeColor = DEFAULT_STROKE_COLOR;

    private int mLineSpace = DEFAULT_LINE_SPACE;

    /**
     * Instantiates a new tw gl string texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param hAlign    the h align
     * @param vAlign    the v align
     * @param text      the text
     * @param textSize  the text size
     * @param color     the color
     */
    public GLStringTexture(GLContext glContext, float left, float top, float width, float height, int hAlign, int vAlign, String text, float textSize, int color, boolean shadow) {
        super(glContext, left, top, width, height);

        mText = text;
        mSize = textSize;
        mColor = color;
        mShadow = shadow;

        mHAlign = hAlign;
        mVAlign = vAlign;

        mPaint = new Paint();
        if (textSize != 0)
            mPaint.setTextSize(textSize);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);

        mMetrics = mPaint.getFontMetricsInt();

        mWidth = (int) width;
        mHeight = (int) height;

        if (mWidth <= 0 || mHeight <= 0) {
            Log.d(TAG, "mWidth : " + mWidth + ", mHeight : " + mHeight);
        }

        mStringWidth = (int) Math.ceil(mPaint.measureText(mText)) + DEFAULT_PADDING * 2;
        mStringHeight = mMetrics.descent - mMetrics.ascent + DEFAULT_PADDING * 2;
        mSizeSpecified = true;
        init();
    }

    /**
     * Instantiates a new tw gl string texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param hAlign    the h align
     * @param vAlign    the v align
     * @param text      the text
     * @param textSize  the text size
     * @param type      the text type
     * @param color     the color
     * @param shadow    the shadow
     */
    public GLStringTexture(GLContext glContext, float left, float top, float width, float height, int hAlign, int vAlign, String text, float textSize, Typeface type, int color, boolean shadow) {
        super(glContext, left, top, width, height);

        mText = text;
        mSize = textSize;
        mColor = color;
        mShadow = shadow;

        mHAlign = hAlign;
        mVAlign = vAlign;

        mPaint = new Paint();
        if (textSize != 0) {
            mPaint.setTextSize(textSize);
        }
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(type);

        mMetrics = mPaint.getFontMetricsInt();

        mWidth = (int) width;
        mHeight = (int) height;

        if (mWidth <= 0 || mHeight <= 0) {
            Log.d(TAG, "mWidth : " + mWidth + ", mHeight : " + mHeight);
        }

        mStringWidth = (int) Math.ceil(mPaint.measureText(mText)) + DEFAULT_PADDING * 2;
        mStringHeight = mMetrics.descent - mMetrics.ascent + DEFAULT_PADDING * 2;
        mSizeSpecified = true;
        init();
    }

    /**
     * Instantiates a new tw gl string texture.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param text      the text
     * @param textSize  the text size
     * @param color     the color
     */
    public GLStringTexture(GLContext glContext, float left, float top, String text, float textSize, int color, boolean shadow) {
        super(glContext, left, top);

        mText = text;
        mSize = textSize;
        mColor = color;
        mShadow = shadow;

        mPaint = new Paint();
        if (textSize != 0) {
            mPaint.setTextSize(textSize);
        }
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mMetrics = mPaint.getFontMetricsInt();

        mStringWidth = (int) Math.ceil(mPaint.measureText(mText)) + DEFAULT_PADDING * 2;
        mStringHeight = mMetrics.descent - mMetrics.ascent + DEFAULT_PADDING * 2;

        mWidth = mStringWidth;
        mHeight = mStringHeight;

        if (mWidth <= 0 || mHeight <= 0) {
            Log.d(TAG, "mWidth : " + mWidth + ", mHeight : " + mHeight);
        }

        mSizeSpecified = true;
        init();
    }

    public int getAvailableRows() {
        return mHeight / mStringHeight;
    }

    public int getStringHeight() {
        return mStringHeight;
    }

    public int getStringWidth() {
        return mStringWidth;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return mText;
    }

    public synchronized void setText(String text) {
        mText = text;
        mStringWidth = (int) Math.ceil(mPaint.measureText(mText)) + DEFAULT_PADDING * 2;
        if (!getSizeGiven()) {
            mWidth = mStringWidth;
            if (mWidth <= 0) {
                Log.d(TAG, "setText - mWidth : " + mWidth);
            }
            super.setSize(mWidth, mHeight);
        }
        reLoad();
    }

    public synchronized void setAlign(int hAlign, int vAlign) {
        mHAlign = hAlign;
        mVAlign = vAlign;
        reLoad();
    }

    public synchronized void setBold(boolean bold) {
        mBold = bold;
        reLoad();
    }

    public synchronized void setBoldColor(boolean bold, int color) {
        mBold = bold;
        mColor = color;
        reLoad();
    }

    public synchronized void setColor(int color) {
        mColor = color;
        reLoad();
    }

    public void setFadingEdge(boolean fading) {
        mFadingEdge = fading;
    }

    public void setFadingEdgeWidth(float width) {
        mFadingEdgeWidth = width;
        reLoad();
    }

    public synchronized void setFontSize(float size) {
        mSize = size;
        mPaint.setTextSize(mSize);

        mMetrics = mPaint.getFontMetricsInt();

        mStringWidth = (int) Math.ceil(mPaint.measureText(mText)) + DEFAULT_PADDING * 2;
        mStringHeight = mMetrics.descent - mMetrics.ascent + DEFAULT_PADDING * 2;

        if (!getSizeSpecified()) {
            mWidth = mStringWidth;
            mHeight = mStringHeight;
            if (mWidth <= 0 || mHeight <= 0) {
                Log.d(TAG, "setFontSize - mWidth : " + mWidth + ", mHeight : " + mHeight);
            }
        }
        reLoad();
    }

    @Override
    public synchronized void setHeight(float height) {
        super.setHeight(height);
        mHeight = (int) height;

        if (mHeight <= 0) {
            Log.d(TAG, "setHeight - mHeight : " + mHeight);
        }

        reLoad();
    }

    /**
     * Sets the layout.
     *
     * @param hAlign the h align
     * @param vAlign the v align
     */
    public void setLayout(int hAlign, int vAlign) {
        switch (hAlign) {
            case H_ALIGN_LEFT:
            case H_ALIGN_CENTER:
            case H_ALIGN_RIGHT:
                mHAlign = hAlign;
                break;
            default:
                mHAlign = H_ALIGN_LEFT;
                break;
        }
        switch (vAlign) {
            case V_ALIGN_TOP:
            case V_ALIGN_MIDDLE:
            case V_ALIGN_BOTTOM:
                mVAlign = vAlign;
                break;
            default:
                mVAlign = V_ALIGN_MIDDLE;
                break;
        }
        reLoad();
    }

    public void setLineSpace(int linespace) {
        mLineSpace = linespace;
    }

    public void setShadowColor(int color) {
        if (mShadowColor != color) {
            mShadowColor = color;
            reLoad();
        }
    }

    public void setShadowLayer(boolean visibility, float radius, float offsetX, float offsetY, int color) {
        boolean changed = false;
        if (mShadow != visibility) {
            mShadow = visibility;
            changed = true;
        }
        if (!GLUtil.floatEquals(mShadowRadius, radius)) {
            mShadowRadius = radius;
            changed = true;
        }
        if (!GLUtil.floatEquals(mShadowOffsetX, offsetX)) {
            mShadowOffsetX = offsetX;
            changed = true;
        }
        if (!GLUtil.floatEquals(mShadowOffsetY, offsetY)) {
            mShadowOffsetY = offsetY;
            changed = true;
        }
        if (mShadowColor != color) {
            mShadowColor = color;
            changed = true;
        }
        if (changed) {
            reLoad();
        }
    }

    public void setShadowOffset(float offsetX, float offsetY) {
        boolean result = false;
        if (!GLUtil.floatEquals(mShadowOffsetX, offsetX)) {
            mShadowOffsetX = offsetX;
            result = true;
        }
        if (!GLUtil.floatEquals(mShadowOffsetY, offsetY)) {
            mShadowOffsetY = offsetY;
            result = true;
        }
        if (result) {
            reLoad();
        }
    }

    public void setShadowRadius(float radius) {
        if (!GLUtil.floatEquals(mShadowRadius, radius)) {
            mShadowRadius = radius;
            reLoad();
        }
    }

    public void setShadowVisibility(boolean visibility) {
        mShadow = visibility;
        reLoad();
    }

    @Override
    public synchronized void setSize(float width, float height) {
        super.setSize(width, height);
        mWidth = (int) width;
        mHeight = (int) height;

        if (mWidth <= 0 || mHeight <= 0) {
            Log.d(TAG, "setSize - mWidth : " + mWidth + ", mHeight : " + mHeight);
        }

        reLoad();
    }

    public void setStroke(boolean visibility, float width, int color) {
        boolean changed = false;

        if (mStroke != visibility) {
            mStroke = visibility;
            changed = true;
        }
        if (!GLUtil.floatEquals(mStrokeWidth, width)) {
            mStrokeWidth = width;
            changed = true;
        }
        if (mStrokeColor != color) {
            mStrokeColor = color;
            changed = true;
        }
        if (changed) {
            reLoad();
        }
    }

    public void setStrokeColor(int color) {
        if (mStrokeColor != color) {
            mStrokeColor = color;
            reLoad();
        }
    }

    public void setStrokeVisibility(boolean visibility) {
        if (mStroke != visibility) {
            mStroke = visibility;
            reLoad();
        }
    }

    public void setStrokeWidth(float width) {
        if (!GLUtil.floatEquals(mStrokeWidth, width)) {
            mStrokeWidth = width;
            reLoad();
        }
    }

    public synchronized void setText(String text, float textSize, int color) {
        mText = text;
        mSize = textSize;
        mColor = color;

        mPaint.setTextSize(mSize);

        mMetrics = mPaint.getFontMetricsInt();

        mStringWidth = (int) Math.ceil(mPaint.measureText(mText)) + DEFAULT_PADDING * 2;
        mStringHeight = mMetrics.descent - mMetrics.ascent + DEFAULT_PADDING * 2;

        if (!getSizeSpecified()) {
            mWidth = mStringWidth;
            mHeight = mStringHeight;
            if (mWidth <= 0 || mHeight <= 0) {
                Log.d(TAG, "setText - mWidth : " + mWidth + ", mHeight : " + mHeight);
            }
        }
        reLoad();
    }

    public synchronized void setTextScaleX(float scaleX) {
        mPaint.setTextScaleX(scaleX);
        reLoad();
    }

    public synchronized void setTypeface(Typeface type) {
        mPaint.setTypeface(type);
    }

    public synchronized void setUnderline(boolean underline) {
        mPaint.setUnderlineText(underline);
    }

    @Override
    public synchronized void setWidth(float width) {
        super.setWidth(width);
        mWidth = (int) width;

        if (mWidth <= 0) {
            Log.d(TAG, "setWidth - mWidth : " + mWidth);
        }

        reLoad();
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLTexture#loadBitmap()
     */
    @Override
    protected synchronized Bitmap loadBitmap() {
        mPaint.setFakeBoldText(mBold);
        mPaint.setColor(mColor);
        mPaint.setShader(null);
        if (mShadow) {
            mPaint.setShadowLayer(mShadowRadius, mShadowOffsetX, mShadowOffsetY, mShadowColor);
        }

        if (mWidth <= 0 || mHeight <= 0) {
            Log.d(TAG, "loadBitmap - mWidth : " + mWidth + ", mHeight : " + mHeight + ", mText : " + getText());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (mWidth < mStringWidth || getNumOfNewLineChar(mText) > 0) { // Multi-line
            drawMultiLineString(canvas);
        } else { // Single line
            drawSingleLineString(canvas);
        }
        return bitmap;
    }

    /**
     * Adds last string to result string array(multi-line string).
     *
     * @param resultStringArray the result string.
     * @param start             the start index of last string.
     * @see #wordBreak(int)
     */
    private void addLastString(List<String> resultStringArray, int start) {
        if (mFadingEdge) {
            resultStringArray.add(mText.substring(start, mText.length()));
        } else {
            resultStringArray.add(insertEllipsis(mText.substring(start, mText.length()), mWidth));
        }
    }

    /**
     * Draws multi-line string to canvas.
     *
     * @param canvas the canvas to draw.
     * @see #loadBitmap()
     */
    private void drawMultiLineString(Canvas canvas) {
        float top = 0;
        float left = 0;

        List<String> resultString = wordBreak(getAvailableRows());

        if (resultString != null) {
            for (int i = 0; i < resultString.size(); i++) {
                top = getMultiLineStringTopPosition(resultString.size(), i);
                boolean isNeedResetTextShader = false;
                if (i == resultString.size() - 1 && mFadingEdge && isFadingNeeded(resultString.get(i), mWidth)) {
                    Shader shader = new LinearGradient(mWidth - mFadingEdgeWidth, 0, mWidth, 0, (mStroke ? mStrokeColor : mColor) | 0xFF000000, (mStroke ? mStrokeColor
                            : mColor) & 0x00FFFFFF, Shader.TileMode.CLAMP);
                    mPaint.clearShadowLayer();
                    mPaint.setShader(shader);
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                    isNeedResetTextShader = true;
                }
                left = getMultiLineStringLeftPosition(mPaint.measureText(resultString.get(i)));
                if (mStroke) {
                    mPaint.setColor(mStrokeColor);
                    Style originalStyle = mPaint.getStyle();
                    mPaint.setStyle(Style.STROKE);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    canvas.drawText(resultString.get(i), left, top, mPaint);
                    mPaint.setStyle(originalStyle);
                    mPaint.setColor(mColor);
                    if (isNeedResetTextShader) {
                        if (GLUtil.isLocaleRTL()) {
                            Shader shader = new LinearGradient(0, 0, mFadingEdgeWidth, 0, mColor & 0x00FFFFFF, mColor | 0xFF000000, Shader.TileMode.CLAMP);
                            mPaint.setShader(shader);
                        } else {
                            Shader shader = new LinearGradient(mWidth - mFadingEdgeWidth, 0, mWidth, 0, mColor | 0xFF000000, mColor & 0x00FFFFFF, Shader.TileMode.CLAMP);
                            mPaint.setShader(shader);
                        }
                    }
                }
                canvas.drawText(resultString.get(i), left, top, mPaint);
            }
        }
    }

    /**
     * Draws single-line string to canvas.
     *
     * @param canvas the canvas to draw.
     * @see #loadBitmap()
     */
    private void drawSingleLineString(Canvas canvas) {
        float top = 0;
        float left = 0;


        switch (mVAlign) {
            case V_ALIGN_TOP:
                top = -mMetrics.ascent;
                break;
            case V_ALIGN_MIDDLE:
                top = (mHeight - (mMetrics.descent - mMetrics.ascent)) / 2 - mMetrics.ascent;
                break;
            case V_ALIGN_BOTTOM:
                top = mHeight - mMetrics.descent;
                break;
            default:
                break;
        }

        switch (mHAlign) {
            case H_ALIGN_LEFT:
                mPaint.setTextAlign(Paint.Align.LEFT);
                if (mShadow && mShadowOffsetX + mShadowRadius < 0) {
                    left = DEFAULT_PADDING + Math.abs(mShadowOffsetX + mShadowRadius);
                } else {
                    left = DEFAULT_PADDING;
                }
                if (mStroke) {
                    mPaint.setColor(mStrokeColor);
                    Style originalStyle = mPaint.getStyle();
                    mPaint.setStyle(Style.STROKE);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    canvas.drawText(mText, left, top, mPaint);
                    mPaint.setStyle(originalStyle);
                    mPaint.setColor(mColor);
                }
                canvas.drawText(mText, left, top, mPaint);
                break;
            case H_ALIGN_CENTER:
                mPaint.setTextAlign(Paint.Align.CENTER);
                if (mStroke) {
                    mPaint.setColor(mStrokeColor);
                    Style originalStyle = mPaint.getStyle();
                    mPaint.setStyle(Style.STROKE);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    canvas.drawText(mText, mWidth / 2f, top, mPaint);
                    mPaint.setStyle(originalStyle);
                    mPaint.setColor(mColor);
                }
                canvas.drawText(mText, mWidth / 2f, top, mPaint);
                break;
            case H_ALIGN_RIGHT:
                mPaint.setTextAlign(Paint.Align.RIGHT);
                if (mShadow && mShadowOffsetX + mShadowRadius > 0) {
                    left = mWidth - DEFAULT_PADDING - (mShadowOffsetX + mShadowRadius);
                } else {
                    left = mWidth - DEFAULT_PADDING;
                }
                if (mStroke) {
                    mPaint.setColor(mStrokeColor);
                    Style originalStyle = mPaint.getStyle();
                    mPaint.setStyle(Style.STROKE);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    canvas.drawText(mText, left, top, mPaint);
                    mPaint.setStyle(originalStyle);
                    mPaint.setColor(mColor);
                }
                canvas.drawText(mText, left, top, mPaint);
                break;
            default:
                break;
        }
    }

    private int getBreakIndex(String string, int fieldWidth) {
        int end = string.length();
        int stringWidth = (int) Math.ceil(mPaint.measureText(string));
        String subString = null;

        if (stringWidth < fieldWidth) {
            return string.length();
        }

        do {
            end--;
            subString = mText.substring(0, end);
            stringWidth = (int) Math.ceil(mPaint.measureText(subString));
        } while (stringWidth > fieldWidth);

        return end;
    }

    private int getDynamicHeight() {
        String subString = null;
        int stringLength = 0;
        int start = 0;
        int end = 0;
        int row = 1;
        int index = 0;
        boolean isDone = false;

        // Word Break
        do {
            end = mText.indexOf(" ", end + 1);

            if (end != -1) { // New word
                subString = mText.substring(start, end);
                stringLength = (int) Math.ceil(mPaint.measureText(subString));
            } else { // End of string
                end = mText.length();
                subString = mText.substring(start, end);
                stringLength = (int) Math.ceil(mPaint.measureText(subString));
            }

            // Handle new-line character
            index = subString.indexOf('\n');

            if (index != -1) {
                row++;
                start += (index + 1); // Move pointer
            } else if (stringLength > mWidth) { // Need to break
                if (end == mText.length()) { // End of string
                    end = mText.lastIndexOf(" ", end - 1);

                    if (end == -1 || start >= (end + 1)) { // There is no delimiter at this String.(There is only one word at this String.) : Need to break character
                        end = start + getBreakIndex(mText.substring(start, mText.length()), mWidth);
                        end--; // There is no delimiter, so we don't need to add compensation value(1)
                    }
                } else { // There are more words
                    int tempEnd = end;

                    end = mText.lastIndexOf(" ", end - 1);

                    if (end == -1 || start >= (end + 1)) { // There is no delimiter at this String.(There is only one word at this String.) : Need to break character
                        end = tempEnd;
                        end = start + getBreakIndex(mText.substring(start, end), mWidth);
                        end--; // There is no delimiter, so we don't need to add compensation value(1)
                    }
                }

                start = end + 1; // Move pointer
                row++;
            } else if (end == mText.length()) {
                isDone = true;
            }
        } while (!isDone);

        return mStringHeight * row + mLineSpace * (row - 1);
    }

    public synchronized void setDynamicHeight(float width) {
        mWidth = (int) width;
        mHeight = getDynamicHeight();
        if (mWidth <= 0 || mHeight <= 0) {
            Log.d(TAG, "setDynamicHeight - mWidth : " + mWidth + ", mHeight : " + mHeight);
        }

        super.setSize(width, mHeight);

        reLoad();
    }

    /**
     * Gets left position of multi-line string and sets text aline of {@link Paint} internally.
     *
     * @param stringWidth the width of string.
     * @return the left position of the line.
     */
    private float getMultiLineStringLeftPosition(float stringWidth) {
        float left = 0;
        switch (mHAlign) {
            case H_ALIGN_LEFT:
                mPaint.setTextAlign(Paint.Align.LEFT);
                if (mShadow && mShadowOffsetX + mShadowRadius < 0) {
                    left = DEFAULT_PADDING + Math.abs(mShadowOffsetX + mShadowRadius);
                } else {
                    left = DEFAULT_PADDING;
                }
                break;
            case H_ALIGN_CENTER:
                mPaint.setTextAlign(Paint.Align.CENTER);
                if (mWidth < (int) Math.ceil(stringWidth) + DEFAULT_PADDING * 2) {
                    mPaint.setTextAlign(Paint.Align.LEFT);
                    if (mShadow && mShadowOffsetX + mShadowRadius < 0) {
                        left = DEFAULT_PADDING + Math.abs(mShadowOffsetX + mShadowRadius);
                    } else {
                        left = DEFAULT_PADDING;
                    }
                } else {
                    left = mWidth / 2f;
                }
                break;
            case H_ALIGN_RIGHT:
                mPaint.setTextAlign(Paint.Align.RIGHT);
                if (mShadow && mShadowOffsetX + mShadowRadius > 0) {
                    left = mWidth - DEFAULT_PADDING - (mShadowOffsetX + mShadowRadius);
                } else {
                    left = mWidth - DEFAULT_PADDING;
                }
                break;
            default:
                break;
        }
        return left;
    }

    /**
     * Gets top position of multi-line string.
     *
     * @param totalLines the total number of lines.
     * @param lineNumber the line number to get.
     * @return the top position of the line.
     */
    private float getMultiLineStringTopPosition(int totalLines, int lineNumber) {
        float top = 0;
        int totalStringHeight = mStringHeight * totalLines + DEFAULT_LINE_SPACE * (totalLines - 1);
        int topPadding = (mHeight - totalStringHeight) / 2;
        int lineSpace = (mHeight - mStringHeight * totalLines) / (totalLines + 1);

        switch (mVAlign) {
            case V_ALIGN_TOP:
                if (lineNumber == 0) {
                    top = (mStringHeight * lineNumber) - mMetrics.ascent;
                } else {
                    top = (mStringHeight * lineNumber) + (DEFAULT_LINE_SPACE * (lineNumber - 1)) - mMetrics.ascent;
                }
                break;
            case V_ALIGN_MIDDLE:
                if (lineSpace > DEFAULT_LINE_SPACE) {
                    top = topPadding + (mStringHeight * lineNumber) + (DEFAULT_LINE_SPACE * lineNumber) - mMetrics.ascent;
                } else {
                    top = lineSpace * (lineNumber + 1) + (mStringHeight * lineNumber) - mMetrics.ascent;
                }
                break;
            case V_ALIGN_BOTTOM:
                if (lineNumber == 0) {
                    top = mHeight - (mStringHeight * ((totalLines - 1) - lineNumber)) - mMetrics.descent - DEFAULT_PADDING;
                } else {
                    top = mHeight - (mStringHeight * ((totalLines - 1) - lineNumber)) - (DEFAULT_LINE_SPACE * (lineNumber - 1)) - mMetrics.descent - DEFAULT_PADDING;
                }

                break;
            default:
                break;
        }
        return top;
    }

    private int getNumOfNewLineChar(String string) {
        int numOfNewLine = 0;
        int start = 0;

        do {
            start = mText.indexOf('\n', start);
            if (start != -1) {
                numOfNewLine++;
                start++;
            }
        } while (start != -1);
        return numOfNewLine;
    }

    private void init() {
        mShadowOffsetX = getContext().getDensity() * DEFAULT_SHADOW_OFFSET_X_DIP;
        mShadowOffsetY = getContext().getDensity() * DEFAULT_SHADOW_OFFSET_Y_DIP;
        mShadowRadius = getContext().getDensity() * DEFAULT_SHADOW_RADIUS_DIP;
        mFadingEdgeWidth = getContext().getDensity() * DEFAULT_FADING_EDGE_WIDTH_DIP;
    }

    private String insertEllipsis(String string, int fieldWidth) {
        int end = string.length() - NUM_OF_ELLIPSIS_CHARACTER;
        int stringWidth = 0;
        String concatString = "";
        String resultString = null;

        if (((int) Math.ceil(mPaint.measureText(string)) < fieldWidth) || end < 0) {
            return string;
        }

        for (int i = 0; i < NUM_OF_ELLIPSIS_CHARACTER; i++) {
            concatString = concatString.concat(".");
        }

        // Text field width is smaller than ellipsis string width. Just return ellipsis string.
        if ((int) Math.ceil(mPaint.measureText(concatString)) >= fieldWidth) {
            return concatString;
        }

        do {
            resultString = string.substring(0, end).concat(concatString);
            stringWidth = (int) Math.ceil(mPaint.measureText(resultString));
            end--;
        } while (fieldWidth < stringWidth);
        return resultString;
    }

    private boolean isFadingNeeded(String string, int fieldWidth) {
        return Math.ceil(mPaint.measureText(string)) > fieldWidth;
    }

    /**
     * Word break.
     *
     * @return the list
     */
    private List<String> wordBreak(int availableRows) {
        List<String> resultStringArray = new ArrayList<>();
        String subString = null;
        int stringLength = 0;
        int start = 0;
        int end = 0;
        int row = 0;
        int index = 0;
        boolean isDone = false;
        int numOfNewLineChar = 0;

        // New-line character handling.
        numOfNewLineChar = getNumOfNewLineChar(mText);
        start = 0;
        if (availableRows == (numOfNewLineChar + 1)) {
            do {
                end = mText.indexOf('\n', start);
                if (end != -1) {
                    resultStringArray.add(mText.substring(start, end));
                    start = end + 1;
                }
            } while (end != -1);
            addLastString(resultStringArray, start);
            return resultStringArray;
        }
        start = 0;
        end = 0;

        // Word Break
        do {
            end = GLText.getIndexOfDelimiters(mText, end + 1);
            if (end != -1) { // New word
                subString = mText.substring(start, end);
                stringLength = (int) Math.ceil(mPaint.measureText(subString));
                if (mText.charAt(end) != ' ') {
                    end--;
                }
            } else { // End of string
                end = mText.length();
                subString = mText.substring(start, end);
                stringLength = (int) Math.ceil(mPaint.measureText(subString));
            }

            // Handle new-line character
            index = subString.indexOf('\n');
            if (index != -1) {
                end = start + index;
                subString = mText.substring(start, end);
                stringLength = (int) Math.ceil(mPaint.measureText(subString));
            }

            if (stringLength > mWidth) { // Need to break
                int[] position = new int[]{start, end};
                isDone = wrap(resultStringArray, availableRows, row, position);
                start = position[1] + 1; // Move pointer
                row++;
            } else if (index != -1) {
                resultStringArray.add(mText.substring(start, start + index));
                start += (index + 1); // Move pointer
                row++;
            } else if (end == mText.length()) {
                resultStringArray.add(subString);
                isDone = true;
            }
        } while (!isDone);
        return resultStringArray;
    }

    /**
     * Wrap strings to fit text area.
     *
     * @param resultStringArray the result array.
     * @param availableRows     the maximum available rows.
     * @param row               the current row.
     * @param position          the current word offset array. [0] : start of current word, [1] : end of current word.
     * @return true if wrapping is completed, false otherwise.
     */
    private boolean wrap(List<String> resultStringArray, int availableRows, int row, int[] position) {
        int start = position[0];
        int end = position[1];
        boolean isDone = false;
        if (end == mText.length()) { // End of string
            end = GLText.getLastIndexOfDelimiters(mText, end - 1);
            if (end == -1 || start >= end) { // There is no delimiter at this String.(There is only one word at this String.) : Need to break character
                if (availableRows > (row + 1)) {
                    end = start + GLText.getBreakIndex(mPaint, mText.substring(start, mText.length()), mWidth);
                    resultStringArray.add(mText.substring(start, end));
                    end--; // There is no delimiter, so we don't need to add compensation value(1)
                } else { // Last row
                    addLastString(resultStringArray, start);
                    isDone = true;
                }
            } else if (start != end) { // There are several words at this string.
                if (availableRows > (row + 1)) {
                    resultStringArray.add(mText.substring(start, end));
                    if (mText.charAt(end) != ' ') {
                        end--;
                    }
                } else {
                    addLastString(resultStringArray, start);
                    isDone = true;
                }
            }
        } else { // There are more words
            int tempEnd = end;
            end = GLText.getLastIndexOfDelimiters(mText, end - 1);
            if (end == -1 || start >= end) { // There is no delimiter at this String.(There is only one word at this String.)
                end = tempEnd;
                if (availableRows > (row + 1)) {
                    end = start + GLText.getBreakIndex(mPaint, mText.substring(start, end), mWidth);
                    resultStringArray.add(mText.substring(start, end));
                    end--; // There is no delimiter, so we don't need to add compensation value(1)
                } else { // Last row
                    addLastString(resultStringArray, start);
                    isDone = true;
                }
            } else {
                if (availableRows > (row + 1)) {
                    resultStringArray.add(mText.substring(start, end));
                    if (mText.charAt(end) != ' ') {
                        end--;
                    }
                } else {
                    addLastString(resultStringArray, start);
                    isDone = true;
                }
            }
        }
        position[1] = end;
        return isDone;
    }
}
