package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class W_SK003 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK003.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK003();
    }

    /**
     * 武器攻擊附加HP奪取
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            int chance = _random.nextInt(1000);
            int random = random(weapon);
            if (random >= chance) {
                int hpadd = Math.max((int) (srcdmg * _type1 / _type2), 1);
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
 * com.lineage.server.model.weaponskill.W_SK003 JD-Core Version: 0.6.2
 */