package com.lineage.managerUI;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.util.StringTokenizer;

/**
 * 類名稱：ServerLogWindow<br>
 * 類描述：服務器日誌信息窗口<br>
 * 創建人:Manly<br>
 * 修改時間：2022年4月27日 下午9:35:46<br>
 * 修改人:QQ:263075225<br>
 * 修改備註:<br>
 *
 * @version<br>
 */
@SuppressWarnings("serial")
public class ServerLogWindow extends JInternalFrame {
    private JTextPane textPane = null;
    private JScrollPane pScroll = null;

    /**
     * 窗口組件設定
     *
     * @param windowName 窗口名稱
     * @param x          界面位置x坐標
     * @param y          界面位置y坐標
     * @param width      寬度
     * @param height     高度
     * @param resizable  true：顯示按鈕 false：不顯示或者隱藏按鈕
     */
    public ServerLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
        super();
        initialize(windowName, x, y, width, height, resizable, closable);
    }

    public void initialize(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
        this.title = windowName;
        this.closable = closable;
        this.isMaximum = false;
        this.maximizable = true;
        this.resizable = resizable;
        this.iconable = true;
        this.isIcon = false;
        setSize(width, height);
        setBounds(x, y, width, height);
        setVisible(true);
        frameIcon = new ImageIcon("");
        setRootPaneCheckingEnabled(true);
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                try {
                    File f = null;
                    String sTemp = "";
                    synchronized (Eva.lock) {
                        sTemp = Eva.getDate();
                        StringTokenizer s = new StringTokenizer(sTemp, " ");
                        Eva.date = s.nextToken();
                        Eva.time = s.nextToken();
                        f = new File("ServerLog/" + Eva.date);
                        if (!f.exists()) {
                            f.mkdir();
                        }
                        Eva.flush(textPane, "[" + Eva.time + "] " + title + " Window", Eva.date);
                        sTemp = null;
                        Eva.date = null;
                        Eva.time = null;
                    }
                    textPane.setText("");
                } catch (Exception ex) {
                    // TODO: handle exception
                }
            }
        });
        updateUI();
        textPane = new JTextPane();
        pScroll = new JScrollPane(textPane);
        textPane.setEditable(false);
        pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pScroll.setAutoscrolls(true);
        add(pScroll);
        // GroupLayout layout = new GroupLayout(getContentPane());
        // getContentPane().setLayout(layout);
        //
        // GroupLayout.SequentialGroup main_horizontal_grp =
        // layout.createSequentialGroup();
        //
        // GroupLayout.SequentialGroup horizontal_grp =
        // layout.createSequentialGroup();
        // GroupLayout.SequentialGroup vertical_grp =
        // layout.createSequentialGroup();
        //
        // GroupLayout.ParallelGroup main =
        // layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // GroupLayout.ParallelGroup col1 =
        // layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // GroupLayout.ParallelGroup col2 =
        // layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // GroupLayout.ParallelGroup col3 =
        // layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // GroupLayout.ParallelGroup col4 =
        // layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        //
        // main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
        // vertical_grp.addContainerGap().addGroup(layout.createBaselineGroup(false,
        // false).addComponent(pScroll));
        //
        // main.addGroup(horizontal_grp);
        // main_horizontal_grp.addGroup(main);
        //
        // layout.setHorizontalGroup(main_horizontal_grp);
        // layout.setVerticalGroup(vertical_grp);
        Style style = null;
        style = textPane.addStyle("Black", null);
        StyleConstants.setForeground(style, Color.black);
        style = textPane.addStyle("Red", null);
        StyleConstants.setForeground(style, Color.red);
        style = textPane.addStyle("Orange", null);
        StyleConstants.setForeground(style, Color.orange);
        style = textPane.addStyle("Yellow", null);
        StyleConstants.setForeground(style, Color.yellow);
        style = textPane.addStyle("Green", null);
        StyleConstants.setForeground(style, Color.green);
        style = textPane.addStyle("Blue", null);
        StyleConstants.setForeground(style, Color.blue);
        style = textPane.addStyle("DarkGray", null);
        StyleConstants.setForeground(style, Color.darkGray);
        style = textPane.addStyle("Pink", null);
        StyleConstants.setForeground(style, Color.pink);
        style = textPane.addStyle("Cyan", null);
        StyleConstants.setForeground(style, Color.cyan);
    }

    /**
     * 服務器日誌信息
     *
     * @param msg   txt
     * @param color 顏色
     */
    public void append(String msg, String color) {
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), msg, textPane.getStyle(color));
            textPane.setCaretPosition(textPane.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void savelog() {
        try {
            File f = null;
            String sTemp = "";
            synchronized (Eva.lock) {
                sTemp = Eva.getDate();
                StringTokenizer s = new StringTokenizer(sTemp, " ");
                Eva.date = s.nextToken();
                Eva.time = s.nextToken();
                f = new File("ServerLog/" + Eva.date);
                if (!f.exists()) {
                    f.mkdir();
                }
                Eva.flush(textPane, "[" + Eva.time + "] 0.系統", Eva.date);
                sTemp = null;
                Eva.date = null;
                Eva.time = null;
            }
            textPane.setText("");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
