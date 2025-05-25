package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class W_SK001 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK001.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK001();
    }

    /**
     * 武器毀滅性攻擊 (骰子匕首類型)
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            int chance = _random.nextInt(1000);
            int random = random(weapon);
            if (random >= chance) {
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
                double dmg = target.getCurrentHp() * _type1 / _type2;
                pc.sendPackets(new S_ServerMessage(158, weapon.getLogName()));
                pc.getInventory().removeItem(weapon, 1L);
                double outdmg = dmg1() + dmg2(srcdmg) + dmg3(pc) + dmg;
                double testDmg = outdmg;
                if ((target.getCurrentHp() - outdmg < 0) && (target.getCurrentHp() != 1)) {
                    outdmg = target.getCurrentHp() - 1;
                }
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) target;
                    tgpc.sendPacketsAll(new S_DoActionGFX(tgpc.getId(), 2));
                    tgpc.receiveDamage(pc, outdmg, false, true);
                    //pc.sendPacketsAll(new S_AttackPacketPc(pc, tgpc, 0, (int)outdmg));
                } else if ((target instanceof L1NpcInstance)) {
                    L1NpcInstance tgnpc = (L1NpcInstance) target;
                    if (tgnpc.getNpcId() >= 45001 && tgnpc.getNpcId() <= 45004) {
                        pc.sendPackets(new S_ServerMessage("特效傷害:" + testDmg));
                    }
                    tgnpc.broadcastPacketAll(new S_DoActionGFX(tgnpc.getId(), 2));
                    tgnpc.receiveDamage(pc, (int) outdmg);
                    //pc.sendPacketsAll(new S_AttackPacketPc(pc, tgnpc, 0, (int)outdmg));
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
 * com.lineage.server.model.weaponskill.W_SK001 JD-Core Version: 0.6.2
 */