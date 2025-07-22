package com.lineage.server.timecontroller.other.ins;

import com.lineage.item_etcitem.SkillScroll;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 自動技能
 */
public class SkillSoundSkillAll extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundSkillAll.class.getName());
    private final L1PcInstance _pc;

    public SkillSoundSkillAll(final L1PcInstance pc) {
        _pc = pc;
    }

    @Override
    public void run() {
        try {
            if (_pc.isDead()) {
                return;
            }
            if (_pc.isParalyzedX()) {
                return;
            }
            // 是否鬼魂/傳送/商店
            if (_pc.isParalyzedX1(_pc)) {
                return;
            }
            if (!_pc.getMap().isUsableSkill()) {
                return;
            }
            if (!_pc.isAutoSkillAll()) {
                return;
            }
            if (_pc.getInventory().getWeight240() >= 197) { // 重量過重
                return;
            }
            synchronized (this) {
                final int sleep = 2000;
                // ■■■■■■■■■■■■■■■■■■■■■■■■ 共用 ■■■■■■■■■■■■■■■■■■■■■■■
                // 自動加速術
                if (_pc.isAutoSkill_1()) {
                    if (_pc.isSkillMastery(HASTE) && !_pc.hasSkillEffect(HASTE) && !_pc.hasSkillEffect(STATUS_HASTE) && !_pc.hasSkillEffect(GREATER_HASTE)) {
                        SkillScroll.DoMySkill1(_pc, HASTE);
                        TimeUnit.MILLISECONDS.sleep(sleep);
                    }
                }
                //				// 自動擬似魔法武器
                //				if (_pc.isAutoSkill_2()) {
                //					if (_pc.isSkillMastery(ENCHANT_WEAPON) && _pc.getCurrentMp() > 20) {
                //						L1ItemInstance tgItem = null;
                //						tgItem = _pc.getWeapon();
                //						if (tgItem != null) {
                //							if (!tgItem.isRunning()) {
                //								final L1SkillUse l1skilluse = new L1SkillUse();
                //								l1skilluse.handleCommands(_pc, ENCHANT_WEAPON, tgItem.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
                //								_pc.setCurrentMp(_pc.getCurrentMp() - 20);
                //								TimeUnit.MILLISECONDS.sleep(sleep);
                //							}
                //						}
                //					}
                //				}
                // 自動保護罩
                if (_pc.isAutoSkill_3()) {
                    if (_pc.isSkillMastery(SHIELD) && !_pc.hasSkillEffect(SHIELD)) {
                        SkillScroll.DoMySkill1(_pc, SHIELD);
                        TimeUnit.MILLISECONDS.sleep(sleep);
                    }
                }
                // 自動祝福魔法武器
                if (_pc.isAutoSkill_4()) {
                    if (_pc.isSkillMastery(BLESS_WEAPON)) {
                        L1ItemInstance tgItem = null;
                        tgItem = _pc.getWeapon();
                        if (tgItem != null) {
                            if (!tgItem.isRunning()) {
                                SkillScroll.DoMySkill1(_pc, BLESS_WEAPON);
                                TimeUnit.MILLISECONDS.sleep(sleep);
                            }
                        }
                    }
                }
                //				// 自動神聖武器
                //				if (_pc.isAutoSkill_5()) {
                //					if (_pc.isSkillMastery(HOLY_WEAPON)) {
                //						L1ItemInstance tgItem = null;
                //						tgItem = _pc.getWeapon();
                //						if (tgItem != null) {
                //							if (!tgItem.isRunning()) {
                //								SkillScroll.DoMySkill1(_pc, HOLY_WEAPON);
                //								TimeUnit.MILLISECONDS.sleep(sleep);
                //							}
                //						}
                //					}
                //				}
                // 自動通暢氣脈術
                if (_pc.isAutoSkill_6()) {
                    if (_pc.isSkillMastery(PHYSICAL_ENCHANT_DEX) && !_pc.hasSkillEffect(PHYSICAL_ENCHANT_DEX)) {
                        SkillScroll.DoMySkill1(_pc, PHYSICAL_ENCHANT_DEX);
                        TimeUnit.MILLISECONDS.sleep(sleep);
                    }
                }
                // 自動鎧甲護持
                if (_pc.isAutoSkill_7()) {
                    if (_pc.isSkillMastery(BLESSED_ARMOR) && _pc.getCurrentMp() > 20) {
                        L1ItemInstance tgItem = null;
                        tgItem = _pc.getInventory().getItemEquipped(2, 2);// 盔甲
                        if (tgItem != null) {
                            if (!tgItem.isRunning()) {
                                final L1SkillUse l1skilluse = new L1SkillUse();
                                l1skilluse.handleCommands(_pc, BLESSED_ARMOR, tgItem.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
                                _pc.setCurrentMp(_pc.getCurrentMp() - 20);
                                TimeUnit.MILLISECONDS.sleep(sleep);
                            }
                        }
                    }
                }
                // 自動體魄強健術
                if (_pc.isAutoSkill_8()) {
                    if (_pc.isSkillMastery(PHYSICAL_ENCHANT_STR) && !_pc.hasSkillEffect(PHYSICAL_ENCHANT_STR)) {
                        SkillScroll.DoMySkill1(_pc, PHYSICAL_ENCHANT_STR);
                        TimeUnit.MILLISECONDS.sleep(sleep);
                    }
                }
                // 優先使用魔法卷軸
                //if (_pc.isAutoSkill_9()) {
                //}
                // ■■■■■■■■■■■■■■■■■■■■■■■■ 王族 ■■■■■■■■■■■■■■■■■■■■■■■
                if (_pc.isCrown()) {// 王族
                    // 自動灼熱武器
                    if (_pc.isAutoSkill_10()) {
                        if (_pc.isSkillMastery(GLOWING_AURA) && !_pc.hasSkillEffect(GLOWING_AURA)) {
                            SkillScroll.DoMySkill1(_pc, GLOWING_AURA);
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        }
                    }
                    // 自動閃亮之盾
                    if (_pc.isAutoSkill_11()) {
                        if (_pc.isSkillMastery(SHINING_AURA) && !_pc.hasSkillEffect(SHINING_AURA)) {
                            SkillScroll.DoMySkill1(_pc, SHINING_AURA);
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        }
                    }
                    // 自動勇猛意志
                    if (_pc.isAutoSkill_12()) {
                        if (_pc.isSkillMastery(BRAVE_AURA) && !_pc.hasSkillEffect(BRAVE_AURA)) {
                            SkillScroll.DoMySkill1(_pc, BRAVE_AURA);
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        }
                    }
                    // 自動恩典庇護
                    if (_pc.isAutoSkill_13()) {
                        if (_pc.isSkillMastery(GRACE_AVATAR) && !_pc.hasSkillEffect(GRACE_AVATAR)) {
                            SkillScroll.DoMySkill1(_pc, GRACE_AVATAR);
                            TimeUnit.MILLISECONDS.sleep(sleep);
                        }
                    }
                } else
                    // ■■■■■■■■■■■■■■■■■■■■■■■■ 騎士 ■■■■■■■■■■■■■■■■■■■■■■■
                    if (_pc.isKnight()) {// 騎士
                        // 自動增幅防禦
                        if (_pc.isAutoSkill_10()) {
                            if (_pc.isSkillMastery(REDUCTION_ARMOR) && !_pc.hasSkillEffect(REDUCTION_ARMOR)) {
                                SkillScroll.DoMySkill1(_pc, REDUCTION_ARMOR);
                                TimeUnit.MILLISECONDS.sleep(sleep);
                            }
                        }
                        // 自動尖刺盔甲
                        if (_pc.isAutoSkill_11()) {
                            if (_pc.isSkillMastery(BOUNCE_ATTACK) && !_pc.hasSkillEffect(BOUNCE_ATTACK)) {
                                SkillScroll.DoMySkill1(_pc, BOUNCE_ATTACK);
                                TimeUnit.MILLISECONDS.sleep(sleep);
                            }
                        }
                        // 自動堅固防護
                        if (_pc.isAutoSkill_12()) {
                            if (_pc.isSkillMastery(SOLID_CARRIAGE) && !_pc.hasSkillEffect(SOLID_CARRIAGE)) {
                                SkillScroll.DoMySkill1(_pc, SOLID_CARRIAGE);
                                TimeUnit.MILLISECONDS.sleep(sleep);
                            }
                        }
                        // 自動反擊屏障
                        if (_pc.isAutoSkill_13()) {
                            if (_pc.isSkillMastery(COUNTER_BARRIER) && !_pc.hasSkillEffect(COUNTER_BARRIER)) {
                                final L1ItemInstance weapon = _pc.getWeapon();
                                if (weapon != null) {
                                    if (weapon.getItem().getType() == 3) { // 雙手劍
                                        SkillScroll.DoMySkill1(_pc, COUNTER_BARRIER);
                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                    }
                                }
                            }
                        }
                        // 自動絕禦之刃
                        if (_pc.isAutoSkill_14()) {
                            if (_pc.isSkillMastery(ABSOLUTE_BLADE) && !_pc.hasSkillEffect(ABSOLUTE_BLADE)) {
                                SkillScroll.DoMySkill1(_pc, ABSOLUTE_BLADE);
                                TimeUnit.MILLISECONDS.sleep(sleep);
                            }
                        }
                    } else
                        // ■■■■■■■■■■■■■■■■■■■■■■■■ 妖精 ■■■■■■■■■■■■■■■■■■■■■■■
                        if (_pc.isElf()) {
                            // 自動魔法防禦
                            if (_pc.isAutoSkill_10()) {
                                if (_pc.isSkillMastery(RESIST_MAGIC) && !_pc.hasSkillEffect(RESIST_MAGIC)) {
                                    SkillScroll.DoMySkill1(_pc, RESIST_MAGIC);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動屬性防禦
                            if (_pc.isAutoSkill_11()) {
                                if (_pc.isSkillMastery(RESIST_ELEMENTAL) && !_pc.hasSkillEffect(RESIST_ELEMENTAL)) {
                                    SkillScroll.DoMySkill1(_pc, RESIST_ELEMENTAL);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動淨化精神
                            if (_pc.isAutoSkill_12()) {
                                if (_pc.isSkillMastery(CLEAR_MIND) && !_pc.hasSkillEffect(CLEAR_MIND)) {
                                    SkillScroll.DoMySkill1(_pc, CLEAR_MIND);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動魔力護盾
                            if (_pc.isAutoSkill_13()) {
                                if (_pc.isSkillMastery(SOUL_BARRIER) && !_pc.hasSkillEffect(SOUL_BARRIER)) {
                                    SkillScroll.DoMySkill1(_pc, SOUL_BARRIER);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動單屬性防禦
                            if (_pc.isAutoSkill_14()) {
                                if (_pc.isSkillMastery(ELEMENTAL_PROTECTION) && !_pc.hasSkillEffect(ELEMENTAL_PROTECTION)) {
                                    SkillScroll.DoMySkill1(_pc, ELEMENTAL_PROTECTION);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動鏡反射
                            if (_pc.isAutoSkill_15()) {
                                if (_pc.isSkillMastery(COUNTER_MIRROR) && !_pc.hasSkillEffect(COUNTER_MIRROR)) {
                                    SkillScroll.DoMySkill1(_pc, COUNTER_MIRROR);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動大地防護
                            if (_pc.isAutoSkill_16()) {
                                if (_pc.isSkillMastery(EARTH_SKIN) && !_pc.hasSkillEffect(EARTH_SKIN)) {
                                    SkillScroll.DoMySkill1(_pc, EARTH_SKIN);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動大地的護衛
                            if (_pc.isAutoSkill_17()) {
                                if (_pc.isSkillMastery(EARTH_BLESS) && !_pc.hasSkillEffect(EARTH_BLESS)) {
                                    SkillScroll.DoMySkill1(_pc, EARTH_BLESS);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動鋼鐵防護
                            if (_pc.isAutoSkill_18()) {
                                if (_pc.isSkillMastery(IRON_SKIN) && !_pc.hasSkillEffect(IRON_SKIN)) {
                                    SkillScroll.DoMySkill1(_pc, IRON_SKIN);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動體能激發
                            if (_pc.isAutoSkill_19()) {
                                if (_pc.isSkillMastery(EXOTIC_VITALIZE) && !_pc.hasSkillEffect(EXOTIC_VITALIZE)) {
                                    SkillScroll.DoMySkill1(_pc, EXOTIC_VITALIZE);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動火焰武器
                            if (_pc.isAutoSkill_20()) {
                                if (_pc.isSkillMastery(FIRE_WEAPON) && !_pc.hasSkillEffect(FIRE_WEAPON)) {
                                    SkillScroll.DoMySkill1(_pc, FIRE_WEAPON);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動烈炎武器
                            if (_pc.isAutoSkill_21()) {
                                if (_pc.isSkillMastery(BURNING_WEAPON) && !_pc.hasSkillEffect(BURNING_WEAPON)) {
                                    SkillScroll.DoMySkill1(_pc, BURNING_WEAPON);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動屬性之火
                            if (_pc.isAutoSkill_22()) {
                                if (_pc.isSkillMastery(ELEMENTAL_FIRE) && !_pc.hasSkillEffect(ELEMENTAL_FIRE)) {
                                    SkillScroll.DoMySkill1(_pc, ELEMENTAL_FIRE);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動烈焰之魂
                            if (_pc.isAutoSkill_23()) {
                                if (_pc.isSkillMastery(SOUL_OF_FLAME) && !_pc.hasSkillEffect(SOUL_OF_FLAME)) {
                                    SkillScroll.DoMySkill1(_pc, SOUL_OF_FLAME);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動能量激發
                            if (_pc.isAutoSkill_24()) {
                                if (_pc.isSkillMastery(ADDITIONAL_FIRE) && !_pc.hasSkillEffect(ADDITIONAL_FIRE)) {
                                    SkillScroll.DoMySkill1(_pc, ADDITIONAL_FIRE);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動舞躍之火
                            if (_pc.isAutoSkill_25()) {
                                if (_pc.isSkillMastery(FIRE_BLESS) && !_pc.hasSkillEffect(FIRE_BLESS) // 沒有舞躍之火
                                        && !_pc.hasSkillEffect(WIND_WALK) // 沒有風之疾走
                                        && !_pc.hasSkillEffect(STATUS_BRAVE2) // 沒有荒神加速
                                        && !_pc.hasSkillEffect(STATUS_BRAVE) // 沒有勇敢藥水效果
                                        && !_pc.hasSkillEffect(STATUS_ELFBRAVE) // 沒有精靈餅乾效果
                                ) {
                                    SkillScroll.DoMySkill1(_pc, FIRE_BLESS);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動生命之泉
                            if (_pc.isAutoSkill_26()) {
                                if (_pc.isSkillMastery(NATURES_TOUCH) && !_pc.hasSkillEffect(NATURES_TOUCH)) {
                                    SkillScroll.DoMySkill1(_pc, NATURES_TOUCH);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動水之元氣
                            if (_pc.isAutoSkill_27()) {
                                if (_pc.isSkillMastery(WATER_LIFE) && !_pc.hasSkillEffect(WATER_LIFE)) {
                                    SkillScroll.DoMySkill1(_pc, WATER_LIFE);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動水之防護
                            if (_pc.isAutoSkill_28()) {
                                if (_pc.isSkillMastery(AQUA_PROTECTER) && !_pc.hasSkillEffect(AQUA_PROTECTER)) {
                                    SkillScroll.DoMySkill1(_pc, AQUA_PROTECTER);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動風之神射
                            if (_pc.isAutoSkill_29()) {
                                if (_pc.isSkillMastery(WIND_SHOT) && !_pc.hasSkillEffect(WIND_SHOT)) {
                                    SkillScroll.DoMySkill1(_pc, WIND_SHOT);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動暴風之眼
                            if (_pc.isAutoSkill_30()) {
                                if (_pc.isSkillMastery(STORM_EYE) && !_pc.hasSkillEffect(STORM_EYE)) {
                                    SkillScroll.DoMySkill1(_pc, STORM_EYE);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                            // 自動暴風神射
                            if (_pc.isAutoSkill_31()) {
                                if (_pc.isSkillMastery(STORM_SHOT) && !_pc.hasSkillEffect(STORM_SHOT)) {
                                    SkillScroll.DoMySkill1(_pc, STORM_SHOT);
                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                }
                            }
                        } else
                            // ■■■■■■■■■■■■■■■■■■■■■■■■ 法師 ■■■■■■■■■■■■■■■■■■■■■■■
                            if (_pc.isWizard()) {// 法師
                                // 自動狂暴術
                                if (_pc.isAutoSkill_10()) {
                                    if (_pc.isSkillMastery(BERSERKERS) && !_pc.hasSkillEffect(BERSERKERS)) {
                                        SkillScroll.DoMySkill1(_pc, BERSERKERS);
                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                    }
                                }
                                // 自動強力加速術
                                if (_pc.isAutoSkill_11()) {
                                    if (_pc.isSkillMastery(GREATER_HASTE) && !_pc.hasSkillEffect(HASTE) && !_pc.hasSkillEffect(STATUS_HASTE) && !_pc.hasSkillEffect(GREATER_HASTE)) {
                                        SkillScroll.DoMySkill1(_pc, GREATER_HASTE);
                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                    }
                                }
                                // 自動聖結界
                                if (_pc.isAutoSkill_12()) {
                                    if (_pc.isSkillMastery(IMMUNE_TO_HARM) && !_pc.hasSkillEffect(IMMUNE_TO_HARM)) {
                                        SkillScroll.DoMySkill1(_pc, IMMUNE_TO_HARM);
                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                    }
                                }
                                // 自動絕對屏障
					/*if (_pc.isAutoSkill_13()) {
						if (_pc.isSkillMastery(ABSOLUTE_BARRIER)
								&& !_pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
							SkillScroll.DoMySkill1(_pc, ABSOLUTE_BARRIER);
							TimeUnit.MILLISECONDS.sleep(sleep);
						}
					}*/
                                // 自動靈魂升華
                                if (_pc.isAutoSkill_14()) {
                                    if (_pc.isSkillMastery(ADVANCE_SPIRIT) && !_pc.hasSkillEffect(ADVANCE_SPIRIT)) {
                                        SkillScroll.DoMySkill1(_pc, ADVANCE_SPIRIT);
                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                    }
                                }
                                // 自動負重強化
                                if (_pc.isAutoSkill_15()) {
                                    if (_pc.isSkillMastery(DECREASE_WEIGHT) && !_pc.hasSkillEffect(DECREASE_WEIGHT)) {
                                        SkillScroll.DoMySkill1(_pc, DECREASE_WEIGHT);
                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                    }
                                }
                            } else
                                // ■■■■■■■■■■■■■■■■■■■■■■■■ 黑妖 ■■■■■■■■■■■■■■■■■■■■■■■
                                if (_pc.isDarkelf()) {// 黑妖
                                    // 自動影之防護
                                    if (_pc.isAutoSkill_10()) {
                                        if (_pc.isSkillMastery(SHADOW_ARMOR) && !_pc.hasSkillEffect(SHADOW_ARMOR)) {
                                            SkillScroll.DoMySkill1(_pc, SHADOW_ARMOR);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動附加劇毒
                                    if (_pc.isAutoSkill_11()) {
                                        if (_pc.isSkillMastery(ENCHANT_VENOM) && !_pc.hasSkillEffect(ENCHANT_VENOM)) {
                                            SkillScroll.DoMySkill1(_pc, ENCHANT_VENOM);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動燃燒鬥志
                                    if (_pc.isAutoSkill_12()) {
                                        if (_pc.isSkillMastery(BURNING_SPIRIT) && !_pc.hasSkillEffect(BURNING_SPIRIT)) {
                                            SkillScroll.DoMySkill1(_pc, BURNING_SPIRIT);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動雙重破壞
                                    if (_pc.isAutoSkill_13()) {
                                        if (_pc.isSkillMastery(DOUBLE_BREAK) && !_pc.hasSkillEffect(DOUBLE_BREAK)) {
                                            SkillScroll.DoMySkill1(_pc, DOUBLE_BREAK);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動力量提升
                                    if (_pc.isAutoSkill_15()) {
                                        if (_pc.isSkillMastery(DRESS_MIGHTY) && !_pc.hasSkillEffect(DRESS_MIGHTY) && !_pc.hasSkillEffect(PHYSICAL_ENCHANT_STR)) {
                                            SkillScroll.DoMySkill1(_pc, DRESS_MIGHTY);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動敏捷提升
                                    if (_pc.isAutoSkill_16()) {
                                        if (_pc.isSkillMastery(DRESS_DEXTERITY) && !_pc.hasSkillEffect(DRESS_DEXTERITY) && !_pc.hasSkillEffect(PHYSICAL_ENCHANT_DEX)) {
                                            SkillScroll.DoMySkill1(_pc, DRESS_DEXTERITY);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動閃避提升
                                    if (_pc.isAutoSkill_17()) {
                                        if (_pc.isSkillMastery(DRESS_EVASION) && !_pc.hasSkillEffect(DRESS_EVASION)) {
                                            SkillScroll.DoMySkill1(_pc, DRESS_EVASION);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動暗影閃避
                                    if (_pc.isAutoSkill_18()) {
                                        if (_pc.isSkillMastery(UNCANNY_DODGE) && !_pc.hasSkillEffect(UNCANNY_DODGE)) {
                                            SkillScroll.DoMySkill1(_pc, UNCANNY_DODGE);
                                            TimeUnit.MILLISECONDS.sleep(sleep);
                                        }
                                    }
                                    // 自動暗影之牙
                                    if (_pc.isAutoSkill_19()) {
                                        if (_pc.isSkillMastery(SHADOW_FANG) && _pc.getCurrentMp() > 20 && _pc.getInventory().checkItem(40321) // 二級黑魔石
                                        ) {
                                            L1ItemInstance tgItem = null;
                                            tgItem = _pc.getWeapon();
                                            if (tgItem != null) {
                                                if (!tgItem.isRunning()) {
                                                    final L1SkillUse l1skilluse = new L1SkillUse();
                                                    l1skilluse.handleCommands(_pc, SHADOW_FANG, tgItem.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
                                                    _pc.setCurrentMp(_pc.getCurrentMp() - 20);
                                                    _pc.getInventory().consumeItem(40321, 1);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                        }
                                    }
                                } else
                                    // ■■■■■■■■■■■■■■■■■■■■■■■■ 龍騎 ■■■■■■■■■■■■■■■■■■■■■■■
                                    if (_pc.isDragonKnight()) {// 龍騎
                                        // 自動龍之護鎧
                                        if (_pc.isAutoSkill_10()) {
                                            if (_pc.isSkillMastery(DRAGON_SKIN) && !_pc.hasSkillEffect(DRAGON_SKIN)) {
                                                SkillScroll.DoMySkill1(_pc, DRAGON_SKIN);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動致命身軀
                                        if (_pc.isAutoSkill_11()) {
                                            if (_pc.isSkillMastery(MORTAL_BODY) && !_pc.hasSkillEffect(MORTAL_BODY)) {
                                                SkillScroll.DoMySkill1(_pc, MORTAL_BODY);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動燃燒擊砍
                                        if (_pc.isAutoSkill_12()) {
                                            if (_pc.isSkillMastery(BURNING_SLASH) && !_pc.hasSkillEffect(BURNING_SLASH)) {
                                                SkillScroll.DoMySkill1(_pc, BURNING_SLASH);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動撕裂護甲
                                        if (_pc.isAutoSkill_13()) {
                                            if (_pc.isSkillMastery(DESTROY) && !_pc.hasSkillEffect(DESTROY)) {
                                                SkillScroll.DoMySkill1(_pc, DESTROY);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動血之渴望
                                        if (_pc.isAutoSkill_14()) {
                                            if (_pc.isSkillMastery(BLOODLUST) && !_pc.hasSkillEffect(BLOODLUST) // 沒有血之渴望
                                                    && !_pc.hasSkillEffect(STATUS_RIBRAVE) // 沒有生命之樹果實效果
                                                    && !_pc.hasSkillEffect(STATUS_BRAVE2) // 沒有荒神加速
                                                    && !_pc.hasSkillEffect(STATUS_BRAVE) // 沒有勇敢藥水效果
                                            ) {
                                                SkillScroll.DoMySkill1(_pc, BLOODLUST);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動覺醒：法利昂
                                        if (_pc.isAutoSkill_15()) {
                                            int awakeSkillId = _pc.getAwakeSkillId();
                                            if (_pc.isSkillMastery(AWAKEN_FAFURION) && !_pc.hasSkillEffect(AWAKEN_FAFURION)
                                                    //&& !_pc.hasSkillEffect(AWAKEN_ANTHARAS)
                                                    //&& !_pc.hasSkillEffect(AWAKEN_VALAKAS)
                                                    && awakeSkillId != AWAKEN_ANTHARAS && awakeSkillId != AWAKEN_FAFURION && awakeSkillId != AWAKEN_VALAKAS) {
                                                SkillScroll.DoMySkill1(_pc, AWAKEN_FAFURION);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動覺醒：安塔瑞斯
                                        if (_pc.isAutoSkill_16()) {
                                            int awakeSkillId = _pc.getAwakeSkillId();
                                            if (_pc.isSkillMastery(AWAKEN_ANTHARAS)
                                                    //&& !_pc.hasSkillEffect(AWAKEN_FAFURION)
                                                    && !_pc.hasSkillEffect(AWAKEN_ANTHARAS)
                                                    //&& !_pc.hasSkillEffect(AWAKEN_VALAKAS)
                                                    && awakeSkillId != AWAKEN_ANTHARAS && awakeSkillId != AWAKEN_FAFURION && awakeSkillId != AWAKEN_VALAKAS) {
                                                SkillScroll.DoMySkill1(_pc, AWAKEN_ANTHARAS);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                        // 自動覺醒：巴拉卡斯
                                        if (_pc.isAutoSkill_17()) {
                                            int awakeSkillId = _pc.getAwakeSkillId();
                                            if (_pc.isSkillMastery(AWAKEN_VALAKAS)
                                                    //&& !_pc.hasSkillEffect(AWAKEN_FAFURION)
                                                    //&& !_pc.hasSkillEffect(AWAKEN_ANTHARAS)
                                                    && !_pc.hasSkillEffect(AWAKEN_VALAKAS) && awakeSkillId != AWAKEN_ANTHARAS && awakeSkillId != AWAKEN_FAFURION && awakeSkillId != AWAKEN_VALAKAS) {
                                                SkillScroll.DoMySkill1(_pc, AWAKEN_VALAKAS);
                                                TimeUnit.MILLISECONDS.sleep(sleep);
                                            }
                                        }
                                    } else
                                        // ■■■■■■■■■■■■■■■■■■■■■■■■ 幻術 ■■■■■■■■■■■■■■■■■■■■■■■
                                        if (_pc.isIllusionist()) {// 幻術
                                            // 自動鏡像
                                            if (_pc.isAutoSkill_10()) {
                                                if (_pc.isSkillMastery(MIRROR_IMAGE) && !_pc.hasSkillEffect(MIRROR_IMAGE)) {
                                                    SkillScroll.DoMySkill1(_pc, MIRROR_IMAGE);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動降低負重
                                            if (_pc.isAutoSkill_11()) {
                                                if (_pc.isSkillMastery(JOY_OF_PAIN) && !_pc.hasSkillEffect(JOY_OF_PAIN)) {
                                                    SkillScroll.DoMySkill1(_pc, JOY_OF_PAIN);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動專注
                                            if (_pc.isAutoSkill_12()) {
                                                if (_pc.isSkillMastery(CONCENTRATION) && !_pc.hasSkillEffect(CONCENTRATION)) {
                                                    SkillScroll.DoMySkill1(_pc, CONCENTRATION);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動幻覺:巫妖
                                            if (_pc.isAutoSkill_13()) {
                                                if (_pc.isSkillMastery(ILLUSION_LICH) && !_pc.hasSkillEffect(ILLUSION_LICH)) {
                                                    SkillScroll.DoMySkill1(_pc, ILLUSION_LICH);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動耐力
                                            if (_pc.isAutoSkill_14()) {
                                                if (_pc.isSkillMastery(PATIENCE) && !_pc.hasSkillEffect(PATIENCE)) {
                                                    SkillScroll.DoMySkill1(_pc, PATIENCE);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動幻覺:化身
                                            if (_pc.isAutoSkill_15()) {
                                                if (_pc.isSkillMastery(ILLUSION_AVATAR) && !_pc.hasSkillEffect(ILLUSION_AVATAR)) {
                                                    SkillScroll.DoMySkill1(_pc, ILLUSION_AVATAR);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動洞察
                                            if (_pc.isAutoSkill_16()) {
                                                if (_pc.isSkillMastery(INSIGHT) && !_pc.hasSkillEffect(INSIGHT)) {
                                                    SkillScroll.DoMySkill1(_pc, INSIGHT);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動幻覺:歐吉
                                            if (_pc.isAutoSkill_17()) {
                                                if (_pc.isSkillMastery(ILLUSION_OGRE) && !_pc.hasSkillEffect(ILLUSION_OGRE)) {
                                                    SkillScroll.DoMySkill1(_pc, ILLUSION_OGRE);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動衝突強化
                                            if (_pc.isAutoSkill_18()) {
                                                if (_pc.isSkillMastery(IMPACT) && !_pc.hasSkillEffect(IMPACT)) {
                                                    SkillScroll.DoMySkill1(_pc, IMPACT);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                            // 自動幻覺:鑽石高侖
                                            if (_pc.isAutoSkill_19()) {
                                                if (_pc.isSkillMastery(ILLUSION_DIA_GOLEM) && !_pc.hasSkillEffect(ILLUSION_DIA_GOLEM)) {
                                                    SkillScroll.DoMySkill1(_pc, ILLUSION_DIA_GOLEM);
                                                    TimeUnit.MILLISECONDS.sleep(sleep);
                                                }
                                            }
                                        } else
                                            // ■■■■■■■■■■■■■■■■■■■■■■■■ 戰士 ■■■■■■■■■■■■■■■■■■■■■■■
                                            if (_pc.isWarrior()) {// 戰士
                                                // 自動體能強化
                                                if (_pc.isAutoSkill_10()) {
                                                    if (_pc.isSkillMastery(GIGANTIC) && !_pc.hasSkillEffect(GIGANTIC)) {
                                                        SkillScroll.DoMySkill1(_pc, GIGANTIC);
                                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                                    }
                                                }
                                                // 自動泰坦狂暴
                                                if (_pc.isAutoSkill_11()) {
                                                    if (_pc.isSkillMastery(TITANL_RISING) && !_pc.hasSkillEffect(TITANL_RISING)) {
                                                        SkillScroll.DoMySkill1(_pc, TITANL_RISING);
                                                        TimeUnit.MILLISECONDS.sleep(sleep);
                                                    }
                                                }
                                            }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
