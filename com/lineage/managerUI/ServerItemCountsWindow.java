package com.lineage.managerUI;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 類名稱：ServerItemCountsWindow<br>
 * 類描述：物品數量監控<br>
 * 創建人:Manly<br>
 * 修改時間：2022年4月27日 下午5:37:48<br>
 * 修改備註:<br>
 *
 * @version<br>
 */
@SuppressWarnings("serial")
public class ServerItemCountsWindow extends JInternalFrame {
    /**
     * 金幣標籤
     */
    private JLabel jJLabel1 = null;
    /**
     * 金幣監控數值
     */
    private JTextField txt_Adena = null;
    /**
     * 天寶標籤
     */
    private JLabel jJLabel2 = null;
    /**
     * 天寶監控數值
     */
    private JTextField txt_Feather = null;
    /**
     * 祝福武卷標籤
     */
    private JLabel jJLabel3 = null;
    /**
     * 祝福武卷監控數值
     */
    private JTextField txt_WeaponScroll = null;
    /**
     * 祝福防卷標籤
     */
    private JLabel jJLabel4 = null;
    /**
     * 祝福防卷監控數值
     */
    private JTextField txt_ArmorScroll = null;
    /**
     * 搜索按鈕
     */
    private JButton btn_Search = null;
    /**
     * 玩家信息組件
     */
    private JTable jJTable = null;
    /**
     * 玩家信息組件滾動條
     */
    private JScrollPane pScroll = null;
    private DefaultTableModel model = null;
    /**
     * 金幣
     */
    private int ItemId1 = 40308;
    /**
     * 天寶
     */
    private int ItemId2 = 44070;
    /**
     * 祝福對武器施法的卷軸
     */
    private int ItemId3 = 140087;
    /**
     * 祝福對盔甲施法的卷軸
     */
    private int ItemId4 = 140074;

    /**
     * 物品數量監控組件參數
     *
     * @param windowName 組件名稱
     * @param x          界面位置x坐標
     * @param y          界面位置y坐標
     * @param width      寬度
     * @param height     高度
     * @param resizable  true：顯示按鈕 false：不顯示或者隱藏按鈕
     * @param closable
     */
    public ServerItemCountsWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
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
        // 更新UI界面
        updateUI();
        // 標籤組件
        jJLabel1 = new JLabel("金幣最大數量");
        jJLabel2 = new JLabel("天寶最大數量");
        jJLabel3 = new JLabel("祝福武卷最大數量");
        jJLabel4 = new JLabel("祝福防卷最大數量");
        // 標籤數值組件
        txt_Adena = new JTextField();
        txt_Adena.setText("1000000000");
        txt_Feather = new JTextField();
        txt_Feather.setText("50000");
        txt_WeaponScroll = new JTextField();
        txt_WeaponScroll.setText("100");
        txt_ArmorScroll = new JTextField();
        txt_ArmorScroll.setText("100");
        btn_Search = new JButton("搜索");
        btn_Search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Eva.isServerStarted) {
                    dataSearch();
                } else {
                    Eva.errorMsg(Eva.NoServerStartMSG);
                }
            }
        });
        String[] modelColName = {"賬號", "角色名", "連接狀態", "金幣", "天寶", "祝福武卷", "祝福防卷"};
        model = new DefaultTableModel(modelColName, 0);
        jJTable = new JTable(model);
        jJTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        jJTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        jJTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        jJTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        jJTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        jJTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        jJTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        jJTable.getColumn("連接狀態").setCellRenderer(new LabelRowCellEdior("連接狀態"));
        jJTable.getColumn("金幣").setCellRenderer(new LabelRowCellEdior("金幣"));
        jJTable.getColumn("天寶").setCellRenderer(new LabelRowCellEdior("天寶"));
        jJTable.getColumn("祝福武卷").setCellRenderer(new LabelRowCellEdior("祝福武卷"));
        jJTable.getColumn("祝福防卷").setCellRenderer(new LabelRowCellEdior("祝福防卷"));
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
        GroupLayout.ParallelGroup col5 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        main.addGroup(horizontal_grp);
        main_horizontal_grp.addGroup(main);
        layout.setHorizontalGroup(main_horizontal_grp);
        layout.setVerticalGroup(vertical_grp);
        col1.addComponent(jJLabel1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addComponent(jJLabel3, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
        col2.addComponent(txt_Adena, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addComponent(txt_WeaponScroll, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
        col3.addComponent(jJLabel2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addComponent(jJLabel4, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
        col4.addComponent(txt_Feather, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE).addComponent(txt_ArmorScroll, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
        col5.addComponent(btn_Search);
        horizontal_grp.addContainerGap().addGap(5).addGroup(col1).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col2).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col3).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col4).addContainerGap();
        horizontal_grp.addContainerGap().addGap(5).addGroup(col5).addContainerGap();
        main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel1).addComponent(txt_Adena).addComponent(jJLabel2).addComponent(txt_Feather));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel3).addComponent(txt_WeaponScroll).addComponent(jJLabel4).addComponent(txt_ArmorScroll).addComponent(btn_Search));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(pScroll));
        // 自定義圖片
        Icon lbl_Note_icon = new ImageIcon("img/ConfigNote.png");
        // 特別提醒
        JLabel lbl_Note = new JLabel("預設數值可以改動.比如金幣監控數量改成100,那麼玩家金幣數量超過100就會出現粉紅色.這裡並不會影響玩家背包數量,只是方便查看!!!", lbl_Note_icon, JLabel.CENTER);
        // 設定字體為紅色
        lbl_Note.setForeground(Color.RED);
        main.addGroup(layout.createSequentialGroup().addComponent(lbl_Note));
        vertical_grp.addGroup(layout.createParallelGroup().addComponent(lbl_Note));
        // 自定義圖片
        Icon Author_icon = new ImageIcon("img/Author.png");
        // 作者信息
        JLabel lbl_Author = new JLabel("LINE:#####  by:老爹 ", Author_icon, JLabel.CENTER);
        // 設定字體為藍色
        lbl_Author.setForeground(Color.blue);
        main.addGroup(layout.createSequentialGroup().addComponent(lbl_Author));
        vertical_grp.addGroup(layout.createParallelGroup().addComponent(lbl_Author));
    }

    /**
     * 搜索數據庫信息
     */
    private void dataSearch() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT a.objid,                 								   ");
            sb.append("        a.char_name,             								   ");
            sb.append("        a.account_name,         									   ");
            sb.append("        a.level,                								       ");
            sb.append("        a.onlinestatus,         								       ");
            sb.append("       (SELECT locationname      								   ");
            sb.append("          FROM mapids     								           ");
            sb.append("         WHERE mapid = a.mapid) AS location,                        ");
            sb.append("        b.item_id,               								   ");
            sb.append("        b.item_name,            			 						   ");
            sb.append("        b.count                                                     ");
            sb.append("   FROM (SELECT account_name,                                       ");
            sb.append("                objid,                                              ");
            sb.append("                char_name,                                          ");
            sb.append("                level,						                       ");
            sb.append("                locx,						                       ");
            sb.append("                locy,						                       ");
            sb.append("                mapid,						                       ");
            sb.append("                onlinestatus					                       ");
            sb.append("           FROM characters) a,					                   ");
            sb.append("         (SELECT char_id,						                   ");
            sb.append("                 item_id,						                   ");
            sb.append("                 item_name,					                       ");
            sb.append("                 count						                       ");
            sb.append("            FROM character_items					                   ");
            sb.append("           WHERE item_id in (40308, '44070', '140087', '140074')) b ");
            sb.append("  WHERE a.objid = b.char_id					                       ");
            sb.append("ORDER BY a.account_name, a.level, a.char_name, b.item_id			   ");
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement(sb.toString());
            rs = pstm.executeQuery();
            DefaultTableModel tModel = (DefaultTableModel) jJTable.getModel();
            Object[] moniter = new Object[7];
            String account = "";
            String charName = "";
            for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
                tModel.removeRow(i);
            }
            for (int i = 0; i < moniter.length; i++) {
                moniter[i] = 0;
            }
            int count = 0;
            while (rs.next()) {
                if (account != rs.getString("account_name")) {
                    account = rs.getString("account_name");
                    moniter[0] = rs.getString("account_name");
                }
                if (charName.equalsIgnoreCase(rs.getString("char_name"))) {
                    if (rs.getInt("item_id") == ItemId1) {
                        moniter[3] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                    if (rs.getInt("item_id") == ItemId2) {
                        moniter[4] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                    if (rs.getInt("item_id") == ItemId3) {
                        moniter[5] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                    if (rs.getInt("item_id") == ItemId4) {
                        moniter[6] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                } else {
                    if (count != 0) {
                        tModel.addRow(moniter);
                        for (int i = 0; i < moniter.length; i++) {
                            moniter[i] = 0;
                        }
                    }
                    charName = rs.getString("char_name");
                    moniter[1] = rs.getString("char_name");
                    moniter[2] = rs.getInt("onlinestatus") == 0 ? "離線" : "在線";
                    if (rs.getInt("item_id") == ItemId1) {
                        moniter[3] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                    if (rs.getInt("item_id") == ItemId2) {
                        moniter[4] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                    if (rs.getInt("item_id") == ItemId3) {
                        moniter[5] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                    if (rs.getInt("item_id") == ItemId4) {
                        moniter[6] = CommonUtil.numberFormat(rs.getInt("count"));
                    }
                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 類名稱：LabelRowCellEdior<br>
     * 類描述：行的單元格編輯標籤<br>
     * 修改時間：2022年4月27日 下午5:06:52<br>
     * 修改備註:<br>
     *
     * @version<br>
     */
    private class LabelRowCellEdior extends JLabel implements TableCellRenderer {
        private String columnName;

        public LabelRowCellEdior(String column) {
            this.columnName = column;
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                if (columnName.equals("連接狀態")) {
                    if (String.valueOf(value).equalsIgnoreCase("在線")) {
                        setBackground(Color.green);
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                } else if (columnName.equals("金幣") && Integer.parseInt(String.valueOf(value).replaceAll(",", "")) > Integer.parseInt(txt_Adena.getText())) {
                    setBackground(Color.pink);
                    setHorizontalAlignment(JLabel.RIGHT);
                } else if (columnName.equals("天寶") && Integer.parseInt(String.valueOf(value).replaceAll(",", "")) > Integer.parseInt(txt_Feather.getText())) {
                    setBackground(Color.pink);
                    setHorizontalAlignment(JLabel.RIGHT);
                } else if (columnName.equals("祝福武卷") && Integer.parseInt(String.valueOf(value).replaceAll(",", "")) > Integer.parseInt(txt_WeaponScroll.getText())) {
                    setBackground(Color.pink);
                    setHorizontalAlignment(JLabel.RIGHT);
                } else if (columnName.equals("祝福防卷") && Integer.parseInt(String.valueOf(value).replaceAll(",", "")) > Integer.parseInt(txt_ArmorScroll.getText())) {
                    setBackground(Color.pink);
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    setHorizontalAlignment(JLabel.RIGHT);
                }
            }
            setText(String.valueOf(value));
            return this;
        }
    }
}
