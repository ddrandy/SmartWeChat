package com.randy.smartwechat.service;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.randy.smartwechat.R;
import com.randy.smartwechat.utils.SPUtil;
import com.randy.smartwechat.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.randy.smartwechat.utils.Constants.DETAIL_UI;
import static com.randy.smartwechat.utils.Constants.LAUNCHER_UI;
import static com.randy.smartwechat.utils.Constants.LIST_VIEW;
import static com.randy.smartwechat.utils.Constants.PACKAGE_ID;
import static com.randy.smartwechat.utils.Constants.RECEIVE_UI;
import static com.randy.smartwechat.utils.Constants.TAG;
import static com.randy.smartwechat.utils.Constants.TEXT_VIEW;


/**
 * Author: randy(dddrandy@gmail.com)
 * Date: 2016/12/27
 * Time: 22:40
 * Description: accessibility service
 */

public class SmartAccessibilityService extends AccessibilityService {
    private boolean flag1 = true;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;

    private Map<AccessibilityNodeInfo, Boolean> mMap;
    private String NOTIFY_MARK;
    private String RECEIVE_OPEN;
    private String RECEIVE_QUIT;
    private String DETAIL_QUIT;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: ");
        init();
    }

    private void init() {
        mMap = new HashMap<>();
        StringUtil.init(this);

        // load String
        NOTIFY_MARK = StringUtil.getNotifyMark();
        RECEIVE_OPEN = PACKAGE_ID + StringUtil.getReceiveOpen();
        RECEIVE_QUIT = PACKAGE_ID + StringUtil.getReceiveQuit();
        DETAIL_QUIT = PACKAGE_ID + StringUtil.getDetailQuit();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventClassName = event.getClassName().toString();
        Log.d(TAG, "ClassName: " + eventClassName + "\neventType:" + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                if (LIST_VIEW.equals(eventClassName)
                        || TEXT_VIEW.equals(eventClassName)) {
                    AccessibilityNodeInfo info = getRootInActiveWindow();
                    flag3 = flag4 = false;
                    getChildText(info);
                    if (flag1) {
                        checkPacket();
                    }
                    flag1 = false;

                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (RECEIVE_UI.equals(eventClassName)) {
                    // 打开红包
                    Log.d(TAG, "onAccessibilityEvent: 打开红包");
                    click(RECEIVE_OPEN);
                } else if (DETAIL_UI.equals(eventClassName)) {
                    // 退出详情
                    Log.d(TAG, "onAccessibilityEvent: 退出详情");
                    click(DETAIL_QUIT);
                } else if (LAUNCHER_UI.equals(eventClassName)) {
                    // 微信主页

                }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (texts == null) {
                    return;
                }
                for (CharSequence text : texts) {
                    String string = text.toString();
                    if (string.contains(NOTIFY_MARK)) {
                        Parcelable parcelableData = event.getParcelableData();
                        if (parcelableData != null && parcelableData instanceof Notification) {
                            PendingIntent pendingIntent = ((Notification) parcelableData).contentIntent;
                            try {
                                wakeScreen();
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.d(TAG, "onAccessibilityEvent: View Scrolled");
                if (!flag2) {
                    mMap.clear();
                }
                flag1 = true;
                break;
        }
    }

    private void wakeScreen() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (!powerManager.isScreenOn()) {
            PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
            wake.acquire();
        }
        ((KeyguardManager) getSystemService(KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE).disableKeyguard();
    }

    private void checkPacket() {
        flag2 = false;
        for (Map.Entry<AccessibilityNodeInfo, Boolean> entry : mMap.entrySet()) {
            if (entry.getValue()) {
                performClick(entry.getKey());
                entry.setValue(false);
                flag2 = true;
            }
        }
    }

    private void getChildText(AccessibilityNodeInfo info) {
        if (info == null || !flag1) {
            Log.d(TAG, "getChildText: info = null or checked");
            return;
        }
        int childCount = info.getChildCount();
        CharSequence charSequence = info.getText();
        String text = null;
        if (charSequence != null) {
            text = charSequence.toString();
        }
        if (childCount == 0 && text != null) {
            if (getString(R.string.get_lucky_money).equals(text)) {
                flag3 = true;
            } else if (getString(R.string.lucky_money).equals(text)) {
                flag4 = true;
            } else {
                flag3 = flag4 = false;
            }
            if (flag3 && flag4) {
//                performClick(info);
                while (info != null) {
                    if (info.isClickable()) {
                        mMap.put(info, !mMap.containsKey(info));
                        break;
                    }
                    info = info.getParent();
                }
                Log.d(TAG, "抢红包===========" + mMap.get(info));
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                getChildText(info.getChild(i));
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
        if (infos.size() == 0 && !RECEIVE_QUIT.equals(viewId)) {
            Log.d(TAG, "checkPacket: 红包过期或未找到打开红包按钮id");
            click(RECEIVE_QUIT);
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
}
