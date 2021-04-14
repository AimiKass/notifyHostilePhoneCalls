/*
 * Copyright (C) 2017 Anton Kaliturin <kaliturin@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.notifyhostilephonecalls.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.SQLite.DBSettingsHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Settings name/value persistence container
 */

public class Settings
{
    public static final String BLOCK_CALLS_FROM_BLACK_LIST = "BLOCK_CALLS_FROM_BLACK_LIST";
    public static final String BLOCK_ALL_CALLS = "BLOCK_ALL_CALLS";
    public static final String BLOCKED_CALL_STATUS_NOTIFICATION = "BLOCKED_CALL_STATUS_NOTIFICATION";
    public static final String HOSTILE_CALL_STATUS_NOTIFICATION = "HOSTILE_CALL_STATUS_NOTIFICATION";
    public static final String BLOCKED_CALL_SOUND_NOTIFICATION = "BLOCKED_CALL_SOUND_NOTIFICATION";
//    public static final String BLOCKED_CALL_VIBRATION_NOTIFICATION = "BLOCKED_CALL_VIBRATION_NOTIFICATION";
//    public static final String BLOCKED_CALL_RINGTONE = "BLOCKED_CALL_RINGTONE";
    public static final String UI_THEME_DARK = "UI_THEME_DARK";
//    public static final String GO_TO_JOURNAL_AT_START = "GO_TO_JOURNAL_AT_START";
//    public static final String REMOVE_FROM_CALL_LOG = "REMOVE_FROM_CALL_LOG";
    public static final String AUTO_BLOCK_HOSTILE_CALLS = "AUTO_BLOCK_HOSTILE_CALLS";

    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";

    private static Map<String, String> settingsMap = new ConcurrentHashMap<>();


    public static boolean setStringValue(Context context, @NonNull String name, @NonNull String value)
    {

        DBSettingsHandler dbSettingsHandler = DBSettingsHandler.getInstance(context);
//        DatabaseAccessHelper db = DatabaseAccessHelper.getInstance(context);
        if (dbSettingsHandler != null && dbSettingsHandler.setSettingsValue(name, value))
        {
            settingsMap.put(name, value);
            return true;
        }
        return false;
    }

    @Nullable
    public static String getStringValue(Context context, @NonNull String name)
    {
        String value = settingsMap.get(name);
        if (value == null)
        {
            DBSettingsHandler dbSettingsHandler = DBSettingsHandler.getInstance(context);
            //DatabaseAccessHelper db = DatabaseAccessHelper.getInstance(context);
            if (dbSettingsHandler != null)
            {
                value = dbSettingsHandler.getSettingsValue(name);
                if (value != null)
                {
                    settingsMap.put(name, value);
                }
            }
        }
        return value;
    }

    public static boolean setBooleanValue(Context context, @NonNull String name, boolean value)
    {
        String v = (value ? TRUE : FALSE);
        return setStringValue(context, name, v);
    }

    public static boolean getBooleanValue(Context context, @NonNull String name)
    {
        String value = getStringValue(context, name);
        return (value != null && value.equals(TRUE));
    }



    public static void initDefaults(Context context)
    {
        Map<String, String> map = new HashMap<>();
        map.put(BLOCK_CALLS_FROM_BLACK_LIST, TRUE);
        map.put(BLOCK_ALL_CALLS, FALSE);
        map.put(BLOCKED_CALL_STATUS_NOTIFICATION, TRUE);
        map.put(BLOCKED_CALL_SOUND_NOTIFICATION, FALSE);
        map.put(HOSTILE_CALL_STATUS_NOTIFICATION,TRUE);
//        map.put(BLOCKED_CALL_VIBRATION_NOTIFICATION, FALSE);
        map.put(UI_THEME_DARK, FALSE);
//        map.put(GO_TO_JOURNAL_AT_START, FALSE);
//        map.put(REMOVE_FROM_CALL_LOG, FALSE);
        map.put(AUTO_BLOCK_HOSTILE_CALLS,FALSE);

        if (!Permissions.isGranted(context, Permissions.WRITE_EXTERNAL_STORAGE))
        {
            settingsMap = new ConcurrentHashMap<>(map);
        } else
        {
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                String name = entry.getKey();
                if (getStringValue(context, name) == null)
                {
                    String value = entry.getValue();
                    setStringValue(context, name, value);
                }
            }
        }
    }

    // Applies the current UI theme depending on settings
    public static void applyCurrentTheme(Activity activity)
    {
        if (getBooleanValue(activity, com.example.notifyhostilephonecalls.utils.Settings.UI_THEME_DARK))
        {
            activity.setTheme(R.style.AppTheme_Dark);
        } else
        {
            activity.setTheme(R.style.AppTheme_Light);
        }
    }
}
