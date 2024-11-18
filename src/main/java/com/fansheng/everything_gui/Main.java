package com.fansheng.everything_gui;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitypeConstants;

/**
 * author fansheng
 * date 2024-11-18 01:48
 */
public class Main {
    public static void main(String[] args) {
        DecoratedFrame frame = new DecoratedFrame();
        frame.setLocationRelativeTo(null);
        HotkeyListener hotkeyListener = i -> {
            switch (i) {
                case 1: {
                    frame.setVisible(true);
                    break;
                }
            }
        };

        frame.getjIntellitype().registerHotKey(1, JIntellitypeConstants.MOD_ALT, 32);
        frame.getjIntellitype().addHotKeyListener(hotkeyListener);
        frame.setHotkeyListener(hotkeyListener);
    }
}
