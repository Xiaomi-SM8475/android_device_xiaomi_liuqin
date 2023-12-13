/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.settings.peripheral;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.os.IBinder;
import android.util.Log;
import android.view.InputDevice;

import com.xiaomi.settings.touch.TfWrapper;

public class PenUtilsService extends Service {

    private static final String TAG = "XiaomiPartsPenUtilsService";
    private static final boolean DEBUG = true;

    private static boolean mIsPenModeEnabled;

    private static InputManager mInputManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(TAG, "Creating service");
        mInputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        mInputManager.registerInputDeviceListener(mInputDeviceListener, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand");
        refreshPenMode();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updatePenMode() {
        Log.d(TAG, "refreshPenMode: " + mIsPenModeEnabled);
        TfWrapper.setTouchFeature(new TfWrapper.TfParams(20, mIsPenModeEnabled ? 18 : 0));
    }

    private void refreshPenMode() {
        for (int id : mInputManager.getInputDeviceIds()) {
            if (isDeviceXiaomiPen(id)) {
                if (DEBUG) Log.d(TAG, "refreshPenMode: Found Xiaomi Pen");
                if (!mIsPenModeEnabled) {
                    mIsPenModeEnabled = true;
                    updatePenMode();
                }
                return;
            }
        }
        if (DEBUG) Log.d(TAG, "refreshPenMode: No Xiaomi Pen found");
        if (mIsPenModeEnabled) {
            mIsPenModeEnabled = false;
            updatePenMode();
        }
    }

    private boolean isDeviceXiaomiPen(int id) {
        InputDevice inputDevice = mInputManager.getInputDevice(id);
        return inputDevice.getVendorId() == 6421 && inputDevice.getProductId() == 19841;
    }

    private InputDeviceListener mInputDeviceListener = new InputDeviceListener() {
            @Override
            public void onInputDeviceAdded(int id) {
                refreshPenMode();
            }
            @Override
            public void onInputDeviceRemoved(int id) {
                refreshPenMode();
            }
            @Override
            public void onInputDeviceChanged(int id) {
                refreshPenMode();
            }
        };

}
