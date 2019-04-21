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

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * This class demonstrates how an accessibility service can query
 * window content to improve the feedback given to the user.
 */
public class AccessibilityGestureHandler extends AccessibilityService implements OnInitListener {

    public static final String ACTION_ACCESSIBILITY_GESTURE_DETECTED = "com.samsung.android.glview.ACCESSIBILITY_GESTURE_DETECTED";
    public static final String ACTION_ACCESSIBILITY_VIEW_FOCUS_GONE = "com.samsung.android.glview.ACTION_ACCESSIBILITY_VIEW_FOCUS_GONE";
    public static final String SYSTEM_PACKAGE_NAME = "com.android.systemui";
    public static final String KEY_GESTURE_ID = "gestureId";
    public static final String KEY_ACCESSIBILITY_EVENT_ID = "accessibilityeventid";
    public static final String KEY_ACCESSIBILITY_NODE_INFO_ID = "accessibilitynodeinfoid";
    public static final int GESTURE_SWIPE_LEFT = AccessibilityService.GESTURE_SWIPE_LEFT;
    public static final int GESTURE_SWIPE_RIGHT = AccessibilityService.GESTURE_SWIPE_RIGHT;
    public static final int GESTURE_SWIPE_UP = AccessibilityService.GESTURE_SWIPE_UP;
    public static final int GESTURE_SWIPE_DOWN = AccessibilityService.GESTURE_SWIPE_DOWN;
    private static final String TAG = "AccessibilityHandler";

    /**
     * Processes an AccessibilityEvent, by traversing the View's tree and
     * putting together a message to speak to the user.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName().equals(SYSTEM_PACKAGE_NAME) && (event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_ACCESSIBILITY_NODE_INFO_ID, event.getSource());
            Intent intent = new Intent(ACTION_ACCESSIBILITY_VIEW_FOCUS_GONE);
            intent.putExtra(KEY_ACCESSIBILITY_EVENT_ID, bundle);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInit(int status) {
        Log.v(TAG, "onInit");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
        /* do nothing */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        setServiceInfo(info);

        Intent intent = new Intent(ACTION_ACCESSIBILITY_VIEW_FOCUS_GONE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * When Talkback and Accessibility service for camera is on, get the gestureId of each swipe direction.
     * And send the intent to Camera Activity by broadcast.
     */
    @Override
    protected boolean onGesture(int gestureId) {
        Log.v(TAG, "onGesture, gestureId : " + gestureId);

        Intent intent = new Intent(ACTION_ACCESSIBILITY_GESTURE_DETECTED);
        switch (gestureId) {
            case AccessibilityService.GESTURE_SWIPE_LEFT:
            case AccessibilityService.GESTURE_SWIPE_LEFT_AND_UP:
            case AccessibilityService.GESTURE_SWIPE_LEFT_AND_DOWN:
                intent.putExtra(KEY_GESTURE_ID, GESTURE_SWIPE_LEFT);
                break;
            case AccessibilityService.GESTURE_SWIPE_RIGHT:
            case AccessibilityService.GESTURE_SWIPE_RIGHT_AND_UP:
            case AccessibilityService.GESTURE_SWIPE_RIGHT_AND_DOWN:
                intent.putExtra(KEY_GESTURE_ID, GESTURE_SWIPE_RIGHT);
                break;
            case AccessibilityService.GESTURE_SWIPE_UP:
            case AccessibilityService.GESTURE_SWIPE_UP_AND_LEFT:
            case AccessibilityService.GESTURE_SWIPE_UP_AND_RIGHT:
                intent.putExtra(KEY_GESTURE_ID, GESTURE_SWIPE_UP);
                break;
            case AccessibilityService.GESTURE_SWIPE_DOWN:
            case AccessibilityService.GESTURE_SWIPE_DOWN_AND_RIGHT:
            case AccessibilityService.GESTURE_SWIPE_DOWN_AND_LEFT:
                intent.putExtra(KEY_GESTURE_ID, GESTURE_SWIPE_DOWN);
                break;
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        return false;
    }
}
