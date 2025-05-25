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

public class W_SK0012 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK0012.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK0012();
    }

    /**
     * 寒冰奇古獸 心靈破壞
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
                int damage = (int) (pc.getSp() * 3.8D);// 魔攻 * 3.8D
                if ((target.getCurrentHp() - damage < 0) && (target.getCurrentHp() != 1)) {
                    damage = target.getCurrentHp() - 1;
                } else if (target.getCurrentHp() == 1) {
                    damage = 0;
                }
                // int reMp = 5;
                //
                // int newMp = target.getCurrentMp() - reMp;//減少對方魔力
                // if (newMp < 0) {
                // newMp = 0;
                // }
                // target.setCurrentMp(newMp);
                damage += _type1 + _random.nextInt(_type2);
                double outdmg = dmg1() + dmg2(srcdmg) + dmg3(pc) + damage;
                double testDmg = outdmg;
                outdmg = calc_dmg(pc, target, outdmg, weapon);
                if (_type3 > 0) {
                    outdmg *= _type3 / 100.0D;
                }
                show(pc, target);
                // System.out.println("outdmg ==" + outdmg);
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) target;
                    tgpc.sendPacketsAll(new S_DoActionGFX(tgpc.getId(), 2));
                    tgpc.receiveDamage(pc, outdmg, false, true);
                    //pc.sendPacketsAll(new S_AttackPacketPc(pc, tgpc, 0, (int)outdmg));
                } else if ((target instanceof L1NpcInstance)) {
                    L1NpcInstance npc = (L1NpcInstance) target;
                    npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 2));
                    if (npc.getNpcId() >= 45001 && npc.getNpcId() <= 45004) {
                        pc.sendPackets(new S_ServerMessage("特效傷害:" + testDmg));
                    }
                    npc.receiveDamage(pc, (int) outdmg);
                    //pc.sendPacketsAll(new S_AttackPacketPc(pc, npc, 0, (int)outdmg));
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
 * com.lineage.server.model.weaponskill.W_SK008 JD-Core Version: 0.6.2
 */