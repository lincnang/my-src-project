package com.lineage.config;

import com.lineage.server.serverpackets.S_BoxMessage;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfigBoxMsg {
    private static final Log _log = LogFactory.getLog(ConfigBoxMsg.class);
    private static final Map<Integer, String> _box_msg_list = new HashMap<>();
    private static final Random _random = new Random();
    private static final String _box_text = "./config/☆服務器設定表☆/寶箱開啟公告設定表.txt";
    public static boolean ISMSG = false;

    public static void load() throws ConfigErrorException {
        try {
            // 取回檔案
            final InputStream is = Files.newInputStream(new File(_box_text).toPath());
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
            _log.error("設置檔案遺失: " + _box_text);
        }
    }

    public static void msg(final String string1, final String string2, final String string3) {
        try {
            final String msg = _box_msg_list.get(_random.nextInt(_box_msg_list.size()) + 1);
            if (msg != null) {
                final String out = String.format(msg, string1, string2, string3);
                if (ConfigSay.BOX_Position) {
                    World.get().broadcastPacketToAlldroplist(new S_PacketBoxGree(2, out));
                } else {
                    World.get().broadcastPacketToAlldroplist(new S_BoxMessage(out));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
