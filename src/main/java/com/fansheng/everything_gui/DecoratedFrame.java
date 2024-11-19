package com.fansheng.everything_gui;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * author fansheng
 * date 2024-11-18 01:20
 */

public class DecoratedFrame extends JFrame {
    private Statement statement;

    public static final Desktop desktop = Desktop.getDesktop();

    public static final String SPLIT_SPACE = "     ";

    private ProcessBuilder pb = new ProcessBuilder();

    private JList<Object> resultList;

    private JTextField jTextField;

    private DefaultListModel<Object> listModel = new DefaultListModel<>();

    private Container container = this.getContentPane();

    private boolean isFirst = true;

    private HotkeyListener hotkeyListener;

    private final JIntellitype jIntellitype = JIntellitype.getInstance();

    private final Everything4j instance = Everything4j.getInstance();

    private volatile CompletableFuture<Void> runningFuture = null;

    private volatile String waitingTask = null;

    private volatile String currentTask = null;

    public void setHotkeyListener(HotkeyListener hotkeyListener) {
        this.hotkeyListener = hotkeyListener;
    }

    public JIntellitype getjIntellitype() {
        return jIntellitype;
    }

    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println(e.getKeyCode());
            Component focusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            switch (e.getKeyCode()) {
                case 40: {
                    // 下箭头
                    if (focusedComponent == jTextField) {
                        resultList.setSelectedIndex(0);
                        SwingUtilities.invokeLater(resultList::requestFocusInWindow);
                    }
                    break;
                }
                case 38: {
                    // 上箭头
                    if (focusedComponent == resultList && resultList.getSelectedIndex() == 0) {
                        SwingUtilities.invokeLater(jTextField::requestFocusInWindow);
                    }
                    break;
                }
                case 27: {
                    // esc 关闭窗口
                    JFrame ancestor = (JFrame) SwingUtilities.getWindowAncestor(e.getComponent());
                    ancestor.dispose();
                }
                case 8: {
                    // 退格键
                    if (focusedComponent == resultList) {
                        SwingUtilities.invokeLater(jTextField::requestFocusInWindow);
                    }
                    break;
                }
                case 10: {
                    // 回车键 控制台启动选择项
//                    System.out.println(listModel.get(resultList.getSelectedIndex()));
                    try {
                        String path = ((String) listModel.get(resultList.getSelectedIndex())).split(SPLIT_SPACE)[1];
                        CompletableFuture.runAsync(() -> {
                            try {
                                ResultSet result = statement.executeQuery("select count(*) from files where path = '" + path + "'");
                                if (result.next() && result.getInt(1) == 0) {
                                    // 数据库中不存在该数据
                                    statement.executeUpdate("insert into files values( '" + path + "',1)");
                                } else {
                                    statement.executeUpdate("update files set times = times + 1 where path = '" + path + "'");
                                }
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        desktop.open(new File(path));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        }
    };

    private void addInResultList(List<String> result) {
        if (!result.isEmpty()) {
            listModel.removeAllElements();
            result.forEach(x -> {
                String[] split = x.split("\\\\");
                String content;
                if (split.length > 1) {
                    content = split[split.length - 1];
                } else {
                    content = x;
                }
                listModel.addElement(content + SPLIT_SPACE + x);
            });

        }
    }

    /**
     * 数据库初始化
     */
    private void initDataSource() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection con = DriverManager.getConnection("jdbc:derby:fileData;create=true");
        statement = con.createStatement();
        ResultSet execute = statement.executeQuery("SELECT COUNT(*) FROM SYS.SYSTABLES T JOIN SYS.SYSSCHEMAS S ON T.SCHEMAID = S.SCHEMAID WHERE T.TABLENAME = 'FILES'");
        if (execute.next() && execute.getInt(1) == 0) {
            // 需要建表
            statement.execute("create table FILES( path varchar(1024), times int)");
        }
    }

    private List<String> searchInDatabase(String str) {
        ResultSet resultSet = null;
        List<String> list = new ArrayList<>();
        try {
            resultSet = statement.executeQuery("select * from FILES where path like '%"+str+"%' order by times asc");
            while (resultSet.next()) {
                list.add(resultSet.getString("path"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection con = DriverManager.getConnection("jdbc:derby:fileData;create=true");
        Statement statement = con.createStatement();
        ResultSet execute = statement.executeQuery("SELECT COUNT(*) FROM SYS.SYSTABLES T JOIN SYS.SYSSCHEMAS S ON T.SCHEMAID = S.SCHEMAID WHERE T.TABLENAME = 'FILES'");
        if (execute.next() && execute.getInt(1) == 0) {
            // 需要建表
            statement.execute("create table FILES( path varchar(1024), times int)");
        } else {
            statement.execute("drop table files");
            statement.execute("create table FILES( path varchar(1024), times int)");
//            ResultSet resultSet = statement.executeQuery("select * from files where path like '%xxxxx%' order by times asc");
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString("path"));
//            }
        }
    }

    public List<String> getSearchResult(String str) {
        List<String> result = instance.searchResult(str);
        List<String> strings = searchInDatabase(str);
        for (String string : strings) {
            if (result.contains(string)) {
                result.remove(string);
                result.add(0, string);
            }
        }
        return result;
    }


    public DecoratedFrame() {
        try {
            initDataSource();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        jTextField = new JTextField("", 1);
        jTextField.setFont(new Font("黑体", Font.PLAIN, 30));
        jTextField.addKeyListener(keyAdapter);
        Document dt = jTextField.getDocument();
        dt.addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            public synchronized void changedUpdate(DocumentEvent e) {
                if (e.getDocument().getLength() == 0) {
                    return;
                }
                if (runningFuture != null) {
                    try {
                        if (e.getDocument().getText(0, e.getDocument().getLength()).equals(currentTask)) {
                            return;
                        }
                        waitingTask = e.getDocument().getText(0, e.getDocument().getLength());
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                    return;
                }
                try {
                    currentTask = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                runningFuture = CompletableFuture.runAsync(() -> {
                    List<String> result = getSearchResult(currentTask);
                    System.out.println(currentTask);
                    addInResultList(result);
                    while (waitingTask != null) {
                        if (waitingTask.equals(currentTask)) {
                            waitingTask = null;
                            return;
                        }
                        currentTask = waitingTask;
                        System.out.println(currentTask);
                        result = getSearchResult(currentTask);
                        currentTask = null;
                        waitingTask = null;
                        addInResultList(result);
                    }
                    runningFuture = null;
                });
            }
        });
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                jIntellitype.removeHotKeyListener(hotkeyListener);
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                jIntellitype.addHotKeyListener(hotkeyListener);
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
                if (isFirst) {
                    isFirst = false;
                    try {
                        jTextField.getDocument().remove(0, jTextField.getDocument().getLength());
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        this.setLayout(new BorderLayout());
        jTextField.setPreferredSize(new Dimension(800, 50));
        container.add(jTextField, BorderLayout.NORTH);
        resultList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(resultList);
        resultList.setFixedCellHeight(30); // 每个搜索项高度
        resultList.setFixedCellWidth(600); // 每个搜索项宽度（可选）
        resultList.setFont(new Font("黑体", Font.PLAIN, 20));
        resultList.addKeyListener(keyAdapter);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setUndecorated(true); // 去掉窗口的装饰
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);  //采用指定的窗口装饰风格
        this.setSize(800, 300);


    }

}
