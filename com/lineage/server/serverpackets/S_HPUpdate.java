package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.RangeInt;

public class S_HPUpdate extends ServerBasePacket {
    private static final RangeInt _hpRange = new RangeInt(1, 32767);
    private byte[] _byte = null;

    public S_HPUpdate(int currentHp, int maxHp) {
        buildPacket(currentHp, maxHp);
    }

    public S_HPUpdate(L1PcInstance pc) {
        buildPacket(pc.getCurrentHp(), pc.getMaxHp());
    }

    public void buildPacket(int currentHp, int maxHp) {
        writeC(S_HIT_POINT);
        writeH(_hpRange.ensure(currentHp));
        writeH(_hpRange.ensure(maxHp));
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
 * com.lineage.server.serverpackets.S_HPUpdate JD-Core Version: 0.6.2
 */