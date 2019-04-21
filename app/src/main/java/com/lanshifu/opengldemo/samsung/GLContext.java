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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The Class GLContext.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class GLContext implements Renderer, OnInitListener, FrameCallback, View.OnHoverListener {

    public static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    public static final int ORIENTATION_CHANGE_MARGIN_IN_DEGREE = 10;
    public static final int NOT_FOCUSED = 0;
    public static final int FOCUSED = 1;
    public static final int HOVER_ENTER = 0;
    public static final int HOVER_EXIT = 1;
    static final boolean TEXTURE_SHARING = true;
    /**
     * Show button background setting key
     */
    private static final String SHOW_BUTTON_BACKGROUND_SETTING_KEY = "show_button_background";
    /**
     * The Constant TAG.
     */
    private static final String TAG = "GLContext";
    private static final char ENABLED_SERVICES_SEPARATOR = ':';
    private static final int FOCUS_INDICATOR_DEFAULT_THICKNESS = 1; // DP
    private static final int HOVER_INDICATOR_DEFAULT_THICKNESS = 2; // DP
    private static final int FOCUS_INDICATOR_DEFAULT_COLOR = Color.argb(230, 0, 0, 255);
    private static final int HOVER_INDICATOR_DEFAULT_COLOR = Color.argb(230, 0, 76, 232);  //004ce8, 90%
    private static final int FPS_CALCULATION_INTERVAL_THRESHOLD = 100;
    private static final int WAITING_FRAMES_COUNT = 1;
    private static final int WAITING_FRAMES_TIMEOUT = 50;

    /**
     * SensorHub name
     */
    private static final String SENSORHUB_SERVICE_NAME = "scontext";
    private static final Object mInitLock = new Object();
    private static final Object mFrameLock = new Object();
    private static final Object mOrientationUpdateLock = new Object();
    /**
     * The m application context.
     */
    private static Context mApplicationContext;
    private static Resources mResources;
    /**
     * The m last orientation.
     */
    private static int mLastOrientation = GLView.ORIENTATION_0;
    private static int mOrientationCompensationValue = 0;
    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;
    private static int mNavigatorHeight = 0;
    private List<HoverEventChangedObserver> mObservers = new ArrayList<>();
    private final Object mObserversLock = new Object();
    private final Object mDeleteTexturesLock = new Object();
    private final Object mWaitFrameLock = new Object();
    /**
     * The m root view.
     */
    private GLViewGroup mRootView = null;
    private GLView mCurrentFocusedView = null;
    private GLView mCurrentHoverFocusedView = null;
    private AccessibilityNodeInfo mAccNode = null;
    /**
     * The m identity matrix.
     */
    private float[] mIdentityMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    /**
     * The m clip rect.
     */
    private Rect mClipRect = new Rect();
    /**
     * The m main handler.
     */
    private Handler mMainHandler = null;
    /**
     * The m main handler thread.
     */
    private HandlerThread mMainHandlerThread = null;
    /**
     * The m frame handler.
     */
    private Handler mFrameHandler = null;
    /**
     * The m frame handler thread.
     */
    private HandlerThread mFrameHandlerThread = null;
    /**
     * The m last touch view.
     */
    private GLView mLastTouchView;
    private GLView mLastHoverView;
    private GLSurfaceView mGLSurfaceView;
    private ArrayList<GLTexture> mTexturesToDelete = new ArrayList<>();
    private GLProgramStorage mGLProgramStorage = null;
    private boolean mDirty = false;
    private boolean mRenderRequested = false;
    private boolean mPaused = false;
    private boolean mScrollBarAutoHide = true;
    private boolean mAlignToPixel = true; // Use integer value to align textures to pixels.
    private boolean mRippleEffectEnabled = true;
    private int mRippleEffectColor = 0;
    private float mDensity = 1f;
    private TextToSpeech mTts;
    private boolean mIsFocusIndicatorVisible = false;
    private boolean mFocusIndicatorVisibilityChanged = false;
    private int mHoverIndicatorColor = HOVER_INDICATOR_DEFAULT_COLOR;
    private int mFocusIndicatorColor = FOCUS_INDICATOR_DEFAULT_COLOR;
    private int mHoverIndicatorThickness = 0;
    private int mFocusIndicatorThickness = 0;
    private GLPreviewData mGLPreviewData = null;
    private int mTapDir = 0;
    private int mTapDirState = 0;
    private View mHoverBaseView = null;
    private long mFrameNum = 0;
    private boolean mIsAccessibilityNodeEnabled = false;
    private boolean mIsAccessibilityServiceEnabled = false;
    private long mFrameCountForFPS = 0;
    private long mAccumulatedTime = 0;
    private long mPrevFrameTimeStamp = 0;
    private int mEstimatedFPS = 0;
    private Choreographer mChoreographer = null;
    private boolean mIsFocusNavigationEnabled = true;
    private AccessibilityDelegate mAccessibilityDelegate = null;
    private boolean mShowButtonBackgroundEnabled = false; // Accessibility setting.
    private volatile CountDownLatch mLatch = null;
    /**
     * GLTextureStorage
     */
    private GLTextureStorage mGLTextureStorage;

    /**
     * The Orientation Listener of android
     */
    private OrientationEventListener mOrientationListener = null;
    /**
     * Check SemContext is possilble or not
     */
    private boolean mIsSemContextListenerAvailable = false;
    private boolean mIsTouchExplorationEnabled;
    /**
     * Check if camera is launched as multi window mode.
     */
    private boolean mIsMultiWindowMode = false;
    /**
     * The m listener.
     */
    private GLInitializeListener mListener;
    /**
     * If talkback is on, last touch MotionEvent is saved to check which operation will be executed.
     */
    private MotionEvent mLastMotionEvent = null;
    /**
     * Receive the Hover Swipe Message from CameraAccessibilityService by LocalBroadcastManager
     * Only left, right swipe actions are valid.
     */
    private BroadcastReceiver mHoverSwipeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isFocusNavigationEnabled() || intent.getExtras() == null) {
                return;
            }
            int gestureId = intent.getExtras().getInt(AccessibilityGestureHandler.KEY_GESTURE_ID);

            switch (gestureId) {
                case AccessibilityGestureHandler.GESTURE_SWIPE_LEFT:
                    if (mLastOrientation == GLView.ORIENTATION_0) {
                        onHoverSwipeEvent(GLView.HOVER_LEFT);
                    } else if (mLastOrientation == GLView.ORIENTATION_180) {
                        onHoverSwipeEvent(GLView.HOVER_RIGHT);
                    }
                    break;
                case AccessibilityGestureHandler.GESTURE_SWIPE_RIGHT:
                    if (mLastOrientation == GLView.ORIENTATION_0) {
                        onHoverSwipeEvent(GLView.HOVER_RIGHT);
                    } else if (mLastOrientation == GLView.ORIENTATION_180) {
                        onHoverSwipeEvent(GLView.HOVER_LEFT);
                    }
                    break;
                case AccessibilityGestureHandler.GESTURE_SWIPE_UP:
                    if (mLastOrientation == GLView.ORIENTATION_90) {
                        onHoverSwipeEvent(GLView.HOVER_LEFT);
                    } else if (mLastOrientation == GLView.ORIENTATION_270) {
                        onHoverSwipeEvent(GLView.HOVER_RIGHT);
                    }
                    break;
                case AccessibilityGestureHandler.GESTURE_SWIPE_DOWN:
                    if (mLastOrientation == GLView.ORIENTATION_90) {
                        onHoverSwipeEvent(GLView.HOVER_RIGHT);
                    } else if (mLastOrientation == GLView.ORIENTATION_270) {
                        onHoverSwipeEvent(GLView.HOVER_LEFT);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Receive the Accessibility view focused event from AccessibilityGestureHandler by LocalBroadcastManager
     */
    private BroadcastReceiver mAccViewFocusedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isFocusNavigationEnabled()) {
                return;
            }
            clearFocus();
            setDirty(true);

            Bundle bundle = intent.getBundleExtra(AccessibilityGestureHandler.KEY_ACCESSIBILITY_EVENT_ID);
            if (bundle == null) {
                mGLSurfaceView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
                return;
            }
            mAccNode = bundle.getParcelable(AccessibilityGestureHandler.KEY_ACCESSIBILITY_NODE_INFO_ID);
        }
    };

    private ContentObserver mDisplaySizeObserver = new ContentObserver(mMainHandler) {
        @Override
        public void onChange(boolean selfChange) {
            Log.v(TAG, "Display size changed");
            updateScreenSize(mScreenWidth, mScreenHeight);
        }
    };
    private ContentObserver mCursorColorObserver = new ContentObserver(mMainHandler) {
        @Override
        public void onChange(boolean selfChange) {
            Log.v(TAG, "Cursor color changed");
            if (mRootView != null) {
                mRootView.onHoverIndicatorColorChanged();
            }
        }
    };
    private ContentObserver mTouchExplorationEnabledContentObserver = new ContentObserver(mMainHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.v(TAG, "Touch Exploration ContentObserver onChange");

            updateTouchExplorationEnabled();

            if (mIsTouchExplorationEnabled) {
                enableAccessibilityService(mApplicationContext);
            } else {
                disableAccessibilityService(mApplicationContext);
            }
        }
    };
    private ContentObserver mEnabledAccessibilityServicesContentObserver = new ContentObserver(mMainHandler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v(TAG, "Accessibility Services ContentObserver onChange");

            if (!isTalkBackEnabled()) {
                disableAccessibilityService(mApplicationContext);
            }
        }
    };
    private ContentObserver mSettingInteractionControlObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (mPaused) {
                Log.w(TAG, "GLContext is pausing, not updated");
                return;
            }
        }
    };

    {
        Matrix.setIdentityM(mIdentityMatrix, 0);
    }

    /**
     * Instantiates a new tw gl context.
     *
     * @param context            the context
     * @param listener           {@link GLInitializeListener}
     * @param surfaceView        {@link GLSurfaceView}
     * @param initialScreenPoint initial screen point from {@link android.view.WindowManager}
     * @param isMultiWindowMode  true if launched as multi window mode, false otherwise.
     * @param isDeviceLandscape  true if device's orientation is landscape, false otherwise.
     */
    public GLContext(Context context, GLInitializeListener listener, GLSurfaceView surfaceView, Point initialScreenPoint, boolean isMultiWindowMode, boolean isDeviceLandscape) {
        synchronized (mInitLock) {
            mApplicationContext = context;
            mResources = mApplicationContext.getResources();
        }

        mGLTextureStorage = new GLTextureStorage();
        mListener = listener;
        mGLSurfaceView = surfaceView;
        mIsMultiWindowMode = isMultiWindowMode;

        mDensity = mApplicationContext.getResources().getDisplayMetrics().density;

        int screenPortraitWidth;
        int screenPortraitHeight;
        if (isDeviceLandscape) {
            screenPortraitWidth = initialScreenPoint.y;
            screenPortraitHeight = initialScreenPoint.x;
        } else {
            screenPortraitWidth = initialScreenPoint.x;
            screenPortraitHeight = initialScreenPoint.y;
        }
        updateScreenSize(screenPortraitWidth, screenPortraitHeight);
        setOrientationListener();

        updateTouchExplorationEnabled();

        // Get default ripple color
        TypedValue outValue = new TypedValue();
        mApplicationContext.getTheme().resolveAttribute(android.R.attr.colorControlHighlight, outValue, true);
        mRippleEffectColor = outValue.data;



        startFrameHandlerThread();
    }

    public static int getColor(int id) {
        return mResources.getColor(id);
    }

    public static float getDimension(int id) {
        return mResources.getDimension(id);
    }

    public static int getInteger(int id) {
        return mResources.getInteger(id);
    }

    /**
     * Gets the last orientation.
     *
     * @return the last orientation ({@link GLView#ORIENTATION_0}, {@link GLView#ORIENTATION_90}, {@link GLView#ORIENTATION_180} or {@link GLView#ORIENTATION_270})
     */
    public static int getLastOrientation() {
        return mLastOrientation;
    }

    private void setLastOrientation(int orientation) {
        synchronized (mOrientationUpdateLock) {
            mLastOrientation = orientation;
        }
    }

    /**
     * Gets the absolute height of the navigator bar in pixels.
     *
     * @return the height of software navigator bar. if there is no software navigator bar, returns 0
     */
    public static int getNavigatorHeightPixels() {
        return mNavigatorHeight;
    }

    public static int getOrientationCompensationValue() {
        return mOrientationCompensationValue;
    }

    public static void setOrientationCompensationValue(int value) {
        mOrientationCompensationValue = value;
    }

    /**
     * Gets the absolute height of the display in pixels.
     *
     * @return the screen height of the display (shortest side)
     */
    public static int getScreenHeightPixels() {
        return mScreenHeight;
    }

    /**
     * Gets the absolute height of the display except navigation bar in pixels.
     *
     * @return the width of the screen(longest side) except navigation bar
     */
    public static int getScreenHeightPixelsExceptNavigation() {
        return mScreenHeight - mNavigatorHeight;
    }

    /**
     * Gets the absolute width of the display in pixels.
     *
     * @return the width of the screen(longest side)
     */
    public static int getScreenWidthPixels() {
        return mScreenWidth;
    }

    public static String getString(int id) {
        return mResources.getString(id);
    }

    /**
     * Check HW kaypad model
     *
     * @return hasKeyPad
     */
    public static boolean hasHardwareKeyPad() {
        if (mApplicationContext == null) {
            return false;
        }

        Configuration config = mApplicationContext.getResources().getConfiguration();
        return config.keyboard == Configuration.KEYBOARD_12KEY && config.navigation == Configuration.NAVIGATION_DPAD;
    }

    /**
     * Check HW screen orientation
     *
     * @return isLandscape
     */
    public static boolean isScreenOrientationLandscape() {
        //TODO : Need to implement the runtime feature routine.
        return false;
    }

    public static boolean isTalkBackEnabled() {
        return false;
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    protected static Context getApplicationContext() {
        return mApplicationContext;
    }

    /**
     * To enable & disable Accessibility Service for Camera, get the App list what use the Accessibility Service from the Settings.
     */
    private static Set<ComponentName> getEnabledServicesFromSettings(Context context) {
        final SimpleStringSplitter sStringColonSplitter = new SimpleStringSplitter(ENABLED_SERVICES_SEPARATOR);

//        final String enabledServicesSetting = GLUtil.getStringForUser(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, UserHandle.SEM_USER_OWNER);
        final String enabledServicesSetting = null;
        if (enabledServicesSetting == null) {
            return Collections.emptySet();
        }

        final Set<ComponentName> enabledServices = new HashSet<>();
        final SimpleStringSplitter colonSplitter = sStringColonSplitter;
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            final String componentNameString = colonSplitter.next();
            final ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
            if (enabledService != null) {
                enabledServices.add(enabledService);
            }
        }

        return enabledServices;
    }

    /**
     * Clear.
     */
    public void clear() {
        if (mRootView != null)
            mRootView.clear();
        mRootView = null;

        mListener = null;

        synchronized (mDeleteTexturesLock) {
            mTexturesToDelete.clear();
        }
        mGLTextureStorage.clear();
        mGLTextureStorage = null;
        mHoverBaseView = null;

        mChoreographer = null;
        if (mFrameHandlerThread != null) {
            mFrameHandlerThread.quitSafely();
            mFrameHandlerThread = null;
        }
    }

    public void clearFocus() {
        if (mCurrentFocusedView != null) {
            mCurrentFocusedView.onFocusStatusChanged(NOT_FOCUSED);
            mCurrentFocusedView = null;
        }
        if (mCurrentHoverFocusedView != null) {
            mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
            mCurrentHoverFocusedView = null;
        }
        mIsFocusIndicatorVisible = false;
    }

    /**
     * Disable the Accessibility Service for Camera.
     */
    public void disableAccessibilityService(Context context) {
        if (!mIsAccessibilityServiceEnabled) {
            return;
        }

        Log.v(TAG, "disableAccessibilityService");
        final SimpleStringSplitter sStringColonSplitter = new SimpleStringSplitter(ENABLED_SERVICES_SEPARATOR);
        Set<ComponentName> enabledServices = getEnabledServicesFromSettings(context);
        if (enabledServices == (Set<?>) Collections.emptySet()) {
            enabledServices = new HashSet<>();
        }

        ComponentName toggledService = ComponentName.unflattenFromString("com.sec.android.app.camera/com.samsung.android.glview.AccessibilityGestureHandler");
        boolean accessibilityEnabled = false;

        enabledServices.remove(toggledService);

        // Check how many enabled and installed services are present.
        Set<ComponentName> installedServices = new HashSet<>();
        for (ComponentName enabledService : enabledServices) {
            if (installedServices.contains(enabledService)) {
                // Disabling the last service disables accessibility.
                accessibilityEnabled = true;
                break;
            }
        }

        // Update the enabled services setting.
        StringBuilder enabledServicesBuilder = new StringBuilder();
        // Keep the enabled services even if they are not installed since we
        // have no way to know whether the application restore process has
        // completed. In general the system should be responsible for the
        // clean up not settings.
        for (ComponentName enabledService : enabledServices) {
            enabledServicesBuilder.append(enabledService.flattenToString());
            enabledServicesBuilder.append(ENABLED_SERVICES_SEPARATOR);
        }

        final int enabledServicesBuilderLength = enabledServicesBuilder.length();
        if (enabledServicesBuilderLength > 0) {
            enabledServicesBuilder.deleteCharAt(enabledServicesBuilderLength - 1);
        }

        String enabledServicesSetting = null;
        enabledServicesSetting = enabledServicesBuilder.toString();
        Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, enabledServicesSetting);

        if (enabledServicesSetting != null) {
            SimpleStringSplitter colonSplitter = sStringColonSplitter;
            colonSplitter.setString(enabledServicesSetting);

            while (colonSplitter.hasNext()) {
                String componentNameString = colonSplitter.next();
                ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

                if (enabledService != null) {
                    accessibilityEnabled = true;
                    break;
                }
            }
        }


        // Update accessibility enabled.
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, accessibilityEnabled ? 1 : 0);

        LocalBroadcastManager.getInstance(mApplicationContext).unregisterReceiver(mHoverSwipeReceiver);
        LocalBroadcastManager.getInstance(mApplicationContext).unregisterReceiver(mAccViewFocusedReceiver);

        mIsAccessibilityServiceEnabled = false;
    }

    public void disableFocusNavigation() {
        mIsFocusNavigationEnabled = false;
    }

    /*
     * (non-Javadoc)
     * @see android.view.Choreographer.FrameCallback#doFrame(long)
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        synchronized (mFrameLock) {
            mGLSurfaceView.requestRender();
            mRenderRequested = false;
        }
    }

    public void dumpViewHierarchy() {
        Log.w(TAG, "=======================DUMP_START=======================");
        if (mRootView != null) {
            mRootView.dumpViewHierarchy(0);
        }
        Log.w(TAG, "=======================DUMP_END=======================");
    }

    /**
     * The enableAccessibilityNode for testBuddy.
     */
    public void enableAccessibilityNode(boolean enabled) {
        //Log.e(TAG, "enableAccessibilityNode : " + enabled);
        mIsAccessibilityNodeEnabled = enabled;
    }

    /**
     * Enable the Accessibility Service for Camera to get the swipe gesture event.
     */
    public void enableAccessibilityService(Context context) {
        if (mIsAccessibilityServiceEnabled) {
            return;
        }

        mIsAccessibilityServiceEnabled = true;

        Log.v(TAG, "enableAccessibilityService");
        Set<ComponentName> enabledServices = getEnabledServicesFromSettings(context);
        if (enabledServices == (Set<?>) Collections.emptySet()) {
            enabledServices = new HashSet<>();
        }

        ComponentName toggledService = ComponentName.unflattenFromString("com.sec.android.app.camera/com.samsung.android.glview.AccessibilityGestureHandler");

        enabledServices.add(toggledService);

        // Update the enabled services setting.
        StringBuilder enabledServicesBuilder = new StringBuilder();
        for (ComponentName enabledService : enabledServices) {
            enabledServicesBuilder.append(enabledService.flattenToString());
            enabledServicesBuilder.append(ENABLED_SERVICES_SEPARATOR);
        }

        final int enabledServicesBuilderLength = enabledServicesBuilder.length();
        if (enabledServicesBuilderLength > 0) {
            enabledServicesBuilder.deleteCharAt(enabledServicesBuilderLength - 1);
        }

        String enabledServicesSetting = null;
        enabledServicesSetting = enabledServicesBuilder.toString();

        Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, enabledServicesSetting);
        // Update accessibility enabled.
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);

        LocalBroadcastManager.getInstance(mApplicationContext).registerReceiver(mHoverSwipeReceiver,
                new IntentFilter(AccessibilityGestureHandler.ACTION_ACCESSIBILITY_GESTURE_DETECTED));
        LocalBroadcastManager.getInstance(mApplicationContext).registerReceiver(mAccViewFocusedReceiver,
                new IntentFilter(AccessibilityGestureHandler.ACTION_ACCESSIBILITY_VIEW_FOCUS_GONE));
    }

    public void enableFocusNavigation() {
        mIsFocusNavigationEnabled = true;
    }

    /**
     * Sets ripple effect.
     *
     * @param enable true will enable ripple effect.
     */
    public void enableRippleEffect(boolean enable) {
        mRippleEffectEnabled = enable;
    }

    public GLView findNextFocusFromView(GLViewGroup rootView, GLView focusedView, int direction) {
        GLView resultView = null;
        if (rootView == null) {
            resultView = mRootView.findNextFocusFromView(focusedView, direction); // Default
        } else {
            resultView = rootView.findNextFocusFromView(focusedView, direction);
        }
        return resultView;
    }

    /**
     * Find view by id.
     *
     * @param id the id
     * @return the tw gl view
     */
    public GLView findViewById(int id) {
        if (mRootView != null)
            return mRootView.findViewById(id);

        return null;
    }

    /**
     * Find view by objectTag.
     *
     * @param objectTag the glview object tag
     * @return glview
     */
    public GLView findViewByObjectTag(String objectTag) {
        if (mRootView != null)
            return mRootView.findViewByObjectTag(objectTag);

        return null;
    }

    /**
     * Find view by tag.
     *
     * @param tag the tag
     * @return the tw gl view
     */
    public GLView findViewByTag(int tag) {
        if (mRootView != null)
            return mRootView.findViewByTag(tag);

        return null;
    }

    public AccessibilityDelegate getAccessibilityDelegate() {
        return mAccessibilityDelegate;
    }

    public boolean getAlignToPixel() {
        return mAlignToPixel;
    }

    public void setAlignToPixel(boolean value) {
        mAlignToPixel = value;
    }

    public float getDensity() {
        return mDensity;
    }

    public int getEstimatedFPS() {
        return mEstimatedFPS;
    }

    /**
     * Retrieve the color of the focus indicator(for keyboard navigation).
     *
     * @return color of focus indicator.
     */
    public int getFocusIndicatorColor() {
        return mFocusIndicatorColor;
    }

    /**
     * Retrieve the thickness of focus indicator(for keyboard navigation).
     *
     * @return thickness of focus indicator.
     */
    public int getFocusIndicatorThickness() {
        return mFocusIndicatorThickness;
    }

    public GLPreviewData getGLPreviewData() {
        return mGLPreviewData;
    }

    /**
     * The getGLSurfaceView for testBuddy.
     */
    public GLSurfaceView getGLSurfaceView() {
        return mGLSurfaceView;
    }

    public GLTextureStorage getGLTextureStorage() {
        return mGLTextureStorage;
    }

    public View getHoverBaseView() {
        return mHoverBaseView;
    }

    public void setHoverBaseView(View view) {
        mHoverBaseView = view;
    }

    /**
     * Retrieve the color of the hover indicator(for explore by touch).
     *
     * @return color of focus indicator.
     */
    public int getHoverIndicatorColor() {
        return mHoverIndicatorColor;
    }

    /**
     * Retrieve the thickness of hover indicator(for explore by touch).
     *
     * @return thickness of focus indicator.
     */
    public int getHoverIndicatorThickness() {
        return mHoverIndicatorThickness;
    }

    public GLView getLastHoverView() {
        return mLastHoverView;
    }

    /**
     * Gets the main handler.
     *
     * @return the main handler
     */
    public Handler getMainHandler() {
        return mMainHandler;
    }

    public GLProgramStorage getProgramStorage() {
        return mGLProgramStorage;
    }

    public float[] getProjMatrix() {
        return mProjMatrix;
    }

    /**
     * Gets default ripple effect color.
     *
     * @return default ripple effect color
     */
    public int getRippleEffectColor() {
        return mRippleEffectColor;
    }

    /**
     * Sets default ripple effect color.
     *
     * @param color ripple effect color
     */
    public void setRippleEffectColor(int color) {
        mRippleEffectColor = color;
    }

    /**
     * Gets the root view.
     *
     * @return the root view
     */
    public GLViewGroup getRootView() {
        return mRootView;
    }

    /**
     * Gets the screen aspect ratio.
     *
     * @return the screen aspect ratio
     */
    public float getScreenAspectRatio() {
        return (float) mScreenWidth / (float) mScreenHeight;
    }

    public boolean getScrollBarAutoHide() {
        return mScrollBarAutoHide;
    }

    public void setScrollBarAutoHide(boolean value) {
        mScrollBarAutoHide = value;
    }



    /**
     * The isEnableAccessibilityNode for testBuddy.
     */
    public boolean isEnableAccessibilityNode() {
        if (mApplicationContext != null) {
            AccessibilityManager manager = (AccessibilityManager) mApplicationContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (manager == null || !manager.isEnabled())
                return false;
        }
        return mIsAccessibilityNodeEnabled;
    }

    public boolean isFocusIndicatorVisible() {
        return mIsFocusIndicatorVisible;
    }

    public boolean isFocusNavigationEnabled() {
        return mIsFocusNavigationEnabled;
    }

    public boolean isHoveringEnabled() {
        return false;
    }

    /**
     * Gets current ripple effect value.
     *
     * @return true if ripple effect is enabled.
     */
    public boolean isRippleEffectEnabled() {
        return mRippleEffectEnabled;
    }

    /**
     * Checks if show button background setting was enabled.
     *
     * @return true if show button background is enabled.
     */
    public boolean isShowButtonBackgroundEnabled() {
        return mShowButtonBackgroundEnabled;
    }

    public boolean isTouchExplorationEnabled() {
        return mIsTouchExplorationEnabled;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        //noinspection StatementWithEmptyBody
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            // Need to flush errors
        }
        synchronized (mDeleteTexturesLock) {
            for (GLTexture texture : mTexturesToDelete) {
                texture.clearTexture();
            }
            mTexturesToDelete.clear();
        }
        if (mPaused) {
            return;
        }

        mDirty = false;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //GL11.glMatrixMode(GLES20.GL_MODELVIEW);
        //GL11.glLoadIdentity();
        //GL11.glOrthof(0, getScreenWidthPixels(), getScreenHeightPixels(), 0, -1, 1);      // left, right, bottom, top, near, far

        long currentTime = System.currentTimeMillis();
        long interval = currentTime - mPrevFrameTimeStamp;

        if (mPrevFrameTimeStamp != 0 && interval < FPS_CALCULATION_INTERVAL_THRESHOLD) {
            mFrameCountForFPS++;
            mAccumulatedTime += interval;
            if (mAccumulatedTime != 0)
                mEstimatedFPS = (int) ((1000 * mFrameCountForFPS) / mAccumulatedTime);
        }
        mPrevFrameTimeStamp = currentTime;
        if (++mFrameNum < 5) {
            currentTime = System.currentTimeMillis();
            Log.e(TAG, "Start drawing frame #" + mFrameNum);
        }

        if (mRootView != null) {
            mRootView.draw(mIdentityMatrix, mClipRect);
        }

        if (mFrameNum < 5) {
            Log.e(TAG, "End drawing frame #" + mFrameNum + " Elapsed time: " + (System.currentTimeMillis() - currentTime) + "ms");
        }

        signalOnDrawFrame();
    }

    public void onFocusChanged(GLView view) {
        if (mCurrentFocusedView != null) {
            mCurrentFocusedView.onFocusStatusChanged(NOT_FOCUSED);
        }
        if (view != null) {
            view.onFocusStatusChanged(FOCUSED);
        }
        mCurrentFocusedView = view;
        if (isTouchExplorationEnabled()) {
            if (view == null) {
                if (mCurrentHoverFocusedView != null) {
                    mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
                }
                mCurrentHoverFocusedView = view;
            } else {
                onHoverChanged(view, null);
            }
        }
        setDirty(true);
    }

    @Override
    public boolean onHover(View arg0, MotionEvent arg1) {
        if (!isFocusNavigationEnabled()) {
            return false;
        }
        if (mApplicationContext.getApplicationContext() != null) {
            return onHoverEvent(arg1);
        }
        return false;
    }

    public void onHoverChanged(GLView view, MotionEvent e) {
        if (view == null || mApplicationContext == null)
            return;

        if (isTalkBackEnabled() && mIsTouchExplorationEnabled) {
            if (view.isFocusable()) {
                if (mCurrentHoverFocusedView != null) {
                    mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
                }
                view.onHoverStatusChanged(HOVER_ENTER);
                if (mAccNode != null) {
                    mAccNode.performAction(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
                    mAccNode.recycle();
                    mAccNode = null;
                }
                mCurrentHoverFocusedView = view;
                mLastHoverView = view;
                setDirty(true);
            }
        } else if (isHoveringEnabled()) {
            if (mCurrentHoverFocusedView != null) {
                mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
            }
            view.onHoverStatusChanged(HOVER_ENTER);
            mCurrentHoverFocusedView = view;
        }
    }

    public boolean onHoverEvent(MotionEvent event) {
        if (mRootView != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    mLastHoverView = mRootView.findViewByCoordinate(event.getX(), event.getY());
                    if (mLastHoverView != null) {
                        notifyHoverEventChanged(mLastHoverView, event);
                        onHoverChanged(mLastHoverView, event);
                    } else {
                        if (isTouchExplorationEnabled()) {
                            if (mCurrentHoverFocusedView != null) {
                                mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
                            }
                        }
                    }
//                Log.v(TAG,"--------ENTER----------");
                    break;
                case MotionEvent.ACTION_HOVER_MOVE: {
                    GLView tempView = mRootView.findViewByCoordinate(event.getX(), event.getY());
                    if (tempView == mLastHoverView) {
                        return true; // if we return false, ACTION_HOVER_ENTER is not coming. To avoid this, return true.
                    }
                    mLastHoverView = tempView;
                    if (mLastHoverView != null) {
                        onHoverChanged(mLastHoverView, event);
                    } else if (isHoveringEnabled()) {
                        if (mCurrentHoverFocusedView != null) {
                            mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
                        }
                    }
//                Log.v(TAG,"--------MOVE--------");
                    break;
                }
                case MotionEvent.ACTION_HOVER_EXIT:
                    if (isHoveringEnabled()) {
                        if (mCurrentHoverFocusedView != null) {
                            mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_EXIT);
                        }
                    } else {
                        GLView tempView = mRootView.findViewByCoordinate(event.getX(), event.getY());
                        if (tempView == null) {
                            if (isTouchExplorationEnabled()) {
                                MotionEvent originEvent = MotionEvent.obtain(event);
                                notifyHoverEventChanged(null, originEvent);
                                originEvent.recycle();
                            }
                        }
                    }
//                Log.v(TAG,"--------EXIT--------");
                    break;
            }
        }
        if (mLastHoverView != null && mLastHoverView.isClickable()) {
            return true;
        }
        return true; // if we return false, ACTION_HOVER_ENTER is not coming. To avoid this, return true.
    }

    public void onHoverSwipeEvent(int hoverEvent) {
        // There is no focused view.
        if (mCurrentHoverFocusedView == null) {
            switch (hoverEvent) {
                case GLView.HOVER_LEFT:
                case GLView.HOVER_RIGHT:
                    if (mRootView != null) {
                        mRootView.requestFocus(GLView.HOVER_DOWN);
                    }
                    break;
            }
        }

        GLView tempView;
        // Normal case
        if (mCurrentHoverFocusedView != null) {
            switch (hoverEvent) {
                case GLView.HOVER_LEFT: // START
                    tempView = findHoverFocusedViewOnSameLine(hoverEvent);
                    if (tempView != null) {
                        tempView.requestFocus();
                        mCurrentHoverFocusedView = tempView;
                    } else {
                        tempView = findHoverFocusedViewFromRightMostBottom(false);
                        if (tempView != null) {
                            tempView.requestFocus();
                            mCurrentHoverFocusedView = tempView;
                        } else { // If the end of the screen, move to right most bottom in the screen.
                            tempView = findHoverFocusedViewFromRightMostBottom(true);
                            if (tempView != null) {
                                tempView.requestFocus();
                                mCurrentHoverFocusedView = tempView;
                            }
                        }
                    }
                    break;
                case GLView.HOVER_RIGHT:
                    tempView = findHoverFocusedViewOnSameLine(hoverEvent);
                    if (tempView != null) {
                        tempView.requestFocus();
                        mCurrentHoverFocusedView = tempView;
                    } else {
                        tempView = findHoverFocusedViewFromLeftMostTop(false);
                        if (tempView != null) {
                            tempView.requestFocus();
                            mCurrentHoverFocusedView = tempView;
                        } else { // If the end of the screen, move to left most top in the screen.
                            tempView = findHoverFocusedViewFromLeftMostTop(true);
                            if (tempView != null) {
                                tempView.requestFocus();
                                mCurrentHoverFocusedView = tempView;
                            }
                        }
                    }
                    break;
            }
        }
        setDirty(true);
    }

    /**
     * Find hover view from the left most top based on the focused view
     *
     * @param isEndOfScreen true if end of screen case, false otherwise
     * @return the found view
     */
    private GLView findHoverFocusedViewFromLeftMostTop(boolean isEndOfScreen) {
        float centerX = (mCurrentHoverFocusedView.getOriginalClipRect().left + mCurrentHoverFocusedView.getOriginalClipRect().right) / 2f;
        float centerY = (mCurrentHoverFocusedView.getOriginalClipRect().top + mCurrentHoverFocusedView.getOriginalClipRect().bottom) / 2f;
        float margin = 0.01f; // For do not include mCurrentHoverFocusedView
        float left = 0;
        float top = 0;

        if (isEndOfScreen) {
            switch (mLastOrientation) {
                case GLView.ORIENTATION_0:
                    left = 0;
                    top = 0;
                    break;
                case GLView.ORIENTATION_180:
                    left = mRootView.getRight();
                    top = mRootView.getBottom();
                    break;
                case GLView.ORIENTATION_90:
                    left = mRootView.getRight();
                    top = 0;
                    break;
                case GLView.ORIENTATION_270:
                    left = 0;
                    top = mRootView.getBottom();
                    break;
            }
        } else {
            switch (mLastOrientation) {
                case GLView.ORIENTATION_0:
                    left = 0;
                    top = centerY + margin;
                    break;
                case GLView.ORIENTATION_180:
                    left = mRootView.getRight();
                    top = centerY - margin;
                    break;
                case GLView.ORIENTATION_90:
                    left = centerX - margin;
                    top = 0;
                    break;
                case GLView.ORIENTATION_270:
                    left = centerX + margin;
                    top = mRootView.getBottom();
                    break;
            }
        }
        return mRootView.findViewFromLeftMostTop(mLastOrientation, left, top);
    }

    /**
     * Find hover view from the right most bottom based on the focused view
     *
     * @param isEndOfScreen true if end of screen case, false otherwise
     * @return the found view
     */
    private GLView findHoverFocusedViewFromRightMostBottom(boolean isEndOfScreen) {
        float centerX = (mCurrentHoverFocusedView.getOriginalClipRect().left + mCurrentHoverFocusedView.getOriginalClipRect().right) / 2f;
        float centerY = (mCurrentHoverFocusedView.getOriginalClipRect().top + mCurrentHoverFocusedView.getOriginalClipRect().bottom) / 2f;
        float margin = 0.01f; // For do not include mCurrentHoverFocusedView
        float left = 0;
        float top = 0;

        if (isEndOfScreen) {
            switch (mLastOrientation) {
                case GLView.ORIENTATION_0:
                    left = mRootView.getRight();
                    top = mRootView.getBottom();
                    break;
                case GLView.ORIENTATION_180:
                    left = 0;
                    top = 0;
                    break;
                case GLView.ORIENTATION_90:
                    left = 0;
                    top = mRootView.getBottom();
                    break;
                case GLView.ORIENTATION_270:
                    left = mRootView.getRight();
                    top = 0;
                    break;
            }
        } else {
            switch (mLastOrientation) {
                case GLView.ORIENTATION_0:
                    left = mRootView.getRight();
                    top = centerY - margin;
                    break;
                case GLView.ORIENTATION_180:
                    left = 0;
                    top = centerY + margin;
                    break;
                case GLView.ORIENTATION_90:
                    left = centerX + margin;
                    top = mRootView.getBottom();
                    break;
                case GLView.ORIENTATION_270:
                    left = centerX - margin;
                    top = 0;
                    break;
            }
        }
        return mRootView.findViewFromRightMostBottom(mLastOrientation, left, top);
    }

    /**
     * Find hover view on same line based on the focused view
     *
     * @param hoverEvent the hover event({@link GLView#HOVER_LEFT} or {@link GLView#HOVER_RIGHT})
     * @return the found view
     */
    private GLView findHoverFocusedViewOnSameLine(int hoverEvent) {
        GLView view = null;
        switch (mLastOrientation) {
            case GLView.ORIENTATION_0:
                if (hoverEvent == GLView.HOVER_LEFT) {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_LEFT);
                } else {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_RIGHT);
                }
                break;
            case GLView.ORIENTATION_180:
                if (hoverEvent == GLView.HOVER_LEFT) {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_RIGHT);
                } else {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_LEFT);
                }
                break;
            case GLView.ORIENTATION_90:
                if (hoverEvent == GLView.HOVER_LEFT) {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_UP);
                } else {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_DOWN);
                }
                break;
            case GLView.ORIENTATION_270:
                if (hoverEvent == GLView.HOVER_LEFT) {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_DOWN);
                } else {
                    view = mRootView.findViewOnSameLine(mCurrentHoverFocusedView, GLView.FOCUS_UP);
                }
                break;
        }
        return view;
    }

    /**
     * On Hover touch event.
     *
     * @param e MotionEvent
     * @return false, if fail, start to autofocus or scrolling or zoom
     * <p>
     * There are several cases use onHoverTouchEvent when talkback is on.
     * Case 1 : Double tap to execute something or start touch autofocus. (ACTION_DOWN -> ACTION_UP, no ACTION_MOVE)
     * Case 2 : Scrolling or Dragging. (ACTION_DOWN -> ACTION_MOVE -> ACTION_UP), It should return 'false' always.
     * Case 3 : Long press to cancel the hover focus (only ACTION_UP)
     */
    // FIXME : Do not make conditions by each scenario
    public boolean onHoverTouchEvent(MotionEvent e) {
        if (mPaused) {
            return false;
        }

        final int action = e.getActionMasked();

        if (mCurrentHoverFocusedView != null && mCurrentHoverFocusedView.isVisible()) {
            if (mCurrentHoverFocusedView.equals(mRootView.findViewByCoordinate(e.getX(), e.getY()))) {
                onTouchEvent(e);
            } else if (action == MotionEvent.ACTION_UP) {
                onTouchEvent(e);
            }
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionEvent = MotionEvent.obtain(e);
                return false;
            case MotionEvent.ACTION_MOVE:  // ACTION_MOVE is shown only in case of scrolling or zooming using two fingers.
                if (mLastMotionEvent != null && mLastMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onTouchEvent(mLastMotionEvent);
                }
                onTouchEvent(e);
                mLastMotionEvent = MotionEvent.obtain(e);

                boolean isZooming = (mRootView.findViewByCoordinate(e.getX(), e.getY()) == null);
                if (isZooming) {
                    return false;
                } else {
                    return true;
                }
            case MotionEvent.ACTION_UP:
                if (mLastMotionEvent == null) { // long click(only ACTION_UP is shown) - cancel hover focus
                    onTouchEvent(e);
                    return true;
                }

                if (mLastMotionEvent.getAction() == MotionEvent.ACTION_DOWN) { // Double tap (ACTION_DOWN -> ACTION_UP)
                    if (mCurrentHoverFocusedView != null && mCurrentHoverFocusedView.isVisible() && mLastHoverView != null) { // Hover focus exists. Start to execute the focused icon.
                        float currentHoverFocusedViewX = (mCurrentHoverFocusedView.getClipRectArea().left + mCurrentHoverFocusedView.getClipRectArea().right) / 2;
                        float currentHoverFocusedViewY = (mCurrentHoverFocusedView.getClipRectArea().top + mCurrentHoverFocusedView.getClipRectArea().bottom) / 2;

                        mLastMotionEvent.setLocation(currentHoverFocusedViewX, currentHoverFocusedViewY);
                        e.setLocation(currentHoverFocusedViewX, currentHoverFocusedViewY);

                        // Down and Up event is executed to do action for hoverfocusedView after change the event location
                        onTouchEvent(mLastMotionEvent);
                        onTouchEvent(e);
                    } else { //   Hover focus does not exist. start to AF using double tab at the vacant. return false
                        mLastMotionEvent = null;
                        return false;
                    }
                } else { // mLastMotionEvent is ACTION_MOVE. End the scrolling.
                    onTouchEvent(e);
                    return false;
                }

                mLastMotionEvent = null;
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                mLastMotionEvent = MotionEvent.obtain(e);
                return false;
        }

        mLastMotionEvent = MotionEvent.obtain(e);
        return true;
    }

    @Override
    public void onInit(int arg0) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPaused || !isFocusNavigationEnabled()) {
            return false;
        }
        // There is no focused view.
        if (mCurrentFocusedView == null) {
            if (isNavigationKeyCode(keyCode)) {
                if (mRootView.requestFocus()) {
                    mIsFocusIndicatorVisible = true;
                    mFocusIndicatorVisibilityChanged = true;
                }
                return false;
            }
        }

        boolean result = false;
        boolean res = false;

        // Normal case
        if (mCurrentFocusedView != null) {
            // If focus indicator is not visible, show indicator and do nothing. (For navigation keys)
            if (!mIsFocusIndicatorVisible && isNavigationKeyCode(keyCode)) {
                mIsFocusIndicatorVisible = true;
                mFocusIndicatorVisibilityChanged = true;
                setDirty(true);
                return true;
            }
            result = mCurrentFocusedView.keyDownEvent(keyCode, event);
            if (!result) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_SPACE:
                        mTapDir = (mTapDir + 1) % 2;
                        res = true;
                        break;
                    case KeyEvent.KEYCODE_TAB:
                        res = handlingKeyEventTap();
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        return true;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (hasHardwareKeyPad()) {
                            result = mCurrentFocusedView.requestFocus(GLView.FOCUS_LEFT, mCurrentFocusedView);
                            break;
                        }
                        switch (mLastOrientation) {
                            case GLView.ORIENTATION_0:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_LEFT, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_180:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_RIGHT, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_90:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_DOWN, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_270:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_UP, mCurrentFocusedView);
                                break;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (hasHardwareKeyPad()) {
                            result = mCurrentFocusedView.requestFocus(GLView.FOCUS_RIGHT, mCurrentFocusedView);
                            break;
                        }
                        switch (mLastOrientation) {
                            case GLView.ORIENTATION_0:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_RIGHT, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_180:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_LEFT, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_90:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_UP, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_270:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_DOWN, mCurrentFocusedView);
                                break;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (hasHardwareKeyPad()) {
                            result = mCurrentFocusedView.requestFocus(GLView.FOCUS_UP, mCurrentFocusedView);
                            break;
                        }
                        switch (mLastOrientation) {
                            case GLView.ORIENTATION_0:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_UP, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_180:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_DOWN, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_90:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_LEFT, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_270:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_RIGHT, mCurrentFocusedView);
                                break;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (hasHardwareKeyPad()) {
                            result = mCurrentFocusedView.requestFocus(GLView.FOCUS_DOWN, mCurrentFocusedView);
                            break;
                        }
                        switch (mLastOrientation) {
                            case GLView.ORIENTATION_0:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_DOWN, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_180:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_UP, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_90:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_RIGHT, mCurrentFocusedView);
                                break;
                            case GLView.ORIENTATION_270:
                                result = mCurrentFocusedView.requestFocus(GLView.FOCUS_LEFT, mCurrentFocusedView);
                                break;
                        }
                        break;
                }
            }

            if (!mCurrentFocusedView.isVisible() && isNavigationKeyCode(keyCode)) {
                GLView tempView = mRootView.findViewFromLeftMostTop();
                if (tempView != null) {
                    tempView.requestFocus();
                    mCurrentFocusedView = tempView;
                }
            }
        }
        if (result || res) {
            AudioManager audioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_UP);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                    break;
                case KeyEvent.KEYCODE_TAB:
                    if (res) {
                        audioManager.playSoundEffect(getAudioSoundOfTapDirection());
                    }
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    break;
            }
        }
        setDirty(true);
        return result;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mPaused || !isFocusNavigationEnabled()) {
            return false;
        }
        if (hasHardwareKeyPad()) {
            if (mCurrentFocusedView == null) {
                if (isNavigationKeyCode(keyCode)) {
                    if (mRootView.requestFocus()) {
                        mIsFocusIndicatorVisible = true;
                        mFocusIndicatorVisibilityChanged = true;
                    }
                    return false;
                }
            }

            if (mCurrentFocusedView != null) {
                if (!mIsFocusIndicatorVisible && isNavigationKeyCode(keyCode)) {
                    mIsFocusIndicatorVisible = true;
                    mFocusIndicatorVisibilityChanged = true;
                    setDirty(true);
                    return true;
                }
            }
        } else {
            if (mFocusIndicatorVisibilityChanged) {
                mFocusIndicatorVisibilityChanged = false;
                return false;
            }
        }
        boolean result = false;

        if (mCurrentFocusedView != null) {
            result = mCurrentFocusedView.keyUpEvent(keyCode, event);
        }
        if (!result && keyCode == KeyEvent.KEYCODE_D) {
            // For Debugging purpose. You can get dump using BT Keyboard.
            dumpViewHierarchy();
        }

        setDirty(true);
        return result;
    }

    public void onPause() {
        if (mPaused) {
            return;
        }
        waitOnDrawFrame(WAITING_FRAMES_COUNT, WAITING_FRAMES_TIMEOUT);
        mPaused = true;

        mChoreographer.removeFrameCallback(this);
        disableAccessibilityService(mApplicationContext);

        if (!mGLSurfaceView.getPreserveEGLContextOnPause()) {

            synchronized (mDeleteTexturesLock) {
                mTexturesToDelete.clear();
            }
            GLProgramStorage.releaseInstance(mGLProgramStorage);

            if (mRootView != null) {
                mRootView.reset();
            }
        }
        GLPreviewData.releaseInstance();

        if (mMainHandlerThread != null) {
            mMainHandlerThread.quitSafely();
            mMainHandlerThread = null;
        }

        if (mTts != null) {
            // Add the Try catch due to Google Issue.
            try {
                mTts.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
                mTts.stop();
                mTts.shutdown();
                mTts = null;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Service is not Registered");
            }
        }

        mFrameNum = 0;
        mFrameCountForFPS = 0;
        mAccumulatedTime = 0;

        mHoverBaseView.setOnHoverListener(null);

        disableOrientationListener();

        mApplicationContext.getContentResolver().unregisterContentObserver(mTouchExplorationEnabledContentObserver);
        mApplicationContext.getContentResolver().unregisterContentObserver(mEnabledAccessibilityServicesContentObserver);
        mApplicationContext.getContentResolver().unregisterContentObserver(mSettingInteractionControlObserver);
        mApplicationContext.getContentResolver().unregisterContentObserver(mCursorColorObserver);
        mApplicationContext.getContentResolver().unregisterContentObserver(mDisplaySizeObserver);
    }

    public void onResume() {
        updateScreenSize(mScreenWidth, mScreenHeight);
        mMainHandlerThread = new HandlerThread("GLContextHandlerThread");
        mMainHandlerThread.start();
        mMainHandler = new Handler(mMainHandlerThread.getLooper());

        mRenderRequested = false;

        mPaused = false;


        mHoverBaseView.setOnHoverListener(this);


        if (mGLSurfaceView != null) {
            mGLSurfaceView.requestRender();
        }

        updateTouchExplorationEnabled();

        if (mIsTouchExplorationEnabled) {
            enableAccessibilityService(mApplicationContext);
        }

        Uri touchExplorationEnabledURI = Settings.Secure.getUriFor(Settings.Secure.TOUCH_EXPLORATION_ENABLED);
        if (touchExplorationEnabledURI != null) {
            mApplicationContext.getContentResolver().registerContentObserver(touchExplorationEnabledURI, true, mTouchExplorationEnabledContentObserver);
        }



        Uri accessibilityCursorColorURI = Settings.Secure.getUriFor("accessibility_cursor_color");
        if (accessibilityCursorColorURI != null) {
            mApplicationContext.getContentResolver().registerContentObserver(accessibilityCursorColorURI, true, mCursorColorObserver);
        }

        Uri enabledAccessibilityServicesURI = Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledAccessibilityServicesURI != null) {
            mApplicationContext.getContentResolver().registerContentObserver(enabledAccessibilityServicesURI, true, mEnabledAccessibilityServicesContentObserver);
        }


        if (Settings.System.getInt(mApplicationContext.getContentResolver(), SHOW_BUTTON_BACKGROUND_SETTING_KEY, 0) == 1) {
            mShowButtonBackgroundEnabled = true;
        }

        Uri displaySizeForcedURI = Settings.Global.getUriFor("display_size_forced");
        if (displaySizeForcedURI != null) {
            mApplicationContext.getContentResolver().registerContentObserver(displaySizeForcedURI, true, mDisplaySizeObserver);
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        Log.v(TAG, "onSurfaceChanged width=" + width + " height=" + height);

        if (width > height && !mIsMultiWindowMode) {
            Log.w(TAG, "width is larger than height");
            return;
        }
        updateScreenSize(width, height);

        Matrix.orthoM(mProjMatrix, 0, 0, width, height, 0, -width, width);

        GLES20.glViewport(0, 0, width, height);
        mClipRect.set(0, 0, width, height);

        if (mRootView != null) {
            mRootView.setSize(width, height);
            mRootView.refreshClipRect();
        }

        if (mListener != null && mRootView != null)
            mListener.onGLInitialized(mRootView);

        setDirty(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(GL10 gUnused, EGLConfig arg1) {
        Log.v(TAG, "onSurfaceCreated");

        GLES20.glClearColor(0f, 0f, 0f, 0f);
        //GL11.glShadeModel(GL10.GL_SMOOTH);

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        //GLES20.glEnable(GLES20.GL_STENCIL_TEST);
        //GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        //GL11.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        mGLPreviewData = GLPreviewData.getInstance(this);

        mGLProgramStorage = GLProgramStorage.getInstance();
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_BASIC);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_TINT_BASIC);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_LINE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_ROUND_RECT);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_CIRCULAR_CLIP);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_FADE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_CIRCLE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_RECTANGLE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_SCALE_CIRCLE_TEXTURE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_ITEM_COLOR_CHANGE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_RECTANGLE);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION_LAND);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_COLOR);
        mGLProgramStorage.addProgram(GLProgramStorage.TYPE_PROGRAM_GRADIENT_WITH_POSITION_AND_COLOR_CHANGE);

        if (mRootView == null) {
            mRootView = new GLViewGroup(this, 0, 0);
        }
    }

    /**
     * On touch event.
     *
     * @param e the e
     * @return true, if successful
     */
    public boolean onTouchEvent(MotionEvent e) {
        if (mPaused) {
            e.setAction(MotionEvent.ACTION_CANCEL); // to return normal button status when activity is paused
        }

        boolean result = false;
        if (!isTouchExplorationEnabled()) {
            mIsFocusIndicatorVisible = false;
        }

        if (mRootView != null) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchView = mRootView.findViewByCoordinate(e.getX(), e.getY());
                    if (mLastTouchView != null) {
                        if (isTalkBackEnabled() && mIsTouchExplorationEnabled) {
                            if (mCurrentHoverFocusedView != null) {
                                result = mLastTouchView.touchEvent(e);
                            }
                        } else {
                            result = mLastTouchView.touchEvent(e);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mLastTouchView != null && mLastTouchView.isVisible()) {
                        if (isTalkBackEnabled() && mIsTouchExplorationEnabled) {
                            if (mCurrentHoverFocusedView != null) {
                                result = mLastTouchView.touchEvent(e);
                                mLastTouchView = null;
                            }
                        } else {
                            result = mLastTouchView.touchEvent(e);
                            mLastTouchView = null;
                        }
                    }
                    break;
                default:
                    if (mLastTouchView != null && mLastTouchView.isVisible()) {
                        if (isTalkBackEnabled() && mIsTouchExplorationEnabled) {
                            if (mCurrentHoverFocusedView != null) {
                                result = mLastTouchView.touchEvent(e);
                            }
                        } else {
                            result = mLastTouchView.touchEvent(e);
                        }
                    }
                    break;
            }
        }
        setDirty(true);
        return result;
    }

    public void queueGLEvent(Runnable event) {
        mGLSurfaceView.queueEvent(event);
    }

    /**
     * Refresh orientation.
     */
    public void refreshOrientation() {
        if (mRootView != null) {
            mRootView.onOrientationChanged(mLastOrientation);
            setDirty(true);
        }
    }

    public void registerHoverEventChangedObserver(HoverEventChangedObserver o) {
        synchronized (mObserversLock) {
            mObservers.add(o);
        }
    }

    public void rotationFocusView() {
        if (mCurrentFocusedView != null) {
            mCurrentFocusedView.onFocusStatusChanged(GLContext.FOCUSED);
        }
        if (isTouchExplorationEnabled()) {
            if (mCurrentHoverFocusedView != null) {
                mCurrentHoverFocusedView.onHoverStatusChanged(HOVER_ENTER);
            }
        }
    }

    public void setDirty(boolean dirty) {
        synchronized (mFrameLock) {
            if (mPaused) {
                return;
            }
            mDirty = dirty;
            if (mChoreographer != null && mDirty && !mRenderRequested && mGLSurfaceView.getRenderMode() == GLSurfaceView.RENDERMODE_WHEN_DIRTY) {
                mChoreographer.postFrameCallback(this);
                mRenderRequested = true;
            }
        }
    }

    public void setFirstOrientation(int firstOrientation) {
        setLastOrientation(GLUtil.getGLOrientationByDisplayOrientation(firstOrientation));
        if (mRootView != null) {
            mRootView.onOrientationChanged(mLastOrientation);
        }
    }

    public synchronized void setPreviewData(int width, int height, byte[] data) {
        if (mGLPreviewData != null && data != null)
            mGLPreviewData.setPreviewData(width, height, data);
    }

    public void setRenderMode(int renderMode) {
        mGLSurfaceView.setRenderMode(renderMode);
    }

    public void unregisterHoverEventChangedObserver(HoverEventChangedObserver o) {
        synchronized (mObserversLock) {
            mObservers.remove(o);
        }
    }


    /**
     * Gets the screen geometry as a Rect
     * <p>
     * Do not modify the values of this Rect or big issues will appear!
     */
    protected final Rect getScreenGeometry() {
        return mClipRect;
    }

    protected void notifyHoverEventChanged(GLView view, MotionEvent e) {
        synchronized (mObserversLock) {
            for (HoverEventChangedObserver observer : mObservers) {
                observer.onHoverEventChanged(view, e);
            }
        }
    }

    void addTextureToDelete(GLTexture texture) {
        synchronized (mDeleteTexturesLock) {
            mTexturesToDelete.add(texture);
        }
    }

    /**
     * Disable orientation listener.
     */
    private void disableOrientationListener() {
        Log.d(TAG, "disableOrientationListener");
    }

    private int getAudioSoundOfTapDirection() {
        switch (mTapDirState) {
            case 0:
                return AudioManager.FX_FOCUS_NAVIGATION_RIGHT;
            case 1:
                return AudioManager.FX_FOCUS_NAVIGATION_DOWN;
            case 2:
                return AudioManager.FX_FOCUS_NAVIGATION_LEFT;
            case 3:
                return AudioManager.FX_FOCUS_NAVIGATION_UP;
        }
        return AudioManager.FX_KEY_CLICK;
    }

    /**
     * Handle the orientation from SemContext or android
     *
     * @param orientation the orientation
     */
    private void handleOrientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            Log.d(TAG, "handleOrientationChanged - ORIENTATION_UNKNOWN");
            return;
        }

        int newOrientation = GLUtil.getGLOrientationBySystemOrientation(orientation);
        if (newOrientation == GLView.ORIENTATION_180) {
            Log.v(TAG, "handleOrientationChanged - ignore ORIENTATION_180");
            return;
        }
        if (mLastOrientation != newOrientation) {
            setLastOrientation(newOrientation);
            if (mRootView != null) {
                Log.v(TAG, "onOrientationChanged, newOrientation : " + newOrientation);
                mRootView.onOrientationChanged(newOrientation);
                setDirty(true);
            }
        }
    }

    /**
     * Move the focus to the right of the current view.
     * The Y-axis is higher priority than the X-axis.
     */
    private boolean handlingKeyEventTap() {
        boolean result = false;

        GLView tempView = null;

        if (hasHardwareKeyPad()) {
            tempView = mRootView.findViewOnSameLine(mCurrentFocusedView, GLView.FOCUS_RIGHT);
        } else {
            tempView = mRootView.findViewById(mCurrentFocusedView.getNextFocusForwardId());

            if (tempView == null) {
                switch (mLastOrientation) {
                    case GLView.ORIENTATION_0:
                        tempView = mRootView.findViewOnSameLine(mCurrentFocusedView, GLView.FOCUS_RIGHT);
                        break;
                    case GLView.ORIENTATION_180:
                        tempView = mRootView.findViewOnSameLine(mCurrentFocusedView, GLView.FOCUS_LEFT);
                        break;
                    case GLView.ORIENTATION_90:
                        tempView = mRootView.findViewOnSameLine(mCurrentFocusedView, GLView.FOCUS_DOWN);
                        break;
                    case GLView.ORIENTATION_270:
                        tempView = mRootView.findViewOnSameLine(mCurrentFocusedView, GLView.FOCUS_UP);
                        break;
                }
            }
        }

        if (tempView != null) {
            tempView.requestFocus();
            mCurrentFocusedView = tempView;
            result = true;
        } else { // If no more a view of the right side, moves to the next line.
            float centerX = (mCurrentFocusedView.getOriginalClipRect().left + mCurrentFocusedView.getOriginalClipRect().right) / 2f;
            float centerY = (mCurrentFocusedView.getOriginalClipRect().top + mCurrentFocusedView.getOriginalClipRect().bottom) / 2f;

            float left = 0;
            float top = 0;
            float margin = 0.01f; // For do not include mCurrentFocusedView

            switch (mLastOrientation) {
                case GLView.ORIENTATION_0:
                    left = 0;
                    top = centerY + margin;
                    break;
                case GLView.ORIENTATION_180:
                    left = mRootView.getRight();
                    top = centerY - margin;
                    break;
                case GLView.ORIENTATION_90:
                    left = centerX - margin;
                    top = 0;
                    break;
                case GLView.ORIENTATION_270:
                    left = centerX + margin;
                    top = mRootView.getBottom();
                    break;
            }

            tempView = mRootView.findViewFromLeftMostTop(mLastOrientation, left, top);

            if (tempView != null) {
                tempView.requestFocus();
                mCurrentFocusedView = tempView;
                result = true;
            } else { // If the end of the screen, move to left most top in the screen.
                switch (mLastOrientation) {
                    case GLView.ORIENTATION_0:
                        left = 0;
                        top = 0;
                        break;
                    case GLView.ORIENTATION_180:
                        left = mRootView.getRight();
                        top = mRootView.getBottom();
                        break;
                    case GLView.ORIENTATION_90:
                        left = mRootView.getRight();
                        top = 0;
                        break;
                    case GLView.ORIENTATION_270:
                        left = 0;
                        top = mRootView.getBottom();
                        break;
                }
                tempView = mRootView.findViewFromLeftMostTop(mLastOrientation, left, top);
                if (tempView != null) {
                    tempView.requestFocus();
                    mCurrentFocusedView = tempView;
                    result = true;
                }
            }
        }

        return result;
    }

    private boolean isNavigationKeyCode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_TAB:
            case KeyEvent.KEYCODE_SPACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Set Orientation Listener
     */
    @SuppressLint("WrongConstant")
    private void setOrientationListener() {

    }

    /**
     * Consume waiting frames.
     */
    private void signalOnDrawFrame() {
        synchronized (mWaitFrameLock) {
            if (mLatch != null) {
                mLatch.countDown();
            }
        }
    }

    private void startFrameHandlerThread() {
        mFrameHandlerThread = new HandlerThread("GLContextFrameHandlerThread");
        mFrameHandlerThread.start();
        mFrameHandler = new Handler(mFrameHandlerThread.getLooper());
        mFrameHandler.post(new Runnable() {
            @Override
            public void run() {
                mChoreographer = Choreographer.getInstance();
            }
        });
    }

    /**
     * Updates screen size.
     *
     * @param width  the screen width
     * @param height the screen height
     */
    private void updateScreenSize(int width, int height) {
        if (width > height && !mIsMultiWindowMode) {
            Log.w(TAG, "width is larger than height");
            return;
        }
        mScreenWidth = width;
        mScreenHeight = height;

        int id = mApplicationContext.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0 && mApplicationContext.getResources().getBoolean(id)) {
            int resourceId = mApplicationContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            mNavigatorHeight = mApplicationContext.getResources().getDimensionPixelSize(resourceId);
        }

        Log.i(TAG, "updateScreenSize : w=" + mScreenWidth + ", h=" + mScreenHeight + ", navi h=" + mNavigatorHeight);
    }

    private void updateTouchExplorationEnabled() {
        //...
    }

    /**
     * Wait for frames to be consumed in onDrawFrame.
     *
     * @param frames  frame count to wait.
     * @param timeout the maximum time to wait.
     */
    private void waitOnDrawFrame(int frames, int timeout) {
        mLatch = new CountDownLatch(WAITING_FRAMES_COUNT);
        setDirty(true);
        try {
            mLatch.await(WAITING_FRAMES_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException is occurred");
        }
        synchronized (mWaitFrameLock) {
            mLatch = null;
        }
    }

    /**
     * The listener interface for receiving onGLInitialized events.
     */
    public interface GLInitializeListener {

        /**
         * On gl initialized.
         *
         * @param rootView the root view
         */
        void onGLInitialized(GLViewGroup rootView);
    }

    public interface HoverEventChangedObserver {
        void onHoverEventChanged(GLView view, MotionEvent e);
    }
}
