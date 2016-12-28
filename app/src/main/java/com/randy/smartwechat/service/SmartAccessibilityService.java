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
    private boolean flag1 = true;
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
                    flag3 = flag4 = false;
                    getChildText(info);
                    if (flag1) {
                        checkPacket();
                    }
                    flag1 = false;

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
                List<CharSequence> texts = event.getText();
                if (texts == null) {
                    return;
                }
                for (CharSequence text : texts) {
                    String string = text.toString();
                    if (string.contains("[微信红包]")) {
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
                mMap.clear();
                flag1 = flag2 = true;
                break;
        }
    }

    private void wakeScreen() {
        PowerManager manager = (PowerManager) getSystemService(POWER_SERVICE);
        if (!manager.isScreenOn()) {
            PowerManager.WakeLock wakeLock = manager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "ACQUIRE_CAUSES_WAKEUP");
            wakeLock.acquire();
        }
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        km.newKeyguardLock("keyguard lock").disableKeyguard();
    }

    private void checkPacket() {
        for (Map.Entry<AccessibilityNodeInfo, Boolean> entry : mMap.entrySet()) {
            if (entry.getValue()) {
                performClick(entry.getKey());
                entry.setValue(false);
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
                Log.d(TAG, "抢红包===========" + info.isClickable());
//                performClick(info);
                while (info != null) {
                    if (info.isClickable()) {
                        mMap.put(info, !mMap.containsKey(info));
                        return;
                    }
                    info = info.getParent();
                }
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
        if (infos.size() == 0 && !"com.tencent.mm:id/bdl".equals(viewId)) {
            Log.d(TAG, "checkPacket: 红包过期或未找到打开红包按钮id");
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
