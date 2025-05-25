package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1SkillEnhance;

public class ADVANCE_SPIRIT extends SkillMode {

    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!cha.hasSkillEffect(57)) {
            L1PcInstance pc = (L1PcInstance) cha;
            // 原本基準加成值: 最大 HP/MP 的 1/5
            int baseBonusHp = pc.getBaseMaxHp() / 5;
            int baseBonusMp = pc.getBaseMaxMp() / 5;

            // 預設 bonus 就是基準值
            int bonusHp = baseBonusHp;
            int bonusMp = baseBonusMp;

            // 取得該技能的技能等級，假設技能 ID 與效果代號 57 相同
            int bookLevel = pc.getSkillLevel(57);

            // 從 SkillEnhanceTable 讀取 ADVANCE_SPIRIT 的增強資料
            L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(57, bookLevel);
            if (enhanceData != null) {
                // 將設定值除以 100 得到百分比比例
                double extraHpRatio = enhanceData.getSetting1() / 100.0;
                double extraMpRatio = enhanceData.getSetting2() / 100.0;
                bonusHp += (int) (baseBonusHp * extraHpRatio);
                bonusMp += (int) (baseBonusMp * extraMpRatio);

                // 提示目前強化技能等級及加成百分比
                String msg = "強化技能等級：Lv." + bookLevel
                        + "，加成百分比：HP +" + enhanceData.getSetting1() + "%，MP +" + enhanceData.getSetting2() + "%";
                pc.sendPackets(new S_SystemMessage(msg));
            }

            pc.setAdvenHp(bonusHp);
            pc.setAdvenMp(bonusMp);
            pc.addMaxHp(pc.getAdvenHp());
            pc.addMaxMp(pc.getAdvenMp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        cha.setSkillEffect(57, integer * 1000);
        return 0;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (!cha.hasSkillEffect(57)) {
            L1PcInstance pc = (L1PcInstance) cha;
            int baseBonusHp = pc.getBaseMaxHp() / 5;
            int baseBonusMp = pc.getBaseMaxMp() / 5;

            int bonusHp = baseBonusHp;
            int bonusMp = baseBonusMp;

            int bookLevel = pc.getSkillLevel(57);
            L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(57, bookLevel);
            if (enhanceData != null) {
                double extraHpRatio = enhanceData.getSetting1() / 100.0;
                double extraMpRatio = enhanceData.getSetting2() / 100.0;
                bonusHp += (int) (baseBonusHp * extraHpRatio);
                bonusMp += (int) (baseBonusMp * extraMpRatio);

                String msg = "強化技能等級：lv" + bookLevel
                        + "，加成百分比：HP +" + enhanceData.getSetting1() + "%，MP +" + enhanceData.getSetting2() + "%";
                pc.sendPackets(new S_SystemMessage(msg));
            }

            pc.setAdvenHp(bonusHp);
            pc.setAdvenMp(bonusMp);
            pc.addMaxHp(pc.getAdvenHp());
            pc.addMaxMp(pc.getAdvenMp());
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        cha.setSkillEffect(57, integer * 1000);
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
        // 保留原本架構
    }

    public void stop(L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addMaxHp(-pc.getAdvenHp());
            pc.addMaxMp(-pc.getAdvenMp());
            pc.setAdvenHp(0); // 清空加成值
            pc.setAdvenMp(0);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
    }
}
