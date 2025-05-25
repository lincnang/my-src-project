package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

public class W_SK005 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK005.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK005();
    }

    /**
     * 武器攻擊附加MP奪取 傷害3次 藍惡靈奪魔
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            int chance = _random.nextInt(1000);
            if (random(weapon) >= chance) {
                if (target.getCurrentMp() > 0) {
                    int evel = weapon.getEnchantLevel() * 2;
                    int mpadd = _type1 + evel;
                    L1Magic magic = new L1Magic(pc, target);
                    magic.commit(0, mpadd);
                }
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
                if (_cha.hasSkillEffect(COUNTER_MAGIC)) {
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
                show(_pc, _cha);
                while (_timeCounter < _type2) {
                    if ((_cha == null) || (_cha.isDead())) {
                        break;
                    }
                    attack();
                    _timeCounter += 1;
                    TimeUnit.MILLISECONDS.sleep(1000L);
                }
            } catch (Throwable e) {
                W_SK005._log.error(e.getLocalizedMessage(), e);
            }
        }

        private void begin() {
            GeneralThreadPool.get().schedule(this, 100L);
        }

        private void attack() {
            _cha.setSkillEffect(BEINGLICKING, _type2 * 1000);// 給予三段式魔法攻擊狀態
            if (_cha.getCurrentMp() > 0) {
                int mpadd = _type1;
                L1Magic magic = new L1Magic(_pc, _cha);
                magic.commit(0, mpadd);
            }
            double damage = DmgAcMr.getDamage(_pc);
            if ((_cha.getCurrentHp() - damage <= 0) && (_cha.getCurrentHp() != 1)) {
                damage = _cha.getCurrentHp() - 1;
            } else if (_cha.getCurrentHp() == 1) {
                damage = 0.0D;
            }
            double outdmg = dmg1() + dmg2(_srcdmg) + dmg3(_pc) + damage;
            outdmg = calc_dmg(_pc, _cha, outdmg, _weapon);
            if (_type3 > 0) {
                outdmg *= _type3 / 100.0D;
            }
            if ((_cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) _cha;
                pc.receiveDamage(_pc, outdmg, true, true);
            } else if ((_cha instanceof L1NpcInstance)) {
                L1NpcInstance npc = (L1NpcInstance) _cha;
                npc.receiveDamage(_pc, (int) outdmg);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.weaponskill.W_SK005 JD-Core Version: 0.6.2
 */