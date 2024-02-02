/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.settings.touch;

import android.os.IHwBinder.DeathRecipient;
import android.util.Log;

import vendor.xiaomi.hw.touchfeature.V1_0.ITouchFeature;

public class TfWrapper {

    private static final String TAG = "XiaomiPartsTouchFeatureWrapper";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    private static ITouchFeature mTouchFeature;

    private static DeathRecipient mDeathRecipient = (cookie) -> {
        if (DEBUG) Log.d(TAG, "serviceDied");
        mTouchFeature = null;
    };

    public static ITouchFeature getTouchFeature() {
        if (mTouchFeature == null) {
            if (DEBUG) Log.d(TAG, "getTouchFeature: mTouchFeature=null");
            try {
                mTouchFeature = ITouchFeature.getService();
                mTouchFeature.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (Exception e) {
                Log.e(TAG, "getTouchFeature failed!", e);
            }
        }
        return mTouchFeature;
    }

    public static void setTouchFeature(TfParams params) {
        final ITouchFeature touchfeature = getTouchFeature();
        if (touchfeature == null) {
            Log.e(TAG, "setTouchFeatureParams: touchfeature is null!");
            return;
        }
        if (DEBUG) Log.d(TAG, "setTouchFeatureParams: " + params);
        try {
            touchfeature.setModeValue(0, params.mode, params.value);
        } catch (Exception e) {
            Log.e(TAG, "setTouchFeatureParams failed!", e);
        }
    }

    public static class TfParams {
        /* touchfeature parameters */
        final int mode, value;

        public TfParams(int mode, int value) {
            this.mode = mode;
            this.value = value;
        }

        public String toString() {
            return "TouchFeatureParams(" + mode + ", " + value + ")";
        }
    }
}
