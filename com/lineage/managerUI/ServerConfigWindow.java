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

public class ServerConfigWindow extends JInternalFrame {
    private static final Log _log = LogFactory.getLog(ServerConfigWindow.class);

    private JLabel lbl_MaxUser, lbl_Exp, lbl_Item, lbl_Lawful, lbl_RateWeightLimitforPet, lbl_ArmorEnchant, lbl_RateShopSellingPrice, lbl_Max_Level;
    private JLabel lbl_ChatLevel, lbl_Adena, lbl_Karma, lbl_Weight, lbl_WeaponEnchant, lbl_AccessoryEnchant, lbl_RateShopPurchasingPrice, lbl_WhisperChatLevel;
    private JTextField txt_MaxUser, txt_Exp, txt_Item, txt_Lawful, txt_RateWeightLimitforPet, txt_ArmorEnchant, txt_RateShopSellingPrice, txt_Max_Level;
    private JTextField txt_ChatLevel, txt_Adena, txt_Karma, txt_Weight, txt_WeaponEnchant, txt_AccessoryEnchant, txt_RateShopPurchasingPrice, txt_WhisperChatLevel;

    public ServerConfigWindow() {
        super();
        initialize();
    }

    public void initialize() {
        File PictureAuthor = new File("img/Author.png");
        String AuthorSize = String.format("%.1f", PictureAuthor.length() / 1024.0);
        double AuthorSizeDouble = Double.parseDouble(AuthorSize);
        if (AuthorSizeDouble != 4.4) {
            JOptionPane.showMessageDialog(null, "檢測到img發生變化.請勿隨意更改.強制關閉.", " 作者-老爹", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            try { setClosed(true); } catch (Exception e) { _log.error(e.getLocalizedMessage(), e); }
            return;
        }
        setTitle("Config設置");
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        setSize(600, 400); // 可自行調整預設大小
        setBounds(350, 0, 600, 400);
        setVisible(true);
        setFrameIcon(new ImageIcon(""));
        setRootPaneCheckingEnabled(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // 特別提醒
        Icon lbl_Note_icon = new ImageIcon("img/ConfigNote.png");
        JLabel lbl_Note = new JLabel("此項設定為線上更改,重啟後將會重置.", lbl_Note_icon, JLabel.CENTER);
        lbl_Note.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        mainPanel.add(lbl_Note, gbc);
        gbc.gridwidth = 1;

        // 欄位名稱與輸入欄位 (分左右兩欄)
        String[] labels = { "允許最大連線用戶", "經驗倍率", "道具倍率", "正義倍率", "寵物負重倍率", "防卷強化倍率", "商店販賣價格倍率", "最高等級限制" };
        String[] labelsR = { "公頻廣播等級限制", "金幣倍率", "友好度倍率", "角色負重倍率", "武卷強化倍率", "武器屬性強化倍率", "商店收購價格倍率", "密語等級限制" };
        JTextField[] fieldsL = new JTextField[] {
                txt_MaxUser = new JTextField(), txt_Exp = new JTextField(), txt_Item = new JTextField(), txt_Lawful = new JTextField(),
                txt_RateWeightLimitforPet = new JTextField(), txt_ArmorEnchant = new JTextField(), txt_RateShopSellingPrice = new JTextField(), txt_Max_Level = new JTextField()
        };
        JTextField[] fieldsR = new JTextField[] {
                txt_ChatLevel = new JTextField(), txt_Adena = new JTextField(), txt_Karma = new JTextField(), txt_Weight = new JTextField(),
                txt_WeaponEnchant = new JTextField(), txt_AccessoryEnchant = new JTextField(), txt_RateShopPurchasingPrice = new JTextField(), txt_WhisperChatLevel = new JTextField()
        };

        // 設置顏色
        for (JTextField tf : fieldsL) tf.setForeground(Color.blue);
        for (JTextField tf : fieldsR) tf.setForeground(Color.blue);

        // 設定內容值
        txt_MaxUser.setText(Config.MAX_ONLINE_USERS + "");
        txt_Exp.setText(ConfigRate.RATE_XP + "");
        txt_Item.setText(ConfigRate.RATE_DROP_ITEMS + "");
        txt_Lawful.setText(ConfigRate.RATE_LA + "");
        txt_RateWeightLimitforPet.setText(ConfigRate.RATE_WEIGHT_LIMIT_PET + "");
        txt_ArmorEnchant.setText(ConfigRate.ENCHANT_CHANCE_ARMOR + "");
        txt_RateShopSellingPrice.setText(ConfigRate.RATE_SHOP_SELLING_PRICE + "");
        txt_Max_Level.setText(ConfigCharSetting.MAX_LEVEL + "");

        txt_ChatLevel.setText(ConfigAlt.GLOBAL_CHAT_LEVEL + "");
        txt_Adena.setText(ConfigRate.RATE_DROP_ADENA + "");
        txt_Karma.setText(ConfigRate.RATE_KARMA + "");
        txt_Weight.setText(ConfigRate.RATE_WEIGHT_LIMIT + "");
        txt_WeaponEnchant.setText(ConfigRate.ENCHANT_CHANCE_WEAPON + "");
        txt_AccessoryEnchant.setText(ConfigRate.ATTR_ENCHANT_CHANCE + "");
        txt_RateShopPurchasingPrice.setText(ConfigRate.RATE_SHOP_PURCHASING_PRICE + "");
        txt_WhisperChatLevel.setText(ConfigAlt.WHISPER_CHAT_LEVEL + "");

        // 加入欄位
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1;
            gbc.weightx = 0.3;
            mainPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            mainPanel.add(fieldsL[i], gbc);
            gbc.gridx = 2; gbc.weightx = 0.3;
            mainPanel.add(new JLabel(labelsR[i]), gbc);
            gbc.gridx = 3; gbc.weightx = 0.7;
            mainPanel.add(fieldsR[i], gbc);
        }

        // 儲存與關閉按鈕
        JButton btn_ok = new JButton("保存設置");
        JButton btn_cancel = new JButton("關閉");
        gbc.gridx = 1; gbc.gridy = labels.length + 2;
        gbc.gridwidth = 1;
        mainPanel.add(btn_ok, gbc);
        gbc.gridx = 2;
        mainPanel.add(btn_cancel, gbc);

        // 作者信息
        Icon Author_icon = new ImageIcon("img/Author.png");
        JLabel lbl_Author = new JLabel("LINE:####  by:老爹 ", Author_icon, JLabel.CENTER);
        lbl_Author.setForeground(Color.blue);
        gbc.gridx = 0; gbc.gridy = labels.length + 3;
        gbc.gridwidth = 4;
        mainPanel.add(lbl_Author, gbc);

        // 按鈕事件
        btn_ok.addActionListener(evt -> {
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
        });
        btn_cancel.addActionListener(evt -> {
            try { setClosed(true); } catch (Exception e) { _log.error(e.getLocalizedMessage(), e); }
        });
        setContentPane(mainPanel);
    }
}
