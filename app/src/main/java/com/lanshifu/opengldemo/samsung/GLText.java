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

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;


/**
 * The Class GLText.
 */
public class GLText extends GLView {
    // private static final String TAG = "GLText";

    /**
     * The Constant DEFAULT_TEXTSIZE.
     */
    private static final float DEFAULT_TEXTSIZE_IN_DIP = 21f;

    /**
     * The Constant DEFAULT_COLOR.
     */
    private static final int DEFAULT_COLOR = GLContext.getColor(android.R.color.white);
    private static final char CHAR_ZERO_WIDTH_SPACE = '\u200B';
    private static final char CHAR_ZERO_WIDTH_NON_JOINER = '\u200C';
    private static final char[] mDelimiters = {' ', CHAR_ZERO_WIDTH_SPACE, CHAR_ZERO_WIDTH_NON_JOINER, '-', '/'};
    /**
     * The m string.
     */
    protected GLStringTexture mString;
    /**
     * The m width.
     */
    private float mWidth = 0f;

    /**
     * The m height.
     */
    private float mHeight = 0f;

    /**
     * The m h align.
     */
    private int mHAlign = H_ALIGN_LEFT;

    /**
     * The m v align.
     */
    private int mVAlign = V_ALIGN_TOP;

    /**
     * The m string pos x.
     */
    private float mStringPosX = 0f;

    /**
     * The m string pos y.
     */
    private float mStringPosY = 0f;

    /**
     * The m text.
     */
    private String mText = null;

    /**
     * The m color.
     */
    private int mColor = DEFAULT_COLOR;

    /**
     * The m size.
     */
    private float mSize = DEFAULT_TEXTSIZE_IN_DIP;
    private boolean mShadow = true;

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param text      the text
     */
    public GLText(GLContext glContext, float left, float top, float width, float height, String text) {
        super(glContext, left, top, width, height);

        if (text != null) {
            mColor = DEFAULT_COLOR;
            mSize = DEFAULT_TEXTSIZE_IN_DIP * getContext().getDensity();
            mText = text;
            mWidth = width;
            mHeight = height;
            mString = new GLStringTexture(glContext, 0, 0, width, height, mHAlign, mVAlign, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public GLText(GLContext glContext, float left, float top, float width, float height, String text, boolean shadow) {
        super(glContext, left, top, width, height);

        mShadow = shadow;

        if (text != null) {
            mColor = DEFAULT_COLOR;
            mSize = DEFAULT_TEXTSIZE_IN_DIP * getContext().getDensity();
            mText = text;
            mWidth = width;
            mHeight = height;
            mString = new GLStringTexture(glContext, 0, 0, width, height, mHAlign, mVAlign, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public GLText(GLContext glContext, float left, float top, float width, float height, String text, float textSize) {
        super(glContext, left, top, width, height);

        if (text != null) {
            mColor = DEFAULT_COLOR;
            mSize = textSize;
            mText = text;
            mWidth = width;
            mHeight = height;
            mString = new GLStringTexture(glContext, 0, 0, width, height, mHAlign, mVAlign, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param text      the text
     * @param textSize  the text size
     * @param color     the color
     */
    public GLText(GLContext glContext, float left, float top, float width, float height, String text, float textSize, int color) {
        super(glContext, left, top, width, height);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mWidth = width;
            mHeight = height;
            mText = text;
            mString = new GLStringTexture(glContext, 0, 0, width, height, mHAlign, mVAlign, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     * @param text      the text
     * @param textSize  the text size
     * @param type      the text type
     * @param color     the color
     * @param shadow    the shadow
     */
    public GLText(GLContext glContext, float left, float top, float width, float height, String text, float textSize, Typeface type, int color, boolean shadow) {
        super(glContext, left, top, width, height);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mWidth = width;
            mHeight = height;
            mText = text;
            mShadow = shadow;
            mString = new GLStringTexture(glContext, 0, 0, width, height, mHAlign, mVAlign, text, mSize, type, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public GLText(GLContext glContext, float left, float top, float width, float height, String text, float textSize, int color, boolean shadow) {
        super(glContext, left, top, width, height);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mWidth = width;
            mHeight = height;
            mText = text;
            mShadow = shadow;
            mString = new GLStringTexture(glContext, 0, 0, width, height, mHAlign, mVAlign, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param text      the text
     */
    public GLText(GLContext glContext, float left, float top, String text) {
        super(glContext, left, top);

        if (text != null) {
            mColor = DEFAULT_COLOR;
            mSize = DEFAULT_TEXTSIZE_IN_DIP * getContext().getDensity();
            mText = text;
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }

        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public GLText(GLContext glContext, float left, float top, String text, float textSize) {
        super(glContext, left, top);

        if (text != null) {
            mColor = DEFAULT_COLOR;
            mSize = textSize;
            mText = text;
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param text      the text
     * @param textSize  the text size
     * @param color     the color
     */
    public GLText(GLContext glContext, float left, float top, String text, float textSize, int color) {
        super(glContext, left, top);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mText = text;
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public GLText(GLContext glContext, float left, float top, String text, float textSize, int color, boolean shadow) {
        super(glContext, left, top);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mText = text;
            mShadow = shadow;
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param text      the text
     */
    public GLText(GLContext glContext, String text) {
        super(glContext, 0, 0);

        if (text != null) {
            mColor = DEFAULT_COLOR;
            mSize = DEFAULT_TEXTSIZE_IN_DIP * getContext().getDensity();
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }

        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    /**
     * Instantiates a new gl text.
     *
     * @param glContext the gl context
     * @param text      the text
     * @param textSize  the text size
     * @param color     the color
     */
    public GLText(GLContext glContext, String text, float textSize, int color) {
        super(glContext, 0, 0);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mText = text;
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public GLText(GLContext glContext, String text, float textSize, int color, boolean shadow) {
        super(glContext, 0, 0);

        if (text != null) {
            mColor = color;
            mSize = textSize;
            mText = text;
            mShadow = shadow;
            mString = new GLStringTexture(glContext, 0, 0, text, mSize, mColor, mShadow);
            setTitle(text);
        }
        if (mString != null) {
            mString.mParent = this;
        }
        setFocusable(true);
    }

    public static int getIndexOfDelimiters(String string, int index) {
        int delimiterIndex = -1;
        int tempIndex = -1;

        for (char delimiter : mDelimiters) {
            tempIndex = string.indexOf(delimiter, index);
            if (tempIndex != -1 && string.charAt(tempIndex) != ' ') {
                if (string.length() == tempIndex + 1) {
                    tempIndex = -1;
                } else {
                    tempIndex++; // If the delimiter is not a space character, + 1 to include delimiter at current string.
                }
            }
            if (delimiterIndex == -1) {
                delimiterIndex = tempIndex;
            } else if (tempIndex != -1 && delimiterIndex > tempIndex) {
                delimiterIndex = tempIndex;
            }
        }
        return delimiterIndex;
    }

    public static int getLastIndexOfDelimiters(String string, int index) {
        int delimiterIndex = -1;
        int tempIndex = -1;

        for (char delimiter : mDelimiters) {
            tempIndex = string.lastIndexOf(delimiter, index);
            if (tempIndex != -1 && string.charAt(tempIndex) != ' ') {
                tempIndex++; // If the delimiter is not a space character, + 1 to include delimiter at current string.
            }
            if (delimiterIndex == -1) {
                delimiterIndex = tempIndex;
            } else if (tempIndex != -1 && delimiterIndex < tempIndex) {
                delimiterIndex = tempIndex;
            }
        }
        return delimiterIndex;
    }

    public static int measureRows(float width, String text, float textSize, Typeface typeface) {
        if (text == null) {
            return 0;
        }
        Paint paint = new Paint();
        String subString = null;
        int stringLength = 0;
        int start = 0;
        int end = 0;
        int row = 0;
        int index = 0;
        boolean isDone = false;

        if (textSize != 0) {
            paint.setTextSize(textSize);
        }
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);

        // Word Break
        do {
            end = getIndexOfDelimiters(text, end + 1);
            if (end != -1) { // New word
                subString = text.substring(start, end);
                stringLength = (int) Math.ceil(paint.measureText(subString));
                if (text.charAt(end) != ' ') {
                    end--;
                }
            } else { // End of string
                end = text.length();
                subString = text.substring(start, end);
                stringLength = (int) Math.ceil(paint.measureText(subString));
            }

            // Handle new-line character
            index = subString.indexOf('\n');
            if (index != -1) {
                end = start + index;
                subString = text.substring(start, end);
                stringLength = (int) Math.ceil(paint.measureText(subString));
            }

            if (stringLength > width) { // Need to break
                if (end == text.length()) { // End of string
                    end = getLastIndexOfDelimiters(text, end - 1);
                    if (end == -1 || start >= end) { // There is no delimiter at this String.(There is only one word at this String.) : Need to break character
                        end = start + getBreakIndex(paint, text.substring(start, text.length()), width);
                        end--; // There is no delimiter, so we don't need to add compensation value(1)
                    }
                } else { // There are more words
                    int tempEnd = end;
                    end = getLastIndexOfDelimiters(text, end - 1);
                    if (end == -1 || start >= end) { // There is no delimiter at this String.(There is only one word at this String.)
                        end = tempEnd;
                        end = start + getBreakIndex(paint, text.substring(start, end), width);
                        end--; // There is no delimiter, so we don't need to add compensation value(1)
                    } else if (text.charAt(end) != ' ') {
                        end--;
                    }
                }
                start = end + 1; // Move pointer
                row++;
            } else if (index != -1) {
                start += (index + 1); // Move pointer
                row++;
            } else if (end == text.length()) {
                row++;
                isDone = true;
            }
        } while (!isDone);
        return row;
    }

    /* package */
    static int getBreakIndex(Paint paint, String string, float fieldWidth) {
        int end = string.length();
        int stringWidth = (int) Math.ceil(paint.measureText(string));
        String subString = null;

        if (stringWidth <= fieldWidth) {
            return string.length();
        }

        do {
            end--;
            subString = string.substring(0, end);
            stringWidth = (int) Math.ceil(paint.measureText(subString));
        } while (stringWidth > fieldWidth);

        return end;
    }

    /* (non-Javadoc)
     * @see com.samsung.android.glview.GLView#clear()
     */
    @Override
    public synchronized void clear() {
        if (mString != null) {
            mString.clear();
            mString = null;
        }
        super.clear();
    }

    /* (non-Javadoc)
     * @see com.samsung.android.glview.GLView#getLoaded()
     */
    @Override
    public boolean getLoaded() {
        return mString.getLoaded();
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return mString.getText();
    }

    /**
     * Reset tint color.
     */
    public void resetTint() {
        super.setTint(0);
        if (mString != null) {
            mString.resetTint();
        }
    }

    /**
     * Sets the text.
     *
     * @param text the new text
     */
    public void setText(String text) {
        if (text == null) {
            return;
        }
        if (text.equals(mText)) {
            return;
        }
        if (mString != null) {
            mText = text;
            setTitle(text);
            if (mSizeGiven) {
                mString.setText(text);
            } else {
                mWidth = mString.getWidth();
                mHeight = mString.getHeight();
                super.setSize(mWidth, mHeight);
            }
        }
    }

    public int getTextColor() {
        return mColor;
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#initSize()
     */
    @Override
    public void initSize() {
        mWidth = getWidth();
        mHeight = getHeight();

        if (mString != null) {
            if (!mSizeSpecified) {
                if (mString.getWidth() > mWidth) {
                    mWidth = mString.getWidth() + mPaddings.left + mPaddings.right;
                }
                if (mString.getHeight() > mHeight) {
                    mHeight = mString.getHeight() + mPaddings.top + mPaddings.bottom;
                }
            }
        }
        setSize(mWidth, mHeight);
    }

    @Override
    public void onAlphaUpdated() {
        super.onAlphaUpdated();
        if (mString != null) {
            mString.onAlphaUpdated();
        }
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onLayoutMove()
     */
    @Override
    public void onLayoutUpdated() {
        super.onLayoutUpdated();
        if (mString != null) {
            mString.onLayoutUpdated();
        }
    }

    @Override
    public void onReset() {
        if (mString != null)
            mString.reset();
    }

    /**
     * Sets the align.
     *
     * @param hAlign the h align
     * @param vAlign the v align
     */
    public void setAlign(int hAlign, int vAlign) {
        mStringPosX = 0;
        mStringPosY = 0;
        float width = getWidth() - mPaddings.left - mPaddings.right;
        float height = getHeight() - mPaddings.top - mPaddings.bottom;
        float stringWidth = mString.getWidth();
        float stringHeight = mString.getHeight();

        if (width < stringWidth) {
            if (width < mString.getStringWidth()) {
                stringWidth = mString.getStringWidth();
            } else {
                stringWidth = width;
            }
        }
        if (height < stringHeight) {
            if (height < mString.getStringHeight()) {
                stringHeight = mString.getStringHeight();
            } else {
                stringHeight = height;
            }
        }

        switch (hAlign) {
            case H_ALIGN_LEFT:
                mHAlign = H_ALIGN_LEFT;
                break;
            case H_ALIGN_CENTER:
                mStringPosX = (width - stringWidth) / 2;
                mHAlign = H_ALIGN_CENTER;
                break;
            case H_ALIGN_RIGHT:
                mStringPosX = width - stringWidth;
                mHAlign = H_ALIGN_RIGHT;
                break;
            default:
                break;
        }

        switch (vAlign) {
            case V_ALIGN_TOP:
                mVAlign = V_ALIGN_TOP;
                break;
            case V_ALIGN_MIDDLE:
                mStringPosY = (height - stringHeight) / 2;
                mVAlign = V_ALIGN_MIDDLE;
                break;
            case V_ALIGN_BOTTOM:
                mStringPosY = (height - stringHeight);
                mVAlign = V_ALIGN_BOTTOM;
                break;
            default:
                break;
        }

        mString.setAlign(hAlign, vAlign);
        mString.moveLayout(mStringPosX, mStringPosY);
    }

    public void setBold(boolean bold) {
        if (mString != null) {
            mString.setBold(bold);
        }
    }

    public void setBoldColor(boolean bold, int color) {
        if (mString != null) {
            mString.setBoldColor(bold, color);
        }
    }

    public void setColor(int color) {
        if (mColor == color) {
            return;
        }
        if (mString != null) {
            mColor = color;
            mString.setColor(color);
        }
    }

    public void setFadingEdge(boolean fading) {
        if (mString != null) {
            mString.setFadingEdge(fading);
        }
    }

    public void setFadingEdgeWidth(float width) {
        if (mString != null) {
            mString.setFadingEdgeWidth(width);
        }
    }

    public void setFontSize(float size) {
        if (GLUtil.floatEquals(mSize, size)) {
            return;
        }
        if (mString != null) {
            mSize = size;
            mString.setFontSize(size);
        }
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        if (mString != null) {
            mHeight = height;
            mString.setHeight(mHeight - mPaddings.top - mPaddings.bottom);
        }
    }

    @Override
    public void setPaddings(Rect paddings) {
        super.setPaddings(paddings);
        if (mString != null) {
            mString.setSize(mWidth - mPaddings.left - mPaddings.right, mHeight - mPaddings.top - mPaddings.bottom);
            setAlign(mHAlign, mVAlign);
        }
    }

    @Override
    public void setShaderFadingPosition(float start, float end) {
        if (mString != null) {
            mString.setShaderFadingPosition(start, end);
        }
    }

    @Override
    public void setShaderFadingOffset(float sideOffset, float colorOffset) {
        if (mString != null) {
            mString.setShaderFadingOffset(sideOffset, colorOffset);
        }
    }

    @Override
    public void setShaderFadingOrientation(int orientation) {
        if (mString != null) {
            mString.setShaderFadingOrientation(orientation);
        }
    }

    @Override
    public void setShaderSideFadingPosition(float start, float end) {
        if (mString != null) {
            mString.setShaderSideFadingPosition(start, end);
        }
    }

    /**
     * @see GLView#setShaderParameter(float)
     */
    @Override
    public void setShaderParameter(float parameter) {
        if (mString != null) {
            mString.setShaderParameter(parameter);
        }
    }

    /**
     * @see GLTexture#setShaderProgram(int)
     */
    @Override
    public void setShaderProgram(int type) {
        if (mString != null) {
            mString.setShaderProgram(type);
        }
    }

    /**
     * @see GLView#setShaderStep(float)
     */
    @Override
    public void setShaderStep(float step) {
        if (mString != null) {
            mString.setShaderStep(step);
        }
    }

    public void setShadowColor(int color) {
        if (mString != null) {
            mString.setShadowColor(color);
        }
    }

    public void setShadowLayer(boolean visibility, float radius, float offsetX, float offsetY, int color) {
        if (mString != null) {
            mString.setShadowLayer(visibility, radius, offsetX, offsetY, color);
        }
    }

    public void setShadowOffset(float offsetX, float offsetY) {
        if (mString != null) {
            mString.setShadowOffset(offsetX, offsetY);
        }
    }

    public void setShadowRadius(float radius) {
        if (mString != null) {
            mString.setShadowRadius(radius);
        }
    }

    public void setShadowVisibility(boolean visibility) {
        if (mString != null) {
            mShadow = visibility;
            mString.setShadowVisibility(visibility);
        }
    }

    /* (non-Javadoc)
     * @see com.samsung.android.glview.GLView#setSize(float, float)
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (mString != null) {
            mWidth = width;
            mHeight = height;
            mString.setSize(mWidth - mPaddings.left - mPaddings.right, mHeight - mPaddings.top - mPaddings.bottom);
        }
    }

    public void setStroke(boolean visibility, float width, int color) {
        if (mString != null) {
            mString.setStroke(visibility, width, color);
        }
    }

    public void setStrokeColor(int color) {
        if (mString != null) {
            mString.setStrokeColor(color);
        }
    }

    public void setStrokeVisibility(boolean visibility) {
        if (mString != null) {
            mString.setStrokeVisibility(visibility);
        }
    }

    public void setStrokeWidth(float width) {
        if (mString != null) {
            mString.setStrokeWidth(width);
        }
    }

    /**
     * Sets the text.
     *
     * @param text     the text
     * @param textSize the text size
     * @param color    the color
     */
    public void setText(String text, float textSize, int color) {
        if (text == null) {
            return;
        }

        if (text.equals(mText) && GLUtil.floatEquals(mSize, textSize) && mColor == color) {
            return;
        }
        if (mString != null) {
            mSize = textSize;
            mColor = color;
            mText = text;
            mString.setText(mText, mSize, mColor);
            setTitle(text);
        }
    }

    public void setTextFont(Typeface type) {
        if (mString != null) {
            mString.setTypeface(type);
        }
    }

    public void setTextScaleX(float scaleX) {
        if (mString != null) {
            mString.setTextScaleX(scaleX);
        }
    }

    @Override
    public void setTint(int color) {
        super.setTint(color);
        if (mString != null) {
            mString.setTint(color);
        }
    }

    public void setUnderline(boolean underline) {
        if (mString != null) {
            mString.setUnderline(underline);
        }
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        if (mString != null) {
            mWidth = width;
            mString.setWidth(mWidth - mPaddings.left - mPaddings.right);
        }
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onDraw()
     */
    @Override
    protected void onDraw() {
        if (mString != null) {
            mString.draw(getMatrix(), getClipRect());
        }
    }

    /* (non-Javadoc)
     * @see com.samsung.android.app.camera.glview.GLView#onLoad()
     */
    @Override
    protected boolean onLoad() {
        if (mString != null) {
            return mString.load();
        }
        return true;
    }

    @Override
    protected void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (mString != null) {
            mString.onVisibilityChanged(visibility);
        }
    }
}
