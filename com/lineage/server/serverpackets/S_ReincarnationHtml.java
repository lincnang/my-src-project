package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import william.ReincarnationSkill;

/**
 * 轉生天賦
 */
public class S_ReincarnationHtml extends ServerBasePacket {
    /**
     * 轉生天賦
     *
     * @param pc
     */
    public S_ReincarnationHtml(L1PcInstance pc) {
        int rei_pt = 0;
        int[] is = pc.getReincarnationSkill();
        for (int i = 0; i < is.length; i++) {
            rei_pt += is[i];
        }
        rei_pt = pc.getTurnLifeSkillCount() - rei_pt;
        writeC(S_HYPERTEXT);
        writeD(pc.getId());
        writeS("Rei_" + pc.getType());
        writeH(1);
        writeH(7);
        writeS(Integer.toString(rei_pt));
        for (int i = 0; i < is.length; i++) {
            writeS(ReincarnationSkill.SKILLS[pc.getType()][i]);
            writeS(Integer.toString(is[i]));
        }
    }

    public final byte[] getContent() {
        return getBytes();
    }
}
