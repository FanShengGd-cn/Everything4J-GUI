package org.helixcs.everything4j;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;
import org.helixcs.everything4j.Everything4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.util.List;

/**
 * author fansheng
 * date 2024-11-18 01:20
 */
public class DecoratedFrame extends JFrame {
    public DecoratedFrame() {
        JTextField jTextField = new JTextField("此处键入搜索内容",1);
        jTextField.setFont(new Font("SansSerif",Font.PLAIN,35));
        Document dt = jTextField.getDocument();
        dt.addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate");
                Everything4j instance = Everything4j.getInstance();
                List<String> strings = null;
                try {
                    strings = instance.searchResult(e.getDocument().getText(0,e.getDocument().getLength()));
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println(strings);
            }
        });
        this.getContentPane().add(jTextField);
        this.setUndecorated(true); // 去掉窗口的装饰
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);  //采用指定的窗口装饰风格
        this.setSize(800, 50);
    }

    public static void main(String[] args) {
        Everything4j instance = Everything4j.getInstance();
        JFrame frame = new DecoratedFrame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

//        JIntellitype jIntellitype = JIntellitype.getInstance();
//        jIntellitype.registerHotKey(1, JIntellitypeConstants.MOD_ALT,32);
//        jIntellitype.addHotKeyListener(new HotkeyListener() {
//            @Override
//            public void onHotKey(int i) {
//                frame.setVisible(true);
//            }
//        });


    }
}
