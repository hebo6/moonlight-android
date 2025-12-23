package com.limelight;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import java.util.Arrays;
import java.util.List;

public class KeyboardAccessibilityService extends AccessibilityService {

    private final static List<Integer> BLACKLISTED_KEYS = Arrays.asList(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_POWER);

    public static KeyboardAccessibilityService instance;

    public static void setFilterState(boolean enabled) {
        if (instance != null) {
            AccessibilityServiceInfo info = instance.getServiceInfo();
            if (info == null) {
                return;
            }
            if (enabled) {
                info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
            } else {
                info.flags &= ~AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
            }
            instance.setServiceInfo(info);
        }
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (Game.instance != null && Game.instance.isConnected() && !BLACKLISTED_KEYS.contains(keyCode)) {
            // Preventing default will disable shortcut actions like alt+tab and etc.
            if (action == KeyEvent.ACTION_DOWN) {
                Game.instance.handleKeyDown(event);
                return true;
            } else if (action == KeyEvent.ACTION_UP) {
                Game.instance.handleKeyUp(event);
                return true;
            }
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onServiceConnected() {
        instance = this;
        LimeLog.info("Keyboard service is connected");

        // Ensure we start with filtering disabled to avoid interfering with other apps
        setFilterState(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (instance == this) {
            instance = null;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }
}