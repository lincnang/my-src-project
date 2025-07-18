package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_NewSkillIcon;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;

import static com.lineage.server.model.skill.L1SkillId.Blood_strength;

/**
 * 力量之血 232
 * Buff、技能判斷、技能圖示、攻擊吸血 全部統一用 232
 */
public class Blood_strength extends SkillMode {
    @Override
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        // 取得技能設定（假設技能ID是 Blood_strength）
        L1Skills skill = SkillsTable.get().getTemplate(Blood_strength);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;

        // 判斷並扣除材料
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                // 可加訊息提示
                srcpc.sendPackets(new S_ServerMessage("施放需要的道具不足，無法啟動力量之血！"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }
        // 先移除對方的 buff（通常是自保/防止重疊）
        if (cha.hasSkillEffect(Blood_strength)) {
            cha.killSkillEffectTimer(Blood_strength);
        }
        // 角色自身 buff 也移除
        if (srcpc.hasSkillEffect(Blood_strength)) {
            srcpc.removeSkillEffect(Blood_strength);
        }
        // 設定 buff 時間，720 秒（12分鐘）
        srcpc.setSkillEffect(Blood_strength, 720 * 1000);
        srcpc.sendPackets(new S_ServerMessage("攻擊[附加吸血效果]"));
        srcpc.sendPackets(new S_OwnCharStatus2(srcpc));
        srcpc.sendPackets(new S_SPMR(srcpc));
        srcpc.sendPackets(new S_NewSkillIcon(Blood_strength, true, 720));
        return dmg;
    }

    @Override
    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // NPC施法版本（可以保留原樣，不處理）
        return 0;
    }

    // buff結束自動移除
    @Override
    public void stop(L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_NewSkillIcon(Blood_strength, false, 0));
            pc.sendPackets(new S_ServerMessage("力量之血效果結束。"));
            pc.sendPackets(new S_OwnCharStatus2(pc));
            pc.sendPackets(new S_SPMR(pc));
        }
    }

    @Override
    public void start(L1PcInstance pc, Object obj) throws Exception {
        // 其他多型用法（保留即可）
    }
}
