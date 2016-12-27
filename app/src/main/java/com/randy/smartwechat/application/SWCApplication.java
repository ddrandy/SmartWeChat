package com.randy.smartwechat.application;

import android.app.Application;

import com.orhanobut.logger.Logger;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2016/12/28
 * Time: 0:27
 * Description: TODO
 */

public class SWCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // init Logger
        Logger.init().hideThreadInfo().setMethodCount(0);
    }
}
