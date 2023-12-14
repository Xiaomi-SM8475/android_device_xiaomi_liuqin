/*
 * Copyright (C) 2023 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaomi.settings.peripheral;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.util.Log;

import android.preference.PreferenceManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import com.android.settingslib.widget.MainSwitchPreference;

import com.xiaomi.settings.R;

public class StylusSettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "XiaomiPeripheralManagerPenUtils";
    private static final String STYLUS_KEY = "stylus_switch_key";

    private SharedPreferences mStylusPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.stylus_settings);

        mStylusPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        SwitchPreference switchPreference = (SwitchPreference) findPreference(STYLUS_KEY);

        switchPreference.setChecked(mStylusPreference.getBoolean(STYLUS_KEY, false));
        switchPreference.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mStylusPreference.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mStylusPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreference, String key) {
        if (STYLUS_KEY.equals(key)) {
            // kjgkjhgj
        }
    }
}
