/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

import com.xiaomi.settings.peripheral.KeyboardUtilsService;
import com.xiaomi.settings.peripheral.PenUtilsService;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "XiaomiParts";
    private static final boolean DEBUG = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(TAG, "Received intent: " + intent.getAction());

        switch (intent.getAction()) {
            case Intent.ACTION_LOCKED_BOOT_COMPLETED:
                onLockedBootCompleted(context);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                onBootCompleted(context);
                break;
        }
    }

    private static void onLockedBootCompleted(Context context) {
        // Peripherals
        context.startServiceAsUser(new Intent(context, KeyboardUtilsService.class),
                UserHandle.CURRENT);
        context.startServiceAsUser(new Intent(context, PenUtilsService.class),
                UserHandle.CURRENT);
    }

    private static void onBootCompleted(Context context) {
    }
}
