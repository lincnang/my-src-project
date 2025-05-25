package com.lineage.server.serverpackets;

import com.lineage.config.ConfigKill;
import com.lineage.config.ConfigSay;

import java.util.Random;

/**
 * 殺人公告
 *
 * @author dexc
 */
public class S_KillMessage extends ServerBasePacket {
    private static final Random _random = new Random();
    private byte[] _byte = null;

    /**
     * 殺人公告
     *
     */
    public S_KillMessage(String winName, String deathName) {
        if (ConfigSay.KILL_Position) {
            writeC(S_MESSAGE); // 螢幕
            writeC(84);
            writeC(2);
            String x1 = (String) ConfigKill.KILL_TEXT_LIST.get(Integer.valueOf(_random.nextInt(ConfigKill.KILL_TEXT_LIST.size()) + 1));
            writeS(String.format(x1, new Object[]{winName, deathName}));
        } else {
            writeC(S_MESSAGE); // 聊天
            writeC(0);
            writeD(0);
            String x1 = (String) ConfigKill.KILL_TEXT_LIST.get(Integer.valueOf(_random.nextInt(ConfigKill.KILL_TEXT_LIST.size()) + 1));
            writeS(String.format(x1, new Object[]{winName, deathName}));
        }
    }

    /**
     * 賭場NPC對話
     *
     */
    public S_KillMessage(String name, String msg, int i) {
        writeC(S_MESSAGE);
        writeC(0);
        writeD(0);
        writeS(" \\fY[" + name + "] " + msg);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
