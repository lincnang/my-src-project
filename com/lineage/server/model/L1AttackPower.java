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
import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.ASTROLOGY_DG_ER;

public class L1AttackPower {
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
                // 1. 物理倍率加乘
                double dmgUp = attrWeapon.getTypeDmgup();
                if (dmgUp > 0.0 && dmgUp != 1.0) {
                    reset_dmg = (int) (reset_dmg * dmgUp);
                }
                // 2. 取屬性傷害
                int attrDmg = attrWeapon.getAttrDmg();
                // 3. 先爆擊（對屬性傷害加倍）
                int attrCriticalPro = attrWeapon.getArrtDmgCriticalPro();
                double attrCriticalRate = attrWeapon.getArrtDmgCritical();
                int criticalAttrDmg = attrDmg;
                boolean isCritical = false;
                if (attrCriticalPro > 0 && attrCriticalRate > 1.0) {
                    int rnd = _random.nextInt(1000);
                    if (rnd < attrCriticalPro) {
                        criticalAttrDmg = (int)(attrDmg * attrCriticalRate);
                        isCritical = true;
                        if (_pc != null) {
                            _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 14495));
                        }
                    }
                }
                // 4. 再扣抗性
                int defenderResist = 0;
                if (_targetPc != null) {
                    switch (attrWeapon.getAttrId()) {
                        case 1: defenderResist = _targetPc.getEarth(); break;
                        case 2: defenderResist = _targetPc.getFire(); break;
                        case 4: defenderResist = _targetPc.getWater(); break;
                        case 8: defenderResist = _targetPc.getWind(); break;
                        default: defenderResist = 0;
                    }
                }
                int effectiveAttrDmg = criticalAttrDmg - defenderResist;
                if (effectiveAttrDmg < 0) effectiveAttrDmg = 0;

                int counterAttrResist = 0;
                if (_targetPc != null) {
                    switch (attrWeapon.getAttrId()) {
                        case 1: counterAttrResist = _targetPc.getWater(); break; // 地剋水
                        case 2: counterAttrResist = _targetPc.getWind(); break;  // 火剋風
                        case 4: counterAttrResist = _targetPc.getFire(); break;  // 水剋火
                        case 8: counterAttrResist = _targetPc.getEarth(); break; // 風剋地
                    }
                }
                double counterRate = 1.0 + (counterAttrResist / 10.0) * 0.1;
                effectiveAttrDmg = (int)(effectiveAttrDmg * counterRate);
                reset_dmg = reset_dmg + effectiveAttrDmg;
                // 7. 有屬性傷害且命中機率才播放特效
                if (_pc != null && effectiveAttrDmg > 0) {
                    int attrEffectChance = attrWeapon.getProbability(); // 從資料庫撈
                    if (attrEffectChance <= 0) attrEffectChance = 1000; // 如果沒設，預設100%
                    int rand = _random.nextInt(1000); // 0~999
                    if (rand < attrEffectChance) {
                        int gfxId = attrWeapon.getGfxId();
                        if (gfxId > 0) {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), gfxId));
                        } else {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749)); // 預設屬性特效
                        }
                    }
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

                    // 這裡寫吸血、束縛、光屬傷、變形…等原本要機率觸發的效果
                    if (attrWeapon.getTypeBind() > 0) { //束縛
                        if (!L1WeaponSkill.isFreeze(_target)) {
                            final int time = (int) (attrWeapon.getTypeBind() * 1000);

                            int gfxId = attrWeapon.getGfxId();
                            final ServerBasePacket packet;
                            if (gfxId == 0) {
                                packet = new S_SkillSound(_target.getId(), 4184); // 預設動畫
                            } else {
                                packet = new S_SkillSound(_target.getId(), gfxId); // 讀表動畫
                            }

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
                    if (attrWeapon.getTypeDrainHp() > 0) { //吸血
                        final int drainHp = 1 + _random.nextInt((int) attrWeapon.getTypeDrainHp());
                        int gfxId = attrWeapon.getGfxId();
                        if (gfxId == 0) {
                            // 沒有設定動畫時，只播預設動畫
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                        } else {
                            // 有設定動畫時，只播設定動畫
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), gfxId));
                        }
                        _pc.setCurrentHp((short) (_pc.getCurrentHp() + drainHp));
                    }

                    if (attrWeapon.getTypeDrainMp() > 0) {  //吸魔
                        final int drainMp = 1 + _random.nextInt(attrWeapon.getTypeDrainMp());
                        int gfxId = attrWeapon.getGfxId();
                        if (gfxId == 0) {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                        } else {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), gfxId));
                        }
                        _pc.setCurrentMp((short) (_pc.getCurrentMp() + drainMp));
                        if (_targetPc != null) {
                            _targetPc.setCurrentMp(Math.max(_targetPc.getCurrentMp() - drainMp, 0));
                        } else if (_targetNpc != null) {
                            _targetNpc.setCurrentMp(Math.max(_targetNpc.getCurrentMp() - drainMp, 0));
                        }
                    }

                    if (attrWeapon.getTypeDmgup() > 0.0D) { // 屬性傷害提升
                        int gfxId = attrWeapon.getGfxId();
                        if (gfxId == 0) {
                            _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 7752));
                        } else {
                            _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), gfxId));
                        }
                        reset_dmg = (int) (reset_dmg * attrWeapon.getTypeDmgup());
                    }

                    if (attrWeapon.getTypeRange() > 0 && attrWeapon.getTypeRangeDmg() > 0) { // 遠程攻擊
                        int gfxId = attrWeapon.getGfxId();
                        if (gfxId == 0) {
                            _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 7749));
                        } else {
                            _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), gfxId));
                        }
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

                    if (attrWeapon.getTypeLightDmg() > 0) { // 光屬性傷害
                        int dmg = attrWeapon.getTypeLightDmg();
                        int gfxId = attrWeapon.getGfxId();
                        if (gfxId == 0) {
                            gfxId = 11746;
                        }
                        _target.broadcastPacketAll(new S_SkillSound(_target.getId(), gfxId));
                        if (_targetPc != null) {
                            _targetPc.sendPackets(new S_SkillSound(_target.getId(), gfxId));
                            _targetPc.receiveDamage(_pc, dmg, false, false);
                        } else if (_targetNpc != null) {
                            _targetNpc.receiveDamage(_pc, dmg);
                        }
                    }

                    if (attrWeapon.getTypeSkill1() && attrWeapon.getTypeSkillTime() > 0.0D) { //技能1
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
            //TODO 星盤技能激活效果簡化
            if (AstrologyCmd.get().getAstrologySkillActive(_pc) > 0) {
                reset_dmg += getAstrologyDamage(reset_dmg);
            }

            // 依詩蒂：根據技能編號判斷是範圍傷害技能還是減益狀態技能
            try {
                // 僅使用玩家當前選擇的依詩蒂技能節點；未選擇時不觸發
                Integer btn = com.add.Tsai.Astrology.YishidiAstrologyCmd.get().getYishidiLastBtn(_pc);
                if (btn != null) {
                    com.add.Tsai.Astrology.YishidiAstrologyData sd = com.add.Tsai.Astrology.YishidiAstrologyTable.get().getData(btn);
                    if (sd != null && sd.getSkillId() > 0) {
                        // 判斷是否為範圍傷害技能（有技能範圍的就是範圍傷害技能）
                        if (sd.getSkillRange() > 0) {
                            // 範圍傷害技能邏輯
                            double proc = Math.max(0.0D, Math.min(100.0D, sd.getProcChance()));
                            int gfx = sd.getSkillProcGfxId();
                            int range = sd.getSkillRange();
                            
                            if (proc > 0) {
                                int roll = _random.nextInt(100) + 1;
                                if (roll <= proc) {
                                    // 播放特效在被攻擊對象上
                                    if (gfx > 0) {
                                        if (_targetPc != null) {
                                            _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), gfx));
                                        } else if (_targetNpc != null) {
                                            _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), gfx));
                                        } else {
                                            _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), gfx));
                                        }
                                    }
                                    
                                    // 範圍傷害
                                    int pct = Math.max(0, Math.min(100, sd.getSkillRangeDamagePercent()));
                                    for (L1PcInstance tgpc : World.get().getVisiblePlayer(_pc, range)) {
                                        if (tgpc == null || tgpc.isDead()) continue;
                                        if (tgpc.getId() == _pc.getId()) continue;
                                        try { if (_pc.isInParty() && _pc.getParty().isMember(tgpc)) continue; } catch (Throwable ignore5) {}
                                        try { if (_pc.getClanid() != 0 && _pc.getClanid() == tgpc.getClanid()) continue; } catch (Throwable ignore6) {}
                                        // 安全區過濾：不對安全區內玩家造成傷害
                                        try { if (tgpc.getMap().isSafetyZone(tgpc.getLocation())) continue; } catch (Throwable ignore7) {}
                                        int areaDamage = Math.max(1, (int) Math.floor(reset_dmg * (pct / 100.0)));
                                        tgpc.receiveDamage(_pc, areaDamage, false, true);
                                    }
                                    for (L1Object o : World.get().getVisibleObjects(_pc, range)) {
                                        if (o instanceof L1NpcInstance) {
                                            L1NpcInstance n = (L1NpcInstance) o;
                                            if (n.isDead()) continue;
                                            // 安全區過濾（若地圖定義安全區也不對NPC造成傷害）
                                            try { if (n.getMap().isSafetyZone(n.getLocation())) continue; } catch (Throwable ignore8) {}
                                            n.receiveDamage(_pc, Math.max(1, (int) Math.floor(reset_dmg * (pct / 100.0))));
                                        }
                                    }
                                }
                            }
                        }
                        // 判斷是否為減益狀態技能（有減益狀態觸發機率的就是減益技能）
                        else if (sd.getDebuffProcPercent() > 0) {
                            // 減益狀態技能邏輯（直接使用減益狀態觸發機率，不需要依詩蒂觸發機率）
                            if (_targetPc != null) {
                                int debuffProc = sd.getDebuffProcPercent();
                                int debuffDown = sd.getDebuffDmgDown();
                                int debuffGfx = sd.getDebuffGfxId();
                                int debuffSec = sd.getDebuffDurationSec();
                                int debuffIconId = sd.getDebuffIconId();
                                int debuffStringId = sd.getDebuffStringId();
                                
                                if (debuffProc > 0 && debuffDown > 0 && debuffSec > 0) {
                                    int r2 = _random.nextInt(100) + 1;
                                    if (r2 <= debuffProc) {
                                        // 播放減益特效
                                        if (debuffGfx > 0) {
                                            _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), debuffGfx));
                                        }
                                        // 套用減益狀態（使用自訂的 ICON）
                                        _targetPc.setYishidiDebuff(debuffDown, debuffSec, debuffIconId, debuffStringId);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ignoreY) {}

        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return reset_dmg;
    }
    /**
     * 計算8方向heading，0=左, 2=上, 4=右, 6=下
     */
    private int calcheading(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (dx > 0) {
            if (dy > 0) return 3;  // 右下
            else if (dy < 0) return 5; // 右上
            else return 4; // 右
        } else if (dx < 0) {
            if (dy > 0) return 1; // 左下
            else if (dy < 0) return 7; // 左上
            else return 0; // 左
        } else {
            if (dy > 0) return 2; // 下
            else if (dy < 0) return 6; // 上
            else return -1; // 同點
        }
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
        if (skill == null) return 0;
        int area = skill.getArea();

        int random = _random.nextInt(1000);
        int chance = skill.getProbabilityValue();
        if (random > chance) return 0;

        // 播放自己身上特效（BUFF、附加特效，不影響目標）
        int castGfx = skill.getCastGfx2();
        if (castGfx > 0) {
            _pc.sendPacketsAll(new S_EffectLocation(_pc.getLocation(), castGfx));
        }

        String skillName = skill.getName();
        int dg = skill.getDamageDiceCount();
        int er = skill.getProbabilityDice();
        int buffTime = skill.getBuffDuration();

        // 處理主目標(A)
        int effectId = skill.getCastGfx();
        if (effectId > 0) {
            // 只對主目標顯示特效
            if (_targetPc != null) {
                _targetPc.sendPacketsAll(new S_EffectLocation(_targetPc.getLocation(), effectId));
            }
            if (_targetNpc != null) {
                _targetNpc.broadcastPacketAll(new S_EffectLocation(_targetNpc.getLocation(), effectId));
            }
        }

        // Debuff判斷（DG/ER，只對主目標且是玩家）
        if (_targetPc != null && (dg > 0 || er > 0) && buffTime > 0) {
            if (!_targetPc.hasSkillEffect(ASTROLOGY_DG_ER)) {
                L1BuffUtil.effect(_targetPc, ASTROLOGY_DG_ER, buffTime, dg, er);
                if (skillName != null) {
                    _targetPc.sendPackets(new S_SystemMessage("你遭受到技能:" + skillName + "的攻擊,DG-" + dg * 10 + ",ER-" + er));
                }
            }
            return 0; // Debuff不扣血
        }
        // 計算傷害
        double dice = skill.getDamageDice() / 100D;
        int skillDamage = skill.getDamageValue();
        damage = Math.max((int) (dice * reset_dmg), skillDamage);
        if (_targetPc != null && skillName != null) {
            _targetPc.sendPackets(new S_SystemMessage("你遭受到技能:" + skillName + "的攻擊,受到額外傷害 \\f3" + damage, 15));
        }
        if (_targetPc != null) {
            _targetPc.receiveDamage(_pc, damage, false, false);
        } else if (_targetNpc != null) {
            _targetNpc.receiveDamage(_pc, damage);
        }

        // 範圍副目標，只扣血不播特效
        if (area > 0) {
            for (L1Object obj : World.get().getVisibleObjects(_target, area)) {
                if (!(obj instanceof L1PcInstance || obj instanceof L1NpcInstance)) continue;
                L1Character ch = (L1Character) obj;
                if (ch == _pc) continue; // 不攻擊自己
                if (ch == _targetPc || ch == _targetNpc) continue; // 不重複打主目標
                // 判斷有無隔牆
                int heading = calcheading(ch.getX(), ch.getY(), _target.getX(), _target.getY());
                if (!ch.getMap().isArrowPassable(ch.getLocation(), heading)) continue;

                // 只傷害，不播特效
                int areaDamage = Math.max((int) (dice * reset_dmg), skillDamage);
                if (ch instanceof L1PcInstance && skillName != null) {
                    ((L1PcInstance) ch).sendPackets(new S_SystemMessage("你遭受到技能:" + skillName + "的攻擊,受到額外傷害 \\f3" + areaDamage, 15));
                    ((L1PcInstance) ch).receiveDamage(_pc, areaDamage, false, false);
                } else if (ch instanceof L1NpcInstance) {
                    ((L1NpcInstance) ch).receiveDamage(_pc, areaDamage);
                }
            }
        }
        return 0;
    }
}
