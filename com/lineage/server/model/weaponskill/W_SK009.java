package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class W_SK009 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK009.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK009();
    }

    /**
     * 魔法武器魔力奪取
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            int chance = _random.nextInt(1000);
            if (random(weapon) >= chance) {
                if (target.hasSkillEffect(31)) {
                    target.removeSkillEffect(31);
                    int castgfx2 = SkillsTable.get().getTemplate(31).getCastGfx2();
                    target.broadcastPacketAll(new S_SkillSound(target.getId(), castgfx2));
                    if ((target instanceof L1PcInstance)) {
                        L1PcInstance tgpc = (L1PcInstance) target;
                        tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), castgfx2));
                    }
                    return 0.0D;
                }
                if (target.hasSkillEffect(153)) {// 魔法消除
                    target.removeSkillEffect(153);
                }
                if (target.getCurrentMp() > 0) {
                    final int mana = _type1 + _random.nextInt(_type2);
                    int drainMana = mana + (pc.getInt() / 2);
                    if (target.getCurrentMp() < drainMana) {
                        drainMana = target.getCurrentMp();
                    }
                    L1Magic magic = new L1Magic(pc, target);
                    magic.commit(0, drainMana);
                    show(pc, target);
                }
            }
            return 0.0D;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0.0D;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.weaponskill.W_SK005 JD-Core Version: 0.6.2
 */