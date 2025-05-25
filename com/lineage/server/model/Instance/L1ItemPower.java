package com.lineage.server.model.Instance;

import java.util.HashMap;
import java.util.Map;

public class L1ItemPower {
    /**
     * 抗魔加成變化清單
     */
    public static final Map<Integer, Integer> MR = new HashMap<Integer, Integer>();
    /**
     * 回魔量加成變化清單
     */
    public static final Map<Integer, Integer> MPR = new HashMap<Integer, Integer>();
    /**
     * 魔攻加成變化清單
     */
    public static final Map<Integer, Integer> SP = new HashMap<Integer, Integer>();
    /**
     * 雙擊率加成變化清單
     */
    public static final Map<Integer, Integer> X2 = new HashMap<Integer, Integer>();
    /**
     * 防具增加命中率加成變化清單
     */
    public static final Map<Integer, Integer> HIT = new HashMap<Integer, Integer>();
    /**
     * 體力上限加成變化清單
     */
    public static final Map<Integer, Integer> HP = new HashMap<Integer, Integer>();
    /**
     * 魔力上限加成變化清單
     */
    public static final Map<Integer, Integer> MP = new HashMap<Integer, Integer>();
    /**
     * 傷害減免加成變化清單
     */
    public static final Map<Integer, Integer> DMG_REDUCE = new HashMap<Integer, Integer>();
    private final L1ItemInstance _itemInstance;

    protected L1ItemPower(L1ItemInstance itemInstance) {
        _itemInstance = itemInstance;
    }

    public static void load() {
        MR.put(Integer.valueOf(20011), new Integer(1));// 抗魔法頭盔
        MR.put(Integer.valueOf(120011), new Integer(1));// 抗魔法頭盔
        MR.put(Integer.valueOf(20110), new Integer(1));// 抗魔法鏈甲
        MR.put(Integer.valueOf(120110), new Integer(1));// 抗魔法鏈甲
        MR.put(Integer.valueOf(20056), new Integer(2));// 抗魔法斗篷
        MR.put(Integer.valueOf(120056), new Integer(2));// 抗魔法斗篷
        MR.put(Integer.valueOf(330005), new Integer(1));// 抗魔法脛甲
        MR.put(Integer.valueOf(20117), new Integer(1));// 巴風特盔甲
        MR.put(Integer.valueOf(20049), new Integer(2));// 巨蟻女皇的金翅膀
        MR.put(Integer.valueOf(20050), new Integer(2));// 巨蟻女皇的銀翅膀
        MR.put(Integer.valueOf(20078), new Integer(3));// 混沌斗篷
        MR.put(Integer.valueOf(400019), new Integer(1));// 特製究極抗魔法T恤
        MR.put(Integer.valueOf(400023), new Integer(1));// 酷金盔甲
        MR.put(Integer.valueOf(300427), new Integer(1));// 塔拉斯長靴
        MR.put(Integer.valueOf(300433), new Integer(3));// 馬昆斯斗篷
        MR.put(Integer.valueOf(1300433), new Integer(3));// 馬昆斯斗篷
        MR.put(Integer.valueOf(310126), new Integer(2));// 暴走的變異披風
        MR.put(Integer.valueOf(70092), new Integer(2));// 神威斗篷
        MR.put(Integer.valueOf(70093), new Integer(2));// 強化抗魔斗篷
        MR.put(Integer.valueOf(401028), new Integer(1));// 滅魔的小籐甲
        MR.put(Integer.valueOf(401029), new Integer(1));// 滅魔的長袍
        MR.put(Integer.valueOf(401030), new Integer(1));// 滅魔的鱗甲
        MR.put(Integer.valueOf(401031), new Integer(1));// 滅魔的金屬盔甲
        MR.put(Integer.valueOf(400052), new Integer(2));// 神聖的魔法防禦頭盔
        MPR.put(Integer.valueOf(410163), new Integer(1));// 冥想魔杖
        MPR.put(Integer.valueOf(1410163), new Integer(1));// 冥想魔杖
        SP.put(Integer.valueOf(20107), new Integer(1));// 巫妖斗篷
        SP.put(Integer.valueOf(120107), new Integer(1));// 巫妖斗篷
        SP.put(Integer.valueOf(300431), new Integer(1));// 法師臂甲
        SP.put(Integer.valueOf(300432), new Integer(1));// 幻象眼魔的心眼
        SP.put(Integer.valueOf(1300432), new Integer(1));// 幻象眼魔的心眼
        SP.put(Integer.valueOf(300433), new Integer(1));// 馬昆斯斗篷
        SP.put(Integer.valueOf(1300433), new Integer(1));// 馬昆斯斗篷
        X2.put(Integer.valueOf(410188), new Integer(1));// 咆哮雙刀
        HIT.put(Integer.valueOf(400051), new Integer(1));// 激怒手套
        HIT.put(Integer.valueOf(21309), new Integer(1));// 激怒手套
        HP.put(Integer.valueOf(300429), new Integer(25));// 體力臂甲
        MP.put(Integer.valueOf(300426), new Integer(10));// 大法師之帽
        DMG_REDUCE.put(Integer.valueOf(300430), new Integer(1));// 守護的臂甲
        DMG_REDUCE.put(Integer.valueOf(310125), new Integer(1));// 石製手套
        DMG_REDUCE.put(Integer.valueOf(1310125), new Integer(1));// 石製手套
    }

    /**
     * 抗魔加成變化
     *
     */
    protected int getMr() {
        int mr = _itemInstance.getItem().get_mdef();// DB設定抗魔
        Integer integer = MR.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            mr += _itemInstance.getEnchantLevel() * integer.intValue();
        }
        return mr;
    }

    /**
     * 回魔量加成變化
     *
     */
    protected int getMpr() {
        int mpr = _itemInstance.getItem().get_addmpr();// DB設定回魔量
        Integer integer = MPR.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            mpr += _itemInstance.getEnchantLevel() * integer.intValue();
        }
        return mpr;
    }

    /**
     * 喝水量加成變化
     *
     */
    protected int getUhp() {
        int uhp = _itemInstance.getItem().get_up_hp_potion();// 增加藥水回復量%
        //		70200	星星力量耳環
        //		70201	星星敏捷耳環
        //		70202	星星智力耳環
        //		70203	月亮力量項鏈
        //		70204	月亮敏捷項鏈
        //		70205	月亮智力項鏈
        if (_itemInstance.getItemId() >= 70200 && _itemInstance.getItemId() <= 70205) {
            if (_itemInstance.getEnchantLevel() == 5) {
                uhp += 4;
            } else if (_itemInstance.getEnchantLevel() == 6) {
                uhp += 6;
            } else if (_itemInstance.getEnchantLevel() == 7) {
                uhp += 10;
            } else if (_itemInstance.getEnchantLevel() == 8) {
                uhp += 14;
            }
        }
        return uhp;
    }

    /**
     * 喝水量加成變化 num
     *
     */
    protected int getUhp_NUM() {
        int uhp_num = _itemInstance.getItem().get_uhp_number();// 增加藥水回復量num
        //		70200	星星力量耳環
        //		70201	星星敏捷耳環
        //		70202	星星智力耳環
        //		70203	月亮力量項鏈
        //		70204	月亮敏捷項鏈
        //		70205	月亮智力項鏈
        if (_itemInstance.getItemId() >= 70200 && _itemInstance.getItemId() <= 70205) {
            if (_itemInstance.getEnchantLevel() == 6) {
                uhp_num += 2;
            } else if (_itemInstance.getEnchantLevel() == 7) {
                uhp_num += 4;
            } else if (_itemInstance.getEnchantLevel() == 8) {
                uhp_num += 6;
            }
        }
        return uhp_num;
    }

    /**
     * 魔攻加成變化
     *
     */
    protected int getSp() {
        int sp = _itemInstance.getItem().get_addsp();// DB設定SP
        Integer integer = SP.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            if (_itemInstance.getItemId() == 20107 || _itemInstance.getItemId() == 120107) {// 巫妖斗篷
                if (_itemInstance.getEnchantLevel() >= 3) {// 加3開始增加魔攻
                    sp += (_itemInstance.getEnchantLevel() - 2) * integer.intValue();
                }
            } else if (_itemInstance.getItemId() == 300431) {// 法師臂甲
                int enchantlvl = _itemInstance.getEnchantLevel();
                if (enchantlvl >= 5 && enchantlvl <= 6) {
                    sp += integer.intValue();
                } else if (enchantlvl >= 7 && enchantlvl <= 8) {
                    sp += integer.intValue() * 2;
                } else if (enchantlvl >= 9) {
                    sp += integer.intValue() * 3;
                }

            } else if (_itemInstance.getItemId() == 300433 || _itemInstance.getItemId() == 1300433) {
                if (_itemInstance.getEnchantLevel() >= 7) {// 加7開始增加魔攻
                    sp += (_itemInstance.getEnchantLevel() - 6) * integer.intValue();
                }
            } else if (_itemInstance.getItemId() == 300432 || _itemInstance.getItemId() == 1300432) {
                if (_itemInstance.getEnchantLevel() >= 8) {// 加8開始增加魔攻
                    sp += (_itemInstance.getEnchantLevel() - 6) * integer.intValue();
                }
            } else {
                sp += _itemInstance.getEnchantLevel() * integer.intValue();
            }
        }
        return sp;
    }

    /**
     * 雙擊率加成變化
     *
     */
    protected int getDoubleDmgChance() {
        int x2 = _itemInstance.getItem().getDoubleDmgChance();// DB設定雙擊率
        Integer integer = X2.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            x2 += _itemInstance.getEnchantLevel() * integer.intValue();
        }
        return x2;
    }

    /**
     * 防具增加命中率加成變化
     *
     */
    protected int getHitModifierByArmor() {
        int hit = _itemInstance.getItem().getHitModifierByArmor();// DB設定命中率
        Integer integer = HIT.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            if (_itemInstance.getItemId() == 400051 || _itemInstance.getItemId() == 21309) {// 激怒手套
                if (_itemInstance.getEnchantLevel() >= 5) {// 加5開始增加命中率
                    hit += (_itemInstance.getEnchantLevel() - 4) * integer.intValue();
                }
            } else {
                hit += _itemInstance.getEnchantLevel() * integer.intValue();
            }
        }
        return hit;
    }

    /**
     * 體力上限加成變化
     *
     */
    protected int get_addhp() {
        int hp = _itemInstance.getItem().get_addhp();// DB設定增加血量
        Integer integer = HP.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            if (_itemInstance.getItemId() == 300429) {// 體力臂甲
                int enchantlvl = _itemInstance.getEnchantLevel();
                if (enchantlvl >= 5 && enchantlvl <= 6) {
                    hp += integer.intValue();
                } else if (enchantlvl >= 7 && enchantlvl <= 8) {
                    hp += integer.intValue() * 2;
                } else if (enchantlvl >= 9) {
                    hp += integer.intValue() * 3;
                }
            } else {
                hp += _itemInstance.getEnchantLevel() * integer.intValue();
            }
        }
        return hp;
    }

    /**
     * MP上限加成變化
     *
     */
    protected int get_addmp() {
        int mp = _itemInstance.getItem().get_addmp();// DB設定增加mp
        Integer integer = MP.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            mp += _itemInstance.getEnchantLevel() * integer.intValue();
        }
        return mp;
    }

    /**
     * 傷害減免加成變化
     *
     */
    protected int getDamageReduction() {
        int reduce = _itemInstance.getItem().getDamageReduction();// DB設定傷害減免
        Integer integer = DMG_REDUCE.get(Integer.valueOf(_itemInstance.getItemId()));
        if (integer != null) {
            if (_itemInstance.getItemId() == 300430) {// 守護的臂甲
                int enchantlvl = _itemInstance.getEnchantLevel();
                if (enchantlvl >= 5 && enchantlvl <= 6) {
                    reduce += integer.intValue();
                } else if (enchantlvl >= 7 && enchantlvl <= 8) {
                    reduce += integer.intValue() * 2;
                } else if (enchantlvl >= 9) {
                    reduce += integer.intValue() * 3;
                }
            } else if (_itemInstance.getItemId() == 310125 || _itemInstance.getItemId() == 1310125) {
                if (_itemInstance.getEnchantLevel() >= 7) {// 加7開始增加傷害減免
                    reduce += (_itemInstance.getEnchantLevel() - 6) * integer.intValue();
                }
            } else {
                reduce += _itemInstance.getEnchantLevel() * integer.intValue();
            }
        }
        return reduce;
    }

    /**
     * 飾品加成變化
     *
     */
    protected void GreaterAtEnchant(L1PcInstance owner, int randomELevel) {
        int level = _itemInstance.getEnchantLevel();
        if (randomELevel == -1) {// 強化值倒扣時則以倒扣前的強化值計算
            level += 1;
            // System.out.println("level =" + level);
        }
        switch (_itemInstance.getItem().get_greater()) {
            case 0:// 0:耐性(耳環/項鏈)
                switch (level) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        owner.addMaxHp(randomELevel * 5);
                        break;
                    case 3:
                    case 4:
                        owner.addMaxHp(randomELevel * 10);
                        break;
                    case 5:
                        owner.addMaxHp(randomELevel * 10);
                        owner.add_up_hp_potion(randomELevel * 2);
                        break;
                    case 6:
                        owner.add_up_hp_potion(randomELevel * 2);
                        break;
                    case 7:
                        owner.addMaxHp(randomELevel * 10);
                        owner.add_up_hp_potion(randomELevel * 2);
                        break;
                    case 8:
                        owner.add_up_hp_potion(randomELevel * 2);
                        break;
                }
                break;
            case 1:// 1:熱情(戒指)
                switch (level) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        owner.addMaxHp(randomELevel * 5);
                        break;
                    case 3:
                    case 4:
                        owner.addMaxHp(randomELevel * 10);
                        break;
                    case 5:
                        owner.addMaxHp(randomELevel * 10);
                        owner.addDmgModifierByArmor(randomELevel * 1);
                        owner.addBowDmgModifierByArmor(randomELevel * 1);
                        break;
                    case 6:
                        owner.addDmgModifierByArmor(randomELevel * 1);
                        owner.addBowDmgModifierByArmor(randomELevel * 1);
                        break;
                    case 7:
                        owner.addMaxHp(randomELevel * 10);
                        owner.addDmgModifierByArmor(randomELevel * 1);
                        owner.addBowDmgModifierByArmor(randomELevel * 1);
                        break;
                    case 8:
                        owner.addDmgModifierByArmor(randomELevel * 1);
                        owner.addBowDmgModifierByArmor(randomELevel * 1);
                        break;
                }
                break;
            case 2:// 2:意志(皮帶)
                switch (level) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        owner.addMaxHp(randomELevel * 5);
                        break;
                    case 3:
                    case 4:
                        owner.addMaxHp(randomELevel * 10);
                        break;
                    case 5:
                        owner.addMaxHp(randomELevel * 10);
                        owner.addDamageReductionByArmor(randomELevel * 1);
                        break;
                    case 6:
                        owner.addDamageReductionByArmor(randomELevel * 1);
                        break;
                    case 7:
                        owner.addMaxHp(randomELevel * 10);
                        owner.addDamageReductionByArmor(randomELevel * 1);
                        break;
                    case 8:
                        owner.addDamageReductionByArmor(randomELevel * 1);
                        break;
                }
                break;
        }
    }

    /**
     * 飾品加成效果
     */
    protected void greater(L1PcInstance owner, boolean equipment) {
        int level = _itemInstance.getEnchantLevel();
        if (level <= 0) {
            return;
        }
        if (equipment) {// 穿上
            switch (_itemInstance.getItem().get_greater()) {
                case 0:// 0:耐性(耳環/項鏈)
                    switch (level) {
                        case 0:
                            break;
                        case 1:
                            owner.addMaxHp(5);
                            break;
                        case 2:
                            owner.addMaxHp(10);
                            break;
                        case 3:
                            owner.addMaxHp(20);
                            break;
                        case 4:
                            owner.addMaxHp(30);
                            //近距儀攻擊
                            owner.addDmgModifierByArmor(1);
                            //遠距離攻擊
                            owner.addBowDmgModifierByArmor(1);
                            break;
                        case 5:
                            owner.addMaxHp(40);
                            owner.add_up_hp_potion(4);
                            owner.addDmgModifierByArmor(2);
                            owner.addBowDmgModifierByArmor(2);
                            owner.addDamageReductionByArmor(1);
                            break;
                        case 6:
                            owner.addMaxHp(40);
                            owner.add_up_hp_potion(6);
                            owner.add_uhp_number(2);
                            owner.addDmgModifierByArmor(4);
                            owner.addBowDmgModifierByArmor(4);
                            owner.addDamageReductionByArmor(2);
                            owner.addOriginalMagicHit(2);// 增加魔法命中
                            break;
                        case 7:
                            owner.addMaxHp(50);
                            owner.add_up_hp_potion(10);
                            owner.add_uhp_number(4);
                            owner.addDmgModifierByArmor(5);
                            owner.addBowDmgModifierByArmor(5);
                            owner.addDamageReductionByArmor(3);
                            owner.addOriginalMagicHit(4);// 增加魔法命中
                            break;
                        case 8:
                            owner.addMaxHp(50);
                            owner.add_up_hp_potion(14);
                            owner.add_uhp_number(6);
                            owner.addDmgModifierByArmor(6);
                            owner.addBowDmgModifierByArmor(6);
                            owner.addDamageReductionByArmor(3);
                            owner.addOriginalMagicHit(8);// 增加魔法命中
                            break;
                        case 9:
                            owner.addMaxHp(100);
                            owner.add_up_hp_potion(16);
                            owner.add_uhp_number(8);
                            owner.addDmgModifierByArmor(8);
                            owner.addBowDmgModifierByArmor(8);
                            owner.addDamageReductionByArmor(5);
                            owner.addOriginalMagicHit(10);// 增加魔法命中
                            owner.addMr(10);
                            break;
                    }
                    break;
                case 1:// 1:熱情(戒指)
                    switch (level) {
                        case 0:
                            break;
                        case 1:
                            owner.addMaxHp(5);
                            break;
                        case 2:
                            owner.addMaxHp(10);
                            break;
                        case 3:
                            owner.addMaxHp(20);
                            break;
                        case 4:
                            owner.addMaxHp(30);
                            owner.addDmgModifierByArmor(1);
                            owner.addBowDmgModifierByArmor(1);
                            owner.addDamageReductionByArmor(1);
                            break;
                        case 5:
                            owner.addMaxHp(40);
                            owner.addDmgModifierByArmor(3);
                            owner.addBowDmgModifierByArmor(3);
                            owner.addDamageReductionByArmor(3);
                            owner.addAc(-1);
                            break;
                        case 6:
                            owner.addMaxHp(40);
                            owner.addDmgModifierByArmor(4);
                            owner.addBowDmgModifierByArmor(4);
                            owner.addDamageReductionByArmor(3);
                            owner.addAc(-3);
                            owner.addSp(1);
                            break;
                        case 7:
                            owner.addMaxHp(50);
                            owner.addDmgModifierByArmor(4);
                            owner.addBowDmgModifierByArmor(4);
                            owner.addDamageReductionByArmor(4);
                            owner.addAc(-4);
                            owner.addSp(2);
                            break;
                        case 8:
                            owner.addMaxHp(50);
                            owner.addDmgModifierByArmor(5);
                            owner.addBowDmgModifierByArmor(5);
                            owner.addDamageReductionByArmor(5);
                            owner.addAc(-5);
                            owner.addSp(4);
                            break;
                        case 9:
                            owner.addMaxHp(100);
                            owner.addDmgModifierByArmor(7);
                            owner.addBowDmgModifierByArmor(7);
                            owner.addDamageReductionByArmor(5);
                            owner.addAc(-7);
                            owner.addSp(5);
                            break;
                    }
                    break;
                case 2:// 2:意志(皮帶)
                    switch (level) {
                        case 0:
                            break;
                        case 1:
                            owner.addMaxMp(5);
                            break;
                        case 2:
                            owner.addMaxMp(10);
                            break;
                        case 3:
                            owner.addMaxMp(20);
                            break;
                        case 4:
                            owner.addMaxMp(30);
                            owner.addDamageReductionByArmor(1);
                            break;
                        case 5:
                            owner.addMaxMp(40);
                            owner.addDamageReductionByArmor(2);
                            owner.addMr(-3);
                            break;
                        case 6:
                            owner.addMaxMp(40);
                            owner.addDamageReductionByArmor(4);
                            owner.addMr(5);
                            owner.addAc(-1);
                            break;
                        case 7:
                            owner.addMaxMp(50);
                            owner.addDamageReductionByArmor(4);
                            owner.addMr(7);
                            owner.addAc(-2);
                            break;
                        case 8:
                            owner.addMaxMp(50);
                            owner.addDamageReductionByArmor(5);
                            owner.addMr(10);
                            owner.addAc(-3);
                            break;
                        case 9:
                            owner.addMaxMp(75);
                            owner.addDamageReductionByArmor(7);
                            owner.addMr(15);
                            owner.addAc(-5);
                            break;
                    }
                    break;
                default:
                    break;
            }
        } else {// 脫下
            switch (_itemInstance.getItem().get_greater()) {
                case 0:// 0:耐性(耳環/項鏈)
                    switch (level) {
                        case 0:
                            break;
                        case 1:
                            owner.addMaxHp(-5);
                            break;
                        case 2:
                            owner.addMaxHp(-10);
                            break;
                        case 3:
                            owner.addMaxHp(-20);
                            break;
                        case 4:
                            owner.addMaxHp(-30);
                            owner.addDmgModifierByArmor(-1);
                            owner.addBowDmgModifierByArmor(-1);
                            break;
                        case 5:
                            owner.addMaxHp(-40);
                            owner.add_up_hp_potion(-4);
                            owner.addDamageReductionByArmor(-1);
                            owner.addDmgModifierByArmor(-2);
                            owner.addBowDmgModifierByArmor(-2);
                            break;
                        case 6:
                            owner.addMaxHp(-40);
                            owner.add_up_hp_potion(-6);
                            owner.add_uhp_number(-2);
                            owner.addDamageReductionByArmor(-2);
                            owner.addOriginalMagicHit(-2);// 增加魔法命中
                            owner.addDmgModifierByArmor(-4);
                            owner.addBowDmgModifierByArmor(-4);
                            break;
                        case 7:
                            owner.addMaxHp(-50);
                            owner.add_up_hp_potion(-10);
                            owner.add_uhp_number(-4);
                            owner.addDamageReductionByArmor(-3);
                            owner.addOriginalMagicHit(-4);// 增加魔法命中
                            owner.addDmgModifierByArmor(-5);
                            owner.addBowDmgModifierByArmor(-5);
                            break;
                        case 8:
                            owner.addMaxHp(-50);
                            owner.add_up_hp_potion(-14);
                            owner.add_uhp_number(-6);
                            owner.addDamageReductionByArmor(-3);
                            owner.addOriginalMagicHit(-8);// 增加魔法命中
                            owner.addDmgModifierByArmor(-6);
                            owner.addBowDmgModifierByArmor(-6);
                            break;
                        case 9:
                            owner.addMaxHp(-100);
                            owner.add_up_hp_potion(-16);
                            owner.add_uhp_number(-8);
                            owner.addDmgModifierByArmor(-8);
                            owner.addBowDmgModifierByArmor(-8);
                            owner.addDamageReductionByArmor(-5);
                            owner.addOriginalMagicHit(-10);// 增加魔法命中
                            owner.addMr(-10);
                            break;
                    }
                    break;
                case 1:// 1:熱情(戒指)
                    switch (level) {
                        case 0:
                            break;
                        case 1:
                            owner.addMaxHp(-5);
                            break;
                        case 2:
                            owner.addMaxHp(-10);
                            break;
                        case 3:
                            owner.addMaxHp(-20);
                            break;
                        case 4:
                            owner.addMaxHp(-30);
                            owner.addDmgModifierByArmor(-1);
                            owner.addBowDmgModifierByArmor(-1);
                            owner.addDamageReductionByArmor(-1);
                            break;
                        case 5:
                            owner.addMaxHp(-40);
                            owner.addDmgModifierByArmor(-3);
                            owner.addBowDmgModifierByArmor(-3);
                            owner.addDamageReductionByArmor(-3);
                            owner.addAc(1);
                            break;
                        case 6:
                            owner.addMaxHp(-40);
                            owner.addDmgModifierByArmor(-4);
                            owner.addBowDmgModifierByArmor(-4);
                            owner.addDamageReductionByArmor(-3);
                            owner.addAc(3);
                            owner.addSp(-1);
                            break;
                        case 7:
                            owner.addMaxHp(-50);
                            owner.addDmgModifierByArmor(-4);
                            owner.addBowDmgModifierByArmor(-4);
                            owner.addDamageReductionByArmor(-4);
                            owner.addAc(4);
                            owner.addSp(-2);
                            break;
                        case 8:
                            owner.addMaxHp(-50);
                            owner.addDmgModifierByArmor(-5);
                            owner.addBowDmgModifierByArmor(-5);
                            owner.addDamageReductionByArmor(-5);
                            owner.addAc(5);
                            owner.addSp(-4);
                            break;
                        case 9:
                            owner.addMaxHp(-100);
                            owner.addDmgModifierByArmor(-7);
                            owner.addBowDmgModifierByArmor(-7);
                            owner.addDamageReductionByArmor(-5);
                            owner.addAc(7);
                            owner.addSp(-5);
                            break;
                    }
                    break;
                case 2:// 2:意志(皮帶)
                    switch (level) {
                        case 0:
                            break;
                        case 1:
                            owner.addMaxMp(-5);
                            break;
                        case 2:
                            owner.addMaxMp(-10);
                            break;
                        case 3:
                            owner.addMaxMp(-20);
                            break;
                        case 4:
                            owner.addMaxMp(-30);
                            owner.addDamageReductionByArmor(-1);
                            break;
                        case 5:
                            owner.addMaxMp(-40);
                            owner.addDamageReductionByArmor(-2);
                            owner.addMr(3);
                            break;
                        case 6:
                            owner.addMaxMp(-40);
                            owner.addDamageReductionByArmor(-4);
                            owner.addMr(-5);
                            owner.addAc(1);
                            break;
                        case 7:
                            owner.addMaxMp(-50);
                            owner.addDamageReductionByArmor(-4);
                            owner.addMr(-7);
                            owner.addAc(2);
                            break;
                        case 8:
                            owner.addMaxMp(-50);
                            owner.addDamageReductionByArmor(-5);
                            owner.addMr(-10);
                            owner.addAc(3);
                            break;
                        case 9:
                            owner.addMaxMp(-75);
                            owner.addDamageReductionByArmor(-7);
                            owner.addMr(-15);
                            owner.addAc(5);
                            break;
                    }
                    break;
            }
        }
    }
}
