package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.poison.L1DamagePoison;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class W_SK002 extends L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(W_SK002.class);
    private static final Random _random = new Random();

    public static L1WeaponSkillType get() {
        return new W_SK002();
    }

    /**
     * 武器附加毒性攻擊
     */
    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            if (target.getPoison() != null) {
                return 0.0D;
            }
            int chance = _random.nextInt(1000);
            int random = random(weapon);
            if (random >= chance) {
                L1DamagePoison.doInfection(pc, target, _type1 * 1000, _type2);
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
 * com.lineage.server.model.weaponskill.W_SK002 JD-Core Version: 0.6.2
 */