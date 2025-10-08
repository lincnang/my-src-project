package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack_Eff;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldEffect;

import java.util.ArrayList;

import static com.lineage.server.model.skill.L1SkillId.TRUE_TARGET;

/**
 * 精準目標
 *
 * @author dexc
 */
public class TRUE_TARGET extends SkillMode {
    public TRUE_TARGET() {
    }

    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;
        for (L1EffectInstance eff : WorldEffect.get().all()) {// 刪除重複的精準目標效果
            if (eff.getMaster() == srcpc && eff.getNpcId() == 86131) {
                eff.deleteMe();
                break;
            }
        }
        ArrayList<L1EffectInstance> effectlist = cha.get_TrueTargetEffectList();// 取回對像身上的精準目標效果
        L1EffectInstance tgeffect = null;
        if (effectlist != null) {// 列表不為空
            for (L1EffectInstance effect : effectlist) {
                if (effect.getMaster() == srcpc) {// 尋找自己施放的精準目標特效
                    tgeffect = effect;
                    break;
                }
            }
        }
        if (tgeffect == null || tgeffect.destroyed()) {// 空物件/已刪除
            tgeffect = L1SpawnUtil.spawnTrueTargetEffect(86131, 16, cha, srcpc, 0, 12299);
            cha.add_TrueTargetEffect(tgeffect);
            // 讓被施放者也立即認得特效NPC，確保自己能看到
            if (cha instanceof L1PcInstance) {
                L1PcInstance targetPcForEffect = (L1PcInstance) cha;
                tgeffect.addKnownObject(targetPcForEffect);
                targetPcForEffect.addKnownObject(tgeffect);
                targetPcForEffect.sendPackets(new S_NPCPack_Eff(tgeffect));
                targetPcForEffect.sendPackets(new S_DoActionGFX(tgeffect.getId(), 4));
                tgeffect.onPerceive(targetPcForEffect);
            }
        }
        if (!cha.hasSkillEffect(TRUE_TARGET)) {// 精準目標效果
            cha.setSkillEffect(TRUE_TARGET, integer * 1000);
            // 被施放者：傷害減免 +3（僅玩家）
            if (cha instanceof L1PcInstance) {
                ((L1PcInstance) cha).addDamageReductionByArmor(3);
            }
        }
        return dmg;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;
        return dmg;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        // 收回被施放者的傷害減免 +3
        if (cha instanceof L1PcInstance) {
            ((L1PcInstance) cha).addDamageReductionByArmor(-3);
        }
    }
}
