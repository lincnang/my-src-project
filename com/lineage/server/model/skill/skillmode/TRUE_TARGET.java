package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldClan;
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
        }
        if (!cha.hasSkillEffect(TRUE_TARGET)) {// 精準目標效果
            cha.setSkillEffect(TRUE_TARGET, integer * 1000);
            // System.out.println("integer ==" + integer);
        }
        // 施展者具有血盟
        if (srcpc.getClan() != null) {
            final L1PcInstance[] onlinemembers = WorldClan.get().getClan(srcpc.getClanname()).getOnlineClanMember();
            // 對血盟成員發送封包
            for (final L1PcInstance clanmember : onlinemembers) {
                clanmember.sendPackets(new S_TrueTarget(cha.getId(), clanmember.getId(), srcpc.getText()));
            }
        } else {
            // 對自己發送封包
            srcpc.sendPackets(new S_TrueTarget(cha.getId(), srcpc.getId(), srcpc.getText()));
        }
        // 清空暫時文字串
        srcpc.setText("");
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
        // TODO Auto-generated method stub
    }
}
