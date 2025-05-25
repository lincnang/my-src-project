package com.lineage.server.model;

import com.add.Tsai.Astrology.AstrologyCmd;
import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1AttrWeapon;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.ASTROLOGY_DG_ER;

public class L1AttackPower {//src016
    private static final Log _log = LogFactory.getLog(L1AttackPower.class);
    private static final Random _random = new Random();
    private final L1PcInstance _pc;
    private final L1Character _target;
    private L1PcInstance _targetPc;
    private L1NpcInstance _targetNpc;
    private final int _weaponAttrEnchantKind;
    private final int _weaponAttrEnchantLevel;

    public L1AttackPower(L1PcInstance attacker, L1Character target, int weaponAttrEnchantKind, int weaponAttrEnchantLevel) {
        _pc = attacker;
        _target = target;
        if (_target instanceof L1NpcInstance) {
            _targetNpc = (L1NpcInstance) _target;
        } else if (_target instanceof L1PcInstance) {
            _targetPc = (L1PcInstance) _target;
        }
        _weaponAttrEnchantKind = weaponAttrEnchantKind;
        _weaponAttrEnchantLevel = weaponAttrEnchantLevel;
    }

    public int set_item_power(int damage) {
        int reset_dmg = damage;
        try {
            if (_weaponAttrEnchantKind > 0) {
                L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(_weaponAttrEnchantKind, _weaponAttrEnchantLevel);
                if (attrWeapon == null) {
                    return damage;
                }
                int Propertyprobability = 0;
                // 增加娃娃屬性卷機率
                if (_pc.getPropertyprobability() != 0) {
                    Propertyprobability = _pc.getPropertyprobability();
                }
                int ReiAttrWeaponChance; // 王族天賦技能屬性之光
                if (_pc.isCrown() && _pc.getReincarnationSkill()[1] > 0) {
                    ReiAttrWeaponChance = _pc.getReincarnationSkill()[1] * 10; // 千分幾率 * 10
                } else {
                    ReiAttrWeaponChance = 0; // 其它0
                }
                //if (attrWeapon.getProbability() + Propertyprobability >= _random.nextInt(1000)) {// 成功機率
                if (attrWeapon.getProbability() + Propertyprobability + ReiAttrWeaponChance >= _random.nextInt(1000)) {// 成功機率
                    if (_targetPc != null && _targetPc.getInventory().consumeItem(44073, 1L)) {
                        _targetPc.sendPackets(new S_SystemMessage("成功抵抗屬性能力的發動"));
                        return damage;
                    }
                    if (attrWeapon.getTypeBind() > 0) {
                        if (!L1WeaponSkill.isFreeze(_target)) {
                            final int time = (int) (attrWeapon.getTypeBind() * 1000);
                            final ServerBasePacket packet = new S_SkillSound(_target.getId(), 4184);
                            _target.broadcastPacketX8(packet);
                            if (_targetPc != null) {
                                _targetPc.sendPackets(packet);
                                _targetPc.setSkillEffect(4000, time);
                                _targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                            } else if (_targetNpc != null) {
                                _targetNpc.setSkillEffect(4000, time);
                                _targetNpc.setParalyzed(true);
                            }
                        }
                    }
                    if (attrWeapon.getTypeDrainHp() > 0) {
                        final int drainHp = 1 + _random.nextInt((int) attrWeapon.getTypeDrainHp());
                        _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                        _pc.setCurrentHp((short) (_pc.getCurrentHp() + drainHp));
                    }
                    if (attrWeapon.getTypeDrainMp() > 0) {
                        final int drainMp = 1 + _random.nextInt(attrWeapon.getTypeDrainMp());
                        _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                        _pc.setCurrentMp((short) (_pc.getCurrentMp() + drainMp));
                        if (_targetPc != null) {
                            _targetPc.setCurrentMp(Math.max(_targetPc.getCurrentMp() - drainMp, 0));
                        } else if (_targetNpc != null) {
                            _targetNpc.setCurrentMp(Math.max(_targetNpc.getCurrentMp() - drainMp, 0));
                        }
                    }
                    if (attrWeapon.getTypeDmgup() > 0.0D) {
                        _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 7752));
                        reset_dmg = (int) (reset_dmg * attrWeapon.getTypeDmgup());
                    }
                    if (attrWeapon.getTypeRange() > 0 && attrWeapon.getTypeRangeDmg() > 0) {
                        _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 7749));
                        int dmg = attrWeapon.getTypeRangeDmg();
                        Iterator<?> localIterator1;
                        if (_targetPc != null) {
                            _targetPc.receiveDamage(_pc, dmg, false, false);
                            localIterator1 = World.get().getVisibleObjects(_targetPc, attrWeapon.getTypeRange()).iterator();
                            while (localIterator1.hasNext()) {
                                L1Object object = (L1Object) localIterator1.next();
                                if (object instanceof L1PcInstance) {
                                    L1PcInstance tgpc = (L1PcInstance) object;
                                    if (!tgpc.isDead()) {
                                        if (tgpc.getId() != _pc.getId()) {
                                            if (tgpc.getClanid() != _pc.getClanid() || tgpc.getClanid() == 0) {
                                                if (!tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
                                                    tgpc.receiveDamage(_pc, dmg, false, false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (_targetNpc != null) {
                            _targetNpc.receiveDamage(_pc, dmg);
                            localIterator1 = World.get().getVisibleObjects(_targetNpc, attrWeapon.getTypeRange()).iterator();
                            while (localIterator1.hasNext()) {
                                L1Object object = (L1Object) localIterator1.next();
                                if (object instanceof L1MonsterInstance) {
                                    L1MonsterInstance tgmob = (L1MonsterInstance) object;
                                    if (!tgmob.isDead()) {
                                        tgmob.receiveDamage(_pc, dmg);
                                    }
                                }
                            }
                        }
                    }
                    if (attrWeapon.getTypeLightDmg() > 0) {
                        int dmg = attrWeapon.getTypeLightDmg();
                        _target.broadcastPacketAll(new S_SkillSound(_target.getId(), 11746));
                        if (_targetPc != null) {
                            _targetPc.sendPackets(new S_SkillSound(_target.getId(), 11746));
                            _targetPc.receiveDamage(_pc, dmg, false, false);
                        } else if (_targetNpc != null) {
                            _targetNpc.receiveDamage(_pc, dmg);
                        }
                    }
                    if (attrWeapon.getTypeSkill1() && attrWeapon.getTypeSkillTime() > 0.0D) {
                        int timeSec = (int) attrWeapon.getTypeSkillTime();
                        if (_targetPc != null && !_target.hasSkillEffect(40)) {
                            _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 10703, timeSec));
                            _targetPc.setSkillEffect(40, timeSec * 1000);
                            if (_targetPc.hasSkillEffect(1012)) {
                                _targetPc.sendPackets(new S_CurseBlind(2));
                            } else {
                                _targetPc.sendPackets(new S_CurseBlind(1));
                            }
                        }
                    }
                    if (attrWeapon.getTypeSkill2() && attrWeapon.getTypeSkillTime() > 0.0D) {
                        int timeSec = (int) attrWeapon.getTypeSkillTime();
                        if (_targetPc != null && !_target.hasSkillEffect(64)) {
                            _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 2177, timeSec));
                            _target.setSkillEffect(64, timeSec * 1000);
                        }
                    }
                    if (attrWeapon.getTypeSkill3() && attrWeapon.getTypeSkillTime() > 0.0D) {
                        if (attrWeapon.getTypePolyList() != null) {
                            int polyId = Integer.parseInt(attrWeapon.getTypePolyList()[_random.nextInt(attrWeapon.getTypePolyList().length)]);
                            if (_targetPc != null) {
                                if (_targetPc.getInventory().checkEquipped(20281) || _targetPc.getInventory().checkEquipped(120281)) {// 裝備變形控制戒指
                                    return reset_dmg;
                                }
                                _targetPc.sendPacketsAll(new S_SkillSound(_target.getId(), 230));
                                L1PolyMorph.doPoly(_targetPc, polyId, (int) attrWeapon.getTypeSkillTime(), 1);
                            } else if (_targetNpc != null) {
                                if (!_targetNpc.getNpcTemplate().is_boss()) {
                                    _target.broadcastPacketAll(new S_SkillSound(_target.getId(), 230));
                                    L1PolyMorph.doPoly(_target, polyId, (int) attrWeapon.getTypeSkillTime(), 1);
                                }
                            }
                        }
                    }
                    if (attrWeapon.getTypeRemoveWeapon() && _targetPc != null && _targetPc.getWeapon() != null) {
                        _targetPc.getInventory().setEquipped(_targetPc.getWeapon(), false, false, false);
                        _targetPc.sendPackets(new S_ServerMessage(1027));
                    }
                    if (attrWeapon.getTypeRemoveDoll() && _targetPc != null && !_targetPc.getDolls().isEmpty()) {
                        Iterator<L1DollInstance> iter = _targetPc.getDolls().values().iterator();
                        if (iter.hasNext()) {
                            L1DollInstance doll = iter.next();
                            doll.deleteDoll();
                        }
                    }
                    // 解除玩家的娃娃 (0:沒效果 1:有效果)
                    if (attrWeapon.getTypeRemoveDoll()) {
                        if (_targetPc != null) {
                            if (!_targetPc.getDolls().isEmpty()) {
                                for (final L1DollInstance doll : _targetPc.getDolls().values()) {
                                    // 6++++ (固定一隻)
                                    doll.deleteDoll();
                                    break;
                                }
                            }
                        }
                    }
                    // 解除玩家的娃娃 (0:沒效果 1:有效果)
                    if (attrWeapon.getTypeRemoveDoll()) {
                        if (_targetPc != null && !_targetPc.getDolls2().isEmpty()) {
                            for (final L1DollInstance2 doll : _targetPc.getDolls2().values()) {
                                // 移除魔法娃娃 (固定一隻)
                                doll.deleteDoll2();
                                break;
                            }
                        }
                    }
                    if (attrWeapon.getTypeRemoveArmor() > 0 && _targetPc != null) {
                        int counter = _random.nextInt(attrWeapon.getTypeRemoveArmor()) + 1;
                        StringBuilder sbr = new StringBuilder();
                        for (L1ItemInstance item : _targetPc.getInventory().getItems()) {
                            if (item.getItem().getType2() != 2 || !item.isEquipped()) {
                                continue;
                            }
                            _targetPc.getInventory().setEquipped(item, false, false, false);
                            sbr.append("[").append(item.getNumberedName(1L, false)).append("]");
                            if (--counter <= 0) {
                                break;
                            }
                        }
                        if (sbr.length() > 0) {
                            _targetPc.sendPackets(new S_SystemMessage("以下裝備被對方卸除:" + sbr.toString()));
                            _pc.sendPackets(new S_SystemMessage("成功卸除對方以下裝備:" + sbr.toString()));
                        }
                    }
                }
            }
            /* 一次只能激活一個技能 簡化代碼
            //TODO 技能1效果整合
            //假設攻擊者為 L1PcInstance，且已經啟用了技能1 (透過 AstrologyCmd.get().isSkill1Active(_pc) 判斷)
            if (_pc != null && AstrologyCmd.get().isSkill1Active(_pc)) {
                // 設定觸發機率，例如30%
                int triggerChance = _random.nextInt(100) + 1;
                if (triggerChance <= 70) {
                    // 將傷害乘以20
                    reset_dmg *= 20;

                    // 發送技能1專屬特效封包，讓攻擊對象呈現特效（技能1效果的特效ID 18846）
                    int skillEffectId = 18846;

                    if (_targetPc != null) {
                        _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), skillEffectId));
                        _targetPc.sendPackets(new S_SystemMessage("你被技能1效果命中，傷害加倍！"));
                    } else if (_targetNpc != null) {
                        _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), skillEffectId));
                    } else {
                        _target.broadcastPacketAll(new S_SkillSound(_target.getId(), skillEffectId));
                    }
                }
            }
            //TODO 技能1效果結束

            //TODO 技能2效果整合
            // 假設攻擊者為 L1PcInstance，且已經啟用了技能2 (透過 AstrologyCmd.get().isSkill2Active(_pc) 判斷)
            if (_pc != null && AstrologyCmd.get().isSkill2Active(_pc)) {
                // 設定觸發機率，例如30%
                int triggerChanceSkill2 = _random.nextInt(100) + 1;
                if (triggerChanceSkill2 <= 70) {
                    // 將傷害乘以30
                    reset_dmg *= 30;

                    // 發送技能2專屬特效封包，讓攻擊對象呈現技能2的特效（假設特效ID 為 18847）
                    int skill2EffectId = 24069;

                    if (_targetPc != null) {
                        _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), skill2EffectId));
                        _targetPc.sendPackets(new S_SystemMessage("你被技能2效果命中，傷害極大提升！"));
                    } else if (_targetNpc != null) {
                        _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), skill2EffectId));
                    } else {
                        _target.broadcastPacketAll(new S_SkillSound(_target.getId(), skill2EffectId));
                    }
                }
            }
            //TODO 技能2效果結合結束
            */
            //TODO 星盤技能激活效果簡化
            if (AstrologyCmd.get().getAstrologySkillActive(_pc) > 0) {
                reset_dmg += getAstrologyDamage(reset_dmg);
            }

        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return reset_dmg;
    }

    /**
     * 使用 skills 資料庫設定簡化守護星盤特效攻擊力計算
     *
     * @param reset_dmg 原始傷害
     * @return 以原始傷害為基數進行倍率計算
     */
    private int getAstrologyDamage(int reset_dmg) {
        int damage = 0;
        final int skillId = AstrologyCmd.get().getAstrologySkillActive(_pc);
        L1Skills skill = SkillsTable.get().getTemplate(skillId);
        if (skill == null) {
            return 0;
        }
        int random = _random.nextInt(1000);// 概率亂數
        int chance = skill.getProbabilityValue();// 概率
        if (random > chance) {
            return 0;
        }

        int castGfx = skill.getCastGfx2();// 出現在自己身上的特效編號
        if (castGfx > 0) {
            _pc.sendPacketsAll(new S_EffectLocation(_pc.getLocation(), castGfx));
        }

        int effectId = skill.getCastGfx();// 出現在目標身上的特效編號
        if (effectId > 0) {
            Optional.ofNullable(_targetNpc).ifPresent(npc -> _targetNpc.broadcastPacketAll(new S_EffectLocation(_targetNpc.getLocation(), effectId)));
            Optional.ofNullable(_targetPc).ifPresent(pc -> _targetPc.sendPacketsAll(new S_EffectLocation(_targetPc.getLocation(), effectId)));
        }

        String skillName = skill.getName();// 技能名稱

        int dg = skill.getDamageDiceCount();// 降低目標近戰回避率 該效果僅對玩家生效
        int er = skill.getProbabilityDice();// 降低目標遠程閃避率 該效果僅對玩家生效
        int buffTime = skill.getBuffDuration();
        if (_targetPc != null && (dg > 0 || er > 0) && buffTime > 0) {
            if (_targetPc.hasSkillEffect(ASTROLOGY_DG_ER)) {
                return 0;
            }
            L1BuffUtil.effect(_targetPc, ASTROLOGY_DG_ER, buffTime, dg, er);
            if (skillName != null) {
                _targetPc.sendPackets(new S_SystemMessage("你遭受到技能:" + skillName + "的攻擊,DG-" + dg * 10 + ",ER-" + er));
            }
            return 0; // 給予目標DeBuff時不會產生傷害
        }

        double dice = skill.getDamageDice() / 100D;// 增加傷害的倍率
        String damageMsg = "";
        if (dice > 0.0D) {
            int skillDamage = skill.getDamageValue();
            damage = Math.max((int) (dice * reset_dmg), skillDamage);// 最低傷害
            damageMsg = ",受到額外傷害 \\f3" + damage;
        }

        if (_targetPc != null && skillName != null) {
            _targetPc.sendPackets(new S_SystemMessage("你遭受到技能:" + skillName + "的攻擊" + damageMsg, 15));
        }

        return damage;
    }
}
