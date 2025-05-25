package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.classes.L1ClassFeature;

/**
 * 重置設定
 *
 * @author admin
 */
public class S_CharReset extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 重置系統初始化
     *
     */
    public S_CharReset(final L1PcInstance pc) {
        writeC(S_VOICE_CHAT);
        writeC(0x01);
        if (pc.isCrown()) {
            writeH(14);
            writeH(2);
        } else if (pc.isKnight()) {
            writeH(16);
            writeH(1);
        } else if (pc.isElf()) {
            writeH(15);
            writeH(4);
        } else if (pc.isWizard()) {
            writeH(12);
            writeH(6);
        } else if (pc.isDarkelf()) {
            writeH(12);
            writeH(3);
        } else if (pc.isDragonKnight()) {
            writeH(15);
            writeH(4);
        } else if (pc.isIllusionist()) {
            writeH(15);
            writeH(4);
        } else if (pc.isWarrior()) {
            writeH(16);
            writeH(1);
        }
        writeC(0x0a);
        writeC(pc.getTempMaxLevel());
    }

    /**
     * 重置升級能力更新 [Server] opcode = 43 0000: 2b /02/ 01 2d/ 0f 00/ 04 00/ 0a 00
     * /0c 0c 0c 0c 12 09 +..-............
     */
    public S_CharReset(final L1PcInstance pc, final int lv, final int hp, final int mp, final int ac, final int str, final int intel, final int wis, final int dex, final int con, final int cha) {
        writeC(S_VOICE_CHAT);
        writeC(0x02);
        writeC(lv);
        writeC(pc.getTempMaxLevel()); // max lv
        writeH(hp);
        writeH(mp);
        writeH(ac);
        writeC(str);
        writeC(intel);
        writeC(wis);
        writeC(dex);
        writeC(con);
        writeC(cha);
    }

    /**
     * 萬能藥點數
     *
     */
    public S_CharReset(final int point) {
        writeC(S_VOICE_CHAT);
        writeC(0x03);
        writeC(point);
    }
    /**
     * 給予角色盟徽編號</br>
     * 『來源:伺服器』<位址:64>{長度:16}(時間:1607823495)</br>
     * 0000: 40 3c 15 ea 7a 00 33 b6 00 00 6a 6c cb 92 b5 2d @<..z.3...jl...-
     * 7.6 取消
     */
	/*public S_CharReset(int pcObjId, int emblemId) {
		writeC(S_OPCODE_CHARRESET);
		writeC(0x3c);
		writeD(pcObjId);
		writeD(emblemId);
	}*/

    /**
     * 初始能力加成 7.6
     *
     */
    public S_CharReset(final L1PcInstance pc, final int type) {
        final int baseStr = L1ClassFeature.ORIGINAL_STR[pc.getType()];
        final int baseDex = L1ClassFeature.ORIGINAL_DEX[pc.getType()];
        final int baseCon = L1ClassFeature.ORIGINAL_CON[pc.getType()];
        final int baseWis = L1ClassFeature.ORIGINAL_WIS[pc.getType()];
        final int baseCha = L1ClassFeature.ORIGINAL_CHA[pc.getType()];
        final int baseInt = L1ClassFeature.ORIGINAL_INT[pc.getType()];
        final int originalStr = pc.getOriginalStr();
        final int originalDex = pc.getOriginalDex();
        final int originalCon = pc.getOriginalCon();
        final int originalWis = pc.getOriginalWis();
        final int originalCha = pc.getOriginalCha();
        final int originalInt = pc.getOriginalInt();
        final int upStr = originalStr - baseStr;
        final int upDex = originalDex - baseDex;
        final int upCon = originalCon - baseCon;
        final int upWis = originalWis - baseWis;
        final int upCha = originalCha - baseCha;
        final int upInt = originalInt - baseInt;
        writeC(S_VOICE_CHAT);
        writeC(type);
        writeC((upInt << 4) + upStr);
        writeC((upDex << 4) + upWis);
        writeC((upCha << 4) + upCon);
        writeC(0x00);
        writeH(0x00);
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
        return this.getClass().getSimpleName();
    }
}
