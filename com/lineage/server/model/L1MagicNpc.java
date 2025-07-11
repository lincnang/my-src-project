package com.lineage.server.model;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigSkillKnight;
import com.lineage.config.Config_Occupational_Damage;
import com.lineage.data.event.SubItemSet;
import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1SkillEnhance;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ConcurrentModificationException;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1MagicNpc extends L1MagicMode {
    private static final Log _log = LogFactory.getLog(L1MagicNpc.class);

    public L1MagicNpc(L1NpcInstance attacker, L1Character target) {
        if (attacker == null) {
            return;
        }
        if ((target instanceof L1PcInstance)) {
            _calcType = NPC_PC;
            _npc = attacker;
            _targetPc = ((L1PcInstance) target);
        } else {
            _calcType = NPC_NPC;
            _npc = attacker;
            _targetNpc = ((L1NpcInstance) target);
        }
    }

    private int getMagicLevel() {
        int magicLevel = _npc.getMagicLevel();
        return magicLevel;
    }

    private int getMagicBonus() {
        int magicBonus = _npc.getMagicBonus();
        return magicBonus;
    }

    private int getLawful() {
        int lawful = _npc.getLawful();
        return lawful;
    }

    /**
     * 機率性魔法是否成功
     */
    public boolean calcProbabilityMagic(int skillId) {
        int probability = 0;
        boolean isSuccess = false;
        switch (_calcType) {
            case NPC_PC:
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
                if (skillId == CANCELLATION) {// 魔法相消術
                    return true;
                }
                break;
            case NPC_NPC:
            /*
            if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
                if (skillId != CANCELLATION) {
                    return false;
                }
            }
            */
                if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
                    if (skillId != CANCELLATION) {
                        return false;
                    }
                }
                break;
        }

        // 這裡命中率如果是CANCELLATION就從資料庫撈
        if (skillId == CANCELLATION) {
            int skillLevel = 1;
            L1SkillEnhance enhance = SkillEnhanceTable.get().getEnhanceData(skillId, skillLevel);
            if (enhance != null) {
                probability = enhance.getSetting1();
            } else {
                probability = 100;
            }
        } else {
            probability = calcProbability(skillId);
        }
        int rnd = _random.nextInt(100) + 1;
        probability = Math.min(probability, 90); // 或 100，依你的需求
        isSuccess = probability >= rnd;

        if (calcEvasion()) {
            return false;
        }
        return isSuccess;
    }


    private int calcProbability(int skillId) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        int attackLevel = _npc.getLevel();
        int defenseLevel = 0;
        int probability = 0;
        switch (_calcType) {
            case NPC_PC:
                defenseLevel = _targetPc.getLevel();
                break;
            case NPC_NPC:
                defenseLevel = _targetNpc.getLevel();
                if ((skillId == 145) && ((_targetNpc instanceof L1SummonInstance))) {
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
            case 153:// 魔法消除
            case 157:// 大地屏障
            case 161:// 封印禁地
            case 173:// 污濁之水
            case 174:// 精準射擊
                probability = (int) (l1skills.getProbabilityDice() / 10.0D * (attackLevel - defenseLevel));
                probability += l1skills.getProbabilityValue();
                probability -= getTargetMr() / 10;
                probability = (int) (probability * (getLeverage() / 10.0D));// DB設定增加命中倍率
                break;
            case 87:// 衝擊之暈
            case EMPIRE:// 暈眩之劍
                if (attackLevel > defenseLevel) {// 攻擊方等級大於防禦方
                    probability = 70;
                } else if (attackLevel == defenseLevel) {// 攻擊方等級相等防禦方
                    probability = 50;
                } else if (attackLevel < defenseLevel) {// 攻擊方等級小於防禦方
                    probability = 30;
                }
                probability = (int) (probability * (getLeverage() / 10.0D));// DB設定增加命中倍率
                break;
            case 91:// 反擊屏障
                probability = 15;
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
                probability = 38 + (attackLevel - defenseLevel) * (_random.nextInt(3) + 2);
                probability = (int) (probability * (getLeverage() / 10.0D));// DB設定增加命中倍率
                break;
            case 202:// 混亂
            case 212:// 幻想
            case 217:// 恐慌
            case 208:// 骷髏毀壞
                probability = Random.nextInt(11) + 20;
                probability += (attackLevel - defenseLevel) * 2;
                probability = (int) (probability * (getLeverage() / 10.0D));// DB設定增加命中倍率
                break;
            case 183:// 護衛毀滅
            case 188:// 恐懼無助
            case 192:// 奪命之雷
            case 193:// 驚悚死神
                probability = (int) (l1skills.getProbabilityDice() / 10.0D * (attackLevel - defenseLevel));
                probability += l1skills.getProbabilityValue();
                probability = (int) (probability * (getLeverage() / 10.0D));// DB設定增加命中倍率
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
                probability = (int) (probability * (getLeverage() / 10.0D));// DB設定增加命中倍率
                probability -= getTargetMr();
        }
        if (_calcType == NPC_PC) {
            switch (skillId) {
                case 157:
                case 192:// 奪命之雷
                    probability -= _targetPc.getRegistSustain();
                    break;
                case 87:
                case 208:
                    probability -= _targetPc.getRegistStun();
                    break;
                case 33:
                    probability -= _targetPc.getRegistStone();
                    break;
                case 66:
                case 103:
                case 212:
                    probability -= _targetPc.getRegistSleep();
                    break;
                case 50:
                case 80:
                    probability -= _targetPc.getRegistFreeze();
                    break;
                case 20:
                case 40:
                    probability -= _targetPc.getRegistBlind();
            }
        }
        return probability;
    }

    public int calcMagicDamage(int skillId) {
        int damage = 0;
        switch (_calcType) {
            case NPC_PC:
                damage = calcPcMagicDamage(skillId);
                break;
            case NPC_NPC:
                damage = calcNpcMagicDamage(skillId);
        }
        damage = calcMrDefense(damage);
        return damage;
    }

    /**
     * NPC對PC傷害計算
     *
     */
    public int calcPcMagicDamage(int skillId) {
        if (_targetPc == null) {
            return 0;
        }
        if ((((_npc instanceof L1PetInstance)) || ((_npc instanceof L1SummonInstance))) && (_targetPc.getZoneType() == 1)) {
            return 0;
        }
        if (dmg0(_targetPc)) {
            return 0;
        }
        if (calcEvasion()) {
            return 0;
        }

        int dmg = 0;
        if (skillId == 108) {
            dmg = _npc.getCurrentMp();
        } else {
            dmg = calcMagicDiceDamage(skillId);
            dmg = (int) (dmg * (getLeverage() / 10.0D));
            if (_npc.getNpcTemplate().get_nameid().startsWith("BOSS")) { // BOSS傷害加倍
                dmg *= Config_Occupational_Damage.BOSS_POWER;
            }
        }
        dmg -= _targetPc.getDamageReductionByArmor() + _targetPc.getMagicDmgReduction();
        dmg -= _targetPc.dmgDowe();
        dmg -= _targetPc.getMagicR(); // 魔法傷害減免
        if (_targetPc.getClanid() != 0) {
            dmg = (int) (dmg - getDamageReductionByClan(_targetPc));
        }
        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            int targetPcLvl = Math.max(_targetPc.getLevel(), 50);
            dmg -= (targetPcLvl - 50) / 5 + 1;
        }
        // 計算 dmg2 並累加到 dmg
        if (SubItemSet.START && _targetPc != null) { // 確保 SubItemSet 開啟且 _targetPc 不為 null
            double dmg2 = calcSubMagic(dmg, null, _targetPc);
            dmg += dmg2; // 將 dmg2 加到 dmg 上
        }

        boolean dmgX2 = false;
        if ((!_targetPc.getSkillisEmpty()) && (_targetPc.getSkillEffect().size() > 0)) {
            try {
                for (Integer key : _targetPc.getSkillEffect()) {
                    Integer integer = (Integer) L1AttackList.SKD3.get(key);
                    if (integer != null) {
                        if (integer.equals(key)) {
                            dmgX2 = true;
                        } else {
                            dmg += integer;
                        }
                    }
                }
            } catch (ConcurrentModificationException localConcurrentModificationException) {
                // 處理並發修改異常
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }

        if (SubItemSet.START && _targetPc != null) { // 確保 _targetPc 不為 null
            dmg = (int) calcSubMagic(dmg, null, _targetPc);
        }

        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
        if (castleId > 0) {
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        if (!isNowWar) {
            if ((_npc instanceof L1PetInstance)) {
                dmg >>= 3;
            }
            if ((_npc instanceof L1SummonInstance)) {
                L1SummonInstance summon = (L1SummonInstance) _npc;
                if (summon.isExsistMaster()) {
                    dmg >>= 3;
                }
            }
        }
        if (dmgX2) {
            dmg >>= 1;
        }
        if ((_targetPc.hasSkillEffect(6685)// 水龍之魔眼
                || _targetPc.hasSkillEffect(6687)// 生命之魔眼
                || _targetPc.hasSkillEffect(6688)// 誕生之魔眼
                || _targetPc.hasSkillEffect(6689))// 形象之魔眼
                && (_random.nextInt(100) < 10)) {// 魔眼10%機率減傷
            dmg /= 2;
            _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 6320));
        }
        if (_targetPc.hasSkillEffect(134)) {
            int npcId = _npc.getNpcTemplate().get_npcId();
            switch (npcId) {
                case 45681:
                case 45682:
                case 45683:
                case 45684:
                case 71014:
                case 71015:
                case 71016:
                case 71026:
                case 71027:
                case 71028:
                case 97204:
                case 97205:
                case 97206:
                case 97207:
                case 97208:
                case 97209:
                    break;
                default:
                    if (_npc.getNpcTemplate().get_IsErase()) {
                        if (_targetPc.getWis() >= _random.nextInt(100)) {
                            _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), 2));
                            _npc.receiveDamage(_targetPc, dmg);
                            _npc.broadcastPacketAll(new S_SkillSound(_targetPc.getId(), 4395));
                            dmg = 0;
                            _targetPc.killSkillEffectTimer(134);
                        }
                    }
                    break;
            }
        }
        int dmgOut = Math.max(dmg, 0);
        return dmgOut;
    }

    private int calcNpcMagicDamage(int skillId) {
        if (_targetNpc == null) {
            return 0;
        }
        if (dmg0(_targetNpc)) {
            return 0;
        }
        int dmg = 0;
        if (skillId == 108) {
            dmg = _npc.getCurrentMp();
        } else {
            dmg = calcMagicDiceDamage(skillId);
            dmg = (int) (dmg * (getLeverage() / 10.0D));
            if (_npc.getNpcTemplate().get_nameid().startsWith("BOSS")) {// BOSS傷害加倍
                dmg *= Config_Occupational_Damage.BOSS_POWER;
            }
        }
        if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM)) {
            dmg /= 2;
        }
        if ((_targetNpc.hasSkillEffect(134)) && (_npc.getNpcTemplate().get_IsErase())) {
            if (_targetNpc.getWis() >= _random.nextInt(100)) {
                _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), 2));
                _npc.receiveDamage(_targetNpc, dmg);
                _npc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 4395));
                dmg = 0;
                _targetNpc.killSkillEffectTimer(134);
            }
        }
        if ((_targetNpc.hasSkillEffect(11059)) && (_npc.getNpcTemplate().get_IsErase())) {
            _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), 2));
            _npc.receiveDamage(_targetNpc, dmg);
            _npc.broadcastPacketAll(new S_SkillSound(_targetNpc.getId(), 4395));
            dmg = 0;
        }
        return dmg;
    }

    private int calcMagicDiceDamage(int skillId) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        int dice = l1skills.getDamageDice();// 骰面
        int diceCount = l1skills.getDamageDiceCount();// 骰數
        int value = l1skills.getDamageValue();// 固定增加傷害
        int magicDamage = 0;
        int charaIntelligence = 0;
        for (int i = 0; i < diceCount; i++) {
            magicDamage += _random.nextInt(dice) + 1;
        }
        magicDamage += value;
        int spByItem = getTargetSp();
        charaIntelligence = Math.max(_npc.getInt() + spByItem - 12, 1);
        double attrDeffence = calcAttrResistance(l1skills.getAttr());
        double coefficient = Math.max(1.0D - attrDeffence + charaIntelligence * 3.0D / 32.0D, 0.0D);
        magicDamage = (int) (magicDamage * coefficient);
        return magicDamage;
    }

    public int calcHealing(int skillId) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
        int dice = l1skills.getDamageDice();
        int value = l1skills.getDamageValue();
        int magicDamage = 0;
        int magicBonus = Math.min(getMagicBonus(), 10);
        int diceCount = value + magicBonus;
        for (int i = 0; i < diceCount; i++) {
            magicDamage += _random.nextInt(dice) + 1;
        }
        double alignmentRevision = 1.0D;
        if (getLawful() > 0) {
            alignmentRevision += getLawful() / 32768.0D;
        }
        magicDamage = (int) (magicDamage * alignmentRevision);
        magicDamage = (int) (magicDamage * (getLeverage() / 10.0D));
        return magicDamage;
    }

    private int calcMrDefense(int dmg) {
        int mr = getTargetMr();
        int rnd = _random.nextInt(100) + 1;
        if (mr >= rnd) {
            dmg /= 2;
        }
        return dmg;
    }

    public void commit(int damage, int drainMana) {
        switch (_calcType) {
            case NPC_PC:
                commitPc(damage, drainMana);
                break;
            case NPC_NPC:
                commitNpc(damage, drainMana);
        }
        if (!ConfigAlt.ALT_ATKMSG) {
            return;
        }
        if (_calcType == NPC_NPC) {
            return;
        }
        if (!_targetPc.isGm()) {
            return;
        }
        StringBuilder atkMsg = new StringBuilder();
        atkMsg.append("受到NPC技能: ");
        atkMsg.append(_npc.getNameId()).append(">");
        atkMsg.append(_targetPc.getName()).append(" ");
        atkMsg.append("傷害: ").append(damage);
        _targetPc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
    }

    private void commitPc(int damage, int drainMana) {
        try {
            _targetPc.receiveDamage(_npc, damage, true, false);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void commitNpc(int damage, int drainMana) {
        try {
            _targetNpc.receiveDamage(_npc, damage);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1MagicNpc JD-Core Version: 0.6.2
 */