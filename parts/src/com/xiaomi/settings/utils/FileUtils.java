/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xiaomi.settings.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class FileUtils {

    private static final String TAG = "XiaomiPartsFileUtils";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    public static String readLine(String fileName) {
        String line = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName), 512);
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "No such file " + fileName + " for reading", e);
        } catch (IOException e) {
            Log.e(TAG, "Could not read from file " + fileName, e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) { }
        }
        return line;
    }

    public static int readLineInt(String fileName) {
        try {
            return Integer.parseInt(readLine(fileName).replace("0x", ""));
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "Could not convert string to int from file " + fileName, e);
        }
        return 0;
    }
}
