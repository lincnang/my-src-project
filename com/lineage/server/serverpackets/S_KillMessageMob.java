package com.lineage.server.serverpackets;

import com.lineage.config.ConfigMobKill;

import java.util.Random;

public final class S_KillMessageMob//src014
        extends ServerBasePacket {
    private static final Random _random = new Random();
    private byte[] _byte = null;

    public S_KillMessageMob(String winName, String deathName) {
        writeC(S_MESSAGE);
        //	writeC(0);
        //	writeD(0);
        writeC(9);
        String x1 = (String) ConfigMobKill.MOBKILL_TEXT_LIST.get(Integer.valueOf(_random.nextInt(ConfigMobKill.MOBKILL_TEXT_LIST.size()) + 1));
        writeS(String.format(x1, new Object[]{winName, deathName}));
    }

    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = getBytes();
        }
        return this._byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
