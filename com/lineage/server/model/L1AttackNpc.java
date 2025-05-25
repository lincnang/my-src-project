package com.lineage.server.model;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigSkillDragon;
import com.lineage.config.ConfigSkillWarrior;
import com.lineage.config.Config_Occupational_Damage;
import com.lineage.data.event.SubItemSet;
import com.lineage.server.datatables.SkillEnhanceTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.poison.L1ParalysisPoison;
import com.lineage.server.model.poison.L1SilencePoison;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1SkillEnhance;
import com.lineage.server.templates.L1SystemMessage;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.types.Point;
import com.lineage.server.utils.RandomArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ConcurrentModificationException;
import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;

/**
 * 攻擊判定
 *
 * @author dexc
 */
public class L1AttackNpc extends L1AttackMode {
    private static final Log _log = LogFactory.getLog(L1AttackNpc.class);

    public L1AttackNpc(L1NpcInstance attacker, L1Character target) {
        if (attacker == null) {
            return;
        }
        if (target == null) {
            return;
        }
        if (target.isDead()) {
            return;
        }
        if (target.getCurrentHp() <= 0) {
            return;
        }
        _npc = attacker;
        if ((target instanceof L1PcInstance)) {
            _targetPc = ((L1PcInstance) target);
            _calcType = NPC_PC;
        } else if ((target instanceof L1NpcInstance)) {
            _targetNpc = ((L1NpcInstance) target);
            _calcType = NPC_NPC;
        }
        _target = target;
        _targetId = target.getId();
        _targetX = target.getX();
        _targetY = target.getY();
    }

    /**
     * 命中判定
     */
    @Override
    public boolean calcHit() {
        if (_target == null) { // 物件遺失
            _isHit = false;
            return _isHit;
        }
        switch (_calcType) {
            case NPC_PC:
                _isHit = calcPcHit();
                break;
            case NPC_NPC:
                _isHit = calcNpcHit();
                break;
        }
        return _isHit;
    }

    /**
     * NPC對PC命中
     *
     */
    private boolean calcPcHit() {
        if ((((_npc instanceof L1PetInstance)) || ((_npc instanceof L1SummonInstance))) && (_targetPc.getZoneType() == 1)) {
            return false;
        }
        // 傷害為0
        if (dmg0(_targetPc)) {
            return false;
        }
        // 迴避攻擊
        if (calcEvasion()) {
            return false;
        }
        //#怪物[Spawnlist清單列中]增加命中率(100%) (0=關閉此功能)
        if (!this._npc.getNpcTemplate().is_boss()) {
            this._hitRate += Config_Occupational_Damage.AllMobHit_chance;
        }
        _hitRate += _npc.getLevel() + 5;
        if ((_npc instanceof L1PetInstance)) { // 寵物武器命中追加
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }
        _hitRate += _npc.getHitup();
        int attackerDice = _random.nextInt(20) + 1 + _hitRate - 3;
        // 技能增加閃避
        attackerDice += attackerDice(_targetPc);
        // 防禦力抵銷
        int defenderDice = 0;
        int defenderValue = _targetPc.getAc() * -1;
        if (_targetPc.getAc() >= 0) {
            defenderDice = 10 - _targetPc.getAc();
        } else if (_targetPc.getAc() < 0) {
            defenderDice = 10 + _random.nextInt(defenderValue) + 1;
        }
        int fumble = _hitRate;
        int critical = _hitRate + 17;
        if (attackerDice <= fumble) {
            _hitRate = 0;
        } else if (attackerDice >= critical) {
            _hitRate = 100;
        } else if (attackerDice > defenderDice) {
            _hitRate = 100;
        } else if (attackerDice <= defenderDice) {
            _hitRate = 0;
        }
		/*final int critical = _hitRate + 19;
		if (attackerDice <= fumble) {
			_hitRate = 15;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			// 防禦力抵銷
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 15;
			}
		}*/
        if (_npc.getNpcTemplate().get_nameid().startsWith("BOSS")) {// BOSS增加命中機率
            attackerDice += Config_Occupational_Damage.BOSS_HIT;
        }
        int rnd = _random.nextInt(100) + 1;
        if ((_npc.get_ranged() >= 10) && (_hitRate > rnd) && (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) >= 2)) {
            return calcErEvasion();
        }
        return _hitRate >= rnd;
    }

    /**
     * NPC對NPC命中
     *
     */
    private boolean calcNpcHit() {
        if (dmg0(_targetNpc)) {
            return false;
        }
        _hitRate += _npc.getLevel() + 3;
        if ((_npc instanceof L1PetInstance)) {
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }
        _hitRate += _npc.getHitup();
        int attackerDice = _random.nextInt(20) + 1 + _hitRate - 3;
        attackerDice += attackerDice(_targetNpc);
        if (_npc.getNpcTemplate().get_nameid().startsWith("BOSS")) {// BOSS增加命中機率
            attackerDice += Config_Occupational_Damage.BOSS_HIT;
        }
        int defenderDice = 0;
        int defenderValue = _targetNpc.getAc() * -1;
        if (_targetNpc.getAc() >= 0) {
            defenderDice = 10 - _targetNpc.getAc();
        } else if (_targetNpc.getAc() < 0) {
            defenderDice = 10 + _random.nextInt(defenderValue) + 1;
        }
        int fumble = _hitRate;
        int critical = _hitRate + 19;
        if (attackerDice <= fumble) {
            _hitRate = 0;
        } else if (attackerDice >= critical) {
            _hitRate = 100;
        } else if (attackerDice > defenderDice) {
            _hitRate = 100;
        } else if (attackerDice <= defenderDice) {
            _hitRate = 0;
        }
		/*final int critical = _hitRate + 19;
		if (attackerDice <= fumble) {
			_hitRate = 15;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 15;
			}
		}*/
        int rnd = _random.nextInt(100) + 1;
        return _hitRate >= rnd;
    }

    /**
     * 傷害計算
     */
    @Override
    public int calcDamage() {
        switch (_calcType) {
            case 3:
                _damage = calcPcDamage();
                break;
            case 4:
                _damage = calcNpcDamage();
                break;
        }
        return _damage;
    }

    /**
     * 基礎傷害計算
     *
     */
    private double npcDmgMode(double dmg) {
        if (_npc.getNpcTemplate().get_nameid().startsWith("BOSS")) {// BOSS傷害加倍
            dmg *= Config_Occupational_Damage.BOSS_POWER;
        }
        if (_random.nextInt(100) < 15) {// 15%機率爆擊
            dmg *= 2.0D;
        }
        //怪物[Spawnlist清單列中]打玩家傷害調整
        if (!this._npc.getNpcTemplate().is_boss() && Config_Occupational_Damage.ModDmg > 0.0) {
            dmg *= Config_Occupational_Damage.ModDmg;
        }
        dmg += _npc.getDmgup();
        //#全怪(包含Boss)爆擊率值(%)
        //#全怪(包含Boss)爆擊傷害倍數
        if ((double) _random.nextInt(100) < Config_Occupational_Damage.Mob_Critical_Strike) {
            dmg *= Config_Occupational_Damage.Mob_Critical_Strike_Dmg;
        }
        dmg += (double) this._npc.getDmgup();
        if (this.isUndeadDamage()) {
            dmg *= 1.2;
        }
        dmg = (double) ((int) (dmg * ((double) this.getLeverage() / 10.0)));
        if (this._npc.isWeaponBreaked()) {
            dmg /= 2.0;
        }
        if (isUndeadDamage()) {// 不死系夜間增加攻擊力
            dmg *= 1.2D;
        }
        dmg = (int) (dmg * (getLeverage() / 10.0D));
        if (_npc.isWeaponBreaked()) {
            dmg /= 2.0D;
        }
        return dmg;
    }

    /**
     * 根據 SkillEnhanceTable 的設定4（dmg除的倍率）調整傷害值
     * 針對 IMMUNE_TO_HARM 技能，直接從 srcpc 取得技能等級
     * 技能強化
     *
     * @param dmg   原始傷害
     * @param srcpc 玩家物件，用來取得 IMMUNE_TO_HARM 的技能等級
     * @return 調整後的傷害
     */
    private double applySkillEnhanceDmgDivision(double dmg, L1PcInstance srcpc) {
        // 先檢查玩家是否有施放 IMMUNE_TO_HARM 的技能效果
        if (!srcpc.hasSkillEffect(L1SkillId.IMMUNE_TO_HARM)) {
            // 若玩家沒有施放該技能，則直接返回原始傷害
            return dmg;
        }
        int currentLevel = srcpc.getSkillLevel(L1SkillId.IMMUNE_TO_HARM);
        L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(L1SkillId.IMMUNE_TO_HARM, currentLevel);
        if (enhanceData != null) {
            double divisionMultiplier = enhanceData.getSetting4();
            // 避免除數為 0
            if (divisionMultiplier != 0) {
                dmg = dmg / divisionMultiplier;
            }
        }
        return dmg;
    }

    /**
     * NPC對PC傷害
     *
     */
    private int calcPcDamage() {
        if (_targetPc == null) {
            return 0;
        }

        if (dmg0(_targetPc)) {
            _isHit = false;
            return 0;
        }

        int lvl = _npc.getLevel();
        double dmg = 0.0D;

        // 早期備用計算（備註保留）
        //final Integer dmgStr = L1AttackList.STRD.get((int) _npc.getStr());
        //dmg = _random.nextInt(lvl) + (_npc.getStr() * 0.8) + dmgStr;

        // 怪物攻擊傷害 = 等級 + 力量
        dmg = lvl + _npc.getStr();

        // 如果是寵物，加上寵物武器傷害
        if ((_npc instanceof L1PetInstance)) {
            dmg += lvl / 7;
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();
        }

        // 怪物的額外傷害模組
        dmg = npcDmgMode(dmg);

        // 扣除玩家基本防禦力 (AC、等級防禦等)
        dmg -= calcPcDefense();

        // 扣除防具減傷 (原版先扣一次)
        dmg -= _targetPc.getDamageReductionByArmor();

        // 扣除娃娃對怪減傷 (只對怪物有效)
        dmg -= _targetPc.getdolldamageReductionByArmor();

        // 扣除PVE額外減傷 (VIP、娃娃、裝備特別針對怪物減傷)
        dmg -= _targetPc.getDamageReductionPVE(); // // 被怪攻擊時額外減免（PVE減免）

        // 技能減傷（魔法、技能加成）
        dmg = applySkillEnhanceDmgDivision(dmg, _targetPc);

        // 如果沒有負面狀態13，額外再扣一次防具減傷（原版寫法，不變動）
        if (!_targetPc.hasSkillEffect(L1SkillId.negativeId13)) {
            dmg -= _targetPc.getDamageReductionByArmor();
        }

        // 扣除隨機減傷（娃娃、飾品、道具可能觸發）
        dmg -= _targetPc.dmgDowe();

        // 血盟技能減傷（如果有加入血盟）
        if (_targetPc.getClanid() != 0) {
            dmg -= getDamageReductionByClan(_targetPc);
        }


        // 護甲身軀
        if (_targetPc.isWarrior() && _targetPc.isARMORGARDE()) {
            dmg += _targetPc.getAc() / 10;
        }
        // 神諭
        if (_targetPc.isWizard() && _targetPc.isOracle()) {
            dmg += _targetPc.getAc() / 10;
        }
        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            int targetPcLvl = Math.max(_targetPc.getLevel(), 50);
            dmg -= (targetPcLvl - 50) / 5 + 1;
        }
        if (_targetPc.isDragonKnight() && _targetPc.getReincarnationSkill()[2] > 0) { // 龍騎天賦技能強之護鎧
            dmg -= (int) (dmg / 100.0) * _targetPc.getReincarnationSkill()[2];
        }
        dmg -= _targetPc.getDmgR(); // 防禦加成 / 物理傷害減免

        //*龍騎增加  H 先生
        if (_targetPc.hasSkillEffect(L1SkillId.MORTAL_BODY)) {//致命身軀
            if (_random.nextInt(100) < ConfigSkillDragon.MORTAL_BODY_ROM) {// 23%機率觸發
                boolean isShortDistance = isShortDistance();
                if ((isShortDistance) && (_weaponType2 != 17)) {
                    int dmg2 = ConfigSkillDragon.MORTAL_BODY_DMG;
                    if (_npc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                        dmg2 /= 2;
                    }
                    _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), 2));
                    _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 10710));
                    _npc.receiveDamage(_targetPc, dmg2);
                }
                return 0;
            }
        }
        /** [原碼] 反叛者的盾牌 機率減免傷害 */
        for (L1ItemInstance item : _targetPc.getInventory().getItems()) {
            if (item.getItemId() == 400041 && item.isEquipped()) {
                Random random = new Random();
                int r = random.nextInt(100) + 1;
                if ((item.getEnchantLevel() * 2) >= r) {
                    dmg -= 50;
                    _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 6320));
                }
            }
        }
        boolean dmgX2 = false;// 傷害除2
        // 取回技能
        if (!_targetPc.getSkillisEmpty() && (_targetPc.getSkillEffect().size() > 0)) {
            try {
                for (final Integer key : _targetPc.getSkillEffect()) {
                    final Integer integer = L1AttackList.SKD3.get(key);
                    if (integer != null) {
                        if (integer.equals(key)) {
                            dmgX2 = true;
                        } else {
                            dmg += integer;
                        }
                    }
                }
            } catch (final ConcurrentModificationException e) {
                // 技能取回發生其他線程進行修改
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        if ((SubItemSet.START) && _weapon != null) {
            dmg = calcSubMagic(dmg, null, _targetPc);
        }
        if (dmgX2) {// 聖界減半
            dmg /= 2.0D;
        }
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
        if (castleId > 0) {
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        if (!isNowWar) {
            if ((_npc instanceof L1PetInstance)) {
                dmg /= 8.0D;
            }
            if ((_npc instanceof L1SummonInstance)) {
                L1SummonInstance summon = (L1SummonInstance) _npc;
                if (summon.isExsistMaster()) {
                    dmg /= 8.0D;
                }
            }
        }
        // TODO  戰士魔法
        int WW4RANDOM = 0;
        if (_targetPc.isWarrior() && _targetPc.getMeteLevel() >= 4) {
            WW4RANDOM = ConfigSkillWarrior.WHYW4;
        }
        int doll1 = 0;
        if (_targetPc.hasSkillEffect(5222)) {
            doll1 = 5;
        }
        //if (_targetPc.isWarrior() && (_targetPc.getCurrentHp() < ((_targetPc.getMaxHp() / 100) * 40// 血量低於40%
        if (_targetPc.isWarrior() && (_targetPc.getCurrentHp() < ((_targetPc.getMaxHp() / 100) * (ConfigSkillWarrior.Warrior_Magic + WW4RANDOM + doll1 + _targetPc.getRisingUp())// 血量低於40%
        )) && _targetPc.isCrystal()) {// 足夠魔法結晶體
            if (!isShortDistance()) {
                if (_targetPc.isTITANBULLET() && (_random.nextInt(100) < ConfigSkillWarrior.isPassive_Tatin_Bullet)) {
                    dmg = 0;
                    actionTitan(true);
                    commitTitan(_targetPc.colcTitanDmg());
                }
            } else {
                if (_targetPc.isTITANROCK() && (_random.nextInt(100) < ConfigSkillWarrior.isPassive_Tatin_Rock)) {
                    dmg = 0;
                    actionTitan(false);
                    commitTitan(_targetPc.colcTitanDmg());
                }
            }
        }
        if (_targetPc.isDarkelf() && _targetPc.getReincarnationSkill()[2] > 0 && RandomArrayList.getInc(100, 1) > 100 - _targetPc.getReincarnationSkill()[2]) { // 黑妖天賦技能神之跳躍
            _targetPc.sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(8013))); // 發動天賦技能 神之跳躍 閃避所有物理傷害。
            //_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 240));
            //_targetPc.broadcastPacketAll(new S_SkillSound(_targetPc.getId(), 240));
            dmg = 0.0D;
        }
        if (dmg <= 0.0D) {
            _isHit = false;
        }
        addNpcPoisonAttack(_targetPc);
        if (!_isHit) {
            dmg = 0.0D;
        }
        return (int) dmg;
    }

    private boolean isCloseRangeAttack() {
        return _npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) <= 1;
    }

    /**
     * NPC對NPC傷害
     *
     */
    private int calcNpcDamage() {
        if (_targetNpc == null) {
            return 0;
        }
        if (dmg0(_targetNpc)) {
            _isHit = false;
            return 0;
        }
        int lvl = _npc.getLevel();
        double dmg = 0.0D;
        if ((_npc instanceof L1PetInstance)) {
            dmg = _random.nextInt(_npc.getNpcTemplate().get_level()) + _npc.getStr() / 2 + 1;
            dmg += lvl / 14; // 每14級追加1點攻擊力 XXX
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();
        } else {
            final Integer dmgStr = L1AttackList.STRD.get((int) _npc.getStr());
            dmg = _random.nextInt(lvl) + (_npc.getStr() / 2) + dmgStr;
        }
        dmg = npcDmgMode(dmg);
        dmg -= calcNpcDamageReduction();
        addNpcPoisonAttack(_targetNpc);
        if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減半
            dmg /= 2.0D;
        }
        if (dmg <= 0.0D) {
            _isHit = false;
        }
        if (!_isHit) {
            dmg = 0.0D;
        }
        return (int) dmg;
    }

    /**
     * 夜間攻擊力增加
     *
     */
    private boolean isUndeadDamage() {
        boolean flag = false;
        int undead = _npc.getNpcTemplate().get_undead();
        boolean isNight = L1GameTimeClock.getInstance().currentTime().isNight();
        if (isNight) {
            switch (undead) {
                case 1:// 不死系
                case 3:// 殭屍系
                case 4:// 不死系(治療系無傷害/無法使用起死回生)
                    flag = true;
                    break;
            }
        }
        return flag;
    }

    /**
     * 毒素附加攻擊
     *
     */
    private void addNpcPoisonAttack(L1Character target) {
        switch (_npc.getNpcTemplate().get_poisonatk()) {
            case 1:// 出血毒
                if (15 >= _random.nextInt(100) + 1) {
                    L1DamagePoison.doInfection(_npc, target, 3000, 20);
                }
                break;
            case 2:// 沉默毒
                if (15 >= _random.nextInt(100) + 1) {
                    L1SilencePoison.doInfection(target);
                }
                break;
            case 4:// 麻痺毒
                if (15 >= _random.nextInt(100) + 1) {
                    L1ParalysisPoison.doInfection(target, 20000, 45000);
                }
                break;
        }
        if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // 麻痹攻击あり
        }
    }

    /**
     * 攻擊動作送出
     */
    public void action() {
        try {
            if (_npc == null) {
                return;
            }
            if (_npc.isDead()) {
                return;
            }
            _npc.setHeading(_npc.targetDirection(_targetX, _targetY));
            // 距離2格以上攻擊
            boolean isLongRange = _npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1;
            int bowActId = _npc.getBowActId();
            // 遠距離武器
            if ((isLongRange) && (bowActId > 0)) {
                actionX1();
                // 近距離武器
            } else {
                actionX2();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 遠距離攻擊
     */
    private void actionX1() {
        try {
            int bowActId = _npc.getBowActId();
            int actId = 1;// 預設攻擊動作
            if (_npc.getNpcId() == 95021) {
                actId = 21;
            }
            if (getActId() > 1) {// 有指定攻擊動作編號
                actId = getActId();
            }
            if (_isHit) {// 命中
                _npc.broadcastPacketAll(new S_UseArrowSkill(_npc, _targetId, bowActId, _targetX, _targetY, _damage, actId));
            } else {// 未命中
                _npc.broadcastPacketAll(new S_UseArrowSkill(_npc, bowActId, _targetX, _targetY, actId));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 近距離攻擊
     */
    private void actionX2() {
        try {
            int actId = 1;// 預設攻擊動作
            if (getActId() > 1) {// 有指定攻擊動作編號
                actId = getActId();
            }
            // 特定外型改變攻擊動作
            switch (_npc.getTempCharGfx()) {
                case 1780:// 烈炎獸
                case 7430:
                case 13076:
                    actId = 30;
                    break;
                case 2757:// 吸血鬼
                case 4104:
                case 13096:
                    actId = 18;
                    break;
            }
            if (_isHit) {// 命中
                if (getGfxId() > 0) {// 有動畫編號
                    _npc.broadcastPacketAll(new S_UseAttackSkill(_npc, _target.getId(), getGfxId(), _targetX, _targetY, actId, _damage));
                } else {// 沒有動畫編號
                    gfx7049();
                    _npc.broadcastPacketAll(new S_AttackPacketNpc(_npc, _target, actId, _damage));
                }
            } else if (getGfxId() > 0) {// 未命中 有動畫編號
                _npc.broadcastPacketAll(new S_UseAttackSkill(_target, _npc.getId(), getGfxId(), _targetX, _targetY, actId, 0));
            } else {// 未命中 沒動畫編號
                _npc.broadcastPacketAll(new S_AttackPacketNpc(_npc, _target, actId));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 幻術師外型 使用古奇獸
     */
    private void gfx7049() {
        if (_npc.getStatus() != 58) {
            return;
        }
        boolean is = false;
        if (_npc.getTempCharGfx() == 6671) {
            is = true;
        }
        if (_npc.getTempCharGfx() == 6650) {
            is = true;
        }
        if (is) {// 幻術師外型 使用古奇獸
            _npc.broadcastPacketAll(new S_SkillSound(_npc.getId(), 7049));
        }
    }

    /**
     * 傷害資訊送出
     */
    public void commit() {
        if (_isHit) {// 命中
            switch (_calcType) {
                case NPC_PC:
                    commitPc();
                    break;
                case NPC_NPC:
                    commitNpc();
                    break;
            }
        } else {// 未命中
            if (_calcType == NPC_PC) {
                _targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 13418));// MISS特效編號
            }
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
        String srcatk = _npc.getName();
        String tgatk = _targetPc.getName();
        String dmginfo = _isHit ? "傷害:" + _damage : "失敗";
        String hitinfo = " 命中:" + _hitRate + "% 剩餘hp:" + _targetPc.getCurrentHp();
        String x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;
        _targetPc.sendPackets(new S_ServerMessage(166, "受到NPC攻擊: " + x));
    }

    private void commitPc() {
        _targetPc.receiveDamage(_npc, _damage, false, false);
    }

    private void commitNpc() {

        _targetNpc.receiveDamage(_npc, _damage);
    }

    /**
     * 是否為近距離攻擊
     */
    public boolean isShortDistance() {
        boolean isShortDistance = true;
        boolean isLongRange = _npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1;
        int bowActId = _npc.getBowActId();
        if ((isLongRange) && (bowActId > 0)) {
            isShortDistance = false;
        }
        return isShortDistance;
    }

    /**
     * 受到反擊屏障傷害的處理
     */
    public void commitCounterBarrier() {
        int damage = calcCounterBarrierDamage();
        if (damage == 0) {
            return;
        }
        if (_npc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
            damage /= 2;
        }
        /*
         * if (damage >= _npc.getCurrentHp()) {// 如果傷害大於等於目前HP damage =
         * _npc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用反屏掛機) }
         */
        _npc.receiveDamage(_target, damage);
        _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), 2));
        _npc.broadcastPacketAll(new S_SkillSound(_target.getId(), 10710));
    }

    public int getHit() {
        return _hitRate;
    }
}
