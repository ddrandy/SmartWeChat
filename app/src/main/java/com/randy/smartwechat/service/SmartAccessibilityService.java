package com.randy.smartwechat.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.orhanobut.logger.Logger;

/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2016/12/27
 * Time: 22:40
 * Description: accessibility service
 */

public class SmartAccessibilityService extends AccessibilityService {
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Logger.d("eventType:" + eventType);
    }

    @Override
    public void onInterrupt() {

    }
}
