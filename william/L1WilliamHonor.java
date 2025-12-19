package william;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.Controller.IntBonusManager;


public class L1WilliamHonor {
    private final int _honorLevel;
    private final int _honorMax;       // 上限值
    private final int _honorMin;    // 最低值
    private final String _honorName;
    private final int _isActive;

    private final int _addHp, _addMp;
    private final int _addStr, _addDex, _addInt, _addCon, _addWis, _addCha;
    private final int _addHpr, _addMpr;
    private final int _addEarth, _addWater, _addFire, _addWind;
    private final int _addStun, _addStone, _addSleep, _addFreeze, _addSustain, _addBlind;
    private final int _addMr, _addSp, _addHit, _addBowHit, _addDmg, _addBowDmg;
    private final int _addReductionDmg, _addMagiDmg, _addReductionMagiDmg;
    private final int _addAc, _magicHit;
    private final int _pvpDmgReduction, _pvpDmgUp;
    private final int _stunLevel;
    private final int _block_weapon;

    public L1WilliamHonor(int honorLevel, int honorMax, int honorMin, String honorName, int isActive,
                          int addHp, int addMp, int addStr, int addDex, int addInt, int addCon, int addWis, int addCha,
                          int addHpr, int addMpr,
                          int addEarth, int addWater, int addFire, int addWind,
                          int addStun, int addStone, int addSleep, int addFreeze, int addSustain, int addBlind,
                          int addMr, int addSp, int addHit, int addBowHit, int addDmg, int addBowDmg,
                          int addReductionDmg, int addMagiDmg, int addReductionMagiDmg,
                          int addAc, int magicHit,
                          int pvpDmgReduction, int pvpDmgUp, int stunLevel, int block_weapon) {
        _honorLevel = honorLevel;
        _honorMax = honorMax;
        _honorMin = honorMin;
        _honorName = honorName;
        _isActive = isActive;

        _addHp = addHp;
        _addMp = addMp;
        _addStr = addStr;
        _addDex = addDex;
        _addInt = addInt;
        _addCon = addCon;
        _addWis = addWis;
        _addCha = addCha;
        _addHpr = addHpr;
        _addMpr = addMpr;
        _addEarth = addEarth;
        _addWater = addWater;
        _addFire = addFire;
        _addWind = addWind;
        _addStun = addStun;
        _addStone = addStone;
        _addSleep = addSleep;
        _addFreeze = addFreeze;
        _addSustain = addSustain;
        _addBlind = addBlind;
        _addMr = addMr;
        _addSp = addSp;
        _addHit = addHit;
        _addBowHit = addBowHit;
        _addDmg = addDmg;
        _addBowDmg = addBowDmg;
        _addReductionDmg = addReductionDmg;
        _addMagiDmg = addMagiDmg;
        _addReductionMagiDmg = addReductionMagiDmg;
        _addAc = addAc;
        _magicHit = magicHit;
        _pvpDmgReduction = pvpDmgReduction;
        _pvpDmgUp = pvpDmgUp;
        _stunLevel = stunLevel;
        _block_weapon = block_weapon;
    }


    public static void showHonorSkill(L1PcInstance pc, int honorlevel) {
        L1WilliamHonor HonorSkill = Honor.getInstance().getTemplate(honorlevel);
        if (HonorSkill == null || HonorSkill.getIsActive() == 0) {
            return;
        }

        pc.sendPackets(new S_SystemMessage("--------獲得爵位能力--------", 3));
        pc.sendPackets(new S_SystemMessage("目前爵位分數為：" + pc.getHonor() + " 。",12));
        pc.sendPackets(new S_SystemMessage("下一階爵位積分: " + HonorSkill.getHonorMax(),11));

        // 基本能力
        if (HonorSkill.getAddHp() > 0) pc.sendPackets(new S_SystemMessage("體力上限+ " + HonorSkill.getAddHp()));
        if (HonorSkill.getAddMp() > 0) pc.sendPackets(new S_SystemMessage("魔力上限+ " + HonorSkill.getAddMp()));
        if (HonorSkill.getAddHpr() > 0) pc.sendPackets(new S_SystemMessage("體力回復量+ " + HonorSkill.getAddHpr()));
        if (HonorSkill.getAddMpr() > 0) pc.sendPackets(new S_SystemMessage("魔力回復量+ " + HonorSkill.getAddMpr()));

        // 屬性能力
        if (HonorSkill.getAddStr() > 0) pc.sendPackets(new S_SystemMessage("力量+ " + HonorSkill.getAddStr()));
        if (HonorSkill.getAddDex() > 0) pc.sendPackets(new S_SystemMessage("敏捷+ " + HonorSkill.getAddDex()));
        if (HonorSkill.getAddInt() > 0) pc.sendPackets(new S_SystemMessage("智力+ " + HonorSkill.getAddInt()));
        if (HonorSkill.getAddCon() > 0) pc.sendPackets(new S_SystemMessage("體質+ " + HonorSkill.getAddCon()));
        if (HonorSkill.getAddWis() > 0) pc.sendPackets(new S_SystemMessage("精神+ " + HonorSkill.getAddWis()));
        if (HonorSkill.getAddCha() > 0) pc.sendPackets(new S_SystemMessage("魅力+ " + HonorSkill.getAddCha()));

        // 屬性防禦
        if (HonorSkill.getAddEarth() > 0)
            pc.sendPackets(new S_SystemMessage("地屬性防禦+ " + HonorSkill.getAddEarth() + "%。"));
        if (HonorSkill.getAddWater() > 0)
            pc.sendPackets(new S_SystemMessage("水屬性防禦+ " + HonorSkill.getAddWater() + "%。"));
        if (HonorSkill.getAddFire() > 0)
            pc.sendPackets(new S_SystemMessage("火屬性防禦+ " + HonorSkill.getAddFire() + "%。"));
        if (HonorSkill.getAddWind() > 0)
            pc.sendPackets(new S_SystemMessage("風屬性防禦+ " + HonorSkill.getAddWind() + "%。"));

        // 抗性
        if (HonorSkill.getAddStun() > 0) pc.sendPackets(new S_SystemMessage("昏迷耐性+ " + HonorSkill.getAddStun()));
        if (HonorSkill.getAddStone() > 0) pc.sendPackets(new S_SystemMessage("石化耐性+ " + HonorSkill.getAddStone()));
        if (HonorSkill.getAddSleep() > 0) pc.sendPackets(new S_SystemMessage("睡眠耐性+ " + HonorSkill.getAddSleep()));
        if (HonorSkill.getAddFreeze() > 0)
            pc.sendPackets(new S_SystemMessage("寒冰耐性+ " + HonorSkill.getAddFreeze()));
        if (HonorSkill.getAddSustain() > 0)
            pc.sendPackets(new S_SystemMessage("支撐耐性+ " + HonorSkill.getAddSustain()));
        if (HonorSkill.getAddBlind() > 0) pc.sendPackets(new S_SystemMessage("暗黑耐性+ " + HonorSkill.getAddBlind()));

        // 魔法
        if (HonorSkill.getAddMr() > 0) pc.sendPackets(new S_SystemMessage("魔防+ " + HonorSkill.getAddMr()));
        if (HonorSkill.getAddSp() > 0) pc.sendPackets(new S_SystemMessage("魔攻+ " + HonorSkill.getAddSp()));
        if (HonorSkill.getMagicHit() > 0) pc.sendPackets(new S_SystemMessage("魔法命中+ " + HonorSkill.getMagicHit()));

        // 命中與傷害
        if (HonorSkill.getAddHit() > 0) pc.sendPackets(new S_SystemMessage("近距離命中+ " + HonorSkill.getAddHit()));
        if (HonorSkill.getAddBowHit() > 0)
            pc.sendPackets(new S_SystemMessage("遠距離命中+ " + HonorSkill.getAddBowHit()));
        if (HonorSkill.getAddDmg() > 0)
            pc.sendPackets(new S_SystemMessage("近距離物理傷害+ " + HonorSkill.getAddDmg()));
        if (HonorSkill.getAddBowDmg() > 0)
            pc.sendPackets(new S_SystemMessage("遠距離物理傷害+ " + HonorSkill.getAddBowDmg()));

        // 其他戰鬥相關
        if (HonorSkill.getAddReductionDmg() > 0)
            pc.sendPackets(new S_SystemMessage("減免物理傷害+ " + HonorSkill.getAddReductionDmg()));
        if (HonorSkill.getAddMagiDmg() > 0)
            pc.sendPackets(new S_SystemMessage("魔法傷害+ " + HonorSkill.getAddMagiDmg()));
        if (HonorSkill.getAddReductionMagiDmg() > 0)
            pc.sendPackets(new S_SystemMessage("減免魔法傷害+ " + HonorSkill.getAddReductionMagiDmg()));
        if (HonorSkill.getAddAC() > 0) pc.sendPackets(new S_SystemMessage("防禦+ " + HonorSkill.getAddAC()));
        if (HonorSkill.getDamageReductionByArmorForPK() > 0)
            pc.sendPackets(new S_SystemMessage("PVP傷害減免+ " + HonorSkill.getDamageReductionByArmorForPK()));
        if (HonorSkill.getDmgupForPK() > 0)
            pc.sendPackets(new S_SystemMessage("無視PVP傷害減免+ " + HonorSkill.getDmgupForPK()));
        if (HonorSkill.getStunLevel() > 0)
            pc.sendPackets(new S_SystemMessage("昏迷命中+ " + HonorSkill.getStunLevel()));
        if (HonorSkill.getBlockWeapon() > 0)
            pc.sendPackets(new S_SystemMessage("阻擋武器+ " + HonorSkill.getBlockWeapon()));
        // 同步狀態顯示（若為合法角色）
        if (pc.getQuest().isValid()) {
            pc.sendPackets(new S_OwnCharStatus(pc));   // HP/MP/能力
            pc.sendPackets(new S_Ability(0, true));    // 四屬 / 命中 / 傷害
            pc.sendPackets(new S_SPMR(pc));            // 魔攻/魔防

        }
    }

    public static void getHonorSkill(L1PcInstance pc) {

        int honorlevel = pc.getHonorLevel(); // ✅ 補上這行
        // 🛡️ 阻擋等級 0 → 不套用任何威望能力
        if (honorlevel <= 0 || pc.isHonorSkillApplied()) return;

        // ✅ 取得資料表設定的能力模板
        L1WilliamHonor HonorSkill = Honor.getInstance().getTemplate(honorlevel);

        if (HonorSkill == null) {
            return;
        }
        if (HonorSkill.getIsActive() == 0) {
            return;
        }
        if (HonorSkill.getHonorLevel() != honorlevel) {
            return;
        }

        // 標記已套用過
        pc.setHonorSkillApplied(true);
        // 能力加成
        pc.addMaxHp(HonorSkill.getAddHp());
        pc.setCurrentHp(pc.getMaxHp());
        pc.addMaxMp(HonorSkill.getAddMp());
        pc.setCurrentMp(pc.getMaxMp());
        pc.addStr(HonorSkill.getAddStr());
        pc.addDex(HonorSkill.getAddDex());
        pc.addCon(HonorSkill.getAddCon());
        pc.addInt(HonorSkill.getAddInt());
        // 智力變動後重新套用智力加成
        if (HonorSkill.getAddInt() != 0) {
            IntBonusManager.get().reapply(pc);
        }
        pc.addWis(HonorSkill.getAddWis());
        pc.addCha(HonorSkill.getAddCha());
        pc.addHpr(HonorSkill.getAddHpr());
        pc.addMpr(HonorSkill.getAddMpr());
        pc.addEarth(HonorSkill.getAddEarth());
        pc.addWater(HonorSkill.getAddWater());
        pc.addFire(HonorSkill.getAddFire());
        pc.addWind(HonorSkill.getAddWind());
        pc.addRegistStun(HonorSkill.getAddStun());
        pc.addRegistStone(HonorSkill.getAddStone());
        pc.addRegistSleep(HonorSkill.getAddSleep());
        pc.add_regist_freeze(HonorSkill.getAddFreeze());
        pc.addRegistSustain(HonorSkill.getAddSustain());
        pc.addRegistBlind(HonorSkill.getAddBlind());
        pc.addMr(HonorSkill.getAddMr());
        pc.addSp(HonorSkill.getAddSp());
        pc.addOriginalMagicHit(HonorSkill.getMagicHit());
        pc.addHitModifierByArmor(HonorSkill.getAddHit());
        pc.addBowHitModifierByArmor(HonorSkill.getAddBowHit());
        pc.addDmgModifierByArmor(HonorSkill.getAddDmg());
        pc.addBowDmgModifierByArmor(HonorSkill.getAddBowDmg());
        pc.addAc(-HonorSkill.getAddAC());
        pc.add_magic_modifier_dmg(HonorSkill.getAddMagiDmg());
        pc.add_magic_reduction_dmg(HonorSkill.getAddReductionMagiDmg());
        pc.setPvpDmg_R(HonorSkill.getDamageReductionByArmorForPK());
        pc.setPvpDmg(HonorSkill.getDmgupForPK());
        pc.addStunLevel(HonorSkill.getStunLevel());
        pc.addBlockWeapon(HonorSkill.getBlockWeapon());
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_Ability(0, true));
    }

    public static void delHonorSkill(L1PcInstance pc, int honorlevel) {
        if (honorlevel <= 0) return;
        L1WilliamHonor HonorSkill = Honor.getInstance().getTemplate(honorlevel);
        if (HonorSkill == null || HonorSkill.getIsActive() == 0) {
            return;
        }
        // 基本能力移除
        pc.addMaxHp(-HonorSkill.getAddHp());
        pc.addMaxMp(-HonorSkill.getAddMp());
        pc.addStr(-HonorSkill.getAddStr());
        pc.addDex(-HonorSkill.getAddDex());
        pc.addCon(-HonorSkill.getAddCon());
        pc.addInt(-HonorSkill.getAddInt());
        // 智力變動後重新套用智力加成
        if (HonorSkill.getAddInt() != 0) {
            IntBonusManager.get().reapply(pc);
        }
        pc.addWis(-HonorSkill.getAddWis());
        pc.addCha(-HonorSkill.getAddCha());
        pc.addHpr(-HonorSkill.getAddHpr());
        pc.addMpr(-HonorSkill.getAddMpr());
        pc.addEarth(-HonorSkill.getAddEarth());
        pc.addWater(-HonorSkill.getAddWater());
        pc.addFire(-HonorSkill.getAddFire());
        pc.addWind(-HonorSkill.getAddWind());
        // 抗性移除
        pc.addRegistStun(-HonorSkill.getAddStun());
        pc.addRegistStone(-HonorSkill.getAddStone());
        pc.addRegistSleep(-HonorSkill.getAddSleep());
        pc.add_regist_freeze(-HonorSkill.getAddFreeze());
        pc.addRegistSustain(-HonorSkill.getAddSustain());
        pc.addRegistBlind(-HonorSkill.getAddBlind());
        // 魔法與命中傷害移除
        pc.addMr(-HonorSkill.getAddMr());
        pc.addSp(-HonorSkill.getAddSp());
        pc.addOriginalMagicHit(-HonorSkill.getMagicHit());
        pc.addHitModifierByArmor(-HonorSkill.getAddHit());
        pc.addBowHitModifierByArmor(-HonorSkill.getAddBowHit());
        pc.addDmgModifierByArmor(-HonorSkill.getAddDmg());
        pc.addBowDmgModifierByArmor(-HonorSkill.getAddBowDmg());
        pc.addAc(HonorSkill.getAddAC()); // AC 是反向
        pc.add_magic_modifier_dmg(-HonorSkill.getAddMagiDmg());
        pc.add_magic_reduction_dmg(-HonorSkill.getAddReductionMagiDmg());
        // PVP
        pc.setPvpDmg_R(0);
        pc.setPvpDmg(0);
        pc.addStunLevel(-HonorSkill.getStunLevel());
        pc.addBlockWeapon(-HonorSkill.getBlockWeapon());
        // 重置布林旗標，允許後續重新加成
        pc.setHonorSkillApplied(false);
        // 同步狀態
        pc.setCurrentHp(Math.min(pc.getCurrentHp(), pc.getMaxHp()));
        pc.setCurrentMp(Math.min(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_Ability(0, true));
        // GM 提示（可選）
        if (pc.isGm()) {
            pc.sendPackets(new S_SystemMessage("[爵位移除] STR=" + pc.getStr() + " HP=" + pc.getMaxHp()));
        }
    }

    public int getHonorLevel() {
        return _honorLevel;
    }



    public int getHonorMin() {
        return _honorMin;
    }

    public String getHonorName() {
        return _honorName;
    }

    public int getIsActive() {
        return _isActive;
    }

    public int getAddHp() {
        return _addHp;
    }

    public int getAddMp() {
        return _addMp;
    }

    public int getAddStr() {
        return _addStr;
    }

    public int getAddDex() {
        return _addDex;
    }

    public int getAddInt() {
        return _addInt;
    }

    public int getAddCon() {
        return _addCon;
    }

    public int getAddWis() {
        return _addWis;
    }

    public int getAddCha() {
        return _addCha;
    }

    public int getAddHpr() {
        return _addHpr;
    }

    public int getAddMpr() {
        return _addMpr;
    }

    public int getAddEarth() {
        return _addEarth;
    }

    public int getAddWater() {
        return _addWater;
    }

    public int getAddFire() {
        return _addFire;
    }

    public int getAddWind() {
        return _addWind;
    }

    public int getAddStun() {
        return _addStun;
    }

    public int getAddStone() {
        return _addStone;
    }

    public int getAddSleep() {
        return _addSleep;
    }

    public int getAddFreeze() {
        return _addFreeze;
    }

    public int getAddSustain() {
        return _addSustain;
    }

    public int getAddBlind() {
        return _addBlind;
    }

    public int getAddMr() {
        return _addMr;
    }

    public int getAddSp() {
        return _addSp;
    }

    public int getAddHit() {
        return _addHit;
    }

    public int getAddBowHit() {
        return _addBowHit;
    }

    public int getAddDmg() {
        return _addDmg;
    }

    public int getAddBowDmg() {
        return _addBowDmg;
    }

    public int getAddReductionDmg() {
        return _addReductionDmg;
    }

    public int getAddMagiDmg() {
        return _addMagiDmg;
    }

    public int getAddReductionMagiDmg() {
        return _addReductionMagiDmg;
    }

    public int getAddAC() {
        return _addAc;
    }

    public int getMagicHit() {
        return _magicHit;
    }

    public int getDamageReductionByArmorForPK() {
        return _pvpDmgReduction;
    }

    public int getDmgupForPK() {
        return _pvpDmgUp;
    }

    public int getStunLevel() {
        return _stunLevel;
    }
    public int getHonorMax() {
        return _honorMax;
    }
    public int getBlockWeapon() {
        return _block_weapon;
    }

}

