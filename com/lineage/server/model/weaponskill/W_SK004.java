package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class W_SK004 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK004.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK004();
    }

    /**
     * 武器攻擊附加MP奪取
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            if (target.getCurrentMp() <= 0) {
                return 0.0D;
            }
            int chance = _random.nextInt(1000);
            int random = random(weapon);
            if (random >= chance) {
                int mpadd = 0;
                if (_type1 > 1) {
                    mpadd += _random.nextInt(_type1) + 1;
                } else {
                    mpadd += _type1;
                }
                if ((_type3 == 1) && (weapon.getEnchantLevel() > 0)) {
                    mpadd += _random.nextInt(weapon.getEnchantLevel()) + 1;
                }
                L1Magic magic = new L1Magic(pc, target);
                magic.commit(0, mpadd);
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
 * com.lineage.server.model.weaponskill.W_SK004 JD-Core Version: 0.6.2
 */