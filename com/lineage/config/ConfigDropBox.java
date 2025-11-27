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
    private static final Log _log = LogFactory.getLog(ConfigDropBox.class);
    private static final Map<Integer, String> _box_msg_list = new HashMap<>();
    private static final Random _random = new Random();
    private static final String _drop_text = "./config/☆服務器設定表☆/掉寶公告設定表.txt";
    public static boolean ISMSG = false;

    public static void load() throws ConfigErrorException {
        try {
            // 取回檔案
            final InputStream is = Files.newInputStream(new File(_drop_text).toPath());
            // 指定檔案編碼
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            final LineNumberReader lnr = new LineNumberReader(isr);
            boolean isWhile = false;
            int i = 1;
            String desc = null;
            while ((desc = lnr.readLine()) != null) {
                if (!isWhile) {// 忽略第一行
                    isWhile = true;
                    continue;
                }
                if ((desc.trim().length() == 0) || desc.startsWith("#")) {
                    continue;
                }
                if (desc.startsWith("ISMSG")) {
                    desc = desc.replaceAll(" ", "");// 取代空白
                    ISMSG = Boolean.parseBoolean(desc.substring(6));
                } else {
                    _box_msg_list.put(i++, desc);
                }
            }
            is.close();
            isr.close();
            lnr.close();
        } catch (final Exception e) {
            _log.error("設置檔案遺失: " + _drop_text);
        }
    }

    // 掉寶公告
    public static void msg(final String playerName, final String npcName, final String itemName) {
        if (_box_msg_list.isEmpty()) {
            return;
        }
        try {
            final String template = _box_msg_list.get(_random.nextInt(_box_msg_list.size()) + 1);
            String message = template;
            try {
                message = String.format(template, playerName, npcName, itemName);
            } catch (final IllegalFormatException ignored) {
                // fallback to default格式
                message = "\\f=恭喜玩家【" + playerName + "】從【" + npcName + "】獲得【" + itemName + "】";
            }

            if (ConfigSay.DROP_Position) {
                World.get().broadcastPacketToAll(new S_PacketBoxGree(message));
                switch (_random.nextInt(4) + 1) {
                    case 1:
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(0x01));
                        break;
                    case 2:
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(6));
                        break;
                    case 3:
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(7));
                        break;
                    case 4:
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(8));
                        break;
                }
            } else {
                World.get().broadcastPacketToAll(new S_PacketBoxGree(message));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
