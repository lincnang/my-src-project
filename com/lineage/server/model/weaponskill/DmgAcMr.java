package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class DmgAcMr {
    private static final Log _log = LogFactory.getLog(DmgAcMr.class);
    private static final Random _random = new Random();

    /**
     * 防禦減傷
     *
     */
    public static int calcDefense(L1Character target) {
        try {
            if ((target instanceof L1PcInstance)) {
                L1PcInstance targetPc = (L1PcInstance) target;
                int ac = Math.max(0, 10 - targetPc.getAc());
                int acDefMax = targetPc.getClassFeature().getAcDefenseMax(ac);
                if (acDefMax != 0) {
                    int srcacd = Math.max(1, acDefMax >> 3);
                    return _random.nextInt(acDefMax) + srcacd;
                }
            } else if ((target instanceof L1MonsterInstance)) {
                L1MonsterInstance targetNpc = (L1MonsterInstance) target;
                int damagereduction = targetNpc.getNpcTemplate().get_damagereduction();
                int srcac = targetNpc.getAc();
                int ac = Math.max(0, 10 - srcac);
                int acDefMax = ac / 7;
                if (acDefMax != 0) {
                    int srcacd = Math.max(1, acDefMax);
                    return _random.nextInt(acDefMax) + srcacd + damagereduction;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 魔防減傷 弓類無視魔防
     *
     */
    public static double calcMrDefense(L1PcInstance pc, L1Character target, double dmg, L1ItemInstance weapon) {
        if (weapon.getItem().getType1() == 20) {// 弓類型武器
            return dmg;// 直接返回不計算魔防減傷
        }
        int mr = getTargetMr(target);
        double mrFloor = 0;
        if (mr < 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 2);
        } else if (mr >= 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 10);
        }
        double mrCoefficient = 0;
        if (mr < 100) {
            mrCoefficient = 1 - 0.01 * mrFloor;
        } else if (mr >= 100) {
            mrCoefficient = 0.6 - 0.01 * mrFloor;
        }
        dmg *= mrCoefficient;
        return dmg;
    }

    /**
     * 魔防減傷
     *
     */
    public static double calcMrDefense2(L1PcInstance pc, L1Character target, double dmg, L1ItemInstance weapon) {
        int mr = getTargetMr(target);
        double mrFloor = 0;
        if (mr < 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 2);
        } else if (mr >= 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 10);
        }
        double mrCoefficient = 0;
        if (mr < 100) {
            mrCoefficient = 1 - 0.01 * mrFloor;
        } else if (mr >= 100) {
            mrCoefficient = 0.6 - 0.01 * mrFloor;
        }
        dmg *= mrCoefficient;
        return dmg;
    }

    /**
     * 取得對方魔防
     *
     */
    private static int getTargetMr(L1Character target) {
        int mr = 0;
        if ((target instanceof L1PcInstance)) {
            L1PcInstance targetPc = (L1PcInstance) target;
            mr = targetPc.getMr();
            switch (targetPc.guardianEncounter()) {
                case 0:
                    mr += 3;
                    break;
                case 1:
                    mr += 6;
                    break;
                case 2:
                    mr += 9;
                default:
                    break;
            }
        } else if ((target instanceof L1MonsterInstance)) {
            L1MonsterInstance targetNpc = (L1MonsterInstance) target;
            mr = targetNpc.getMr();
        } else if ((target instanceof L1MerchantInstance)) {
            L1MerchantInstance targetNpc = (L1MerchantInstance) target;
            mr = targetNpc.getMr();
        }
        return mr;
    }

    /**
     * 智力魔攻加成傷害計算
     *
     */
    public static double getDamage(L1PcInstance pc) {
        L1ItemInstance weapon = pc.getWeapon();
        int weapontype = weapon.getItem().getType1();
        double dmg = 0;
        //System.out.println("pc.getSp():"+pc.getSp()+" pc.getTrueSp():"+pc.getTrueSp());
        int spByItem = pc.getSp() - pc.getTrueSp();
        int intel = pc.getInt();
        int charaIntelligence = intel + spByItem - 12;
        double coefficientA = 1.0 + 0.09375 * charaIntelligence;
        if (coefficientA < 1) {
            coefficientA = 1.0;
        }
        double coefficientB = 0;
        if (intel > 18) {// 超過18
            coefficientB = (intel + 2.0) / intel;
        } else if (intel <= 12) {// 小於等於12
            coefficientB = 12.0 * 0.065;
        } else {// 13~18之間
            coefficientB = intel * 0.065;
        }
        double coefficientC = 0;
        if (intel <= 12) {
            coefficientC = 12;
        } else {
            coefficientC = intel;
        }
        if (weapontype != 20) {// 除了弓以外
            dmg = (_random.nextInt(6) + 8) * coefficientA * coefficientB / 10.5D * coefficientC;
        } else {// 弓類型魔法武器
            dmg = (_random.nextInt(4) + 4) * coefficientA * coefficientB / 10.5D * coefficientC;
        }
        return dmg;
    }

    /**
     * 智力魔攻加成傷害計算
     *
     */
    public static double getSpInt(L1PcInstance pc) {
        int spByItem = pc.getSp() - pc.getTrueSp();
        int intel = pc.getInt();
        return (spByItem + intel) * 3;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.weaponskill.DmgAcMr JD-Core Version: 0.6.2
 */