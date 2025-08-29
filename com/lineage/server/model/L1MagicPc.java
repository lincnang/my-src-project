package com.lineage.server.model;
//import static com.lineage.server.model.skill.L1SkillId.HEAL; /* 初級治愈術

import com.lineage.config.*;
import com.lineage.data.event.SubItemSet;
import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1SkillEnhance;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.templates.L1SystemMessage;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.RandomArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ConcurrentModificationException;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1MagicPc extends L1MagicMode {
    private static final Log _log = LogFactory.getLog(L1MagicPc.class);
    protected L1ItemInstance _weapon = null;

    public L1MagicPc(L1PcInstance attacker, L1Character target) {
        if (attacker == null) {
            return;
        }
        _pc = attacker;
        if ((target instanceof L1PcInstance)) {
            _calcType = PC_PC;
            _targetPc = ((L1PcInstance) target);
        } else {
            _calcType = PC_NPC;
            _targetNpc = ((L1NpcInstance) target);
        }
    }

    private int getMagicLevel() {
        return _pc.getMagicLevel();
    }

    private int getMagicBonus() {
        return _pc.getMagicBonus();
    }

    private int getLawful() {
        return _pc.getLawful();
    }

    public boolean calcProbabilityMagic(int skillId) {
        int probability;
        boolean isSuccess;
        switch (_calcType) {
            case PC_PC:
                if ((skillId == CANCELLATION) && // 相消
                        (_pc != null) && (_targetPc != null)) {
                    if (_pc.getId() == _targetPc.getId()) {// 消自己
                        return true;
                    }
                    // 相同血盟100%成功
                    if (_pc.getClanid() > 0 && _pc.getMapId() != 10502) { // 底比斯大戰地圖例外
                        if (_pc.getClanid() == _targetPc.getClanid()) {
                            if (_pc.get_redbluejoin() == 0) {//判斷陣營戰同盟之間
                                return true;
                            }
                        }
                    }
                    /*
                     * if ((_pc.isInParty()) &&
                     * (_pc.getParty().isMember(_targetPc))) {//消隊友 return true; }
                     */
                }
                if (skillId == SHAPE_CHANGE) {// 變形術
                    if (_pc.getId() == _targetPc.getId()) {// 變自己
                        return true;
                    }
                    if ((_pc.getClanid() > 0) && (_pc.getClanid() == _targetPc.getClanid())) {// 變盟友
                        return true;
                    }
                }
                if (skillId == PHYSICAL_ENCHANT_INT_DEX_STR) {
                    return true;
                }
                /*
                 * if (_pc.isGm()) { return true; }
                 */
                if (!checkZone(skillId)) {
                    return false;
                }
                /*
                if (_targetPc.hasSkillEffect(ICE_LANCE)) {
                    if (skillId != CANCELLATION) {
                        return false;
                    }
                }
                */
                if (_targetPc.hasSkillEffect(EARTH_BIND)) {
                    if (skillId != CANCELLATION) {
                        return false;
                    }
                }
                if (calcEvasion()) {
                    return false;
                }
                break;
            case PC_NPC:
                /*
                 * if (_pc.isGm()) { return true; }
                 */
                if (_targetNpc != null) {
                    if ((_targetNpc instanceof L1DeInstance)) {
                        if (!checkZoneDE(skillId)) {
                            return false;
                        }
                    }
                    int gfxid = _targetNpc.getNpcTemplate().get_gfxid();
                    switch (gfxid) {
                        case 2412:
                            if (!_pc.getInventory().checkEquipped(20046)) {
                                return false;
                            }
                            break;
                    }
                    int npcId = _targetNpc.getNpcTemplate().get_npcId();
                    Integer tgskill = L1AttackList.SKNPC.get(npcId);
                    if ((tgskill != null) && (!_pc.hasSkillEffect(tgskill))) {
                        return false;
                    }
                    Integer tgpoly = L1AttackList.PLNPC.get(npcId);
                    if ((tgpoly != null) && (tgpoly.equals(_pc.getTempCharGfx()))) {
                        return false;
                    }
                    boolean dgskill = L1AttackList.DNNPC.containsKey(npcId);
                    if (dgskill) {
                        Integer[] dgskillids = L1AttackList.DNNPC.get(npcId);
                        for (Integer dgskillid : dgskillids) {
                            if (dgskillid.equals(skillId)) {
                                return false;
                            }
                        }
                    }
                }
                if (skillId == CANCELLATION) {
                    return true;
                }
                if (_targetNpc != null) {
                    if (skillId == SHAPE_CHANGE) {// 變形術
                        if (_targetNpc.getLevel() >= 50) {// 怪物等級大於50級
                            return false;
                        }
                    }
                /*
                if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
                    if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
                        return false;
                    }
                }
                */
                    if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
                        if (skillId != WEAPON_BREAK) {
                            return false;
                        }
                    }
                }
                break;
        }
        probability = calcProbability(skillId);// 命中機率計算
        int rnd = _random.nextInt(100) + 1;
        probability = Math.min(probability, 100);
        probability = Math.max(probability, 0);
        if (probability >= rnd) {
            isSuccess = true;
            if (this._pc.isGm())
                this._pc.sendPackets((ServerBasePacket) new S_SystemMessage("玩家發動:" + probability + "大於系統發動值" + rnd + "觸發"));
        } else {
            isSuccess = false;
            if (this._pc.isGm())
                this._pc.sendPackets((ServerBasePacket) new S_SystemMessage("玩家發動:" + probability + "小於系統發動值" + rnd + "失敗"));
        }
        if (!ConfigAlt.ALT_ATKMSG) {
            return isSuccess;
        }
        switch (_calcType) {
            case 1:
                if ((!_pc.isGm()) && (!_targetPc.isGm())) {
                    return isSuccess;
                }
                break;
            case 2:
                if (!_pc.isGm()) {
                    return isSuccess;
                }
                break;
        }
        switch (_calcType) {
            case PC_PC:
                if (_pc.isGm()) {
                    StringBuilder atkMsg = new StringBuilder();
                    atkMsg.append("對PC送出技能: ");
                    atkMsg.append(_pc.getName()).append(">");
                    atkMsg.append(_targetPc.getName()).append(" ");
                    atkMsg.append(isSuccess ? "成功" : "失敗");
                    atkMsg.append(" 成功機率:").append(probability).append("%");
                    _pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
                }
                if (_targetPc.isGm()) {
                    StringBuilder atkMsg = new StringBuilder();
                    atkMsg.append("受到PC技能: ");
                    atkMsg.append(_pc.getName()).append(">");
                    atkMsg.append(_targetPc.getName()).append(" ");
                    atkMsg.append(isSuccess ? "成功" : "失敗");
                    atkMsg.append(" 成功機率:").append(probability).append("%");
                    _targetPc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
                }
                break;
            case PC_NPC:
                if (_pc.isGm()) {
                    StringBuilder atkMsg = new StringBuilder();
                    atkMsg.append("對NPC送出技能: ");
                    atkMsg.append(_pc.getName()).append(">");
                    atkMsg.append(_targetNpc.getName()).append(" ");
                    atkMsg.append(isSuccess ? "成功" : "失敗");
                    atkMsg.append(" 成功機率:").append(probability).append("%");
                    _pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
                }
                break;
        }
        return isSuccess;
    }

    private boolean checkZone(int skillId) {
        if ((_pc != null) && (_targetPc != null)) {
            if ((_pc.isSafetyZone()) || (_targetPc.isSafetyZone())) {
                Boolean isBoolean = L1AttackList.NZONE.get(skillId);
                if (isBoolean != null) {
                    _pc.sendPackets(new S_ServerMessage("在安全區域無法使用此技能。"));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkZoneDE(int skillId) {
        if ((_pc != null) && (_targetNpc != null)) {
            if ((_pc.isSafetyZone()) || (_targetNpc.isSafetyZone())) {
                Boolean isBoolean = L1AttackList.NZONE.get(skillId);
                if (isBoolean != null) {
                    _pc.sendPackets(new S_ServerMessage("在安全區域無法使用此技能。"));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 命中機率計算
     *
     */
    private int calcProbability(int skillId) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        int attackLevel = _pc.getLevel();
        int defenseLevel = 0;
        int probability = 0;
        switch (_calcType) {
            case PC_PC:
                if (_targetPc.isGm()) {
                    return -1;
                }
                if (_targetPc.getId() == _pc.getId()) {
                    return -1;
                }
                defenseLevel = _targetPc.getLevel();
                break;
            case PC_NPC:
                defenseLevel = _targetNpc.getLevel();
                if ((skillId == RETURN_TO_NATURE) && ((_targetNpc instanceof L1SummonInstance))) {
                    L1SummonInstance summon = (L1SummonInstance) _targetNpc;
                    defenseLevel = summon.getMaster().getLevel();
                }
                break;
        }
        switch (skillId) {
            case 133:// 弱化屬性
            case 145:// 釋放元素
            case 152:// 地面障礙
            case 167:// 風之枷鎖
            case 157:// 大地屏障
            case 161:// 封印禁地
            case 173:// 污濁之水
                probability = (int) (l1skills.getProbabilityDice() / 10.0D * (attackLevel - defenseLevel));
                probability += l1skills.getProbabilityValue();
                probability -= getTargetMr() / 10;
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                break;
            case 174:// 精準射擊
                // 成功確率 魔法固有係數 × LV差 + 基本確率
                if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
                    probability = ConfigSkillElf.Precision1;
                } else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
                    probability = ConfigSkillElf.Precision2;
                } else { // 攻擊者等級大於等於被攻擊者
                    probability = ConfigSkillElf.Precision3;
                }
                break;
            case THUNDER_GRAB:// 奪命之雷
                if (attackLevel > defenseLevel) {// 攻擊方等級大於防禦方
                    probability = ConfigSkillDragon.SLAY_BREAK_1;
                } else if (attackLevel == defenseLevel) {// 攻擊方等級相等防禦方
                    probability = ConfigSkillDragon.SLAY_BREAK_2;
                } else if (attackLevel < defenseLevel) {// 攻擊方等級小於防禦方
                    probability = ConfigSkillDragon.SLAY_BREAK_3;
                }
            case 153:// 魔法消除
                probability = (int) (l1skills.getProbabilityDice() / 10.0D * (attackLevel - defenseLevel));
                if (attackLevel - defenseLevel >= 5) {
                    probability += l1skills.getProbabilityValue();
                } else {
                    probability += (l1skills.getProbabilityValue() / 2);
                }
                if (_calcType == PC_NPC) {
                    probability -= getTargetMr();
                } else {
                    probability -= getTargetMr() / 30;
                }
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                if (probability > 50) {
                    probability = 50;
                }
                break;
            case SHOCK_STUN:// 衝擊之暈
            case EMPIRE:// 帝國
            case KINGDOM_STUN:// 王者之劍
                if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
                    probability = ConfigSkillKnight.ImpactHalo1;// SRC 20
                } else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
                    probability = ConfigSkillKnight.ImpactHalo2;// SRC NO
                } else {// 攻擊者等級大於等於被攻擊者
                    probability = ConfigSkillKnight.ImpactHalo3;// SRC 80
                }
                if (_pc.isKnight() && _pc.getReincarnationSkill()[0] > 0) { // 騎士天賦技能強化衝暈
                    probability += _pc.getReincarnationSkill()[0];
                }
                probability += _pc.getImpactUp(); // 幻術師新技能 衝突強化
                break;
            case 91:// 反擊屏障
                // 成功機率 基本確率 + LV差1每 +-1%
                //probability = l1skills.getProbabilityValue() + attackLevel
                //		- defenseLevel;
                probability = ConfigSkillKnight.BARRIERchance;
                // 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
                probability += (_pc.getOriginalMagicHit() << 1);
                if (_pc.getMeteLevel() >= 4) {  //SRC0808
                    probability += ConfigSkillKnight.K4;
                }
                break;
            case COUNTER_BARRIER_VETERAN:// 反擊屏障:精通
                int AddProbability = 0;
                int Lv = this._pc.getLevel();
                if (_pc.isKnight() && _pc.isCOUNTER_BARRIER_VETERAN()) {
                    if (Lv >= 85) {
                        AddProbability = 1;
                    } else if (Lv == 86) {
                        AddProbability = 2;
                    } else if (Lv == 87) {
                        AddProbability = 3;
                    } else if (Lv == 88) {
                        AddProbability = 4;
                    } else if (Lv == 89) {
                        AddProbability = 5;
                    } else if (Lv == 90) {
                        AddProbability = 6;
                    } else if (Lv == 91) {
                        AddProbability = 7;
                    } else if (Lv == 92) {
                        AddProbability = 8;
                    } else if (Lv == 93) {
                        AddProbability = 9;
                    } else if (Lv == 94) {
                        AddProbability = 10;
                    } else if (Lv == 95) {
                        AddProbability = 11;
                    } else if (Lv == 96) {
                        AddProbability = 12;
                    } else if (Lv == 97) {
                        AddProbability = 13;
                    } else if (Lv == 98) {
                        AddProbability = 14;
                    } else if (Lv == 99) {
                        AddProbability = 15;
                    }
                }
                // 成功機率 基本確率 + LV差1毎 +-1%
                probability = ConfigSkillKnight.BARRIERchance + AddProbability;
                break;
            case 103:// 暗黑盲咒
            case 112:// 破壞盔甲
                int doll = 0;
                if (_pc.hasSkillEffect(5221)) {
                    doll = 10;
                }
                // 成功確率 魔法固有係數 × LV差 + 基本確率
                if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
                    probability = ConfigSkillDarkElf.Damage1 + doll;// SRC 20
                } else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
                    probability = ConfigSkillDarkElf.Damage2 + doll;// SRC NO
                } else { // 攻擊者等級大於等於被攻擊者
                    probability = ConfigSkillDarkElf.Damage3 + doll;// SRC 80
                }
                break;
            case 202:// 混亂
            case 212:// 幻想
            case 217:// 恐慌
                probability = Random.nextInt(11) + 20;
                probability += (attackLevel - defenseLevel) * 2;
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                break;
            case BONE_BREAK://骷髏毀壞
                probability = Random.nextInt(11) + 20;
                int stunlevel2 = attackLevel + _pc.getStunLevel();// 人物昏迷等級
                probability += (stunlevel2 - defenseLevel) * 2;
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                if (_pc.isIllusionist() && _pc.getReincarnationSkill()[0] > 0) { // 幻術天賦技能毀壞之身
                    probability += _pc.getReincarnationSkill()[0];
                }
                probability += _pc.getImpactUp(); // 幻術師新技能 衝突強化
                break;
            case 183:// 護衛毀滅
            case 188:// 恐懼無助
            case 193:// 驚悚死神
                probability = (int) (l1skills.getProbabilityDice() / 10.0D * (attackLevel - defenseLevel));
                probability += l1skills.getProbabilityValue();
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                break;
            case 67:// 變形術
                // probability = 3 * (attackLevel - defenseLevel) + 200 -
                // getTargetMr();
                probability = (attackLevel - defenseLevel) + _pc.getSp() + 110 - getTargetMr();
                break;
            case 228:// 拘束移動
            case 230:// 亡命之徒
                probability = (int) (l1skills.getProbabilityDice() / 10.0D * (attackLevel - defenseLevel));
                probability += l1skills.getProbabilityValue();
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                probability += _pc.getImpactUp(); // 幻術師新技能 衝突強化
                probability += this._pc.get_FearLevel();
                break;
            default:
                int dice2 = l1skills.getProbabilityDice();
                int diceCount2 = 0;
                diceCount2 = getMagicBonus() + getMagicLevel();
                diceCount2 = Math.max(diceCount2, 1);
                for (int i = 0; i < diceCount2; i++) {
                    if (dice2 > 0) {
                        probability += (_random.nextInt(dice2) + 1);
                    }
                }
                probability += (_pc.getOriginalMagicHit());// 魔法命中增加成功機率
                probability -= getTargetMr();
			/*
			 * if (getTargetMr() >= 156) {//魔防超過156免疫法師負面魔法 probability = 0; }
			 * /
			//int Chances = _pc.getInt() - ConfigSkill.IntBigger / ConfigSkill.IntRepeated;
			if (_calcType == PC_PC
					&& (skillId == 27 // 壞物術
					|| skillId == 29  // 緩速術
					|| skillId == 33  // 木乃伊的詛咒
					|| skillId == 36  // 迷魅術
					|| skillId == 39  // 魔力奪取
					|| skillId == 44  // 魔法相消術
					|| skillId == 50  // 冰矛圍籬
					|| skillId == 56  // 疾病術
					|| skillId == 64  // 魔法封印
					|| skillId == 66  // 沉睡之霧
					|| skillId == 71  // 藥水霜化術
					|| skillId == 76  // 集體緩速術
					|| skillId == 202) // 混亂
					&& _pc.getId() != _targetPc.getId()) {
				if (ConfigSkill.AddInt) {
					probability -= getTargetMr() / 3;
				} else {
					probability -= getTargetMr();
					if (probability < 0) {
						probability = 0;
					}
				}
				if (_calcType == PC_PC && probability <= 0 && _pc.getInt() > ConfigSkill.IntBigger) {
					if (ConfigSkill.AddInt) {
						if (_pc.getInt() > ConfigSkill.IntBigger && _pc.getInt() < ConfigSkill.IntLess) {
							//probability += Chances;
							for (int s = ConfigSkill.IntBigger; s < _pc.getInt(); s++) {
								probability += ConfigSkill.IntRepeated;
							}
						}
						if (_pc.getInt() > ConfigSkill.IntBigger && _pc.getInt() >= ConfigSkill.IntLess) {
							probability = 100;
						}
					}
				}
			}*/
                if (skillId == TAMING_MONSTER) {
                    double probabilityRevision = 1;
                    if ((_targetNpc.getMaxHp() / 4) > _targetNpc.getCurrentHp()) {
                        probabilityRevision = 1.3;
                    } else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc.getCurrentHp()) {
                        probabilityRevision = 1.2;
                    } else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc.getCurrentHp()) {
                        probabilityRevision = 1.1;
                    }
                    probability *= probabilityRevision;
                }
                break;
        }
        // 負面技能幾率設置DB化
        if (_calcType == PC_PC && _pc.getId() != _targetPc.getId()) {
            if (l1skills.getProbability_Lv1() != 0 && l1skills.getProbability_Lv2() != 0 && l1skills.getProbability_Lv3() != 0) {
                if (attackLevel < defenseLevel) {
                    probability = l1skills.getProbability_Lv1(); // 攻擊者等級小於被攻擊者
                } else if (attackLevel == defenseLevel) {
                    probability = l1skills.getProbability_Lv2(); // 攻擊者等級等於被攻擊者
                } else {
                    probability = l1skills.getProbability_Lv3(); // 攻擊者等級大於等於被攻擊者
                }
            }
            if (l1skills.getProbability_Mr() != 0) {
                probability -= getTargetMr() / l1skills.getProbability_Mr(); // 被攻擊者魔防扣幾率百分比
            }
            if (l1skills.get_intel_add_probability() != 0) { // 攻擊者智力加幾率(0=不增加 1=目前智力除以1 10=目前智力除以10)
                int max = l1skills.get_intel_add_probability_max(); // 智力增加幾率上限值 (0=不限 50=智力增加幾率超過50等於50)
                int pc_int = _pc.getInt();
                int pc_intprobability = pc_int / l1skills.get_intel_add_probability();
                if (max != 0 && pc_intprobability > max) {
                    pc_intprobability = max;
                }
                probability += pc_intprobability;
            }
        }
        // --- 新增：從 SkillEnhanceTable 獲取技能強化加成 ---
        // 對於所有可以被「吃書」強化的技能，都應該套用這個邏輯
        // 我們假設 `CharSkillReading.get().getSkillLevel(pc.getId(), skillId)`
        // 能夠正確返回玩家透過吃書得到的「技能吃書等級」
        int playerSkillEnhanceLevel = CharSkillReading.get().getSkillLevel(_pc.getId(), skillId);
        // 只有吃書後才取強化
        L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(skillId, playerSkillEnhanceLevel);
        if (enhanceData != null) {
            // clamp 數值，避免 DB 異常
            int addProb = enhanceData.getSetting1();
            if (addProb < -1000) addProb = -1000; // 下限
            if (addProb > 1000) addProb = 1000;   // 上限
            probability += addProb;
        }

        switch (_calcType) {
            case PC_PC:
                switch (skillId) {
                    case DEATH_HEAL:
                        probability = ConfigSkillWizard.DEATH_HEAL;
                        break;
                    case 157:// 大地屏障
                    case 192:// 奪命之雷
                    case 228:// 拘束移動
                        probability -= _targetPc.getRegistSustain();
                        break;
                    case SHOCK_STUN://衝擊之暈
                    case KINGDOM_STUN://王者之劍
                    case EMPIRE:// 帝國
                    case BONE_BREAK://骷髏毀壞
                        probability -= _targetPc.getRegistStun();
                        if (probability < 10) {
                            probability = 10;
                        }
                        break;
                    case 33: // 木乃伊的詛咒
                        probability -= _targetPc.getRegistStone();
                        break;
                    case 66:  // 沉睡之霧
                    case 103: // 暗黑盲咒
                    case 212: // 幻想
                        probability -= _targetPc.getRegistSleep();
                        break;
                    case 50: // 冰矛圍籬
                    case FREEZING_BLIZZARD: // 寒冰尖刺
                        probability -= _targetPc.getRegistFreeze();
                        break;
                    case 20: // 闇盲咒術
                    case 40: // 黑闇之影
                        probability -= _targetPc.getRegistBlind();
                    case 230:
                        probability -= this._targetPc.getRegistFear();
                }
                break;
        }
        return probability;
    }

    /**
     * 魔法傷害計算
     */
    public int calcMagicDamage(int skillId) {
        int damage = 0;
        switch (_calcType) {
            case 1:
                damage = calcPcMagicDamage(skillId);
                break;
            case 2:
                damage = calcNpcMagicDamage(skillId);
        }
        //damage = calcMrDefense(damage);
        if (_pc.isWizard() && _pc.getReincarnationSkill()[0] > 0 && RandomArrayList.getInc(100, 1) > 100 - _pc.getReincarnationSkill()[0] * 2) { // 法師天賦技能致命一擊
            _pc.sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(8041))); // 發動天賦技能 致命一擊 無視對方抗魔。
        } else {
            damage = calcMrDefense(damage);
        }
        return damage;
    }

    /**
     * 攻擊PC時的魔法傷害計算
     *
     */
    private int calcPcMagicDamage(int skillId) {
        if (_targetPc == null) {
            return 0;
        }
        if (dmg0(_targetPc)) {
            return 0;
        }
        if (calcEvasion()) {
            return 0;
        }
        int dmg;
        if (skillId == FINAL_BURN) {
            dmg = _pc.getCurrentMp();
        } else {
            dmg = calcMagicDiceDamage(skillId);// 魔法基礎傷害計算
            dmg = (int) (dmg * (getLeverage() / 10.0D));
        }
        // 如果目標有 HAND_DARKNESS 狀態，將傷害值變為3倍
        if (_targetPc.hasSkillEffect(HAND_DARKNESS)) {
            dmg *= 3;
        }
        /*玩家打玩家限制設定*/
        if (_pc.isCrown()) { // 王族
            // dmg += 9;
            dmg *= Config_Occupational_Damage.Crown_ADD_MagicPC;
        } else if (_pc.isKnight()) { // 騎士
            // dmg += 12;
            dmg *= Config_Occupational_Damage.Knight_ADD_MagicPC;
        } else if (_pc.isElf()) { //妖精
            // dmg += 5;
            dmg *= Config_Occupational_Damage.Elf_ADD_MagicPC;
        } else if (_pc.isDarkelf()) { //黑暗妖精
            // dmg += 5;
            dmg *= Config_Occupational_Damage.Darkelf_ADD_MagicPC;
        } else if (_pc.isWizard()) { //法師
            // dmg += 4;
            dmg *= Config_Occupational_Damage.Wizard_ADD_MagicPC;
        } else if (_pc.isDragonKnight()) { //龍騎士
            // dmg += 4;
            dmg *= Config_Occupational_Damage.DragonKnight_ADD_MagicPC;
        } else if (_pc.isIllusionist()) { // 幻術師
            // dmg += 7;
            dmg *= Config_Occupational_Damage.Illusionist_ADD_MagicPC;
        } else if (_pc.isWarrior()) { // 幻術師
            // dmg += 7;
            dmg *= Config_Occupational_Damage.Warrior_ADD_MagicPC;
        }
        dmg -= _targetPc.getDamageReductionByArmor() + _targetPc.getMagicDmgReduction();// 裝備傷害減免
        dmg -= _targetPc.dmgDowe();// 隨機傷害減免
        if (_targetPc.getClanid() != 0) {
            dmg = (int) (dmg - getDamageReductionByClan(_targetPc));// 血盟魔法減傷
        }
        // 護甲身軀
        if (_targetPc.isWarrior() && _targetPc.isARMORGARDE()) {
            dmg += _targetPc.getAc() / 10;
        }
        // 神諭
        if (_targetPc.isWizard() && _targetPc.isOracle()) {
            dmg += _targetPc.getAc() / 10;
        }
        // 增幅防禦 傷害減免
        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            if (_targetPc.getLevel() >= 50 && ConfigSkillKnight.KnightYN && _targetPc.getMeteLevel() >= 0) {
                dmg -= Math.min((_targetPc.getLevel() - 50) / 5 + 1, 7);
            }
        }
        if (skillId == METEOR_STRIKE || skillId == DISINTEGRATE) { // SRC0808
            if (_pc.getMeteLevel() >= 4) {
                dmg *= ConfigSkillWizard.Meteor_TO_Turn4;
            }
        }
        if (dmg > _targetPc.getCurrentHp()) { // 精靈新技能 魔力護盾
            if (_targetPc.isElf() && _targetPc.hasSkillEffect(SOUL_BARRIER)) {
                if (dmg > _targetPc.getCurrentHp() + _targetPc.getCurrentMp()) {
                    dmg = _targetPc.getCurrentHp() + _targetPc.getCurrentMp();
                }
            } else {
                dmg = _targetPc.getCurrentHp();
            }
        }
        if (_targetPc.getzhufuMoPvp() != 0) {//祝福化PVP傷害減免
            dmg -= _targetPc.getzhufuMoPvp();
        }
        if (_targetPc.getzhufuMoPvpbai() != 0) {//祝福化PVP傷害減免百分比
            dmg -= ((dmg / 100) * (_targetPc.getzhufuMoPvpbai()));
        }
        int MagicR = _targetPc.getMagicR() // 魔法傷害減免
                + _targetPc.getDamageReductionByArmor(); // 被攻擊者防具增加全部傷害減免

        boolean isMagicR;
        if (MagicR != 0) {
            final int IgnoreMagicR = _pc.getIgnoreMagicR();
            isMagicR = true;
            if (IgnoreMagicR != 0) {
                if (IgnoreMagicR > _random.nextInt(300)) {
                    isMagicR = false;
                    _pc.sendPackets(new S_ServerMessage("無視目標魔法傷減", 17));
                }
            }

            final int DownTgMagicR = _pc.getDownTgMagicR();
            if (DownTgMagicR != 0 && isMagicR) {
                if (DownTgMagicR >= MagicR) {
                    isMagicR = false;
                } else {
                    MagicR -= DownTgMagicR;
                }
            }
            if (isMagicR) {
                dmg -= MagicR;
            }
        }

        if (SubItemSet.START && _targetPc != null) { // 確保 _targetPc 不為 null
            dmg = (int) calcSubMagic(dmg, null, _targetPc);
        }
        // 其他傷害減免
        boolean dmgX2 = false;
        if ((!_targetPc.getSkillisEmpty()) && (_targetPc.getSkillEffect().size() > 0)) {
            try {
                for (Integer key : _targetPc.getSkillEffect()) {
                    Integer integer = L1AttackList.SKD3.get(key);
                    if (integer != null) {
                        if (integer.equals(key)) {
                            dmgX2 = true;
                        } else {
                            dmg += integer;
                        }
                    }
                }
            } catch (ConcurrentModificationException ignored) {
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        if (l1skills.getTarget().equals("attack") && (l1skills.getArea() == 0)) {// 單體攻擊魔法
            dmg += AttrAmuletEffect();// 火焰之暈
        }
        if (dmgX2) {// 聖界減半
            dmg /= 2;
        }
        if ((_targetPc.hasSkillEffect(6685)// 水龍之魔眼
                || _targetPc.hasSkillEffect(6687)// 生命之魔眼
                || _targetPc.hasSkillEffect(6688)// 誕生之魔眼
                || _targetPc.hasSkillEffect(6689))// 形象之魔眼
                && (_random.nextInt(100) < 10)) {// 魔眼10%機率減傷
            dmg /= 2;
            _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 6320));
        }
        // 鏡反射
        if ((_targetPc.hasSkillEffect(COUNTER_MIRROR)) && (_calcType == PC_PC) && (_targetPc.getWis() >= _random.nextInt(100))) {
            _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), 2));
            _pc.receiveDamage(_targetPc, dmg, false, false);
            _pc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 4395));
            dmg = 0;
            _targetPc.killSkillEffectTimer(COUNTER_MIRROR);
        }
        // 破壞之鏡
        if ((_targetPc.isLIFE_STREAM()) && (_calcType == PC_PC) && (_targetPc.getWis() >= _random.nextInt(100))) {
            _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), 2));
            _pc.receiveDamage(_targetPc, dmg, false, false);
            _pc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 4395)); // 攻击者动画效果
            L1SkillUse skillUse = new L1SkillUse(); // 攻击者被魔法相消
            skillUse.handleCommands(_targetPc, CANCELLATION, _pc.getId(), _pc.getX(), _pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
            dmg = 0;
        }
        int WW4RANDOM = 0;
        if (_targetPc.isWarrior() && _targetPc.getMeteLevel() >= 4) {
            WW4RANDOM = ConfigSkillWarrior.WHYW4;
        } // SRC0808
        int doll1 = 0;
        if (_targetPc.hasSkillEffect(5222)) {
            doll1 = 5;
        }
        if (_targetPc.isWarrior() && (_targetPc.getCurrentHp() < ((_targetPc.getMaxHp() / 100D) * (ConfigSkillWarrior.Warrior_Magic + WW4RANDOM + _targetPc.getRisingUp() + doll1)// 血量低於40%
        )) && _targetPc.isCrystal()// 足夠魔法結晶體
                && _targetPc.isTITANMAGIC() && (_random.nextInt(100) < ConfigSkillWarrior.isPassive_Tatin_Magic)) { // SRC0808
            dmg = 0;
            actionTitan();
            commitTitan(_targetPc.colcTitanDmg());
        }
        _weapon = _pc.getWeapon();
        if (_weapon != null) {
            if (_pc.getNERan() != 0) {
                if (_random.nextInt(100) < _pc.getNERan()) {
                    dmg += _pc.getNEExtraMagicDmg();
                    if (_pc.getNEGfx() != 0) {
                        _pc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), _pc.getNEGfx()));
                    }
                    if (_pc.getNEGfx2() != 0) {
                        _pc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), _pc.getNEGfx2()));
                    }
                }
            } else {
                dmg += _pc.getNEExtraMagicDmg();
            }
            if (dmg > 0) {
                if (_pc.getBeFollowId() != 0) {
                    _pc.setAttackId(_targetPc.getId());
                }
                L1PinkName.onAction(_targetPc, _pc);
            }
        }
        return Math.max(dmg, 0);
    }

    /**
     * 攻擊NPC時的魔法傷害計算
     *
     */
    private int calcNpcMagicDamage(int skillId) {
        if (_targetNpc == null) {
            return 0;
        }
        if (dmg0(_targetNpc)) {
            return 0;
        }
        /*
         * if(!checkAttackError(_targetNpc,_pc)){ return 0; }
         */
        int npcId = _targetNpc.getNpcTemplate().get_npcId();
        Integer tgskill = L1AttackList.SKNPC.get(npcId);
        if ((tgskill != null) && (!_pc.hasSkillEffect(tgskill))) {
            return 0;
        }
        Integer tgpoly = L1AttackList.PLNPC.get(npcId);
        if ((tgpoly != null) && (tgpoly.equals(_pc.getTempCharGfx()))) {
            return 0;
        }
        int dmg = 0;
        if (skillId == FINAL_BURN) {
            dmg = _pc.getCurrentMp();
        } else {
            dmg = calcMagicDiceDamage(skillId);
            dmg = (int) (dmg * (getLeverage() / 10.0D));
        }
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
        if (castleId > 0) {
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        boolean isPet = false;
        if ((_targetNpc instanceof L1PetInstance)) {
            isPet = true;
            if (_targetNpc.getMaster().equals(_pc)) {
                dmg = 0;
            }
        }
        if ((_targetNpc instanceof L1SummonInstance)) {
            L1SummonInstance summon = (L1SummonInstance) _targetNpc;
            if (summon.isExsistMaster()) {
                isPet = true;
            }
            if (_targetNpc.getMaster().equals(_pc)) {
                dmg = 0;
            }
        }
        if (skillId == METEOR_STRIKE || skillId == DISINTEGRATE) { // SRC0808
            if (_pc.getMeteLevel() >= 4) {
                dmg *= ConfigSkillWizard.Meteor_TO_Turn4;
            }
        }
        if (dmg > _pc.getCurrentHp()) { // 精靈新技能 魔力護盾
            if (_pc.isElf() && _pc.hasSkillEffect(L1SkillId.SOUL_BARRIER)) {
                if (dmg > _pc.getCurrentHp() + _pc.getCurrentMp()) {
                    dmg = _pc.getCurrentHp() + _pc.getCurrentMp();
                }
            } else {
                dmg = _pc.getCurrentHp();
            }
        }
        if ((!isNowWar) && (isPet) && (dmg != 0)) {
            dmg /= 8;
        }
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        if (l1skills.getTarget().equals("attack") && (l1skills.getArea() == 0)) {// 單體攻擊魔法
            dmg += AttrAmuletEffect();// 火焰之暈
        }
        /*
         * if (l1skills.getTarget().equals("none") && (l1skills.getArea() > 0))
         * {// 無方向範圍魔法 if (dmg >= _targetNpc.getCurrentHp()) {// 如果傷害大於等於目前HP
         * dmg = _targetNpc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用範圍魔法掛機) } }
         */
        if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
            dmg /= 2;
        }
        if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM2)) {// 聖界減傷
            dmg /= 2;
        }
        // 鏡反射
        if ((_targetNpc.hasSkillEffect(COUNTER_MIRROR)) && (_calcType == PC_NPC) && (_targetNpc.getWis() >= _random.nextInt(100))) {
            _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), 2));
            _pc.receiveDamage(_targetNpc, dmg, false, false);
            _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 4395));
            dmg = 0;
            _targetNpc.killSkillEffectTimer(COUNTER_MIRROR);
        }
        // 吉爾塔斯-全體鏡反射
        if ((_targetNpc.hasSkillEffect(11059)) && (_calcType == PC_NPC)) {
            _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), 2));
            _pc.receiveDamage(_targetNpc, dmg, false, false);
            _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 4395));
            dmg = 0;
        }
        _weapon = _pc.getWeapon();
        if (_weapon != null) {
            if (_pc.getNERan() != 0) {
                if (_random.nextInt(100) < _pc.getNERan()) {
                    dmg += _pc.getNEExtraMagicDmg();
                    if (_pc.getNEGfx() != 0) {
                        _pc.sendPacketsAll(new S_SkillSound(_targetNpc.getId(), _pc.getNEGfx()));
                    }
                    if (_pc.getNEGfx2() != 0) {
                        _pc.sendPacketsAll(new S_SkillSound(_targetNpc.getId(), _pc.getNEGfx2()));
                    }
                }
            } else {
                dmg += _pc.getNEExtraMagicDmg();
            }
            if (SubItemSet.START && _targetPc != null) { // 確保 _targetPc 不為 null
                dmg = (int) calcSubMagic(dmg, null, _targetPc);
            }
            if (_pc.getBeFollowId() != 0) {
                _pc.setAttackId(_targetNpc.getId());
            }
        }
        return dmg;
    }

    /**
     * 火焰之暈傷害計算
     */
    private int AttrAmuletEffect() {
        int dmg = 0;
        switch (_calcType) {
            case PC_PC:
                if (_pc.hasSkillEffect(FIRESTUN)) {
                    if (_random.nextInt(100) < 5) {// 5%機率傷害+50
                        dmg = 50;
                        _targetPc.broadcastPacketAll(new S_SkillSound(_targetPc.getId(), 13542));
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _targetPc, 0, dmg));
                    }
                } else if (_pc.hasSkillEffect(TRUEFIRESTUN)) {
                    if (_random.nextInt(100) < 5) {// 7%機率傷害+80
                        dmg = 80;
                        _targetPc.broadcastPacketAll(new S_SkillSound(_targetPc.getId(), 13542));
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _targetPc, 0, dmg));
                    }
                } else if (_pc.hasSkillEffect(MOONATTACK)) {
                    if (_random.nextInt(100) < 5) {// 7%機率傷害+120
                        dmg = 120;
                        _targetPc.broadcastPacketAll(new S_SkillSound(_targetPc.getId(), 13989));
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _targetPc, 0, dmg));
                    }
                }
                break;
            case PC_NPC:
                if (_pc.hasSkillEffect(FIRESTUN)) {
                    if (_random.nextInt(100) < 5) {// 5%機率傷害+50
                        dmg = 50;
                        _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 13542));
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _targetNpc, 0, dmg));
                    }
                } else if (_pc.hasSkillEffect(TRUEFIRESTUN)) {
                    if (_random.nextInt(100) < 5) {// 7%機率傷害+80
                        dmg = 80;
                        _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 13542));
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _targetNpc, 0, dmg));
                    }
                } else if (_pc.hasSkillEffect(MOONATTACK)) {
                    if (_random.nextInt(100) < 5) {// 7%機率傷害+120
                        dmg = 120;
                        _targetNpc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 13989));
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _targetNpc, 0, dmg));
                    }
                }
                break;
        }
        // System.out.println("dmg ==" + dmg);
        return dmg;
    }

    /**
     * 魔法基礎傷害計算（支援BUFF加成，設定1控制傷害加乘%）
     */
    private int calcMagicDiceDamage(int skillId) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        int dice = l1skills.getDamageDice();            // 骰面
        int diceCount = l1skills.getDamageDiceCount();  // 骰數
        int value = l1skills.getDamageValue();          // 固定增加傷害
        int magicDamage = 0;
        // 1. 骰子傷害
        for (int i = 0; i < diceCount; i++) {
            magicDamage += _random.nextInt(dice) + 1;
        }
        magicDamage += value;

        // 2. 血盟魔法增傷
        if (_pc.getClanid() != 0) {
            magicDamage = (int) (magicDamage + getDamageUpByClan(_pc));
        }

        // 3. 魔攻加成計算
        int spByItem = getTargetSp();
        int charaIntelligence = Math.max(_pc.getInt() + spByItem - 12, 1);
        double attrDeffence = calcAttrResistance(l1skills.getAttr());
        double coefficient = Math.max(1.0D - attrDeffence + charaIntelligence * 3.0D / 32.0D, 0.0D);
        magicDamage = (int) (magicDamage * coefficient);

        // 4. 爆擊計算
        int totalCriticalChance = 5 + _pc.getOriginalMagicCritical();
        int rnd = _random.nextInt(100) + 1;
        boolean fear = false;
        final boolean critical = skillId == DISINTEGRATE || l1skills.getSkillLevel() <= 6;
        final boolean random_crit = rnd <= totalCriticalChance;

        double criticalCoefficient;
        if (critical && random_crit) {
            if (skillId == DISINTEGRATE && _pc.isDISINTEGRATE_2()) {
                criticalCoefficient = 10.0;
                fear = true;
            } else {
                criticalCoefficient = 1.5;
            }
            magicDamage = (int) (magicDamage * criticalCoefficient);
            _pc.setMagicCritical(true);
        }

        // 5. DISINTEGRATE帶debuff
        if (fear) {
            if (_targetPc != null) {
                if (_targetPc.hasSkillEffect(RESIST_FEAR)) {
                    _targetPc.killSkillEffectTimer(RESIST_FEAR);
                }
                _targetPc.setSkillEffect(RESIST_FEAR, 15 * 1000);
                _targetPc.add_dodge_down(5);
                _targetPc.sendPackets(new S_PacketBoxIcon1(false, _targetPc.get_dodge_down()));
                _targetPc.sendPackets(new S_InventoryIcon(13008, true, 2747, 15));
            }
        }
        // 6. 魔攻來源加成
        magicDamage += _pc.getOriginalMagicDamage() + _pc.getMagicDmgModifier();
        // 7. 吃書傷害加成（設定4）
        int bookLevelForDmg = _pc.getSkillLevel(skillId);
        L1SkillEnhance enhanceDataForDmg = SkillEnhanceTable.get().getEnhanceData(skillId, bookLevelForDmg);
        if (enhanceDataForDmg != null) {
            double extraDamagePercent = enhanceDataForDmg.getSetting4();
            if (extraDamagePercent < 0) extraDamagePercent = 0; // 下限
            if (extraDamagePercent > 500) extraDamagePercent = 500; // 上限（可調）
            magicDamage += (int) (magicDamage * (extraDamagePercent / 100.0));
        }
        // === 8. BUFF加成（只要身上有古代啟示BUFF，所有魔法都受益，幅度依設定1）===
        if (_pc.hasSkillEffect(ICE_LANCE)) {
            int level = _pc.getSkillLevel(ICE_LANCE);
            L1SkillEnhance enhance = SkillEnhanceTable.get().getEnhanceData(ICE_LANCE, level);
            if (enhance != null) {
                double dmgBonus = enhance.getSetting1(); // 設定1作為傷害加成%
                if (dmgBonus < -100) dmgBonus = -100;
                if (dmgBonus > 500) dmgBonus = 500;
                magicDamage = (int)(magicDamage * (1.0 + dmgBonus / 100.0));
            }
        }
        return magicDamage;
    }

    /**
     * 治癒魔法的處理
     */
    public int calcHealing(int skillId) {
        //		L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        //		int dice = l1skills.getDamageDice();
        //		int value = l1skills.getDamageValue();
        //		int magicDamage = 0;
        //		int magicBonus = getMagicBonus();
        //
        //		int diceCount = value + magicBonus;
        //		for (int i = 0; i < diceCount; i++) {
        //			magicDamage += _random.nextInt(dice) + 1;
        //		}
        //
        //		double alignmentRevision = 1.0D;
        //		if (getLawful() > 0) {
        //			alignmentRevision += getLawful() / 32768.0D;
        //		}
        //
        //		magicDamage = (int) (magicDamage * alignmentRevision);
        //
        //		magicDamage = (int) (magicDamage * (getLeverage() / 10.0D));
        //
        //		if (_pc.isWizard() || _pc.isIllusionist()) {
        //			magicDamage *= 1.0;
        //		} else if (_pc.isElf() || _pc.isDragonKnight()) {
        //			magicDamage *= 0.8;
        //		} else if (_pc.isDarkelf() || _pc.isCrown()) {
        //			magicDamage *= 0.5;
        //		} else if (_pc.isKnight() || _pc.isWarrior()) {
        //			magicDamage *= 0.3;
        //		}
        //
        //		return magicDamage;
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        int dice = l1skills.getDamageDice();
        int value = l1skills.getDamageValue();
        int magicDamage = 0;
        if (skillId != NATURES_BLESSING) {
            int magicBonus = (getMagicBonus() - 2);
            if (magicBonus > 10) {
                magicBonus = 10;
            }
            int diceCount = value + magicBonus;
            for (int i = 0; i < diceCount; i++) {
                magicDamage += (_random.nextInt(dice) + 1);
            }
        } else {
            int Int = 0;
            if (_calcType == PC_PC || _calcType == PC_NPC) {
                Int = _pc.getInt();
            } else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
                Int = _npc.getInt();
            }
            if (Int < 12) {
                Int = 12;
            }
            for (int i = 12; i <= Int; i++) {
                if (i == 12) {
                    magicDamage += (100 + _random.nextInt(80));
                } else if (i >= 13 && i <= 17) {
                    magicDamage += (3 + _random.nextInt(2));
                } else if (i >= 18 && i <= 25) {
                    magicDamage += (10 + _random.nextInt(6));
                } else if (i >= 26) {
                    magicDamage += (1 + _random.nextInt(2));
                }
            }
            magicDamage /= 2.2;
        }
        double alignmentRevision = 1.0;
        if (getLawful() > 0) {
            alignmentRevision += (getLawful() / 32768.0);
        }
        magicDamage *= alignmentRevision;
        if (skillId != NATURES_BLESSING) {
            magicDamage = (magicDamage * getLeverage()) / 10;
        }
        if (skillId == NATURES_BLESSING && _pc.getMeteLevel() >= 4) {  //SRC0808
            magicDamage *= ConfigSkillElf.E4NATURES_BLESSING;
        }
        if (_pc.isElf() && _pc.getReincarnationSkill()[0] > 0 && (/*skillId == HEAL || */skillId == EXTRA_HEAL || skillId == GREATER_HEAL || skillId == NATURES_BLESSING)) { // 妖精天賦技能神聖祝福
            magicDamage += (int) (magicDamage * (_pc.getReincarnationSkill()[0] * 0.05)); // 每+1點補血量+5%
        }
        if (_pc.isWizard() && _pc.getReincarnationSkill()[1] > 0 && RandomArrayList.getInc(100, 1) <= 10 // 機率固定10%
                && (/*skillId == HEAL || */skillId == EXTRA_HEAL || skillId == GREATER_HEAL || skillId == FULL_HEAL)) { // 法師天賦技能療癒風暴
            magicDamage += (int) (magicDamage * (_pc.getReincarnationSkill()[1] * 0.1)); // 每+1點補血量+10%
        }
        return magicDamage;
    }

    /**
     * 魔防減傷的計算
     *
     */
    private int calcMrDefense(int dmg) {
        int mr = getTargetMr();
        double mrFloor = 0;
        if (mr < 100) {
            mrFloor = Math.floor((mr - _pc.getOriginalMagicHit()) / 2);
        } else if (mr >= 100) {
            mrFloor = Math.floor((mr - _pc.getOriginalMagicHit()) / 10);
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
     * 傷害資訊送出
     */
    public void commit(int damage, int drainMana) {
        switch (_calcType) {
            case 1:
                commitPc(damage, drainMana);
                break;
            case 2:
                commitNpc(damage, drainMana);
        }
        if (!ConfigAlt.ALT_ATKMSG) {
            return;
        }
        switch (_calcType) {
            case 1:
                if ((_pc.getAccessLevel() == 0) && (_targetPc.getAccessLevel() == 0)) {
                    return;
                }
                break;
            case 2:
                if (_pc.getAccessLevel() == 0) {
                    return;
                }
                break;
        }
        switch (_calcType) {
            case 1:
                if (_pc.getAccessLevel() > 0) {
                    StringBuilder atkMsg = new StringBuilder();
                    atkMsg.append("對PC送出技能: ");
                    atkMsg.append(_pc.getName()).append(">");
                    atkMsg.append(_targetPc.getName()).append(" ");
                    atkMsg.append("傷害: ").append(damage);
                    atkMsg.append(" 目標HP:").append(_targetPc.getCurrentHp());
                    _pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
                }
                if (_targetPc.getAccessLevel() > 0) {
                    StringBuilder atkMsg = new StringBuilder();
                    atkMsg.append("受到PC技能: ");
                    atkMsg.append(_pc.getName()).append(">");
                    atkMsg.append(_targetPc.getName()).append(" ");
                    atkMsg.append("傷害: ").append(damage);
                    atkMsg.append(" 目標HP:").append(_targetPc.getCurrentHp());
                    _targetPc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
                }
                break;
            case 2:
                if (_pc.getAccessLevel() > 0) {
                    StringBuilder atkMsg = new StringBuilder();
                    atkMsg.append("對NPC送出技能: ");
                    atkMsg.append(_pc.getName()).append(">");
                    atkMsg.append(_targetNpc.getNameId()).append(" ");
                    atkMsg.append("傷害: ").append(damage);
                    atkMsg.append(" 目標HP:").append(_targetNpc.getCurrentHp());
                    _pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
                }
                break;
        }
    }

    /**
     * 傷害資訊送出
     */
    private void commitPc(int damage, int drainMana) {
        try {
            if (drainMana > 0) {
                if (_targetPc.getCurrentMp() > 0) {
                    drainMana = Math.min(drainMana, _targetPc.getCurrentMp());
                    int newMp = _pc.getCurrentMp() + drainMana;
                    _pc.setCurrentMp(newMp);
                } else {
                    drainMana = 0;
                }
            }
            _targetPc.receiveManaDamage(_pc, drainMana);
            _targetPc.receiveDamage(_pc, damage, true, false);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 傷害資訊送出
     */
    private void commitNpc(int damage, int drainMana) {
        try {
            if (drainMana > 0) {
                if (_targetNpc.getCurrentMp() > 0) {
                    int drainValue = _targetNpc.drainMana(drainMana);
                    int newMp = _pc.getCurrentMp() + (drainValue >= 2 ? drainValue / 2 : drainValue);
                    _pc.setCurrentMp(newMp);
                } else {
                    drainMana = 0;
                }
            }
            _targetNpc.ReceiveManaDamage(_pc, drainMana);
            _targetNpc.receiveDamage(_pc, damage);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1MagicPc JD-Core Version: 0.6.2
 */