package com.app.mvpdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * 轻量级存储工具类
 *
 * @author weiwen
 * @created 2022/4/28 6:05 下午
 */

public class SharedPreferencesUtils {

    private static final String DEFAULT_FILENAME = "microsingle";

    public static boolean putInt(Context context, String key, int value) {
        return putInt(context, DEFAULT_FILENAME, key, value);
    }

    public static boolean putString(Context context, String key, String value) {
        return putString(context, DEFAULT_FILENAME, key, value);
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        return putBoolean(context, DEFAULT_FILENAME, key, value);
    }

    public static boolean putFloat(Context context, String key, float value) {
        return putFloat(context, DEFAULT_FILENAME, key, value);
    }

    public static boolean putLong(Context context, String key, long value) {
        return putLong(context, DEFAULT_FILENAME, key, value);
    }

    public static boolean putStringSet(Context context, String key, Set value) {
        return putStringSet(context, DEFAULT_FILENAME, key, value);
    }

    public static boolean putStringMap(Context context, Map<String, Object> value) {
        return putStringMap(context, DEFAULT_FILENAME, value);
    }

    public static boolean putInt(Context context, String fileName, String key, int value) {
        return putInt(context, fileName, key, value, Context.MODE_PRIVATE);
    }

    public static boolean putString(Context context, String fileName, String key, String value) {
        return putString(context, fileName, key, value, Context.MODE_PRIVATE);
    }

    public static boolean putBoolean(Context context, String fileName, String key, boolean value) {
        return putBoolean(context, fileName, key, value, Context.MODE_PRIVATE);
    }

    public static boolean putFloat(Context context, String fileName, String key, float value) {
        return putFloat(context, fileName, key, value, Context.MODE_PRIVATE);
    }

    public static boolean putLong(Context context, String fileName, String key, long value) {
        return putLong(context, fileName, key, value, Context.MODE_PRIVATE);
    }

    public static boolean putStringSet(Context context, String fileName, String key, Set value) {
        return putStringSet(context, fileName, key, value, Context.MODE_PRIVATE);
    }

    public static boolean putStringMap(Context context, String fileName, Map<String, Object> value) {
        return putStringMap(context, fileName, value, Context.MODE_PRIVATE);
    }

    public static boolean putInt(Context context, String fileName, String key, int value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        editor.putInt(key, value);
        return editor.commit();
    }

    public static boolean putString(Context context, String fileName, String key, String value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean putBoolean(Context context, String fileName, String key, boolean value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean putFloat(Context context, String fileName, String key, float value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static boolean putLong(Context context, String fileName, String key, long value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        editor.putLong(key, value);
        return editor.commit();
    }

    public static boolean putStringSet(Context context, String fileName, String key, Set<String> value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        editor.putStringSet(key, value);
        return editor.commit();
    }

    public static boolean putStringMap(Context context, String fileName, Map<String, Object> value, int mode) {
        SharedPreferences.Editor editor = getEditor(context, fileName, mode);
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            editor.putString(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return editor.commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getInt(context, DEFAULT_FILENAME, key, defaultValue);
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getString(context, DEFAULT_FILENAME, key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return getLong(context, DEFAULT_FILENAME, key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getBoolean(context, DEFAULT_FILENAME, key, defaultValue);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        return getFloat(context, DEFAULT_FILENAME, key, defaultValue);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue) {
        return getStringSet(context, DEFAULT_FILENAME, key, defaultValue);
    }

    public static int getInt(Context context, String fileName, String key, int defaultValue) {
        return getInt(context, fileName, key, defaultValue, Context.MODE_PRIVATE);
    }

    public static String getString(Context context, String fileName, String key, String defaultValue) {
        return getString(context, fileName, key, defaultValue, Context.MODE_PRIVATE);
    }

    public static long getLong(Context context, String fileName, String key, long defaultValue) {
        return getLong(context, fileName, key, defaultValue, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue) {
        return getBoolean(context, fileName, key, defaultValue, Context.MODE_PRIVATE);
    }

    public static float getFloat(Context context, String fileName, String key, float defaultValue) {
        return getFloat(context, fileName, key, defaultValue, Context.MODE_PRIVATE);
    }

    public static Set<String> getStringSet(Context context, String fileName, String key, Set<String> defaultValue) {
        return getStringSet(context, fileName, key, defaultValue, Context.MODE_PRIVATE);
    }

    public static int getInt(Context context, String fileName, String key, int defaultValue, int mode) {
        return getSharedPreferences(context, fileName, mode).getInt(key, defaultValue);
    }

    public static String getString(Context context, String fileName, String key, String defaultValue, int mode) {
        return getSharedPreferences(context, fileName, mode).getString(key, defaultValue);
    }

    public static long getLong(Context context, String fileName, String key, long defaultValue, int mode) {
        return getSharedPreferences(context, fileName, mode).getLong(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue, int mode) {
        return getSharedPreferences(context, fileName, mode).getBoolean(key, defaultValue);
    }

    public static float getFloat(Context context, String fileName, String key, float defaultValue, int mode) {
        return getSharedPreferences(context, fileName, mode).getFloat(key, defaultValue);
    }

    public static Set<String> getStringSet(Context context, String fileName, String key, Set<String> defaultValue,
            int mode) {
        return getSharedPreferences(context, fileName, mode).getStringSet(key, defaultValue);
    }

    private static SharedPreferences.Editor getEditor(Context context, String fileName, int mode) {
        SharedPreferences.Editor editor = getSharedPreferences(context, fileName, mode).edit();
        return editor;
    }

    private static SharedPreferences getSharedPreferences(Context context, String fileName, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, mode);
        return sharedPreferences;
    }
}
