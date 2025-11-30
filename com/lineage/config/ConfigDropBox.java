package com.lineage.config;

import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Random;

public class ConfigDropBox {
    // 日誌記錄器
    private static final Log _log = LogFactory.getLog(ConfigDropBox.class);
    // 儲存掉寶公告訊息模板的Map，key為序號，value為訊息模板
    private static final Map<Integer, String> _box_msg_list = new HashMap<>();
    // 隨機數生成器，用於隨機選擇公告訊息和螢幕特效
    private static final Random _random = new Random();
    // 掉寶公告設定檔案路徑
    private static final String _drop_text = "./config/☆服務器設定表☆/掉寶公告設定表.txt";

    /** 掉寶公告系統開關 (true=啟用, false=停用) */
    public static boolean ISMSG = false;

    /** S_PacketBoxGree螢幕特效開關 (true=啟用特效, false=只發文字訊息) */
    public static boolean ISBOXGREE = true;

      /**
     * 載入掉寶公告設定檔案
     * 讀取 ./config/☆服務器設定表☆/掉寶公告設定表.txt 檔案並解析設定
     * @throws ConfigErrorException 當檔案不存在或讀取失敗時拋出異常
     */
    public static void load() throws ConfigErrorException {
        try {
            // 開啟設定檔案
            final InputStream is = Files.newInputStream(new File(_drop_text).toPath());
            // 指定UTF-8編碼以支援中文
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            final LineNumberReader lnr = new LineNumberReader(isr);

            boolean isFirstLine = true; // 標記是否為第一行（標題行）
            int messageIndex = 1;      // 訊息模板的序號，從1開始
            String lineContent = null; // 當前讀取的行內容

            // 逐行讀取設定檔案
            while ((lineContent = lnr.readLine()) != null) {
                // 忽略第一行（標題行）
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 忽略空行和註解行（以#開頭）
                if ((lineContent.trim().length() == 0) || lineContent.startsWith("#")) {
                    continue;
                }

                // 解析系統設定
                if (lineContent.startsWith("ISMSG")) {
                    // 解析掉寶公告系統開關：ISMSG = true/false
                    lineContent = lineContent.replaceAll(" ", ""); // 移除空白字元
                    ISMSG = Boolean.parseBoolean(lineContent.substring(6));

                } else if (lineContent.startsWith("ISBOXGREE") || lineContent.startsWith("ISBoxGree")) {
                    // 解析S_PacketBoxGree螢幕特效開關：ISBOXGREE/ISBoxGree = true/false

                    lineContent = lineContent.replaceAll(" ", ""); // 移除空白字元
                    int equalsIndex = lineContent.indexOf("=");
                    if (equalsIndex != -1) {
                        String value = lineContent.substring(equalsIndex + 1).trim();

                        // 手動解析布林值，避免解析問題
                        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("no")) {
                            ISBOXGREE = false;
                        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("yes")) {
                            ISBOXGREE = true;
                        } else {
                            _log.warn("無效的ISBOXGREE值: " + value + "，使用預設值 false");
                            ISBOXGREE = false;
                        }
                    } else {
                        _log.warn("ISBOXGREE設定格式錯誤，找不到等號: " + lineContent);
                    }

                } else {
                    // 將其他行作為公告訊息模板儲存
                    _box_msg_list.put(messageIndex++, lineContent);
                }
            }

            // 關閉檔案資源
            is.close();
            isr.close();
            lnr.close();

        } catch (final Exception e) {
            _log.error("掉寶公告設定檔案載入失敗: " + _drop_text, e);
            throw new ConfigErrorException("設置檔案遺失: " + _drop_text);
        }
    }

    /**
     * 發送掉寶公告訊息
     * 根據設定隨機選擇公告訊息模板，並發送給所有線上玩家
     *
     * @param playerName 獲得物品的玩家名稱 (第一個%s)
     * @param npcName     被擊敗的NPC名稱 (第二個%s)
     * @param itemName    獲得的物品名稱 (第三個%s)
     */
    public static void msg(final String playerName, final String npcName, final String itemName) {
        // 檢查是否有公告訊息模板
        if (_box_msg_list.isEmpty()) {
            _log.warn("沒有可用的掉寶公告訊息模板");
            return;
        }

        try {
            // 隨機選擇一則公告訊息模板
            final String template = _box_msg_list.get(_random.nextInt(_box_msg_list.size()) + 1);
            String formattedMessage = template;

            // 嘗試格式化訊息，替換%s參數
            try {
                formattedMessage = String.format(template, playerName, npcName, itemName);
            } catch (final IllegalFormatException e) {
                // 格式化失敗時使用預設格式
                _log.warn("公告訊息格式化失敗，使用預設格式: " + template);
                formattedMessage = "\\f=恭喜玩家【" + playerName + "】從【" + npcName + "】獲得【" + itemName + "】";
            }

            // 根據ISBOXGREE設定決定發送公告的類型 (ISBOXGREE完全控制特效)
            if (ISBOXGREE) {
                // 啟用螢幕特效：發送文字訊息 + 隨機螢幕特效
                World.get().broadcastPacketToAll(new S_PacketBoxGree(formattedMessage));

                // 隨機選擇螢幕特效 (4種特效)
                switch (_random.nextInt(4) + 1) {
                    case 1:
                        // 特效1: 螢幕框特效-紫
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(0x01));
                        break;
                    case 2:
                        // 特效2: 螢幕標框特效-藍
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(6));
                        break;
                    case 3:
                        // 特效3: 螢幕標框特效-黃
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(7));
                        break;
                    case 4:
                        // 特效4: 螢幕標框特效-白
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(8));
                        break;
                }

            } else {
                // 只發送文字訊息，不顯示螢幕特效
                World.get().broadcastPacketToAll(new S_PacketBoxGree(formattedMessage));
            }

        } catch (final Exception e) {
        }
    }
}
