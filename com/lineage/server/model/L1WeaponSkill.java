package com.lineage.server.model;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.WeaponSkillTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.Random;

public class L1WeaponSkill {
    private static Random _random = new Random();
    private int _weaponId;
    private String _skillName; // 魔法武器發動的技能名稱
    private int _probability;
    private int _fixDamage;
    private int _randomDamage;
    private int _area;
    private int _skillId;
    private int _skillTime;
    private int _effectId;
    private int _effectTarget;
    private int _effectId1;
    private int _effectTarget1; // エフェクトの對像 0:相手 1:自分
    private int _effect_xy1;
    private int _effectId2;
    private int _effectTarget2; // エフェクトの對像 0:相手 1:自分
    private int _effect_xy2;
    private int _effectId3;
    private int _effectTarget3; // エフェクトの對像 0:相手 1:自分
    private int _effect_xy3;
    private int _effectId4;
    private int _effectTarget4; // エフェクトの對像 0:相手 1:自分
    private int _effect_xy4;
    private boolean _isArrowType;
    private int _attr;

    public static double getWeaponSkillDamage(L1PcInstance pc, L1Character cha, int weaponId) {
        L1WeaponSkill weaponSkill = WeaponSkillTable.get().getTemplate(weaponId);
        if ((pc == null) || (cha == null) || (weaponSkill == null)) {
            return 0.0D;
        }
        int chance = _random.nextInt(100) + 1;
        int id = 0;
        int dollchance = 0;
        int weaponMDC = pc.getweaponMDC();
        if (weaponId == 47) {
            chance = _random.nextInt(200) + 1;
        }
        // 增加娃娃魔法武器發動機率
        if (pc.getWeaponsprobability() != 0) {
            dollchance += pc.getWeaponsprobability();
        }
        if (pc.getWeapon().getItem().getType2() == 1 && pc.getWeapon().getItemprobability() > 0) {
            id = pc.getWeapon().getItemprobability();
        }
        //if (weaponSkill.getProbability() + dollchance + id < chance) {
        if (weaponSkill.getProbability() + dollchance + id + weaponMDC < chance) {
            return 0.0D;
        }
        int skillId = weaponSkill.getSkillId();
        if (skillId != 0) {
            L1Skills skill = SkillsTable.get().getTemplate(skillId);
            if ((skill != null) && (skill.getTarget().equals("buff")) && (!isFreeze(cha))) {
                cha.setSkillEffect(skillId, weaponSkill.getSkillTime() * 1000);
            } else if (skillId == 4000) {
                cha.setSkillEffect(4000, weaponSkill.getSkillTime() * 1000);
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) cha;
                    tgpc.sendPackets(new S_Paralysis(6, true));
                } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                    L1NpcInstance tgnpc = (L1NpcInstance) cha;
                    // tgnpc.setParalyzed(true);
                    tgnpc.setPassispeed(0);
                }
            }
        }
        final int effectId = weaponSkill.getEffectId();
        // 具有動畫
        if (effectId > 0) {
            int chaId = 0;
            if (weaponSkill.getEffectTarget() == 0) {
                chaId = cha.getId();
            } else {
                chaId = pc.getId();
            }
            final boolean isArrowType = weaponSkill.isArrowType();
            if (!isArrowType) {
                pc.sendPacketsYN(new S_SkillSound(chaId, effectId));
            } else {
                final S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(), effectId, cha.getX(), cha.getY(), ActionCodes.ACTION_Attack, false);
                pc.sendPacketsYN(packet);
            }
        }
        final int effectId1 = weaponSkill.getEffectId1();
        if (effectId1 > 0) {
            L1Character target = cha;
            if (weaponSkill.getEffectTarget1() != 0) {
                target = pc;
            }
            int area = weaponSkill.getEffectXY1();
            if (area > 0) {
                int x = target.getX();
                int y = target.getY();
                for (; area > 0; area--) {
                    // 四面
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y + area, effectId1));
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y - area, effectId1));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y - area, effectId1));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y + area, effectId1));
                }
            }
            // 最後特效落原點
            pc.sendPacketsYN(new S_SkillSound(target.getId(), effectId1));
        }
        final int effectId2 = weaponSkill.getEffectId2();
        // 具有動畫
        if (effectId2 > 0) {
            L1Character target = cha;
            if (weaponSkill.getEffectTarget2() != 0) {
                target = pc;
            }
            int area = weaponSkill.getEffectXY2();
            if (area > 0) {
                int x = target.getX();
                int y = target.getY();
                for (; area > 0; area--) {
                    // 四面
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y + area, effectId2));
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y - area, effectId2));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y - area, effectId2));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y + area, effectId2));
                }
            }
            // 最後特效落原點
            pc.sendPacketsYN(new S_SkillSound(target.getId(), effectId2));
        }
        final int effectId3 = weaponSkill.getEffectId3();
        // 具有動畫
        if (effectId3 > 0) {
            L1Character target = cha;
            if (weaponSkill.getEffectTarget3() != 0) {
                target = pc;
            }
            int area = weaponSkill.getEffectXY3();
            if (area > 0) {
                int x = target.getX();
                int y = target.getY();
                for (; area > 0; area--) {
                    // 四面
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y + area, effectId3));
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y - area, effectId3));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y - area, effectId3));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y + area, effectId3));
                }
            }
            // 最後特效落原點
            pc.sendPacketsYN(new S_SkillSound(target.getId(), effectId3));
        }
        final int effectId4 = weaponSkill.getEffectId4();
        // 具有動畫
        if (effectId4 > 0) {
            L1Character target = cha;
            if (weaponSkill.getEffectTarget4() != 0) {
                target = pc;
            }
            int area = weaponSkill.getEffectXY4();
            if (area > 0) {
                int x = target.getX();
                int y = target.getY();
                for (; area > 0; area--) {
                    // 四面
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y + area, effectId4));
                    pc.sendPacketsYN(new S_EffectLocation(x + area, y - area, effectId4));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y - area, effectId4));
                    pc.sendPacketsYN(new S_EffectLocation(x - area, y + area, effectId4));
                }
            }
            // 最後特效落原點
            pc.sendPacketsYN(new S_SkillSound(target.getId(), effectId4));
        }
        double damage = 0.0D;
        int randomDamage = weaponSkill.getRandomDamage();
        if (randomDamage != 0) {
            damage = _random.nextInt(randomDamage);
        }
        //damage += weaponSkill.getFixDamage();
        int ReiWeaponSkillDamage = weaponSkill.getFixDamage();
        if (pc.isWarrior() && pc.getReincarnationSkill()[1] > 0) { // 戰士天賦技能魔傷其發
            ReiWeaponSkillDamage += (int) (((double) ReiWeaponSkillDamage / 100) * (pc.getReincarnationSkill()[1] * 3 * 2)); // 再乘2
        }
        damage += ReiWeaponSkillDamage;
        if (pc.getweaponMD() != 0) {
            damage *= 1 + (pc.getweaponMD() / 100D);
        }
        // 增加娃娃魔法武器傷害
        if (pc.getWeapondmg() != 0) {
            damage += pc.getWeapondmg();
        }
        int area = weaponSkill.getArea();
        if ((area > 0) || (area == -1)) {
            for (L1Object object : World.get().getVisibleObjects(cha, area)) {
                if (object != null) {
                    if ((object instanceof L1Character)) {
                        if (object.getId() != pc.getId()) {
                            if (object.getId() != cha.getId()) {
                                if ((!(cha instanceof L1MonsterInstance)) || ((object instanceof L1MonsterInstance))) {
                                    if (((!(cha instanceof L1PcInstance)) && (!(cha instanceof L1SummonInstance)) && (!(cha instanceof L1PetInstance))) || ((object instanceof L1PcInstance)) || ((object instanceof L1SummonInstance)) || ((object instanceof L1PetInstance)) || ((object instanceof L1MonsterInstance))) {
                                        damage = calcDamageReduction(pc, (L1Character) object, damage, weaponSkill.getAttr());
                                        if (damage > 0.0D) {
                                            if ((object instanceof L1PcInstance)) {
                                                L1PcInstance targetPc = (L1PcInstance) object;
                                                targetPc.sendPacketsAll(new S_DoActionGFX(targetPc.getId(), 2));
                                                targetPc.receiveDamage(pc, (int) damage, false, false);
                                            } else if (((object instanceof L1SummonInstance)) || ((object instanceof L1PetInstance)) || ((object instanceof L1MonsterInstance))) {
                                                L1NpcInstance targetNpc = (L1NpcInstance) object;
                                                targetNpc.broadcastPacketAll(new S_DoActionGFX(targetNpc.getId(), 2));
                                                targetNpc.receiveDamage(pc, (int) damage);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return calcDamageReduction(pc, cha, damage, weaponSkill.getAttr());
    }

    public static double getBaphometStaffDamage(L1PcInstance pc, L1Character cha) {
        double dmg = 0.0D;
        int chance = _random.nextInt(100) + 1;
        if (14 >= chance) {
            int locx = cha.getX();
            int locy = cha.getY();
            int sp = pc.getSp();
            int intel = pc.getInt();
            dmg = (intel + sp) * 1.8D + _random.nextInt(intel + sp) * 1.8D;
            pc.sendPacketsAll(new S_EffectLocation(locx, locy, 129));
        }
        return calcDamageReduction(pc, cha, dmg, 1);
    }

    public static double getDiceDaggerDamage(L1PcInstance pc, L1PcInstance targetPc, L1ItemInstance weapon) {
        double dmg = 0.0D;
        int chance = _random.nextInt(100) + 1;
        if (3 >= chance) {
            dmg = targetPc.getCurrentHp() * 2 / 3D;
            if (targetPc.getCurrentHp() - dmg < 0.0D) {
                dmg = 0.0D;
            }
            String msg = weapon.getLogName();
            pc.sendPackets(new S_ServerMessage(158, msg));
            pc.getInventory().removeItem(weapon, 1L);
        }
        return dmg;
    }

    public static double getChaserDamage(L1PcInstance pc, L1Character cha) {
        double dmg = 0.0D;
        int chance = _random.nextInt(100) + 1;
        if (4 >= chance) {
            dmg = 4.0D;
            pc.sendPacketsAll(new S_EffectLocation(cha.getX(), cha.getY(), 7025));
        }
        return dmg;
    }

    /**
     * 奇古獸傷害計算
     *
     */
    public static double getKiringkuDamage(L1PcInstance pc, L1Character cha) {
        int dmg = 0;
        int dice = 5;
        int diceCount = 2;
        int value;
        int kiringkuDamage = 0;
        int charaIntelligence = 0;
        switch (pc.getWeapon().getItem().getItemId()) {
            case 270:
            case 369:
                value = 16;
                break;
            case 410171:
            case 410186:
                value = 22;
                break;
            case 271:
            default:
                value = 14;
                break;
        }
        for (int i = 0; i < diceCount; i++) {
            kiringkuDamage += _random.nextInt(dice) + 1;
        }
        kiringkuDamage += value;
        int weaponAddDmg = 0; // 武器上的魔法增傷
        L1ItemInstance weapon = pc.getWeapon();
        if (weapon != null) {
            weaponAddDmg = weapon.getItem().getMagicDmgModifier();
        }
        kiringkuDamage += weaponAddDmg;
        int spByItem = pc.getSp() - pc.getTrueSp();
        charaIntelligence = pc.getInt() + spByItem - 12;
        if (charaIntelligence < 1) {
            charaIntelligence = 1;
        }
        double kiringkuCoefficientA = 1.0D + charaIntelligence * 3.0D / 32.0D;
        kiringkuDamage = (int) (kiringkuDamage * kiringkuCoefficientA);
        double kiringkuFloor = kiringkuDamage;
        dmg += kiringkuFloor + pc.getOriginalMagicDamage();
        if (pc.hasSkillEffect(219)) {
            dmg += 10;
        }
        // 改變面向
        if (pc.getHeading() != pc.targetDirection(cha.getX(), cha.getY())) {
            pc.setHeading(pc.targetDirection(cha.getX(), cha.getY()));
            pc.sendPacketsAll(new S_ChangeHeading(pc));
        }
        // int rnd = _random.nextInt(100) + 1;
        boolean magicBon = false;
        if (pc.getBaseInt() >= 40) {// 敏捷
            int rndVal = 0;
            if (pc.getBaseInt() >= 40) {
                rndVal++;
            }
            if (pc.getBaseInt() >= 45) {
                rndVal++;
            }
            if (pc.getBaseInt() >= 50) {
                rndVal++;
            }
            if (pc.getBaseInt() >= 60) {
                rndVal++;
            }
            if (_random.nextInt(100) < rndVal) {
                double criticalCoefficient = 1.2D;// 1.2倍傷害
                dmg = (int) (dmg * criticalCoefficient);
                magicBon = true;
            }
        }
        boolean followEffect = false;
        if (pc.getTempCharGfx() == 13739 || pc.getTempCharGfx() == 13741) {// 真幻術外型
            followEffect = true;
            /*
             * R版Top10暴擊特效處理
            L1Location loc = cha.getLocation();
            L1NpcInstance dummy = L1SpawnUtil.spawnS(loc, 86132, pc.get_showId(), 1, pc.getHeading());
            dummy.broadcastPacketAll(new S_NPCPack(dummy));
            dummy.broadcastPacketAll(new S_SkillSound(dummy.getId(), 13396));
            */
        } else if (pc.getWeapon().getItem().getItemId() == 270) {
            if (magicBon) {
                followEffect = true;
                /*
                 * R版Top10暴擊特效處理
                L1Location loc = cha.getLocation();
                L1NpcInstance dummy = L1SpawnUtil.spawnS(loc, 86132, pc.get_showId(), 1, pc.getHeading());
                dummy.broadcastPacketAll(new S_NPCPack(dummy));
                dummy.broadcastPacketAll(new S_SkillSound(dummy.getId(), 13396));
                */
            } else {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 6983));
            }
        } else {
            if (magicBon) {
                followEffect = true;
                /*
                 * R版Top10暴擊特效處理
                L1Location loc = cha.getLocation();
                L1NpcInstance dummy = L1SpawnUtil.spawnS(loc, 86132, pc.get_showId(), 1, pc.getHeading());
                dummy.broadcastPacketAll(new S_NPCPack(dummy));
                dummy.broadcastPacketAll(new S_SkillSound(dummy.getId(), 13396));
                */
            } else {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7049));
            }
        }
        if (followEffect) {
            pc.sendFollowEffect(cha, 13396);
        }
        return calcDamageReduction(pc, cha, dmg, 0);
    }

    public static double getAreaSkillWeaponDamage(L1PcInstance pc, L1Character cha, int weaponId) {
        double dmg = 0.0D;
        int probability = 0;
        int attr = 0;
        int chance = _random.nextInt(100) + 1;
        if (weaponId == 263) {
            probability = 5;
            attr = 4;
        } else if (weaponId == 260) {
            probability = 4;
            attr = 8;
        }
        if (probability >= chance) {
            int sp = pc.getSp();
            int intel = pc.getInt();
            int area = 0;
            int effectTargetId = 0;
            int effectId = 0;
            L1Character areaBase = cha;
            double damageRate = 0.0D;
            switch (weaponId) {
                case 263:
                    area = 3;
                    damageRate = 1.4D;
                    effectTargetId = cha.getId();
                    effectId = 1804;
                    break;
                case 260:
                    area = 4;
                    damageRate = 1.5D;
                    effectTargetId = pc.getId();
                    effectId = 758;
                    areaBase = pc;
                    break;
            }
            dmg = (intel + sp) * damageRate + _random.nextInt(intel + sp) * damageRate;
            pc.sendPacketsAll(new S_SkillSound(effectTargetId, effectId));
            for (L1Object object : World.get().getVisibleObjects(areaBase, area)) {
                if (object != null) {
                    if ((object instanceof L1Character)) {
                        if (object.getId() != pc.getId()) {
                            if (object.getId() != cha.getId()) {
                                if ((!(cha instanceof L1MonsterInstance)) || ((object instanceof L1MonsterInstance))) {
                                    if (((!(cha instanceof L1PcInstance)) && (!(cha instanceof L1SummonInstance)) && (!(cha instanceof L1PetInstance))) || ((object instanceof L1PcInstance)) || ((object instanceof L1SummonInstance)) || ((object instanceof L1PetInstance)) || ((object instanceof L1MonsterInstance))) {
                                        dmg = calcDamageReduction(pc, (L1Character) object, dmg, attr);
                                        if (dmg > 0.0D) {
                                            if ((object instanceof L1PcInstance)) {
                                                L1PcInstance targetPc = (L1PcInstance) object;
                                                targetPc.sendPacketsAll(new S_DoActionGFX(targetPc.getId(), 2));
                                                targetPc.receiveDamage(pc, (int) dmg, false, false);
                                            } else if (((object instanceof L1SummonInstance)) || ((object instanceof L1PetInstance)) || ((object instanceof L1MonsterInstance))) {
                                                L1NpcInstance targetNpc = (L1NpcInstance) object;
                                                targetNpc.broadcastPacketAll(new S_DoActionGFX(targetNpc.getId(), 2));
                                                targetNpc.receiveDamage(pc, (int) dmg);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return calcDamageReduction(pc, cha, dmg, attr);
    }

    public static double getLightningEdgeDamage(L1PcInstance pc, L1Character cha) {
        double dmg = 0.0D;
        int chance = _random.nextInt(100) + 1;
        if (4 >= chance) {
            int sp = pc.getSp();
            int intel = pc.getInt();
            dmg = (intel + sp) * 2.0D + _random.nextInt(intel + sp) * 2;
            pc.sendPacketsAll(new S_SkillSound(cha.getId(), 10));
        }
        return calcDamageReduction(pc, cha, dmg, 8);
    }

    public static void giveArkMageDiseaseEffect(L1PcInstance pc, L1Character cha) {
        int chance = _random.nextInt(1000) + 1;
        int probability = (5 - cha.getMr() / 10 * 5) * 10;
        if (probability == 0) {
            probability = 10;
        }
        if (probability >= chance) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, 56, cha.getId(), cha.getX(), cha.getY(), 0, 4);
        }
    }

    public static void giveTurn_Undead(L1PcInstance pc, L1Character cha) {
        L1ItemInstance weapon = pc.getWeapon();
        int chance = weapon.getEnchantLevel() + 3;
        if (_random.nextInt(1000) <= (chance * 10)) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, 18, cha.getId(), cha.getX(), cha.getY(), 0, 4);
        }
    }

    public static void giveFettersEffect(L1PcInstance pc, L1Character cha) {
        int fettersTime = 8;
        int chance = 1;
        if (isFreeze(cha)) {
            return;
        }
        if (_random.nextInt(1000) <= (chance * 10)) {
            L1SpawnUtil.spawnEffect(81182, fettersTime, cha.getX(), cha.getY(), pc.getMapId(), pc, 0);
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance targetPc = (L1PcInstance) cha;
                targetPc.setSkillEffect(4000, fettersTime * 1000);
                targetPc.sendPacketsAll(new S_SkillSound(targetPc.getId(), 4184));
                targetPc.sendPackets(new S_Paralysis(6, true));
            } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                L1NpcInstance npc = (L1NpcInstance) cha;
                npc.setSkillEffect(4000, fettersTime * 1000);
                npc.broadcastPacketAll(new S_SkillSound(npc.getId(), 4184));
                //npc.setParalyzed(true);
                npc.setPassispeed(0);
            }
        }
    }

    /**
     * 魔防及屬性減傷計算
     *
     */
    public static double calcDamageReduction(L1PcInstance pc, L1Character cha, double dmg, int attr) {
        if (isFreeze(cha)) {
            return 0.0D;
        }
        int mr = cha.getMr();
        double mrFloor = 0.0D;
        if (mr < 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 2D);
        } else {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 10D);
        }
        double mrCoefficient = 0.0D;
        if (mr < 100) {
            mrCoefficient = 1.0D - 0.01D * mrFloor;
        } else {
            mrCoefficient = 0.6D - 0.01D * mrFloor;
        }
        dmg *= mrCoefficient;
        int resist = 0;
        if (attr == 1) {
            resist = cha.getEarth();
        } else if (attr == 2) {
            resist = cha.getFire();
        } else if (attr == 4) {
            resist = cha.getWater();
        } else if (attr == 8) {
            resist = cha.getWind();
        }
        int resistFloor = (int) (0.16D * Math.abs(resist));
        if (resist < 0) {
            resistFloor *= -1;
        }
        double attrDeffence = resistFloor / 32.0D;
        dmg = (1.0D - attrDeffence) * dmg;
        return dmg;
    }

    public static boolean isFreeze(L1Character cha) {
        if (cha.hasSkillEffect(78)) {// 絕對屏障
            return true;
        }
        if (cha.hasSkillEffect(50)) {// 冰矛圍籬
            return true;
        }
        if (cha.hasSkillEffect(157)) {// 大地屏障
            return true;
        }
        if (cha.hasSkillEffect(31)) {
            cha.removeSkillEffect(31);
            int castgfx2 = SkillsTable.get().getTemplate(31).getCastGfx2();
            cha.broadcastPacketAll(new S_SkillSound(cha.getId(), castgfx2));
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), castgfx2));
            }
            return true;
        }
        return false;
    }

    public int getWeaponId() {
        return _weaponId;
    }

    public void setWeaponId(int i) {
        _weaponId = i;
    }

    /**
     * 魔法武器發動的技能名稱
     */
    public String getSkillName() {
        return _skillName;
    }

    /**
     * 魔法武器發動的技能名稱
     */
    public void setSkillName(String i) {
        _skillName = i;
    }

    public int getProbability() {
        return _probability;
    }

    public void setProbability(int i) {
        _probability = i;
    }

    public int getFixDamage() {
        return _fixDamage;
    }

    public void setFixDamage(int i) {
        _fixDamage = i;
    }

    public int getRandomDamage() {
        return _randomDamage;
    }

    public void setRandomDamage(int i) {
        _randomDamage = i;
    }

    public int getArea() {
        return _area;
    }

    public void setArea(int i) {
        _area = i;
    }

    public int getSkillId() {
        return _skillId;
    }

    public void setSkillId(int i) {
        _skillId = i;
    }

    public int getSkillTime() {
        return _skillTime;
    }

    public void setSkillTime(int i) {
        _skillTime = i;
    }

    public int getEffectId() {
        return _effectId;
    }

    public void setEffectId(int i) {
        _effectId = i;
    }

    public int getEffectTarget() {
        return _effectTarget;
    }

    public void setEffectTarget(int i) {
        _effectTarget = i;
    }

    public boolean isArrowType() {
        return _isArrowType;
    }

    public void setArrowType(boolean i) {
        _isArrowType = i;
    }

    public int getAttr() {
        return _attr;
    }

    public void setAttr(int i) {
        _attr = i;
    }

    //SRC0724
    public int getEffectId1() {
        return this._effectId1;
    }

    public void setEffectId1(int i) {
        _effectId1 = i;
    }

    //
    public int getEffectId2() {
        return this._effectId2;
    }

    public void setEffectId2(int i) {
        _effectId2 = i;
    }

    //
    public int getEffectId3() {
        return this._effectId3;
    }

    public void setEffectId3(int i) {
        _effectId3 = i;
    }

    //
    public int getEffectId4() {
        return this._effectId4;
    }

    public void setEffectId4(int i) {
        _effectId4 = i;
    }

    //
    public int getEffectTarget1() {
        return this._effectTarget1;
    }

    public void setEffectTarget1(int i) {
        _effectTarget1 = i;
    }

    //
    public int getEffectTarget2() {
        return this._effectTarget2;
    }

    public void setEffectTarget2(int i) {
        _effectTarget2 = i;
    }

    //
    public int getEffectTarget3() {
        return this._effectTarget3;
    }

    public void setEffectTarget3(int i) {
        _effectTarget3 = i;
    }

    //
    public int getEffectTarget4() {
        return this._effectTarget4;
    }

    public void setEffectTarget4(int i) {
        _effectTarget4 = i;
    }

    //
    public int getEffectXY1() {
        return this._effect_xy1;
    }

    public void setEffectXY1(int i) {
        _effect_xy1 = i;
    }

    //
    public int getEffectXY2() {
        return this._effect_xy2;
    }

    public void setEffectXY2(int i) {
        _effect_xy2 = i;
    }

    public int getEffectXY3() {
        return this._effect_xy3;
    }

    //
    public void setEffectXY3(int i) {
        _effect_xy3 = i;
    }

    public int getEffectXY4() {
        return this._effect_xy4;
    }

    //
    public void setEffectXY4(int i) {
        _effect_xy4 = i;
    }
}
