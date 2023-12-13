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
import android.os.IBinder;
import android.util.Log;
import android.view.InputDevice;

public class KeyboardUtilsService extends Service {

    private static final String TAG = "XiaomiPartsKeyboardUtilsService";
    private static final boolean DEBUG = true;

    private static InputManager mInputManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(TAG, "Creating service");
        if (mInputManager == null) {
            mInputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        }
        setKeyboardEnabled(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand");
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

    public static void setKeyboardEnabled(boolean enabled) {
        if (DEBUG) Log.d(TAG, "setKeyboardEnabled: " + enabled);
        for (int id : mInputManager.getInputDeviceIds()) {
            if (isDeviceXiaomiKeyboard(id)) {
                if (DEBUG) Log.d(TAG, "setKeyboardEnabled: Found Xiaomi Keyboard with id: " + id);
                if (enabled) {
                    if (DEBUG) Log.d(TAG, "setKeyboardEnabled: Enabling Xiaomi Keyboard");
                    mInputManager.enableInputDevice(id);
                } else {
                    if (DEBUG) Log.d(TAG, "setKeyboardEnabled: Disabling Xiaomi Keyboard");
                    mInputManager.disableInputDevice(id);
                }
            }
        }
    }

    private static boolean isDeviceXiaomiKeyboard(int id) {
        InputDevice inputDevice = mInputManager.getInputDevice(id);
        return inputDevice.getVendorId() == 5593 && inputDevice.getProductId() == 163;
    }

}
