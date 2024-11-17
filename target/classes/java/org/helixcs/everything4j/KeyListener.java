package org.helixcs.everything4j;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

/**
 * author fansheng
 * date 2024-11-17 16:50
 */
public class KeyListener {
    public static void main(String[] args) {
        JIntellitype jIntellitype = JIntellitype.getInstance();
        jIntellitype.registerHotKey(1, JIntellitypeConstants.MOD_ALT,32);
        jIntellitype.addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int i) {
                System.out.println("按下快捷键："+i);

            }
        });
    }
}
