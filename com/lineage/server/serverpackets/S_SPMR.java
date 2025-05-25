package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 更新魔攻與魔防
 *
 * @author dexc
 */
public class S_SPMR extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SPMR(L1PcInstance pc, boolean isLogin) {
        writeC(S_MAGIC_STATUS);
        writeH(pc.getTrueSp()); // 魔攻
        writeH(pc.getBaseMr()); // 魔防
    }

    /**
     * 更新魔攻與魔防
     *
     */
    public S_SPMR(L1PcInstance pc) {
        buildPacket(pc);
    }

    /**
     * 更新魔攻與魔防 - 測試
     *
     */
    public S_SPMR() {
        writeC(S_MAGIC_STATUS);
        writeH(50);
        writeH(100);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(S_MAGIC_STATUS);
        int sp = pc.getSp() - pc.getTrueSp();
		/*if (pc.hasSkillEffect(1004)) {// 慎重藥水效果
			sp -= 2;
		}*/
        int mr = pc.getTrueMr() - pc.getBaseMr();
        switch (pc.guardianEncounter()) {
            case 0: // 正義的守護 Lv.1
                mr += 3;
                break;
            case 1: // 正義的守護 Lv.2
                mr += 6;
                break;
            case 2: // 正義的守護 Lv.3
                mr += 9;
                break;
            case 3: // 邪惡的守護 Lv.1
                //sp++;
                sp += 1;
                break;
            case 4: // 邪惡的守護 Lv.2
                sp += 2;
                break;
            case 5: // 邪惡的守護 Lv.3
                sp += 3;
                break;
        }
        writeH(sp); // 魔攻
        writeH(mr); // 魔防
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
