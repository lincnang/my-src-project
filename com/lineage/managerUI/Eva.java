package com.lineage.managerUI;

import com.lineage.Server;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.datatables.sql.IpTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

/**
 * 管理器 類名稱：Eva<br>
 * 創建人:Manly<br>
 * 修改時間：2022年4月26日 下午5:09:41<br>
 * 修改人:QQ:263075225<br>
 *
 * @version<br>
 */
public class Eva {
    public static final Object lock = new Object();
    /**
     * 此服務器沒有運行.
     */
    public static final String NoServerStartMSG = "此服務器沒有運行.";
    /**
     * 確定要重啟服務器？
     */
    public static final String realExitServer = "確定要重啟服務器？";
    /**
     * 未指定角色名.
     */
    public static final String blankSetUser = "未指定角色名.";
    /**
     * 角色信息保存失敗.
     */
    public static final String characterSaveFail = "角色信息保存失敗.";
    /**
     * 角色信息保存成功.
     */
    public static final String characterSaveSuccess = "角色信息保存成功.";
    /**
     * 無連接用戶.
     */
    public static final String NoConnectUser = "無連接用戶.";
    /**
     * 你確定要刪除一周未連接的用戶嗎?
     */
    public static final String UserDelete = "你確定要刪除一周未連接的用戶嗎?";
    /**
     * 確定要重新加載?
     */
    public static final String ReloadMSG = "確定要重新加載?";
    /**
     * 你確定要加全體狀態?
     */
    public static final String AllBuffMSG = "你確定要加全體狀態?";
    /**
     * 輸入你的搜索關鍵字.
     */
    public static final String SearchNameMSG = "輸入你的搜索關鍵字.";
    private static final Log _log = LogFactory.getLog(Eva.class);
    public static int saveCount = 0;
    public static int width = 0;
    public static int height = 0;
    public static String date = "";
    public static String time = "";
    /**
     * 服務器運行狀態
     */
    public static boolean isServerStarted;
    public static int userCount;
    public static JFrame jJFrame = null;
    public static JDesktopPane jJDesktopPane = new JDesktopPane();
    /**
     * Config設置
     */
    public static ServerConfigWindow jServerSettingWindow = null;
    /**
     * 服務器日誌 系統
     */
    public static ServerLogWindow jSystemLogWindow = null;
    /**
     * 世界頻道信息
     */
    public static ServerLogWindow jWorldChatLogWindow = null;
    /**
     * 一般頻道信息
     */
    public static ServerLogWindow jNomalChatLogWindow = null;
    /**
     * 密語頻道信息
     */
    public static ServerLogWindow jWhisperChatLogWindow = null;
    /**
     * 血盟頻道信息
     */
    public static ServerLogWindow jClanChatLogWindow = null;
    /**
     * 組隊頻道信息
     */
    public static ServerLogWindow jPartyChatLogWindow = null;
    /**
     * 買賣頻道信息
     */
    public static ServerLogWindow jTradeChatLogWindow = null;
    /**
     * 倉庫
     */
    public static ServerLogWindow jWareHouseLogWindow = null;
    /**
     * 交易
     */
    public static ServerLogWindow jTradeLogWindow = null;
    /**
     * 強化
     */
    public static ServerLogWindow jEnchantLogWindow = null;
    /**
     * 丟棄物品
     */
    public static ServerLogWindow jObserveLogWindow = null;
    /**
     * 穿牆提示
     */
    public static ServerLogWindow MoveErrorLogWindow = null;
    /**
     * 錯誤
     */
    public static ServerLogWindow jBugLogWindow = null;
    /**
     * 命令
     */
    public static ServerLogWindow jCommandLogWindow = null;
    /**
     * 服務器聊天監控
     */
    public static ServerChatLogWindow jServerChatLogWindow = null;
    /**
     * 服務器用戶監控
     */
    public static ServerItemCountsWindow jServerUserMoniterWindow = null;
    /**
     * 全體信息監控
     */
    public static ServerMultiChatLogWindow jServerMultiChatLogWindow = null;
    /**
     * 用戶信息
     */
    public static ServerUserInfoWindow jServerUserInfoWindow = null;
    private JMenuBar jJMenuBar = null;
    private Container jContainer = null;
    private BorderLayout jBorderLayout = new BorderLayout();

    public Eva() {
        initialize();
    }

    /**
     * ** 로그 설정 부분 *****
     */
    public static int userCount(int i) {
        userCount += i;
        return userCount;
    }

    public static void refreshMemory() {
        // lblMemory.setText(" 메모리 : " + SystemUtil.getUsedMemoryMB() + " MB");
    }

    public static void LogServerAppend(String s, String s1) {
        // 서버로그창 : 2s
        // textServer.append("\n" + getLogTime() + "　" + s + "　" + s1);
        if (jSystemLogWindow != null && !jSystemLogWindow.isClosed()) {
            jSystemLogWindow.append(getLogTime() + "　" + s + "　" + s1 + "\n", "Black");
        } else {
            jSystemLogWindow = null;
        }
    }

    /**
     * 服務器日誌窗口：S - 連接，終端
     *
     */
    public static synchronized void LogServerAppend(String s, L1PcInstance pc, String string, int i) {
        if (jServerUserInfoWindow != null) {
            if (jServerUserInfoWindow.getTableModel() != null) {
                try {
                    jServerUserInfoWindow.jJTable0.clearSelection();
                    if (s.equals("連接")) {
                        jServerUserInfoWindow.jJTable0.clearSelection();
                        for (int row = 0; row < jServerUserInfoWindow.getTableModel().getRowCount(); row++) {
                            if (jServerUserInfoWindow.getTableModel().getValueAt(row, 0).equals(pc.getName())) {
                                jServerUserInfoWindow.getTableModel().removeRow(row);
                                i -= 1;
                                break;
                            }
                        }
                        String[] name = new String[1];
                        name[0] = pc.getName();
                        jServerUserInfoWindow.getTableModel().addRow(name);
                    } else {
                        jServerUserInfoWindow.jJTable0.clearSelection();
                        for (int row = 0; row < jServerUserInfoWindow.getTableModel().getRowCount(); row++) {
                            if (jServerUserInfoWindow.getTableModel().getValueAt(row, 0).equals(pc.getName())) {
                                jServerUserInfoWindow.getTableModel().removeRow(row);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jServerUserInfoWindow.getLbl_UserCount().setText("用戶數 : " + (jServerUserInfoWindow.getTableModel().getRowCount()));
            }
        }
        if (jSystemLogWindow != null && !jSystemLogWindow.isClosed()) {
            if (s.equals("連接")) {
                jSystemLogWindow.append(getLogTime() + "　" + s + "賬號:" + pc.getAccountName() + "　角色名:" + pc.getName() + "　IP:" + string + "　Uid:" + userCount(i) + "\n", "Blue");
            } else {
                jSystemLogWindow.append(getLogTime() + "　" + s + "賬號:" + pc.getAccountName() + "　角色名:" + pc.getName() + "　IP:" + string + "　Uid:" + userCount(i) + "\n", "Red");
            }
        } else {
            jSystemLogWindow = null;
        }
    }

    /**
     * 世界頻道
     *
     */
    public static void LogChatAppend(String s, String pcname, String text) {
        if (jWorldChatLogWindow != null && !jWorldChatLogWindow.isClosed()) {
            jWorldChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　:" + text + "\n", "Blue");
        } else {
            jWorldChatLogWindow = null;
        }
        if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
            if (jServerChatLogWindow.chk_World.isSelected()) {
                jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　:" + text + "\n", "Blue");
            }
        } else {
            jServerChatLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("worldChatText", getLogTime() + "　" + s + "　" + pcname + "　:" + text + "\n", "Blue");
        }
    }

    /**
     * 一般頻道、大叫頻道
     *
     */
    public static void LogChatNormalAppend(String s, String pcname, String text) {
        if (jNomalChatLogWindow != null && !jNomalChatLogWindow.isClosed()) {
            jNomalChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
        } else {
            jNomalChatLogWindow = null;
        }
        if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
            if (jServerChatLogWindow.chk_Noaml.isSelected()) {
                jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
            }
        } else {
            jServerChatLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("nomalChatText", getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
        }
    }

    /**
     * 聯合血盟頻道、隊伍頻道
     *
     */
    public static void LogChatPartyAppend(String s, String pcname, String text) {
        if (jPartyChatLogWindow != null && !jPartyChatLogWindow.isClosed()) {
            jPartyChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
        } else {
            jPartyChatLogWindow = null;
        }
        if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
            if (jServerChatLogWindow.chk_Party.isSelected()) {
                jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
            }
        } else {
            jServerChatLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("partyChatText", getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
        }
    }

    /**
     * 密語、私聊
     *
     */
    public static void LogChatWisperAppend(String s, String pcname, String c, String text, String sel) {
        if (jWhisperChatLogWindow != null && !jWhisperChatLogWindow.isClosed()) {
            jWhisperChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text + "\n", "Blue");
        } else {
            jWhisperChatLogWindow = null;
        }
        if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
            if (jServerChatLogWindow.chk_Whisper.isSelected()) {
                jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text + "\n", "Blue");
            }
        } else {
            jServerChatLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("whisperChatText", getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text + "\n", "Blue");
        }
    }

    /**
     * 血盟頻道
     *
     */
    public static void LogChatClanAppend(String s, String pcname, String c, String text) {
        if (jClanChatLogWindow != null && !jClanChatLogWindow.isClosed()) {
            jClanChatLogWindow.append(getLogTime() + "　" + s + "　玩家:" + pcname + " 血盟:" + c + " : " + text + "\n", "Blue");
        } else {
            jClanChatLogWindow = null;
        }
        if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
            if (jServerChatLogWindow.chk_Clan.isSelected()) {
                jServerChatLogWindow.append(getLogTime() + "　" + s + "　玩家:" + pcname + " 血盟:" + c + " : " + text + "\n", "Blue");
            }
        } else {
            jServerChatLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("clanChatText", getLogTime() + "　" + s + "　" + pcname + "[" + c + "] : " + text + "\n", "Blue");
        }
    }

    /**
     * 交易頻道
     *
     */
    public static void LogChatTradeAppend(String s, String pcname, String text) {
        if (jTradeChatLogWindow != null && !jTradeChatLogWindow.isClosed()) {
            jTradeChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
        } else {
            jTradeChatLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("tradeChatText", getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
        }
    }

    /**
     * 倉庫日誌
     *
     */
    public static void LogWareHouseAppend(String s, String pcname, String clanname, L1ItemInstance item, int count, int obj) {
        if (jWareHouseLogWindow != null && !jWareHouseLogWindow.isClosed()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　玩家:" + pcname + "　" + s);
            if (!clanname.equalsIgnoreCase("")) {
                sb.append(" 血盟:" + clanname);
            }
            sb.append("　");
            sb.append(item.getItemNameEva(count) + " OBJID:" + obj + "\n");
            jWareHouseLogWindow.append(sb.toString(), "Blue");
        } else {
            jWareHouseLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　玩家:" + pcname + "　" + s);
            if (!clanname.equalsIgnoreCase("")) {
                sb.append(" 血盟:" + clanname);
            }
            sb.append("　");
            sb.append(item.getItemNameEva(count) + "　OBJID:" + obj + "\n");
            jServerMultiChatLogWindow.append("wareHouseText", sb.toString(), "Blue");
        }
    }

    /**
     * 交易日誌
     *
     */
    public static void LogTradeAppend(String s, String pcname, String targetname, String itemname, long count, int obj) {
        if (jTradeLogWindow != null && !jTradeLogWindow.isClosed()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + s + pcname + " 和  " + targetname + "　");
            sb.append("交易物品:" + itemname + "(" + count + ") 物品OBJ:" + obj + "\n");
            jTradeLogWindow.append(sb.toString(), "Blue");
        } else {
            jTradeLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + s + pcname + " 和  " + targetname + "　");
            sb.append("交易物品:" + itemname + "(" + count + ") 物品OBJ:" + obj + "\n");
            jServerMultiChatLogWindow.append("tradeText", sb.toString(), "Blue");
        }
    }

    /**
     * 強化日誌
     *
     */
    public static void LogEnchantAppend(String s, String pcname, String itemname, int obj) {
        if (jEnchantLogWindow != null && !jEnchantLogWindow.isClosed()) {
            if (s.indexOf("成功") > -1) {// 若s返回值包含成功則藍色字體，若沒有則紅色字體
                jEnchantLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　" + itemname + "　OBJID:" + obj + "\n", "Blue");
            } else {
                jEnchantLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　" + itemname + "　OBJID:" + obj + "\n", "Red");
            }
        } else {
            jEnchantLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            if (s.indexOf("成功") > -1) {// 若s返回值包含成功則藍色字體，若沒有則紅色字體
                jServerMultiChatLogWindow.append("enchantText", getLogTime() + "　" + s + "　" + pcname + "　" + itemname + "　OBJID:" + obj + "\n", "Blue");
            } else {
                jServerMultiChatLogWindow.append("enchantText", getLogTime() + "　" + s + "　" + pcname + "　" + itemname + "　OBJID:" + obj + "\n", "Red");
            }
        }
    }

    /**
     * 丟棄物品日誌
     *
     */
    public static void LogObserverAppend(String s, String pcname, L1ItemInstance item, int count, int obj) {
        if (jObserveLogWindow != null && !jObserveLogWindow.isClosed()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + pcname + "　" + s);
            sb.append("　");
            sb.append(item.getItemNameEva(count) + "　OBJID:" + obj + "\n");
            jObserveLogWindow.append(sb.toString(), "Blue");
        } else {
            jObserveLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + pcname + "　" + s);
            sb.append("　");
            sb.append(item.getItemNameEva(count) + "　OBJID:" + obj + "\n");
            jServerMultiChatLogWindow.append("observeText", sb.toString(), "Blue");
        }
    }

    /**
     * 穿牆日誌
     *
     */
    public static void LogMoveerrorAppend(String s, String pcname) {
        if (MoveErrorLogWindow != null && !MoveErrorLogWindow.isClosed()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + s + pcname);
            sb.append("　");
            sb.append("可能是誤判.多次提醒就要注意了.\n");
            MoveErrorLogWindow.append(sb.toString(), "Blue");
        } else {
            MoveErrorLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + s + pcname);
            sb.append("　");
            sb.append("可能是誤判.多次提醒就要注意了.\n");
            jServerMultiChatLogWindow.append("moveerrorText", sb.toString(), "Blue");
        }
    }

    /**
     * 錯誤日誌
     *
     * @param type 1：錯誤信息<br>
     *             2：x、y、mapid，錯誤信息<br>
     *             3：玩家名稱，錯誤信息<br>
     */
    public static void LogBugAppend(String s, L1PcInstance pc, int type) {
        if (jBugLogWindow != null && !jBugLogWindow.isClosed()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + s + "　" + pc.getName());
            switch (type) {
                case 1:
                    jBugLogWindow.append(sb.toString(), "Blue");
                    break;
                case 2:
                    sb.append("　x,y,map:" + pc.getLocation().getX() + "," + pc.getLocation().getY() + "," + pc.getLocation().getMapId() + "\n");
                    jBugLogWindow.append(sb.toString(), "Blue");
                    break;
                case 3:
                    sb.append(pc.getName() + " " + s + "\n");
                    jBugLogWindow.append(sb.toString(), "Blue");
                    break;
                default:
                    break;
            }
        } else {
            jBugLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getLogTime() + "　" + s + "　" + pc.getName());
            switch (type) {
                case 1:
                    jServerMultiChatLogWindow.append("observeText", sb.toString(), "Blue");
                    break;
                case 2:
                    sb.append("　x,y,map:" + pc.getLocation().getX() + "," + pc.getLocation().getY() + "," + pc.getLocation().getMapId() + "\n");
                    jServerMultiChatLogWindow.append("bugText", sb.toString(), "Blue");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 命令日誌
     *
     */
    public static void LogCommandAppend(String pcname, String cmd, String arg) {
        if (jCommandLogWindow != null && !jCommandLogWindow.isClosed()) {
            jCommandLogWindow.append(getLogTime() + "　" + pcname + "　使用命令:" + cmd + " " + arg + "\n", "Blue");
        } else {
            jCommandLogWindow = null;
        }
        if (jServerMultiChatLogWindow != null) {
            jServerMultiChatLogWindow.append("commandText", getLogTime() + "　" + pcname + "　使用命令:" + cmd + " " + arg + "\n", "Blue");
        }
    }

    /**
     * 管理器點擊事件出現是、否的提示框
     *
     * @param s 內容
     */
    public static int QMsg(String s) {
        int result = JOptionPane.showConfirmDialog(null, s, "大陸Manly提醒你：", 2, JOptionPane.INFORMATION_MESSAGE);
        return result;
    }

    /**
     * 管理器點擊事件提示框，只有確定選項
     *
     */
    public static void infoMsg(String s) {
        JOptionPane.showMessageDialog(null, s, "大陸Manly提醒你：", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 管理器錯誤提示語
     *
     */
    public static void errorMsg(String s) {
        JOptionPane.showMessageDialog(null, s, "Server Message", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 獲取當前時間
     */
    private static String getLogTime() {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
        String time = dateFormat.format(currentDate.getTime());
        return time;
    }

    /**
     * 保存日誌
     */
    public static void savelog() {
        jSystemLogWindow.savelog();
        jServerMultiChatLogWindow.savelog();
    }

    public static void flush(JTextPane text, String FileName, String date) {
        try {
            RandomAccessFile rnd = new RandomAccessFile("ServerLog/" + date + "/" + FileName + ".txt", "rw");
            rnd.write(text.getText().getBytes());
            rnd.close();
        } catch (Exception e) {
        }
    }

    // 日期格式(yyyy-MM-dd) 時間(hh-mm)
    public static String getDate() {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh-mm", Locale.CHINA);
        return s.format(Calendar.getInstance().getTime());
    }

    private void initialize() {
        try {
            // 設置外觀
            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
            JFrame.setDefaultLookAndFeelDecorated(true);
            if (jJFrame == null) {
                jJFrame = new JFrame();
                // 圖片文件的名字及路徑
                jJFrame.setIconImage(new ImageIcon("img/icon.png").getImage());
                // 設置組件的長寬大小
                jJFrame.setSize(1200, 800);
                // true：顯示按鈕 false：不顯示或者隱藏按鈕
                jJFrame.setVisible(true);
                // 標題
                jJFrame.setTitle(":::: 天堂 服務器控制台 作者:老爹  ::::");
                // 關閉程序和DOS
                jJFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                jContainer = jJFrame.getContentPane();
                // 佈局
                jContainer.setLayout(jBorderLayout);
                jContainer.add("Center", jJDesktopPane);
            }
            if (jJMenuBar == null) {
                jJMenuBar = new JMenuBar();
                jJFrame.setJMenuBar(jJMenuBar);
                JMenu jJMenu1 = new JMenu("服務(A)");
                JMenu jJMenu2 = new JMenu("監控(B)");
                JMenu jJMenu3 = new JMenu("輔助(C)");
                JMenu jJMenu4 = new JMenu("刷新(R)");
                JMenu jJMenu5 = new JMenu("信息(I)");
                jJMenu1.setMnemonic(KeyEvent.VK_A); // 服務 快捷鍵ALT+A
                jJMenu2.setMnemonic(KeyEvent.VK_B); // 監控 快捷鍵ALT+B
                jJMenu3.setMnemonic(KeyEvent.VK_C); // 輔助 快捷鍵ALT+C
                jJMenu4.setMnemonic(KeyEvent.VK_R); // 刷新
                jJMenu5.setMnemonic(KeyEvent.VK_I); // 信息
                // 服務(ALT+A)
                JMenuItem serverSet = new JMenuItem("Config設置");
                serverSet.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
                serverSet.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jServerSettingWindow = new ServerConfigWindow();
                        jJDesktopPane.add(jServerSettingWindow, 0);
                        jServerSettingWindow.setLocation((jJFrame.getContentPane().getSize().width / 2) - (jServerSettingWindow.getContentPane().getSize().width / 2), (jJFrame.getContentPane().getSize().height / 2) - (jServerSettingWindow.getContentPane().getSize().height / 2));
                    }
                });
                JMenuItem serverSave = new JMenuItem("存儲玩家信息");
                serverSave.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
                serverSave.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // 所有人物信息存儲
                        Collection<L1PcInstance> allPc = World.get().getAllPlayers();
                        for (L1PcInstance tgpc : allPc) {
                            try {
                                tgpc.save();
                            } catch (Exception save) {
                                _log.error("人物信息存儲錯誤" + tgpc.getName());
                            }
                        }
                        // end
                        infoMsg(characterSaveSuccess);// 角色信息保存成功
                        jSystemLogWindow.append(getLogTime() + " 管理器執行：存儲玩家信息" + "\n", "Red");
                    }
                });
                JMenuItem serverExit = new JMenuItem("重啟服務器");
                serverExit.setAccelerator(KeyStroke.getKeyStroke(',', InputEvent.CTRL_DOWN_MASK));
                serverExit.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg(realExitServer) == 0) {
                            Shutdown.getInstance().startShutdown(null, 30, true);// 默認30秒關機
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：重啟服務器" + "\n", "Red");
                        }
                    }
                });
                JMenuItem serverNowExit = new JMenuItem("立即重啟");
                serverNowExit.setAccelerator(KeyStroke.getKeyStroke('.', InputEvent.CTRL_DOWN_MASK));
                serverNowExit.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg(realExitServer) == 0) {
                            Shutdown.getInstance().startShutdown(null, 0, true);// 立即關機
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：立即重啟" + "\n", "Red");
                        }
                    }
                });
                jJMenu1.add(serverSet);
                jJMenu1.add(serverSave);
                jJMenu1.add(serverExit);
                jJMenu1.add(serverNowExit);
                // 監控(B)
                JMenuItem worldChatLogWindow = new JMenuItem("世界聊天");
                worldChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('1', InputEvent.CTRL_DOWN_MASK));
                worldChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jWorldChatLogWindow != null && jWorldChatLogWindow.isClosed()) {
                            jWorldChatLogWindow = null;
                        }
                        if (jWorldChatLogWindow == null) {
                            jWorldChatLogWindow = new ServerLogWindow("世界聊天信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jWorldChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem nomalChatLogWindow = new JMenuItem("一般頻道");
                nomalChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('2', InputEvent.CTRL_DOWN_MASK));
                nomalChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jNomalChatLogWindow != null && jNomalChatLogWindow.isClosed()) {
                            jNomalChatLogWindow = null;
                        }
                        if (jNomalChatLogWindow == null) {
                            jNomalChatLogWindow = new ServerLogWindow("一般頻道信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jNomalChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem whisperChatLogWindow = new JMenuItem("密語聊天");
                whisperChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('3', InputEvent.CTRL_DOWN_MASK));
                whisperChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jWhisperChatLogWindow != null && jWhisperChatLogWindow.isClosed()) {
                            jWhisperChatLogWindow = null;
                        }
                        if (jWhisperChatLogWindow == null) {
                            jWhisperChatLogWindow = new ServerLogWindow("密語聊天信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jWhisperChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem clanChatLogWindow = new JMenuItem("血盟聊天");
                clanChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('4', InputEvent.CTRL_DOWN_MASK));
                clanChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jClanChatLogWindow != null && jClanChatLogWindow.isClosed()) {
                            jClanChatLogWindow = null;
                        }
                        if (jClanChatLogWindow == null) {
                            jClanChatLogWindow = new ServerLogWindow("血盟聊天信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jClanChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem partyChatLogWindow = new JMenuItem("隊伍聊天");
                partyChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('5', InputEvent.CTRL_DOWN_MASK));
                partyChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jPartyChatLogWindow != null && jPartyChatLogWindow.isClosed()) {
                            jPartyChatLogWindow = null;
                        }
                        if (jPartyChatLogWindow == null) {
                            jPartyChatLogWindow = new ServerLogWindow("隊伍聊天信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jPartyChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem tradeChatLogWindow = new JMenuItem("交易頻道");
                tradeChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('6', InputEvent.CTRL_DOWN_MASK));
                tradeChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jTradeChatLogWindow != null && jTradeChatLogWindow.isClosed()) {
                            jTradeChatLogWindow = null;
                        }
                        if (jTradeChatLogWindow == null) {
                            jTradeChatLogWindow = new ServerLogWindow("交易頻道信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jTradeChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem wareHouseLogWindow = new JMenuItem("倉庫");
                wareHouseLogWindow.setAccelerator(KeyStroke.getKeyStroke('7', InputEvent.CTRL_DOWN_MASK));
                wareHouseLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jWareHouseLogWindow != null && jWareHouseLogWindow.isClosed()) {
                            jWareHouseLogWindow = null;
                        }
                        if (jWareHouseLogWindow == null) {
                            jWareHouseLogWindow = new ServerLogWindow("倉庫信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jWareHouseLogWindow, 0);
                        }
                    }
                });
                JMenuItem tradeLogWindow = new JMenuItem("交易");
                tradeLogWindow.setAccelerator(KeyStroke.getKeyStroke('8', InputEvent.CTRL_DOWN_MASK));
                tradeLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jTradeLogWindow != null && jTradeLogWindow.isClosed()) {
                            jTradeLogWindow = null;
                        }
                        if (jTradeLogWindow == null) {
                            jTradeLogWindow = new ServerLogWindow("交易信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jTradeLogWindow, 0);
                        }
                    }
                });
                JMenuItem enchatLogWindow = new JMenuItem("強化");
                enchatLogWindow.setAccelerator(KeyStroke.getKeyStroke('9', InputEvent.CTRL_DOWN_MASK));
                enchatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jEnchantLogWindow != null && jEnchantLogWindow.isClosed()) {
                            jEnchantLogWindow = null;
                        }
                        if (jEnchantLogWindow == null) {
                            jEnchantLogWindow = new ServerLogWindow("強化信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jEnchantLogWindow, 0);
                        }
                    }
                });
                JMenuItem observeLogWindow = new JMenuItem("丟棄");
                observeLogWindow.setAccelerator(KeyStroke.getKeyStroke('0', InputEvent.CTRL_DOWN_MASK));
                observeLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jObserveLogWindow != null && jObserveLogWindow.isClosed()) {
                            jObserveLogWindow = null;
                        }
                        if (jObserveLogWindow == null) {
                            jObserveLogWindow = new ServerLogWindow("丟棄物品信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jObserveLogWindow, 0);
                        }
                    }
                });
                JMenuItem moveerrorLogWindow = new JMenuItem("穿牆");
                moveerrorLogWindow.setAccelerator(KeyStroke.getKeyStroke('M', InputEvent.CTRL_DOWN_MASK));
                moveerrorLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (MoveErrorLogWindow != null && MoveErrorLogWindow.isClosed()) {
                            MoveErrorLogWindow = null;
                        }
                        if (MoveErrorLogWindow == null) {
                            MoveErrorLogWindow = new ServerLogWindow("穿牆信息(誤判也會提醒)", 20, 20, width, height, true, true);
                            jJDesktopPane.add(MoveErrorLogWindow, 0);
                        }
                    }
                });
                JMenuItem bugLogWindow = new JMenuItem("錯誤");
                bugLogWindow.setAccelerator(KeyStroke.getKeyStroke('-', InputEvent.CTRL_DOWN_MASK));
                bugLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jBugLogWindow != null && jBugLogWindow.isClosed()) {
                            jBugLogWindow = null;
                        }
                        if (jBugLogWindow == null) {
                            jBugLogWindow = new ServerLogWindow("錯誤信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jBugLogWindow, 0);
                        }
                    }
                });
                JMenuItem commandLogWindow = new JMenuItem("命令");
                commandLogWindow.setAccelerator(KeyStroke.getKeyStroke('=', InputEvent.CTRL_DOWN_MASK));
                commandLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jCommandLogWindow != null && jCommandLogWindow.isClosed()) {
                            jCommandLogWindow = null;
                        }
                        if (jCommandLogWindow == null) {
                            jCommandLogWindow = new ServerLogWindow("命令信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jCommandLogWindow, 0);
                        }
                    }
                });
                JMenuItem serverChatLogWindow = new JMenuItem("多個聊天監控");
                serverChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('\\', InputEvent.CTRL_DOWN_MASK));
                serverChatLogWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jServerChatLogWindow != null && jServerChatLogWindow.isClosed()) {
                            jServerChatLogWindow = null;
                        }
                        if (jServerChatLogWindow == null) {
                            jServerChatLogWindow = new ServerChatLogWindow("多個聊天監控信息", 20, 20, width, height, true, true);
                            jJDesktopPane.add(jServerChatLogWindow, 0);
                        }
                    }
                });
                JMenuItem serverUserMoniterWindow = new JMenuItem("物品數量監控");
                serverUserMoniterWindow.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.CTRL_DOWN_MASK));
                serverUserMoniterWindow.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (jServerUserMoniterWindow != null && jServerUserMoniterWindow.isClosed()) {
                            jServerUserMoniterWindow = null;
                        }
                        if (jServerUserMoniterWindow == null) {
                            jServerUserMoniterWindow = new ServerItemCountsWindow("物品數量監控信息", 20, 20, width + 220, height + 200, true, true);
                            jJDesktopPane.add(jServerUserMoniterWindow, 0);
                        }
                    }
                });
                jJMenu2.add(worldChatLogWindow);// 世界聊天
                jJMenu2.add(nomalChatLogWindow);// 一般頻道
                jJMenu2.add(whisperChatLogWindow);// 密語聊天
                jJMenu2.add(clanChatLogWindow);// 血盟聊天
                jJMenu2.add(partyChatLogWindow);// 隊伍聊天
                jJMenu2.add(tradeChatLogWindow);// 交易頻道
                jJMenu2.add(wareHouseLogWindow);// 倉庫
                jJMenu2.add(tradeLogWindow);// 交易
                jJMenu2.add(enchatLogWindow);// 強化
                jJMenu2.add(observeLogWindow);// 丟棄
                jJMenu2.add(moveerrorLogWindow);// 穿牆
                jJMenu2.add(bugLogWindow);// 錯誤
                jJMenu2.add(commandLogWindow);// 命令
                jJMenu2.add(serverChatLogWindow);// 多個聊天監控
                jJMenu2.add(serverUserMoniterWindow);// 物品數量監控
                // 輔助(H)
                JMenuItem characterDelete = new JMenuItem("刪除一周未登錄角色");
                characterDelete.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK));
                characterDelete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isServerStarted) {
                            if (QMsg(UserDelete) == 0) {
                                // try {
                                // Server.clearDB();
                                // } catch (SQLException ex) {
                                //
                                // }
                                jSystemLogWindow.append(getLogTime() + "　管理器執行：角色刪除完成." + "\n", "Red");
                            }
                        } else {
                            errorMsg(NoServerStartMSG);
                        }
                    }
                });
                JMenuItem allBuff = new JMenuItem("全體狀態");
                allBuff.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));
                allBuff.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isServerStarted) {
                            if (QMsg(AllBuffMSG) == 0) {
                                // SpecialEventHandler.getInstance().doAllBuf();
                                AllPcBuff.get().startAllBuff();// 自改
                                jSystemLogWindow.append(getLogTime() + "　管理器執行：全體狀態." + "\n", "Blue");
                            }
                        } else {
                            errorMsg(NoServerStartMSG);
                        }
                    }
                });
                JMenuItem chat = new JMenuItem("世界聊天");
                chat.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.CTRL_DOWN_MASK));
                chat.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isServerStarted) {
                            if (World.get().isWorldChatElabled()) {
                                if (QMsg("世界聊天當前是開啟的。您確定要禁用嗎？") == 0) {
                                    // SpecialEventHandler.getInstance().doNotChatEveryone();
                                    World.get().set_worldChatElabled(false);
                                    jSystemLogWindow.append(getLogTime() + "　管理器執行：世界聊天 禁用." + "\n", "Red");
                                }
                            } else {
                                if (QMsg("世界聊天當前是被禁用的。你確定要激活嗎？") == 0) {
                                    // SpecialEventHandler.getInstance().doChatEveryone();
                                    World.get().set_worldChatElabled(true);
                                    jSystemLogWindow.append(getLogTime() + "　管理器執行：世界聊天 激活." + "\n", "Blue");
                                }
                            }
                        } else {
                            errorMsg(NoServerStartMSG);
                        }
                    }
                });
                jJMenu3.add(characterDelete);
                jJMenu3.add(allBuff);
                jJMenu3.add(chat);
                // 刷新(R)
                /*
                 * JMenuItem noticeReload = new JMenuItem("Notice");
                 * noticeReload.setAccelerator(KeyStroke.getKeyStroke('Q',
                 * InputEvent.SHIFT_DOWN_MASK));
                 * noticeReload.addActionListener(new ActionListener() { public
                 * void actionPerformed(ActionEvent e) { if (QMsg("Notice " +
                 * ReloadMSG) == 0) { NoticeTable.reload();
                 * jSystemLogWindow.append(getLogTime() +
                 * "　管理器執行：Notice Update Complete..." + "\n", "Red"); } } });
                 */
                JMenuItem configReload = new JMenuItem("配置文件");
                configReload.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.SHIFT_DOWN_MASK));
                configReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("配置文件 " + ReloadMSG) == 0) {
                            Server.StartConfig();// 自改
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：服務器配置更新完成..." + "\n", "Red");
                        }
                    }
                });
                /*
                 * JMenuItem weaponAddDamageReload = new
                 * JMenuItem("WeaponAddDamage");
                 * weaponAddDamageReload.setAccelerator
                 * (KeyStroke.getKeyStroke('E', InputEvent.SHIFT_DOWN_MASK));
                 * weaponAddDamageReload.addActionListener(new ActionListener()
                 * { public void actionPerformed(ActionEvent e) { if
                 * (QMsg("WeaponAddDamage " + ReloadMSG) == 0) {
                 * WeaponAddDamage.reload();
                 * jSystemLogWindow.append(getLogTime() +
                 * "　管理器執行：WeaponAddDamage Update Complete..." + "\n", "Red"); }
                 * } });
                 */
                JMenuItem itemEnchantListReload = new JMenuItem("道具強化表");
                itemEnchantListReload.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.SHIFT_DOWN_MASK));
                itemEnchantListReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("道具強化表 " + ReloadMSG) == 0) {
                            // ItemEnchantList.reload();
                            System.out.println("道具強化表沒開啟");
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：道具強化表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem dropReload = new JMenuItem("道具掉落表");
                dropReload.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.SHIFT_DOWN_MASK));
                dropReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("道具掉落表 " + ReloadMSG) == 0) {
                            DropTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：道具掉落表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem dropItemReload = new JMenuItem("掉落幾率表");
                dropItemReload.setAccelerator(KeyStroke.getKeyStroke('I', InputEvent.SHIFT_DOWN_MASK));
                dropItemReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("掉落幾率表 " + ReloadMSG) == 0) {
                            DropItemTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：掉落幾率表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem polyReload = new JMenuItem("變身列表");
                polyReload.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.SHIFT_DOWN_MASK));
                polyReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("變身列表 " + ReloadMSG) == 0) {
                            PolyTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：變身列表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem resolventReload = new JMenuItem("溶解物品表");
                resolventReload.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.SHIFT_DOWN_MASK));
                resolventReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("溶解物品表 " + ReloadMSG) == 0) {
                            ResolventTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：溶解物品表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem treasureBoxReload = new JMenuItem("開箱設置表");
                treasureBoxReload.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.SHIFT_DOWN_MASK));
                treasureBoxReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("開箱設置表 " + ReloadMSG) == 0) {
                            ItemBoxTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：開箱設置表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem skillsReload = new JMenuItem("魔法技能表");
                skillsReload.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.SHIFT_DOWN_MASK));
                skillsReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("魔法技能表 " + ReloadMSG) == 0) {
                            SkillsTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：魔法技能表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem mobSkillReload = new JMenuItem("怪物技能表");
                mobSkillReload.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.SHIFT_DOWN_MASK));
                mobSkillReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("怪物技能表 " + ReloadMSG) == 0) {
                            MobSkillTable.getInstance().loadMobSkillData();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：怪物技能表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem mapFixKeyReload = new JMenuItem("地圖修復key");
                mapFixKeyReload.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.SHIFT_DOWN_MASK));
                mapFixKeyReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("地圖修復key " + ReloadMSG) == 0) {
                            MapsTable.get().load();// 地圖設置
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：地圖修復（key）刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem itemReload = new JMenuItem("物品道具表");
                itemReload.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.SHIFT_DOWN_MASK));
                itemReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("物品道具表 " + ReloadMSG) == 0) {
                            ItemTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：物品道具表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem shopReload = new JMenuItem("商店列表");
                shopReload.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.SHIFT_DOWN_MASK));
                shopReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("商店列表 " + ReloadMSG) == 0) {
                            ShopTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：商店列表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem autoLootReload = new JMenuItem("自動拾取表");
                autoLootReload.setAccelerator(KeyStroke.getKeyStroke('J', InputEvent.SHIFT_DOWN_MASK));
                autoLootReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("自動拾取表 " + ReloadMSG) == 0) {
                            // AutoLoot.getInstance().reload();
                            System.out.println("自動拾取表暫未開放");
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：自動拾取表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem npcReload = new JMenuItem("Npc列表");
                npcReload.setAccelerator(KeyStroke.getKeyStroke('K', InputEvent.SHIFT_DOWN_MASK));
                npcReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("Npc列表 " + ReloadMSG) == 0) {
                            NpcTable.get().load();
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：Npc列表刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem bossCycleReload = new JMenuItem("boss循環週期");
                bossCycleReload.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.SHIFT_DOWN_MASK));
                bossCycleReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("boss循環週期 " + ReloadMSG) == 0) {
                            SpawnBossReading.get().load();// 召喚BOSS資料
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：boss循環週期刷新完成..." + "\n", "Red");
                        }
                    }
                });
                /*
                 * JMenuItem weaponSkillReload = new JMenuItem("WeaponSkill");
                 * weaponSkillReload.setAccelerator(KeyStroke.getKeyStroke('Z',
                 * InputEvent.SHIFT_DOWN_MASK));
                 * weaponSkillReload.addActionListener(new ActionListener() {
                 * public void actionPerformed(ActionEvent e) { if
                 * (QMsg("WeaponSkill " + ReloadMSG) == 0) {
                 * WeaponSkillTable.reload();
                 * jSystemLogWindow.append(getLogTime() +
                 * "　管理器執行：WeaponSkill Update Complete..." + "\n", "Red"); } }
                 * });
                 *
                 * JMenuItem itemExplanationReload = new
                 * JMenuItem("ItemExplanation");
                 * itemExplanationReload.setAccelerator
                 * (KeyStroke.getKeyStroke('X', InputEvent.SHIFT_DOWN_MASK));
                 * itemExplanationReload.addActionListener(new ActionListener()
                 * { public void actionPerformed(ActionEvent e) { if
                 * (QMsg("ItemExplanation " + ReloadMSG) == 0) {
                 * ItemExplanation.reload();
                 * jSystemLogWindow.append(getLogTime() +
                 * "　管理器執行：ItemExplanation Update Complete..." + "\n", "Red"); }
                 * } });
                 *
                 * JMenuItem autoShopReload = new JMenuItem("AutoShop");
                 * autoShopReload.setAccelerator(KeyStroke.getKeyStroke('C',
                 * InputEvent.SHIFT_DOWN_MASK));
                 * autoShopReload.addActionListener(new ActionListener() {
                 * public void actionPerformed(ActionEvent e) { if
                 * (QMsg("AutoShop " + ReloadMSG) == 0) {
                 * AutoShopTable.reload(); jSystemLogWindow.append(getLogTime()
                 * + "　管理器執行：AutoShop Update Complete..." + "\n", "Red"); } }
                 * });
                 *
                 * JMenuItem quizReload = new JMenuItem("Quiz");
                 * quizReload.setAccelerator(KeyStroke.getKeyStroke('V',
                 * InputEvent.SHIFT_DOWN_MASK));
                 * quizReload.addActionListener(new ActionListener() { public
                 * void actionPerformed(ActionEvent e) { if (QMsg("Quiz " +
                 * ReloadMSG) == 0) { QuizQuestionTable.reload();
                 * jSystemLogWindow.append(getLogTime() +
                 * "　管理器執行：Quiz Update Complete..." + "\n", "Red"); } } });
                 */
                JMenuItem banIpReload = new JMenuItem("封鎖IP");
                banIpReload.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.SHIFT_DOWN_MASK));
                banIpReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("封鎖IP " + ReloadMSG) == 0) {
                            IpTable.get();// 禁止IP列表
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：封鎖IP 刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem castleReload = new JMenuItem("城堡資料");
                castleReload.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.SHIFT_DOWN_MASK));
                castleReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("城堡資料 " + ReloadMSG) == 0) {
                            CastleReading.get().load();// 城堡資料
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：城堡資料 刷新完成..." + "\n", "Red");
                        }
                    }
                });
                JMenuItem clanReload = new JMenuItem("血盟資料");
                clanReload.setAccelerator(KeyStroke.getKeyStroke('M', InputEvent.SHIFT_DOWN_MASK));
                clanReload.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (QMsg("血盟資料 " + ReloadMSG) == 0) {
                            ClanReading.get().load();// 血盟資料
                            jSystemLogWindow.append(getLogTime() + "　管理器執行：血盟資料 刷新完成..." + "\n", "Red");
                        }
                    }
                });
                // jJMenu4.add(noticeReload);
                jJMenu4.add(configReload);
                // jJMenu4.add(weaponAddDamageReload);
                jJMenu4.add(itemEnchantListReload);
                jJMenu4.add(dropReload);
                jJMenu4.add(dropItemReload);
                jJMenu4.add(polyReload);
                jJMenu4.add(resolventReload);
                jJMenu4.add(treasureBoxReload);
                jJMenu4.add(skillsReload);
                jJMenu4.add(mobSkillReload);
                jJMenu4.add(mapFixKeyReload);
                jJMenu4.add(itemReload);
                jJMenu4.add(shopReload);
                jJMenu4.add(autoLootReload);
                jJMenu4.add(npcReload);
                jJMenu4.add(bossCycleReload);
                // jJMenu4.add(weaponSkillReload);
                // jJMenu4.add(itemExplanationReload);
                // jJMenu4.add(autoShopReload);
                // jJMenu4.add(quizReload);
                jJMenu4.add(banIpReload);
                jJMenu4.add(castleReload);
                jJMenu4.add(clanReload);
                ;
                JMenuItem developerInfo = new JMenuItem("關於作者");
                developerInfo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "QQ263075225大陸Manly", "信息", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                jJMenu5.add(developerInfo);
                jJMenuBar.add(jJMenu1);
                jJMenuBar.add(jJMenu2);
                jJMenuBar.add(jJMenu3);
                jJMenuBar.add(jJMenu4);
                jJMenuBar.add(jJMenu5);
                width = jJFrame.getContentPane().getSize().width / 2;
                height = (jJFrame.getContentPane().getSize().height - 50) / 2;
                // 管理器界面佈局
                if (jSystemLogWindow == null) {
                    jSystemLogWindow = new ServerLogWindow("服務器日誌", 0, 0, width, height, false, false);
                    jJDesktopPane.add(jSystemLogWindow);
                }
                if (jServerMultiChatLogWindow == null) {
                    jServerMultiChatLogWindow = new ServerMultiChatLogWindow("全體信息監控", 0, height, width, height, false, false);
                    jJDesktopPane.add(jServerMultiChatLogWindow);
                }
                if (jServerUserInfoWindow == null) {
                    jServerUserInfoWindow = new ServerUserInfoWindow("用戶信息", width, 0, width, height, false, false);
                    jJDesktopPane.add(jServerUserInfoWindow);
                }
            }
            isServerStarted = true;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
