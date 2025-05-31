package com.lineage.managerUI;

import com.lineage.DatabaseFactory;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("serial")
public class ServerUserInfoWindow extends JInternalFrame {
    public JTable jJTable0 = null;
    private ServerPresentWindow jServerPresentWindow = null;
    private ServerPolyWindow jServerPolyWindow = null;
    private JTable jJTable1 = null;
    private JTable jJTable2 = null;
    private JTable jJTable3 = null;
    private JTable jJTable4 = null;
    private JTable jJTable5 = null;
    private JScrollPane jJScrollPane1 = null;
    private JScrollPane jJScrollPane2 = null;
    private JScrollPane jJScrollPane3 = null;
    private JScrollPane jJScrollPane4 = null;
    private JScrollPane jJScrollPane5 = null;
    private DefaultTableModel model0 = null;
    private DefaultTableModel model1 = null;
    private DefaultTableModel model2 = null;
    private DefaultTableModel model3 = null;
    private DefaultTableModel model4 = null;
    private DefaultTableModel model5 = null;
    private JTabbedPane jJTabbedPane1 = null;
    private JTabbedPane jJTabbedPane2 = null;
    private JLabel lbl_UserCount = null;
    private JLabel lbl_Helper = null;
    private JTextField txt_UserName = null;
    // public JList jJList = null;
    // private DefaultListModel listModel = null;
    private JScrollPane pScroll = null;
    private JButton btn_Search = null;
    //private JButton btn_Refresh = null;
    private JButton btn_Ban = null;
    private JButton btn_NoChat = null;
    private JButton btn_Chat = null;
    private JButton btn_Present = null;
    private JButton btn_Poly = null;
    private JButton btn_AllPresent = null;
    private JButton btn_AllPoly = null;
    private JCheckBox chk_Infomation = null;
    private javax.swing.Timer userListTimer = null;

    public ServerUserInfoWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
        super();
        initialize(windowName, x, y, width, height, resizable, closable);
    }


    // 刷新用戶列表，把所有在線玩家名稱加到 jJTable0 表格中
    public void refreshUserList() {
        // 先清空原本表格內容
        DefaultTableModel tableModel = (DefaultTableModel) jJTable0.getModel();
        tableModel.setRowCount(0);

        // 從 World 取得所有在線玩家名單
        for (L1PcInstance pc : World.get().getAllPlayers()) {
                String[] row = { pc.getName() };
                tableModel.addRow(row);
        }
        // 更新用戶數量標籤
        lbl_UserCount.setText("在線用戶數量 : " + tableModel.getRowCount());
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
            jJTabbedPane1 = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
            jJTabbedPane2 = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
            lbl_UserCount = new JLabel("在線用戶數量 : 0");
            lbl_UserCount.setForeground(Color.red);
            lbl_Helper = new JLabel("搜索用戶後 點擊 >");
            lbl_Helper.setForeground(Color.blue);
            // listModel = new DefaultListModel();
            // jJList = new JList(listModel);
            // jJList.addListSelectionListener(new JListHandler());
            String[] model0ColName = {""};
            model0 = new DefaultTableModel(model0ColName, 0);
            jJTable0 = new JTable(model0);
            jJTable0.setEnabled(true);
            jJTable0.addMouseListener(new MouseListenner());
            pScroll = new JScrollPane(jJTable0);
            txt_UserName = new JTextField();
            txt_UserName.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    chatKeyPressed(evt);
                }
            });
            btn_Search = new JButton("搜索");
            btn_Search.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    try {
                        /*
                         * if (txt_UserName.getText().equalsIgnoreCase(""))
                         * { eva.errorMsg(eva.blankSetUser); return; } for
                         * (int i = 0; i < listModel.size(); i ++) { if
                         * (((String
                         * )listModel.get(i)).indexOf(txt_UserName.getText
                         * ()) > -1) { jJList.setSelectedIndex(i);
                         * jJList.ensureIndexIsVisible(i); break; } }
                         */
                        if (txt_UserName.getText().equalsIgnoreCase("")) {
                            Eva.errorMsg(Eva.blankSetUser);
                            return;
                        }
                        /*
                         * for (int i = 0; i <
                         * jJTable0.getModel().getRowCount(); i++) { if
                         * (((String)jJTable0.getModel().getValueAt(i,
                         * 0)).indexOf(txt_UserName.getText()) > -1) {
                         * CharacterInfoSearch(i, 0); return; } }
                         */
                        L1PcInstance player = World.get().getPlayer(txt_UserName.getText());
                        if (player != null) {
                            txt_UserName.setText(player.getName());
                            CharacterInfoSearch();
                        } else {
                            JOptionPane.showMessageDialog(null, txt_UserName.getText() + "當前遊戲中不在線的.", "Server Message", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            // btn_Refresh = new JButton("Refresh");
            // btn_Refresh.addActionListener(new ActionListener() {
            // public void actionPerformed(ActionEvent e) {
            // if (eva.isServerStarted) {
            // try {
            // jJList.clearSelection();
            // listModel.clear();
            //
            // for (L1PcInstance player : World.get().getAllPlayers()) {
            // if (!player.isNoPlayer()) {
            // listModel.addElement(player.getName());
            // }
            // }
            //
            // lbl_UserCount.setText("접속자수 : " + (listModel.size()));
            // } catch (Exception ex) {
            // jJList.clearSelection();
            // }
            // } else {
            // eva.errorMsg(eva.NoServerStartMSG);
            // }
            // }
            // });
            btn_Ban = new JButton("踢人");
            btn_Ban.setToolTipText("將選擇的用戶踢出遊戲.");
            btn_Ban.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    if (!txt_UserName.getText().equalsIgnoreCase("")) {
                        L1PcInstance pc = World.get().getPlayer(txt_UserName.getText());
                        // IpTable iptable = IpTable.get();
                        if (pc != null) {
                            /*
                             * Account.ban(pc.getAccountName());
                             * iptable.banIp(pc.getNetConnection().getIp());
                             * // BAN IP.
                             */
                            // 自改
                            final ClientExecutor targetClient = pc.getNetConnection();
                            targetClient.kick();
                            // end
                            pc.sendPackets(new S_Disconnect());
                            Eva.LogCommandAppend("[***]", "強制驅逐", pc.getName());
                            txt_UserName.setText("");
                        } else {
                            Eva.errorMsg(txt_UserName.getText() + Eva.NoConnectUser);
                        }
                    } else {
                        Eva.errorMsg(Eva.blankSetUser);
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            btn_NoChat = new JButton("禁言");
            btn_NoChat.setToolTipText("將選擇的用戶禁言10分鐘.");
            btn_NoChat.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    if (!txt_UserName.getText().equalsIgnoreCase("")) {
                        L1PcInstance pc = World.get().getPlayer(txt_UserName.getText());
                        if (pc != null) {
                            pc.setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, 10 * 60 * 1000);
                            pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 10 * 60));
                            pc.sendPackets(new S_ServerMessage(286, String.valueOf(10))); // \f3因你不正當的行為而
                            // %0分鐘之內無法交談.
                            Eva.LogCommandAppend("[***]", "禁言", "10");
                            txt_UserName.setText("");
                        } else {
                            Eva.errorMsg(txt_UserName.getText() + Eva.NoConnectUser);
                        }
                    } else {
                        Eva.errorMsg(Eva.blankSetUser);
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            btn_Present = new JButton("贈送");
            btn_Present.setToolTipText("贈送禮物給選定的用戶.");
            btn_Present.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    if (!txt_UserName.getText().equalsIgnoreCase("")) {
                        jServerPresentWindow = new ServerPresentWindow("贈送", 0, 0, 400, 400, false, true);
                        Eva.jJDesktopPane.add(jServerPresentWindow, 0);
                        jServerPresentWindow.setLocation((Eva.jJFrame.getContentPane().getSize().width / 2) - (jServerPresentWindow.getContentPane().getSize().width / 2), (Eva.jJFrame.getContentPane().getSize().height / 2) - (jServerPresentWindow.getContentPane().getSize().height / 2));
                        jServerPresentWindow.txt_UserName.setText(txt_UserName.getText());
                    } else {
                        Eva.errorMsg(Eva.blankSetUser);
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            btn_Poly = new JButton("變身");
            btn_Poly.setToolTipText("將選定的用戶變成指定的怪物樣子.");
            btn_Poly.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    if (!txt_UserName.getText().equalsIgnoreCase("")) {
                        if (!txt_UserName.getText().equalsIgnoreCase("")) {
                            jServerPolyWindow = new ServerPolyWindow("變身", 0, 0, 400, 400, false, true);
                            Eva.jJDesktopPane.add(jServerPolyWindow, 0);
                            jServerPolyWindow.setLocation((Eva.jJFrame.getContentPane().getSize().width / 2) - (jServerPolyWindow.getContentPane().getSize().width / 2), (Eva.jJFrame.getContentPane().getSize().height / 2) - (jServerPolyWindow.getContentPane().getSize().height / 2));
                            jServerPolyWindow.txt_UserName.setText(txt_UserName.getText());
                        } else {
                            Eva.errorMsg(Eva.blankSetUser);
                        }
                    } else {
                        Eva.errorMsg(Eva.blankSetUser);
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            btn_AllPresent = new JButton("全體贈送");
            btn_AllPresent.setToolTipText("贈送禮物給所有用戶.");
            btn_AllPresent.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    try {
                        jServerPresentWindow = new ServerPresentWindow("全體贈送", 0, 0, 400, 400, false, true);
                        Eva.jJDesktopPane.add(jServerPresentWindow, 0);
                        int centerX = (Eva.jJFrame.getContentPane().getSize().width / 2) - (jServerPresentWindow.getContentPane().getSize().width / 2);
                        int centerY = (Eva.jJFrame.getContentPane().getSize().height / 2) - (jServerPresentWindow.getContentPane().getSize().height / 2);
                        jServerPresentWindow.setLocation(centerX, centerY);
                        jServerPresentWindow.txt_UserName.setText("全體用戶");
                    } catch (Exception ex) {
                        ex.printStackTrace(); // 顯示完整錯誤堆疊
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });

            btn_AllPoly = new JButton("全體變身");
            btn_AllPoly.setToolTipText("遊戲在線的所有角色變身成指定的怪物樣子.");
            btn_AllPoly.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    try {
                        jServerPolyWindow = new ServerPolyWindow("全體變身", 0, 0, 400, 400, false, true);
                        Eva.jJDesktopPane.add(jServerPolyWindow, 0);
                        int centerX = (Eva.jJFrame.getContentPane().getSize().width / 2) - (jServerPolyWindow.getContentPane().getSize().width / 2);
                        int centerY = (Eva.jJFrame.getContentPane().getSize().height / 2) - (jServerPolyWindow.getContentPane().getSize().height / 2);
                        jServerPolyWindow.setLocation(centerX, centerY);
                        jServerPolyWindow.txt_UserName.setText("全體用戶"); // 這裡預設「全體用戶」
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            btn_Chat = new JButton("解除禁言");
            btn_Chat.setToolTipText("將選擇的用戶解除禁言.");
            btn_Chat.addActionListener(e -> {
                if (Eva.isServerStarted) {
                    if (!txt_UserName.getText().equalsIgnoreCase("")) {
                        L1PcInstance pc = World.get().getPlayer(txt_UserName.getText());
                        if (pc != null) {
                            pc.removeSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED);
                            Eva.LogCommandAppend("[***]", "已解除禁言", "!");
                            txt_UserName.setText("");
                        } else {
                            Eva.errorMsg(txt_UserName.getText() + Eva.NoConnectUser);
                        }
                    } else {
                        Eva.errorMsg(Eva.blankSetUser);
                    }
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            });
            try {
                chk_Infomation = new JCheckBox("信息");
                chk_Infomation.setSelected(true);
                chk_Infomation.setToolTipText("可實時更新 信息,背包,倉庫,裝備,帳號信息!");
                JLabel infoLabel = new JLabel("可實時更新 信息,背包,倉庫,裝備,帳號信息!");
                infoLabel.setForeground(Color.GREEN.darker());

                pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jJTabbedPane1.addTab("用戶列表", pScroll);
                String[] model1ColName = {"", "", "", ""};
                String[] model2ColName = {"編號", "名稱", "數量", "強化", "屬性", "階段"};
                String[] model5ColName = {"帳號", "IP", "角色名", "等級", "血盟", "狀態"};
                model1 = new DefaultTableModel(model1ColName, 0);
                model2 = new DefaultTableModel(model2ColName, 0);
                model3 = new DefaultTableModel(model2ColName, 0);
                model4 = new DefaultTableModel(model2ColName, 0);
                model5 = new DefaultTableModel(model5ColName, 0);
                jJTable1 = new JTable(model1);
                jJTable2 = new JTable(model2);
                jJTable2.setAutoCreateRowSorter(true);
                TableRowSorter sorter2 = new TableRowSorter(jJTable2.getModel());
                jJTable2.setRowSorter(sorter2);
                jJTable2.getColumnModel().getColumn(0).setPreferredWidth(70);
                jJTable2.getColumnModel().getColumn(1).setPreferredWidth(180);
                jJTable2.getColumnModel().getColumn(2).setPreferredWidth(40);
                jJTable2.getColumnModel().getColumn(3).setPreferredWidth(40);
                jJTable2.getColumnModel().getColumn(4).setPreferredWidth(50);
                jJTable2.getColumnModel().getColumn(5).setPreferredWidth(100);
                jJTable3 = new JTable(model3);
                jJTable3.setAutoCreateRowSorter(true);
                TableRowSorter sorter3 = new TableRowSorter(jJTable3.getModel());
                jJTable3.setRowSorter(sorter3);
                jJTable3.getColumnModel().getColumn(0).setPreferredWidth(70);
                jJTable3.getColumnModel().getColumn(1).setPreferredWidth(180);
                jJTable3.getColumnModel().getColumn(2).setPreferredWidth(40);
                jJTable3.getColumnModel().getColumn(3).setPreferredWidth(40);
                jJTable3.getColumnModel().getColumn(4).setPreferredWidth(50);
                jJTable3.getColumnModel().getColumn(5).setPreferredWidth(100);
                jJTable4 = new JTable(model4);
                jJTable4.setAutoCreateRowSorter(true);
                TableRowSorter sorter4 = new TableRowSorter(jJTable4.getModel());
                jJTable4.setRowSorter(sorter4);
                jJTable4.getColumnModel().getColumn(0).setPreferredWidth(70);
                jJTable4.getColumnModel().getColumn(1).setPreferredWidth(180);
                jJTable4.getColumnModel().getColumn(2).setPreferredWidth(40);
                jJTable4.getColumnModel().getColumn(3).setPreferredWidth(40);
                jJTable4.getColumnModel().getColumn(4).setPreferredWidth(50);
                jJTable4.getColumnModel().getColumn(5).setPreferredWidth(100);
                jJTable5 = new JTable(model5);
                jJTable5.setAutoCreateRowSorter(true);
                TableRowSorter sorter5 = new TableRowSorter(jJTable5.getModel());
                jJTable5.setRowSorter(sorter5);
                jJTable5.getColumnModel().getColumn(0).setPreferredWidth(100);
                jJTable5.getColumnModel().getColumn(1).setPreferredWidth(100);
                jJTable5.getColumnModel().getColumn(2).setPreferredWidth(90);
                jJTable5.getColumnModel().getColumn(3).setPreferredWidth(80);
                jJTable5.getColumnModel().getColumn(4).setPreferredWidth(70);
                jJTable5.getColumnModel().getColumn(5).setPreferredWidth(40);
                jJTable1.setEnabled(false);
                jJTable2.setEnabled(false);
                jJTable3.setEnabled(false);
                jJTable4.setEnabled(false);
                jJTable5.setEnabled(false);
                jJScrollPane1 = new JScrollPane(jJTable1);
                jJScrollPane2 = new JScrollPane(jJTable2);
                jJScrollPane3 = new JScrollPane(jJTable3);
                jJScrollPane4 = new JScrollPane(jJTable4);
                jJScrollPane5 = new JScrollPane(jJTable5);
                jJScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jJScrollPane5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                jJTabbedPane2.addTab("信息", jJScrollPane1);
                jJTabbedPane2.addTab("背包", jJScrollPane2);
                jJTabbedPane2.addTab("倉庫", jJScrollPane3);
                jJTabbedPane2.addTab("裝備", jJScrollPane4);
                jJTabbedPane2.addTab("帳號", jJScrollPane5);

                // ---- GroupLayout 配置 ----
                GroupLayout layout = new GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);

                // 橫向配置
                layout.setHorizontalGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(txt_UserName, 100, 120, 200)
                                        .addComponent(btn_Search, 60, 60, 80)
                                        .addComponent(chk_Infomation)
                                        .addComponent(infoLabel)
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jJTabbedPane1, 150, 200, 300)
                                        .addComponent(jJTabbedPane2, 440, 480, 600)
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(lbl_Helper, 100, 100, 120)
                                        .addComponent(btn_Ban, 50, 50, 70)
                                        .addComponent(btn_NoChat, 50, 50, 70)
                                        .addComponent(btn_Present, 50, 50, 70)
                                        .addComponent(btn_Poly, 50, 50, 70)
                                        .addComponent(btn_AllPresent, 70, 80, 100)
                                        .addComponent(btn_AllPoly, 70, 80, 100)
                                        .addComponent(btn_Chat, 70, 80, 100)
                                )
                                .addComponent(lbl_UserCount, 200, 220, 300)
                );

                // 垂直配置
                layout.setVerticalGroup(
                        layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txt_UserName)
                                        .addComponent(btn_Search)
                                        .addComponent(chk_Infomation)
                                        .addComponent(infoLabel)
                                )
                                .addGap(12)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jJTabbedPane1)
                                        .addComponent(jJTabbedPane2)
                                )
                                .addGap(12)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lbl_Helper)
                                        .addComponent(btn_Ban)
                                        .addComponent(btn_NoChat)
                                        .addComponent(btn_Present)
                                        .addComponent(btn_Poly)
                                        .addComponent(btn_AllPresent)
                                        .addComponent(btn_AllPoly)
                                        .addComponent(btn_Chat)
                                )
                                .addGap(10)
                                .addComponent(lbl_UserCount)
                );
                refreshUserList();
                System.out.println("當前線上人數：" + World.get().getAllPlayers().size());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("UI介面故障 請檢查配置文件或重新啟動伺服器.");

            }
            if (userListTimer == null) {
                userListTimer = new javax.swing.Timer(3000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            refreshUserList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                userListTimer.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void chatKeyPressed(KeyEvent evt) {
        if (Eva.isServerStarted) {
            try {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (txt_UserName.getText().equalsIgnoreCase("")) {
                        Eva.errorMsg(Eva.blankSetUser);
                        return;
                    }
                    L1PcInstance player = World.get().getPlayer(txt_UserName.getText());
                    if (player != null) {
                        txt_UserName.setText(player.getName());
                        CharacterInfoSearch();
                    } else {
                        JOptionPane.showMessageDialog(null, txt_UserName.getText() + "當前在線遊戲中不存在的.", "Server Message", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Eva.errorMsg(Eva.NoServerStartMSG);
        }
    }

    private void CharacterInfoSearch() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try {
            // txt_UserName.setText((String)model0.getValueAt(row, column));
            if (chk_Infomation.isSelected()) {
                L1PcInstance player = World.get().getPlayer(txt_UserName.getText());
                DefaultTableModel tModel1 = (DefaultTableModel) jJTable1.getModel();
                DefaultTableModel tModel2 = (DefaultTableModel) jJTable2.getModel();
                DefaultTableModel tModel3 = (DefaultTableModel) jJTable3.getModel();
                DefaultTableModel tModel4 = (DefaultTableModel) jJTable4.getModel();
                DefaultTableModel tModel5 = (DefaultTableModel) jJTable5.getModel();
                for (int i = tModel1.getRowCount() - 1; i >= 0; i--) {
                    tModel1.removeRow(i);
                }
                for (int i = tModel2.getRowCount() - 1; i >= 0; i--) {
                    tModel2.removeRow(i);
                }
                for (int i = tModel3.getRowCount() - 1; i >= 0; i--) {
                    tModel3.removeRow(i);
                }
                for (int i = tModel4.getRowCount() - 1; i >= 0; i--) {
                    tModel4.removeRow(i);
                }
                for (int i = tModel5.getRowCount() - 1; i >= 0; i--) {
                    tModel5.removeRow(i);
                }
                if (player != null) {
                    int lv = player.getLevel();
                    long currentLvExp = ExpTable.getExpByLevel(lv);
                    long nextLvExp = ExpTable.getExpByLevel(lv + 1);
                    double neededExp = nextLvExp - currentLvExp;
                    double currentExp = player.getExp() - currentLvExp;
                    int per = (int) ((currentExp / neededExp) * 100.0);
                    String ClassName = "";
                    if (player.isCrown()) {
                        ClassName = "王族";
                    } else if (player.isKnight()) {
                        ClassName = "騎士";
                    } else if (player.isElf()) {
                        ClassName = "妖精";
                    } else if (player.isDarkelf()) {
                        ClassName = "黑暗妖精";
                    } else if (player.isWizard()) {
                        ClassName = "魔法師";
                    } else if (player.isDragonKnight()) {
                        ClassName = "龍騎士";
                    } else if (player.isIllusionist()) {
                        ClassName = "幻術士";
                    }
                    // 傳回玩家減重
                    double armorWeight = player.getWeightReduction();
                    // 娃娃的數量
                    double dollWeight = player.get_weightUP();
                    /*
                     * for (L1DollInstance doll : player.getDolls().values()) {
                     * dollWeight = doll.getWeightReductionByDoll(); }
                     */
                    // 額外負重減少數值
                    double plusWeight = armorWeight + dollWeight;
                    int hpr = player.getHpr() + player.getInventory().hpRegenPerTick();
                    int mpr = player.getMpr() + player.getInventory().mpRegenPerTick();
                    String[] lbls = {"職業", "等級", "HP", "MP", "負重程度", "正義值數", "力量(加值/基礎)", "敏捷(加值/基礎)", "體質(加值/基礎)", "智力(加值/基礎)", "精神(加值/基礎)", "魅力(加值/基礎)", "回血量", "回魔量", "[普通]近距離命中", "[普通]近距離傷害", "[普通]遠距離命中", "[普通]遠距離傷害", "[防具]近距離命中", "[防具]近距離傷害", "[防具]遠距離命中", "[防具]遠距離傷害", "[娃娃]遠距離命中", "[娃娃]遠距離傷害", "[普通]減免傷害值", ""};
                    String[] charInfo = new String[4];
                    int index = 0;
                    for (int i = 0; i < lbls.length; i++) {
                        charInfo[0] = lbls[i];
                        charInfo[2] = lbls[++i];
                        switch (index) {
                            case 0:
                                charInfo[1] = ClassName;
                                charInfo[3] = String.valueOf(player.getLevel()) + " ( " + per + "% )";
                                break;
                            case 1:
                                charInfo[1] = String.valueOf(player.getMaxHp());
                                charInfo[3] = String.valueOf(player.getMaxMp());
                                break;
                            case 2:
                                charInfo[1] = String.valueOf(plusWeight);
                                charInfo[3] = String.valueOf(player.getLawful());
                                break;
                            case 3:
                                charInfo[1] = String.valueOf(player.getOriginalStr()) + " / " + player.getBaseStr();
                                charInfo[3] = String.valueOf(player.getOriginalDex()) + " / " + player.getBaseDex();
                                break;
                            case 4:
                                charInfo[1] = String.valueOf(player.getOriginalCon()) + " / " + player.getBaseCon();
                                charInfo[3] = String.valueOf(player.getOriginalInt()) + " / " + player.getBaseInt();
                                break;
                            case 5:
                                charInfo[1] = String.valueOf(player.getOriginalWis()) + " / " + player.getBaseWis();
                                charInfo[3] = String.valueOf(player.getOriginalCha()) + " / " + player.getBaseCha();
                                break;
                            case 6:
                                charInfo[1] = String.valueOf(hpr);
                                charInfo[3] = String.valueOf(mpr);
                                break;
                            case 7:
                                charInfo[1] = String.valueOf(player.getHitup());
                                charInfo[3] = String.valueOf(player.getDmgup());
                                break;
                            case 8:
                                charInfo[1] = String.valueOf(player.getBowHitup());
                                charInfo[3] = String.valueOf(player.getBowDmgup());
                                break;
                            case 9:
                                charInfo[1] = String.valueOf(player.getHitModifierByArmor());
                                charInfo[3] = String.valueOf(player.getDmgModifierByArmor());
                                break;
                            case 10:
                                charInfo[1] = String.valueOf(player.getBowHitModifierByArmor());
                                charInfo[3] = String.valueOf(player.getBowDmgModifierByArmor());
                                break;
                            case 11:
                                charInfo[1] = String.valueOf(player.getBowHitup());
                                charInfo[3] = String.valueOf(player.getBowDmgup());
                                break;
                            case 12:
                                charInfo[1] = String.valueOf(player.getDamageReductionByArmor());
                                charInfo[3] = "";
                                break;
                            default:
                                break;
                        }
                        tModel1.addRow(charInfo);
                        index++;
                    }
                    try {
                        con = DatabaseFactory.get().getConnection();
                        pstm = con.prepareStatement("select * from character_warehouse where account_name = ? ORDER BY 2 DESC, 1 ASC");
                        pstm.setString(1, player.getAccountName());
                        rs1 = pstm.executeQuery();
                        while (rs1.next()) {
                            String[] items = new String[5];
                            items[0] = String.valueOf(rs1.getInt("item_id"));
                            items[1] = rs1.getString("item_name");
                            items[2] = CommonUtil.numberFormat(rs1.getInt("count"));
                            items[3] = String.valueOf(rs1.getInt("enchantlvl"));
                            L1ItemInstance temp = ItemTable.get().createItem(rs1.getInt("item_id"));
                            temp.setAttrEnchantLevel(rs1.getInt("attr_enchant_level"));
                            items[4] = getAttrName(temp);
                            tModel3.addRow(items);
                        }
                    } catch (Exception ex) {
                    } finally {
                        SQLUtil.close(rs1);
                        SQLUtil.close(pstm);
                        SQLUtil.close(con);
                    }
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("SELECT login, ip, host FROM accounts WHERE ip = ");
                        sb.append("(SELECT ip FROM accounts WHERE login = ");
                        sb.append("(SELECT account_name FROM characters WHERE char_name = ?))");
                        con = DatabaseFactory.get().getConnection();
                        pstm = con.prepareStatement(sb.toString());
                        pstm.setString(1, player.getName());
                        rs1 = pstm.executeQuery();
                        while (rs1.next()) {
                            pstm = con.prepareStatement("SELECT char_name, level, highlevel, clanname, onlinestatus FROM characters WHERE account_name = ?");
                            pstm.setString(1, rs1.getString("login"));
                            rs2 = pstm.executeQuery();
                            String[] account = new String[6];
                            account[0] = rs1.getString("login");
                            account[1] = rs1.getString("host");
                            while (rs2.next()) {
                                account[2] = rs2.getString("char_name");
                                account[3] = rs2.getInt("level") + " / " + rs2.getInt("highlevel");
                                account[4] = rs2.getString("clanname");
                                account[5] = rs2.getInt("onlinestatus") == 0 ? "離線" : "在線";
                                tModel5.addRow(account);
                            }
                        }
                    } catch (Exception ex) {
                    } finally {
                        SQLUtil.close(rs2);
                        SQLUtil.close(rs1);
                        SQLUtil.close(pstm);
                        SQLUtil.close(con);
                    }
                    for (L1ItemInstance item : player.getInventory().getItems()) {
                        String[] items = new String[6];
                        items[0] = String.valueOf(item.getItemId());
                        items[1] = item.getNameEva();
                        items[2] = String.valueOf(item.getCount());
                        items[3] = String.valueOf(item.getEnchantLevel());
                        items[4] = getAttrName(item);
                        tModel2.addRow(items);
                        if (item.getItem().getType2() != 0) {
                            tModel4.addRow(items);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } finally {
            SQLUtil.close(rs2);
            SQLUtil.close(rs1);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private String getAttrName(L1ItemInstance item) {
        int attrenchantlvl = item.getAttrEnchantKind();
        String attrname = "";
        if (attrenchantlvl > 0 && item.getItem().getType2() == 1) {
            switch (attrenchantlvl) {
                case 1: // 地
                    if (item.getAttrEnchantLevel() == 1) {
                        attrname = "地之";
                    } else if (item.getAttrEnchantLevel() == 2) {
                        attrname = "崩裂";
                    } else if (item.getAttrEnchantLevel() == 3) {
                        attrname = "地靈";
                    }
                    break;
                case 2: // 火
                    if (item.getAttrEnchantLevel() == 1) {
                        attrname = "火之";
                    } else if (item.getAttrEnchantLevel() == 2) {
                        attrname = "烈焰";
                    } else if (item.getAttrEnchantLevel() == 3) {
                        attrname = "火靈";
                    }
                    break;
                case 4: // 水
                    if (item.getAttrEnchantLevel() == 1) {
                        attrname = "水之";
                    } else if (item.getAttrEnchantLevel() == 2) {
                        attrname = "海嘯";
                    } else if (item.getAttrEnchantLevel() == 3) {
                        attrname = "水靈";
                    }
                    break;
                case 8: // 風
                    if (item.getAttrEnchantLevel() == 1) {
                        attrname = "風之";
                    } else if (item.getAttrEnchantLevel() == 2) {
                        attrname = "暴風";
                    } else if (item.getAttrEnchantLevel() == 3) {
                        attrname = "風靈";
                    }
                    break;
                default:
                    attrname = "無";
                    break;
            }
        }
        if (attrenchantlvl > 0 && item.getItem().getType2() == 2) {
            switch (attrenchantlvl) {
                case 1:
                    attrname = "屬性 I";
                    break;
                case 2:
                    attrname = "屬性 II";
                    break;
                case 3:
                    attrname = "屬性 III";
                    break;
                case 4:
                    attrname = "屬性 IV";
                    break;
                case 5:
                    attrname = "屬性 V";
                    break;
                default:
                    attrname = "無";
                    break;
            }
        }
        return attrname;
    }

    /*
     * private class JListHandler implements ListSelectionListener { // 리스트의 항목이
     * 선택이 되면 public void valueChanged(ListSelectionEvent event) { Connection
     * con = null; PreparedStatement pstm = null; ResultSet rs1 = null;
     * ResultSet rs2 = null; try {
     * //txt_UserName.setText((String)listModel.elementAt
     * (jJList.getSelectedIndex()));
     *
     * if (chk_Infomation.isSelected()) { L1PcInstance player =
     * World.get().getPlayer(txt_UserName.getText()); DefaultTableModel tModel1
     * = (DefaultTableModel)jJTable1.getModel(); DefaultTableModel tModel2 =
     * (DefaultTableModel)jJTable2.getModel(); DefaultTableModel tModel3 =
     * (DefaultTableModel)jJTable3.getModel(); DefaultTableModel tModel4 =
     * (DefaultTableModel)jJTable4.getModel(); DefaultTableModel tModel5 =
     * (DefaultTableModel)jJTable5.getModel();
     *
     * for (int i = tModel1.getRowCount() - 1; i >= 0; i--) {
     * tModel1.removeRow(i); } for (int i = tModel2.getRowCount() - 1; i >= 0;
     * i--) { tModel2.removeRow(i); } for (int i = tModel3.getRowCount() - 1; i
     * >= 0; i--) { tModel3.removeRow(i); } for (int i = tModel4.getRowCount() -
     * 1; i >= 0; i--) { tModel4.removeRow(i); } for (int i =
     * tModel5.getRowCount() - 1; i >= 0; i--) { tModel5.removeRow(i); }
     *
     * if (player != null) { int lv = player.getLevel(); int currentLvExp =
     * ExpTable.getExpByLevel(lv); int nextLvExp = ExpTable.getExpByLevel(lv +
     * 1); double neededExp = nextLvExp - currentLvExp; double currentExp =
     * player.getExp() - currentLvExp; int per = (int) ((currentExp / neededExp)
     * * 100.0); String ClassName = "";
     *
     * if (player.isCrown()) { ClassName = "군주"; } else if (player.isKnight()) {
     * ClassName = "기사"; } else if (player.isElf()) { ClassName = "요정"; } else
     * if (player.isDarkelf()) { ClassName = "다크엘프"; } else if
     * (player.isWizard()) { ClassName = "마법사"; } else if
     * (player.isDragonknight()) { ClassName = "용기사"; } else if
     * (player.isIllusionist()) { ClassName = "환술사"; }
     *
     * // 방어구에 의한 수치 double armorWeight = player.getWeightReduction(); // 인형에 의한
     * 수치 int dollWeight = 0; for (L1DollInstance doll :
     * player.getDollList().values()) { dollWeight =
     * doll.getWeightReductionByDoll(); } // 추가 무게감소 수치 double plusWeight =
     * armorWeight + dollWeight;
     *
     * int hpr = player.getHpr() + player.getInventory().hpRegenPerTick(); int
     * mpr = player.getMpr() + player.getInventory().mpRegenPerTick();
     *
     * String[] lbls = { "Class", "Level", "HP", "MP", "Weight", "Lawful",
     * "Str(Total/Base)", "Dex(Total/Base)", "Con(Total/Base)",
     * "Int(Total/Base)", "Wis(Total/Base)","Cha(Total/Base)", "Hpr", "Mpr",
     * "[일반]공격성공", "[일반]추가타격", "[일반]활명중치", "[일반]활타격치", "[갑옷]공격성공", "[갑옷]추가타격",
     * "[갑옷]활명중치", "[갑옷]활타격치", "[인형]공격성공", "[인형]추가타격", "[인형]활명중치", "[인형]활타격치",
     * "[일반]데미지리덕", "", "[추가]추가타격", "[추가]추가타격확률", "[추가]데미지리덕", "[추가]데미지리덕확률" };
     * String[] charInfo = new String[4];
     *
     * int index = 0; for (int i = 0; i < lbls.length; i++) { charInfo[0] =
     * lbls[i]; charInfo[2] = lbls[++i]; switch (index) { case 0: charInfo[1] =
     * ClassName; charInfo[3] = String.valueOf(player.getLevel()) + " ( " + per
     * + "% )"; break; case 1: charInfo[1] = String.valueOf(player.getMaxHp());
     * charInfo[3] = String.valueOf(player.getMaxMp()); break; case 2:
     * charInfo[1] = String.valueOf(plusWeight); charInfo[3] =
     * String.valueOf(player.getLawful()); break; case 3: charInfo[1] =
     * String.valueOf(player.getOriginalStr()) + " / " +
     * player.getAbility().getBaseStr(); charInfo[3] =
     * String.valueOf(player.getOriginalDex()) + " / " +
     * player.getAbility().getBaseDex(); break; case 4: charInfo[1] =
     * String.valueOf(player.getOriginalCon()) + " / " +
     * player.getAbility().getBaseCon(); charInfo[3] =
     * String.valueOf(player.getOriginalInt()) + " / " +
     * player.getAbility().getBaseInt(); break; case 5: charInfo[1] =
     * String.valueOf(player.getOriginalWis()) + " / " +
     * player.getAbility().getBaseWis(); charInfo[3] =
     * String.valueOf(player.getOriginalCha()) + " / " +
     * player.getAbility().getBaseCha(); break; case 6: charInfo[1] =
     * String.valueOf(hpr); charInfo[3] = String.valueOf(mpr); break; case 7:
     * charInfo[1] = String.valueOf(player.getHitup()); charInfo[3] =
     * String.valueOf(player.getDmgup()); break; case 8: charInfo[1] =
     * String.valueOf(player.getBowHitup()); charInfo[3] =
     * String.valueOf(player.getBowDmgup()); break; case 9: charInfo[1] =
     * String.valueOf(player.getHitupByArmor()); charInfo[3] =
     * String.valueOf(player.getDmgupByArmor()); break; case 10: charInfo[1] =
     * String.valueOf(player.getBowHitupByArmor()); charInfo[3] =
     * String.valueOf(player.getBowDmgupByArmor()); break; case 11: charInfo[1]
     * = String.valueOf(player.getHitupByDoll()); charInfo[3] =
     * String.valueOf(player.getDmgupByDoll()); break; case 12: charInfo[1] =
     * String.valueOf(player.getBowHitupByDoll()); charInfo[3] =
     * String.valueOf(player.getBowDmgupByDoll()); break; case 13: charInfo[1] =
     * String.valueOf(player.getDamageReductionByArmor()); charInfo[3] = "";
     * break; case 14: charInfo[1] = String.valueOf(player.getAddDamage());
     * charInfo[3] = String.valueOf(player.getAddDamageRate()); break; case 15:
     * charInfo[1] = String.valueOf(player.getAddReduction()); charInfo[3] =
     * String.valueOf(player.getAddReductionRate()); break; default: break; }
     *
     * tModel1.addRow(charInfo); index++; }
     *
     *
     * try { con = L1DatabaseFactory.get().getConnection(); pstm =
     * con.prepareStatement(
     * "select * from character_warehouse where account_name = ? ORDER BY 2 DESC, 1 ASC"
     * ); pstm.setString(1, player.getAccountName()); rs1 = pstm.executeQuery();
     * while (rs1.next()) { String[] items = new String[6]; items[0] =
     * String.valueOf(rs1.getInt("item_id")); items[1] =
     * rs1.getString("item_name"); items[2] =
     * String.valueOf(rs1.getInt("count")); items[3] =
     * String.valueOf(rs1.getInt("enchantlvl")); L1ItemInstance temp =
     * ItemTable.get().createItem(rs1.getInt("item_id"));
     * temp.setAttrEnchantLevel(rs1.getInt("attr_enchantlvl")); items[4] =
     * getAttrName(temp); items[5] = getStepName(rs1.getInt("step_enchantlvl"));
     * tModel3.addRow(items); } } catch (Exception e) { }
     *
     *
     * try { StringBuilder sb = new StringBuilder();
     * sb.append("SELECT login, ip, host, phone FROM accounts WHERE ip = ");
     * sb.append("(SELECT ip FROM accounts WHERE login = ");
     * sb.append("(SELECT account_name FROM characters WHERE char_name = ?))");
     *
     * con = L1DatabaseFactory.get().getConnection(); pstm =
     * con.prepareStatement(sb.toString()); pstm.setString(1, player.getName());
     * rs1 = pstm.executeQuery();
     *
     * while (rs1.next()) { pstm = con.prepareStatement(
     * "SELECT char_name, level, highlevel, clanname, onlinestatus FROM characters WHERE account_name = ?"
     * ); pstm.setString(1, rs1.getString("login")); rs2 = pstm.executeQuery();
     *
     * String[] account = new String[6];
     *
     * account[0] = rs1.getString("login"); account[1] = rs1.getString("host");
     * while (rs2.next()) { account[2] = rs2.getString("char_name"); account[3]
     * = rs2.getInt("level") + " / " + rs2.getInt("highlevel"); account[4] =
     * rs2.getString("clanname"); account[5] = rs2.getInt("onlinestatus") == 0 ?
     * "X" : "O";
     *
     * tModel5.addRow(account); } } } catch (Exception e) { }
     *
     *
     * for (L1ItemInstance item : player.getInventory().getItems()) { String[]
     * items = new String[6]; items[0] = String.valueOf(item.getItemId());
     * items[1] = item.getName(); items[2] = String.valueOf(item.getCount());
     * items[3] = String.valueOf(item.getEnchantLevel()); items[4] =
     * getAttrName(item); items[5] = getStepName(item.getStepEnchantLevel());
     * tModel2.addRow(items);
     *
     * if (item.getItem().getType2() != 0) { tModel4.addRow(items); } } } } }
     * catch (Exception e) {
     *
     * } finally { SQLUtil.close(rs2); SQLUtil.close(rs1); SQLUtil.close(pstm);
     * SQLUtil.close(con); } } }
     */
    private String getStepName(int stepenchantlvl) {
        String stepname = "";
        if (stepenchantlvl > 0) {
            switch (stepenchantlvl) {
                case 1:
                case 2:
                case 3:
                    stepname = "力量" + stepenchantlvl + "階段";
                    break;
                case 4:
                case 5:
                case 6:
                    stepname = "敏捷" + (stepenchantlvl - 3) + "階段";
                    break;
                case 7:
                case 8:
                case 9:
                    stepname = "體力" + (stepenchantlvl - 6) + "階段";
                    break;
                case 10:
                case 11:
                case 12:
                    stepname = "精神" + (stepenchantlvl - 9) + "階段";
                    break;
                case 13:
                case 14:
                case 15:
                    stepname = "智力" + (stepenchantlvl - 12) + "階段";
                    break;
                case 16:
                case 17:
                case 18:
                    stepname = "魅力" + (stepenchantlvl - 15) + "階段";
                    break;
                case 19:
                case 20:
                case 21:
                    stepname = "傷害減免" + (stepenchantlvl - 18) + "階段";
                    break;
                case 22:
                case 23:
                case 24:
                    stepname = "重量減少" + (stepenchantlvl - 21) + "階段";
                    break;
                case 25:
                case 26:
                case 27:
                    stepname = "攻擊成功" + (stepenchantlvl - 24) + "階段";
                    break;
                case 28:
                case 29:
                case 30:
                    stepname = "額外攻擊" + (stepenchantlvl - 27) + "階段";
                    break;
                case 31:
                case 32:
                case 33:
                    stepname = "弓命中值" + (stepenchantlvl - 30) + "階段";
                    break;
                case 34:
                case 35:
                case 36:
                    stepname = "弓傷害值" + (stepenchantlvl - 33) + "階段";
                    break;
                default:
                    stepname = "無";
                    break;
            }
        }
        return stepname;
    }

    public synchronized DefaultTableModel getTableModel() {
        return model0;
    }

    /*
     * public synchronized DefaultListModel getListModel() { return listModel; }
     *
     * public void setListModel(DefaultListModel listModel) { this.listModel =
     * listModel; }
     */
    public void setListModel(DefaultTableModel tableModel) {
        this.model0 = tableModel;
    }

    public JLabel getLbl_UserCount() {
        return lbl_UserCount;
    }

    public void setLbl_UserCount(JLabel lbl_UserCount) {
        this.lbl_UserCount = lbl_UserCount;
    }

    private class MouseListenner extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                int row = jJTable0.getSelectedRow();
                if (row >= 0) {
                    // 取得玩家名稱
                    String playerName = (String) jJTable0.getValueAt(row, 0);
                    txt_UserName.setText(playerName);
                    CharacterInfoSearch();
                }
            }
        }
    }
}

