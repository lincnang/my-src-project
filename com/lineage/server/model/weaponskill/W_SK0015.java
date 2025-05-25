package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class W_SK0015 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK0015.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK0015();
    }

    /**
     * 武器攻擊附加HP奪取 火神系列雙刀 鎖煉  雙手劍 巨斧
     * +10以上才會吸血
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            int chance = _random.nextInt(100);
            int random = random(weapon);
            int ran1 = weapon.getEnchantLevel() - 10 + 4;
            int max = ran1 + 1;
            int min = ran1 - 1;
            if (random >= chance) {
                int hpadd = _random.nextInt(max - min + 1) + min;
                short newHp = (short) (pc.getCurrentHp() + hpadd);
                pc.setCurrentHp(newHp);
                return 0.0D;
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
 * com.lineage.server.model.weaponskill.W_SK0015 JD-Core Version: 0.6.2
 */