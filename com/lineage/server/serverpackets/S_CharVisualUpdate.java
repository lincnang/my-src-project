package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

/**
 * 物件動作種類(長時間)
 *
 * @author dexc
 */
public class S_CharVisualUpdate extends ServerBasePacket {
    private byte[] _byte = null;

    public S_CharVisualUpdate(final int objid, final int weaponType) {
        writeC(S_WIELD);
        writeD(objid);
        writeC(weaponType);
    }

    public S_CharVisualUpdate(final L1Character cha, final int status) {
        writeC(S_WIELD);
        writeD(cha.getId());
        writeC(status);
        writeC(0xff);
        writeC(0xff);
    }

    public S_CharVisualUpdate(final L1PcInstance cha) {
        writeC(S_WIELD);
        writeD(cha.getId());
        writeC(cha.getCurrentWeapon());
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}
