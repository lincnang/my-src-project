package com.lineage.managerUI;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigCharSetting;
import com.lineage.config.ConfigRate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 類名稱：ServerConfigWindow<br>
 * 類描述：Config設置<br>
 * 創建人:Manly<br>
 * 修改時間：2022年4月27日 下午7:18:25<br>
 * 修改人:QQ:263075225<br>
 * 修改備註:<br>
 *
 * @version<br>
 */
@SuppressWarnings("serial")
public class ServerConfigWindow extends JInternalFrame {
    private static final Log _log = LogFactory.getLog(ServerConfigWindow.class);
    /**
     * 允許最大連線用戶標籤
     */
    private JLabel lbl_MaxUser = null;
    /**
     * 允許最大連線用戶參數
     */
    private JTextField txt_MaxUser = null;
    /**
     * 經驗倍率標籤
     */
    private JLabel lbl_Exp = null;
    /**
     * 經驗倍率參數
     */
    private JTextField txt_Exp = null;
    /**
     * 道具倍率標籤
     */
    private JLabel lbl_Item = null;
    /**
     * 道具倍率參數
     */
    private JTextField txt_Item = null;
    /**
     * 正義倍率標籤
     */
    private JLabel lbl_Lawful = null;
    /**
     * 正義倍率參數
     */
    private JTextField txt_Lawful = null;
    /**
     * 寵物負重倍率標籤
     */
    private JLabel lbl_RateWeightLimitforPet = null;
    /**
     * 寵物負重倍率參數
     */
    private JTextField txt_RateWeightLimitforPet = null;
    /**
     * 防卷強化倍率標籤
     */
    private JLabel lbl_ArmorEnchant = null;
    /**
     * 防卷強化倍率參數
     */
    private JTextField txt_ArmorEnchant = null;
    /**
     * 商店販賣價格倍率標籤
     */
    private JLabel lbl_RateShopSellingPrice = null;
    /**
     * 商店販賣價格倍率參數
     */
    private JTextField txt_RateShopSellingPrice = null;
    /**
     * 最高等級限制標籤
     */
    private JLabel lbl_Max_Level = null;
    /**
     * 最高等級限制參數
     */
    private JTextField txt_Max_Level = null;
    /**
     * 公頻廣播等級限制標籤
     */
    private JLabel lbl_ChatLevel = null;
    /**
     * 公頻廣播等級限制參數
     */
    private JTextField txt_ChatLevel = null;
    /**
     * 金幣倍率標籤
     */
    private JLabel lbl_Adena = null;
    /**
     * 金幣倍率參數
     */
    private JTextField txt_Adena = null;
    /**
     * 友好度倍率標籤
     */
    private JLabel lbl_Karma = null;
    /**
     * 友好度倍率參數
     */
    private JTextField txt_Karma = null;
    /**
     * 角色負重倍率標籤
     */
    private JLabel lbl_Weight = null;
    /**
     * 角色負重倍率參數
     */
    private JTextField txt_Weight = null;
    /**
     * 武卷強化倍率標籤
     */
    private JLabel lbl_WeaponEnchant = null;
    /**
     * 武卷強化倍率參數
     */
    private JTextField txt_WeaponEnchant = null;
    /**
     * 武器屬性強化倍率標籤
     */
    private JLabel lbl_AccessoryEnchant = null;
    /**
     * 武器屬性強化倍率參數
     */
    private JTextField txt_AccessoryEnchant = null;
    /**
     * 商店收購價格倍率標籤
     */
    private JLabel lbl_RateShopPurchasingPrice = null;
    /**
     * 商店收購價格倍率參數
     */
    private JTextField txt_RateShopPurchasingPrice = null;
    /**
     * 是否允許丟棄道具至地面標籤
     */
    private JLabel lbl_WhisperChatLevel = null;
    /**
     * 是否允許丟棄道具至地面參數
     */
    private JTextField txt_WhisperChatLevel = null;

    public ServerConfigWindow() {
        super();
        initialize();
    }

    public void initialize() {
        // 獲取圖片位置
        File PictureAuthor = new File("img/Author.png");
        // 計算圖片大小
        String AuthorSize = String.format("%.1f", PictureAuthor.length() / 1024.0);
        // 計算圖片大小轉換為Double
        double AuthorSizeDouble = Double.parseDouble(AuthorSize);
        // png被改動 大小發生變化 直接強制關閉
        if (AuthorSizeDouble != 4.4) {
            JOptionPane.showMessageDialog(null, "檢測到img發生變化.請勿隨意更改.強制關閉.", " 作者-老爹", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            try {
                setClosed(true);
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            return;
        }
        title = "Config設置";
        closable = true;// 關閉
        isMaximum = false;// 最大
        maximizable = true;// 開啟最大化按鈕
        resizable = true;// 可以調整窗口大小
        iconable = true;
        isIcon = false;
        setSize(400, 360);// 組件長寬
        setBounds(400, 0, 400, 360);// 組件在容器X軸上的起點 組件在容器Y軸上的起點 組件的長度 組件的高度
        setVisible(true);// 組件是否可見
        frameIcon = new ImageIcon("");
        setRootPaneCheckingEnabled(true);
        updateUI();// 刷新面板
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        // Config設置左邊配置
        lbl_MaxUser = new JLabel("允許最大連線用戶");
        txt_MaxUser = new JTextField();
        txt_MaxUser.setForeground(Color.blue);
        lbl_Exp = new JLabel("經驗倍率");
        txt_Exp = new JTextField();
        txt_Exp.setForeground(Color.blue);
        lbl_Item = new JLabel("道具倍率");
        txt_Item = new JTextField();
        txt_Item.setForeground(Color.blue);
        lbl_Lawful = new JLabel("正義倍率");
        txt_Lawful = new JTextField();
        txt_Lawful.setForeground(Color.blue);
        lbl_RateWeightLimitforPet = new JLabel("寵物負重倍率");
        txt_RateWeightLimitforPet = new JTextField();
        txt_RateWeightLimitforPet.setForeground(Color.blue);
        lbl_ArmorEnchant = new JLabel("防卷強化倍率");
        txt_ArmorEnchant = new JTextField();
        txt_ArmorEnchant.setForeground(Color.blue);
        lbl_RateShopSellingPrice = new JLabel("商店販賣價格倍率");
        txt_RateShopSellingPrice = new JTextField();
        txt_RateShopSellingPrice.setForeground(Color.blue);
        lbl_Max_Level = new JLabel("最高等級限制");
        txt_Max_Level = new JTextField();
        txt_Max_Level.setForeground(Color.blue);
        // Config設置右邊配置
        lbl_ChatLevel = new JLabel("公頻廣播等級限制");
        txt_ChatLevel = new JTextField();
        txt_ChatLevel.setForeground(Color.blue);
        lbl_Adena = new JLabel("金幣倍率");
        txt_Adena = new JTextField();
        txt_Adena.setForeground(Color.blue);
        lbl_Karma = new JLabel("友好度倍率");
        txt_Karma = new JTextField();
        txt_Karma.setForeground(Color.blue);
        lbl_Weight = new JLabel("角色負重倍率");
        txt_Weight = new JTextField();
        txt_Weight.setForeground(Color.blue);
        lbl_WeaponEnchant = new JLabel("武卷強化倍率");
        txt_WeaponEnchant = new JTextField();
        txt_WeaponEnchant.setForeground(Color.blue);
        lbl_AccessoryEnchant = new JLabel("武器屬性強化倍率");
        txt_AccessoryEnchant = new JTextField();
        txt_AccessoryEnchant.setForeground(Color.blue);
        lbl_RateShopPurchasingPrice = new JLabel("商店收購價格倍率");
        txt_RateShopPurchasingPrice = new JTextField();
        txt_RateShopPurchasingPrice.setForeground(Color.blue);
        lbl_WhisperChatLevel = new JLabel("密語等級限制");
        txt_WhisperChatLevel = new JTextField();
        txt_WhisperChatLevel.setForeground(Color.blue);
        // 參數設置
        txt_MaxUser.setText(Config.MAX_ONLINE_USERS + "");
        txt_ChatLevel.setText(ConfigAlt.GLOBAL_CHAT_LEVEL + "");
        txt_Exp.setText(ConfigRate.RATE_XP + "");
        txt_Adena.setText(ConfigRate.RATE_DROP_ADENA + "");
        txt_Item.setText(ConfigRate.RATE_DROP_ITEMS + "");
        txt_Karma.setText(ConfigRate.RATE_KARMA + "");
        txt_Lawful.setText(ConfigRate.RATE_LA + "");
        txt_Weight.setText(ConfigRate.RATE_WEIGHT_LIMIT + "");
        txt_RateWeightLimitforPet.setText(ConfigRate.RATE_WEIGHT_LIMIT_PET + "");
        txt_WeaponEnchant.setText(ConfigRate.ENCHANT_CHANCE_WEAPON + "");
        txt_ArmorEnchant.setText(ConfigRate.ENCHANT_CHANCE_ARMOR + "");
        txt_AccessoryEnchant.setText(ConfigRate.ATTR_ENCHANT_CHANCE + "");
        txt_RateShopSellingPrice.setText(ConfigRate.RATE_SHOP_SELLING_PRICE + "");
        txt_RateShopPurchasingPrice.setText(ConfigRate.RATE_SHOP_PURCHASING_PRICE + "");
        txt_Max_Level.setText(ConfigCharSetting.MAX_LEVEL + "");
        txt_WhisperChatLevel.setText(ConfigAlt.WHISPER_CHAT_LEVEL + "");
        JButton btn_ok = new JButton("保存設置");
        btn_ok.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Config.MAX_ONLINE_USERS = Short.parseShort(txt_MaxUser.getText());
                    ConfigAlt.GLOBAL_CHAT_LEVEL = Short.parseShort(txt_ChatLevel.getText());
                    ConfigRate.RATE_XP = Double.parseDouble(txt_Exp.getText());
                    ConfigRate.RATE_DROP_ADENA = Double.parseDouble(txt_Adena.getText());
                    ConfigRate.RATE_DROP_ITEMS = Double.parseDouble(txt_Item.getText());
                    ConfigRate.RATE_KARMA = Double.parseDouble(txt_Karma.getText());
                    ConfigRate.RATE_LA = Double.parseDouble(txt_Lawful.getText());
                    ConfigRate.RATE_WEIGHT_LIMIT = Double.parseDouble(txt_Weight.getText());
                    ConfigRate.RATE_WEIGHT_LIMIT_PET = Double.parseDouble(txt_RateWeightLimitforPet.getText());
                    ConfigRate.ENCHANT_CHANCE_WEAPON = Integer.parseInt(txt_WeaponEnchant.getText());
                    ConfigRate.ENCHANT_CHANCE_ARMOR = Integer.parseInt(txt_ArmorEnchant.getText());
                    ConfigRate.ATTR_ENCHANT_CHANCE = Integer.parseInt(txt_AccessoryEnchant.getText());
                    ConfigRate.RATE_SHOP_SELLING_PRICE = Double.parseDouble(txt_RateShopSellingPrice.getText());
                    ConfigRate.RATE_SHOP_PURCHASING_PRICE = Double.parseDouble(txt_RateShopPurchasingPrice.getText());
                    ConfigCharSetting.MAX_LEVEL = Integer.parseInt(txt_Max_Level.getText());
                    ConfigAlt.WHISPER_CHAT_LEVEL = (short) Integer.parseInt(txt_WhisperChatLevel.getText());
                    Eva.jSystemLogWindow.append("允許最大連線用戶 : " + Config.MAX_ONLINE_USERS + "\n", "Blue");
                    Eva.jSystemLogWindow.append("公頻廣播等級限制 : " + ConfigAlt.GLOBAL_CHAT_LEVEL + "\n", "Blue");
                    Eva.jSystemLogWindow.append("經驗倍率 : " + ConfigRate.RATE_XP + "\n", "Blue");
                    Eva.jSystemLogWindow.append("金幣倍率 : " + ConfigRate.RATE_DROP_ADENA + "\n", "Blue");
                    Eva.jSystemLogWindow.append("道具倍率 : " + ConfigRate.RATE_DROP_ITEMS + "\n", "Blue");
                    Eva.jSystemLogWindow.append("友好度倍率 : " + ConfigRate.RATE_KARMA + "\n", "Blue");
                    Eva.jSystemLogWindow.append("正義倍率 : " + ConfigRate.RATE_LA + "\n", "Blue");
                    Eva.jSystemLogWindow.append("角色負重倍率 : " + ConfigRate.RATE_WEIGHT_LIMIT + "\n", "Blue");
                    Eva.jSystemLogWindow.append("寵物負重倍率 : " + ConfigRate.RATE_WEIGHT_LIMIT_PET + "\n", "Blue");
                    Eva.jSystemLogWindow.append("武卷強化倍率 : " + ConfigRate.ENCHANT_CHANCE_WEAPON + "\n", "Blue");
                    Eva.jSystemLogWindow.append("防卷強化倍率 : " + ConfigRate.ENCHANT_CHANCE_ARMOR + "\n", "Blue");
                    Eva.jSystemLogWindow.append("武器屬性強化倍率 : " + ConfigRate.ATTR_ENCHANT_CHANCE + "\n", "Blue");
                    Eva.jSystemLogWindow.append("商店販賣價格倍率 : " + ConfigRate.RATE_SHOP_SELLING_PRICE + "\n", "Blue");
                    Eva.jSystemLogWindow.append("商店收購價格倍率 : " + ConfigRate.RATE_SHOP_PURCHASING_PRICE + "\n", "Blue");
                    Eva.jSystemLogWindow.append("最高等級限制 : " + ConfigCharSetting.MAX_LEVEL + "\n", "Blue");
                    Eva.jSystemLogWindow.append("密語等級限制 : " + ConfigAlt.WHISPER_CHAT_LEVEL + "\n", "Blue");
                    Eva.jSystemLogWindow.append("Config設置已變更." + "\n", "Blue");
                    JOptionPane.showMessageDialog(null, "Config設置已變更.", " Server Message", JOptionPane.INFORMATION_MESSAGE);
                    setClosed(true);
                } catch (Exception e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            }
        });
        // 關閉Config組件
        JButton btn_cancel = new JButton("關閉");
        btn_cancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    setClosed(true);
                } catch (Exception e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            }
        });
        // 組件水平
        GroupLayout.SequentialGroup main_horizontal_grp = layout.createSequentialGroup();
        GroupLayout.SequentialGroup horizontal_grp = layout.createSequentialGroup();
        GroupLayout.SequentialGroup vertical_grp = layout.createSequentialGroup();
        // 組件大綱
        GroupLayout.ParallelGroup main = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // 控件名稱(左邊)
        GroupLayout.ParallelGroup col1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // 控件數值(左邊)
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // 控件名稱(右邊)
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // 控件數值(右邊)
        GroupLayout.ParallelGroup col4 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        main.addGroup(horizontal_grp);
        main_horizontal_grp.addGroup(main);
        layout.setHorizontalGroup(main_horizontal_grp);
        layout.setVerticalGroup(vertical_grp);
        // 左邊名稱
        col1.addComponent(lbl_MaxUser).addComponent(lbl_Exp).addComponent(lbl_Item).addComponent(lbl_Lawful).addComponent(lbl_RateWeightLimitforPet).addComponent(lbl_ArmorEnchant).addComponent(lbl_RateShopSellingPrice).addComponent(lbl_Max_Level);
        // 左邊名稱數值
        col2.addComponent(txt_MaxUser).addComponent(txt_Exp).addComponent(txt_Item).addComponent(txt_Lawful).addComponent(txt_RateWeightLimitforPet).addComponent(txt_ArmorEnchant).addComponent(txt_RateShopSellingPrice).addComponent(txt_Max_Level);
        // 右邊名稱
        col3.addComponent(lbl_ChatLevel).addComponent(lbl_Adena).addComponent(lbl_Karma).addComponent(lbl_Weight).addComponent(lbl_WeaponEnchant).addComponent(lbl_AccessoryEnchant).addComponent(lbl_RateShopPurchasingPrice).addComponent(lbl_WhisperChatLevel);
        // 右邊名稱數值
        col4.addComponent(txt_ChatLevel).addComponent(txt_Adena).addComponent(txt_Karma).addComponent(txt_Weight).addComponent(txt_WeaponEnchant).addComponent(txt_AccessoryEnchant).addComponent(txt_RateShopPurchasingPrice).addComponent(txt_WhisperChatLevel);
        horizontal_grp.addGap(10).addContainerGap().addGroup(col1).addContainerGap();
        horizontal_grp.addGap(10).addContainerGap().addGroup(col2).addContainerGap();
        horizontal_grp.addGap(10).addContainerGap().addGroup(col3).addContainerGap();
        horizontal_grp.addGap(10).addContainerGap().addGroup(col4).addContainerGap();
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_MaxUser).addComponent(txt_MaxUser).addComponent(lbl_ChatLevel).addComponent(txt_ChatLevel));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Exp).addComponent(txt_Exp).addComponent(lbl_Adena).addComponent(txt_Adena));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Item).addComponent(txt_Item).addComponent(lbl_Karma).addComponent(txt_Karma));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Lawful).addComponent(txt_Lawful).addComponent(lbl_Weight).addComponent(txt_Weight));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_RateWeightLimitforPet).addComponent(txt_RateWeightLimitforPet).addComponent(lbl_WeaponEnchant).addComponent(txt_WeaponEnchant));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_ArmorEnchant).addComponent(txt_ArmorEnchant).addComponent(lbl_AccessoryEnchant).addComponent(txt_AccessoryEnchant));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_RateShopSellingPrice).addComponent(txt_RateShopSellingPrice).addComponent(lbl_RateShopPurchasingPrice).addComponent(txt_RateShopPurchasingPrice));
        vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Max_Level).addComponent(txt_Max_Level).addComponent(lbl_WhisperChatLevel).addComponent(txt_WhisperChatLevel));
        main.addGroup(layout.createSequentialGroup().addGap(130, 130, 130).addComponent(btn_ok).addGap(10).addComponent(btn_cancel));
        vertical_grp.addGap(15).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addGap(19, 19, 19).addComponent(btn_ok).addComponent(btn_cancel));
        // 自定義圖片
        Icon lbl_Note_icon = new ImageIcon("img/ConfigNote.png");
        // 特別提醒
        JLabel lbl_Note = new JLabel("此項設定為線上更改,重啟後將會重置.", lbl_Note_icon, JLabel.CENTER);
        // 設定字體為紅色
        lbl_Note.setForeground(Color.RED);
        main.addGroup(layout.createSequentialGroup().addComponent(lbl_Note));
        vertical_grp.addGroup(layout.createParallelGroup().addComponent(lbl_Note));
        // 自定義圖片
        Icon Author_icon = new ImageIcon("img/Author.png");
        // 作者信息
        JLabel lbl_Author = new JLabel("LINE:####  by:老爹 ", Author_icon, JLabel.CENTER);
        // 設定字體為藍色
        lbl_Author.setForeground(Color.blue);
        main.addGroup(layout.createSequentialGroup().addComponent(lbl_Author));
        vertical_grp.addGroup(layout.createParallelGroup().addComponent(lbl_Author));
    }
}
