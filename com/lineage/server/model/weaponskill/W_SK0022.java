package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * 武器毀滅性攻擊 使用這項技能武器將會毀滅並造成設定的傷害 _type1:對方HP殘留值等份量 _type2:對方HP殘留值等份 計算後數字加入傷害質
 * 範例: _type1 = 2 _type2 = 3 取回對方殘留HP的1/3 後 * 2次 也就是殘留HP 2/3的意思
 *
 * @author daien
 */
public class W_SK0022 extends L1WeaponSkillType {

    private static final Log _log = LogFactory.getLog(W_SK0022.class);

    private static final Random _random = new Random();

    public W_SK0022() {
    }

    public static L1WeaponSkillType get() {
        return new W_SK0022();
    }

    @Override
    public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
                                     final L1ItemInstance weapon, final double srcdmg) {
        try {
            if (mode2(pc, target)) {
                return 0.0D;
            }
            final int random = random(weapon);
            if ((_random.nextInt(1000) + 1) <= random) {
                if (mode1(pc, target)) {
                    return 0.0D;
                }
                if ((target instanceof L1MonsterInstance) ||
                        (target instanceof L1SummonInstance) ||
                        (target instanceof L1PetInstance)) {
                    final L1NpcInstance npc = (L1NpcInstance) target;
                    if (npc.getNpcTemplate().is_boss()) {
                        return 0.0D;
                    }
                    final int undeadType = npc.getNpcTemplate().get_undead();
                    if ((undeadType == 1) || (undeadType == 3)) {
                        show(pc, npc);
                        final int outdmg = npc.getCurrentHp();
                        return outdmg;
                    }
                }
            }
            return 0;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    private boolean mode2(L1PcInstance pc, L1Character target) {
        return pc == null || target == null || target.isDead();
    }

    private boolean mode1(L1PcInstance pc, L1Character target) {
        if (target.hasSkillEffect(31)) {
            target.removeSkillEffect(31);
            int castgfx2 = SkillsTable.get().getTemplate(31).getCastGfx2();
            target.broadcastPacketAll(new S_SkillSound(target.getId(), castgfx2));
            if (target instanceof L1PcInstance) {
                L1PcInstance tgpc = (L1PcInstance) target;
                tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), castgfx2));
            }
            return true;
        }
        if (target.hasSkillEffect(153)) {// 魔法消除
            target.removeSkillEffect(153);
        }
        return false;
    }
}
