package com.lineage.item_etcitem;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.templates.L1Skills;

import static com.lineage.server.model.skill.L1SkillId.DECREASE_WEIGHT;
import static com.lineage.server.model.skill.L1SkillId.JOY_OF_PAIN;

public class SkillScroll {
    /**
     * 自動技能新增 -> 使用技能 會損耗需求 判斷
     *
     * @param pc
     * @param skillid
     */
    public static void DoMySkill1(L1PcInstance pc, int skillid) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillid);
        if (pc.getCurrentHp() + 1 < l1skills.getHpConsume() + 1) {
            //pc.sendPackets(new S_ServerMessage(279)); // \F2魔法：無法使用（HP不足）
            return;
        }
        if (pc.getCurrentMp() < l1skills.getMpConsume()) {
            //pc.sendPackets(new S_ServerMessage(278)); // \F2魔法：無法使用（MP不足）
            return;
        }
        if (l1skills.getItemConsumeId() != 0) {
            if (!pc.getInventory().checkItem(l1skills.getItemConsumeId(), l1skills.getItemConsumeCount())) {
                //L1Item temp = ItemTable.get().getTemplate(l1skills.getItemConsumeId());
                //pc.sendPackets(new S_SystemMessage("施放此技能需要【" + temp.getName() + "】"));
                return;
            }
        }
        L1SkillUse l1skilluse = new L1SkillUse();
        l1skilluse.handleCommands(pc, skillid, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_SPELLSC);// 類型:魔法卷軸 - 有施法動作
        int lawful = pc.getLawful() + l1skills.getLawful();
        if (lawful > 32767) {
            lawful = 32767;
        }
        if (lawful < -32767) {
            lawful = -32767;
        }
        pc.setLawful(lawful);
        if (l1skills.getItemConsumeId() != 0) {
            pc.getInventory().consumeItem(l1skills.getItemConsumeId(), l1skills.getItemConsumeCount());
        }
        pc.setCurrentHp(pc.getCurrentHp() - l1skills.getHpConsume());
        pc.setCurrentMp(pc.getCurrentMp() - l1skills.getMpConsume());
        if (skillid == DECREASE_WEIGHT || skillid == JOY_OF_PAIN) {
            // XXX 7.6 重量程度資訊
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
        }
    }
}
