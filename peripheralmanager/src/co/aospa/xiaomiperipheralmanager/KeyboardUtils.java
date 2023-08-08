/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.xiaomiperipheralmanager;

import android.content.Context;
import android.hardware.input.InputManager;
import android.util.Log;
import android.view.InputDevice;

public class KeyboardUtils {

    private static final String TAG = "XiaomiPeripheralManagerKeyboardUtils";
    private static final boolean DEBUG = true;

    private static InputManager mInputManager;

    public static void setup(Context context) {
        if (mInputManager == null) {
            mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        }
        setKeyboardEnabled(false);
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