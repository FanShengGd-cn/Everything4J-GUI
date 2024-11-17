package org.helixcs.everything4j;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import javax.swing.*;

/**
 * author fansheng
 * date 2024-11-18 01:48
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new DecoratedFrame();
        frame.setLocationRelativeTo(null);
        JIntellitype jIntellitype = JIntellitype.getInstance();
        jIntellitype.registerHotKey(1, JIntellitypeConstants.MOD_ALT,32);
        jIntellitype.addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int i) {
                frame.setVisible(true);
            }
        });
    }
}
