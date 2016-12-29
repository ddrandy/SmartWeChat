package com.randy.smartwechat.utils;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2016/12/29
 * Time: 13:38
 * Description: TODO
 */

public class StringUtil {

    private StringUtil() {
    }

    public static void init(@NonNull Context context) {
        SPUtil.init(context);
    }

    public static String getNotifyMark() {
        return SPUtil.getString("notification_mark", "[微信红包]");
    }

    public static String getReceiveOpen() {
        return SPUtil.getString("receive_open", "bdh");
    }

    public static String getReceiveQuit() {
        return SPUtil.getString("receive_quit", "bdl");
    }

    public static String getDetailQuit() {
        return SPUtil.getString("detail_quit", "gq");
    }

    public static void putNotifyMark(String value) {
        SPUtil.putString("notification_mark", value);
    }

    public static void putReceiveOpen(String value) {
        SPUtil.putString("receive_open", value);
    }

    public static void putReceiveQuit(String value) {
        SPUtil.putString("receive_quit", value);
    }

    public static void putDetailQuit(String value) {
        SPUtil.putString("detail_quit", value);
    }

    public static void clear() {
        SPUtil.clear();
    }
}
