package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

public class S_CharVisualUpdate_old extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 要求更新物件外型
     *
     * @param objid
     * @param weaponType
     */
    public S_CharVisualUpdate_old(int objid, int weaponType) {
        writeC(S_WIELD);
        writeD(objid);
        writeC(weaponType);
    }

    /**
     * 要求更新物件外型
     *
     * @param cha
     */
    public S_CharVisualUpdate_old(L1PcInstance cha) {
        writeC(S_WIELD);
        writeD(cha.getId());
        if (cha.getWeapon() != null) {
            int weapontype = cha.getWeapon().getItem().getType1();
            if (weapontype == 24) {// 兩格類型武器
                switch (cha.getTempCharGfx()) {
                    case 9206:// 80死騎
                    case 9205:// 75死騎
                    case 6157:// 70死騎
                    case 6152:// 65死騎
                    case 6147:// 60死騎
                    case 6142:// 55死騎
                    case 6137:// 52死騎
                    case 12229:// 黑闇死騎
                    case 12230:// 雷霆死騎
                    case 12231:// 黃金死騎
                    case 12232:// 焚焰死騎
                    case 12681:// 真死騎
                    case 13152:// 雷霆真死騎
                    case 13153:// 焚焰真死騎
                    case 12280:// 格立特
                    case 12283:// 特羅斯
                        writeC(83);
                        cha.sendPackets(new S_SkillIconGFX(cha.getTempCharGfx(), true));
                        break;
                    default:
                        writeC(cha.getCurrentWeapon());
                        cha.sendPackets(new S_SkillIconGFX(cha.getTempCharGfx(), false));
                        break;
                }
            } else {
                writeC(cha.getCurrentWeapon());
            }
            writeC(0xff);
            writeC(0xff);
        }
    }

    /**
     * 要求更新物件外型
     *
     * @param cha
     * @param status
     */
    public S_CharVisualUpdate_old(L1Character cha, int status) {
        writeC(S_WIELD);
        writeD(cha.getId());
        writeC(status);
        writeC(255);
        writeC(255);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_CharVisualUpdate JD-Core Version: 0.6.2
 */