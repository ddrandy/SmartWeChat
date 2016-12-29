package com.randy.smartwechat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.randy.smartwechat.utils.Constants.TAG;

import android.support.annotation.NonNull;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2016/12/29
 * Time: 12:52
 * Description: get default Shared Preference
 */

public class SPUtil {

    private static SharedPreferences dSP;

    private SPUtil() {
    }

    public static void init(@NonNull Context context) {
        if (dSP == null) {
            synchronized (SPUtil.class) {
                if (dSP == null) {
                    dSP = context.getSharedPreferences(TAG, 0);
                }
            }
        }
    }

    public static String getString(String key, String defValue) {
        return dSP.getString(key, defValue);
    }

    private static SharedPreferences.Editor getEditor() {
        return dSP.edit();
    }

    public static void putString(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    public static void clear() {
        getEditor().clear().apply();
    }
}
