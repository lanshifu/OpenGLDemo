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

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

import com.lanshifu.opengldemo.R;

import java.util.ArrayList;

/**
 * This class represents the basic building block for OpenGL rendered view objects.
 * A GLView occupies a rectangular area on the screen and is responsible for drawing and event handling. {@link GLViewGroup} can contain several GLViews.
 */
public abstract class GLView {
//    private static final String TAG = "GLView";

    /**
     * This view is visible. Use with {@link #setVisibility(int)}.
     */
    public static final int VISIBLE = 0x00000000; // View.VISIBLE
    /**
     * This view is invisible. Use with {@link #setVisibility(int)}.
     */
    public static final int INVISIBLE = 0x00000004; // View.INVISIBLE
    /**
     * This view is invisible. Use with {@link #setVisibility(int)}.
     */
    public static final int GONE = 0x00000008; // View.GONE
    /**
     * This view is disabled. <code>GLView</code> whose visibility is disabled will be rendered as dimmed.
     * Use with {@link #setVisibility(int)}.
     */
    public static final int DISABLED = 0x00000020; // View.DISABLED
    /**
     * Alpha value for dimmed view.
     */
    public static final float DIM_ALPHA_VALUE = 0.45f;
    /**
     * For internal use.
     */
    public static final int CLICKABLE = 0x00004000;
    /**
     * Use with {@link #requestFocus(int)}. Move focus to the previous focusable
     * item.
     */
    public static final int FOCUS_BACKWARD = 0x00000001;
    /**
     * Use with {@link #requestFocus(int)}. Move focus to the next focusable
     * item.
     */
    public static final int FOCUS_FORWARD = 0x00000002;
    /**
     * Use with {@link #requestFocus(int)}. Move focus to the left.
     */
    public static final int FOCUS_LEFT = 0x00000011;
    /**
     * Use with {@link #requestFocus(int)}. Move focus up.
     */
    public static final int FOCUS_UP = 0x00000021;
    /**
     * Use with {@link #requestFocus(int)}. Move focus to the right.
     */
    public static final int FOCUS_RIGHT = 0x00000042;
    /**
     * Use with {@link #requestFocus(int)}. Move focus down.
     */
    public static final int FOCUS_DOWN = 0x00000082;
    /**
     * Use with {@link #requestFocus(int)}. Move Hover focus up.
     */
    public static final int HOVER_UP = 0x00000041;
    /**
     * Use with {@link #requestFocus(int)}. Move Hover focus down.
     */
    public static final int HOVER_DOWN = 0x00000052;
    /**
     * Use with {@link #requestFocus(int)}. Move Hover focus left.
     */
    public static final int HOVER_LEFT = 0x00000031;
    /**
     * Use with {@link #requestFocus(int)}. Move Hover focus right.
     */
    public static final int HOVER_RIGHT = 0x00000062;
    /**
     * The angle of this view's orientation is 0 degree. See {@link OrientationChangeListener}.
     */
    public static final int ORIENTATION_0 = 0;
    /**
     * The angle of this view's orientation is 90 degree clockwise. See {@link OrientationChangeListener}.
     */
    public static final int ORIENTATION_90 = 1;
    /**
     * The angle of this view's orientation is 180 degree. See {@link OrientationChangeListener}.
     */
    public static final int ORIENTATION_180 = 2;
    /**
     * The angle of this view's orientation is 90 degree counter-clockwise. See {@link OrientationChangeListener}.
     */
    public static final int ORIENTATION_270 = 3;
    /**
     * No horizontal alignment is applied to this view. Use with {@link #setParentHAlign(int)}.
     */
    public static final int H_ALIGN_NONE = 0;
    /**
     * This view is drawn to the left of its parent. Use with {@link #setParentHAlign(int)}.
     */
    public static final int H_ALIGN_LEFT = 1;
    /**
     * This view is drawn centered horizontally on its parent. Use with {@link #setParentHAlign(int)}.
     */
    public static final int H_ALIGN_CENTER = 2;
    /**
     * This view is drawn to the right of its parent. Use with {@link #setParentHAlign(int)}.
     */
    public static final int H_ALIGN_RIGHT = 3;
    /**
     * No vertical alignment is applied to this view. Use with {@link #setParentVAlign(int)}.
     */
    public static final int V_ALIGN_NONE = 0;
    /**
     * This view is drawn to the top of its parent. Use with {@link #setParentVAlign(int)}.
     */
    public static final int V_ALIGN_TOP = 1;
    /**
     * This view is drawn in the vertical middle of its parent. Use with {@link #setParentVAlign(int)}.
     */
    public static final int V_ALIGN_MIDDLE = 2;

    //private static final int VISIBILITY_MASK = 0x0000000C; // View.VISIBILITY_MASK
    /**
     * This view is drawn to the bottom of its parent. Use with {@link #setParentVAlign(int)}.
     */
    public static final int V_ALIGN_BOTTOM = 3;
    /**
     * This view is drawn to the starting place of its parent.
     */
    public static final int ALIGN_START = 1;
    /**
     * This view is drawn to the middle place of its parent.
     */
    public static final int ALIGN_MIDDLE = 2;
    /**
     * This view is drawn to the ending place of its parent.
     */
    public static final int ALIGN_END = 3;
    /**
     * The Constant DRAG_SENSITIVITY_ABSOLUTE.
     */
    public static final int DRAG_SENSITIVITY_ABSOLUTE = 0;
    /**
     * The Constant DRAG_SENSITIVITY_HIGH.
     */
    public static final int DRAG_SENSITIVITY_HIGH = 1;
    /**
     * The Constant DRAG_SENSITIVITY_NORMAL.
     */
    public static final int DRAG_SENSITIVITY_NORMAL = 2;
    /**
     * The Constant LONG_CLICK_SENSITIVITY_HIGH.
     */
    public static final int LONG_CLICK_SENSITIVITY_HIGH = 1;
    /**
     * The Constant LONG_CLICK_SENSITIVITY_NORMAL.
     */
    public static final int LONG_CLICK_SENSITIVITY_NORMAL = 2;
    private static final int NO_ID = -1;
    /**
     * For internal use.
     */
    private static final int NOT_FOCUSABLE = 0x00000000; // View.NOT_FOCUSABLE
    /**
     * For internal use.
     */
    private static final int FOCUSABLE = 0x00000001; // View.FOCUSABLE
    /**
     * For internal use.
     */
    private static final int FOCUSABLE_MASK = 0x00000001;
    private static final int DEFAULT_REPEAT_CLICK_INTERVAL = 100;
    private static final int DRAG_HOLD_TIME_ABSOLUTE = 0;
    private static final int DRAG_HOLD_TIME_HIGH = 300;
    private static final int DRAG_HOLD_TIME_NORMAL = 500;
    private static final int LONG_CLICK_HOLD_TIME_HIGH = 300;
    private static final int LONG_CLICK_HOLD_TIME_NORMAL = 500;

    /**
     * Animation will be cancel if is not started within timeout
     */
    private static final int ANIMATION_PENDING_TIMEOUT = 100;
    /**
     * Minimum count to guarantee drawn on screen
     */
    private static final int MINIMUM_COUNT_TO_GUARANTEE_DRAWN_ON_SCREEN = 2;
    /**
     * The m parent.
     */
    public GLView mParent;
    protected String mSubTitle;
    protected String mContentDescription;
    /**
     * The m gl context.
     */
    protected final GLContext mGLContext;
    /**
     * The m Title.
     */
    protected String mTitle;
    /**
     * The m in screen.
     */
    protected boolean mInScreen = false;
    /**
     * The m is clipped.
     */
    protected boolean mIsClipped = false;
    /**
     * The transformed coordinate.
     */
    protected float[] mTransformedScreenCoordinate = new float[2];
    /**
     * The m default orientation.
     */
    protected int mDefaultOrientation = ORIENTATION_0;
    /**
     * Rotation degree
     */
    protected int mRotateDegree = 0;
    /**
     * The m size specified.
     */
    protected boolean mSizeSpecified;
    protected boolean mSizeGiven = false;
    /**
     * The m layout updated.
     */
    protected boolean mLayoutUpdated = true;
    /**
     * The m position changed.
     */
    protected boolean mPositionChanged = true;
    /**
     * The m scale changed.
     */
    protected boolean mScaleChanged = false;
    /**
     * The m paddings.
     */
    protected Rect mPaddings = new Rect();
    /**
     * The m alpha.
     */
    protected float mAlpha = 1.0f;
    /**
     * The m Tint Color.
     */
    protected float[] mTintColor = new float[4];
    /**
     * The m VI step.
     */
    protected float mShaderStep = 1.0f;
    /**
     * The m VI parameter.
     */
    protected float mShaderParameter = 1.0f;

//    /**
//     * The m alpha changed.
//     */
//    protected boolean mAlphaChanged = true;
    /**
     * The m Shader Fading position.
     */
    protected float[] mShaderFadingPos = new float[2];
    /**
     * The m Shader Fading bias.
     */
    protected float[] mShaderFadingOffset = new float[2];
    /**
     * The m Shader position.
     */
    protected float[] mShaderSideFadingPos = new float[2];
    /**
     * The m Shader Fading orientation.
     */
    protected int mShaderFadingOrientation = 0;
    /**
     * The m original left.
     */
    protected float mOriginalLeft = 0f;
    /**
     * The m original top.
     */
    protected float mOriginalTop = 0f;
    /**
     * The m base left.
     */
    protected float mBaseLeft = 0f;
    /**
     * The m base top.
     */
    protected float mBaseTop = 0f;
    /**
     * The m manual clip.
     */
    protected boolean mManualClip = false;
    /**
     * The m dragging.
     */
    protected boolean mDragging = false;
    /**
     * The m draw first time.
     */
    protected boolean mDrawFirstTime = true;
    /**
     * The m async load.
     */
    protected boolean mAsyncLoad = false;
    protected GLView mThis;
    /**
     * The m drag listener.
     */
    protected DragListener mDragListener = null;
    /**
     * The m touch listener.
     */
    protected TouchListener mTouchListener = null;
    /**
     * The m key listener.
     */
    protected KeyListener mKeyListener = null;
    protected FocusListener mFocusListener = null;
    protected AnimationEventListener mAnimationEventListener = null;
    protected ClickListener mClickListener = null;
    protected LongClickListener mLongClickListener = null;
    /**
     * onDraw count
     */
    private long mDrawCount = 0;
    /**
     * The m internal flags. (Focusable, Clickable)
     */
    private int mViewFlags;
    /**
     * The m view id.
     */
    private int mViewId = NO_ID;
    /**
     * The m view tag.
     */
    private int mViewTag;
    /**
     * The m object tag for testBuddy.
     */
    private String mObjectTag = "NONE";
    /**
     * The m ParentViewId for testBuddy.
     */
    private int mParentViewId = NO_ID;
    /**
     * The m bound.
     */
    private RectF mBound;
    /**
     * The m loaded.
     */
    private boolean mLoaded = false;
    /**
     * The m loading.
     */
    private boolean mLoading = false;
    /**
     * The m animation pending.
     */
    private boolean mAnimationPending = false;
    /**
     * The m animation finished.
     */
    private boolean mAnimationFinished = true;
    /**
     * The m animation started.
     */
    private boolean mAnimationStarted = false;
    /**
     * The m animation started event.
     */
    private boolean mAnimationStartedEvent = false;
    /**
     * The m hide after animation.
     */
    private boolean mHideAfterAnimation = false;
    /**
     * The m update matrix after animation. for child view
     */
    private boolean mUpdateMatrixAfterAnimation = false;
    /**
     * The m visibility.
     */
    private int mVisibility = VISIBLE;
    /**
     * The m dimmed.
     */
    private boolean mDimmed = false;

    /**
     * The m focused.
     */
    private boolean mFocused = false;
    /**
     * The m hover focused.
     */
    private boolean mHoverFocused = false;
    /**
     * The m rotation matrix.
     */
    private float[] mRotationMatrix = new float[16];
    /**
     * The m translation matrix.
     */
    private float[] mTranslationMatrix = new float[16];
    /**
     * The m scale matrix.
     */
    private float[] mScaleMatrix = new float[16];

//    protected float mParentAlpha = 1.0f;

//    protected float mBlendedAlpha = 1.0f;
    /**
     * The m temp matrix.
     */
    private float[] mTempMatrix = new float[16];
    /**
     * The m combined matrix.
     */
    private float[] mCombinedMatrix = new float[16];
    /**
     * The m anim matrix.
     */
    private float[] mAnimMatrix = new float[16];
    /**
     * The m anim gl matrix.
     */
    private float[] mAnimGLMatrix = new float[16];
    /**
     * The m parent matrix.
     */
    private float[] mMatrix = new float[16];
    /**
     * The gl coordinate.
     */
    private float glCoordinate[] = new float[4];
    /**
     * The gl transformed coordinate.
     */
    private float glTransformedCoordinate[] = new float[4];
    /**
     * The m transformation.
     */
    private Transformation mTransformation = new Transformation();
    /**
     * The m scale x.
     */
    private float mScaleX = 1f;
    /**
     * The m scale y.
     */
    private float mScaleY = 1f;
    /**
     * The m translate x.
     */
    private float mTranslateX = 0f;
    /**
     * The m translate y.
     */
    private float mTranslateY = 0f;
    /**
     * The m animation.
     */
    private Animation mAnimation = null;
    /**
     * Sets the continuous draw.
     */
    private boolean mContinuousDrawMode = false;
    /**
     * The m rotation.
     */
    private int mOrientation = ORIENTATION_0;
    /**
     * The m last orientation.
     */
    private int mLastOrientation = ORIENTATION_0;
    /**
     * The m rotatable.
     */
    private boolean mRotatable = false;
    /**
     * The m center pivot.
     */
    private boolean mCenterPivot = false;
    /**
     * The m rotate animation.
     */
    private boolean mRotateAnimation = false;
    private Interpolator mRotateAnimationInterpolator = null;
    private int mRotateAnimationDuration = 300;
    /**
     * The m parent h align.
     */
    private int mParentHAlign = H_ALIGN_NONE;
    /**
     * The m parent v align.
     */
    private int mParentVAlign = V_ALIGN_NONE;
    /**
     * The m left top coordinates.
     */
    private RectF mLeftTopCoordinates[] = new RectF[4];
    /**
     * The m left.
     */
    private float mLeft;
    /**
     * The m top.
     */
    private float mTop;
    /**
     * The m background.
     */
    private GLView mBackground;
    /**
     * The background's resource ID
     */
    private int mBackgroundResId = 0;
    /**
     * The m old alpha.
     */
    private float mOldAlpha = 1.0f; // To restore original alpha after alpha animation.
    /**
     * The left top.
     */
    private float mLeftTop[] = new float[2];
    /**
     * The left bottom.
     */
    private float mLeftBottom[] = new float[2];
    /**
     * The right top.
     */
    private float mRightTop[] = new float[2];
    /**
     * The right bottom.
     */
    private float mRightBottom[] = new float[2];

    // private GLRectangle mFocusIndicator = null;
    /**
     * The m clip rect.
     */
    private Rect mClipRect;
    /**
     * The m original clip rect.
     */
    private Rect mOriginalClipRect;

    /**
     * The manual clip rect.
     */
    private Rect mManualClipRect;
    /**
     * The m clipping.
     */
    private boolean mClipping = true;
    /**
     * The m clipping forced.
     */
    private boolean mForcedClipping = false;
    /**
     * The m drag x.
     */
    private float mPreviousDragX;
    /**
     * The m drag y.
     */
    private float mPreviousDragY;
    /**
     * The m draggable.
     */
    private boolean mDraggable = true;
    /**
     * The set dragging.
     */
    private final Runnable setDragging = new Runnable() {
        @SuppressLint("ServiceCast")
        @Override
        public void run() {
            if (mGLContext == null || GLContext.getApplicationContext() == null)
                return;

            if (mDragListener != null && mDraggable) {
                mDragging = true;
                mDragListener.onDragStart(mThis, mPreviousDragX, mPreviousDragY);
            }
        }
    };
    /**
     * The m bypass touch.
     */
    private boolean mBypassTouch = false;
    /**
     * The m temp orientation.
     */
    private int mTempOrientation = 0;
    /**
     * The m internal focus.
     */
    private boolean mInternalFocus = false;
    private int mNextFocusLeftId = NO_ID;
    private int mNextFocusRightId = NO_ID;
    private int mNextFocusUpId = NO_ID;
    private int mNextFocusDownId = NO_ID;
    private int mNextFocusForwardId = NO_ID;
    private GLRectangle mFocusIndicator = null;
    private GLRectangle mHoverIndicator = null;
    private boolean mLongClickable = false;
    private boolean mRepeatClickWhenLongClicked = false;
    private int mRepeatClickInterval = DEFAULT_REPEAT_CLICK_INTERVAL;
    /**
     * The repeatClick.
     */
    private final Runnable repeatClick = new Runnable() {
        @Override
        public void run() {
            if (mGLContext == null || GLContext.getApplicationContext() == null)
                return;
            if (mLongClickable && mRepeatClickWhenLongClicked) {
                if (mClickListener != null) {
                    mClickListener.onClick(mThis);
                }
                mGLContext.getMainHandler().postDelayed(this, mRepeatClickInterval);
            }
        }

    };
    /**
     * The set longclick.
     */
    private final Runnable setLongClick = new Runnable() {
        @SuppressLint("ServiceCast")
        @Override
        public void run() {
            if (mGLContext == null || GLContext.getApplicationContext() == null)
                return;
            if (mLongClickable) {
                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick(mThis);
                }
                if (mRepeatClickWhenLongClicked) {
                    mGLContext.getMainHandler().postDelayed(repeatClick, mRepeatClickInterval);
                }
            }
        }
    };
    private boolean mIsTouchCanceled = false;
    /**
     * The m drag sensitivity.
     */
    private int mDragSensitivity = DRAG_SENSITIVITY_NORMAL;
    /**
     * The m long click sensitivity.
     */
    private int mLongClickSensitivity = LONG_CLICK_SENSITIVITY_NORMAL;
    /**
     * The m on orientation changed listener.
     */
    private OrientationChangeListener mOrientationChangeListener;
    private FirstDrawListener mFirstDrawListener;

    {
        for (int i = 0; i < 4; i++)
            mLeftTopCoordinates[i] = new RectF();
    }

    /**
     * Instantiates a new gl view.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     */
    public GLView(GLContext glContext, float left, float top) {
        mBound = new RectF();
        mBound.left = left;
        mBound.top = top;

        mOriginalLeft = left;
        mOriginalTop = top;

        mBaseLeft = left;
        mBaseTop = top;

        mGLContext = glContext;
        resetTransformMatrix();

        mSizeSpecified = false;
        mSizeGiven = false;

        setLeftTop(ORIENTATION_0, left, top);
        setLeftTop(ORIENTATION_90, left, top);
        setLeftTop(ORIENTATION_180, left, top);
        setLeftTop(ORIENTATION_270, left, top);

        mThis = this;

        mViewId = this.hashCode();
    }

    /**
     * Instantiates a new gl view.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     */
    public GLView(GLContext glContext, float left, float top, float width, float height) {
        mBound = new RectF(left, top, left + width, top + height);

        mGLContext = glContext;
        resetTransformMatrix();

        mOriginalLeft = left;
        mOriginalTop = top;

        mBaseLeft = left;
        mBaseTop = top;

        mSizeSpecified = true;
        mSizeGiven = true;

        setLeftTop(ORIENTATION_0, left, top);
        setLeftTop(ORIENTATION_90, left, top);
        setLeftTop(ORIENTATION_180, left, top);
        setLeftTop(ORIENTATION_270, left, top);

        mThis = this;
        mViewId = this.hashCode();
    }

    /**
     * The addAccessibilityChildViewNode for testBuddy.
     */
    public synchronized void addAccessibilityChildViewNode(ArrayList<GLView> listChildViewNode) {
        if (!mInScreen || getVisibility() != VISIBLE)
            return;

        if (isClickable() && !getBypassTouch() && !mIsClipped)
            listChildViewNode.add(this);
    }

    /**
     * Adds the view.
     *
     * @param view the view
     */
    public void addView(GLView view) {
    }

    public void addView(int position, GLView view) {
    }

    public final void bringToFront() {
        if (mParent != null) {
            mParent.removeView(this);
            mParent.addView(this);
        }
    }

    public final void cancelAnimation() {
        if (mAnimation == null) {
            return;
        }
        mAnimation.cancel();
        if (mTransformation != null) {
            mTransformation.clear();
        }
        mAnimation.reset();
        mAnimationPending = false;
        mAnimationFinished = true;
        mAnimationStarted = false;

        mGLContext.setDirty(true);
    }

    /**
     * Clear.
     */
    public synchronized void clear() {
        mRotatable = false;
        mRotationMatrix = null;
        mAnimation = null;

        if (mBackground != null) {
            mBackground.clear();
            mBackground = null;
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.clear();
            mFocusIndicator = null;
        }
        if (mHoverIndicator != null) {
            mHoverIndicator.clear();
            mHoverIndicator = null;
        }
        mTouchListener = null;

        if (mDragListener != null) {
            mDragListener = null;
        }

        if (mFocusListener != null) {
            mFocusListener = null;
        }

        if (mClickListener != null) {
            mClickListener = null;
        }
        if (mLeftTopCoordinates != null) {
            for (int i = 0; i < 4; i++)
                mLeftTopCoordinates[i] = null;
            mLeftTopCoordinates = null;
        }
        if (mParent != null) {
            mParent.removeView(this);
        }
    }

    /**
     * Contains.
     *
     * @param x the x
     * @param y the y
     * @return true, if successful
     */
    public boolean contains(float x, float y) {
        if (mManualClip && mClipRect == null) { // setClipRect() called but onDraw() is not called. onDraw will update mClipRect using mManualClipRect.
            return mManualClipRect.contains((int) x, (int) y);
        }

        if (mClipRect == null) {
            refreshClipRect();
        }

        if (!mInScreen) {
            return false;
        }

        if (mRotateDegree != 0) {
            float rotationPivot[] = new float[2];
            float leftTop[] = getLeftTop((mOrientation + mDefaultOrientation) % 4);

            leftTop[0] = (getLeft() + getRight()) / 2;
            leftTop[1] = (getTop() + getBottom()) / 2;
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, rotationPivot, leftTop[0], leftTop[1]); // pivot
            PointF rotatedPoint = GLUtil.rotatePoint(x, y, ((getOrientation() + mDefaultOrientation) % 4) * 90 - getRotateDegree(), leftTop[0], leftTop[1]);
            return mClipRect.contains((int) rotatedPoint.x, (int) rotatedPoint.y);
        }
        return mClipRect.contains((int) x, (int) y);
    }

    /**
     * Draw.
     *
     * @param parentMatrix   the parent matrix
     * @param parentClipRect the parent clip rect
     */
    @SuppressLint("WrongCall")
    public final synchronized void draw(float[] parentMatrix, Rect parentClipRect) {
        if (!mLoaded) {
            if (!load()) {
                mGLContext.setDirty(true);
                return;
            }
        }

        if (mVisibility == INVISIBLE) {
            return;
        }

        if (mDrawFirstTime && mRotatable) {
            setOrientation(GLContext.getLastOrientation());
        }

        if (mContinuousDrawMode)
            mGLContext.setDirty(true);

        //float alpha = getAlpha();
        //GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        //GL11.glColor4f(alpha, alpha, alpha, alpha);

        float[] currentMatrix = mCombinedMatrix;

        if (mAnimation != null) {
            if (!mAnimationFinished) {
                mGLContext.setDirty(true);
                long timeMillis = AnimationUtils.currentAnimationTimeMillis();
                if (mAnimationPending) {
                    startAnimation();
                    return;
                }

                if (mAnimationStarted) {
                    mAnimationStarted = false;
                    mAnimationStartedEvent = true;
                    mAnimation.reset();
                    mAnimation.setStartTime(timeMillis);
                }

                if (mAnimation.getTransformation(timeMillis, mTransformation)) {
                    mTransformation.getMatrix().getValues(mAnimMatrix);

                    // calculate GL coordinates
//                    mAnimMatrix[2] = GLUtil.getGLDistanceFromScreenDistanceX(mGLContext, mAnimMatrix[2]);
//                    mAnimMatrix[5] = GLUtil.getGLDistanceFromScreenDistanceY(mGLContext, mAnimMatrix[5]);

                    GLUtil.toGLMatrix(mAnimMatrix);
                    GLUtil.multiplyMM(mAnimGLMatrix, mAnimMatrix, mCombinedMatrix);

                    mAlpha = mTransformation.getAlpha(); // Do not use setAlpha at draw() : to prevent infinite-loop
                    //onAlphaUpdated();

                    //GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                    //GL11.glColor4f(alpha, alpha, alpha, alpha);
                    //GL11.glColor4f(mBlendedAlpha, mBlendedAlpha, mBlendedAlpha, mBlendedAlpha);
                    currentMatrix = mAnimGLMatrix;
                } else {
                    mAnimationFinished = true;
                    mUpdateMatrixAfterAnimation = true;

                    if (mAnimationEventListener != null) {
                        mGLContext.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mAnimationEventListener.onAnimationEnd(GLView.this, mAnimation);
                            }
                        });
                    }

                    if (mHideAfterAnimation) {
                        setVisibility(INVISIBLE);
                        return;
                    }
                }
            }

            if (mAnimationFinished) {
                if (mUpdateMatrixAfterAnimation) {
                    mUpdateMatrixAfterAnimation = false;
                    updateLayout();
                }
                mLayoutUpdated = true;
                if (mAnimation.getFillAfter()) {
                    mTransformation.getMatrix().getValues(mAnimMatrix);
                    GLUtil.toGLMatrix(mAnimMatrix);
                    GLUtil.multiplyMM(mAnimGLMatrix, mAnimMatrix, mCombinedMatrix);
                    currentMatrix = mAnimGLMatrix;
                    mAlpha = mTransformation.getAlpha();
                    mOldAlpha = mAlpha;
                } else {
                    mAlpha = mOldAlpha; // Do not use setAlpha at draw() : to prevent infinite-loop
//                    mAlphaChanged = true;
                }
            }
            if (mAnimationStartedEvent) {
                mAnimationStartedEvent = false;
                if (mAnimationEventListener != null) {
                    mGLContext.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mAnimationEventListener.onAnimationStart(GLView.this, mAnimation);
                        }
                    });
                }
            }
        }

        GLUtil.multiplyMM(mMatrix, parentMatrix, currentMatrix);

        if (mLayoutUpdated) {
            refreshClipRect();
            if (mManualClip) {
                if (!mClipRect.setIntersect(mManualClipRect, mGLContext.getScreenGeometry())) { // Check screen border
                    if (mClipping) {
                        mInScreen = false;
                        mIsClipped = true;
                        onOutOfScreen();
                        return;
                    }
                }
                if (parentClipRect.contains(mManualClipRect)) { // Check parent
                    mIsClipped = false;
                } else if (mClipRect.setIntersect(mManualClipRect, parentClipRect)) {
                    mIsClipped = true;
                } else { // Does not intersects
                    mClipRect.set(0, 0, 0, 0);
                    mIsClipped = true;
                }
            } else {
                if (!mClipRect.setIntersect(mOriginalClipRect, mGLContext.getScreenGeometry())) { // Check screen border
                    if (mClipping && getRotateDegree() == 0) {
                        mInScreen = false;
                        mIsClipped = true;
                        onOutOfScreen();
                        return;
                    }
                }
                if (parentClipRect.contains(mOriginalClipRect)) { // Check parent
                    mIsClipped = false;
                } else if (mClipRect.setIntersect(mOriginalClipRect, parentClipRect)) {
                    mIsClipped = true;
                } else { // Does not intersects
                    mClipRect.set(0, 0, 0, 0);
                    mIsClipped = true;
                }
            }
            mInScreen = true;
        }

        if (mBackground != null)
            mBackground.draw(mMatrix, mClipRect);

        onDraw();
        mDrawFirstTime = false;
        mDrawCount++;
        if (mDrawCount == MINIMUM_COUNT_TO_GUARANTEE_DRAWN_ON_SCREEN) {
            if (mFirstDrawListener != null) {
                mFirstDrawListener.onFirstDrawn();
            }
        }
        if (getContext().isTouchExplorationEnabled() && mHoverFocused && mHoverIndicator != null) {
            mHoverIndicator.draw(mMatrix, mClipRect);
        }
        if (getContext().isFocusIndicatorVisible() && mFocused && mFocusIndicator != null) {
            mFocusIndicator.draw(mMatrix, mClipRect);
        }
    }

    public void dumpViewHierarchy(int level) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < level; i++) {
            s.append("| ");
        }

        s.append(this.getClass().getSimpleName());
        s.append("(").append((int) getLeft()).append(",").append((int) getTop()).append(",").append((int) getWidth()).append(",").append((int) getHeight()).append(")");

        if (getTitle() != null) {
            s.append(" Title : ").append(getTitle());
        }
        s.append(", Visible=").append(isVisible());

        if (mClipRect != null) {
            s.append(" Clip(Manual:").append(mManualClip).append(",").append(mClipRect.left).append(",").append(mClipRect.top).append(",").append(mClipRect.width()).append(",")
                    .append(mClipRect.height()).append(")");
        }

        Log.e("DUMP", s.toString());
    }

    public GLView findNextFocusFromView(GLView focusedView, int direction) {
        if (!isFocusable() || focusedView == null) {
            return null;
        }
        GLView resultView = null;

        // To reduce recursive calls. store values to local variable.
        float centerX = (getOriginalClipRect().left + getOriginalClipRect().right) / 2f;
        float centerY = (getOriginalClipRect().top + getOriginalClipRect().bottom) / 2f;
        float focusedViewCenterX = (focusedView.getOriginalClipRect().left + focusedView.getOriginalClipRect().right) / 2f;
        float focusedViewCenterY = (focusedView.getOriginalClipRect().top + focusedView.getOriginalClipRect().bottom) / 2f;
        float horizontalOffset = Math.abs(focusedViewCenterX - centerX);
        float verticalOffset = Math.abs(focusedViewCenterY - centerY);

        switch (direction) {
            case FOCUS_LEFT:
            case HOVER_LEFT:
                if (focusedViewCenterX > centerX) {
                    if (horizontalOffset > verticalOffset) {
                        resultView = this;
                    }
                }
                break;
            case FOCUS_RIGHT:
            case HOVER_RIGHT:
                if (focusedViewCenterX < centerX) {
                    if (horizontalOffset > verticalOffset) {
                        resultView = this;
                    }
                }
                break;
            case FOCUS_UP:
            case HOVER_UP:
                if (focusedViewCenterY > centerY) {
                    if (horizontalOffset < verticalOffset) {
                        resultView = this;
                    }
                }
                break;
            case FOCUS_DOWN:
            case HOVER_DOWN:
                if (focusedViewCenterY < centerY) {
                    if (horizontalOffset < verticalOffset) {
                        resultView = this;
                    }
                }
                break;
            default:
                resultView = null;
        }
        return resultView;
    }

    /**
     * Find view by coordinate.
     *
     * @param x the x
     * @param y the y
     * @return the gl view
     */
    public GLView findViewByCoordinate(float x, float y) {
        // TODO : Confirm below change.
        // if (getVisibility() != VISIBLE)

        if (mVisibility != VISIBLE) {
            return null;
        }

        if (mBypassTouch) {
            return null;
        }

        if (contains(x, y)) {
            return this;
        }

        return null;
    }

    /**
     * Find view by id.
     *
     * @param id the id
     * @return the gl view
     */
    public GLView findViewById(int id) {
        if (mViewId == id)
            return this;
        else
            return null;
    }

    /**
     * Find view by objectTag.
     *
     * @param objectTag the glview object tag
     * @return glview
     */
    public GLView findViewByObjectTag(String objectTag) {
        if (objectTag.equals(mObjectTag))
            return this;
        else
            return null;
    }

    /**
     * Find view by tag.
     *
     * @param tag the tag
     * @return the gl view
     */
    public GLView findViewByTag(int tag) {
        if (mViewTag == tag)
            return this;
        else
            return null;
    }

    /**
     * Find view from coordinate(0, 0) nearby.
     *
     * @return the gl view
     */

    public GLView findViewFromLeftMostTop() {
        if (mVisibility != VISIBLE) {
            return null;
        }

        if (mBypassTouch) {
            return null;
        }

        return this;
    }

    /**
     * Find view from coordinate(orientation, left, top) nearby with orientation
     *
     * @param orientation the orientation
     * @param left        the left
     * @param top         the top
     * @return the found view
     */
    public GLView findViewFromLeftMostTop(int orientation, float left, float top) {
        if (mVisibility != VISIBLE) {
            return null;
        }

        if (mBypassTouch) {
            return null;
        }

        float centerX = (getOriginalClipRect().left + getOriginalClipRect().right) / 2f;
        float centerY = (getOriginalClipRect().top + getOriginalClipRect().bottom) / 2f;

        switch (orientation) {
            case GLView.ORIENTATION_0:
                if (centerX >= left && centerY >= top) {
                    return this;
                }
                break;
            case GLView.ORIENTATION_180:
                if (centerX <= left && centerY <= top) {
                    return this;
                }
                break;
            case GLView.ORIENTATION_90:
                if (centerX <= left && centerY >= top) {
                    return this;
                }
                break;
            case GLView.ORIENTATION_270:
                if (centerX >= left && centerY <= top) {
                    return this;
                }
                break;
            default:
                break;
        }

        return null;
    }

    /**
     * Find view from coordinate(orientation, left, top) nearby with orientation
     *
     * @param orientation the orientation
     * @param left        the left
     * @param top         the top
     * @return the found view
     */
    public GLView findViewFromRightMostBottom(int orientation, float left, float top) {
        if (mVisibility != VISIBLE) {
            return null;
        }

        if (mBypassTouch) {
            return null;
        }

        float centerX = (getOriginalClipRect().left + getOriginalClipRect().right) / 2f;
        float centerY = (getOriginalClipRect().top + getOriginalClipRect().bottom) / 2f;

        switch (orientation) {
            case GLView.ORIENTATION_0:
                if (centerX <= left && centerY <= top) {
                    return this;
                }
                break;
            case GLView.ORIENTATION_180:
                if (centerX >= left && centerY >= top) {
                    return this;
                }
                break;
            case GLView.ORIENTATION_90:
                if (centerX >= left && centerY <= top) {
                    return this;
                }
                break;
            case GLView.ORIENTATION_270:
                if (centerX <= left && centerY >= top) {
                    return this;
                }
                break;
            default:
                break;
        }

        return null;
    }

    /**
     * Find view from focused view nearby with direction
     *
     * @param focusedView the focused view
     * @param direction   the search direction
     * @return the found view
     */
    public GLView findViewOnSameLine(GLView focusedView, int direction) {
        if (!isFocusable() || focusedView == null) {
            return null;
        }
        GLView resultView = null;

        float centerX = (getOriginalClipRect().left + getOriginalClipRect().right) / 2f;
        float centerY = (getOriginalClipRect().top + getOriginalClipRect().bottom) / 2f;
        float focusedViewCenterX = (focusedView.getOriginalClipRect().left + focusedView.getOriginalClipRect().right) / 2f;
        float focusedViewCenterY = (focusedView.getOriginalClipRect().top + focusedView.getOriginalClipRect().bottom) / 2f;

        switch (direction) {
            case FOCUS_LEFT:
            case HOVER_LEFT:
                if (centerX < focusedViewCenterX && GLUtil.floatEquals(centerY, focusedViewCenterY)) {
                    resultView = this;
                }
                break;
            case FOCUS_RIGHT:
            case HOVER_RIGHT:
                if (centerX > focusedViewCenterX && GLUtil.floatEquals(centerY, focusedViewCenterY)) {
                    resultView = this;
                }
                break;
            case FOCUS_UP:
            case HOVER_UP:
                if (centerY < focusedViewCenterY && GLUtil.floatEquals(centerX, focusedViewCenterX)) {
                    resultView = this;
                }
                break;
            case FOCUS_DOWN:
            case HOVER_DOWN:
                if (centerY > focusedViewCenterY && GLUtil.floatEquals(centerX, focusedViewCenterX)) {
                    resultView = this;
                }
                break;
            default:
                resultView = null;
        }
        return resultView;

    }

    /**
     * Gets the alpha.
     *
     * @return the alpha
     */
    public float getAlpha() {
        if (mParent != null) {
            return mAlpha * mParent.getAlpha() * (mDimmed ? DIM_ALPHA_VALUE : 1f);
        }

        return mAlpha * (mDimmed ? DIM_ALPHA_VALUE : 1f);

//        if (mVisibility == DISABLED)
//            return DIM_ALPHA_VALUE;
//
//        if (mAlphaChanged) {
//            if (mParent != null) {
//                mParentAlpha = mParent.getAlpha();
//                mBlendedAlpha = mAlpha * mParentAlpha;
//            } else {
//                mBlendedAlpha = mAlpha;
//            }
//            mAlphaChanged = false;
//            return mBlendedAlpha;
//        }
//        if (mParent != null) {
//            mBlendedAlpha = mAlpha * mParentAlpha;
//        } else {
//            mBlendedAlpha = mAlpha;
//        }
//        return mBlendedAlpha;
    }

    /**
     * Sets the alpha.
     *
     * @param alpha the new alpha
     */
    public void setAlpha(float alpha) {
        if (!GLUtil.floatEquals(mAlpha, alpha)) {
            mAlpha = alpha;
            mOldAlpha = alpha;
            updateAlpha();
        }
    }

    public RectF getArea() {
        return new RectF(getLeft(), getTop(), getRight(), getBottom());
    }

    /**
     * Gets the bottom.
     *
     * @return the bottom
     */
    public final float getBottom() {
        if (!mSizeSpecified)
            initSize();

        if (mParent != null)
            return mBound.bottom + mParent.getTop();

        return mBound.bottom;
    }

    public boolean getBypassTouch() {
        return mBypassTouch;
    }

    public void setBypassTouch(boolean bypass) {
        mBypassTouch = bypass;
    }

    /**
     * Gets the center pivot.
     *
     * @return the center pivot
     */
    public final boolean getCenterPivot() {
        return mCenterPivot;
    }

    /**
     * Sets the center pivot.
     *
     * @param centerPivot the new center pivot
     */
    public void setCenterPivot(boolean centerPivot) {
        mCenterPivot = centerPivot;
    }

    public ClickListener getClickListener() {
        return mClickListener;
    }

    public void setClickListener(ClickListener l) {
        mClickListener = l;
    }

    /**
     * Gets the clip rect.
     *
     * @return the clip rect
     */
    public Rect getClipRect() {
        if (mClipRect == null) {
            refreshClipRect();
        }
        return mClipRect;
    }

    /**
     * Sets a rectangular area on this view to which the view will be clipped when it is drawn.
     *
     * @param clipRect The rectangular area, in the local coordinates of this view, to which future drawing operations will be clipped.
     */
    public void setClipRect(Rect clipRect) {
        if (mManualClipRect == null) {
            mManualClipRect = new Rect(clipRect);
        } else {
            mManualClipRect.set(clipRect);
        }
        mManualClip = true;
    }

    public RectF getClipRectArea() {
        return new RectF(getClipRect().left, getClipRect().top, getClipRect().right, getClipRect().bottom);
    }

    public RectF getContentArea() {
        float left = getContentAreaLeft();
        float top = getContentAreaTop();
        return new RectF(left, top, left + getContentAreaWidth(), top + getContentAreaHeight());
    }

    /**
     * Gets the content height.
     *
     * @return the content height
     */
    public float getContentAreaHeight() {
        return getHeight() - mPaddings.top - mPaddings.bottom;
    }

    public float getContentAreaLeft() {
        return getLeft() + mPaddings.left;
    }

    public float getContentAreaTop() {
        return getTop() + mPaddings.top;
    }

    /**
     * Gets the content width.
     *
     * @return the content width
     */
    public float getContentAreaWidth() {
        return getWidth() - mPaddings.left - mPaddings.right;
    }

    public String getContentDescription() {
        return mContentDescription;
    }

    public void setContentDescription(String description) {
        mContentDescription = description;
    }

    /**
     * Gets the context.
     *
     * @return the context
     */
    public final GLContext getContext() {
        return mGLContext;
    }

    /**
     * Gets current position
     *
     * @return the current area rect
     */
    public RectF getCurrentArea() {
        float offsetX = getTranslateX();
        float offsetY = getTranslateY();
        float currentLeft = getLeft() + offsetX;
        float currentTop = getTop() + offsetY;
        float currentRight = currentLeft + getWidth();
        float currentBottom = currentTop + getHeight();

        return new RectF(currentLeft, currentTop, currentRight, currentBottom);
    }

    /**
     * Gets the Current Bottom position.
     *
     * @return real bottom coordinate
     */
    public float getCurrentBottom() {
        return getBottom() + getTranslateY();
    }

    public RectF getCurrentContentArea() {
        RectF current = getCurrentArea();
        return new RectF(current.left + mPaddings.left, current.top + mPaddings.top, current.right - mPaddings.right, current.bottom - mPaddings.bottom);
    }

    /**
     * Gets the Current Left position.
     *
     * @return real left coordinate
     */
    public float getCurrentLeft() {
        return getLeft() + getTranslateX();
    }

    /**
     * Gets the Current Right position.
     *
     * @return real right coordinate
     */
    public float getCurrentRight() {
        return getRight() + getTranslateX();
    }

    /**
     * Gets the Current Top position.
     *
     * @return real top coordinate
     */
    public float getCurrentTop() {
        return getTop() + getTranslateY();
    }

    public boolean getDraggable() {
        return mDraggable;
    }

    public void setDraggable(boolean draggable) {
        mDraggable = draggable;
    }

    public FocusListener getFocusListener() {
        return mFocusListener;
    }

    public void setFocusListener(FocusListener l) {
        mFocusListener = l;
    }

    /**
     * Gets the height.
     *
     * @return the height
     */
    public final float getHeight() {
        if (!mSizeSpecified)
            initSize();

        return mBound.bottom - mBound.top;
    }

    public void setHeight(float height) {
        if (!mSizeSpecified) {
            return;
        }
        mBound.bottom = mBound.top + height;
        if (mBackground != null) {
            mBackground.setHeight(height);
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.setHeight(height - mPaddings.top - mPaddings.bottom);
        }

        if (mHoverIndicator != null) {
            mHoverIndicator.setHeight(height - mPaddings.top - mPaddings.bottom);
        }
        updateLayout();
    }

    public final int getId() {
        return mViewId;
    }

    public boolean getInternalFocus() {
        boolean result = mInternalFocus;
        if (mParent != null) {
            result = mParent.getInternalFocus() || result;
        }
        return result;
    }

    /**
     * Set the internal focus.
     * If this flag is set, Keyboard navigation only works
     * inside of this GLViewGroup. To give focus to outside, you should use
     * {@link #requestFocus()}.
     *
     * @param value If true, then {@link #requestFocus(int)} can't go outside of this GLView}
     */
    public void setInternalFocus(boolean value) {
        mInternalFocus = value;
    }

    public GLView getInternalFocusParent() {
        if (mInternalFocus) {
            return this;
        } else if (mParent != null) {
            return mParent.getInternalFocusParent();
        } else {
            return null;
        }
    }

    /**
     * Gets the layout x.
     *
     * @return the layout x
     */
    public float getLayoutX() {
        return mBound.left;
    }

    /**
     * Gets the layout y.
     *
     * @return the layout y
     */
    public float getLayoutY() {
        return mBound.top;
    }

    /**
     * Gets the left.
     *
     * @return the left
     */
    public final float getLeft() {
        updatePosition();
        return mLeft;
    }

    /**
     * Gets the left top.
     *
     * @param orientation the orientation
     * @return the left top
     */
    public final float[] getLeftTop(int orientation) {
        if (orientation > ORIENTATION_270 || orientation < ORIENTATION_0)
            throw new IllegalArgumentException();

        if (mCenterPivot) {
            float centerX = (getLeft() + getRight()) / 2;
            float centerY = (getTop() + getBottom()) / 2;

            switch (orientation) {
                case ORIENTATION_0:
                    mLeftTop[0] = getLeft();
                    mLeftTop[1] = getTop();
                    break;
                case ORIENTATION_90:
                    mLeftTop[0] = centerX + getHeight() / 2;
                    mLeftTop[1] = centerY - getWidth() / 2;
                    break;
                case ORIENTATION_180:
                    mLeftTop[0] = getLeft() + getWidth();
                    mLeftTop[1] = getTop() + getHeight();
                    break;
                case ORIENTATION_270:
                    mLeftTop[0] = centerX - getHeight() / 2;
                    mLeftTop[1] = centerY + getWidth() / 2;
                    break;
                default:
                    // ignore wrong request
                    break;
            }
        } else {
            if (mLeftTopCoordinates != null) {
                mLeftTop[0] = mLeftTopCoordinates[orientation].left;
                mLeftTop[1] = mLeftTopCoordinates[orientation].top;
            }
        }

        return mLeftTop;
    }

    /**
     * Gets the loaded.
     *
     * @return the loaded
     */
    public boolean getLoaded() {
        return mLoaded;
    }

    public LongClickListener getLongClickListener() {
        return mLongClickListener;
    }

    public void setLongClickListener(LongClickListener l) {
        mLongClickable = true;
        mLongClickListener = l;
    }

    public final float getMoveLayoutX() {
        if (mParent != null) {
            return mBound.left - mBaseLeft + mParent.getMoveLayoutX();
        }
        return mBound.left - mBaseLeft;
    }

    public final float getMoveLayoutY() {
        if (mParent != null) {
            return mBound.top - mBaseTop + mParent.getMoveLayoutY();
        }
        return mBound.top - mBaseTop;
    }

    public final int getNextFocusDownId() {
        return mNextFocusDownId;
    }

    public void setNextFocusDownId(int id) {
        mNextFocusDownId = id;
    }

    /**
     * Gets the id of the view to use when the next focus is {@link #FOCUS_FORWARD}.
     *
     * @return The next focus ID, or {@link #NO_ID}.
     */
    public int getNextFocusForwardId() {
        return mNextFocusForwardId;
    }

    /**
     * Sets the id of the view to use when the next focus is {@link #FOCUS_FORWARD}.
     *
     * @param nextFocusForwardId The next focus ID, or {@link #NO_ID}.
     */
    public void setNextFocusForwardId(int nextFocusForwardId) {
        mNextFocusForwardId = nextFocusForwardId;
    }

    public final int getNextFocusLeftId() {
        return mNextFocusLeftId;
    }

    public void setNextFocusLeftId(int id) {
        mNextFocusLeftId = id;
    }

    public final int getNextFocusRightId() {
        return mNextFocusRightId;
    }

    public void setNextFocusRightId(int id) {
        mNextFocusRightId = id;
    }

    public final int getNextFocusUpId() {
        return mNextFocusUpId;
    }

    public void setNextFocusUpId(int id) {
        mNextFocusUpId = id;
    }

    /**
     * Gets the tag.
     * (commandID or ButtonID)
     *
     * @return commandID
     */
    public final String getObjectTag() {
        return mObjectTag;
    }

    /**
     * Sets the Object tag.
     * (commandString)
     *
     * @param commandString the command string
     */
    public final void setObjectTag(String commandString) {
        mObjectTag = commandString;
    }

    /**
     * Gets the Orientation.
     *
     * @return the orientation
     */
    public final int getOrientation() {
        if (mParent != null) {
            return (mOrientation + mParent.getOrientation()) % 4;
        }

        return mOrientation;
    }

    /**
     * Rotate.
     *
     * @param orientation the new orientation
     */
    public final synchronized void setOrientation(int orientation) {
        if (!(orientation == ORIENTATION_0 || orientation == ORIENTATION_90 || orientation == ORIENTATION_180 || orientation == ORIENTATION_270)) {
            throw new IllegalArgumentException();
        }

        if (mRotationMatrix == null) {
            return;
        }

        mLastOrientation = mOrientation;
        mOrientation = orientation;

        updateRotationMatrix();

        if (mOrientationChangeListener != null) {
            mOrientationChangeListener.onOrientationChanged(mOrientation);
        }
        mGLContext.setDirty(true);
    }

    /**
     * Gets the original clip rect.
     *
     * @return the original clip rect
     */
    public Rect getOriginalClipRect() {
        if (mOriginalClipRect == null) {
            refreshClipRect();
        }
        return mOriginalClipRect;
    }

    /**
     * Gets the paddings.
     *
     * @return the paddings
     */
    public Rect getPaddings() {
        return mPaddings;
    }

    /**
     * Sets the paddings.
     *
     * @param paddings the new paddings
     */
    public void setPaddings(Rect paddings) {
        mPaddings = paddings;
        if (mFocusIndicator != null) {
            mFocusIndicator.setSize(getWidth() - mPaddings.left - mPaddings.right, getHeight() - mPaddings.top - mPaddings.bottom);
        }
        if (mHoverIndicator != null) {
            mHoverIndicator.setSize(getWidth() - mPaddings.left - mPaddings.right, getHeight() - mPaddings.top - mPaddings.bottom);
        }
    }

    /**
     * Gets the parent forced clip rect.
     *
     * @return the parent forced clip rect
     */
    public Rect getParentForcedClipRect() {
        if (mParent == null) {
            return null;
        }

        if (mParent.isClippingForced()) {
            return mParent.getClipRect();
        }

        return mParent.getParentForcedClipRect();
    }

    /**
     * Gets the parent h align.
     *
     * @return the parent h align
     */
    public final int getParentHAlign() {
        return mParentHAlign;
    }

    /**
     * Sets the parent h align.
     *
     * @param halign the new parent h align
     */
    public final void setParentHAlign(int halign) {
        mParentHAlign = halign;
    }

    /**
     * The getParentId for testBuddy.
     */

    public int getParentId() {
        return mParentViewId;
    }

    /**
     * The setParentId for testBuddy.
     */

    public void setParentId(int id) {
        mParentViewId = id;
    }

    /**
     * Gets the parent v align.
     *
     * @return the parent v align
     */
    public final int getParentVAlign() {
        return mParentVAlign;
    }

    /**
     * Sets the parent v align.
     *
     * @param valign the new parent v align
     */
    public final void setParentVAlign(int valign) {
        mParentVAlign = valign;
    }

    public int getRepeatClickInterval() {
        return mRepeatClickInterval;
    }

    public void setRepeatClickInterval(int milliSeconds) {
        mRepeatClickInterval = milliSeconds;
    }

    /**
     * Gets the right.
     *
     * @return the right
     */
    public final float getRight() {
        if (!mSizeSpecified)
            initSize();

        if (mParent != null)
            return mBound.right + mParent.getLeft();

        return mBound.right;
    }

    /**
     * Gets the rotatable.
     *
     * @return the rotatable
     */
    public final boolean getRotatable() {
        return mRotatable;
    }

    /**
     * Sets the rotatable.
     *
     * @param rotatable the new rotatable
     */
    public void setRotatable(boolean rotatable) {
        mRotatable = rotatable;
    }

    /**
     * Gets the rotate animation.
     *
     * @return the rotate animation
     */
    public final boolean getRotateAnimation() {
        return mRotateAnimation;
    }

    /**
     * Sets the rotate animation.
     *
     * @param rotateAnimation the new rotate animation
     */
    public void setRotateAnimation(boolean rotateAnimation) {
        mRotateAnimation = rotateAnimation;
    }

    public int getRotateDegree() {
        if (mParent != null) {
            return mRotateDegree + mParent.getRotateDegree();
        }
        return mRotateDegree;
    }

    /**
     * Gets the scroll hint.
     *
     * @return the scroll hint
     */
    public boolean getScrollHint() {
        return false;
    }

    public final boolean getSizeGiven() {
        return mSizeGiven;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String title) {
        mSubTitle = title;
    }

    /**
     * Gets the tag.
     *
     * @return the tag
     */
    public final int getTag() {
        return mViewTag;
    }

    /**
     * Sets the tag.
     *
     * @param viewTag the new tag
     */
    public final void setTag(int viewTag) {
        mViewTag = viewTag;
    }

    /**
     * @return new tint color
     */
    public int getTint() {
        return Color.argb((int) (mTintColor[3] * 255.0f), (int) (mTintColor[0] * 255.0f), (int) (mTintColor[1] * 255.0f), (int) (mTintColor[2] * 255.0f));
    }

    /**
     * @param color the new tint color
     */
    public void setTint(int color) {
        mTintColor[0] = Color.red(color) / 255.0f;
        mTintColor[1] = Color.green(color) / 255.0f;
        mTintColor[2] = Color.blue(color) / 255.0f;
        mTintColor[3] = Color.alpha(color) / 255.0f;
    }

    public final String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Gets the top.
     *
     * @return the top
     */
    public final float getTop() {
        updatePosition();
        return mTop;
    }

    /**
     * Gets the translate amount of x.
     *
     * @return translate amount of x
     */
    public final float getTranslateX() {
        if (mParent != null) {
            return mTranslateX + mParent.getTranslateX();
        }
        return mTranslateX;
    }

    /**
     * Gets the translate amount of y.
     *
     * @return translate amount of y
     */
    public final float getTranslateY() {
        if (mParent != null) {
            return mTranslateY + mParent.getTranslateY();
        }
        return mTranslateY;
    }

    /**
     * Provides a string for TTS(Text-To-Speech)
     *
     * @return tts string
     */
    public String getTtsString() {
        StringBuilder ttsString = new StringBuilder();

        if (mContentDescription != null) {
            ttsString.append(mContentDescription);
        } else if (mTitle != null) {
            ttsString.append(mTitle);
        }
        if (GLUtil.isTimeInfo(ttsString.toString())) {
            // Replace string.
            String temp = GLUtil.convertTimeInfoForTTS(GLContext.getApplicationContext(), ttsString.toString());
            ttsString.setLength(0);
            ttsString.append(temp);
        }
        if (mSubTitle != null) {
            ttsString.append(",");
            ttsString.append(mSubTitle);
        }
        if (isDim()) {
            ttsString.append(",");
            ttsString.append(GLContext.getApplicationContext().getString(R.string.disable));
        }
        return ttsString.toString();
    }

    /**
     * Gets the visibility.
     *
     * @return the visibility
     */
    public final int getVisibility() {
        return mVisibility;
    }

    /**
     * Sets the visibility.
     *
     * @param visibility the new visibility
     */
    public void setVisibility(int visibility) {
        if (mVisibility != visibility) {
            mVisibility = visibility;
            if (mGLContext != null) {
                mGLContext.setDirty(true);
            }
            onVisibilityChanged(visibility);
        }
    }

    /**
     * Gets the width.
     *
     * @return the width
     */
    public final float getWidth() {
        if (!mSizeSpecified)
            initSize();

        return mBound.right - mBound.left;
    }

    public void setWidth(float width) {
        if (!mSizeSpecified) {
            return;
        }
        mBound.right = mBound.left + width;
        if (mBackground != null) {
            mBackground.setWidth(width);
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.setWidth(width - mPaddings.right - mPaddings.left);
        }

        if (mHoverIndicator != null) {
            mHoverIndicator.setWidth(width - mPaddings.right - mPaddings.left);
        }
        updateLayout();
    }

    /**
     * Inits the size.
     */
    public abstract void initSize();

    public boolean isAnimationFinished() {
        return mAnimationFinished;
    }

    public final boolean isClickable() {
        return (mViewFlags & CLICKABLE) == CLICKABLE;
    }

    /**
     * Enables or disables click events for this GLView.
     * When a GLView is clickable it will change its state to "pressed" on every click.
     * Subclasses should set the GLView clickable to visually react to user's clicks.
     *
     * @param clickable true to make the view clickable, false otherwise
     */
    public void setClickable(boolean clickable) {
        mViewFlags = (mViewFlags & ~CLICKABLE) | (clickable ? CLICKABLE : 0 & CLICKABLE);
    }

    /**
     * Gets the forced clipping.
     *
     * @return true if clipping is forced. false otherwise
     */
    public boolean isClippingForced() {
        return mForcedClipping;
    }

    public boolean isDim() {
        if (mParent != null) {
            return mParent.isDim() || mDimmed;
        }
        return mDimmed;
    }

    /**
     * Sets the dim.
     *
     * @param dimmed the new dim
     */
    public void setDim(boolean dimmed) {
        if (mDimmed != dimmed) {
            mDimmed = dimmed;
            mGLContext.setDirty(true);
        }
    }

    public boolean isDragging() {
        return mDragging;
    }

    public final boolean isFocusable() {
        // TODO : isVisible() is recursive call. should check about performance.
        return ((mViewFlags & FOCUSABLE_MASK) == FOCUSABLE && (mParent != null) && isVisible());
    }

    /**
     * Set whether this GLView can receive the focus. Setting this to false will also ensure that this GLView is not focusable.
     *
     * @param focusable If true, this view can receive the focus.
     */
    public void setFocusable(boolean focusable) {
        mViewFlags = (mViewFlags & ~FOCUSABLE_MASK) | (focusable ? FOCUSABLE : NOT_FOCUSABLE & FOCUSABLE_MASK);
    }

    public final boolean isFocused() {
        return mFocused;
    }

    public boolean isHoverSwipeEvent(int direction) {
        return direction == HOVER_UP || direction == HOVER_DOWN || direction == HOVER_LEFT || direction == HOVER_RIGHT;
    }

    public boolean isInScreen() {
        return mInScreen;
    }

    public boolean isLongClickable() {
        return mLongClickable;
    }

    public void setLongClickable(boolean longClickable) {
        mLongClickable = longClickable;
    }

    /**
     * Gets the parent forced clipping.
     *
     * @return true if parent clipping is forced. false otherwise
     */
    public boolean isParentClippingForced() {
        if (mParent == null) {
            return false;
        }

        if (mParent.isClippingForced()) {
            return true;
        }

        return mParent.isParentClippingForced();
    }

    public boolean isParentRotatable() {
        if (mParent != null) {
            if (mParent.getRotatable()) {
                return true;
            } else {
                return mParent.isParentRotatable();
            }
        }
        return false;
    }

    public boolean isRepeatClickWhenLongClicked() {
        return mRepeatClickWhenLongClicked;
    }

    public void setRepeatClickWhenLongClicked(boolean enable) {
        if (enable) {
            mLongClickable = enable;
        }
        mRepeatClickWhenLongClicked = enable;
    }

    public final boolean isVisible() {
        if (mParent != null) {
            return mParent.isVisible() ? (mVisibility == VISIBLE) : false;
        }
        return (mVisibility == VISIBLE);
    }

    public boolean keyDownEvent(int keyCode, KeyEvent event) {
        if (mKeyListener != null) {
            if (mKeyListener.onKeyDown(this, event))
                return true;
        }
        return onKeyDownEvent(keyCode, event);
    }

    public boolean keyUpEvent(int keyCode, KeyEvent event) {
        if (mKeyListener != null) {
            if (mKeyListener.onKeyUp(this, event))
                return true;
        }
        return onKeyUpEvent(keyCode, event);
    }

    /**
     * Load.
     *
     * @return true, if successful
     */
    public final synchronized boolean load() {
        if (mLoading) {
            if (getLoaded()) {
                mLoaded = true;
                mLoading = false;
                return true;
            }
            return false;
        }

        if (mLoaded)
            return true;

        if (onLoad()) {
            mLoaded = true;
            mLoading = false;
            if (mBackground != null)
                mBackground.load();
        } else {
            mLoading = true;
            return false;
        }
        return mLoaded;
    }

    /**
     * Map point.
     *
     * @param transformedScreenCoordinate the transformed screen coordinate
     * @param screenX                     the screen x
     * @param screenY                     the screen y
     */
    public final void mapPoint(float[] transformedScreenCoordinate, float screenX, float screenY) {
        // Multiply coordinate to rotation matrix
//        glCoordinate[3] = 1;
//        GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, glCoordinate, screenX, screenY);
//
//        glTransformedCoordinate[3] = 1;
//        Matrix.multiplyMV(glTransformedCoordinate, 0, mMatrix, 0, glCoordinate, 0);
//
//        GLUtil.getScreenCoordinateFromGLCoordinate(mGLContext, transformedScreenCoordinate, glTransformedCoordinate[0], glTransformedCoordinate[1]);
        glCoordinate[0] = screenX;
        glCoordinate[1] = screenY;
        glCoordinate[3] = 1;
        glTransformedCoordinate[3] = 1;
        Matrix.multiplyMV(glTransformedCoordinate, 0, mMatrix, 0, glCoordinate, 0);

        transformedScreenCoordinate[0] = glTransformedCoordinate[0];
        transformedScreenCoordinate[1] = glTransformedCoordinate[1];
    }

    public final void moveBaseLayout(float x, float y) {
        float width = getWidth();
        float height = getHeight();

        mBaseLeft += x;
        mBaseTop += y;

        mBound.left += x;
        mBound.top += y;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        updateLayout();
    }

    public final void moveBaseLayoutAbsolute(float x, float y) {
        float width = getWidth();
        float height = getHeight();
        float moveX = getMoveLayoutX();
        float moveY = getMoveLayoutY();

        mBaseLeft = mOriginalLeft + x;
        mBaseTop = mOriginalTop + y;

        mBound.left = mBaseLeft + moveX;
        mBound.top = mBaseTop + moveY;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        updateLayout();
    }

    public final void moveBaseLayoutAbsolute(float x, float y, boolean update) {
        float width = getWidth();
        float height = getHeight();
        float moveX = getMoveLayoutX();
        float moveY = getMoveLayoutY();

        mBaseLeft = mOriginalLeft + x;
        mBaseTop = mOriginalTop + y;

        mBound.left = mBaseLeft + moveX;
        mBound.top = mBaseTop + moveY;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        if (update) {
            updateLayout();
        }
    }

    /**
     * Move layout. (From current layout)
     *
     * @param x the x
     * @param y the y
     */
    public void moveLayout(float x, float y) {
        mBound.left += x;
        mBound.top += y;
        mBound.right += x;
        mBound.bottom += y;

        updateLayout();
    }

    /**
     * Move layout. (From base layout)
     *
     * @param x the x
     * @param y the y
     */
    public final void moveLayoutAbsolute(float x, float y) {
        float width = getWidth();
        float height = getHeight();

        mBound.left = mBaseLeft + x;
        mBound.top = mBaseTop + y;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        updateLayout();
    }

    public final void moveLayoutAbsolute(float x, float y, boolean update) {
        float width = getWidth();
        float height = getHeight();

        mBound.left = mBaseLeft + x;
        mBound.top = mBaseTop + y;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        if (update) {
            updateLayout();
        }
    }

    public void onFocusStatusChanged(int focusStatus) {
        if (focusStatus == GLContext.FOCUSED) {
            mFocused = true;
            if (mFocusIndicator == null) {
                mFocusIndicator = new GLRectangle(getContext(), mPaddings.left, mPaddings.top, getWidth() - mPaddings.right - mPaddings.left, getHeight() - mPaddings.bottom
                        - mPaddings.top, getContext().getFocusIndicatorColor(), getContext().getFocusIndicatorThickness());
                mFocusIndicator.setBypassTouch(true);
                mFocusIndicator.setClipping(false);
                mFocusIndicator.mParent = this;
            }
        } else {
            mFocused = false;
        }
        if (mFocusListener != null) {
            mFocusListener.onFocusChanged(this, focusStatus);
        }
    }

    /**
     * Called when hover indicator color was changed.
     */
    public void onHoverIndicatorColorChanged() {
        if (mHoverIndicator != null) {
            mHoverIndicator.setColor(getContext().getHoverIndicatorColor());
        }
    }

    public void onHoverStatusChanged(int hoverStatus) {
        if (hoverStatus == GLContext.HOVER_ENTER) {
            mHoverFocused = true;
            if (mHoverIndicator == null) {
                mHoverIndicator = new GLRectangle(getContext(), mPaddings.left, mPaddings.top, getWidth() - mPaddings.right - mPaddings.left, getHeight() - mPaddings.bottom
                        - mPaddings.top, getContext().getHoverIndicatorColor(), getContext().getHoverIndicatorThickness());
                mHoverIndicator.setBypassTouch(true);
                mHoverIndicator.setClipping(false);
                mHoverIndicator.mParent = this;
            }
        } else {
            mHoverFocused = false;
        }
    }

    public boolean onKeyDownEvent(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUpEvent(int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * Removes the view.
     *
     * @param view the view
     */
    public void removeView(GLView view) {
    }

    public final boolean requestFocus() {
        return requestFocus(FOCUS_DOWN); // Default direction
    }

    public final boolean requestFocus(int direction) {
        return requestFocus(direction, null);
    }

    public boolean requestFocus(int direction, GLView previouslyFocusedView) {
        if (previouslyFocusedView == null) {
            if ((mViewFlags & FOCUSABLE_MASK) == FOCUSABLE) {
                if (isHoverSwipeEvent(direction)) {
                    getContext().onHoverChanged(this, null);
                } else {
                    getContext().onFocusChanged(this);
                }

                return true;
            }
        } else { // previouslyFocusedRect is specified.
            GLView nextViewToFocus = null;
            int revisedDirection = direction;

            // Handle specified next view first.
            switch (revisedDirection) {
                case FOCUS_LEFT:
                case HOVER_LEFT:
                    if (mNextFocusLeftId != NO_ID) {
                        nextViewToFocus = getContext().findViewById(mNextFocusLeftId);
                    }
                    break;
                case FOCUS_RIGHT:
                case HOVER_RIGHT:
                    if (mNextFocusRightId != NO_ID) {
                        nextViewToFocus = getContext().findViewById(mNextFocusRightId);
                    }
                    break;
                case FOCUS_UP:
                case HOVER_UP:
                    if (mNextFocusUpId != NO_ID) {
                        nextViewToFocus = getContext().findViewById(mNextFocusUpId);
                    }
                    break;
                case FOCUS_DOWN:
                case HOVER_DOWN:
                    if (mNextFocusDownId != NO_ID) {
                        nextViewToFocus = getContext().findViewById(mNextFocusDownId);
                    }
                    break;
            }

            if (nextViewToFocus != null && !nextViewToFocus.isVisible()) {
                nextViewToFocus = null;
            }

            // There is no specified next focus for current direction.
            // Find next focus from previouslyFocusedRect
            if (nextViewToFocus == null) {
                if (previouslyFocusedView.getId() == getId() && mInternalFocus) {
                    return false;
                }
                if (mParent.getInternalFocus()) {
                    nextViewToFocus = getContext().findNextFocusFromView((GLViewGroup) getInternalFocusParent(), previouslyFocusedView, revisedDirection);
                } else {
                    nextViewToFocus = getContext().findNextFocusFromView(null, previouslyFocusedView, revisedDirection);
                }
            }

            if (nextViewToFocus != null) {
                nextViewToFocus.requestFocus(direction, null);
//                getContext().onFocusChanged(nextViewToFocus);
                return true;
            }
        }

        return false;
    }

    /**
     * Reset mLoaded.
     */
    public final synchronized void reset() {
        mLoaded = false;
        mLoading = false;
        if (mBackground != null) {
            mBackground.reset();
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.reset();
        }
        if (mHoverIndicator != null) {
            mHoverIndicator.reset();
        }
        onReset();
    }

    public final void resetBaseLayout() {
        float width = getWidth();
        float height = getHeight();

        mBaseLeft = mOriginalLeft;
        mBaseTop = mOriginalTop;

        mBound.left = mOriginalLeft;
        mBound.top = mOriginalTop;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        updateLayout();
    }

    public void resetClipRect() {
        mManualClip = false;
        refreshClipRect();
    }

    /**
     * Reset drag.
     */
    public void resetDrag() {
        mDragging = false;
        mGLContext.getMainHandler().removeCallbacks(setDragging);
    }

    /**
     * Reset layout.
     */
    public final void resetLayout() {
        float width = getWidth();
        float height = getHeight();

        mBound.left = mBaseLeft;
        mBound.top = mBaseTop;
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        updateLayout();
    }

    /**
     * Reset all next focus id.
     */
    public void resetNextFocusId() {
        mNextFocusDownId = NO_ID;
        mNextFocusUpId = NO_ID;
        mNextFocusLeftId = NO_ID;
        mNextFocusRightId = NO_ID;
    }

    /**
     * Reset scale.
     */
    public final void resetScale() {
        mScaleChanged = false;

        Matrix.setIdentityM(mScaleMatrix, 0);

        combineMatrices();

        mScaleX = 1f;
        mScaleY = 1f;
    }

    /**
     * Reset transform matrix.
     */
    public final synchronized void resetTransformMatrix() {
        if (mRotationMatrix == null)
            return;

        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.setIdentityM(mCombinedMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.setIdentityM(mTempMatrix, 0);
        Matrix.setIdentityM(mMatrix, 0);
    }

    /**
     * Reset translate.
     */
    public synchronized final void resetTranslate() {
        if (mTranslateX == 0 && mTranslateY == 0) {
            return;
        }
        Matrix.setIdentityM(mTranslationMatrix, 0);

        combineMatrices();

        mTranslateX = 0f;
        mTranslateY = 0f;

        updateLayout();
    }

    /**
     * Rotate degree.
     *
     * @param degree the degree
     */
    public synchronized void rotateDegree(int degree) {
        mRotateDegree = degree % 360;
        updateLayout();
    }

    /**
     * Scale.
     *
     * @param x the x
     * @param y the y
     */
    public final void scale(float x, float y) {
        if (mLeftTop == null)
            return;
        mLeftTop[0] = (getLeft() + getRight()) / 2;
        mLeftTop[1] = (getTop() + getBottom()) / 2;

        float currentPivot[] = new float[2];
        GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, currentPivot, mLeftTop[0], mLeftTop[1]); // pivot

        Matrix.translateM(mScaleMatrix, 0, currentPivot[0], currentPivot[1], 0);
        Matrix.scaleM(mScaleMatrix, 0, x, y, 1.0f);
        Matrix.translateM(mScaleMatrix, 0, -currentPivot[0], -currentPivot[1], 0);

        mScaleX *= x;
        mScaleY *= y;

        combineMatrices();
    }

    /**
     * The sendAccessibilityEvent for testBuddy.
     */

    public void sendAccessibilityEvent(int eventType) {
        if (mGLContext != null && mGLContext.isEnableAccessibilityNode()) {
            AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
            event.setSource(mGLContext.getGLSurfaceView(), mViewId);
            event.setClassName(this.getClass().getSimpleName());
            event.setPackageName(GLContext.getApplicationContext().getPackageName());
            if (getTitle() != null)
                event.getText().add(getTitle());
            else
                event.getText().add(getObjectTag());
            mGLContext.getGLSurfaceView().getParent().requestSendAccessibilityEvent(mGLContext.getGLSurfaceView(), event);
        }
    }

    /**
     * Sets the animation.
     *
     * @param animation the new animation
     */
    public final void setAnimation(Animation animation) {
        setAnimation(animation, false);
    }

    /**
     * Sets the animation.
     *
     * @param animation          the animation
     * @param hideAfterAnimation the hide after animation
     */
    public final synchronized void setAnimation(Animation animation, boolean hideAfterAnimation) {
        mHideAfterAnimation = hideAfterAnimation;
        mAnimation = animation;
    }

    public void setAnimationEventListener(AnimationEventListener l) {
        mAnimationEventListener = l;
    }

    public void setAsyncLoad(boolean async) {
        mAsyncLoad = async;
    }

    /**
     * Sets the background.
     *
     * @param resId the new background
     */
//    public boolean setBackground(int resId) {
//        if (mBackgroundResId == resId) {
//            return false;
//        }
//        if (mBackground != null) {
//            mBackground.clear();
//            mBackground = null;
//        }
//        mBackgroundResId = resId;
//        if (mBackgroundResId != 0) {
//            mBackground = new GLResourceTexture(mGLContext, 0, 0, getWidth(), getHeight(), resId);
//            mBackground.mParent = this;
//        }
//        return true;
//    }

    /**
     * Sets the background alpha.
     *
     * @param alpha the new background alpha
     */
    public boolean setBackgroundAlpha(float alpha) {
        if (mBackground == null) {
            return false;
        }
        mBackground.setAlpha(alpha);
        return true;
    }

    /**
     * Sets the clipping.
     *
     * @param clipping the new clipping
     */
    public void setClipping(boolean clipping) {
        mClipping = clipping;
        if (mClipping) {
            refreshClipRect();
        }
    }

    /**
     * Sets the continuous draw.
     *
     * @param continuousDrawMode rendermode continuously
     */
    public final void setContinuousDrawMode(boolean continuousDrawMode) {
        mContinuousDrawMode = continuousDrawMode;
    }

    /**
     * Sets the default orientation.
     *
     * @param orientation the new default orientation
     */
    public final void setDefaultOrientation(int orientation) {
        mDefaultOrientation = orientation;
        updateRotationMatrix();
        mGLContext.setDirty(true);
    }

    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    /**
     * Sets the drag sensitivity.
     *
     * @param sensitivity the new drag sensitivity
     */
    public void setDragSensitivity(int sensitivity) {
        if (sensitivity != DRAG_SENSITIVITY_ABSOLUTE && sensitivity != DRAG_SENSITIVITY_HIGH && sensitivity != DRAG_SENSITIVITY_NORMAL) {
            throw new IllegalArgumentException();
        }

        mDragSensitivity = sensitivity;
    }

    public void setFirstDrawListener(FirstDrawListener listener) {
        mFirstDrawListener = listener;
    }

    /**
     * Sets the forced clipping.
     *
     * @param forced the new forced clipping value
     */
    public void setForcedClipping(boolean forced) {
        mForcedClipping = forced;
    }

    public void setKeyListener(KeyListener l) {
        mKeyListener = l;
    }

    /**
     * Sets the left top.
     *
     * @param orientation the orientation
     * @param left        the left
     * @param top         the top
     */
    public final void setLeftTop(int orientation, float left, float top, boolean update) {
        if (orientation > ORIENTATION_270 || orientation < ORIENTATION_0)
            throw new IllegalArgumentException();

        if (mLeftTopCoordinates != null) {
            mLeftTopCoordinates[orientation].left = left;
            mLeftTopCoordinates[orientation].top = top;
        }

        if (update) {
            setRotatable(true);
            updateLayout();
        }
    }

    /**
     * Sets the left top.
     *
     * @param orientation the orientation
     * @param left        the left
     * @param top         the top
     */
    public final void setLeftTop(int orientation, float left, float top) {
        if (orientation > ORIENTATION_270 || orientation < ORIENTATION_0)
            throw new IllegalArgumentException();

        if (mLeftTopCoordinates != null) {
            mLeftTopCoordinates[orientation].left = left;
            mLeftTopCoordinates[orientation].top = top;
        }
    }

    /**
     * Sets the left top.
     *
     * @param orientation the orientation
     * @param leftTops    the left tops
     */
    public final void setLeftTop(int orientation, float[] leftTops) {
        if (orientation > ORIENTATION_270 || orientation < ORIENTATION_0)
            throw new IllegalArgumentException();

        mLeftTopCoordinates[orientation].left = leftTops[0];
        mLeftTopCoordinates[orientation].top = leftTops[1];
    }

    /**
     * Sets the long click sensitivity.
     *
     * @param sensitivity the new long click sensitivity
     */
    public void setLongClickSensitivity(int sensitivity) {
        if (sensitivity != LONG_CLICK_SENSITIVITY_HIGH && sensitivity != LONG_CLICK_SENSITIVITY_NORMAL) {
            throw new IllegalArgumentException();
        }

        mLongClickSensitivity = sensitivity;
    }

    public boolean setNextFocusDownView(GLView view) {
        if (view != null) {
            mNextFocusDownId = view.getId();
            return true;
        }
        return false;
    }

    public boolean setNextFocusLeftView(GLView view) {
        if (view != null) {
            mNextFocusLeftId = view.getId();
            return true;
        }
        return false;
    }

    public boolean setNextFocusRightView(GLView view) {
        if (view != null) {
            mNextFocusRightId = view.getId();
            return true;
        }
        return false;
    }

    public boolean setNextFocusUpView(GLView view) {
        if (view != null) {
            mNextFocusUpId = view.getId();
            return true;
        }
        return false;
    }

    /**
     * Sets the nine patch background.
     *
     * @param resId the new nine patch background
     */
    public boolean setNinePatchBackground(int resId) {
        if (mBackgroundResId == resId) {
            return false;
        }
        if (mBackground != null) {
            mBackground.clear();
            mBackground = null;
        }
        mBackgroundResId = resId;
        mBackground = new GLNinePatch(mGLContext, 0, 0, getWidth(), getHeight(), resId);
        mBackground.mParent = this;
        setPaddings(mBackground.getPaddings());
        return true;
    }

    /**
     * Sets the nine patch background.
     *
     * @param resId the res id
     * @param alpha the alpha
     */
    public boolean setNinePatchBackground(int resId, float alpha) {
        if (mBackgroundResId == resId) {
            return false;
        }
        if (mBackground != null) {
            mBackground.clear();
            mBackground = null;
        }
        mBackgroundResId = resId;
        mBackground = new GLNinePatch(mGLContext, 0, 0, getWidth(), getHeight(), resId, alpha);
        mBackground.mParent = this;
        setPaddings(mBackground.getPaddings());
        return true;
    }

    /**
     * Sets the on orientation changed listener.
     *
     * @param l the new on orientation changed listener
     */
    public void setOrientationChangeListener(OrientationChangeListener l) {
        mOrientationChangeListener = l;
    }

    public void setRotateAnimationDuration(int duration) {
        mRotateAnimationDuration = duration;
    }

    public void setRotateAnimationInterpolator(Interpolator interpolator) {
        if (interpolator != null)
            mRotateAnimationInterpolator = interpolator;
    }

    /**
     * Sets the shader fading position. Refer to {@link GLProgramStorage} for more information.
     *
     * @param start the shader start position
     * @param end   the shader end position
     */
    public void setShaderFadingPosition(float start, float end) {
        mShaderFadingPos[0] = start;
        mShaderFadingPos[1] = end;
    }

    /**
     * Sets the shader fading position. Refer to {@link GLProgramStorage} for more information.
     *
     * @param sideOffset  the side shader bias
     * @param colorOffset the color shader bias
     */
    public void setShaderFadingOffset(float sideOffset, float colorOffset) {
        mShaderFadingOffset[0] = sideOffset;
        mShaderFadingOffset[1] = colorOffset;
    }

    /**
     * Sets the shader fading orientation. Refer to {@link GLProgramStorage} for more information.
     *
     * @param orientation the shader orientation
     */
    public void setShaderFadingOrientation(int orientation) {
        mShaderFadingOrientation = orientation;
    }

    /**
     * Sets the shader side fading position. Refer to {@link GLProgramStorage} for more information.
     *
     * @param start the shader start position
     * @param end   the shader end position
     */
    public void setShaderSideFadingPosition(float start, float end) {
        mShaderSideFadingPos[0] = start;
        mShaderSideFadingPos[1] = end;
    }

    /**
     * Sets the shader parameter. Refer to {@link GLProgramStorage} for more information
     *
     * @param parameter the shader parameter
     */
    public void setShaderParameter(float parameter) {
        mShaderParameter = parameter;
    }

    /**
     * Sets the shader program. Refer to {@link GLProgramStorage} and {@link GLTexture#setShaderProgram(int)} for more information
     *
     * @param type the shader program type
     */
    public void setShaderProgram(int type) {
    }

    /**
     * Sets the shader step. Refer to {@link GLProgramStorage} for more information
     *
     * @param step the shader step
     */
    public void setShaderStep(float step) {
        mShaderStep = step;
    }

    /**
     * Sets the size.
     *
     * @param width  the width
     * @param height the height
     */
    public void setSize(float width, float height) {
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        mSizeSpecified = true;
        mSizeGiven = true;

        if (mBackground != null) {
            mBackground.setSize(width, height);
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.setSize(width - mPaddings.right - mPaddings.left, height - mPaddings.bottom - mPaddings.top);
        }

        if (mHoverIndicator != null) {
            mHoverIndicator.setSize(width - mPaddings.right - mPaddings.left, height - mPaddings.bottom - mPaddings.top);
        }

        updateLayout();
    }

    /**
     * Sets the on touch listener.
     *
     * @param l the touch listener to attach to this view.
     */
    public void setTouchListener(TouchListener l) {
        mTouchListener = l;
    }

    public void setVisibility(int visibility, boolean update) {
        if (mVisibility == visibility) {
            return;
        }
        mVisibility = visibility;
        if (update && mGLContext != null) {
            mGLContext.setDirty(true);
        }
        onVisibilityChanged(visibility);
    }

    /**
     * Start animation.
     */
    public final void startAnimation() {
        if (mAnimation == null)
            return;

        if (mLoaded) {
            mAnimation.reset();
            mAnimationPending = false;
            mAnimationStarted = true;
        } else {
            mAnimationPending = true;
            mAnimationStarted = false;
        }
        mAnimationFinished = false;

        mGLContext.setDirty(true);
    }

    /**
     * Touch event.
     *
     * @param e the e
     * @return true, if successful
     */
    public boolean touchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouchCanceled = false;
                mPreviousDragX = e.getX();
                mPreviousDragY = e.getY();
                resetDrag();
                if (mDraggable) {
                    mTempOrientation = getOrientation();
                    if (mDragSensitivity == DRAG_SENSITIVITY_ABSOLUTE) {
                        setDragging.run();
                    } else {
                        mGLContext.getMainHandler().postDelayed(setDragging, getDragHoldTime());
                    }
                }
                if (mLongClickable) {
                    mGLContext.getMainHandler().postDelayed(setLongClick, getLongClickHoldTime());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDraggable) {
                    if (mDragging) {
                        if (mTempOrientation != getOrientation()) {
                            if (mDragListener != null) {
                                mDragListener.onDragEnd(this, e.getX(), e.getY());
                            }
                            e.setAction(MotionEvent.ACTION_CANCEL);
                            resetDrag();
                            return true;
                        }
                        if (mDragListener != null) {
                            mDragListener.onDrag(this, e.getX(), e.getY(), e.getX() - mPreviousDragX, e.getY() - mPreviousDragY);
                        }
                        mPreviousDragX = e.getX();
                        mPreviousDragY = e.getY();
                        if (getDragHoldTime() != DRAG_HOLD_TIME_ABSOLUTE)
                            return true;
                    } else if (!contains(e.getX(), e.getY())) {
                        resetDrag();
                    } else {
                        mPreviousDragX = e.getX();
                        mPreviousDragY = e.getY();
                    }
                } else if (!contains(e.getX(), e.getY())) {
//                e.setAction(MotionEvent.ACTION_CANCEL);  // TODO : Temporary blocked to support list scrolling
                    if (mLongClickable) {
                        mGLContext.getMainHandler().removeCallbacks(setLongClick);
                        if (mRepeatClickWhenLongClicked) {
                            mGLContext.getMainHandler().removeCallbacks(repeatClick);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mDraggable) {
                    if (mDragging) {
                        if (mDragListener != null) {
                            mDragListener.onDragEnd(this, e.getX(), e.getY());
                        }
                        e.setAction(MotionEvent.ACTION_CANCEL);
                    }
                    resetDrag();
                } else {
                    if (!contains(e.getX(), e.getY())) {
                        e.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }
                if (mLongClickable) {
                    mGLContext.getMainHandler().removeCallbacks(setLongClick);
                    if (mRepeatClickWhenLongClicked) {
                        mGLContext.getMainHandler().removeCallbacks(repeatClick);
                    }
                }
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mDraggable) {
                    if (mDragging) {
                        if (mDragListener != null) {
                            mDragListener.onDragEnd(this, e.getX(), e.getY());
                        }
                    }
                    resetDrag();
                }
                if (mLongClickable) {
                    mGLContext.getMainHandler().removeCallbacks(setLongClick);
                    if (mRepeatClickWhenLongClicked) {
                        mGLContext.getMainHandler().removeCallbacks(repeatClick);
                    }
                }
                break;
        }

        // TODO : Need to clarify ACTION_CANCEL, list scrolling concept.
        if (mTouchListener != null) {
            if (mTouchListener.onTouch(this, e)) {
                if (mDraggable && !mDragging) {
                    resetDrag();
                }
                return true;
            }
        }

        if (e.getAction() == MotionEvent.ACTION_UP && !contains(e.getX(), e.getY())) {
            e.setAction(MotionEvent.ACTION_CANCEL);
        }

        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mIsTouchCanceled) { // To prevent duplicated cancel event.
                return true;
            }
            mIsTouchCanceled = true;
        }
        return onTouchEvent(e);
    }

    /**
     * Translate.
     *
     * @param x the x
     * @param y the y
     */
    public synchronized final void translate(float x, float y) {
        if (x == 0 && y == 0) {
            return;
        }
        float alignedX = x;
        float alignedY = y;

        // Align
        if (getContext().getAlignToPixel()) {
            alignedX = ((int) ((mTranslateX + x) + 0.5f)) - (float) ((int) (mTranslateX + 0.5f));
            alignedY = ((int) ((mTranslateY + y) + 0.5f)) - (float) ((int) (mTranslateY + 0.5f));
        }

        Matrix.translateM(mTranslationMatrix, 0, GLUtil.getGLDistanceFromScreenDistanceX(mGLContext, alignedX), GLUtil.getGLDistanceFromScreenDistanceY(mGLContext, alignedY), 0);

        combineMatrices();

        updateLayout();
        mTranslateX += x;
        mTranslateY += y;
    }

    public synchronized final void translate(float x, float y, boolean update) {
        if (x == 0 && y == 0) {
            return;
        }
        float alignedX = x;
        float alignedY = y;

        // Align
        if (getContext().getAlignToPixel()) {
            alignedX = ((int) ((mTranslateX + x) + 0.5f)) - (float) ((int) (mTranslateX + 0.5f));
            alignedY = ((int) ((mTranslateY + y) + 0.5f)) - (float) ((int) (mTranslateY + 0.5f));
        }

        Matrix.translateM(mTranslationMatrix, 0, GLUtil.getGLDistanceFromScreenDistanceX(mGLContext, alignedX), GLUtil.getGLDistanceFromScreenDistanceY(mGLContext, alignedY), 0);

        combineMatrices();

        if (update) {
            updateLayout();
        }
        mTranslateX += x;
        mTranslateY += y;
    }

    public synchronized final void translateAbsolute(float x, float y) {
        if (GLUtil.floatEquals(mTranslateX, x) && GLUtil.floatEquals(mTranslateY, y)) {
            return;
        }
        float alignedX = x;
        float alignedY = y;

        // Reset
        Matrix.setIdentityM(mTranslationMatrix, 0);
        combineMatrices();

        // Align
        if (getContext().getAlignToPixel()) {
            if (alignedX >= 0)
                alignedX = ((int) (x + 0.5f));
            else
                alignedX = ((int) (x - 0.5f));

            if (alignedY >= 0)
                alignedY = ((int) (y + 0.5f));
            else
                alignedY = ((int) (y - 0.5f));
        }

        // Translate
        Matrix.translateM(mTranslationMatrix, 0, GLUtil.getGLDistanceFromScreenDistanceX(mGLContext, alignedX), GLUtil.getGLDistanceFromScreenDistanceY(mGLContext, alignedY), 0);

        combineMatrices();

        updateLayout();

        mTranslateX = x;
        mTranslateY = y;
    }

    public synchronized final void translateAbsolute(float x, float y, boolean update) {
        if (GLUtil.floatEquals(mTranslateX, x) && GLUtil.floatEquals(mTranslateY, y)) {
            return;
        }
        float alignedX = x;
        float alignedY = y;

        // Reset
        Matrix.setIdentityM(mTranslationMatrix, 0);
        combineMatrices();

        // Align
        if (getContext().getAlignToPixel()) {
            if (alignedX >= 0)
                alignedX = ((int) (x + 0.5f));
            else
                alignedX = ((int) (x - 0.5f));

            if (alignedY >= 0)
                alignedY = ((int) (y + 0.5f));
            else
                alignedY = ((int) (y - 0.5f));
        }

        // Translate
        Matrix.translateM(mTranslationMatrix, 0, GLUtil.getGLDistanceFromScreenDistanceX(mGLContext, alignedX), GLUtil.getGLDistanceFromScreenDistanceY(mGLContext, alignedY), 0);

        combineMatrices();

        if (update) {
            updateLayout();
        }
        mTranslateX = x;
        mTranslateY = y;
    }

    public void updateAlpha() {
        mGLContext.setDirty(true);
        onAlphaUpdated();
        if (mBackground != null) {
            mBackground.onAlphaUpdated();
        }
    }

    public void updateLayout() {
        mLayoutUpdated = true;
        onLayoutUpdated();
        if (mBackground != null) {
            mBackground.onLayoutUpdated();
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.onLayoutUpdated();
        }
        if (mHoverIndicator != null) {
            mHoverIndicator.onLayoutUpdated();
        }
        mGLContext.setDirty(true);

        if (mRotatable) {
            updateRotationMatrix();
        }

        if (mScaleChanged) {
            updateScaleMatrix();
        }
    }

    public final synchronized void updateRotationMatrix() {
        Matrix.setIdentityM(mRotationMatrix, 0);

        float currentPivot[] = new float[2];
        float rotationPivot[] = new float[2];
        float leftTop[] = getLeftTop((mOrientation + mDefaultOrientation) % 4);

        if (mCenterPivot) {
            leftTop[0] = (getLeft() + getRight()) / 2;
            leftTop[1] = (getTop() + getBottom()) / 2;

            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, currentPivot, leftTop[0], leftTop[1]); // pivot

            Matrix.translateM(mRotationMatrix, 0, currentPivot[0], currentPivot[1], 0);
            Matrix.rotateM(mRotationMatrix, 0, ((mOrientation + mDefaultOrientation) % 4) * 90 - getRotateDegree(), 0, 0, -1);
            Matrix.translateM(mRotationMatrix, 0, -currentPivot[0], -currentPivot[1], 0);
        } else {
            if (mParent != null) {
                leftTop[0] += mParent.getLeft();
                leftTop[1] += mParent.getTop();
            }

            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, currentPivot, getLeft(), getTop()); // pivot
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, rotationPivot, leftTop[0], leftTop[1]); // pivot

            Matrix.translateM(mRotationMatrix, 0, rotationPivot[0], rotationPivot[1], 0);
            Matrix.rotateM(mRotationMatrix, 0, ((mOrientation + mDefaultOrientation) % 4) * 90 - getRotateDegree(), 0, 0, -1);
            Matrix.translateM(mRotationMatrix, 0, -currentPivot[0], -currentPivot[1], 0);
        }

        combineMatrices();
    }

    public final void updateScaleMatrix() {
        mScaleChanged = true;

        float currentPivot[] = new float[2];

        // update scaleMatrix
        mLeftTop[0] = (getLeft() + getRight()) / 2;
        mLeftTop[1] = (getTop() + getBottom()) / 2;

        GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, currentPivot, mLeftTop[0], mLeftTop[1]); // pivot

        Matrix.translateM(mScaleMatrix, 0, currentPivot[0], currentPivot[1], 0);
        Matrix.scaleM(mScaleMatrix, 0, mScaleX, mScaleY, 1.0f);
        Matrix.translateM(mScaleMatrix, 0, -currentPivot[0], -currentPivot[1], 0);

        combineMatrices();
    }

    /**
     * Clear clip.
     */
    protected final void clearClip() {
        GLES20.glScissor(0, 0, GLContext.getScreenWidthPixels(), GLContext.getScreenHeightPixels());
    }

    /**
     * Clip.
     */
    protected final void clip() {
        if (isParentClippingForced()) {
            Rect parentForcedRect = getParentForcedClipRect();
            if (parentForcedRect != null) {
                GLES20.glScissor(parentForcedRect.left, GLContext.getScreenHeightPixels() - parentForcedRect.bottom, (parentForcedRect.right - parentForcedRect.left), (parentForcedRect.bottom - parentForcedRect.top));
            }
        } else {
            if (mClipping) {
                // screen coordinate for scissor is upside-down.
                GLES20.glScissor(mClipRect.left, GLContext.getScreenHeightPixels() - mClipRect.bottom, (mClipRect.right - mClipRect.left), (mClipRect.bottom - mClipRect.top));
            } else {
                clearClip();
            }
        }
    }

    /**
     * Gets the matrix.
     *
     * @return the matrix
     */
    protected float[] getMatrix() {
        return mMatrix;
    }

    /**
     * Gets the size specified.
     *
     * @return the size specified
     */
    protected final boolean getSizeSpecified() {
        return mSizeSpecified;
    }

    protected synchronized boolean isClipped() {
        return mIsClipped;
    }

    /**
     * Translation Assistant related.
     */
    protected Rect mClipRect() {
        return mClipRect;
    }

    /**
     * Map point reverse.
     *
     * @param transformedScreenCoordinate the transformed screen coordinate
     * @param screenX                     the screen x
     * @param screenY                     the screen y
     */
    protected final void mapPointReverse(float[] transformedScreenCoordinate, float screenX, float screenY) {
        int orientation = (getOrientation() + mDefaultOrientation) % 4;
        if (orientation == ORIENTATION_0) {
            transformedScreenCoordinate[0] = screenX;
            transformedScreenCoordinate[1] = screenY;
            return;
        }

        float coordinateTransformMatrix[] = new float[16];

        // Generate Matrix for rotation
        {
            float leftTop[] = getLeftTop(orientation);
            if (mParent != null) {
                leftTop[0] += mParent.getLeft();
                leftTop[1] += mParent.getTop();
            }

            float glLeftTopCoordinate[] = new float[4];
            glLeftTopCoordinate[3] = 1;
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, glLeftTopCoordinate, getLeft(), getTop()); // pivot

            float glRotatedLeftTopCoordinate[] = new float[4];
            glRotatedLeftTopCoordinate[3] = 1;
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, glRotatedLeftTopCoordinate, leftTop[0], leftTop[1]); // pivot

            Matrix.setIdentityM(coordinateTransformMatrix, 0);
            Matrix.translateM(coordinateTransformMatrix, 0, glLeftTopCoordinate[0], glLeftTopCoordinate[1], 0);
            Matrix.rotateM(coordinateTransformMatrix, 0, orientation * 90, 0, 0, 1);
            Matrix.translateM(coordinateTransformMatrix, 0, -glRotatedLeftTopCoordinate[0], -glRotatedLeftTopCoordinate[1], 0);
        }

        // Multiply coordinate to rotation matrix
        {
            float glCoordinate[] = new float[4];
            glCoordinate[3] = 1;
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, glCoordinate, screenX, screenY);

            float glTransformedCoordinate[] = new float[4];
            glTransformedCoordinate[3] = 1;
            Matrix.multiplyMV(glTransformedCoordinate, 0, coordinateTransformMatrix, 0, glCoordinate, 0);

            GLUtil.getScreenCoordinateFromGLCoordinate(mGLContext, transformedScreenCoordinate, glTransformedCoordinate[0], glTransformedCoordinate[1]);
        }
    }

    protected void onAlphaUpdated() {
//        mAlphaChanged = true;
    }

    /**
     * On draw.
     */
    protected abstract void onDraw();

    /**
     * On layout move.
     */
    protected void onLayoutUpdated() {
        mPositionChanged = true;
    }

    /**
     * On load.
     *
     * @return true, if successful
     */
    protected abstract boolean onLoad();

    protected void onOutOfScreen() {
        mInScreen = false;
    }

    /**
     * On reset.
     */
    protected abstract void onReset();

    /**
     * On touch event.
     *
     * @param e the event
     * @return true if handeled the event, false otherwise
     */
    protected boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    /**
     * On visibility changed.
     */
    protected void onVisibilityChanged(int visibility) {
        if (visibility != VISIBLE) {
            if (mGLContext.getMainHandler() != null && mLongClickable) {
                mGLContext.getMainHandler().removeCallbacks(setLongClick);
                if (mRepeatClickWhenLongClicked) {
                    mGLContext.getMainHandler().removeCallbacks(repeatClick);
                }
            }

            if (mFocused && mGLContext.getRootView() != null) {
                mGLContext.getRootView().requestFocus();
            }
        }
    }

    /**
     * Returns parent clip rect.
     *
     * @return parent clip rect.
     */
    protected Rect parentClipRect() {
        if (mParent != null)
            return mParent.mClipRect();
        else
            return null;
    }

    /**
     * Refresh clip rect.
     */
    protected void refreshClipRect() {
        int left, top, right, bottom;

        left = (int) (getLeft() + 0.5f) + mPaddings.left;
        top = (int) (getTop() + 0.5f) + mPaddings.top;
        right = (int) (getRight() + 0.5f) - mPaddings.right;
        bottom = (int) (getBottom() + 0.5f) - mPaddings.bottom;
        transformScreenCoordinates(left, top, right, bottom);

        left = (int) (mLeftTop[0] + 0.5f);
        top = (int) (mLeftTop[1] + 0.5f);
        right = (int) (mRightBottom[0] + 0.5f);
        bottom = (int) (mRightBottom[1] + 0.5f);

        switch ((getOrientation() + mDefaultOrientation) % 4) {
            case ORIENTATION_270:
                left = (int) (mLeftBottom[0] + 0.5f);
                top = (int) (mLeftBottom[1] + 0.5f);
                right = (int) (mRightTop[0] + 0.5f);
                bottom = (int) (mRightTop[1] + 0.5f);
                break;
            case ORIENTATION_90:
                left = (int) (mRightTop[0] + 0.5f);
                top = (int) (mRightTop[1] + 0.5f);
                right = (int) (mLeftBottom[0] + 0.5f);
                bottom = (int) (mLeftBottom[1] + 0.5f);
                break;
            case ORIENTATION_180:
                left = (int) (mRightBottom[0] + 0.5f);
                top = (int) (mRightBottom[1] + 0.5f);
                right = (int) (mLeftTop[0] + 0.5f);
                bottom = (int) (mLeftTop[1] + 0.5f);
                break;
            case ORIENTATION_0:
            default:
                break;
        }

        if (left > right || top > bottom || getRotateDegree() != 0) {
            // If you apply RotateDegree, clip rect area is smaller than the area of the view by matrix operations.
            // Use getRotateDegree() for correction. It should refactor. because it's not best solution.
            left = (int) (getLeft() + 0.5f) + mPaddings.left;
            top = (int) (getTop() + 0.5f) + mPaddings.top;
            right = (int) (getRight() + 0.5f) - mPaddings.right;
            bottom = (int) (getBottom() + 0.5f) - mPaddings.bottom;
        }

        if (mClipRect == null) {
            mClipRect = new Rect();
        }
        mClipRect.set(left, top, right, bottom);
        if (mOriginalClipRect == null) {
            mOriginalClipRect = new Rect();
        }
        mOriginalClipRect.set(left, top, right, bottom);
    }

    protected void updateSize(float width, float height) {
        mBound.right = mBound.left + width;
        mBound.bottom = mBound.top + height;

        mSizeSpecified = true;

        if (mBackground != null) {
            mBackground.updateSize(width, height);
        }
        if (mFocusIndicator != null) {
            mFocusIndicator.updateSize(width - mPaddings.right - mPaddings.left, height - mPaddings.bottom - mPaddings.top);
        }

        if (mHoverIndicator != null) {
            mHoverIndicator.updateSize(width - mPaddings.right - mPaddings.left, height - mPaddings.bottom - mPaddings.top);
        }
        updateLayout();
    }

    /**
     * On orientation changed.
     *
     * @param orientation the orientation
     */
    void onOrientationChanged(int orientation) {
        if (mRotatable) {
            mLastOrientation = mOrientation;
            if (mLastOrientation == orientation) {
                return;
            }

            int tempOrientation = orientation;
            if (tempOrientation == ORIENTATION_0 && mLastOrientation == ORIENTATION_270) {
                tempOrientation = ORIENTATION_270 + ORIENTATION_90;
            } else if (tempOrientation == ORIENTATION_270 && mLastOrientation == ORIENTATION_0) {
                mLastOrientation = ORIENTATION_270 + ORIENTATION_90;
            }

            int degree = (tempOrientation - mLastOrientation) * 90;
            setOrientation(orientation);

            if (!mDrawFirstTime && isVisible() && isAnimationFinished()) {
                if (!mRotateAnimation) {
                    if (mAnimation != null && mTransformation != null)
                        mTransformation.getMatrix().reset();
                    if (mHideAfterAnimation) {
                        setVisibility(INVISIBLE);
                    }
                    setAnimation(GLUtil.getAlphaOnAnimation(mAlpha));
                    startAnimation();
                } else {
                    RotateAnimation anim = new RotateAnimation(degree, 0, Animation.ABSOLUTE, getLeft() + getWidth() / 2, Animation.ABSOLUTE, getTop() + getHeight() / 2);
                    anim.initialize((int) getWidth(), (int) getHeight(), GLContext.getScreenWidthPixels(), GLContext.getScreenHeightPixels());
                    anim.setDuration(mRotateAnimationDuration);
                    if (mRotateAnimationInterpolator != null)
                        anim.setInterpolator(mRotateAnimationInterpolator);
                    setAnimation(anim);
                    startAnimation();
                }
            }
        }

        if (mGLContext.getMainHandler() != null) {
            mGLContext.getMainHandler().removeCallbacks(setLongClick);
        }
    }

    /**
     * Combine matrices.
     */
    private synchronized void combineMatrices() {
        try {
            GLUtil.multiplyMM(mTempMatrix, mRotationMatrix, mScaleMatrix);
            GLUtil.multiplyMM(mCombinedMatrix, mTranslationMatrix, mTempMatrix);
        } catch (IllegalArgumentException e) {
            // ignore exception
        }
    }

    /**
     * Gets hold time of drag event.
     *
     * @return The holding period.
     */
    private int getDragHoldTime() {
        int dragHoldTime;
        switch (mDragSensitivity) {
            case DRAG_SENSITIVITY_ABSOLUTE:
                dragHoldTime = DRAG_HOLD_TIME_ABSOLUTE;
                break;
            case DRAG_SENSITIVITY_HIGH:
                dragHoldTime = DRAG_HOLD_TIME_HIGH;
                break;
            case DRAG_SENSITIVITY_NORMAL:
            default:
                dragHoldTime = DRAG_HOLD_TIME_NORMAL;
                break;
        }
        return dragHoldTime;
    }

    /**
     * Gets hold time of long click event.
     *
     * @return The holding period.
     */
    private int getLongClickHoldTime() {
        int longClickHoldTime;
        switch (mLongClickSensitivity) {
            case LONG_CLICK_SENSITIVITY_HIGH:
                longClickHoldTime = LONG_CLICK_HOLD_TIME_HIGH;
                break;
            case LONG_CLICK_SENSITIVITY_NORMAL:
            default:
                longClickHoldTime = LONG_CLICK_HOLD_TIME_NORMAL;
                break;
        }
        return longClickHoldTime;
    }

    // To improve drawing performance
    private void transformScreenCoordinates(int left, int top, int right, int bottom) {
        mLeftTop[0] = mMatrix[0] * left + mMatrix[4] * top + mMatrix[12];
        mLeftTop[1] = mMatrix[1] * left + mMatrix[5] * top + mMatrix[13];
        mLeftBottom[0] = mMatrix[0] * left + mMatrix[4] * bottom + mMatrix[12];
        mLeftBottom[1] = mMatrix[1] * left + mMatrix[5] * bottom + mMatrix[13];
        mRightTop[0] = mMatrix[0] * right + mMatrix[4] * top + mMatrix[12];
        mRightTop[1] = mMatrix[1] * right + mMatrix[5] * top + mMatrix[13];
        mRightBottom[0] = mMatrix[0] * right + mMatrix[4] * bottom + mMatrix[12];
        mRightBottom[1] = mMatrix[1] * right + mMatrix[5] * bottom + mMatrix[13];
    }

    /**
     * Update position (left and top) if needed(when position is changed or not initialized).
     *
     * @return true if updated, false otherwise.
     */
    private boolean updatePosition() {
        if (mPositionChanged) {
            if (mParent != null) {
                mLeft = mBound.left + mParent.getLeft();
                mTop = mBound.top + mParent.getTop();
            } else {
                mLeft = mBound.left;
                mTop = mBound.top;
            }
            mPositionChanged = false;
            return true;
        }
        return false;
    }

    /**
     * An animation listener receives notifications from an animation. Notifications indicate animation related events, such as the end or start of the animation.
     */
    public interface AnimationEventListener {

        /**
         * On animation end.
         */
        void onAnimationEnd(GLView view, Animation animation);

        /**
         * On animation start.
         */
        void onAnimationStart(GLView view, Animation animation);
    }

    public interface ClickListener {

        /**
         * On click.
         *
         * @param view the view
         */
        boolean onClick(GLView view);
    }

    /**
     * Interface definition for a callback to be invoked when a drag event detected.
     * If you want to drag(move) the view with drag event, you need your own implementation.
     *
     * @see {@link #translate(float, float)}, {@link #moveLayout(float, float)}
     */
    public interface DragListener {

        /**
         * On drag.
         *
         * @param view the touched view.
         * @param x    the current x coordinate
         * @param y    the current y coordinate
         * @param dx   the x coordinate difference from previous event.
         * @param dy   the y coordinate difference from previous event.
         */
        void onDrag(GLView view, float x, float y, float dx, float dy);

        /**
         * On drag end.
         *
         * @param view the touched view.
         * @param x    the current x coordinate
         * @param y    the current y coordinate
         */
        void onDragEnd(GLView view, float x, float y);

        /**
         * On drag start.
         *
         * @param view the touched view.
         * @param x    the current x coordinate
         * @param y    the current y coordinate
         */
        void onDragStart(GLView view, float x, float y);
    }

    public interface FirstDrawListener {
        /**
         * Called when a view has been drawn first on the screen.
         */
        void onFirstDrawn();
    }

    public interface FocusListener {
        boolean onFocusChanged(GLView view, int focusStatus);
    }

    public interface KeyListener {

        /**
         * Called when a key down event has occurred.
         *
         * @param view  the view what is received key event.
         * @param event the event
         * @return true if consumed the event, false otherwise.
         */
        boolean onKeyDown(GLView view, KeyEvent event);

        /**
         * Called when a key up event has occurred.
         *
         * @param view  the view what is received key event.
         * @param event the event
         * @return true if consumed the event, false otherwise.
         */
        boolean onKeyUp(GLView view, KeyEvent event);
    }

    public interface LongClickListener {
        boolean onLongClick(GLView view);
    }

    /**
     * The listener interface for receiving onOrientationChanged events.
     * The class that is interested in processing a onOrientationChanged
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's {@link #setOrientationChangeListener(OrientationChangeListener)} method. When
     * the onOrientationChanged event occurs, that object's appropriate
     * method is invoked.
     */
    public interface OrientationChangeListener {

        /**
         * Called when the orientation of the device has changed.  orientation parameters are
         * {@link #ORIENTATION_0}, {@link #ORIENTATION_90}, {@link #ORIENTATION_180} and {@link #ORIENTATION_270}
         *
         * @param orientation the orientation
         */
        void onOrientationChanged(int orientation);
    }

    /**
     * The listener interface for receiving onTouch events.
     * The class that is interested in processing a onTouch
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's {@link #setTouchListener(TouchListener)} method. When
     * the onTouch event occurs, that object's appropriate
     * method is invoked.
     */
    public interface TouchListener {

        /**
         * On touch.
         *
         * @param view  the touched view
         * @param event the touch event
         * @return true, if consumed the event, false otherwise.
         */
        boolean onTouch(GLView view, MotionEvent event);
    }
}
