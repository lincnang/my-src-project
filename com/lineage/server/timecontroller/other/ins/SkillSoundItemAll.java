package com.lineage.server.timecontroller.other.ins;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 自動道具
 */
public class SkillSoundItemAll extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundItemAll.class.getName());
    private final L1PcInstance _pc;

    public SkillSoundItemAll(final L1PcInstance pc) {
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
            if (!_pc.getMap().isUsableItem()) {
                return;
            }
            if (!_pc.isAutoItemAll()) {
                return;
            }
            synchronized (this) {
                // 自動解毒藥水
                if (_pc.isAutoItem1()) {
                    if (_pc.hasSkillEffect(STATUS_POISON)// 毒素效果
                            || _pc.hasSkillEffect(STATUS_POISON_SILENCE)// 沉默型中毒
                            || _pc.hasSkillEffect(ENCHANT_VENOM)// 附加劇毒
                        // || _pc.hasSkillEffect(STATUS_CURSE_PARALYZING)// 詛咒型麻痺效果
                        // || _pc.hasSkillEffect(STATUS_CURSE_PARALYZED)// 詛咒型麻痺效果開始
                    ) {
                        if (L1BuffUtil.stopPotion(_pc)) {
                            if (_pc.getInventory().checkItem(40017)) { // 解毒藥水
                                _pc.getInventory().consumeItem(40017, 1);
                                L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 192)); // 送出解毒特效
                                _pc.curePoison();
                            }
                        }
                    }
                }
                // 自動藍色藥水
                if (_pc.isAutoItem2()) {
                    if (!_pc.hasSkillEffect(STATUS_BLUE_POTION)) {
                        if (L1BuffUtil.stopPotion(_pc)) {
                            if (_pc.getInventory().checkItem(40015)) { // 加速魔力回復藥水
                                _pc.getInventory().consumeItem(40015, 1);
                                _pc.setSkillEffect(STATUS_BLUE_POTION, 600 * 1000);
                                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 11815));// 特效
                                L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                            }
                        }
                    }
                }
                // 自動慎重藥水
                if (_pc.isAutoItem3()) {
                    if (!_pc.hasSkillEffect(STATUS_WISDOM_POTION) && _pc.isWizard()) {// 慎重藥水效果
                        if (L1BuffUtil.stopPotion(_pc)) {
                            if (_pc.getInventory().checkItem(40016)) { // 慎重藥水
                                _pc.getInventory().consumeItem(40016, 1);
                                _pc.addSp(2);
                                _pc.addMpr(2);
                                _pc.setSkillEffect(STATUS_WISDOM_POTION, 300 * 1000);
                                _pc.sendPackets(new S_PacketBoxWisdomPotion(300));
                                _pc.sendPackets(new S_SPMR(_pc));// 更新魔攻
                                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 9275));// 特效
                                L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                            }
                        }
                    }
                }
                // 自動綠色藥水
                if (_pc.isAutoItem4()) {
                    if (_pc.getHasteItemEquipped() <= 0) { // 裝備有加速能力裝備(裝備數量)
                        if (!_pc.hasSkillEffect(STATUS_HASTE) // 加速藥水
                                && !_pc.hasSkillEffect(HASTE)// 加速術
                                && !_pc.hasSkillEffect(GREATER_HASTE)// 強力加速術
                        ) {
                            if (L1BuffUtil.stopPotion(_pc)) {
                                if (_pc.getInventory().checkItem(40013)) { // 自我加速藥水
                                    // 解除各種被施加的減速魔法技能
                                    if (_pc.hasSkillEffect(SLOW)) {
                                        _pc.killSkillEffectTimer(SLOW);
                                        _pc.sendPacketsAll(new S_SkillHaste(_pc.getId(), 0, 0));
                                        _pc.getInventory().consumeItem(40013, 1);
                                        //*									} else if (_pc.hasSkillEffect(MASS_SLOW)) {
                                        //*										_pc.killSkillEffectTimer(MASS_SLOW);
                                        //*										_pc.sendPacketsAll(new S_SkillHaste(_pc.getId(), 0, 0));
                                        //*										_pc.getInventory().consumeItem(40013, 1);
                                        //*									} else if (_pc.hasSkillEffect(ENTANGLE)) {
                                        //*										_pc.killSkillEffectTimer(ENTANGLE);
                                        //*										_pc.sendPacketsAll(new S_SkillHaste(_pc.getId(), 0, 0));
                                        //*										_pc.getInventory().consumeItem(40013, 1);
                                    } else {
                                        _pc.getInventory().consumeItem(40013, 1);
                                        _pc.sendPackets(new S_SkillHaste(_pc.getId(), 1, 300));
                                        _pc.setSkillEffect(STATUS_HASTE, 300 * 1000);
                                        _pc.setDrink(false);// 解除醉酒狀態
                                        _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 191));
                                        _pc.broadcastPacketAll(new S_SkillHaste(_pc.getId(), 1, 0));
                                        _pc.setMoveSpeed(1);
                                        L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                                    }
                                } else if (_pc.getInventory().checkItem(40018)) { // 強化綠色藥水
                                    // 解除各種被施加的減速魔法技能
                                    if (_pc.hasSkillEffect(SLOW)) {
                                        _pc.killSkillEffectTimer(SLOW);
                                        _pc.sendPacketsAll(new S_SkillHaste(_pc.getId(), 0, 0));
                                        _pc.getInventory().consumeItem(40018, 1);
                                        //*									} else if (_pc.hasSkillEffect(MASS_SLOW)) {
                                        //*										_pc.killSkillEffectTimer(MASS_SLOW);
                                        //*										_pc.sendPacketsAll(new S_SkillHaste(_pc.getId(), 0, 0));
                                        //*										_pc.getInventory().consumeItem(40018, 1);
                                    } else if (_pc.hasSkillEffect(ENTANGLE)) {
                                        _pc.killSkillEffectTimer(ENTANGLE);
                                        _pc.sendPacketsAll(new S_SkillHaste(_pc.getId(), 0, 0));
                                        _pc.getInventory().consumeItem(40018, 1);
                                    } else {
                                        _pc.getInventory().consumeItem(40018, 1);
                                        _pc.sendPackets(new S_SkillHaste(_pc.getId(), 1, 1800));
                                        _pc.setSkillEffect(STATUS_HASTE, 1800 * 1000);
                                        _pc.setDrink(false);// 解除醉酒狀態
                                        _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 191));
                                        _pc.broadcastPacketAll(new S_SkillHaste(_pc.getId(), 1, 0));
                                        _pc.setMoveSpeed(1);
                                        L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                                    }
                                }
                            }
                        }
                    }
                }
                // 自動名譽貨幣
                if (_pc.isAutoItem5()) {
                    if (!_pc.hasSkillEffect(BLOODLUST) // 沒有血之渴望
                            && !_pc.hasSkillEffect(STATUS_RIBRAVE) // 沒有生命之樹果實效果
                            && !_pc.hasSkillEffect(HOLY_WALK) // 沒有神聖疾走
                            && !_pc.hasSkillEffect(MOVING_ACCELERATION) // 沒有行走加速
                            && !_pc.hasSkillEffect(WIND_WALK) // 沒有風之疾走
                            && !_pc.hasSkillEffect(STATUS_BRAVE2) // 沒有荒神加速
                            && !_pc.hasSkillEffect(STATUS_BRAVE) // 沒有勇敢藥水效果
                            && !_pc.hasSkillEffect(STATUS_ELFBRAVE) // 沒有精靈餅乾效果
                            && !_pc.hasSkillEffect(FIRE_BLESS) // 沒有舞躍之火
                    ) {
                        if (L1BuffUtil.stopPotion(_pc)) {
                            if (_pc.getInventory().checkItem(40733)) { // 名譽貨幣
                                L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                                _pc.getInventory().consumeItem(40733, 1);
                                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 751));
                                _pc.sendPackets(new S_SkillBrave(_pc.getId(), 1, 600));
                                _pc.broadcastPacketAll(new S_SkillBrave(_pc.getId(), 1, 0));
                                _pc.setSkillEffect(STATUS_BRAVE, 600 * 1000);
                                _pc.setBraveSpeed(1);
                            }
                        }
                    }
                }
                // 自動巧克力蛋糕
                if (_pc.isAutoItem6()) {
                    if (!_pc.hasSkillEffect(STATUS_BRAVE3)) {
                        if (L1BuffUtil.stopPotion(_pc)) {
                            if (_pc.getInventory().checkItem(49138)) { // 巧克力蛋糕
                                L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                                _pc.getInventory().consumeItem(49138, 1);
                                _pc.sendPacketsAll(new S_Liquor(_pc.getId(), 0x08));// 巧克力蛋糕效果(速度增加1.15)
                                _pc.sendPackets(new S_ServerMessage(1065));// 將發生神秘的奇跡力量。
                                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 751));
                                _pc.setSkillEffect(STATUS_BRAVE3, 600 * 1000);
                            }
                        }
                    }
                }
                // 自動變形卷軸
                if (_pc.isAutoItem7()) {
                    if (!_pc.hasSkillEffect(SHAPE_CHANGE)) {
                        int awakeSkillId = _pc.getAwakeSkillId();
                        if (awakeSkillId != AWAKEN_ANTHARAS && awakeSkillId != AWAKEN_FAFURION && awakeSkillId != AWAKEN_VALAKAS) {
                            if (_pc.getInventory().checkItem(40088)) { // 變形卷軸
                                _pc.getInventory().consumeItem(40088, 1);
                                int polyid = 12702; // 真 死亡騎士
                                if (_pc.isCrown()) {// 王族
                                    polyid = 11392; // 白金騎士
                                } else if (_pc.isKnight()) {// 騎士
                                    polyid = 11392; // 白金騎士
                                } else if (_pc.isElf()) {// 精靈
                                    if (_pc.getWeapon() != null) {
                                        if (_pc.getWeapon().getItem().getRange() == -1) {
                                            polyid = 11394; // 白金巡守
                                        } else {
                                            polyid = 11392; // 白金騎士
                                        }
                                    } else {
                                        polyid = 11392; // 白金騎士
                                    }
                                } else if (_pc.isWizard()) {// 法師
                                    polyid = 11395; // 白金法師
                                } else if (_pc.isDarkelf()) {// 黑妖
                                    polyid = 11393; // 白金刺客
                                } else if (_pc.isDragonKnight()) {// 龍騎
                                    polyid = 11392; // 白金騎士
                                } else if (_pc.isIllusionist()) {// 幻術
                                    polyid = 11392; // 白金騎士
                                } else if (_pc.isWarrior()) {// 戰士
                                    polyid = 11392; // 白金騎士
                                }
                                L1PolyMorph.doPoly(_pc, polyid, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                            }
                        }
                    }
                }
                // 自動聖結界卷軸
                if (_pc.isAutoItem8()) {
                    if (!_pc.hasSkillEffect(IMMUNE_TO_HARM)) {
                        if (_pc.getInventory().checkItem(44166)) { // 魔法卷軸 (聖結界)
                            _pc.getInventory().consumeItem(44166, 1);
                            L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                            final L1SkillUse l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(_pc, IMMUNE_TO_HARM, _pc.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
                        }
                    }
                }
                // 自動魔法娃娃召喚
                //if (_pc.isAutoItem9()) {
                //}
                // 自動一段經驗藥水 目前只設150%跟200%的
                if (_pc.isAutoItem10()) {
                    if (!_pc.hasSkillEffect(EXP13) && !_pc.hasSkillEffect(EXP15) && !_pc.hasSkillEffect(EXP17) && !_pc.hasSkillEffect(EXP20) && !_pc.hasSkillEffect(EXP25) && !_pc.hasSkillEffect(EXP30) && !_pc.hasSkillEffect(EXP35) && !_pc.hasSkillEffect(EXP40) && !_pc.hasSkillEffect(EXP45) && !_pc.hasSkillEffect(EXP50) && !_pc.hasSkillEffect(EXP55) && !_pc.hasSkillEffect(EXP60) && !_pc.hasSkillEffect(EXP65) && !_pc.hasSkillEffect(EXP70) && !_pc.hasSkillEffect(EXP75) && !_pc.hasSkillEffect(EXP80) && !_pc.hasSkillEffect(EXP85) && !_pc.hasSkillEffect(EXP90) && !_pc.hasSkillEffect(EXP95) && !_pc.hasSkillEffect(EXP100)) {
                        if (_pc.getInventory().checkItem(44104)) { // 經驗加倍藥水200%
                            _pc.getInventory().consumeItem(44104, 1);
                            _pc.setSkillEffect(EXP13, 600 * 1000);
                            _pc.sendPackets(new S_ServerMessage("第一段 經驗值提升200%(" + 600 + "秒)"));
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), 750));
                            _pc.sendPackets(new S_PacketBoxCooking(_pc, 32, 600));// 狩獵的經驗職將會增加
                        } else if (_pc.getInventory().checkItem(44102)) { // 經驗加倍藥水150%
                            _pc.getInventory().consumeItem(44102, 1);
                            _pc.setSkillEffect(EXP20, 600 * 1000);
                            _pc.sendPackets(new S_ServerMessage("第一段 經驗值提升150%(" + 600 + "秒)"));
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), 750));
                            _pc.sendPackets(new S_PacketBoxCooking(_pc, 32, 600));// 狩獵的經驗職將會增加
                        }
                    }
                }
                // 自動二段經驗藥水
                if (_pc.isAutoItem11()) {
                    if (!_pc.hasSkillEffect(SEXP13) && !_pc.hasSkillEffect(SEXP30) && !_pc.hasSkillEffect(SEXP150) && !_pc.hasSkillEffect(SEXP175) && !_pc.hasSkillEffect(SEXP200) && !_pc.hasSkillEffect(SEXP225) && !_pc.hasSkillEffect(SEXP250) && !_pc.hasSkillEffect(SEXP500)) {
                        if (_pc.getInventory().checkItem(641371)) { // 【2段】200%經驗加倍水
                            _pc.getInventory().consumeItem(641371, 1);
                            _pc.setSkillEffect(SEXP200, 600 * 1000);
                            _pc.sendPackets(new S_ServerMessage("第二段 經驗值提升200%(" + 600 + "秒)"));
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), 9714));
                            _pc.sendPackets(new S_InventoryIcon(3069, true, 1292, 600));
                        } else if (_pc.getInventory().checkItem(641369)) { // 【2段】150%經驗加倍水
                            _pc.getInventory().consumeItem(641369, 1);
                            _pc.setSkillEffect(SEXP150, 600 * 1000);
                            _pc.sendPackets(new S_ServerMessage("第二段 經驗值提升150%(" + 600 + "秒)"));
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), 9714));
                            _pc.sendPackets(new S_InventoryIcon(3069, true, 1292, 600));
                        }
                    }
                }
                // 飽食度瞬滿(5萬金幣)
                if (_pc.isAutoItem12()) {
                    if (_pc.get_food() <= 40) {
                        if (_pc.getInventory().checkItem(40308, 50000)) { // 金幣
                            _pc.getInventory().consumeItem(40308, 50000);
                            _pc.set_food(255);
                            _pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, (short) _pc.get_food()));
                        }
                    }
                }
                // 自動魔法卷軸(魔法屏障)
                if (_pc.isAutoItem13()) {
                    if (!_pc.hasSkillEffect(COUNTER_MAGIC)) {
                        if (_pc.getInventory().checkItem(40889)) { // 魔法卷軸(魔法屏障)
                            _pc.getInventory().consumeItem(40889, 1);
                            L1BuffUtil.cancelAbsoluteBarrier(_pc);// 解除魔法技能絕對屏障
                            final L1SkillUse l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(_pc, COUNTER_MAGIC, _pc.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
                        }
                    }
                }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
