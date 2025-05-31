package com.lineage.managerUI;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeShape;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("serial")
public class ServerPolyWindow extends JInternalFrame {
    public JTextField txt_UserName = null;
    private JLabel jJLabel1 = null;
    private JLabel jJLabel2 = null;
    private JLabel jJLabel3 = null;
    private JLabel jJLabel4 = null;
    private JTextField txt_PolyId = null;
    private JTextField txt_PolyName = null;
    private JTextField txt_PolyNameSearch = null;
    private JButton btn_Search = null;
    private JButton btn_Poly = null;
    private JTable jJTable = null;
    private JScrollPane pScroll = null;
    private DefaultTableModel model = null;

    public ServerPolyWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
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
        jJLabel1 = new JLabel("角色名");
        jJLabel2 = new JLabel("編號");
        jJLabel3 = new JLabel("名稱");
        jJLabel4 = new JLabel("搜索名稱");
        txt_UserName = new JTextField();
        txt_UserName.setEditable(false);
        txt_PolyId = new JTextField();
        txt_PolyName = new JTextField();
        txt_PolyNameSearch = new JTextField();
        txt_PolyNameSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chatKeyPressed(evt);
            }
        });
        btn_Search = new JButton("搜索");
        btn_Search.addActionListener(e -> {
            if (Eva.isServerStarted) {
                try {
                    DefaultTableModel tModel = (DefaultTableModel) jJTable.getModel();
                    for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
                        tModel.removeRow(i);
                    }
                    String[] poly = new String[2];
                    int polyId = 0;
                    Connection con = null;
                    PreparedStatement pstm = null;
                    ResultSet rs = null;
                    try {
                        polyId = Integer.parseInt(txt_PolyNameSearch.getText());
                        con = DatabaseFactory.get().getConnection();
                        pstm = con.prepareStatement("SELECT * FROM polymorphs where polyid = ?");
                        pstm.setInt(1, polyId);
                        rs = pstm.executeQuery();
                        while (rs.next()) {
                            poly[0] = String.valueOf(rs.getInt("polyid"));
                            poly[1] = rs.getString("name");
                            model.addRow(poly);
                        }
                    } catch (Exception ex) {
                        con = DatabaseFactory.get().getConnection();
                        pstm = con.prepareStatement("SELECT * FROM polymorphs where name = ?");
                        pstm.setString(1, txt_PolyNameSearch.getText());
                        rs = pstm.executeQuery();
                        while (rs.next()) {
                            poly[0] = String.valueOf(rs.getInt("polyid"));
                            poly[1] = rs.getString("name");
                            model.addRow(poly);
                        }
                    } finally {
                        SQLUtil.close(rs);
                        SQLUtil.close(pstm);
                        SQLUtil.close(con);
                    }
                } catch (Exception ex) {
                }
            } else {
                Eva.errorMsg(Eva.NoServerStartMSG);
            }
        });
        btn_Poly = new JButton("變身");
        btn_Poly.addActionListener(e -> {
            if (Eva.isServerStarted) {
                try {
                    String userName = txt_UserName.getText().trim();
                    String idText = txt_PolyId.getText().trim();

                    if (userName.isEmpty()) {
                        Eva.errorMsg(Eva.blankSetUser);
                        return;
                    }
                    if (idText.isEmpty()) {
                        Eva.errorMsg("請輸入變身怪物ID！");
                        return;
                    }

                    int polyId;
                    try {
                        polyId = Integer.parseInt(idText);
                    } catch (NumberFormatException ex) {
                        Eva.errorMsg("怪物ID必須是數字！");
                        return;
                    }

                    boolean isAll = userName.equalsIgnoreCase("全體用戶");
                    if (isAll) {
                        int count = 0;
                        for (L1PcInstance pc : World.get().getAllPlayers()) {
                            if (pc == null || pc.getNetConnection() == null || pc.isPrivateShop()) continue;
                            pc.setTempCharGfx(polyId);
                            pc.sendPackets(new S_ChangeShape(pc, polyId));
                            World.get().broadcastPacketToAll(new S_ChangeShape(pc, polyId));
                            pc.sendPackets(new S_SystemMessage("受到管理員的變身。"));
                            count++;
                        }
                        Eva.LogServerAppend("已全體變身為怪物ID:" + polyId + "，共 " + count + " 人", "全體變身成功");
                    } else {
                        L1PcInstance pc = World.get().getPlayer(userName);
                        if (pc != null && pc.getNetConnection() != null) {
                            pc.setTempCharGfx(polyId);
                            pc.sendPackets(new S_ChangeShape(pc, polyId));
                            pc.sendPackets(new S_SystemMessage("受到管理員的變身。")); // 變身後提示訊息
                            World.get().broadcastPacketToAll(new S_ChangeShape(pc, polyId));
                            Eva.LogServerAppend(pc.getName() + " 變身為怪物ID:" + polyId, "變身成功");
                        } else {
                            Eva.errorMsg("指定角色不存在或未在線");
                        }
                    }
                } catch (Exception ex) {
                    Eva.errorMsg("發生未知錯誤，請檢查輸入或伺服器狀態！");
                }
            } else {
                Eva.errorMsg(Eva.NoServerStartMSG);
            }
        });


        String[] modelColName = {"編號", "名稱"};
        model = new DefaultTableModel(modelColName, 0);
        jJTable = new JTable(model);
        jJTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        jJTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        jJTable.addMouseListener(new MouseListenner());
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM polymorphs");
            rs = pstm.executeQuery();
            String[] poly = new String[2];
            while (rs.next()) {
                poly[0] = String.valueOf(rs.getInt("polyid"));
                poly[1] = rs.getString("name");
                model.addRow(poly);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        pScroll = new JScrollPane(jJTable);
        pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pScroll.setAutoscrolls(true);
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
        col1.addComponent(jJLabel1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(jJLabel4, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(jJLabel2, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE);
        col2.addComponent(txt_UserName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addComponent(txt_PolyNameSearch, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addComponent(txt_PolyId, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
        col3.addComponent(btn_Search, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(jJLabel3, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE);
        col4.addComponent(txt_PolyName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
        horizontal_grp.addContainerGap().addGap(5).addGroup(col1).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col2).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col3).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col4).addContainerGap();
        main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
        main.addGroup(layout.createSequentialGroup().addGap(150, 150, 150).addComponent(btn_Poly, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel1).addComponent(txt_UserName));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel4).addComponent(txt_PolyNameSearch).addComponent(btn_Search));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(pScroll));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel2).addComponent(txt_PolyId).addComponent(jJLabel3).addComponent(txt_PolyName));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addGap(19, 19, 19).addComponent(btn_Poly)).addGap(5);
    }

    private void chatKeyPressed(KeyEvent evt) {
        // 서버 채팅
        if (Eva.isServerStarted) {
            try {
                DefaultTableModel tModel = (DefaultTableModel) jJTable.getModel();
                for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
                    tModel.removeRow(i);
                }
                String[] poly = new String[2];
                int polyId = 0;
                Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                try {
                    polyId = Integer.parseInt(txt_PolyNameSearch.getText());
                    con = DatabaseFactory.get().getConnection();
                    pstm = con.prepareStatement("SELECT * FROM polymorphs where polyid = ?");
                    pstm.setInt(1, polyId);
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        poly[0] = String.valueOf(rs.getInt("polyid"));
                        poly[1] = rs.getString("name");
                        model.addRow(poly);
                    }
                } catch (Exception ex) {
                    con = DatabaseFactory.get().getConnection();
                    pstm = con.prepareStatement("SELECT * FROM polymorphs where name = ?");
                    pstm.setString(1, txt_PolyNameSearch.getText());
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        poly[0] = String.valueOf(rs.getInt("polyid"));
                        poly[1] = rs.getString("name");
                        model.addRow(poly);
                    }
                } finally {
                    SQLUtil.close(rs);
                    SQLUtil.close(pstm);
                    SQLUtil.close(con);
                }
            } catch (Exception ex) {
            }
        } else {
            Eva.errorMsg(Eva.NoServerStartMSG);
        }
    }

    private class MouseListenner extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1) {
                int column = ((JTable) e.getSource()).getSelectedColumn();
                int row = ((JTable) e.getSource()).getSelectedRow();
                txt_PolyId.setText((String) ((JTable) e.getSource()).getValueAt(row, 0));
                txt_PolyName.setText((String) ((JTable) e.getSource()).getValueAt(row, 1));
            }
        }
    }
}
