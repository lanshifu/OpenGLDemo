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

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;


import com.lanshifu.opengldemo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Locale;

/**
 * Collection of utility functions used in this package.
 */
public class GLUtil {
    private static final String TAG = "GLUtil";

    private static final int ANIMATION_DURATION = 300;
    private static final double EPSILON = 0.00001f;

    /**
     * Instantiates a new gl util.
     */
    private GLUtil() {
    }

    /**
     * Check not null.
     *
     * @param <T>    the generic type
     * @param object the object
     * @return the t
     */
    public static <T> T checkNotNull(T object) {
        if (object == null)
            throw new NullPointerException();
        return object;
    }

    /**
     * Clamp.
     *
     * @param x   the x
     * @param min the min
     * @param max the max
     * @return the int
     */
    public static int clamp(int x, int min, int max) {
        if (x > max)
            return max;
        if (x < min)
            return min;
        return x;
    }

    /*
     *  When the string "mm:ss" is passed to TTS,
     *  Only Korean TTS engine announces this as "hh:mm".
     *  Other engines announce just as "num , num"
     */
    public static String convertTimeInfoForTTS(Context context, String text) {
        // remove "(", ")" for Hyperlapse mode recording time
        if (text.contains("(") && text.contains(")")) {
            text = text.substring(2, text.length() - 1);
        }

        String convertedText, Hr, Min, Sec;
        int hour, minute, second;

        if (text.matches("[0-5][0-9]:[0-5][0-9]:[0-5][0-9]") || text.matches("[0-5][0-9]:[0-5][0-9]:[0-5][0-9] / [0-5][0-9]:[0-5][0-9]:[0-5][0-9]")) {
            // ":" is bias, get the hour, min and sec string from text
            Hr = text.substring(0, 2);
            Min = text.substring(3, 5);
            Sec = text.substring(6, 8);

            /* String -> Integer,
             * without this, TTS says like "zero zero hour zero zero minute zero five second"
             */
            hour = Integer.parseInt(Hr);
            minute = Integer.parseInt(Min);
            second = Integer.parseInt(Sec);

            String ttsHr = (hour > 1) ? context.getString(R.string.tts_hours) : context.getString(R.string.tts_hour);
            String ttsMin = (minute > 1) ? context.getString(R.string.tts_minutes) : context.getString(R.string.tts_minute);
            String ttsSec = (second > 1) ? context.getString(R.string.tts_seconds) : context.getString(R.string.tts_second);

            // Integer -> String, make TTS string finally.
            convertedText = Integer.toString(hour) + ttsHr + Integer.toString(minute) + ttsMin + Integer.toString(second) + ttsSec;

            if (text.contains("/")) {
                String hrMax, minMax, secMax;
                int hourMax, minuteMax, secondMax;

                hrMax = text.substring(11, 13);
                minMax = text.substring(14, 16);
                secMax = text.substring(17, 19);

                hourMax = Integer.parseInt(hrMax);
                minuteMax = Integer.parseInt(minMax);
                secondMax = Integer.parseInt(secMax);

                ttsHr = (minuteMax > 1) ? context.getString(R.string.tts_hours) : context.getString(R.string.tts_hour);
                ttsMin = (minuteMax > 1) ? context.getString(R.string.tts_minutes) : context.getString(R.string.tts_minute);
                ttsSec = (secondMax > 1) ? context.getString(R.string.tts_seconds) : context.getString(R.string.tts_second);

                // Integer -> String, make TTS string finally.
                convertedText = convertedText + "/" + Integer.toString(hourMax) + ttsHr + Integer.toString(minuteMax) + ttsMin + Integer.toString(secondMax) + ttsSec;
            }
        } else {
            // ":" is bias, get the min and sec string from text
            Min = text.substring(0, 2);
            Sec = text.substring(3, 5);

            /* String -> Integer,
             * without this, TTS says like "zero zero minute zero five second"
             */
            minute = Integer.parseInt(Min);
            second = Integer.parseInt(Sec);

            String ttsMin = (minute > 1) ? context.getString(R.string.tts_minutes) : context.getString(R.string.tts_minute);
            String ttsSec = (second > 1) ? context.getString(R.string.tts_seconds) : context.getString(R.string.tts_second);

            // Integer -> String, make TTS string finally.
            convertedText = Integer.toString(minute) + ttsMin + Integer.toString(second) + ttsSec;

            if (text.contains("/")) {
                String minMax, secMax;
                int minuteMax, secondMax;

                minMax = text.substring(8, 10);
                secMax = text.substring(11, 13);

                minuteMax = Integer.parseInt(minMax);
                secondMax = Integer.parseInt(secMax);

                ttsMin = (minuteMax > 1) ? context.getString(R.string.tts_minutes) : context.getString(R.string.tts_minute);
                ttsSec = (secondMax > 1) ? context.getString(R.string.tts_seconds) : context.getString(R.string.tts_second);

                // Integer -> String, make TTS string finally.
                convertedText = convertedText + "/" + Integer.toString(minuteMax) + ttsMin + Integer.toString(secondMax) + ttsSec;
            }
        }
        return convertedText;
    }

    /**
     * Distance.
     *
     * @param x  the x
     * @param y  the y
     * @param sx the sx
     * @param sy the sy
     * @return the float
     */
    public static float distance(float x, float y, float sx, float sy) {
        float dx = x - sx;
        float dy = y - sy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static boolean doubleEquals(double a, double b) {
        return Double.compare(a, b) == 0;
    }

    /**
     * Equals.
     *
     * @param a the a
     * @param b the b
     * @return true, if successful
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static boolean floatEquals(double a, double b) {
        return (Math.abs(a - b) < EPSILON);
    }

    public static boolean floatEquals(float a, float b) {
        return Float.compare(a, b) == 0;
    }

    /**
     * Gets the alpha off animation.
     *
     * @return the alpha on animation
     */
    public static Animation getAlphaOffAnimation() {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(1F, 0F);
        showAlphaViewAnimation.setDuration(ANIMATION_DURATION);

        return showAlphaViewAnimation;
    }

    /**
     * get AlphaOff Animation
     *
     * @param from value to start the animation
     * @return alpha animation
     */
    public static Animation getAlphaOffAnimation(float from) {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(from, 0F);
        showAlphaViewAnimation.setDuration(ANIMATION_DURATION);

        return showAlphaViewAnimation;
    }

    public static Animation getAlphaOffAnimation(int duration, Interpolator interpolator) {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(1F, 0F);
        showAlphaViewAnimation.setDuration(duration);
        showAlphaViewAnimation.setInterpolator(interpolator);

        return showAlphaViewAnimation;
    }

    /**
     * Gets the alpha on animation.
     *
     * @return the alpha on animation
     */
    public static Animation getAlphaOnAnimation() {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(0F, 1F);
        showAlphaViewAnimation.setDuration(ANIMATION_DURATION);

        return showAlphaViewAnimation;
    }

    public static Animation getAlphaOnAnimation(float to) {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(0F, to);
        showAlphaViewAnimation.setDuration(ANIMATION_DURATION);

        return showAlphaViewAnimation;
    }

    public static Animation getAlphaOnAnimation(int duration, int offset, Interpolator interpolator) {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(0F, 1);
        showAlphaViewAnimation.setDuration(duration);
        showAlphaViewAnimation.setInterpolator(interpolator);
        showAlphaViewAnimation.setStartOffset(offset);

        return showAlphaViewAnimation;
    }

    public static Animation getAlphaOnAnimation(int duration, Interpolator interpolator) {
        AlphaAnimation showAlphaViewAnimation = new AlphaAnimation(0F, 1);
        showAlphaViewAnimation.setDuration(duration);
        showAlphaViewAnimation.setInterpolator(interpolator);

        return showAlphaViewAnimation;
    }

    public static Animation getBlinkAnimation(boolean repeat) {
        AlphaAnimation blinkAnimation = new AlphaAnimation(0F, 1F);
        blinkAnimation.setDuration(ANIMATION_DURATION);
        if (repeat) {
            blinkAnimation.setRepeatMode(Animation.REVERSE);
            blinkAnimation.setRepeatCount(Animation.INFINITE);
        }
        return blinkAnimation;
    }

    /**
     * Gets the byte buffer from byte array.
     *
     * @param array the array
     * @return the byte buffer from byte array
     */
    public static ByteBuffer getByteBufferFromByteArray(byte array[]) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(array.length);
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    /**
     * Gets the float buffer from float array.
     *
     * @param array the array
     * @return the float buffer from float array
     */
    public static FloatBuffer getFloatBufferFromFloatArray(float array[]) {
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(array.length * 4);
        tempBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = tempBuffer.asFloatBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    /**
     * Gets the gL coordinate from screen coordinate.
     *
     * @param glContext    the gl context
     * @param glCoordinate the gl coordinate
     * @param screenX      the screen x
     * @param screenY      the screen y
     */
    public static void getGLCoordinateFromScreenCoordinate(GLContext glContext, float[] glCoordinate, float screenX, float screenY) {
        //glCoordinate[0] = ((float) screenX - ((float) glContext.getScreenWidthPixels() / 2f)) / ((float) glContext.getScreenWidthPixels() / 2f);
        //glCoordinate[1] = -((float) screenY - ((float) glContext.getScreenHeightPixels() / 2f)) / ((float) glContext.getScreenHeightPixels() / 2f);

        //glCoordinate[0] *= glContext.getScreenAspectRatio();
        glCoordinate[0] = screenX;
        glCoordinate[1] = screenY;
    }

    /**
     * Gets the gL distance from screen distance.
     *
     * @param glContext      the gl context
     * @param screenDistance the screen distance
     * @return the gL distance from screen distance
     */
    public static float getGLDistanceFromScreenDistanceX(GLContext glContext, float screenDistance) {
        return screenDistance;
        //return (float) screenDistance / ((float) glContext.getScreenWidthPixels() / 2f) * glContext.getScreenAspectRatio();
    }

    /**
     * Gets the gL distance from screen distance y.
     *
     * @param glContext      the gl context
     * @param screenDistance the screen distance
     * @return the gL distance from screen distance y
     */
    public static float getGLDistanceFromScreenDistanceY(GLContext glContext, float screenDistance) {
        return screenDistance;
        //return (float) screenDistance / ((float) glContext.getScreenHeightPixels() / 2f);
    }

    public static int getGLOrientationByDisplayOrientation(int displayOrientation) {
        switch (displayOrientation) {
            case Surface.ROTATION_0:
                return GLView.ORIENTATION_0;
            case Surface.ROTATION_90:
                return GLView.ORIENTATION_270;
            case Surface.ROTATION_180:
                return GLView.ORIENTATION_180;
            case Surface.ROTATION_270:
                return GLView.ORIENTATION_90;
            default:
                return GLView.ORIENTATION_0;
        }
    }

    /**
     * Gets the gL orientation by system orientation.
     *
     * @param orientation the orientation
     * @return the gL orientation by system orientation
     */
    public static int getGLOrientationBySystemOrientation(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
            return GLView.ORIENTATION_0;

        int degree = GLUtil.roundOrientation((orientation + GLContext.getOrientationCompensationValue()) % 360);

        switch (degree) {
            case 90:
                return GLView.ORIENTATION_90;
            case 180:
                return GLView.ORIENTATION_180;
            case 270:
                return GLView.ORIENTATION_270;
            case 0:
            default:
                return GLView.ORIENTATION_0;
        }
    }

    /**
     * Gets the screen coordinate from gl coordinate.
     *
     * @param glContext        the gl context
     * @param screenCoordinate the screen coordinate
     * @param glX              the gl x
     * @param glY              the gl y
     */
    public static void getScreenCoordinateFromGLCoordinate(GLContext glContext, float[] screenCoordinate, float glX, float glY) {
        //screenCoordinate[0] = ((glX / glContext.getScreenAspectRatio()) * ((float) glContext.getScreenWidthPixels() / 2f)) + ((float) glContext.getScreenWidthPixels() / 2f);
        //screenCoordinate[1] = -(glY * ((float) glContext.getScreenHeightPixels() / 2f)) + ((float) glContext.getScreenHeightPixels() / 2f);
        screenCoordinate[0] = glX;
        screenCoordinate[1] = glY;
    }

    /**
     * Gets the screen distance from gl distance.
     *
     * @param glContext  the gl context
     * @param glDistance the gl distance
     * @return the screen distance from gl distance
     */
    public static float getScreenDistanceFromGLDistanceX(GLContext glContext, float glDistance) {
        //return (glDistance / glContext.getScreenAspectRatio()) * ((float) glContext.getScreenWidthPixels() / 2f);
        return glDistance;
    }

    /**
     * Gets the screen distance from gl distance y.
     *
     * @param glContext  the gl context
     * @param glDistance the gl distance
     * @return the screen distance from gl distance y
     */
    public static float getScreenDistanceFromGLDistanceY(GLContext glContext, float glDistance) {
        //return glDistance * ((float) glContext.getScreenHeightPixels() / 2f);
        return glDistance;
    }

    /**
     * Index of.
     *
     * @param <T>   the generic type
     * @param array the array
     * @param s     the s
     * @return the int
     */
    public static <T> int indexOf(T[] array, T s) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method for getting the Locale-specified direction is right-to-left (RTL).
     *
     * @return status value.
     */
    public static boolean isLocaleRTL() {
        return (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL);
    }

    /**
     * Checks if is power of2.
     *
     * @param n the n
     * @return true, if is power of2
     */
    public static boolean isPowerOf2(int n) {
        return (n & -n) == n;
    }

    /*
     * This routine checks TTS string is "Recorded Time" or not.
     */
    public static boolean isTimeInfo(String text) {
        // remove "(", ")" for Hyperlapse mode recording time
        if (text.contains("(") && text.contains(")")) {
            text = text.substring(2, text.length() - 1);
        }

        return text.matches("[0-5][0-9]:[0-5][0-9]") || text.matches("[0-5][0-9]:[0-5][0-9] / [0-5][0-9]:[0-5][0-9]") || text.matches("[0-5][0-9]:[0-5][0-9]:[0-5][0-9]")
                || text.matches("[0-5][0-9]:[0-5][0-9]:[0-5][0-9] / [0-5][0-9]:[0-5][0-9]:[0-5][0-9]");
    }

    /**
     * Multiplying matrixes in Java to avoid JNI calls overhead
     *
     * @param result matrix with result of multiplication
     * @param lhs    left hand side matrix
     * @param rhs    right hand side matrix
     */
    public static void multiplyMM(float[] result, float[] lhs, float[] rhs) {
        if (result == null || lhs == null || rhs == null) {
            return;
        }

        result[0] = lhs[0] * rhs[0] + lhs[4] * rhs[1] + lhs[8] * rhs[2] + lhs[12] * rhs[3];
        result[1] = lhs[1] * rhs[0] + lhs[5] * rhs[1] + lhs[9] * rhs[2] + lhs[13] * rhs[3];
        result[2] = lhs[2] * rhs[0] + lhs[6] * rhs[1] + lhs[10] * rhs[2] + lhs[14] * rhs[3];
        result[3] = lhs[3] * rhs[0] + lhs[7] * rhs[1] + lhs[11] * rhs[2] + lhs[15] * rhs[3];

        result[4] = lhs[0] * rhs[4] + lhs[4] * rhs[5] + lhs[8] * rhs[6] + lhs[12] * rhs[7];
        result[5] = lhs[1] * rhs[4] + lhs[5] * rhs[5] + lhs[9] * rhs[6] + lhs[13] * rhs[7];
        result[6] = lhs[2] * rhs[4] + lhs[6] * rhs[5] + lhs[10] * rhs[6] + lhs[14] * rhs[7];
        result[7] = lhs[3] * rhs[4] + lhs[7] * rhs[5] + lhs[11] * rhs[6] + lhs[15] * rhs[7];

        result[8] = lhs[0] * rhs[8] + lhs[4] * rhs[9] + lhs[8] * rhs[10] + lhs[12] * rhs[11];
        result[9] = lhs[1] * rhs[8] + lhs[5] * rhs[9] + lhs[9] * rhs[10] + lhs[13] * rhs[11];
        result[10] = lhs[2] * rhs[8] + lhs[6] * rhs[9] + lhs[10] * rhs[10] + lhs[14] * rhs[11];
        result[11] = lhs[3] * rhs[8] + lhs[7] * rhs[9] + lhs[11] * rhs[10] + lhs[15] * rhs[11];

        result[12] = lhs[0] * rhs[12] + lhs[4] * rhs[13] + lhs[8] * rhs[14] + lhs[12] * rhs[15];
        result[13] = lhs[1] * rhs[12] + lhs[5] * rhs[13] + lhs[9] * rhs[14] + lhs[13] * rhs[15];
        result[14] = lhs[2] * rhs[12] + lhs[6] * rhs[13] + lhs[10] * rhs[14] + lhs[14] * rhs[15];
        result[15] = lhs[3] * rhs[12] + lhs[7] * rhs[13] + lhs[11] * rhs[14] + lhs[15] * rhs[15];
    }

    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.

    /**
     * Next power of2.
     *
     * @param n the n
     * @return the int
     */
    public static int nextPowerOf2(int n) {
        n -= 1;
        n |= n >>> 16;
        n |= n >>> 8;
        n |= n >>> 4;
        n |= n >>> 2;
        n |= n >>> 1;
        return n + 1;
    }

    /**
     * Rotate.
     *
     * @param b       the b
     * @param degrees the degrees
     * @return the bitmap
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b = null;
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    public static PointF rotatePoint(float x, float y, int degree, float pivotX, float pivotY) {
        Matrix transform = new Matrix();

        transform.setRotate(degree, pivotX, pivotY);

        float[] point = new float[2];
        point[0] = x;
        point[1] = y;
        transform.mapPoints(point);

        return new PointF(point[0], point[1]);
    }

    public static int roundOrientation(int orientation) {
        // Default rule.
        // -1, -45(315) <= orientation < 45, return value = 0
        // 45 <= orientation < 135, return value = 90
        // 135 <= orientation < 225, return value = 180
        // 225 <= orientation < 315, return value = 270
        // Conditional rule.
        //+- MARGIN to prevent frequent orientation changes.
        switch (GLContext.getLastOrientation()) {
            case GLView.ORIENTATION_0:
                if ((315 - GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE) <= orientation || orientation < (45 + GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE)) {
                    return 0;
                }
                break;
            case GLView.ORIENTATION_90:
                if ((45 - GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE) <= orientation && orientation < (135 + GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE)) {
                    return 90;
                }
                break;
            case GLView.ORIENTATION_180:
                if ((135 - GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE) <= orientation && orientation < (225 + GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE)) {
                    return 180;
                }
                break;
            case GLView.ORIENTATION_270:
                if ((225 - GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE) <= orientation && orientation < (315 + GLContext.ORIENTATION_CHANGE_MARGIN_IN_DEGREE)) {
                    return 270;
                }
                break;
        }
        return ((orientation + 45) / 90 * 90) % 360;
    }

    /**
     * To gl matrix.
     *
     * @param v the v
     * @return the float[]
     */
    public static float[] toGLMatrix(float v[]) {
        v[15] = v[8];
        v[13] = v[5]; // Translate Y
        v[5] = v[4]; // Scale Y
        v[4] = v[1]; // Skew X
        v[12] = v[2]; // Translate X
        v[1] = v[3]; // Skew Y
        v[3] = v[6];
        v[2] = v[6] = v[8] = v[9] = 0;
        v[10] = 1; // Scale Z
        return v;
    }

    public static void transformEventByGLOrientation(MotionEvent event, int glOrientation, int screenWidth, int screenHeight) {
        switch (glOrientation) {
            case GLView.ORIENTATION_90:
                event.setLocation(screenWidth - event.getY(), event.getX());
                break;
            case GLView.ORIENTATION_180:
                event.setLocation(screenWidth - event.getX(), screenHeight - event.getY());
                break;
            case GLView.ORIENTATION_270:
                event.setLocation(event.getY(), screenHeight - event.getX());
                break;
            default:
                break;
        }
    }

    public static void transformEventByScreenOrientation(MotionEvent event, int screenOrientation, int screenWidth, int screenHeight, boolean parentRotatable) {
        if (parentRotatable) {
            switch (screenOrientation) {
                case GLView.ORIENTATION_90:
                    event.setLocation(screenHeight - event.getY(), event.getX());
                    break;
                case GLView.ORIENTATION_180:
                    event.setLocation(screenWidth - event.getX(), screenHeight - event.getY());
                    break;
                case GLView.ORIENTATION_270:
                    event.setLocation(event.getY(), screenWidth - event.getX());
                    break;
                default:
                    break;
            }
        } else if (GLContext.isScreenOrientationLandscape()) {
            switch (screenOrientation) {
                case Surface.ROTATION_90:
                    event.setLocation(screenWidth - event.getY(), event.getX());
                    break;
                case Surface.ROTATION_180:
                    event.setLocation(screenWidth - event.getX(), screenHeight - event.getY());
                    break;
                case Surface.ROTATION_270:
                    event.setLocation(event.getY(), screenHeight - event.getX());
                    break;
                default:
                    break;
            }
        } else {
            switch (screenOrientation) {
                case Surface.ROTATION_0:
                    event.setLocation(event.getY(), screenHeight - event.getX());
                    break;
                case Surface.ROTATION_180:
                    event.setLocation(screenWidth - event.getY(), event.getX());
                    break;
                case Surface.ROTATION_270:
                    event.setLocation(screenWidth - event.getX(), screenHeight - event.getY());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Checks setting value for requested userid.
     *
     * @param cr         the ContentResolver
     * @param name       string of requested setting
     * @param def        default value
     * @param userHandle requested userid
     * @return setting value
     */
    public static int getIntForUser(ContentResolver cr, String name, int def, int userHandle) {
        try {
            Method method = android.provider.Settings.Secure.class.getMethod(
                    "getIntForUser", ContentResolver.class, String.class, int.class, int.class);
            try {
                return (int) method.invoke(null, cr, name, def, userHandle);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, "getIntForUser : " + e.getMessage());
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getIntForUser : " + e.getMessage());
        }
        return -1;
    }

    /**
     * Checks setting value for requested userid.
     *
     * @param cr         the ContentResolver
     * @param name       string of requested setting
     * @param userHandle requested userid
     * @return setting value
     */
    public static String getStringForUser(ContentResolver cr, String name, int userHandle) {
        try {
            Method method = android.provider.Settings.Secure.class.getMethod(
                    "getStringForUser", ContentResolver.class, String.class, int.class);
            try {
                return (String) method.invoke(null, cr, name, userHandle);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, "getStringForUser : " + e.getMessage());
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getStringForUser : " + e.getMessage());
        }
        return null;
    }
}
