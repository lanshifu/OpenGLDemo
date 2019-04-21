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

import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Class GLViewGroup.
 */
public class GLViewGroup extends GLView {
//    private static final String TAG = "GLViewGroup";

    /**
     * This view will get focus before any of its descendants.
     */
    public static final int FOCUS_BEFORE_DESCENDANTS = 0x20000;
    /**
     * This view will get focus only if none of its descendants want it.
     */
    public static final int FOCUS_AFTER_DESCENDANTS = 0x40000;
    /**
     * This view will block any of its descendants from getting focus, even
     * if they are focusable.
     */
    public static final int FOCUS_BLOCK_DESCENDANTS = 0x60000;
    private static final int FLAG_MASK_FOCUSABILITY = 0x60000;
    /**
     * The m gl views.
     */
    protected CopyOnWriteArrayList<GLView> mGLViews = new CopyOnWriteArrayList<>();
    /**
     * Internal flags.
     * <p/>
     * This field should be made private, so it is hidden from the SDK.
     */
    private int mViewGroupFlags;

    /**
     * Instantiates a new gl view group.
     *
     * @param glContext the gl context
     */
    public GLViewGroup(GLContext glContext) {
        super(glContext, 0, 0);
        initViewGroup();
    }

    /**
     * Instantiates a new gl view group.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     */
    public GLViewGroup(GLContext glContext, float left, float top) {
        super(glContext, left, top);
        initViewGroup();
    }

    /**
     * Instantiates a new gl view group.
     *
     * @param glContext the gl context
     * @param left      the left
     * @param top       the top
     * @param width     the width
     * @param height    the height
     */
    public GLViewGroup(GLContext glContext, float left, float top, float width, float height) {
        super(glContext, left, top, width, height);
        initViewGroup();
    }

    /**
     * The addAccessibilityBaseViewNode for testBuddy.
     */
    public void addAccessibilityBaseViewNode(ArrayList<GLView> listBaseViewNode) {
        if (!mInScreen || getVisibility() != VISIBLE)
            return;
        listBaseViewNode.add(this);
    }

    /**
     * The addAccessibilityChildViewNode for testBuddy.
     */
    @Override
    public void addAccessibilityChildViewNode(ArrayList<GLView> listChildViewNode) {
        Iterator<GLView> iterator = mGLViews.iterator();
        if (!mInScreen || getVisibility() != VISIBLE)
            return;
        listChildViewNode.add(this);
        while (iterator.hasNext()) {
            GLView view = iterator.next();
            view.setParentId(this.getId());
            view.addAccessibilityChildViewNode(listChildViewNode);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#addView(com.samsung.android.app.camera.glview.GLView)
     */
    @Override
    public void addView(GLView view) {
        if (view == null)
            throw new IllegalArgumentException();

        view.mParent = this;
        view.onOrientationChanged(GLContext.getLastOrientation());
        mGLViews.add(view);
        view.onLayoutUpdated();
        view.onAlphaUpdated();
        if (!mSizeGiven) {
            updateSize();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#addView(com.samsung.android.app.camera.glview.GLView)
     */
    @Override
    public void addView(int position, GLView view) {
        if (view == null)
            throw new IllegalArgumentException();

        view.mParent = this;
        try {
            mGLViews.add(position, view);
        } catch (IndexOutOfBoundsException e) {
            mGLViews.add(view);
        }
        view.onLayoutUpdated();
        view.onAlphaUpdated();
        if (!mSizeGiven) {
            updateSize();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.glview.GLView#clear()
     */
    @Override
    public synchronized void clear() {
        for (GLView view : mGLViews) {
            view.clear();
        }
        mGLViews.clear();
        super.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.glview.GLView#contains(float, float)
     */
    @Override
    public boolean contains(float x, float y) {
        float pointX = x;
        float pointY = y;
        if (!mInScreen) {
            return false;
        }
        if (mRotateDegree != 0) {
            float rotationPivot[] = new float[2];
            float leftTop[] = getLeftTop((getOrientation() + mDefaultOrientation) % 4);

            leftTop[0] = (getLeft() + getRight()) / 2;
            leftTop[1] = (getTop() + getBottom()) / 2;
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, rotationPivot, leftTop[0], leftTop[1]); // pivot
            PointF rotatedPoint = GLUtil.rotatePoint(x, y, ((getOrientation() + mDefaultOrientation) % 4) * 90 - getRotateDegree(), leftTop[0], leftTop[1]);
            pointX = rotatedPoint.x;
            pointY = rotatedPoint.y;
        }
        for (GLView view : mGLViews) {
            if (view.getVisibility() == GLView.VISIBLE && view.contains(pointX, pointY)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(GLView view) {
        if (view == null) {
            return false;
        }
        for (GLView glView : mGLViews) {
            if (glView == view) {
                return true;
            }
        }
        return false;
    }

    @Override
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

        if (getClipRect() != null) {
            s.append(" Clip(Manual:").append(mManualClip).append(",").append(getClipRect().left).append(",").append(getClipRect().top).append(",").append(getClipRect().width()).append(",")
                    .append(getClipRect().height()).append(")");
        }
        Log.e("DUMP", s.toString());

        for (GLView view : mGLViews) {
            view.dumpViewHierarchy(level + 1);
        }
    }

    @Override
    public GLView findNextFocusFromView(GLView focusedView, int direction) {
        if (focusedView == null) {
            return null;
        }
        GLView candidateView = null;
        GLView tempView;
        tempView = super.findNextFocusFromView(focusedView, direction);
        if (tempView != null) {
            candidateView = tempView;
        }

        float tempViewCenterX = 0f;
        float tempViewCenterY = 0f;
        float candidateViewCenterX = 0f;
        float candidateViewCenterY = 0f;
        float horizontalOffset = 0f;
        float verticalOffset = 0f;
        float candidateHorizontalOffset = 0f;
        float candidateVerticalOffset = 0f;
        float focusedViewCenterX = (focusedView.getOriginalClipRect().left + focusedView.getOriginalClipRect().right) / 2f;
        float focusedViewCenterY = (focusedView.getOriginalClipRect().top + focusedView.getOriginalClipRect().bottom) / 2f;

        for (GLView view : mGLViews) {
            tempView = view.findNextFocusFromView(focusedView, direction);
            if (tempView != null) {
                if (candidateView == null) {
                    candidateView = tempView;
                } else {
                    tempViewCenterX = (tempView.getOriginalClipRect().left + tempView.getOriginalClipRect().right) / 2f;
                    tempViewCenterY = (tempView.getOriginalClipRect().top + tempView.getOriginalClipRect().bottom) / 2f;
                    candidateViewCenterX = (candidateView.getOriginalClipRect().left + candidateView.getOriginalClipRect().right) / 2f;
                    candidateViewCenterY = (candidateView.getOriginalClipRect().top + candidateView.getOriginalClipRect().bottom) / 2f;
                    horizontalOffset = Math.abs(focusedViewCenterX - tempViewCenterX);
                    verticalOffset = Math.abs(focusedViewCenterY - tempViewCenterY);
                    candidateHorizontalOffset = Math.abs(focusedViewCenterX - candidateViewCenterX);
                    candidateVerticalOffset = Math.abs(focusedViewCenterY - candidateViewCenterY);

                    switch (direction) {
                        case FOCUS_LEFT:
                        case HOVER_LEFT:
                            if (tempViewCenterX >= candidateViewCenterX && tempViewCenterX <= focusedViewCenterX) {
                                if (horizontalOffset >= verticalOffset) {
                                    if (Math.abs(verticalOffset - candidateVerticalOffset) <= Math.abs(horizontalOffset - candidateHorizontalOffset)) {
                                        candidateView = tempView;
                                    } else {
                                        if (verticalOffset <= candidateVerticalOffset)
                                            candidateView = tempView;
                                    }
                                }
                            }
                            break;
                        case FOCUS_RIGHT:
                        case HOVER_RIGHT:
                            if (tempViewCenterX <= candidateViewCenterX && tempViewCenterX >= focusedViewCenterX) {
                                if (horizontalOffset >= verticalOffset) {
                                    if (Math.abs(verticalOffset - candidateVerticalOffset) <= Math.abs(horizontalOffset - candidateHorizontalOffset)) {
                                        candidateView = tempView;
                                    } else {
                                        if (verticalOffset <= candidateVerticalOffset)
                                            candidateView = tempView;
                                    }
                                }
                            }
                            break;
                        case FOCUS_UP:
                        case HOVER_UP:
                            if (tempViewCenterY >= candidateViewCenterY && tempViewCenterY <= focusedViewCenterY) {
                                if (horizontalOffset <= verticalOffset) {
                                    if (Math.abs(verticalOffset - candidateVerticalOffset) >= Math.abs(horizontalOffset - candidateHorizontalOffset)) {
                                        candidateView = tempView;
                                    } else {
                                        if (horizontalOffset <= candidateHorizontalOffset)
                                            candidateView = tempView;
                                    }
                                }
                            }
                            break;
                        case FOCUS_DOWN:
                        case HOVER_DOWN:
                            if (tempViewCenterY <= candidateViewCenterY && tempViewCenterY >= focusedViewCenterY) {
                                if (horizontalOffset <= verticalOffset) {
                                    if (Math.abs(verticalOffset - candidateVerticalOffset) >= Math.abs(horizontalOffset - candidateHorizontalOffset)) {
                                        candidateView = tempView;
                                    } else {
                                        if (horizontalOffset <= candidateHorizontalOffset)
                                            candidateView = tempView;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        return candidateView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#findViewByCoordinate(float, float)
     */
    @Override
    public GLView findViewByCoordinate(float x, float y) {
        GLView resultView = null;
        float pointX = x;
        float pointY = y;
        if (!mInScreen) {
            return null;
        }

        if (getVisibility() != VISIBLE)
            return null;

        if (getBypassTouch())
            return null;
        if (mRotateDegree != 0) {
            float rotationPivot[] = new float[2];
            float leftTop[] = getLeftTop((getOrientation() + mDefaultOrientation) % 4);
            leftTop[0] = (getLeft() + getRight()) / 2;
            leftTop[1] = (getTop() + getBottom()) / 2;
            GLUtil.getGLCoordinateFromScreenCoordinate(mGLContext, rotationPivot, leftTop[0], leftTop[1]); // pivot
            PointF rotatedPoint = GLUtil.rotatePoint(x, y, ((getOrientation() + mDefaultOrientation) % 4) * 90 - getRotateDegree(), leftTop[0], leftTop[1]);
            pointX = rotatedPoint.x;
            pointY = rotatedPoint.y;
        }
        for (GLView glview : mGLViews) {
            GLView view = glview.findViewByCoordinate(pointX, pointY);
            if (view != null) {
                resultView = view;
            }
        }

        if (resultView != null)
            return resultView;

        if (contains(x, y))
            return this;

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#findViewById(int)
     */
    @Override
    public GLView findViewById(int id) {
        if (getId() == id) {
            return this;
        }
        for (GLView glView : mGLViews) {
            GLView view = glView.findViewById(id);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#findViewByObjectTag(String)
     */
    @Override
    public GLView findViewByObjectTag(String objectTag) {
        if (objectTag.equals(getObjectTag())) {
            return this;
        }
        for (GLView glView : mGLViews) {
            GLView view = glView.findViewByObjectTag(objectTag);
            if (view != null) {
                if (view.isVisible() && view.getClipRect().width() > 0 && view.getClipRect().height() > 0) {
                    return view;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#findViewByTag(int)
     */
    @Override
    public GLView findViewByTag(int tag) {
        if (getTag() == tag) {
            return this;
        }
        for (GLView glView : mGLViews) {
            GLView view = glView.findViewByTag(tag);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#findViewFromLeftMostTop()
     */
    @Override
    public GLView findViewFromLeftMostTop() {
        if (!mInScreen) {
            return null;
        }

        if (getVisibility() != VISIBLE) {
            return null;
        }

        if (getBypassTouch()) {
            return null;
        }

        GLView resultView = null;

        for (GLView glView : mGLViews) {
            GLView view = glView.findViewFromLeftMostTop();
            if (view != null && view.isFocusable()) {
                if (resultView == null) {
                    resultView = view;
                }

                if (view.getCurrentLeft() < resultView.getCurrentLeft()) {
                    resultView = view;
                } else if (view.getCurrentLeft() == resultView.getCurrentLeft()) {
                    if (view.getCurrentTop() <= resultView.getCurrentTop()) {
                        resultView = view;
                    }
                }
            }
        }

        return resultView;
    }

    @Override
    public GLView findViewFromLeftMostTop(int orientation, float left, float top) {
        if (getVisibility() != VISIBLE) {
            return null;
        }

        if (getBypassTouch()) {
            return null;
        }

        GLView tempView = super.findViewFromLeftMostTop(orientation, left, top);
        GLView resultView = null;
        if (tempView != null && tempView.isFocusable()) {
            resultView = tempView;
        }

        for (GLView glView : mGLViews) {
            tempView = glView.findViewFromLeftMostTop(orientation, left, top);
            if (tempView != null && tempView.isFocusable()) {
                if (resultView == null) {
                    resultView = tempView;
                } else {
                    float centerX = (tempView.getOriginalClipRect().left + tempView.getOriginalClipRect().right) / 2f;
                    float centerY = (tempView.getOriginalClipRect().top + tempView.getOriginalClipRect().bottom) / 2f;

                    float resultCenterX = (resultView.getOriginalClipRect().left + resultView.getOriginalClipRect().right) / 2f;
                    float resultCenterY = (resultView.getOriginalClipRect().top + resultView.getOriginalClipRect().bottom) / 2f;

                    switch (orientation) {
                        case GLView.ORIENTATION_0:
                            if (centerY < resultCenterY) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerY, resultCenterY)) {
                                if (centerX <= resultCenterX) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        case GLView.ORIENTATION_180:
                            if (centerY > resultCenterY) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerY, resultCenterY)) {
                                if (centerX > resultCenterX) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        case GLView.ORIENTATION_90:
                            if (centerX > resultCenterX) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerX, resultCenterX)) {
                                if (centerX <= resultCenterY) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        case GLView.ORIENTATION_270:
                            if (centerX < resultCenterX) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerX, resultCenterX)) {
                                if (centerY >= resultCenterY) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

            }
        }

        return resultView;
    }

    @Override
    public GLView findViewFromRightMostBottom(int orientation, float left, float top) {
        if (getVisibility() != VISIBLE) {
            return null;
        }

        if (getBypassTouch()) {
            return null;
        }

        GLView tempView = super.findViewFromRightMostBottom(orientation, left, top);
        GLView resultView = null;
        if (tempView != null && tempView.isFocusable()) {
            resultView = tempView;
        }

        for (GLView glView : mGLViews) {
            tempView = glView.findViewFromRightMostBottom(orientation, left, top);
            if (tempView != null && tempView.isFocusable()) {
                if (resultView == null) {
                    resultView = tempView;
                } else {
                    float centerX = (tempView.getOriginalClipRect().left + tempView.getOriginalClipRect().right) / 2f;
                    float centerY = (tempView.getOriginalClipRect().top + tempView.getOriginalClipRect().bottom) / 2f;
                    float resultCenterX = (resultView.getOriginalClipRect().left + resultView.getOriginalClipRect().right) / 2f;
                    float resultCenterY = (resultView.getOriginalClipRect().top + resultView.getOriginalClipRect().bottom) / 2f;

                    switch (orientation) {
                        case GLView.ORIENTATION_0:
                            if (centerY > resultCenterY) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerY, resultCenterY)) {
                                if (centerX >= resultCenterX) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        case GLView.ORIENTATION_180:
                            if (centerY > resultCenterY) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerY, resultCenterY)) {
                                if (centerX > resultCenterX) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        case GLView.ORIENTATION_90:
                            if (centerX > resultCenterX) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerX, resultCenterX)) {
                                if (centerX <= resultCenterY) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        case GLView.ORIENTATION_270:
                            if (centerX < resultCenterX) {
                                resultView = tempView;
                            } else if (GLUtil.floatEquals(centerX, resultCenterX)) {
                                if (centerY >= resultCenterY) {
                                    resultView = tempView;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

            }
        }

        return resultView;
    }

    public GLView findViewOnSameLine(GLView focusedView, int direction) {
        if (focusedView == null) {
            return null;
        }

        GLView candidateView = null;
        GLView tempView;
        tempView = super.findViewOnSameLine(focusedView, direction);
        if (tempView != null) {
            candidateView = tempView;
        }

        float tempViewCenterX = 0f;
        float tempViewCenterY = 0f;
        float candidateViewCenterX = 0f;
        float candidateViewCenterY = 0f;
        float focusedViewCenterX = (focusedView.getOriginalClipRect().left + focusedView.getOriginalClipRect().right) / 2f;
        float focusedViewCenterY = (focusedView.getOriginalClipRect().top + focusedView.getOriginalClipRect().bottom) / 2f;

        for (GLView view : mGLViews) {
            tempView = view.findViewOnSameLine(focusedView, direction);
            if (tempView != null && tempView.isFocusable()) {
                if (candidateView == null) {
                    candidateView = tempView;
                } else {
                    tempViewCenterX = (tempView.getOriginalClipRect().left + tempView.getOriginalClipRect().right) / 2f;
                    tempViewCenterY = (tempView.getOriginalClipRect().top + tempView.getOriginalClipRect().bottom) / 2f;
                    candidateViewCenterX = (candidateView.getOriginalClipRect().left + candidateView.getOriginalClipRect().right) / 2f;
                    candidateViewCenterY = (candidateView.getOriginalClipRect().top + candidateView.getOriginalClipRect().bottom) / 2f;

                    switch (direction) {
                        case FOCUS_LEFT:
                        case HOVER_LEFT:
                            if (tempViewCenterX > candidateViewCenterX && tempViewCenterX < focusedViewCenterX) {
                                candidateView = tempView;
                            }
                            break;
                        case FOCUS_RIGHT:
                        case HOVER_RIGHT:
                            if (tempViewCenterX < candidateViewCenterX && tempViewCenterX > focusedViewCenterX) {
                                candidateView = tempView;
                            }
                            break;
                        case FOCUS_UP:
                        case HOVER_UP:
                            if (tempViewCenterY > candidateViewCenterY && tempViewCenterY < focusedViewCenterY) {
                                candidateView = tempView;
                            }
                            break;
                        case FOCUS_DOWN:
                        case HOVER_DOWN:
                            if (tempViewCenterY < candidateViewCenterY && tempViewCenterY > focusedViewCenterY) {
                                candidateView = tempView;
                            }
                            break;
                    }
                }
            }
        }

        return candidateView;
    }

    public int getDescendantFocusability() {
        return mViewGroupFlags & FLAG_MASK_FOCUSABILITY;
    }

    public void setDescendantFocusability(int focusability) {
        switch (focusability) {
            case FOCUS_BEFORE_DESCENDANTS:
            case FOCUS_AFTER_DESCENDANTS:
            case FOCUS_BLOCK_DESCENDANTS:
                break;
            default:
                throw new IllegalArgumentException("must be one of FOCUS_BEFORE_DESCENDANTS, " + "FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS");
        }
        mViewGroupFlags &= ~FLAG_MASK_FOCUSABILITY;
        mViewGroupFlags |= (focusability & FLAG_MASK_FOCUSABILITY);
    }

    public int getIndex(GLView view) {
        Iterator<GLView> iterator = mGLViews.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            if (iterator.next() == view) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.glview.GLView#getLoaded()
     */
    @Override
    public boolean getLoaded() {
        // To enhance loading speed. just return true.
        // com.samsung.android.app.camera.glview.GLView#draw() will call loading procedure.
        return true;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return mGLViews.size();
    }

    /**
     * Gets the view.
     *
     * @param index the index
     * @return the view
     */
    public GLView getView(int index) {
        return mGLViews.get(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#initSize()
     */
    @Override
    public void initSize() {
        float right = getLeft();
        float bottom = getTop();

        for (GLView view : mGLViews) {
            if (view.getLeft() + view.getWidth() > right) {
                right = view.getLeft() + view.getWidth();
            }
            if (view.getTop() + view.getHeight() > bottom) {
                bottom = view.getTop() + view.getHeight();
            }
        }
        if (!getSizeSpecified()) {
            updateSize(right - getLeft(), bottom - getTop());
        }
    }

    @Override
    public void onAlphaUpdated() {
        super.onAlphaUpdated();
        for (GLView view : mGLViews) {
            view.onAlphaUpdated();
        }
    }

    @Override
    public void onHoverIndicatorColorChanged() {
        for (GLView view : mGLViews) {
            view.onHoverIndicatorColorChanged();
        }
        super.onHoverIndicatorColorChanged();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#onLayoutMove()
     */
    @Override
    public void onLayoutUpdated() {
        super.onLayoutUpdated();
        for (GLView view : mGLViews) {
            view.onLayoutUpdated();
        }
    }

    public boolean onRequestFocusInDescendants(int direction, GLView previouslyFocusedView) {
        int index;
        int increment;
        int end;
        int count = getSize();
        if ((direction & FOCUS_FORWARD) != 0) {
            index = 0;
            increment = 1;
            end = count;
        } else {
            index = count - 1;
            increment = -1;
            end = -1;
        }

        @SuppressWarnings("unchecked")
        CopyOnWriteArrayList<GLView> list = (CopyOnWriteArrayList<GLView>) mGLViews.clone(); // Have to use clone to avoid concurrentmodification exception.
        for (int i = index; i != end; i += increment) {
            if (i >= list.size())
                break;
            GLView child = list.get(i);
            if (child.isVisible() && (child.isFocusable() || child instanceof GLViewGroup)) { // TODO: Is this optimal place to check visibility? check again.
                if (child.requestFocus(direction, previouslyFocusedView)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onReset() {
        for (GLView view : mGLViews) {
            view.reset();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#removeView(com.samsung.android.app.camera.glview.GLView)
     */
    @Override
    public void removeView(GLView view) {
        if (view == null)
            return;

        if (!mGLViews.remove(view)) {
            for (GLView glView : mGLViews) {
                glView.removeView(view);
            }
        }
        view.onLayoutUpdated();
        view.onAlphaUpdated();
        view.onVisibilityChanged(GONE);
    }

    @Override
    public boolean requestFocus(int direction, GLView previouslyFocusedView) {
        int descendantFocusability = getDescendantFocusability();

        switch (descendantFocusability) {
            case FOCUS_BLOCK_DESCENDANTS:
                return super.requestFocus(direction, previouslyFocusedView);
            case FOCUS_BEFORE_DESCENDANTS: {
                final boolean took = super.requestFocus(direction, previouslyFocusedView);
                return took ? took : onRequestFocusInDescendants(direction, previouslyFocusedView);
            }
            case FOCUS_AFTER_DESCENDANTS: {
                final boolean took = onRequestFocusInDescendants(direction, previouslyFocusedView);
                return took ? took : super.requestFocus(direction, previouslyFocusedView);
            }
            default:
                throw new IllegalStateException("descendant focusability must be " + "one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS " + "but is "
                        + descendantFocusability);
        }
    }

    @Override
    public void setDragListener(DragListener l) {
        for (GLView view : mGLViews) {
            view.setDragListener(l);
        }
        super.setDragListener(l);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#setPaddings(android.graphics.Rect)
     */
    @Override
    public void setPaddings(Rect paddings) {
        super.setPaddings(paddings);
        for (GLView view : mGLViews) {
            Rect childPaddings = new Rect(0, 0, 0, 0);
            if ((view.getLeft() - getLeft() < paddings.left) && (view.getLeft() >= getLeft())) {
                childPaddings.left = (int) (paddings.left - (view.getLeft() - getLeft()));
            } else {
                childPaddings.left = view.getPaddings().left;
            }
            if ((getRight() - view.getRight() < paddings.right) && (getRight() >= view.getRight())) {
                childPaddings.right = (int) (paddings.right - (getRight() - view.getRight()));
            } else {
                childPaddings.right = view.getPaddings().right;
            }
            if ((view.getTop() - getTop() < paddings.top) && (view.getTop() >= getTop())) {
                childPaddings.top = (int) (paddings.top - (view.getTop() - getTop()));
            } else {
                childPaddings.top = view.getPaddings().top;
            }
            if ((getBottom() - view.getBottom() < paddings.bottom) && (getBottom() >= view.getBottom())) {
                childPaddings.bottom = (int) (paddings.bottom - (getBottom() - view.getBottom()));
            } else {
                childPaddings.bottom = view.getPaddings().bottom;
            }

            view.setPaddings(childPaddings);
        }
    }

    /**
     * Sets the shader fading position. Refer to {@link GLProgramStorage} for more information.
     *
     * @param start the shader start position
     * @param end   the shader end position
     */
    @Override
    public void setShaderFadingPosition(float start, float end) {
        for (GLView view : mGLViews) {
            view.setShaderFadingPosition(start, end);
        }
        super.setShaderFadingPosition(start, end);
    }

    /**
     * Sets the shader fading bias. Refer to {@link GLProgramStorage} for more information.
     *
     * @param sideOffset  the side shader bias
     * @param colorOffset the color shader bias
     */
    @Override
    public void setShaderFadingOffset(float sideOffset, float colorOffset) {
        for (GLView view : mGLViews) {
            view.setShaderFadingOffset(sideOffset, colorOffset);
        }
        super.setShaderFadingOffset(sideOffset, colorOffset);
    }

    /**
     * Sets the shader parameter. Refer to {@link GLProgramStorage} for more information
     *
     * @param parameter the shader parameter
     */
    @Override
    public void setShaderParameter(float parameter) {
        // TODO : Need to implement step calculation for child.
        for (GLView view : mGLViews) {
            view.setShaderParameter(parameter);
        }
        super.setShaderParameter(parameter);
    }

    /**
     * Sets the shader program. Refer to {@link GLProgramStorage} for more information.
     *
     * @param type the shader program type
     */
    @Override
    public void setShaderProgram(int type) {
        for (GLView view : mGLViews) {
            view.setShaderProgram(type);
        }
        super.setShaderProgram(type);
    }

    /**
     * Sets the shader step. Refer to {@link GLProgramStorage} for more information
     *
     * @param step the shader step
     */
    @Override
    public void setShaderStep(float step) {
        // TODO : Need to implement step calculation for child.
        for (GLView view : mGLViews) {
            view.setShaderStep(step);
        }
        super.setShaderStep(step);
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        for (GLView view : mGLViews) {
            view.updateLayout();
        }
    }

    /**
     * Update size.
     */
    public void updateSize() {
        float right = getLeft();
        float bottom = getTop();
        for (GLView view : mGLViews) {
            if (view.getLeft() + view.getWidth() > right) {
                right = view.getLeft() + view.getWidth();
            }
            if (view.getTop() + view.getHeight() > bottom) {
                bottom = view.getTop() + view.getHeight();
            }
        }
        updateSize(right - getLeft(), bottom - getTop());
        refreshClipRect();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#onDraw()
     */
    @Override
    protected void onDraw() {
        float[] matrix = getMatrix();
        Rect clipRect = getClipRect();
        for (GLView view : mGLViews) {
            clip();
            //GL11.glPushMatrix();
            view.draw(matrix, clipRect);
            //GL11.glPopMatrix();
            clearClip();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#onLoad()
     */
    @Override
    protected boolean onLoad() {
        // To enhance loading speed. just return true.
        // com.samsung.android.app.camera.glview.GLView#draw() will call loading procedure.
        return true;
    }

    @Override
    protected void onOutOfScreen() {
        for (GLView view : mGLViews) {
            view.onOutOfScreen();
        }
        super.onOutOfScreen();
    }

    @Override
    protected void onVisibilityChanged(int visibility) {
        for (GLView view : mGLViews) {
            view.onVisibilityChanged(visibility);
        }
        super.onVisibilityChanged(visibility);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.samsung.android.app.camera.glview.GLView#onOrientationChanged(int)
     */
    @Override
    void onOrientationChanged(int orientation) {
        for (GLView view : mGLViews) {
            view.onOrientationChanged(orientation);
        }
        super.onOrientationChanged(orientation);
    }

    private void initViewGroup() {
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
    }
}
