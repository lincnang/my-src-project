package com.lineage.list;

import com.lineage.server.model.Instance.L1PcInstance;

import java.util.ArrayList;

import static com.lineage.server.model.skill.L1SkillId.*;

public class PcLvSkillList {
    /**
     * 一般魔法老師(限制教學到3級魔法)
     *
     */
    public static ArrayList<Integer> scount(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        switch (pc.getType()) {
            case 0: // 王族
                //			if (pc.getLevel() >= 10) {
                //				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                //			if (pc.getLevel() >= 20) {
                //				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                //			break;
            case 1: // 騎士
                if (pc.getLevel() >= 50) {
                    //				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
                    //					skillList.add(new Integer(skillid));
                    //				}
                }
                break;
            case 2: // 精靈
                //			if (pc.getLevel() >= 8) {
                //				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                //			if (pc.getLevel() >= 16) {
                //				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                if (pc.getLevel() >= 24) {
                    for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
                        skillList.add(skillid);
                    }
                }
                break;
            case 3: // 法師
                //			if (pc.getLevel() >= 4) {
                //				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                //			if (pc.getLevel() >= 8) {
                //				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                if (pc.getLevel() >= 12) {
                    for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
                        skillList.add(skillid);
                    }
                }
                break;
            case 4: // 黑妖
                //			if (pc.getLevel() >= 12) {
                //				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                //			if (pc.getLevel() >= 24) {
                //				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                //			break;
            case 7:
                //			if (pc.getLevel() >= 50) {
                //				for (int skillid = HEAL - 1; skillid < HOLY_WEAPON; skillid++) {
                //					skillList.add(new Integer(skillid));
                //				}
                //			}
                break;
        }
        return skillList;
    }

    /**
     * 幻術
     *
     */
    public static ArrayList<Integer> isIllusionist(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        if (pc.getLevel() >= 10) {
            for (int skillid = (MIRROR_IMAGE - 1); skillid < BONE_BREAK; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 20) {
            for (int skillid = (ILLUSION_LICH - 1); skillid < PHANTASM; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 30) {
            for (int skillid = (ARM_BREAKER - 1); skillid < INSIGHT; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 40) {
            for (int skillid = (PANIC - 1); skillid < CUBE_BALANCE; skillid++) {
                skillList.add(skillid);
            }
        }
        return skillList;
    }

    /**
     * 龍騎
     *
     */
    public static ArrayList<Integer> isDragonKnight(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        if (pc.getLevel() >= 15) {
            for (int skillid = (DRAGON_SKIN - 1); skillid < MAGMA_BREATH; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 30) {
            for (int skillid = (AWAKEN_ANTHARAS - 1); skillid < THUNDER_GRAB; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 45) {
            for (int skillid = (HORROR_OF_DEATH - 1); skillid < AWAKEN_VALAKAS; skillid++) {
                skillList.add(skillid);
            }
        }
        return skillList;
    }

    /**
     * 黑妖
     *
     */
    public static ArrayList<Integer> isDarkelf(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        //		if (pc.getLevel() >= 12) {
        //			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        //		if (pc.getLevel() >= 24) {
        //			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        if (pc.getLevel() >= 15) {
            for (int skillid = (BLIND_HIDING - 1); skillid < BRING_STONE; skillid++) {
                skillList.add(skillid);
            }
            skillList.add(DRESS_MIGHTY - 1);
        }
        //		if (pc.getLevel() >= 30) {
        //			for (int skillid = (MOVING_ACCELERATION - 1); skillid < VENOM_RESIST; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //			skillList.add(new Integer(DRESS_DEXTERITY - 1));
        //		}
        if (pc.getLevel() >= 45) {
            for (int skillid = (DOUBLE_BREAK - 1); skillid < FINAL_BURN; skillid++) {
                skillList.add(skillid);
            }
            skillList.add(DRESS_EVASION - 1);
            skillList.add(Shadow_Dash - 1);
            skillList.add(M_ASSASSIN - 1);
        }
        return skillList;
    }

    /**
     * 法師
     *
     */
    public static ArrayList<Integer> isWizard(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        //		if (pc.getLevel() >= 4) {
        //			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        //		if (pc.getLevel() >= 8) {
        //			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        if (pc.getLevel() >= 12) {
            for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 16) {
            for (int skillid = (FIREBALL - 1); skillid < MEDITATION; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 20) {
            for (int skillid = (CURSE_PARALYZE - 1); skillid < DARKNESS; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 24) {
            for (int skillid = (CREATE_ZOMBIE - 1); skillid < BLESS_WEAPON; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 28) {
            for (int skillid = (HEAL_ALL - 1); skillid < DISEASE; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 32) {
            for (int skillid = (FULL_HEAL - 1); skillid < SILENCE; skillid++) {
                skillList.add(skillid);
            }
        }
        //        if (pc.getLevel() >= 36) {
        //            for (int skillid = (LIGHTNING_STORM - 1); skillid < COUNTER_DETECTION; skillid++) {
        //                skillList.add(new Integer(skillid));
        //            }
        //        }
        if (pc.getLevel() >= 40) {
            //for (int skillid = (CREATE_MAGICAL_WEAPON - 1); skillid < FREEZING_BLIZZARD; skillid++) {
            for (int skillid = (DEATH_HEAL - 1); skillid < FREEZING_BLIZZARD; skillid++) {
                skillList.add(skillid);
            }
        }
        return skillList;
    }

    /**
     * 精靈
     *
     */
    public static ArrayList<Integer> isElf(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        //		if (pc.getLevel() >= 8) {
        //			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        //		if (pc.getLevel() >= 16) {
        //			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        if (pc.getLevel() >= 24) {
            for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 32) {
            for (int skillid = (FIREBALL - 1); skillid < MEDITATION; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 40) {
            for (int skillid = (CURSE_PARALYZE - 1); skillid < DARKNESS; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 48) {
            for (int skillid = (CREATE_ZOMBIE - 1); skillid < BLESS_WEAPON; skillid++) {
                skillList.add(skillid);
            }
        }
        if (pc.getLevel() >= 10) {
            skillList.add(RESIST_MAGIC - 1);
            skillList.add(BODY_TO_MIND - 1);
            skillList.add(TELEPORT_TO_MATHER - 1);
        }
        if (pc.getLevel() >= 20) {
            skillList.add(CLEAR_MIND - 1);
            skillList.add(RESIST_ELEMENTAL - 1);
        }
        if (pc.getLevel() >= 30) {
            skillList.add(TRIPLE_ARROW - 1);
            skillList.add(RETURN_TO_NATURE - 1);
            skillList.add(BLOODY_SOUL - 1);
            skillList.add(ELEMENTAL_PROTECTION - 1);
            switch (pc.getElfAttr()) {
                case 1:// 地屬性
                    skillList.add(EARTH_SKIN - 1);
                    skillList.add(ENTANGLE - 1);
                    break;
                case 2:// 火屬性
                    skillList.add(FIRE_WEAPON - 1);
                    break;
                case 4:// 水屬性
                    skillList.add(WATER_LIFE - 1);
                    break;
                case 8:// 風屬性
                    skillList.add(WIND_SHOT - 1);
                    skillList.add(WIND_WALK - 1);
                    break;
            }
        }
        if (pc.getLevel() >= 40) {
            skillList.add(ELEMENTAL_FALL_DOWN - 1);
            skillList.add(ERASE_MAGIC - 1);
//            skillList.add(LESSER_ELEMENTAL - 1);
            switch (pc.getElfAttr()) {
                case 1:// 地屬性
                    skillList.add(EARTH_BIND - 1);
                    skillList.add(EARTH_BLESS - 1);
                    break;
                case 2:// 火屬性
                    skillList.add(FIRE_BLESS - 1);
                    break;
                case 4:// 水屬性
                    skillList.add(NATURES_TOUCH - 1);
                    skillList.add(AQUA_PROTECTER - 1);
                    break;
                case 8:// 風屬性
                    skillList.add(STORM_EYE - 1);
                    break;
            }
        }
        if (pc.getLevel() >= 50) {
            skillList.add(COUNTER_MIRROR - 1);
            skillList.add(AREA_OF_SILENCE - 1);
            skillList.add(GREATER_ELEMENTAL - 1);
            skillList.add(HeavyStrikeArrow - 1);
            skillList.add(Eagle_Eye - 1);
            switch (pc.getElfAttr()) {
                case 1:// 地屬性
                    skillList.add(IRON_SKIN - 1);
                    skillList.add(EXOTIC_VITALIZE - 1);
                    break;
                case 2:// 火屬性
                    skillList.add(BURNING_WEAPON - 1);
                    skillList.add(ELEMENTAL_FIRE - 1);
                    skillList.add(SOUL_OF_FLAME - 1);
                    skillList.add(ADDITIONAL_FIRE - 1);
                    break;
                case 4:// 水屬性
                    skillList.add(NATURES_BLESSING - 1);
                    skillList.add(CALL_OF_NATURE - 1);
                    skillList.add(POLLUTE_WATER - 1);
                    break;
                case 8:// 風屬性
                    skillList.add(STORM_SHOT - 1);
                    skillList.add(WIND_SHACKLE - 1);
                    skillList.add(STRIKER_GALE - 1);
                    break;
            }
        }
        return skillList;
    }

    /**
     * 騎士
     *
     */
    public static ArrayList<Integer> isKnight(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        //		if (pc.getLevel() >= 50) {// magic lv1
        //			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        if (pc.getLevel() >= 50) {// Knight magic lv1
            skillList.add(SHOCK_STUN - 1);
            skillList.add(REDUCTION_ARMOR - 1);
            skillList.add(SOLID_CARRIAGE - 1);
            skillList.add(COUNTER_BARRIER - 1);
            skillList.add(Counter_attack - 1);
            skillList.add(PASSIVE_SWORD - 1);
            skillList.add(PASSIVE_SWORD_2 - 1);
            skillList.add(LICH_CHANGE_LOCATION - 1);//換位
        }
        if (pc.getLevel() >= 60) {// Knight magic lv2
            skillList.add(SOLID_CARRIAGE - 1);
        }
        return skillList;
    }

    /**
     * 王族
     *
     */
    public static ArrayList<Integer> isCrown(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        //		if (pc.getLevel() >= 10) {// magic lv1
        //			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //
        //		}
        //		if (pc.getLevel() >= 20) {// magic lv2
        //			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        if (pc.getLevel() >= 15) {// Crown magic lv1
            skillList.add(TRUE_TARGET - 1);
        }
        //		if (pc.getLevel() >= 30) {// Crown magic lv2
        //			skillList.add(new Integer(CALL_CLAN - 1));
        //		}
        if (pc.getLevel() >= 50) {// Crown magic lv3
            skillList.add(GLOWING_AURA_2 - 1);
        }
        if (pc.getLevel() >= 40) {// Crown magic lv3
            skillList.add(GLOWING_AURA - 1);
        }
        if (pc.getLevel() >= 50) {// Crown magic lv4
            skillList.add(Armor_Valor - 1);
        }
        if (pc.getLevel() >= 50) {// Crown magic lv5
            skillList.add(BRAVE_AURA - 1);
            skillList.add(KINGDOM_STUN - 1);
            skillList.add(GRACE_AVATAR - 1);
        }
        if (pc.getLevel() >= 50) {// Crown magic lv6
            skillList.add(SHINING_AURA - 1);
        }
        return skillList;
    }

    public static ArrayList<Integer> isWarrior(final L1PcInstance pc) {
        final ArrayList<Integer> skillList = new ArrayList<>();
        //		if (pc.getLevel() >= 50) {
        //			for (int skillid = HEAL - 1; skillid < HOLY_WEAPON; skillid++) {
        //				skillList.add(new Integer(skillid));
        //			}
        //		}
        if (pc.getLevel() >= 15) {
            skillList.add(PASSIVE_SLAYER - 1);
        }
        if (pc.getLevel() >= 30) {
            skillList.add(HOWL - 1);
        }
        if (pc.getLevel() >= 45) {
            skillList.add(PASSIVE_CRASH - 1);
            skillList.add(TOMAHAWK - 1);
            skillList.add(Warrior_Charge - 1);
            skillList.add(GAIA - 1);
        }
        if (pc.getLevel() >= 50) {
            skillList.add(PASSIVE_ARMORGARDE - 1);
        }
        if (pc.getLevel() >= 55) {
            skillList.add(GIGANTIC - 1);
            skillList.add(Blood_strength - 1);
        }
        if (pc.getLevel() >= 60) {
            skillList.add(DESPERADO - 1);
            skillList.add(PASSIVE_FURY - 1);
            skillList.add(PASSIVE_TITANROCK - 1);
        }
        if (pc.getLevel() >= 60) {
            skillList.add(POWERGRIP - 1);
        }
        if (pc.getLevel() >= 60) {
            skillList.add(PASSIVE_TITANMAGIC - 1);
        }
        if (pc.getLevel() >= 60) {
            skillList.add(PASSIVE_TITANBULLET - 1);
        }
        if (pc.getLevel() >= 60) {
            skillList.add(TITAN_STUN - 1);
        }
        return skillList;
    }
}
