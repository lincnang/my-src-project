package com.lineage.server.model.weaponskill;

import com.lineage.server.datatables.WeaponSkillPowerTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Random;

public class WeaponSkillStart {
    private static final Log _log = LogFactory.getLog(WeaponSkillStart.class);
    private static final Random _random = new Random();

    public static double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        try {
            if (weapon == null) {
                return 0.0D;
            }
            ArrayList<L1WeaponSkillType> list = WeaponSkillPowerTable.get().getTemplate(weapon.getItemId());
            if (list != null) {
                L1WeaponSkillType tmp = list.get(_random.nextInt(list.size()));
                if (tmp != null) {
                    if ((tmp.get_boss_holdout()) && ((target instanceof L1NpcInstance))) {
                        L1NpcInstance npc = (L1NpcInstance) target;
                        if (npc.getNpcTemplate().get_nameid().startsWith("BOSS")) {
                            return 0.0D;
                        }
                    }
                    return tmp.start_weapon_skill(pc, target, weapon, srcdmg);
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
 * com.lineage.server.model.weaponskill.WeaponSkillStart JD-Core Version: 0.6.2
 */