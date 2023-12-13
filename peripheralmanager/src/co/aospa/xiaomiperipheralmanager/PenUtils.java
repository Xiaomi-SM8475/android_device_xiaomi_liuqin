/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.xiaomiperipheralmanager;

import android.content.Context;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.util.Log;
import android.view.InputDevice;

public class PenUtils {

    private static final String TAG = "XiaomiPeripheralManagerPenUtils";
    private static final boolean DEBUG = true;

    private static InputManager mInputManager;

    public static void setup(Context context) {
        mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        refreshPenMode();
        mInputManager.registerInputDeviceListener(mInputDeviceListener, null);
    }

    private static void enablePenMode() {
        Log.d(TAG, "enablePenMode: Enable Pen Mode");
        TfWrapper.setTouchFeature(new TfWrapper.TfParams(20, 18));
    }

    private static void disablePenMode() {
        Log.d(TAG, "disablePenMode: Disable Pen Mode");
        TfWrapper.setTouchFeature(new TfWrapper.TfParams(20, 0));
    }

    private static void refreshPenMode() {
        for (int id : mInputManager.getInputDeviceIds()) {
            if (isDeviceXiaomiPen(id)) {
                if (DEBUG) Log.d(TAG, "refreshPenMode: Found Xiaomi Pen");
                enablePenMode();
                return;
            }
        }
        if (DEBUG) Log.d(TAG, "refreshPenMode: No Xiaomi Pen found");
        disablePenMode();
    }

    private static boolean isDeviceXiaomiPen(int id) {
        InputDevice inputDevice = mInputManager.getInputDevice(id);
        return inputDevice.getVendorId() == 6421 && inputDevice.getProductId() == 19841;
    }

    private static InputDeviceListener mInputDeviceListener = new InputDeviceListener() {
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