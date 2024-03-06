/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.settings.peripheral;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;

import androidx.preference.PreferenceManager;

import com.xiaomi.settings.touch.TfWrapper;

public class PenUtilsService extends Service {

    private static final String TAG = "XiaomiPartsPenUtilsService";
    private static final boolean DEBUG = true;

    private static final String STYLUS_KEY = "stylus_switch_key";

    private static final String KEY_PEAK_REFRESH_RATE = "peak_refresh_rate";
    private static final String KEY_MIN_REFRESH_RATE = "min_refresh_rate";

    private static final String SURFACE_FLINGER_SERVICE_KEY = "SurfaceFlinger";
    private static final String SURFACE_COMPOSER_INTERFACE_KEY = "android.ui.ISurfaceComposer";
    private static final int SURFACE_FLINGER_DISABLE_OVERLAYS_CODE = 1008;

    private static boolean mIsPenModeEnabled;
    private static boolean mIsPenModeForced;

    private static float mDefaultMinRate;
    private static float mDefaultPeakRate;

    private static InputManager mInputManager;
    private static SharedPreferences mSharedPrefs;
    private static IBinder mSurfaceFlinger;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(TAG, "Creating service");
        mInputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        mInputManager.registerInputDeviceListener(mInputDeviceListener, null);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(mSharedPrefsListener);
        mIsPenModeForced = mSharedPrefs.getBoolean(STYLUS_KEY, false);
        mSurfaceFlinger = ServiceManager.getService(SURFACE_FLINGER_SERVICE_KEY);
        mDefaultMinRate = Settings.System.getFloat(getContentResolver(), KEY_MIN_REFRESH_RATE, 30f);
        mDefaultPeakRate = Settings.System.getFloat(getContentResolver(), KEY_PEAK_REFRESH_RATE, 144f);
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
        Settings.System.putFloat(getContentResolver(), KEY_MIN_REFRESH_RATE, mDefaultMinRate);
        Settings.System.putFloat(getContentResolver(), KEY_PEAK_REFRESH_RATE, mDefaultPeakRate);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updatePenMode() {
        Log.d(TAG, "refreshPenMode: " + mIsPenModeEnabled);
        TfWrapper.setTouchFeature(new TfWrapper.TfParams(20, mIsPenModeEnabled ? 18 : 2));
    }

    private void updateHardwareOverlaysSetting() {
        if (mSurfaceFlinger == null) return;
        try {
            final Parcel data = Parcel.obtain();
            data.writeInterfaceToken(SURFACE_COMPOSER_INTERFACE_KEY);
            data.writeInt(mIsPenModeEnabled ? 1 : 0);
            mSurfaceFlinger.transact(SURFACE_FLINGER_DISABLE_OVERLAYS_CODE, data, null, 0);
            data.recycle();
        } catch (RemoteException ex) { }
    }

    private void updateRefreshRateSetting() {
        if (mIsPenModeEnabled) {
            mDefaultMinRate = Settings.System.getFloat(getContentResolver(), KEY_MIN_REFRESH_RATE, 30f);
            mDefaultPeakRate = Settings.System.getFloat(getContentResolver(), KEY_PEAK_REFRESH_RATE, 144f);
            Settings.System.putFloat(getContentResolver(), KEY_MIN_REFRESH_RATE, 120f);
            Settings.System.putFloat(getContentResolver(), KEY_PEAK_REFRESH_RATE, 120f);
        } else {
            Settings.System.putFloat(getContentResolver(), KEY_MIN_REFRESH_RATE, mDefaultMinRate);
            Settings.System.putFloat(getContentResolver(), KEY_PEAK_REFRESH_RATE, mDefaultPeakRate);
        }

    }

    private void refreshPenMode() {
        if (mIsPenModeForced) {
            if (DEBUG) Log.d(TAG, "refreshPenMode: Pen Mode forced");
            if (!mIsPenModeEnabled) {
                mIsPenModeEnabled = true;
                updatePenMode();
                updateHardwareOverlaysSetting();
                updateRefreshRateSetting();
            }
            return;
        }
        for (int id : mInputManager.getInputDeviceIds()) {
            if (isDeviceXiaomiPen(id)) {
                if (DEBUG) Log.d(TAG, "refreshPenMode: Found Xiaomi Pen");
                if (!mIsPenModeEnabled) {
                    mIsPenModeEnabled = true;
                    updatePenMode();
                    updateHardwareOverlaysSetting();
                    updateRefreshRateSetting();
                }
                return;
            }
        }
        if (DEBUG) Log.d(TAG, "refreshPenMode: No Xiaomi Pen found");
        if (mIsPenModeEnabled) {
            mIsPenModeEnabled = false;
            updatePenMode();
            updateHardwareOverlaysSetting();
            updateRefreshRateSetting();
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
    
    private OnSharedPreferenceChangeListener mSharedPrefsListener = new OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(STYLUS_KEY)) {
                mIsPenModeForced = prefs.getBoolean(STYLUS_KEY, false);
                refreshPenMode();
            }
        }
    };

}
