package com.eric.gui;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.ConfigRate;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.clientpackets.C_LoginToServer;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.world.World;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class J_Main extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static J_Main instance;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    ImageIcon img = new ImageIcon("img/icon.png");
    private int select = 0;
    private DefaultTableModel DTM = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    };
    private DefaultTableModel DTM_Item = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    };
    private JButton B_Item;
    private JButton B_Submit;
    private JComboBox<?> CB_Channel;
    @SuppressWarnings("rawtypes")
    private JComboBox CB_Item;
    private JDialog D_Item;
    private JFrame F_Player;
    private JLabel L_AccessLevel;
    private JLabel L_AccessLevel7;
    private JLabel L_Account;
    private JLabel L_Cha;
    private JLabel L_Clan;
    private JLabel L_Con;
    private JLabel L_Dex;
    private JLabel L_Exp;
    private JLabel L_Hp;
    private JLabel L_Image;
    private JLabel L_Int;
    private JLabel L_Leavl;
    private JLabel L_Map;
    private JLabel L_Mp;
    private JLabel L_Mp1;
    private JLabel L_Name;
    private JLabel L_Str;
    private JLabel L_Title;
    private JLabel L_Wis;
    private JLabel L_X;
    private JLabel L_Y;
    private JMenuBar MB;
    private JMenuItem MI_LA;
    private JMenuItem MI_ATTR_ENCHANT_CHANCE;
    private JMenuItem MI_CHANCE_ARMOR;
    private JMenuItem MI_CHANCE_WEAPON;
    private JMenuItem MI_XP_PET;
    private JMenuItem MI_Exp;
    private JMenuItem MI_Drop;
    private JMenuItem MI_Adena;
    private JMenuItem MI_AllBuff;
    private JMenuItem MI_AllRess;
    private JMenuItem MI_Angel;
    private JMenuItem MI_BanIP;
    private JMenuItem MI_Close;
    private JMenuItem MI_Kill;
    private JMenuItem MI_Save;
    private JMenuItem MI_SetClose;
    private JMenuItem MI_ShowPlayer;
    private JMenuItem MI_Whisper;
    private JMenu M_Edit;
    private JMenu M_File;
    private JMenu M_Special;
    private JPopupMenu PM_Player;
    private JScrollPane SP_;
    private JScrollPane SP_AllChat;
    private JScrollPane SP_Clan;
    private JScrollPane SP_Consol;
    private JScrollPane SP_Normal;
    private JSplitPane SP_Split;
    private JScrollPane SP_Team;
    private JScrollPane SP_World;
    private JScrollPane SP_player;
    private JTextArea TA_AllChat;
    private JTextArea TA_Clan;
    private JTextArea TA_Consol;
    private JTextArea TA_Normal;
    private JTextArea TA_Private;
    private JTextArea TA_Team;
    private JTextArea TA_World;
    private JTextField TF_Ac;
    private JTextField TF_AccessLevel;
    private JTextField TF_Account;
    private JTextField TF_Cha;
    private JTextField TF_Clan;
    private JTextField TF_Con;
    private JTextField TF_Dex;
    private JTextField TF_Exp;
    private JTextField TF_Hp;
    private JTextField TF_Int;
    private JTextField TF_Level;
    private JTextField TF_Map;
    private JTextField TF_Mp;
    private JTextField TF_Msg;
    private JTextField TF_Name;
    private JTextField TF_Sex;
    private JTextField TF_Str;
    private JTextField TF_Target;
    private JTextField TF_Title;
    private JTextField TF_Wis;
    private JTextField TF_X;
    private JTextField TF_Y;
    private JTabbedPane TP;
    private JTable T_Item;
    private JTable T_Player;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;

    public J_Main() {
        iniPlayerTable();
        initComponents();
        TA_Consol.setForeground(new Color(150, 205, 205));
        TA_AllChat.setForeground(new Color(150, 205, 205));
        TA_Clan.setForeground(new Color(150, 205, 205));
        TA_Normal.setForeground(new Color(150, 205, 205));
        TA_Private.setForeground(new Color(150, 205, 205));
        TA_Team.setForeground(new Color(150, 205, 205));
        TA_World.setForeground(new Color(150, 205, 205));
        TA_Consol.setBackground(new Color(0, 0, 120));
        TA_AllChat.setBackground(new Color(0, 0, 120));
        TA_Clan.setBackground(new Color(0, 0, 120));
        TA_Normal.setBackground(new Color(0, 0, 120));
        TA_Private.setBackground(new Color(0, 0, 120));
        TA_Team.setBackground(new Color(0, 0, 120));
        TA_World.setBackground(new Color(0, 0, 120));
        setIconImage(img.getImage());
        iniAction();
        T_Item.setSize(300, 400);
        D_Item.pack();
        String[] s = {"物品名稱", "物品數量", "物品ID"};
        DTM_Item.setColumnIdentifiers(s);
    }

    public static J_Main getInstance() {
        if (instance == null) {
            instance = new J_Main();
        }
        return instance;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new J_Main().setVisible(true);
            }
        });
    }

    private void iniAction() {
        MI_Kill.addActionListener(this);
        MI_BanIP.addActionListener(this);
        MI_ShowPlayer.addActionListener(this);
        MI_Whisper.addActionListener(this);
        MI_Save.addActionListener(this);
        MI_Close.addActionListener(this);
        MI_Angel.addActionListener(this);
        MI_SetClose.addActionListener(this);
        MI_AllBuff.addActionListener(this);
        MI_AllRess.addActionListener(this);
        MI_XP_PET.addActionListener(this);
        MI_CHANCE_WEAPON.addActionListener(this);
        MI_CHANCE_ARMOR.addActionListener(this);
        MI_ATTR_ENCHANT_CHANCE.addActionListener(this);
        MI_LA.addActionListener(this);
        MI_Adena.addActionListener(this);
        MI_Exp.addActionListener(this);
        MI_Drop.addActionListener(this);
    }

    public void addWorldChat(String from, String text) {
        Calendar cal = Calendar.getInstance();
        AllChat(sdf.format(cal.getTime()) + "公頻『" + from + "』:" + text + "\r\n");
        TA_World.append(from + " : " + text + "\r\n");
        TA_World.setCaretPosition(TA_World.getDocument().getLength());
    }

    public void addClanChat(String from, String text) {
        Calendar cal = Calendar.getInstance();
        AllChat(sdf.format(cal.getTime()) + "血盟『" + from + "』:" + text + "\r\n");
        TA_Clan.append(from + " : " + text + "\r\n");
        TA_Clan.setCaretPosition(TA_Clan.getDocument().getLength());
    }

    public void addNormalChat(String from, String text) {
        Calendar cal = Calendar.getInstance();
        AllChat(sdf.format(cal.getTime()) + "一般『" + from + "』:" + text + "\r\n");
        TA_Normal.append(from + " : " + text + "\r\n");
        TA_Normal.setCaretPosition(TA_Normal.getDocument().getLength());
    }

    public void addTeamChat(String from, String text) {
        Calendar cal = Calendar.getInstance();
        AllChat(sdf.format(cal.getTime()) + "隊伍『" + from + "』:" + text + "\r\n");
        TA_Team.append(from + " : " + text + "\r\n");
        TA_Team.setCaretPosition(TA_Team.getDocument().getLength());
    }

    public void addConsol(String text) {
        TA_Consol.append(text + "\r\n");
        TA_Consol.setCaretPosition(TA_Consol.getDocument().getLength());
    }

    public void addConsolPost(String text) {
        TA_Consol.append(text + "\r\n");
        TA_Consol.setCaretPosition(TA_Consol.getDocument().getLength());
    }

    public void addConsolNoLR(String text) {
        TA_Consol.append(text);
        TA_Consol.setCaretPosition(TA_Consol.getDocument().getLength());
    }

    public void AllChat(String text) {
        TA_AllChat.append(text + "\r\n");
        TA_AllChat.setCaretPosition(TA_AllChat.getDocument().getLength());
    }

    public void addPrivateChat(String from, String to, String text) {
        Calendar cal = Calendar.getInstance();
        AllChat(sdf.format(cal.getTime()) + "密語『" + from + "->" + to + "』:" + text + "\r\n");
        TA_Private.append(from + "->" + to + " : " + text + "\r\n");
        TA_Private.setCaretPosition(TA_Private.getDocument().getLength());
    }

    public void addItemTable(String itemname, long l, long id) {
        Object[] o = {itemname, Long.valueOf(l), Long.valueOf(id)};
        DTM_Item.addRow(o);
    }

    public void iniTable() {
        int cont = DTM_Item.getRowCount();
        while (cont > 1) {
            DTM_Item.removeRow(cont - 1);
            cont--;
        }
    }

    public void addPlayerTable(String account, String name, StringBuilder IP) {
        Object[] o = {account, name, IP};
        DTM.addRow(o);
    }

    private int findPlayer(String name) {
        try {
            for (int j = 0; j < DTM.getRowCount(); j++) {
                if (name.equals(DTM.getValueAt(j, 1).toString())) {
                    return j;
                }
            }
            return -1;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void delPlayerTable(String name) {
        int findNum = 0;
        if ((findNum = findPlayer(name)) != -1) {
            DTM.removeRow(findNum);
        }
    }

    private void iniPlayerTable() {
        String[] s = {"帳號", "角色名稱", "IP"};
        DTM.setColumnIdentifiers(s);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initComponents() {
        F_Player = new JFrame();
        L_Name = new JLabel();
        L_Title = new JLabel();
        L_Account = new JLabel();
        L_Leavl = new JLabel();
        L_AccessLevel = new JLabel();
        L_Exp = new JLabel();
        L_Hp = new JLabel();
        L_Mp = new JLabel();
        L_Int = new JLabel();
        L_Str = new JLabel();
        L_Con = new JLabel();
        L_Dex = new JLabel();
        L_Wis = new JLabel();
        L_Cha = new JLabel();
        jPanel1 = new JPanel();
        L_Image = new JLabel();
        L_Clan = new JLabel();
        L_AccessLevel7 = new JLabel();
        L_Mp1 = new JLabel();
        L_Map = new JLabel();
        L_X = new JLabel();
        L_Y = new JLabel();
        TF_Account = new JTextField();
        TF_Name = new JTextField();
        TF_Title = new JTextField();
        TF_Level = new JTextField();
        TF_AccessLevel = new JTextField();
        TF_Clan = new JTextField();
        TF_Exp = new JTextField();
        TF_Hp = new JTextField();
        TF_Mp = new JTextField();
        TF_Sex = new JTextField();
        TF_Str = new JTextField();
        TF_Con = new JTextField();
        TF_Dex = new JTextField();
        TF_Wis = new JTextField();
        TF_Int = new JTextField();
        TF_Cha = new JTextField();
        TF_Ac = new JTextField();
        TF_Map = new JTextField();
        TF_X = new JTextField();
        TF_Y = new JTextField();
        B_Item = new JButton();
        CB_Item = new JComboBox();
        PM_Player = new JPopupMenu();
        MI_Kill = new JMenuItem();
        MI_BanIP = new JMenuItem();
        jSeparator1 = new JSeparator();
        MI_ShowPlayer = new JMenuItem();
        jSeparator2 = new JSeparator();
        MI_Whisper = new JMenuItem();
        jLabel1 = new JLabel();
        D_Item = new JDialog();
        jScrollPane1 = new JScrollPane();
        T_Item = new JTable(DTM_Item);
        SP_Split = new JSplitPane();
        TP = new JTabbedPane();
        SP_Consol = new JScrollPane();
        TA_Consol = new JTextArea();
        SP_AllChat = new JScrollPane();
        TA_AllChat = new JTextArea();
        SP_World = new JScrollPane();
        TA_World = new JTextArea();
        SP_Normal = new JScrollPane();
        TA_Normal = new JTextArea();
        SP_ = new JScrollPane();
        TA_Private = new JTextArea();
        SP_Clan = new JScrollPane();
        TA_Clan = new JTextArea();
        SP_Team = new JScrollPane();
        TA_Team = new JTextArea();
        SP_player = new JScrollPane();
        T_Player = new JTable(DTM);
        jPanel2 = new JPanel();
        CB_Channel = new JComboBox();
        TF_Target = new JTextField();
        B_Submit = new JButton();
        TF_Msg = new JTextField();
        MB = new JMenuBar();
        M_File = new JMenu();
        MI_Save = new JMenuItem();
        jSeparator3 = new JSeparator();
        MI_SetClose = new JMenuItem();
        MI_Close = new JMenuItem();
        M_Edit = new JMenu();
        M_Special = new JMenu();
        MI_LA = new JMenuItem();
        MI_ATTR_ENCHANT_CHANCE = new JMenuItem();
        MI_CHANCE_ARMOR = new JMenuItem();
        MI_CHANCE_WEAPON = new JMenuItem();
        MI_XP_PET = new JMenuItem();
        MI_Exp = new JMenuItem();
        MI_Drop = new JMenuItem();
        MI_Adena = new JMenuItem();
        MI_Angel = new JMenuItem();
        MI_AllBuff = new JMenuItem();
        MI_AllRess = new JMenuItem();
        L_Name.setText("名字:");
        L_Title.setText("稱號:");
        L_Account.setText("帳號:");
        L_Leavl.setText("等級:");
        L_AccessLevel.setText("權限:");
        L_Exp.setText(" Exp:");
        L_Hp.setText("Hp:");
        L_Mp.setText("Mp:");
        L_Int.setText("智力:");
        L_Str.setText("力量:");
        L_Con.setText("體質:");
        L_Dex.setText("敏捷:");
        L_Wis.setText("精神:");
        L_Cha.setText("魅力:");
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(L_Image, -1, 108, 32767).addContainerGap()));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(L_Image, -1, 180, 32767).addContainerGap()));
        L_Clan.setText("血盟:");
        L_AccessLevel7.setText("防禦力:");
        L_Mp1.setText("性別:");
        L_Map.setText("Map:");
        L_X.setText("X:");
        L_Y.setText("Y:");
        TF_Account.setEditable(false);
        TF_Name.setEditable(false);
        TF_Title.setEditable(false);
        TF_Level.setEditable(false);
        TF_AccessLevel.setEditable(false);
        TF_Clan.setEditable(false);
        TF_Exp.setEditable(false);
        TF_Hp.setEditable(false);
        TF_Mp.setEditable(false);
        TF_Sex.setEditable(false);
        TF_Str.setEditable(false);
        TF_Con.setEditable(false);
        TF_Dex.setEditable(false);
        TF_Wis.setEditable(false);
        TF_Int.setEditable(false);
        TF_Cha.setEditable(false);
        TF_Ac.setEditable(false);
        TF_Map.setEditable(false);
        TF_X.setEditable(false);
        TF_Y.setEditable(false);
        B_Item.setText("物品欄顯示");
        B_Item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                J_Main.this.B_ItemActionPerformed(evt);
            }
        });
        CB_Item.setModel(new DefaultComboBoxModel(new String[]{"0,身上物品", "1,倉庫", "2,血盟倉庫", "3,妖森倉庫"}));
        GroupLayout F_PlayerLayout = new GroupLayout(F_Player.getContentPane());
        F_Player.getContentPane().setLayout(F_PlayerLayout);
        F_PlayerLayout.setHorizontalGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(F_PlayerLayout.createSequentialGroup().addComponent(jPanel1, -2, -1, -2).addGap(18, 18, 18).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(F_PlayerLayout.createSequentialGroup().addComponent(L_Account).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(TF_Account, -2, 108, -2)).addGroup(F_PlayerLayout.createSequentialGroup().addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(L_Name).addComponent(L_Title).addComponent(L_Leavl).addComponent(L_AccessLevel).addComponent(L_Clan).addComponent(L_Exp, GroupLayout.Alignment.TRAILING, -1, 27, 32767).addComponent(L_Hp, GroupLayout.Alignment.TRAILING).addComponent(L_Mp, GroupLayout.Alignment.TRAILING).addComponent(L_Mp1, GroupLayout.Alignment.TRAILING)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(TF_Mp, -1, 108, 32767).addComponent(TF_Sex, -1, 108, 32767).addComponent(TF_Hp, -1, 108, 32767).addComponent(TF_Exp, -1, 108, 32767).addComponent(TF_Clan, -1, 108, 32767).addComponent(TF_AccessLevel, -1, 108, 32767).addComponent(TF_Level, -1, 108, 32767).addComponent(TF_Title, -1, 108, 32767).addComponent(TF_Name, -1, 108, 32767).addComponent(CB_Item, 0, -1, 32767)))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(F_PlayerLayout.createSequentialGroup().addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(L_Int).addComponent(L_Wis).addComponent(L_Dex).addComponent(L_Cha).addComponent(L_AccessLevel7).addComponent(L_Con).addComponent(L_Str).addComponent(L_Map).addComponent(L_X)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(TF_Str, -2, 108, -2).addComponent(TF_Con, -2, 108, -2).addComponent(TF_Dex, -2, 108, -2).addComponent(TF_Wis, -2, 108, -2).addComponent(TF_Int, -2, 108, -2).addComponent(TF_Cha, -2, 108, -2).addComponent(TF_Ac, -2, 108, -2).addComponent(TF_Map, -2, 108, -2).addComponent(TF_X, -2, 108, -2))).addGroup(F_PlayerLayout.createSequentialGroup().addComponent(L_Y).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(TF_Y, -2, 108, -2))).addComponent(B_Item)).addContainerGap(52, 32767)));
        F_PlayerLayout.setVerticalGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(F_PlayerLayout.createSequentialGroup().addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(F_PlayerLayout.createSequentialGroup().addContainerGap().addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Account).addComponent(TF_Account, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Name).addComponent(TF_Name, -2, 18, -2)).addGap(5, 5, 5).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Title).addComponent(TF_Title, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Leavl).addComponent(TF_Level, -2, 18, -2)).addGap(5, 5, 5).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_AccessLevel).addComponent(TF_AccessLevel, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Clan).addComponent(TF_Clan, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Exp).addComponent(TF_Exp, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Hp).addComponent(TF_Hp, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Mp).addComponent(TF_Mp, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Mp1).addComponent(TF_Sex, -2, 18, -2).addComponent(L_Y).addComponent(TF_Y, -2, 18, -2))).addGroup(F_PlayerLayout.createSequentialGroup().addGap(26, 26, 26).addComponent(jPanel1, -2, -1, -2)).addGroup(F_PlayerLayout.createSequentialGroup().addContainerGap().addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Str).addComponent(TF_Str, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Con).addComponent(TF_Con, -2, 18, -2)).addGap(5, 5, 5).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Dex).addComponent(TF_Dex, -2, 18, -2)).addGap(5, 5, 5).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Wis).addComponent(TF_Wis, -2, 18, -2)).addGap(5, 5, 5).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Int).addComponent(TF_Int, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Cha).addComponent(TF_Cha, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_AccessLevel7).addComponent(TF_Ac, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_Map).addComponent(TF_Map, -2, 18, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(L_X).addComponent(TF_X, -2, 18, -2)))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(F_PlayerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(CB_Item, -2, -1, -2).addComponent(B_Item)).addContainerGap(27, 32767)));
        MI_Kill.setMnemonic('K');
        MI_Kill.setText("強制踢除(K)");
        PM_Player.add(MI_Kill);
        MI_BanIP.setMnemonic('B');
        MI_BanIP.setText("封鎖IP(B)");
        PM_Player.add(MI_BanIP);
        PM_Player.add(jSeparator1);
        MI_ShowPlayer.setMnemonic('P');
        MI_ShowPlayer.setText("玩家資料(P)");
        PM_Player.add(MI_ShowPlayer);
        PM_Player.add(jSeparator2);
        MI_Whisper.setMnemonic('W');
        MI_Whisper.setText("密語(W)");
        PM_Player.add(MI_Whisper);
        jLabel1.setText("jLabel1");
        D_Item.getContentPane().setLayout(new GridLayout(1, 0));
        jScrollPane1.setViewportView(T_Item);
        D_Item.getContentPane().add(jScrollPane1);
        setDefaultCloseOperation(3);
        setTitle("天堂管理介面");
        setLocationByPlatform(true);
        setMinimumSize(new Dimension(1024, 768));
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent evt) {
                J_Main.this.formWindowClosed(evt);
            }
        });
        SP_Split.setDividerLocation(550);
        SP_Consol.setAutoscrolls(true);
        TA_Consol.setBackground(new Color(174, 238, 238));
        TA_Consol.setColumns(20);
        TA_Consol.setEditable(false);
        TA_Consol.setForeground(new Color(174, 238, 238));
        TA_Consol.setRows(5);
        TA_Consol.setEnabled(false);
        SP_Consol.setViewportView(TA_Consol);
        TP.addTab("管理器", SP_Consol);
        SP_AllChat.setAutoscrolls(true);
        TA_AllChat.setBackground(new Color(174, 238, 238));
        TA_AllChat.setColumns(20);
        TA_AllChat.setEditable(false);
        TA_AllChat.setForeground(new Color(174, 238, 238));
        TA_AllChat.setRows(5);
        SP_AllChat.setViewportView(TA_AllChat);
        TP.addTab("全部頻道", SP_AllChat);
        SP_World.setAutoscrolls(true);
        TA_World.setColumns(20);
        TA_World.setEditable(false);
        TA_World.setForeground(new Color(174, 238, 238));
        TA_World.setRows(5);
        TA_World.setEnabled(false);
        SP_World.setViewportView(TA_World);
        TP.addTab("世界 ", SP_World);
        SP_Normal.setAutoscrolls(true);
        TA_Normal.setColumns(20);
        TA_Normal.setEditable(false);
        TA_Normal.setRows(5);
        TA_Normal.setEnabled(false);
        SP_Normal.setViewportView(TA_Normal);
        TP.addTab("一般", SP_Normal);
        SP_.setAutoscrolls(true);
        TA_Private.setColumns(20);
        TA_Private.setEditable(false);
        TA_Private.setForeground(new Color(174, 238, 238));
        TA_Private.setRows(5);
        TA_Private.setEnabled(false);
        SP_.setViewportView(TA_Private);
        TP.addTab("密語", SP_);
        SP_Clan.setAutoscrolls(true);
        TA_Clan.setColumns(20);
        TA_Clan.setEditable(false);
        TA_Clan.setForeground(new Color(174, 238, 238));
        TA_Clan.setRows(5);
        TA_Clan.setEnabled(false);
        SP_Clan.setViewportView(TA_Clan);
        TP.addTab("血盟", SP_Clan);
        SP_Team.setAutoscrolls(true);
        TA_Team.setColumns(20);
        TA_Team.setEditable(false);
        TA_Team.setForeground(new Color(174, 238, 238));
        TA_Team.setRows(5);
        TA_Team.setEnabled(false);
        SP_Team.setViewportView(TA_Team);
        TP.addTab("組隊", SP_Team);
        SP_Split.setLeftComponent(TP);
        T_Player.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                J_Main.this.T_PlayerMousePressed(evt);
            }

            public void mouseReleased(MouseEvent evt) {
                J_Main.this.T_PlayerMouseReleased(evt);
            }
        });
        SP_player.setViewportView(T_Player);
        SP_Split.setRightComponent(SP_player);
        getContentPane().add(SP_Split, "Center");
        CB_Channel.setModel(new DefaultComboBoxModel(new String[]{"訊息頻道", "密語", "公告"}));
        B_Submit.setText("發送");
        B_Submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                J_Main.this.B_SubmitActionPerformed(evt);
            }
        });
        TF_Msg.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                J_Main.this.TF_MsgKeyPressed(evt);
            }
        });
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(CB_Channel, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(TF_Target, -2, 68, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(TF_Msg, -2, 310, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(B_Submit).addGap(175, 175, 175)));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(6, 6, 6).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(CB_Channel, -2, -1, -2).addComponent(TF_Target, -2, -1, -2).addComponent(TF_Msg, -2, -1, -2).addComponent(B_Submit))));
        getContentPane().add(jPanel2, "South");
        M_File.setMnemonic('F');
        M_File.setText("檔案(F)");
        MI_Save.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        MI_Save.setMnemonic('S');
        MI_Save.setText("儲存訊息(S)");
        M_File.add(MI_Save);
        M_File.add(jSeparator3);
        MI_SetClose.setAccelerator(KeyStroke.getKeyStroke(69, 2));
        MI_SetClose.setMnemonic('E');
        MI_SetClose.setText("設定關閉伺服器(E)...");
        M_File.add(MI_SetClose);
        MI_Close.setMnemonic('C');
        MI_Close.setText("關閉伺服器(C)");
        M_File.add(MI_Close);
        MB.add(M_File);
        M_Edit.setMnemonic('E');
        M_Edit.setText("編輯(E)");
        MI_LA.setMnemonic('W');
        MI_LA.setText("正義倍率(W)");
        M_Edit.add(MI_LA);
        MI_ATTR_ENCHANT_CHANCE.setMnemonic('X');
        MI_ATTR_ENCHANT_CHANCE.setText("屬性強化倍率(X)");
        M_Edit.add(MI_ATTR_ENCHANT_CHANCE);
        MI_CHANCE_ARMOR.setMnemonic('S');
        MI_CHANCE_ARMOR.setText("防具強化倍率(S)");
        M_Edit.add(MI_CHANCE_ARMOR);
        MI_CHANCE_WEAPON.setMnemonic('Q');
        MI_CHANCE_WEAPON.setText("武器強化倍率(Q)");
        M_Edit.add(MI_CHANCE_WEAPON);
        MI_XP_PET.setMnemonic('G');
        MI_XP_PET.setText("寵物經驗倍率(G)");
        M_Edit.add(MI_XP_PET);
        MI_Exp.setMnemonic('F');
        MI_Exp.setText("經驗倍率(F)");
        M_Edit.add(MI_Exp);
        MI_Exp.setMnemonic('F');
        MI_Exp.setText("經驗倍率(F)");
        MI_Drop.setMnemonic('D');
        MI_Drop.setText("掉寶率(D)");
        MI_Adena.setMnemonic('M');
        MI_Adena.setText("掉錢倍率(M)");
        M_Edit.add(MI_Exp);
        M_Edit.add(MI_Drop);
        M_Edit.add(MI_Adena);
        MB.add(M_Edit);
        M_Special.setMnemonic('S');
        M_Special.setText("特殊功能(S)");
        MI_Angel.setMnemonic('A');
        MI_Angel.setText("大天使祝福(A)");
        M_Special.add(MI_Angel);
        MI_AllBuff.setMnemonic('B');
        MI_AllBuff.setText("終極祝福(B)");
        M_Special.add(MI_AllBuff);
        MI_AllRess.setMnemonic('R');
        MI_AllRess.setText("全體復活補血魔(R)");
        M_Special.add(MI_AllRess);
        MB.add(M_Special);
        setJMenuBar(MB);
        pack();
    }

    private void T_PlayerMouseReleased(MouseEvent evt) {
        if ((evt.getClickCount() == 2) && (evt.getButton() == 1)) {
            select = T_Player.getSelectedRow();
            setPlayerView((String) DTM.getValueAt(select, 1));
            F_Player.pack();
            F_Player.setVisible(true);
        }
        if (evt.isPopupTrigger()) {
            select = T_Player.getSelectedRow();
            PM_Player.show(T_Player, evt.getX(), evt.getY());
        }
    }

    private void formWindowClosed(WindowEvent evt) {
        closeServer();
    }

    private void closeServer() {
        saveChatData(false);
        System.exit(0);
    }

    private void T_PlayerMousePressed(MouseEvent evt) {
        processEvent(evt);
    }

    private void B_SubmitActionPerformed(ActionEvent evt) {
        submitMsg(CB_Channel.getSelectedIndex());
    }

    private void TF_MsgKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == 10) {
            submitMsg(CB_Channel.getSelectedIndex());
        }
    }

    private void submitMsg(int select) {
        if (TF_Msg.getText().equals("")) {
            return;
        }
        switch (select) {
            case 0:
                String title1 = TF_Target.getText().equals(null) || TF_Target.getText().equals("") ? "系統公告" : TF_Target.getText();
                World.get().broadcastServerMessage("【" + title1 + "】:" + TF_Msg.getText());
                addWorldChat("【" + title1 + "】", TF_Msg.getText());
                break;
            case 1:
                if (World.get().getPlayer(TF_Target.getText()) == null) {
                    return;
                }
                L1PcInstance target = World.get().getPlayer(TF_Target.getText());
                target.sendPackets(new S_SystemMessage("【系統密語】:" + TF_Msg.getText()));
                addPrivateChat("【系統密語】", TF_Target.getText(), TF_Msg.getText());
                break;
            case 2:
                String title2 = TF_Target.getText().equals(null) || TF_Target.getText().equals("") ? "系統公告" : TF_Target.getText();
                World.get().broadcastServerMessage_green("【" + title2 + "】:" + TF_Msg.getText());
                addWorldChat("【" + title2 + "】", TF_Msg.getText());
                break;
        }
        TF_Msg.setText("");
    }

    private void showItemTable(int num) {
        iniTable();
        L1PcInstance pc = L1PcInstance.load(TF_Name.getText());
        if (pc.getInventory().getSize() == 0) {
            C_LoginToServer.items(pc);
        }
        L1Inventory inv = null;
        switch (num) {
            case 0:
                if (pc.getInventory() == null) {
                    return;
                }
                inv = pc.getInventory();
                D_Item.setTitle("身上物品");
                for (L1ItemInstance item : inv.getItems()) {
                    addItemTable(item.getName(), item.getCount(), item.getItemId());
                }
                break;
            case 1:
                if (pc.getDwarfInventory() == null) {
                    return;
                }
                D_Item.setTitle("倉庫物品");
                inv = pc.getDwarfInventory();
                for (L1ItemInstance item : inv.getItems()) {
                    addItemTable(item.getName(), item.getCount(), item.getItemId());
                }
                break;
            case 2:
                if (pc.getClan().getDwarfForClanInventory() == null) {
                    return;
                }
                D_Item.setTitle("血盟倉庫物品");
                inv = pc.getClan().getDwarfForClanInventory();
                for (L1ItemInstance item : inv.getItems()) {
                    addItemTable(item.getName(), item.getCount(), item.getItemId());
                }
                break;
            case 3:
                if (pc.getDwarfForElfInventory() == null) {
                    return;
                }
                D_Item.setTitle("妖森倉庫物品");
                inv = pc.getDwarfForElfInventory();
                for (L1ItemInstance item : inv.getItems()) {
                    addItemTable(item.getName(), item.getCount(), item.getItemId());
                }
        }
        D_Item.setVisible(true);
    }

    private void B_ItemActionPerformed(ActionEvent evt) {
        showItemTable(CB_Item.getSelectedIndex());
    }

    private void setPlayerView(String name) {
        L1PcInstance pc = L1PcInstance.load(name);
        int job = 0;
        switch (pc.getClassId()) {
            case 0:
                job = 715;
                break;
            case 1:
                job = 647;
                break;
            case 61:
                job = 384;
                break;
            case 48:
                job = 317;
                break;
            case 138:
                job = 247;
                break;
            case 37:
                job = 198;
                break;
            case 734:
                job = 532;
                break;
            case 1186:
                job = 452;
                break;
            case 2786:
                job = 145;
                break;
            case 2796:
                job = 25;
                break;
            case 6658:
                job = 903;
                break;
            case 6661:
                job = 930;
                break;
            case 6671:
                job = 1029;
                break;
            case 6650:
                job = 1056;
        }
        ImageIcon imageIcon = new ImageIcon("img/" + job + ".png");
        Icon icon = imageIcon;
        L_Image.setIcon(icon);
        TF_Account.setText(pc.getAccountName());
        TF_Name.setText(pc.getName());
        TF_Title.setText(pc.getTitle());
        TF_AccessLevel.setText("" + pc.getAccessLevel());
        TF_Sex.setText(pc.get_sex() == 1 ? "女" : "男");
        TF_Ac.setText(pc.getAc() + "");
        TF_Cha.setText(pc.getCha() + "");
        TF_Int.setText(pc.getInt() + "");
        TF_Str.setText(pc.getStr() + "");
        TF_Con.setText(pc.getCon() + "");
        TF_Wis.setText(pc.getWis() + "");
        TF_Dex.setText(pc.getDex() + "");
        TF_Exp.setText(pc.getExp() + "");
        TF_Map.setText(pc.getMapId() + "");
        TF_X.setText(pc.getX() + "");
        TF_Y.setText(pc.getY() + "");
        TF_Clan.setText(pc.getClanname());
        TF_Level.setText(pc.getLevel() + "");
        TF_Hp.setText(pc.getCurrentHp() + " / " + pc.getMaxHp());
        TF_Mp.setText(pc.getCurrentMp() + " / " + pc.getMaxMp());
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (((e.getModifiers() & 0x10) == 0L) && (((e.getModifiers() & 0x4) != 0) || ((e.getModifiers() & 0x8) != 0))) {
            return;
        }
        if (command.equals("強制踢除(K)")) {
            L1PcInstance target = World.get().getPlayer((String) DTM.getValueAt(select, 1));
            if (target != null) {
                addConsol("您把玩家：" + (String) DTM.getValueAt(select, 1) + "強制剔除遊戲。");
                target.sendPackets(new S_Disconnect());
            } else {
                addConsol("此玩家不在線上。");
            }
        } else if (command.equals("封鎖IP(B)")) {
            L1PcInstance target = World.get().getPlayer((String) DTM.getValueAt(select, 2));
            if (target != null) {
                ClientExecutor targetClient = target.getNetConnection();
                String ipaddr = targetClient.getIp().toString();
                if ((ipaddr != null) && (!LanSecurityManager.BANIPMAP.containsKey(ipaddr))) {
                    IpReading.get().add(ipaddr.toString(), getName() + ":L1PowerKick 封鎖IP");
                }
                targetClient.kick();
            }
            addConsol("已封鎖" + target.getName() + "的IP");
        } else if (command.equals("玩家資料(P)")) {
            setPlayerView((String) DTM.getValueAt(select, 1));
            F_Player.pack();
            F_Player.setVisible(true);
        } else if (command.equals("密語(W)")) {
            TF_Target.setText((String) DTM.getValueAt(select, 1));
            CB_Channel.setSelectedIndex(1);
        } else if (command.equals("儲存訊息(S)")) {
            saveChatData(false);
        } else if (command.equals("大天使祝福(A)")) {
            angel();
        } else if (command.equals("關閉伺服器(C)")) {
            closeServer();
        } else if (command.equals("正義倍率(W)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務正義倍率：" + ConfigRate.RATE_LA + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.RATE_LA = second;
                World.get().broadcastServerMessage("正義倍率變更為：" + ConfigRate.RATE_LA + "倍。");
                addConsol(" 正義率變更為：" + ConfigRate.RATE_LA + "倍。");
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("屬性強化倍率(X)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務屬性強化%：" + ConfigRate.ATTR_ENCHANT_CHANCE + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.ATTR_ENCHANT_CHANCE = second;
                World.get().broadcastServerMessage("屬性強化%變更為：" + ConfigRate.ATTR_ENCHANT_CHANCE + "%。");
                addConsol(" 屬性強化%變更為：" + ConfigRate.ATTR_ENCHANT_CHANCE + "%。");
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("防具強化倍率(S)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務防具強化倍率：" + ConfigRate.ENCHANT_CHANCE_ARMOR + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.ENCHANT_CHANCE_ARMOR = second;
                World.get().broadcastServerMessage("防具強化%變更為：" + ConfigRate.ENCHANT_CHANCE_ARMOR + "%。");
                addConsol(" 防具強化率變更為：" + ConfigRate.ENCHANT_CHANCE_ARMOR + "%。");
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("武器強化倍率(Q)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務武器強化%：" + ConfigRate.ENCHANT_CHANCE_WEAPON + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.ENCHANT_CHANCE_WEAPON = second;
                World.get().broadcastServerMessage("武器強化%變更為：" + ConfigRate.ENCHANT_CHANCE_WEAPON + "%。");
                addConsol(" 武器強化%變更為：" + ConfigRate.ENCHANT_CHANCE_WEAPON + "%。");
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("寵物經驗倍率(G)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務器寵物經驗倍率：" + ConfigRate.RATE_XP + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.RATE_XP = second;
                World.get().broadcastServerMessage("遊戲寵物經驗倍率變更為：" + ConfigRate.RATE_XP + "倍。");
                addConsol(" 遊戲寵物經驗倍率變更為：" + ConfigRate.RATE_XP + "倍。");
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("經驗倍率(F)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務器經驗倍率：" + ConfigRate.RATE_XP + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.RATE_XP = second;
                World.get().broadcastServerMessage("遊戲經驗倍率變更為：" + ConfigRate.RATE_XP);
                addConsol(" 遊戲經驗倍率變更為：" + ConfigRate.RATE_XP);
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("掉錢倍率(M)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務器掉錢率：" + ConfigRate.RATE_DROP_ADENA + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.RATE_DROP_ADENA = second;
                World.get().broadcastServerMessage("掉錢倍率變更為：" + ConfigRate.RATE_DROP_ADENA);
                addConsol(" 掉錢倍率變更為：" + ConfigRate.RATE_DROP_ADENA);
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("掉寶率(D)")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("當前服務器掉寶倍率：" + ConfigRate.RATE_DROP_ITEMS + " 請輸入新倍率：");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                ConfigRate.RATE_DROP_ITEMS = second;
                World.get().broadcastServerMessage("掉寶率變更為：" + ConfigRate.RATE_DROP_ITEMS);
                addConsol(" 掉寶率變更為：" + ConfigRate.RATE_DROP_ITEMS);
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else if (command.equals("設定關閉伺服器(E)...")) {
            String temp = "";
            try {
                temp = JOptionPane.showInputDialog("請輸入幾秒重後重開!");
                if ((temp == null) || (temp.equals(""))) {
                    return;
                }
                int second = Integer.valueOf(temp).intValue();
                if (second == 0) {
                    closeServer();
                }
                Shutdown.getInstance().startShutdown(null, second, true);
                World.get().broadcastServerMessage("伺服器將於(" + second + ")秒鐘後關閉伺服器!");
                addWorldChat("管理器", "伺服器將於(" + second + ")秒鐘後關閉伺服器!");
            } catch (NumberFormatException e2) {
                JOptionPane.showMessageDialog(this, "請輸入整數!");
            }
        } else {
            L1PcInstance targetpc;
            if (command.equals("終極祝福(B)")) {
                int[] allBuffSkill = {14, 26, 42, 48, 55, 68, 79, 88, 89, 90, 98, 102, 104, 105, 106, 111, 114, 117, 129, 137, 147, 160, 163, 168, 169, 170, 171, 175, 176};
                for (Iterator<?> localIterator = World.get().getAllPlayers().iterator(); localIterator.hasNext(); ) {
                    targetpc = (L1PcInstance) localIterator.next();
                    L1BuffUtil.haste(targetpc, 3600000);
                    L1BuffUtil.brave(targetpc, 3600000);
                    for (int element : allBuffSkill) {
                        if ((element == 26) || (element == 42)) {
                            L1Skills skill = SkillsTable.get().getTemplate(element);
                            new L1SkillUse().handleCommands(targetpc, element, targetpc.getId(), targetpc.getX(), targetpc.getY(), skill.getBuffDuration(), 4);
                        } else {
                            L1Skills skill = SkillsTable.get().getTemplate(element);
                            new L1SkillUse().handleCommands(targetpc, element, targetpc.getId(), targetpc.getX(), targetpc.getY(), skill.getBuffDuration(), 4);
                        }
                    }
                    targetpc.sendPackets(new S_ServerMessage(166, "祝福降臨人世,全體玩家得到祝福,GM是個大好人"));
                }
            } else if (command.equals("全體復活補血魔(R)")) {
                for (L1PcInstance tg : World.get().getAllPlayers()) {
                    if ((tg.getCurrentHp() == 0) && (tg.isDead())) {
                        tg.sendPackets(new S_SystemMessage("GM幫你復活嚕。"));
                        tg.broadcastPacketX10(new S_SkillSound(tg.getId(), 3944));
                        tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
                        tg.setTempID(tg.getId());
                        tg.sendPackets(new S_Message_YN(322, ""));
                    } else {
                        tg.sendPackets(new S_SystemMessage("GM幫你治癒嚕。"));
                        tg.broadcastPacketX10(new S_SkillSound(tg.getId(), 832));
                        tg.sendPackets(new S_SkillSound(tg.getId(), 832));
                        tg.setCurrentHp(tg.getMaxHp());
                        tg.setCurrentMp(tg.getMaxMp());
                    }
                }
            }
        }
    }

    private void angel() {
        for (L1PcInstance pc : World.get().getAllPlayers()) {
            if (pc.hasSkillEffect(71)) {
                pc.sendPackets(new S_ServerMessage(698));
                return;
            }
            int time = 3600;
            if (pc.hasSkillEffect(78)) {
                pc.killSkillEffectTimer(78);
                pc.startHpRegeneration();
                pc.startMpRegeneration();
                pc.startMpRegeneration();
            }
            if (pc.hasSkillEffect(1016)) {
                pc.killSkillEffectTimer(1016);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(52)) {
                pc.killSkillEffectTimer(52);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(101)) {
                pc.killSkillEffectTimer(101);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(150)) {
                pc.killSkillEffectTimer(150);
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            if (pc.hasSkillEffect(1017)) {
                pc.killSkillEffectTimer(1017);
                pc.setBraveSpeed(0);
            }
            pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
            pc.broadcastPacketX10(new S_SkillBrave(pc.getId(), 1, 0));
            pc.sendPackets(new S_SkillSound(pc.getId(), 751));
            pc.broadcastPacketX10(new S_SkillSound(pc.getId(), 751));
            pc.setSkillEffect(1000, time * 1000);
            pc.setBraveSpeed(1);
            pc.setDrink(false);
            if (pc.hasSkillEffect(43)) {
                pc.killSkillEffectTimer(43);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 0, 0));
                pc.setMoveSpeed(0);
            } else if (pc.hasSkillEffect(54)) {
                pc.killSkillEffectTimer(54);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 0, 0));
                pc.setMoveSpeed(0);
            } else if (pc.hasSkillEffect(1001)) {
                pc.killSkillEffectTimer(1001);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 0, 0));
                pc.setMoveSpeed(0);
            }
            if (pc.hasSkillEffect(29)) {
                pc.killSkillEffectTimer(29);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 0, 0));
            } else if (pc.hasSkillEffect(76)) {
                pc.killSkillEffectTimer(76);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 0, 0));
            } else if (pc.hasSkillEffect(152)) {
                pc.killSkillEffectTimer(152);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 0, 0));
            } else {
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
                pc.broadcastPacketX10(new S_SkillHaste(pc.getId(), 1, 0));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(1001, time * 1000);
            }
            new L1SkillUse().handleCommands(pc, 42, pc.getId(), pc.getX(), pc.getY(), time, 4);
            new L1SkillUse().handleCommands(pc, 26, pc.getId(), pc.getX(), pc.getY(), time, 4);
            new L1SkillUse().handleCommands(pc, 79, pc.getId(), pc.getX(), pc.getY(), time, 4);
            pc.setCurrentHp(pc.getMaxHp());
            pc.setCurrentMp(pc.getMaxMp());
        }
        World.get().broadcastServerMessage("大天使祝福降臨!所有玩家獲得狀態1小時!");
    }

    private void saveChatData(boolean bool) {
        SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
        Date d = Calendar.getInstance().getTime();
        String date = " " + sdfmt.format(d);
        try {
            FileOutputStream fos = new FileOutputStream("chatLog/Consol" + date + ".txt");
            fos.write(TA_Consol.getText().getBytes());
            fos.close();
            fos = new FileOutputStream("chatLog/AllChat" + date + ".txt");
            fos.write(TA_AllChat.getText().getBytes());
            fos.close();
            fos = new FileOutputStream("chatLog/World" + date + ".txt");
            fos.write(TA_World.getText().getBytes());
            fos.close();
            fos = new FileOutputStream("chatLog/Clan" + date + ".txt");
            fos.write(TA_Clan.getText().getBytes());
            fos.close();
            fos = new FileOutputStream("chatLog/Normal" + date + ".txt");
            fos.write(TA_Normal.getText().getBytes());
            fos.close();
            fos = new FileOutputStream("chatLog/Team" + date + ".txt");
            fos.write(TA_Team.getText().getBytes());
            fos.close();
            fos = new FileOutputStream("chatLog/Whisper" + date + ".txt");
            fos.write(TA_Private.getText().getBytes());
            fos.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void processEvent(MouseEvent e) {
        if ((e.getModifiers() & 0x4) != 0) {
            int modifiers = e.getModifiers();
            modifiers -= 4;
            modifiers |= 16;
            MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), modifiers, e.getX(), e.getY(), e.getClickCount(), false);
            T_Player.dispatchEvent(ne);
        }
    }
}
