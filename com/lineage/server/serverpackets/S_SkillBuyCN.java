package com.lineage.server.serverpackets;

import com.lineage.data.event.SkillTeacherSet;
import com.lineage.list.PcLvSkillList;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

/**
 * 魔法購買(金幣 / 全職技能導師)
 *
 * @author dexc pctype 0:王族 1:騎士 2:精靈 3:法師 4:黑妖 5:龍騎 6:幻術
 */
public class S_SkillBuyCN extends ServerBasePacket {
    // 職業定義初始化價格(0:王族 1:騎士 2:精靈 3:法師 4:黑妖 5:龍騎 6:幻術)
    public static final int[] PCTYPE = new int[]{2150, 2450, 1800, 5580, 1950, 620, 470};
    private static final Log _log = LogFactory.getLog(S_SkillBuyCN.class);
    private byte[] _byte = null;

    /**
     * 魔法購買(金幣 / 全職技能導師)
     *
     */
    public S_SkillBuyCN(final L1PcInstance pc, final L1NpcInstance npc) {
        ArrayList<Integer> skillList = null;
        if (pc.isCrown()) {// 王族
            skillList = PcLvSkillList.isCrown(pc);
        } else if (pc.isKnight()) {// 騎士
            skillList = PcLvSkillList.isKnight(pc);
        } else if (pc.isElf()) {// 精靈
            skillList = PcLvSkillList.isElf(pc);
        } else if (pc.isWizard()) {// 法師
            skillList = PcLvSkillList.isWizard(pc);
        } else if (pc.isDarkelf()) {// 黑妖
            skillList = PcLvSkillList.isDarkelf(pc);
        } else if (pc.isDragonKnight()) {// 龍騎
            skillList = PcLvSkillList.isDragonKnight(pc);
        } else if (pc.isIllusionist()) {// 幻術
            skillList = PcLvSkillList.isIllusionist(pc);
        } else if (pc.isWarrior()) {// 戰士
            skillList = PcLvSkillList.isWarrior(pc);
        }
        final ArrayList<Integer> newSkillList = new ArrayList<>();
        // 排除不予學習的技能
        for (final Integer integer : skillList) {
            if (SkillTeacherSet.RESKILLLIST.get(integer) == null) {
                // 檢查是否已學習該法術
                if (!CharSkillReading.get().spellCheck(pc.getId(), (integer + 1))) {
                    newSkillList.add(integer);
                }
            }
        }
        if (newSkillList.size() <= 0) {// 全職技能導師
            this.writeC(S_HYPERTEXT);
            this.writeD(npc.getId());
            this.writeS("y_skill_02");
            this.writeH(0x00);
            this.writeH(0x00);
        } else {
            final int startAdena = PCTYPE[pc.getType()];
            try {
                this.writeC(S_BUYABLE_SPELL_LIST);
                this.writeD(startAdena);
                this.writeH(newSkillList.size());
                for (final Integer integer : newSkillList) {
                    this.writeD(integer);
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
