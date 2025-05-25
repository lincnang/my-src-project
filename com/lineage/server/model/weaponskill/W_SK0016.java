package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

public class W_SK0016 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK0016.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK0016();
    }

    /**
     * 底比斯武器魔法 惡魔之吻 傷害3次
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            int chance = _random.nextInt(1000);
            if (random(weapon) >= chance) {
                DmgSet dmgSet = new DmgSet(pc, target, weapon, srcdmg);
                dmgSet.begin();
                return 0.0D;
            }
            return 0.0D;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0.0D;
    }

    private class DmgSet implements Runnable {
        private final L1PcInstance _pc;
        private final L1Character _cha;
        private final L1ItemInstance _weapon;
        private int _timeCounter = 0;
        private double _srcdmg = 0.0D;

        private DmgSet(L1PcInstance pc, L1Character cha, L1ItemInstance weapon, double srcdmg) {
            _cha = cha;
            _pc = pc;
            _srcdmg = srcdmg;
            _weapon = weapon;
        }

        public void run() {
            try {
                if (_cha.hasSkillEffect(BEINGLICKING)) {// 身上有三段式魔武攻擊狀態
                    return;
                }
                if (_weapon.getItem().getType1() != 20 && _weapon.getItem().getType1() != 62 && _cha.hasSkillEffect(COUNTER_MAGIC)) {
                    _cha.removeSkillEffect(COUNTER_MAGIC);
                    int castgfx2 = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx2();
                    _cha.broadcastPacketAll(new S_SkillSound(_cha.getId(), castgfx2));
                    if ((_cha instanceof L1PcInstance)) {
                        L1PcInstance tgpc = (L1PcInstance) _cha;
                        tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), castgfx2));
                    }
                    return;
                }
                if (_cha.hasSkillEffect(ERASE_MAGIC)) {// 魔法消除
                    _cha.removeSkillEffect(ERASE_MAGIC);
                }
                while (_timeCounter < _type2) {
                    if ((_cha == null) || (_cha.isDead())) {
                        break;
                    }
                    attack();
                    _timeCounter += 1;
                    TimeUnit.MILLISECONDS.sleep(1000L);
                }
            } catch (Throwable e) {
                W_SK0016._log.error(e.getLocalizedMessage(), e);
            }
        }

        private void begin() {
            GeneralThreadPool.get().schedule(this, 100L);
        }

        private void attack() {
            _cha.setSkillEffect(BEINGLICKING, _type2 * 1000);// 給予三段式魔法攻擊狀態
            double damage = DmgAcMr.getDamage(_pc);// 智力魔攻加成傷害計算
            if ((_cha.getCurrentHp() - damage <= 0) && (_cha.getCurrentHp() != 1)) {
                damage = _cha.getCurrentHp();
            } else if (_cha.getCurrentHp() == 1) {
                damage = 0.0D;
            }
            double outdmg = dmg1() + dmg2(_srcdmg) + dmg3(_pc) + damage;
            double testDmg = outdmg;
            outdmg = calc_dmg(_pc, _cha, outdmg, _weapon);
            show(_pc, _cha);
            if (_type3 > 0) {
                outdmg *= _type3 / 100.0D;
            }
            if ((_cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) _cha;
                pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), 2));
                //System.out.println("outdmg:"+outdmg);
                pc.receiveDamage(_pc, outdmg, true, true);
                //_pc.sendPacketsAll(new S_AttackPacketPc(_pc, pc, 0, (int)outdmg));
            } else if ((_cha instanceof L1NpcInstance)) {
                L1NpcInstance npc = (L1NpcInstance) _cha;
                if (npc.getNpcId() >= 45001 && npc.getNpcId() <= 45004) {
                    _pc.sendPackets(new S_ServerMessage("特效傷害:" + testDmg));
                }
                npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 2));
                //System.out.println("outdmg:"+outdmg);
                npc.receiveDamage(_pc, (int) outdmg);
                //_pc.sendPacketsAll(new S_AttackPacketPc(_pc, npc, 0, (int)outdmg));
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.weaponskill.W_SK007 JD-Core Version: 0.6.2
 */