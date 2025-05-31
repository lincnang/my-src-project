package com.lineage.managerUI;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.StringTokenizer;

/**
 * 類名稱：ServerMultiChatLogWindow<br>
 * 類描述：多個聊天監控組件<br>
 * 創建人:Manly<br>
 * 修改時間：2022年4月27日 上午1:52:44<br>
 * 修改人:QQ:263075225<br>
 * 修改備註:<br>
 *
 * @version<br>
 */
@SuppressWarnings("serial")
public class ServerMultiChatLogWindow extends JInternalFrame {
    /**
     * 世界信頻道信息
     */
    private JTextPane worldChatText = null;
    /**
     * 一般頻道、大叫頻道信息
     */
    private JTextPane nomalChatText = null;
    /**
     * 密語、私聊信息
     */
    private JTextPane whisperChatText = null;
    /**
     * 血盟頻道信息
     */
    private JTextPane clanChatText = null;
    /**
     * 隊伍頻道信息
     */
    private JTextPane partyChatText = null;
    /**
     * 交易頻道信息
     */
    private JTextPane tradeChatText = null;
    /**
     * 倉庫信息
     */
    private JTextPane wareHouseText = null;
    /**
     * 交易信息
     */
    private JTextPane tradeText = null;
    /**
     * 強化信息
     */
    private JTextPane enchantText = null;
    /**
     * 刪物物品信息
     */
    private JTextPane observeText = null;
    /**
     * 穿牆信息
     */
    private JTextPane moveerrorText = null;
    /**
     * 錯誤信息
     */
    private JTextPane bugText = null;
    /**
     * 命令信息
     */
    private JTextPane commandText = null;
    private JScrollPane worldChatScroll = null;
    private JScrollPane nomalChatScroll = null;
    private JScrollPane whisperChatScroll = null;
    private JScrollPane clanChatScroll = null;
    private JScrollPane partyChatScroll = null;
    private JScrollPane tradeChatScroll = null;
    private JScrollPane wareHouseScroll = null;
    private JScrollPane tradeScroll = null;
    private JScrollPane enchantScroll = null;
    private JScrollPane observeScroll = null;
    private JScrollPane moveerrorScroll = null;
    private JScrollPane bugScroll = null;
    private JScrollPane commandScroll = null;
    private JTextField txt_ChatUser = null;
    private JTextField txt_ChatSend = null;
    private JButton btn_Clear = null;
    private JTabbedPane jJTabbedPane = null;

    public ServerMultiChatLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
        super();
        initialize(windowName, x, y, width, height, resizable, closable);
    }

    public void initialize(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
        this.title = windowName;
        this.closable = closable;
        this.isMaximum = false;
        this.maximizable = false;
        this.resizable = resizable;
        this.iconable = true;
        this.isIcon = false;
        setSize(width, height);
        setBounds(x, y, width, height);
        setVisible(true);
        frameIcon = new ImageIcon("");
        setRootPaneCheckingEnabled(true);
        updateUI();
        try {
            jJTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
            worldChatText = new JTextPane();
            nomalChatText = new JTextPane();
            whisperChatText = new JTextPane();
            clanChatText = new JTextPane();
            partyChatText = new JTextPane();
            tradeChatText = new JTextPane();
            wareHouseText = new JTextPane();
            tradeText = new JTextPane();
            enchantText = new JTextPane();
            observeText = new JTextPane();
            moveerrorText = new JTextPane();
            bugText = new JTextPane();
            commandText = new JTextPane();
            worldChatScroll = new JScrollPane(worldChatText);
            nomalChatScroll = new JScrollPane(nomalChatText);
            whisperChatScroll = new JScrollPane(whisperChatText);
            clanChatScroll = new JScrollPane(clanChatText);
            partyChatScroll = new JScrollPane(partyChatText);
            tradeChatScroll = new JScrollPane(tradeChatText);
            wareHouseScroll = new JScrollPane(wareHouseText);
            tradeScroll = new JScrollPane(tradeText);
            enchantScroll = new JScrollPane(enchantText);
            observeScroll = new JScrollPane(observeText);
            moveerrorScroll = new JScrollPane(moveerrorText);
            bugScroll = new JScrollPane(bugText);
            commandScroll = new JScrollPane(commandText);
            worldChatText.setEditable(false);
            nomalChatText.setEditable(false);
            whisperChatText.setEditable(false);
            clanChatText.setEditable(false);
            partyChatText.setEditable(false);
            tradeChatText.setEditable(false);
            wareHouseText.setEditable(false);
            tradeText.setEditable(false);
            enchantText.setEditable(false);
            observeText.setEditable(false);
            moveerrorText.setEditable(false);
            bugText.setEditable(false);
            commandText.setEditable(false);
            worldChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            worldChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            nomalChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            nomalChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            whisperChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            whisperChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            clanChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            clanChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            partyChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            partyChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            tradeChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tradeChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            wareHouseScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            wareHouseScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            tradeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tradeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            enchantScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            enchantScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            observeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            observeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            moveerrorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            moveerrorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            bugScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            bugScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            commandScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            commandScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            setStyle(worldChatText);
            setStyle(nomalChatText);
            setStyle(whisperChatText);
            setStyle(clanChatText);
            setStyle(partyChatText);
            setStyle(tradeChatText);
            setStyle(wareHouseText);
            setStyle(tradeText);
            setStyle(enchantText);
            setStyle(observeText);
            setStyle(moveerrorText);
            setStyle(bugText);
            setStyle(commandText);
            jJTabbedPane.addTab("世界", worldChatScroll);
            jJTabbedPane.addTab("普通", nomalChatScroll);
            jJTabbedPane.addTab("密聊", whisperChatScroll);
            jJTabbedPane.addTab("血盟", clanChatScroll);
            jJTabbedPane.addTab("隊伍", partyChatScroll);
            jJTabbedPane.addTab("買賣", tradeChatScroll);
            jJTabbedPane.addTab("倉庫", wareHouseScroll);
            jJTabbedPane.addTab("交易", tradeScroll);
            jJTabbedPane.addTab("強化", enchantScroll);
            jJTabbedPane.addTab("刪物", observeScroll);
            jJTabbedPane.addTab("穿牆", moveerrorScroll);
            jJTabbedPane.addTab("錯誤", bugScroll);
            jJTabbedPane.addTab("命令", commandScroll);
            txt_ChatUser = new JTextField();
            txt_ChatSend = new JTextField();
            txt_ChatSend.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    chatKeyPressed(evt);
                }
            });
            btn_Clear = new JButton("清除");
            btn_Clear.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
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
                            Eva.jSystemLogWindow.savelog();
                            Eva.flush(worldChatText, "[" + Eva.time + "] 世界", Eva.date);
                            Eva.flush(nomalChatText, "[" + Eva.time + "] 普通", Eva.date);
                            Eva.flush(whisperChatText, "[" + Eva.time + "] 密聊", Eva.date);
                            Eva.flush(clanChatText, "[" + Eva.time + "] 血盟", Eva.date);
                            Eva.flush(partyChatText, "[" + Eva.time + "] 隊伍", Eva.date);
                            Eva.flush(wareHouseText, "[" + Eva.time + "] 倉庫", Eva.date);
                            Eva.flush(tradeText, "[" + Eva.time + "] 交易", Eva.date);
                            Eva.flush(enchantText, "[" + Eva.time + "] 強化", Eva.date);
                            Eva.flush(observeText, "[" + Eva.time + "] 刪物", Eva.date);
                            Eva.flush(moveerrorText, "[" + Eva.time + "] 穿牆", Eva.date);
                            Eva.flush(bugText, "[" + Eva.time + "] 錯誤", Eva.date);
                            Eva.flush(commandText, "[" + Eva.time + "] 命令", Eva.date);
                            sTemp = null;
                            Eva.date = null;
                            Eva.time = null;
                        }
                        worldChatText.setText("");
                        nomalChatText.setText("");
                        whisperChatText.setText("");
                        clanChatText.setText("");
                        partyChatText.setText("");
                        tradeChatText.setText("");
                        wareHouseText.setText("");
                        tradeText.setText("");
                        enchantText.setText("");
                        observeText.setText("");
                        moveerrorText.setText("");
                        bugText.setText("");
                        commandText.setText("");
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
            GroupLayout layout = new GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            GroupLayout.SequentialGroup main_horizontal_grp = layout.createSequentialGroup();
            GroupLayout.SequentialGroup horizontal_grp = layout.createSequentialGroup();
            GroupLayout.SequentialGroup vertical_grp = layout.createSequentialGroup();
            GroupLayout.ParallelGroup main = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.ParallelGroup col1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.ParallelGroup col2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.ParallelGroup col3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.ParallelGroup col4 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            main.addGroup(horizontal_grp);
            main_horizontal_grp.addGroup(main);
            layout.setHorizontalGroup(main_horizontal_grp);
            layout.setVerticalGroup(vertical_grp);
            col1.addComponent(txt_ChatUser, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
            col2.addComponent(txt_ChatSend, GroupLayout.PREFERRED_SIZE, 405, GroupLayout.PREFERRED_SIZE);
            col3.addComponent(btn_Clear, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE);
            horizontal_grp.addGroup(col1).addGap(5);
            horizontal_grp.addGroup(col2).addGap(5);
            horizontal_grp.addGroup(col3).addGap(5);
            main.addGroup(layout.createSequentialGroup().addComponent(jJTabbedPane, GroupLayout.PREFERRED_SIZE, 588, GroupLayout.PREFERRED_SIZE));
            vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(jJTabbedPane));
            vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(txt_ChatUser).addComponent(txt_ChatSend).addComponent(btn_Clear)).addGap(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 管理器聊天調用
     *
     */
    private void chatKeyPressed(KeyEvent evt) {
        if (Eva.isServerStarted) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if (txt_ChatUser.getText().equalsIgnoreCase("")) {// 如果沒指定玩家、則全服通知
                    ServerChat.getInstance().sendChatToAll("\\fW[***] " + txt_ChatSend.getText());
                    Eva.LogChatAppend("[世界]", "[***]", txt_ChatSend.getText());
                } else {// 指定玩家密語
                    boolean result = ServerChat.getInstance().whisperToPlayer(txt_ChatUser.getText(), txt_ChatSend.getText());
                    if (result) {
                        Eva.LogChatWisperAppend("[密聊]", "[***]", txt_ChatUser.getText(), txt_ChatSend.getText(), ">");
                    } else {
                        Eva.errorMsg(txt_ChatUser.getText() + Eva.NoConnectUser);
                    }
                }
                txt_ChatSend.setText("");
            }
        } else {
            Eva.errorMsg(Eva.NoServerStartMSG);
        }
    }

    public void append(String paneName, String msg, String color) {
        StyledDocument doc = null;
        switch (paneName) {
            case "worldChatText":
                doc = worldChatText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, worldChatText.getStyle(color));
                    worldChatText.setCaretPosition(worldChatText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "nomalChatText":
                doc = nomalChatText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, nomalChatText.getStyle(color));
                    nomalChatText.setCaretPosition(nomalChatText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "whisperChatText":
                doc = whisperChatText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, whisperChatText.getStyle(color));
                    whisperChatText.setCaretPosition(whisperChatText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "clanChatText":
                doc = clanChatText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, clanChatText.getStyle(color));
                    clanChatText.setCaretPosition(clanChatText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "partyChatText":
                doc = partyChatText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, partyChatText.getStyle(color));
                    partyChatText.setCaretPosition(partyChatText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "tradeChatText":
                doc = tradeChatText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, tradeChatText.getStyle(color));
                    tradeChatText.setCaretPosition(tradeChatText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "wareHouseText":
                doc = wareHouseText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, wareHouseText.getStyle(color));
                    wareHouseText.setCaretPosition(wareHouseText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "tradeText":
                doc = tradeText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, tradeText.getStyle(color));
                    tradeText.setCaretPosition(tradeText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "enchantText":
                doc = enchantText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, enchantText.getStyle(color));
                    enchantText.setCaretPosition(enchantText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "observeText":
                doc = observeText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, observeText.getStyle(color));
                    observeText.setCaretPosition(observeText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "moveerrorText":
                doc = moveerrorText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, moveerrorText.getStyle(color));
                    moveerrorText.setCaretPosition(moveerrorText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "bugText":
                doc = bugText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, bugText.getStyle(color));
                    bugText.setCaretPosition(bugText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
            case "commandText":
                doc = commandText.getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), msg, commandText.getStyle(color));
                    commandText.setCaretPosition(commandText.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setStyle(JTextPane textPane) {
        try {
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
        } catch (Exception e) {
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
                Eva.flush(worldChatText, "[" + Eva.time + "] 1.世界", Eva.date);
                Eva.flush(nomalChatText, "[" + Eva.time + "] 2.普通", Eva.date);
                Eva.flush(whisperChatText, "[" + Eva.time + "] 3.密聊", Eva.date);
                Eva.flush(clanChatText, "[" + Eva.time + "] 4.血盟", Eva.date);
                Eva.flush(partyChatText, "[" + Eva.time + "] 5.隊伍", Eva.date);
                Eva.flush(wareHouseText, "[" + Eva.time + "] 6.倉庫", Eva.date);
                Eva.flush(tradeText, "[" + Eva.time + "] 7.交易", Eva.date);
                Eva.flush(enchantText, "[" + Eva.time + "] 8.強化", Eva.date);
                Eva.flush(observeText, "[" + Eva.time + "] 9.刪物", Eva.date);
                Eva.flush(moveerrorText, "[" + Eva.time + "] 10.穿牆", Eva.date);
                Eva.flush(bugText, "[" + Eva.time + "] 11.錯誤", Eva.date);
                Eva.flush(commandText, "[" + Eva.time + "] 12.命令", Eva.date);
                sTemp = null;
                Eva.date = null;
                Eva.time = null;
            }
            worldChatText.setText("");
            nomalChatText.setText("");
            whisperChatText.setText("");
            clanChatText.setText("");
            partyChatText.setText("");
            tradeChatText.setText("");
            wareHouseText.setText("");
            tradeText.setText("");
            enchantText.setText("");
            observeText.setText("");
            moveerrorText.setText("");
            bugText.setText("");
            commandText.setText("");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
