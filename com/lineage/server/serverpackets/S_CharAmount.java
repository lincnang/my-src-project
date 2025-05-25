package com.lineage.server.serverpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;

public class S_CharAmount extends ServerBasePacket {
    private byte[] _byte = null;

    public S_CharAmount(int value, ClientExecutor client) {
        buildPacket(value, client);
    }

    private void buildPacket(int value, ClientExecutor client) {
        int characterSlot = client.getAccount().get_character_slot();
        int maxAmount = ConfigAlt.DEFAULT_CHARACTER_SLOT + characterSlot;
        writeC(S_NUM_CHARACTER);
        writeC(value);
        writeC(maxAmount);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_CharAmount JD-Core Version: 0.6.2
 */