package com.randy.smartwechat.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.randy.smartwechat.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.randy.smartwechat.utils.Constants.TAG;


/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2016/12/27
 * Time: 22:40
 * Description: accessibility service
 */

public class SmartAccessibilityService extends AccessibilityService {
    private boolean flag1;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private Map<AccessibilityNodeInfo, Boolean> mMap;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: ");
        mMap = new HashMap<>();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventClassName = event.getClassName().toString();
        Log.d(TAG, "ClassName: " + eventClassName + "\neventType:" + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                if ("android.widget.ListView".equals(eventClassName)
                        || "android.widget.TextView".equals(eventClassName)) {
                    AccessibilityNodeInfo info = getRootInActiveWindow();
                    flag1 = flag2 = flag3 = flag4 = false;
                    getChildText(info);
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(eventClassName)) {
                    // 打开红包
                    Log.d(TAG, "onAccessibilityEvent: 打开红包");
                    click("com.tencent.mm:id/bdh");
                } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(eventClassName)) {
                    // 退出详情
                    Log.d(TAG, "onAccessibilityEvent: 退出详情");
                    click("com.tencent.mm:id/gq");
                }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.d(TAG, "onAccessibilityEvent: View Scrolled");

                flag1 = flag2 = false;
                break;
        }
    }

    private void getChildText(AccessibilityNodeInfo info) {
        if (info == null) {
            Log.d(TAG, "getChildText: info = null");
            return;
        }
        int childCount = info.getChildCount();
        CharSequence charSequence = info.getText();
        String text = null;
        if (charSequence != null) {
            text = charSequence.toString();
        }
//        Log.d(TAG, "getChildText: childCount == " + childCount);
        if (childCount == 0 && text != null) {
//            Log.d(TAG, "getChildText: " + text);
            /*if (getString(R.string.new_friends).equals(text)) {
                flag1 = true;
            } else if (getString(R.string.saved_groups).equals(text)) {
                flag2 = true;
            } else */
            if (getString(R.string.get_lucky_money).equals(text)) {
                flag3 = true;
            } else if (getString(R.string.lucky_money).equals(text)) {
                flag4 = true;
            } else {
                flag3 = flag4 = false;
            }
            if (flag3 && flag4) {
                Log.d(TAG, "抢红包===========" + info.isClickable());
                performClick(info);
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                getChildText(info.getChild(i));
                if (flag1 && flag2) {
                    break;
                }
            }
        }

    }

    private void performClick(AccessibilityNodeInfo info) {
//        if (info.isClickable()) {
//            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//        }
        AccessibilityNodeInfo parent = info;
        while (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            parent = parent.getParent();
        }
    }

    private void click(String viewId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
        if (infos.size() == 0 && !"com.tencent.mm:id/bdl".equals(viewId)) {
            Log.d(TAG, "click: 红包过期或未找到打开红包按钮id");
            click("com.tencent.mm:id/bdl");
        }
        for (AccessibilityNodeInfo info : infos) {
            performClick(info);
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: on Start Command");
        return super.onStartCommand(intent, flags, startId);
    }
}
