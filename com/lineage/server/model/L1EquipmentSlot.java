package com.lineage.server.model;

import com.add.ArmorEnchantSystem;
import com.add.JiezEnchant;
import com.add.Tsai.NewEnchantSystem;
import com.lineage.data.ItemClass;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.data.item_weapon.proficiency.L1WeaponProficiency;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1EquipmentSlot {  //src039
    public static final Log _log = LogFactory.getLog(L1EquipmentSlot.class);
    private final L1PcInstance _owner;
    private final ArrayList<ArmorSet> _currentArmorSet;
    private final ArrayList<L1ItemInstance> _armors;
    private L1ItemInstance _weapon;

    public L1EquipmentSlot(L1PcInstance owner) {
        _owner = owner;
        _armors = new ArrayList<>();
        _currentArmorSet = new ArrayList<>();
    }

    public L1ItemInstance getWeapon() {
        return _weapon;
    }

    /**
     * 穿上武器的處理
     *
     * @param weapon 要穿上的武器
     */
    private void setWeapon(L1ItemInstance weapon) {
        // 處理祝福化能力（只有bless=0且模板bless!=0才觸發）
        if (weapon.getBless() == 0 && weapon.getItem().getBless() != 0) {
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(weapon.getItem().getItemId(), 1);
            if (zhufu != null) {
                L1Zhufu.getAddZhufu(_owner, weapon.getItem().getItemId(), 1);
            }
        }

        // 判斷是否為戰士副手武器
        weapon._isSecond = _owner.isWarrior() && _owner.getWeapon() != null;
        boolean SecondWeapon = weapon._isSecond;
        if (SecondWeapon) {
            _owner.setSecondWeapon(weapon);
            _owner.setCurrentWeapon(4); // 單手劍 （實際應該是88才對 by 聖子默默）
        } else {
            _owner.setWeapon(weapon);
            _owner.setCurrentWeapon(weapon.getItem().getType1());
        }

        // 啟動裝備計時器
        weapon.startEquipmentTimer(_owner);

        // 各種屬性加成
        L1Item item = weapon.getItem();
        int magicdmg = item.getMagicDmgModifier();
        _owner.addMagicDmgModifier(magicdmg);
        int addFire = item.get_defense_fire();
        _owner.addFire(addFire);
        int addWater = item.get_defense_water();
        _owner.addWater(addWater);
        int addWind = item.get_defense_wind();
        _owner.addWind(addWind);
        int addEarth = item.get_defense_earth();
        _owner.addEarth(addEarth);
        int add_regist_freeze = item.get_regist_freeze();
        _owner.add_regist_freeze(add_regist_freeze);
        int addRegistStone = item.get_regist_stone();
        _owner.addRegistStone(addRegistStone);
        int addRegistSleep = item.get_regist_sleep();
        _owner.addRegistSleep(addRegistSleep);
        int addRegistBlind = item.get_regist_blind();
        _owner.addRegistBlind(addRegistBlind);
        int addRegistStun = item.get_regist_stun();
        _owner.addRegistStun(addRegistStun);
        int addRegistSustain = item.get_regist_sustain();
        _owner.addRegistSustain(addRegistSustain);

        // 新附魔系統（你原本的邏輯）
        NewEnchantSystem NE_List2 = NewEnchantSystem.get().get2(
                _owner.getWeapon().getSafeEnchantLevel(),
                _owner.getWeapon().getEnchantLevel(),
                _owner.getWeapon().getItem().getType()
        );
        if (NE_List2 != null) {
            _owner.setNERan(NE_List2.getRan());
            _owner.setNEHit(NE_List2.getHit());
            _owner.setNEExtraDmg(NE_List2.getExtraDmg());
            _owner.setNEExtraMagicDmg(NE_List2.getExtraMagicDmg());
            _owner.setNECritDmg(NE_List2.getCritDmg());
            _owner.setNECritRate(NE_List2.getCritRate());
            _owner.setNECritGfx(NE_List2.getCritGfx());
            _owner.setNEExpUp(NE_List2.getExpUp());
            if (NE_List2.getHp() > 0) {
                _owner.addMaxHp(NE_List2.getHp());
            }
            if (NE_List2.getMp() > 0) {
                _owner.addMaxMp(NE_List2.getMp());
            }
            _owner.setNESkillId2(NE_List2.getSkillId());
            _owner.setNEGfx2(NE_List2.getGfx());
            _owner.setNETimeGfx2(NE_List2.getTimeGfx());
            if (NE_List2.getSkillId() > 0) {
                final L1SkillUse skillUse = new L1SkillUse();
                final L1Skills skill = SkillsTable.get().getTemplate(NE_List2.getSkillId());
                skillUse.handleCommands(_owner, NE_List2.getSkillId(), _owner.getId(), _owner.getX(), _owner.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
            }
            if (NE_List2.getTimeGfx() != 0 && !_owner.getSkillSound()) {
                _owner.startSkillSound();
            }
        }

        // ⚡️[重點] 武器熟練度初始化（這裡每次裝備都會做，不會漏掉！）
        if (weapon != null) {
            int type = weapon.getItem().getType();
            _owner.getProficiency().setProficiencyType(L1WeaponProficiency.normalizeWeaponType(type));
        }

        enforceCounterBarrierWeaponRestriction(weapon);
    }

    private void enforceCounterBarrierWeaponRestriction(final L1ItemInstance weapon) {
        if (!_owner.hasSkillEffect(COUNTER_BARRIER)) {
            return;
        }
        final boolean isTwoHandSword = weapon != null && weapon.getItem().getType() == 3;
        if (!isTwoHandSword) {
            removeCounterBarrierEffect();
        }
    }

    // Ensures both effect state and UI icon are cleared when Counter Barrier becomes invalid.
    private void removeCounterBarrierEffect() {
        _owner.removeSkillEffect(COUNTER_BARRIER);
        // 直接使用固定的 iconId=10187 來取消反擊屏障 BUFF ICON
        // 這樣可以確保即使資料庫未配置或配置錯誤，也能正確清除 BUFF 圖示
        _owner.sendPackets(new S_InventoryIcon(10187, false, 1088, 0));
    }


    public ArrayList<L1ItemInstance> getArmors() {
        return _armors;
    }

    /**
     * 脫下武器的處理
     *
     * @param weapon 要脫下的武器
     */
    private void removeWeapon(L1ItemInstance weapon) {
        //		NewEnchantSystem NE_List = NewEnchantSystem.get().get(_owner.getWeapon().getItemId(),
        //				_owner.getWeapon().getEnchantLevel());
        //		if (NE_List != null) {
        //			_owner.setNERan(-NE_List.getRan());
        //			_owner.setNEExtraDmg(-NE_List.getExtraDmg());
        //			_owner.setNEHit(-NE_List.getHit());
        //			_owner.setNEExtraMagicDmg(-NE_List.getExtraMagicDmg());
        //			_owner.setNECritDmg(-NE_List.getCritDmg());
        //			_owner.setNECritRate(-NE_List.getCritRate());
        //			_owner.setNECritGfx(-NE_List.getCritGfx());
        //			_owner.setNEExpUp(-NE_List.getExpUp());
        //			_owner.setNESkillId(0);
        //			_owner.setNEGfx(0);
        //			if (_owner.getNETimeGfx() != 0 && _owner.getSkillSound()) {
        //				_owner.stopSkillSound();
        //			}
        //			_owner.setNETimeGfx(0);
        //			if (NE_List.getHp() > 0) {
        //				_owner.addMaxHp(-NE_List.getHp());
        //			}
        //			if (NE_List.getMp() > 0) {
        //				_owner.addMaxMp(-NE_List.getMp());
        //			}
        //		}
        NewEnchantSystem NE_List2 = NewEnchantSystem.get().get2(_owner.getWeapon().getSafeEnchantLevel(), _owner.getWeapon().getEnchantLevel(), _owner.getWeapon().getItem().getType());
        if (/*NE_List == null &&*/ NE_List2 != null) {
            _owner.setNERan(-NE_List2.getRan());
            _owner.setNEExtraDmg(-NE_List2.getExtraDmg());
            _owner.setNEHit(-NE_List2.getHit());
            _owner.setNEExtraMagicDmg(-NE_List2.getExtraMagicDmg());
            _owner.setNECritDmg(-NE_List2.getCritDmg());
            _owner.setNECritRate(-NE_List2.getCritRate());
            _owner.setNECritGfx(-NE_List2.getCritGfx());
            _owner.setNEExpUp(-NE_List2.getExpUp());
            _owner.setNESkillId2(0);
            _owner.setNEGfx2(0);
            if (NE_List2.getHp() > 0) {
                _owner.addMaxHp(-NE_List2.getHp());
            }
            if (NE_List2.getMp() > 0) {
                _owner.addMaxMp(-NE_List2.getMp());
            }
            if (_owner.getNETimeGfx2() != 0 && _owner.getSkillSound()) {
                _owner.stopSkillSound();
            }
            _owner.setNETimeGfx2(0);
        }
        boolean Secondweapon = weapon._isSecond; // 是否為副武器
        if (Secondweapon) { // 副武器
            _owner.setSecondWeapon(null); // 副武器欄位設為空
            _owner.setCurrentWeapon(_owner.getWeapon().getItem().getType1()); // 更新主手外型
        } else { // 主武器
            if (_owner.getSecondWeapon() != null) { // 副手仍有武器
                _owner.getSecondWeapon()._isSecond = false;
                _owner.sendPackets(new S_EquipmentWindow(_owner.getWeapon().getId(), 8, false));
                _owner.setWeapon(_owner.getSecondWeapon());
                _owner.setSecondWeapon(null);
                _owner.sendPackets(new S_EquipmentWindow(_owner.getWeapon().getId(), 8, true));
                _owner.setCurrentWeapon(_owner.getWeapon().getItem().getType1());
                // 檢查新的主手武器是否符合反擊屏障的武器限制
                enforceCounterBarrierWeaponRestriction(_owner.getWeapon());
            } else { // 副手沒有武器
                _owner.setWeapon(null);
                _owner.setCurrentWeapon(0); // 設定為空手

                // 統一使用 removeCounterBarrierEffect 方法，確保同時移除效果與 BUFF ICON
                if (_owner.hasSkillEffect(COUNTER_BARRIER)) {
                    removeCounterBarrierEffect();
                }
                if (_owner.hasSkillEffect(FIRE_BLESS)) {
                    _owner.removeSkillEffect(FIRE_BLESS);
                }
            }
        }
        weapon._isSecond = false;
        weapon.stopEquipmentTimer(_owner);

        // 移除所有加成屬性
        L1Item item = weapon.getItem();
        int magicdmg = item.getMagicDmgModifier();
        _owner.addMagicDmgModifier(-magicdmg);
        int addFire = item.get_defense_fire();
        _owner.addFire(-addFire);
        int addWater = item.get_defense_water();
        _owner.addWater(-addWater);
        int addWind = item.get_defense_wind();
        _owner.addWind(-addWind);
        int addEarth = item.get_defense_earth();
        _owner.addEarth(-addEarth);
        int add_regist_freeze = item.get_regist_freeze();
        _owner.add_regist_freeze(-add_regist_freeze);
        int addRegistStone = item.get_regist_stone();
        _owner.addRegistStone(-addRegistStone);
        int addRegistSleep = item.get_regist_sleep();
        _owner.addRegistSleep(-addRegistSleep);
        int addRegistBlind = item.get_regist_blind();
        _owner.addRegistBlind(-addRegistBlind);
        int addRegistStun = item.get_regist_stun();
        _owner.addRegistStun(-addRegistStun);
        int addRegistSustain = item.get_regist_sustain();
        _owner.addRegistSustain(-addRegistSustain);

        int itemId = weapon.getItem().getItemId();
        
        // 處理祝福化能力移除（只有bless=0且模板bless!=0才移除）
        if (weapon.getBless() == 0 && weapon.getItem().getBless() != 0) {
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(itemId, 1);
            if (zhufu != null) {
                L1Zhufu.getRedzhufuhua(_owner, itemId, 1);
            }
        }

        // ⚡️【重點】移除武器熟練度加成
        int type = weapon.getItem().getType();
        _owner.getProficiency().removeProficiency(L1WeaponProficiency.normalizeWeaponType(type));
    }

    private void setArmor(L1ItemInstance armor) {
        L1Item item = armor.getItem();
        int itemId = armor.getItem().getItemId();
        int use_type = armor.getItem().getUseType();
        int apc = 0;
        switch (use_type) {
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
            case 70: // 脛甲
                _owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() + armor.get_durability());
                _owner.sendPackets(new S_OwnCharAttrDef(_owner));
                break;
            case 23:// 戒指
                if (item.get_ac() != 0) {
                    _owner.addAc(item.get_ac());
                }
                if (armor.getItem().get_greater() != 3) {
                    armor.greater(_owner, true);
                }
                JiezEnchant AE_List = JiezEnchant.get().get(item.getItemId(), armor.getEnchantLevel() - item.get_safeenchant());
                if (AE_List != null && item.get_safeenchant() >= 0/*安定值-1 沒效果*/) {
                    _owner.addStr(AE_List.getStr());
                    _owner.addDex(AE_List.getDex());
                    _owner.addCon(AE_List.getCon());
                    _owner.addInt(AE_List.getInt());
                    _owner.addWis(AE_List.getWis());
                    _owner.addCha(AE_List.getCha());
                    _owner.addAc(AE_List.getAc());
                    _owner.addMaxHp(AE_List.getHp());
                    _owner.addMaxMp(AE_List.getMp());
                    //_owner.setNEExpUp(AE_List.getExpUp());
                    _owner.addDamageReductionByArmor(AE_List.getDmgR());
				/*if (AE_List.getTimeGfx() != 0 && !_owner.isInAETimeGfx(AE_List.getTimeGfx())) {
					_owner.setAETimeGfx(AE_List.getTimeGfx());
					if (!_owner.getArmorSound()) {
						_owner.startArmorSound();
					}
				}*/
                }
                JiezEnchant AE_List2 = JiezEnchant.get().get2(armor.getEnchantLevel() - item.get_safeenchant());
                if (AE_List2 != null && item.get_safeenchant() >= 0/*安定值-1 沒效果*/) {
                    _owner.addStr(AE_List2.getStr());
                    _owner.addDex(AE_List2.getDex());
                    _owner.addCon(AE_List2.getCon());
                    _owner.addInt(AE_List2.getInt());
                    _owner.addWis(AE_List2.getWis());
                    _owner.addCha(AE_List2.getCha());
                    _owner.addAc(AE_List2.getAc());
                    _owner.addMaxHp(AE_List2.getHp());
                    _owner.addMaxMp(AE_List2.getMp());
                    //_owner.setNEExpUp(AE_List2.getExpUp());
                    _owner.addDamageReductionByArmor(AE_List2.getDmgR());
				/*if (AE_List2.getTimeGfx() != 0 && !_owner.isInAETimeGfx(AE_List2.getTimeGfx())) {
					_owner.setAETimeGfx(AE_List2.getTimeGfx());
					if (!_owner.getArmorSound()) {
						_owner.startArmorSound();
					}
				}*/
                }
                break;
            case 24:// 項鏈
            case 37:// 腰帶
            case 40:// 耳環
                if (item.get_ac() != 0) {
                    _owner.addAc(item.get_ac());
                }
                if (armor.getItem().get_greater() != 3) {
                    armor.greater(_owner, true);
                }
                break;
            case 43: // 符文道具左
            case 44: // 符文道具右
            case 45: // 符文道具中
            case 48: // 六芒星護身符
            case 49: // 蒂蜜特祝福系列
            case 51: // 蒂蜜特符文
            case 52: // vip
            case 53: // vip2
                if (item.get_ac() != 0) {
                    _owner.addAc(item.get_ac());
                }
                break;
        }
        set_time_item(armor);
        _owner.add_up_hp_potion(item.get_up_hp_potion());// 增加藥水回復量
        _owner.add_uhp_number(item.get_uhp_number());// 增加藥水回復指定量
        _owner.setPvpDmg(item.getPvpDmg());// PVP傷害值
        _owner.setPvpDmg_R(item.getPvpDmg_R());// PVP傷害值減免
        //_owner.add_Critical(item.get_CriticalChance());// 近距離暴擊率
        //		_owner.add_Bow_Critical(item.get_Bow_CriticalChance());// 遠距離暴擊率
        //		_owner.add_Magic_Critical(item.get_Magic_CriticalChance());// 魔法暴擊率
        _owner.addDamageReductionByArmor(armor.getDamageReduction() + armor.getItemReductionDmg());// 增加傷害減免
        _owner.addWeightReduction(item.getWeightReduction());// 增加負重
        int hit = armor.getHitModifierByArmor();// 增加命中
        int dmg = item.getDmgModifierByArmor();// 增加攻擊力
        _owner.addHitModifierByArmor(hit);// 增加命中
        _owner.addDmgModifierByArmor(dmg);// 增加攻擊力
        _owner.addBowHitModifierByArmor(item.getBowHitModifierByArmor());// 增加遠距離命中
        _owner.addBowDmgModifierByArmor(item.getBowDmgModifierByArmor());// 增加遠距離攻擊力
        _owner.addOriginalMagicHit(item.getMagicHitModifierByArmor());// 增加魔法命中
        _owner.set_dmgAdd(item.getDmg(), item.get_dice_dmg());// 機率給予爆擊質
        // 潘朵拉的6種香水
        //		if (armor.get_pandora_type() > 0) {
        //			armor.set_pandora_buff(_owner, true);
        //		}
        //		// 潘朵拉的9種紋樣
        //		if (armor.get_pandora_mark() > 0) {
        //			armor.set_pandora_markbuff(_owner, true);
        //		}
        int addFire = item.get_defense_fire();
        _owner.addFire(addFire);
        int addWater = item.get_defense_water();
        _owner.addWater(addWater);
        int addWind = item.get_defense_wind();
        _owner.addWind(addWind);
        int addEarth = item.get_defense_earth();
        _owner.addEarth(addEarth);
        int add_regist_freeze = item.get_regist_freeze();
        _owner.add_regist_freeze(add_regist_freeze);
        int addRegistStone = item.get_regist_stone();
        _owner.addRegistStone(addRegistStone);
        int addRegistSleep = item.get_regist_sleep();
        _owner.addRegistSleep(addRegistSleep);
        int addRegistBlind = item.get_regist_blind();
        _owner.addRegistBlind(addRegistBlind);
        int addRegistStun = item.get_regist_stun();
        _owner.addRegistStun(addRegistStun);
        int addRegistSustain = item.get_regist_sustain();
        _owner.addRegistSustain(addRegistSustain);
        final int addKitType = item.get_kitType();
        this._owner.addArmorKit(addKitType);

        if (item instanceof L1Armor) {
            L1Armor l1armor = (L1Armor) item;
            if (l1armor.get_influence_safe() != -1) {
                int enchantDiff = armor.getEnchantLevel() - l1armor.get_safeenchant();
                if (enchantDiff > 0) {
                    _owner.addStr(l1armor.get_influence_str() * enchantDiff);
                    _owner.addDex(l1armor.get_influence_dex() * enchantDiff);
                    _owner.addCon(l1armor.get_influence_con() * enchantDiff);
                    _owner.addInt(l1armor.get_influence_int() * enchantDiff);
                    _owner.addWis(l1armor.get_influence_wis() * enchantDiff);
                    _owner.addCha(l1armor.get_influence_cha() * enchantDiff);
                    _owner.addSp(l1armor.get_influence_sp() * enchantDiff);
                    _owner.addMr(l1armor.get_influence_mr() * enchantDiff);
                    _owner.addMaxHp(l1armor.get_influence_hp() * enchantDiff);
                    _owner.addMaxMp(l1armor.get_influence_mp() * enchantDiff);
                    _owner.addDamageReductionByArmor(l1armor.get_influence_dmgR() * enchantDiff);
                    _owner.addHitModifierByArmor(l1armor.get_influence_hitAndDmg() * enchantDiff);
                    _owner.addDmgModifierByArmor(l1armor.get_influence_hitAndDmg() * enchantDiff);
                    _owner.addBowHitModifierByArmor(l1armor.get_influence_bowHitAndDmg() * enchantDiff);
                    _owner.addBowDmgModifierByArmor(l1armor.get_influence_bowHitAndDmg() * enchantDiff);
                }
            }
        }

        switch (use_type) {
            case 2:// 盔甲
            case 22:// 頭盔
            case 19:// 斗篷
            case 18:// T恤
            case 20:// 手套
            case 21:// 靴
            case 25:// 盾牌
            case 47:// 脛甲
            case 70:// 脛甲（修復：添加 case 70）
			/*int es = armor.getEnchantLevel() - item.get_safeenchant();
			if (es < 0) {
				es = 0;
			}*/
                int es = armor.getEnchantLevel() - item.get_safeenchant();
                if (es < 0) {
                    es = 0;
                }
                //			ArmorEnchantSystem AE_List = ArmorEnchantSystem.get().get(item.getItemId(), armor.getEnchantLevel() - item.get_safeenchant());
                //			if (AE_List != null  && item.get_safeenchant() >= 0/**安定值-1 沒效果*/) {
                //				_owner.addStr(AE_List.getStr());
                //				_owner.addDex(AE_List.getDex());
                //				_owner.addCon(AE_List.getCon());
                //				_owner.addInt(AE_List.getInt());
                //				_owner.addWis(AE_List.getWis());
                //				_owner.addCha(AE_List.getCha());
                //				_owner.addAc(AE_List.getAc());
                //				_owner.addMaxHp(AE_List.getHp());
                //				_owner.addMaxMp(AE_List.getMp());
                //				//_owner.setNEExpUp(AE_List.getExpUp());
                //				_owner.addDamageReductionByArmor(AE_List.getDmgR());
                //				/*if (AE_List.getTimeGfx() != 0 && !_owner.isInAETimeGfx(AE_List.getTimeGfx())) {
                //					_owner.setAETimeGfx(AE_List.getTimeGfx());
                //					if (!_owner.getArmorSound()) {
                //						_owner.startArmorSound();
                //					}
                //				}*/
                //			}
                ArmorEnchantSystem AE_List2 = ArmorEnchantSystem.get().get2(armor.getSafeEnchantLevel(), armor.getEnchantLevel());
                if (AE_List2 != null && item.get_safeenchant() >= 0/**安定值-1 沒效果*/) {
                    _owner.addStr(AE_List2.getStr());
                    _owner.addDex(AE_List2.getDex());
                    _owner.addCon(AE_List2.getCon());
                    _owner.addInt(AE_List2.getInt());
                    _owner.addWis(AE_List2.getWis());
                    _owner.addCha(AE_List2.getCha());
                    _owner.addAc(AE_List2.getAc());
                    _owner.addMaxHp(AE_List2.getHp());
                    _owner.addMaxMp(AE_List2.getMp());
                    //_owner.setNEExpUp(AE_List2.getExpUp());
                    _owner.addDamageReductionByArmor(AE_List2.getDmgR());
				/*if (AE_List2.getTimeGfx() != 0 && !_owner.isInAETimeGfx(AE_List2.getTimeGfx())) {
					_owner.setAETimeGfx(AE_List2.getTimeGfx());
					if (!_owner.getArmorSound()) {
						_owner.startArmorSound();
					}
				}*/
                }
                break;
        }
        _armors.add(armor);
        for (Integer key : ArmorSet.getAllSet().keySet()) {
            ArmorSet armorSet = (ArmorSet) ArmorSet.getAllSet().get(key);
            if ((armorSet.isPartOfSet(itemId)) && (armorSet.isValid(_owner))) {
                // 檢查套裝是否已經激活，避免重複處理
                if (!_currentArmorSet.contains(armorSet)) {
                    if (armor.getItem().getUseType() == 23) {// 戒指
                        if (!armorSet.isEquippedRingOfArmorSet(_owner)) {// 沒有裝備相同套裝戒指兩個以上
                            armorSet.giveEffect(_owner);// 給予套裝完成效果
                            _currentArmorSet.add(armorSet);
                            _owner.getInventory().setPartMode(armorSet, true);
                        }
                    } else {// 其他裝備
                        armorSet.giveEffect(_owner);// 給予套裝完成效果
                        _currentArmorSet.add(armorSet);
                        _owner.getInventory().setPartMode(armorSet, true);
                    }
                }
            }
        }
        // 處理祝福化能力（只有bless=0且模板bless!=0才觸發）
        if (armor.getBless() == 0 && armor.getItem().getBless() != 0) {
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(itemId, 2);
            if (zhufu != null) {
                L1Zhufu.getAddZhufu(_owner, itemId, 2);
            }
        }

        armor.startEquipmentTimer(_owner);
    }

    private void removeArmor(L1ItemInstance armor) {
        L1Item item = armor.getItem();
        int itemId = armor.getItem().getItemId();
        /**暗黑系統 解除*/
        int apc = 0;
        if (armor.get_ItemAttrName2() != null) {//極品裝備同步穿戴屬性
            final L1ItemAttr attr = armor.get_ItemAttrName2();
            apc += attr.get_ac();
        }
        // 處理祝福化能力移除（只有bless=0且模板bless!=0才移除）
        if (armor.getBless() == 0 && armor.getItem().getBless() != 0) {
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(itemId, 2);
            if (zhufu != null) {
                L1Zhufu.getRedzhufuhua(_owner, itemId, 2);
            }
        }
        
        int use_type = armor.getItem().getUseType();
        switch (use_type) {
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
            case 70: // 脛甲
            {
                //			ArmorEnchantSystem AE_List = ArmorEnchantSystem.get().get(item.getItemId(), armor.getEnchantLevel() - item.get_safeenchant());
                //			if (AE_List != null && item.get_safeenchant() >= 0/**安定值-1 沒效果*/) {
                //				_owner.addStr(-AE_List.getStr());
                //				_owner.addDex(-AE_List.getDex());
                //				_owner.addCon(-AE_List.getCon());
                //				_owner.addInt(-AE_List.getInt());
                //				_owner.addWis(-AE_List.getWis());
                //				_owner.addCha(-AE_List.getCha());
                //				_owner.addAc(-AE_List.getAc());
                //				_owner.addMaxHp(-AE_List.getHp());
                //				_owner.addMaxMp(-AE_List.getMp());
                //				//_owner.setNEExpUp(-AE_List.getExpUp());
                //				_owner.addDamageReductionByArmor(-AE_List.getDmgR());
                //				/*if (AE_List.getTimeGfx() != 0 && _owner.isInAETimeGfx(AE_List.getTimeGfx())) {
                //					_owner.removeAETimeGfx(AE_List.getTimeGfx());
                //					if (_owner.InAETimeGfx() == null) {
                //						_owner.stopArmorSound();
                //					}
                //				}*/
                //			}
                ArmorEnchantSystem AE_List2 = ArmorEnchantSystem.get().get2(armor.getSafeEnchantLevel(), armor.getEnchantLevel());
                if (AE_List2 != null && item.get_safeenchant() >= 0/**安定值-1 沒效果*/) {
                    _owner.addStr(-AE_List2.getStr());
                    _owner.addDex(-AE_List2.getDex());
                    _owner.addCon(-AE_List2.getCon());
                    _owner.addInt(-AE_List2.getInt());
                    _owner.addWis(-AE_List2.getWis());
                    _owner.addCha(-AE_List2.getCha());
                    _owner.addAc(-AE_List2.getAc());
                    _owner.addMaxHp(-AE_List2.getHp());
                    _owner.addMaxMp(-AE_List2.getMp());
                    //_owner.setNEExpUp(-AE_List2.getExpUp());
                    _owner.addDamageReductionByArmor(-AE_List2.getDmgR());
				/*if (AE_List2.getTimeGfx() != 0 && _owner.isInAETimeGfx(AE_List2.getTimeGfx())) {
					_owner.removeAETimeGfx(AE_List2.getTimeGfx());
					if (_owner.InAETimeGfx() == null) {
						_owner.stopArmorSound();
					}
				}*/
                }
                //_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic()));
                _owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getAcByMagic() + armor.get_durability()));
                _owner.sendPackets(new S_OwnCharAttrDef(_owner));
            }
            break;
            case 23:// 戒指
                if (item.get_ac() != 0) {
                    _owner.addAc(-item.get_ac());
                }
                if (armor.getItem().get_greater() != 3) {
                    armor.greater(_owner, false);
                }
                JiezEnchant AE_List = JiezEnchant.get().get(item.getItemId(), armor.getEnchantLevel() - item.get_safeenchant());
                if (AE_List != null && item.get_safeenchant() >= 0/*安定值-1 沒效果*/) {
                    _owner.addStr(-AE_List.getStr());
                    _owner.addDex(-AE_List.getDex());
                    _owner.addCon(-AE_List.getCon());
                    _owner.addInt(-AE_List.getInt());
                    _owner.addWis(-AE_List.getWis());
                    _owner.addCha(-AE_List.getCha());
                    _owner.addAc(-AE_List.getAc());
                    _owner.addMaxHp(-AE_List.getHp());
                    _owner.addMaxMp(-AE_List.getMp());
                    _owner.addDamageReductionByArmor(-AE_List.getDmgR());
                }
                JiezEnchant AE_List2 = JiezEnchant.get().get2(armor.getEnchantLevel() - item.get_safeenchant());
                if (AE_List2 != null && item.get_safeenchant() >= 0/*安定值-1 沒效果*/) {
                    _owner.addStr(-AE_List2.getStr());
                    _owner.addDex(-AE_List2.getDex());
                    _owner.addCon(-AE_List2.getCon());
                    _owner.addInt(-AE_List2.getInt());
                    _owner.addWis(-AE_List2.getWis());
                    _owner.addCha(-AE_List2.getCha());
                    _owner.addAc(-AE_List2.getAc());
                    _owner.addMaxHp(-AE_List2.getHp());
                    _owner.addMaxMp(-AE_List2.getMp());
                    _owner.addDamageReductionByArmor(-AE_List2.getDmgR());
                }
                break;
            case 24:// 項鏈
            case 37:// 腰帶
            case 40:// 耳環
                if (item.get_ac() != 0) {
                    _owner.addAc(-item.get_ac());
                }
                if (armor.getItem().get_greater() != 3) {
                    armor.greater(_owner, false);
                }
                break;
            case 43: // 符文道具左
            case 44: // 符文道具右
            case 45: // 符文道具中
            case 48: // 六芒星護身符
            case 49: // 蒂蜜特祝福系列
            case 51: // 蒂蜜特符文
            case 52: // vip
            case 53: // vip2
                if (item.get_ac() != 0) {
                    _owner.addAc(-item.get_ac());
                }
                break;
        }
        _owner.add_up_hp_potion(-item.get_up_hp_potion());// 降低藥水回復量
        _owner.add_uhp_number(-item.get_uhp_number());// 降低藥水回復指定量
        _owner.setPvpDmg(-item.getPvpDmg());// PVP傷害值
        _owner.setPvpDmg_R(-item.getPvpDmg_R());// PVP傷害值減免
        //		_owner.add_Critical(-item.get_CriticalChance());// 近距離暴擊率
        //		_owner.add_Bow_Critical(-item.get_Bow_CriticalChance());// 遠距離暴擊率
        //		_owner.add_Magic_Critical(-item.get_Magic_CriticalChance());// 魔法暴擊率
        _owner.addDamageReductionByArmor(-armor.getDamageReduction() - armor.getItemReductionDmg());
        _owner.addWeightReduction(-item.getWeightReduction());
        int hit = armor.getHitModifierByArmor();
        int dmg = item.getDmgModifierByArmor();
        _owner.addHitModifierByArmor(-hit);
        _owner.addDmgModifierByArmor(-dmg);
        _owner.addBowHitModifierByArmor(-item.getBowHitModifierByArmor());
        _owner.addBowDmgModifierByArmor(-item.getBowDmgModifierByArmor());
        _owner.addOriginalMagicHit(-item.getMagicHitModifierByArmor());// 降低魔法命中
        _owner.set_dmgAdd(-item.getDmg(), -item.get_dice_dmg());// 機率給予爆擊質
        int addFire = item.get_defense_fire();
        _owner.addFire(-addFire);
        int addWater = item.get_defense_water();
        _owner.addWater(-addWater);
        int addWind = item.get_defense_wind();
        _owner.addWind(-addWind);
        int addEarth = item.get_defense_earth();
        _owner.addEarth(-addEarth);
        int add_regist_freeze = item.get_regist_freeze();
        _owner.add_regist_freeze(-add_regist_freeze);
        int addRegistStone = item.get_regist_stone();
        _owner.addRegistStone(-addRegistStone);
        int addRegistSleep = item.get_regist_sleep();
        _owner.addRegistSleep(-addRegistSleep);
        int addRegistBlind = item.get_regist_blind();
        _owner.addRegistBlind(-addRegistBlind);
        int addRegistStun = item.get_regist_stun();
        _owner.addRegistStun(-addRegistStun);
        int addRegistSustain = item.get_regist_sustain();
        _owner.addRegistSustain(-addRegistSustain);
        final int addKitType = item.get_kitType();
        this._owner.removeArmorKit(addKitType);

        if (item instanceof L1Armor) {
            L1Armor l1armor = (L1Armor) item;
            if (l1armor.get_influence_safe() != -1) {
                int enchantDiff = armor.getEnchantLevel() - l1armor.get_safeenchant();
                if (enchantDiff > 0) {
                    _owner.addStr(-(l1armor.get_influence_str() * enchantDiff));
                    _owner.addDex(-(l1armor.get_influence_dex() * enchantDiff));
                    _owner.addCon(-(l1armor.get_influence_con() * enchantDiff));
                    _owner.addInt(-(l1armor.get_influence_int() * enchantDiff));
                    _owner.addWis(-(l1armor.get_influence_wis() * enchantDiff));
                    _owner.addCha(-(l1armor.get_influence_cha() * enchantDiff));
                    _owner.addSp(-(l1armor.get_influence_sp() * enchantDiff));
                    _owner.addMr(-(l1armor.get_influence_mr() * enchantDiff));
                    _owner.addMaxHp(-(l1armor.get_influence_hp() * enchantDiff));
                    _owner.addMaxMp(-(l1armor.get_influence_mp() * enchantDiff));
                    _owner.addDamageReductionByArmor(-(l1armor.get_influence_dmgR() * enchantDiff));
                    _owner.addHitModifierByArmor(-(l1armor.get_influence_hitAndDmg() * enchantDiff));
                    _owner.addDmgModifierByArmor(-(l1armor.get_influence_hitAndDmg() * enchantDiff));
                    _owner.addBowHitModifierByArmor(-(l1armor.get_influence_bowHitAndDmg() * enchantDiff));
                    _owner.addBowDmgModifierByArmor(-(l1armor.get_influence_bowHitAndDmg() * enchantDiff));
                }
            }
        }

        for (Integer key : ArmorSet.getAllSet().keySet()) {
            ArmorSet armorSet = (ArmorSet) ArmorSet.getAllSet().get(key);
            if ((armorSet.isPartOfSet(itemId)) && (_currentArmorSet.contains(armorSet)) && (!armorSet.isValid(_owner))) {
                armorSet.cancelEffect(_owner);
                _currentArmorSet.remove(armorSet);
                _owner.getInventory().setPartMode(armorSet, false);
            }
        }
        armor.stopEquipmentTimer(_owner);
        _armors.remove(armor);
    }

    /**
     * 穿上道具的處理
     */
    public void set(L1ItemInstance eq) {
        L1Item item = eq.getItem();
        if (item.getType2() == 0) {
            return;
        }
        int addhp = eq.get_addhp();
        int addmp = eq.get_addmp();
        int get_addstr = item.get_addstr() + eq.getItemStr();
        int get_adddex = item.get_adddex() + eq.getItemDex();
        int get_addcon = item.get_addcon() + eq.getItemCon();
        int get_addwis = item.get_addwis() + eq.getItemWis();
        int get_addint = item.get_addint() + eq.getItemInt();
        int get_addcha = item.get_addcha() + eq.getItemCha();
        int addMr = 0;
        addMr += eq.getMr();
        if ((eq.getName().equals("$187")) && (_owner.isElf())) {
            addMr += 5;
        }
        int addSp = 0;
        addSp += eq.getSp() + eq.getItemSp();
        boolean isHasteItem = item.isHasteItem();
        if (isHasteItem) {
            _owner.addHasteItemEquipped(1);
            _owner.removeHasteSkillEffect();
            if (_owner.getMoveSpeed() != 1) {
                _owner.setMoveSpeed(1);
                _owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
                _owner.broadcastPacketAll(new S_SkillHaste(_owner.getId(), 1, 0));
            }
        }
        switch (item.getType2()) {
            case 1:
                setWeapon(eq);
                ItemClass.get().item_weapon(true, _owner, eq);
                break;
            case 2:
                setArmor(eq);
                setMagic(eq);
                ItemClass.get().item_armor(true, _owner, eq);
        }
        final int pvpdmg = item.getPvpDmg();// pvp攻擊
        if (pvpdmg != 0) {
            _owner.setPvpDmg(pvpdmg);
        }
        final int pvpdmg_r = item.getPvpDmg_R();// pvp減免
        if (pvpdmg_r != 0) {
            _owner.setPvpDmg_R(pvpdmg_r);
        }
        // 強化擴充能力
        final int updatestr = eq.getUpdateStr();// 力量
        if (updatestr != 0) {
            get_addstr += updatestr;
        }
        final int updatedex = eq.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            get_adddex += updatedex;
        }
        final int updatecon = eq.getUpdateCon();// 體質
        if (updatecon != 0) {
            get_addcon += updatecon;
        }
        final int updatewis = eq.getUpdateWis();// 精神
        if (updatewis != 0) {
            get_addwis += updatewis;
        }
        final int updateint = eq.getUpdateInt();// 智力
        if (updateint != 0) {
            get_addint += updateint;
        }
        final int updatecha = eq.getUpdateCha();// 魅力
        if (updatecha != 0) {
            get_addcha += updatecha;
        }
        final int updatehp = eq.getUpdateHp();// 血量
        if (updatehp != 0) {
            _owner.addMaxHp(updatehp);
        }
        final int updatemp = eq.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _owner.addMaxMp(updatemp);
        }
        final int updateearth = eq.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _owner.addEarth(updateearth);
        }
        final int updatewind = eq.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _owner.addWind(updatewind);
        }
        final int updatewater = eq.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _owner.addWater(updatewater);
        }
        final int updatefire = eq.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _owner.addFire(updatefire);
        }
        final int updatemr = eq.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            addMr += updatemr;
        }
        final int updateac = eq.getUpdateAc();// 防禦
        if (updateac != 0) {
            _owner.addAc(-updateac);
        }
        final int updatesp = eq.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            addSp += updatesp;
        }
        final int updatehpr = eq.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _owner.addHpr(updatehpr);
        }
        final int updatempr = eq.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _owner.addMpr(updatempr);
        }
        final int updatedmg = eq.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _owner.addDmgModifierByArmor(updatedmg);
        }
        final int updatehit = eq.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _owner.addHitModifierByArmor(updatehit);
        }
        final int updatebowdmg = eq.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _owner.addBowDmgModifierByArmor(updatebowdmg);
        }
        final int updatebowhit = eq.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _owner.addBowHitModifierByArmor(updatebowhit);
        }
        final int updatepvpdmg = eq.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _owner.setPvpDmg(updatepvpdmg);
        }
        final int updatepvpdmg_r = eq.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _owner.setPvpDmg_R(updatepvpdmg_r);
        }
        // 強化擴充能力 end
        // 取回全部古文字組合 (fixed by terry0412)
        if (eq.get_power_name() != null && eq.get_power_name().get_power_name() != null) {
            for (final L1ItemPower_text values : ItemPowerTable.POWER_TEXT.values()) {
                if (values.check_pc(this._owner)) {
                    _owner.add_power(values);
                }
            }
        }
        if (eq.get_power_bless() != null) {
            L1ItemPower_bless power = eq.get_power_bless();
            final int stonePower[] = {power.get_hole_1(), power.get_hole_2(), power.get_hole_3(), power.get_hole_4(), power.get_hole_5()};
            for (int j : stonePower) {
                if (j != 0) {
                    final int id = j;
                    final L1StonePower stone_power = StonePowerTable.getInstance().get(id);
                    if (stone_power != null) {
                        final int ac = stone_power.getAc();
                        if (ac != 0) {
                            _owner.addAc(ac);
                        }
                        final int hp = stone_power.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = stone_power.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = stone_power.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(hpr);
                        }
                        final int mpr = stone_power.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(mpr);
                        }
                        final int str = stone_power.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = stone_power.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = stone_power.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = stone_power.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = stone_power.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = stone_power.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        final int sp = stone_power.getSp();
                        if (sp != 0) {
                            addSp += sp;
                        }
                        final int mr = stone_power.getMr();
                        if (mr != 0) {
                            addMr += mr;
                        }
                        final int hitModifer = stone_power.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(hitModifer);
                        }
                        final int dmgModifer = stone_power.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(dmgModifer);
                        }
                        final int bowHit = stone_power.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(bowHit);
                        }
                        final int bowDmg = stone_power.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(bowDmg);
                        }
                        final int magicDmg = stone_power.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(magicDmg);
                        }
                        final int magicReduction = stone_power.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(magicReduction);
                        }
                        final int reductionDmg = stone_power.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(reductionDmg);
                        }
                        final int fire = stone_power.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(fire);
                        }
                        final int water = stone_power.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(water);
                        }
                        final int wind = stone_power.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(wind);
                        }
                        final int earth = stone_power.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(earth);
                        }
                        final int registStun = stone_power.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(registStun);
                        }
                        final int registSustain = stone_power.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(registSustain);
                        }
                        final int registStone = stone_power.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(registStone);
                        }
                        final int registSleep = stone_power.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(registSleep);
                        }
                        final int registFreeze = stone_power.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(registFreeze);
                        }
                        final int registBlind = stone_power.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(registBlind);
                        }
                    }
                }
            }
            if (power.get_hole_str() != 0) {
                get_addstr += power.get_hole_str();
            }
            if (power.get_hole_dex() != 0) {
                get_adddex += power.get_hole_dex();
            }
            if (power.get_hole_int() != 0) {
                get_addint += power.get_hole_int();
            }
            if (power.get_hole_dmg() != 0) {
                _owner.addDmgModifierByArmor(power.get_hole_dmg());
            }
            if (power.get_hole_bowdmg() != 0) {
                _owner.addBowDmgModifierByArmor(power.get_hole_bowdmg());
            }
            if (power.get_hole_mcdmg() != 0) {
                _owner.addmcDmgModifierByArmor(power.get_hole_mcdmg());
            }
        }
        //SRC0907
        if (eq.get_power_name() != null) {
            final L1ItemPower_name power = eq.get_power_name();
            if (power.get_super_rune_4() == 1) {
                _owner.addCounterattack(80);
            }
            if (power.get_super_rune_4() == 2) {
                _owner.addBowcounterattack(80);
            }
            final int superRune[] = {power.get_super_rune_1(), power.get_super_rune_2(), power.get_super_rune_3()};
            for (int j : superRune) {
                if (j != 0) {
                    final int id = j;
                    final L1SuperRune super_rune = SuperRuneTable.getInstance().get(id);
                    if (super_rune != null) {
                        final int ac = super_rune.getAc();
                        if (ac != 0) {
                            _owner.addAc(ac);
                        }
                        final int hp = super_rune.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = super_rune.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = super_rune.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(hpr);
                        }
                        final int mpr = super_rune.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(mpr);
                        }
                        final int str = super_rune.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = super_rune.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = super_rune.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = super_rune.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = super_rune.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = super_rune.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        final int sp = super_rune.getSp();
                        if (sp != 0) {
                            addSp += sp;
                        }
                        final int mr = super_rune.getMr();
                        if (mr != 0) {
                            addMr += mr;
                        }
                        final int hitModifer = super_rune.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(hitModifer);
                        }
                        final int dmgModifer = super_rune.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(dmgModifer);
                        }
                        final int bowHit = super_rune.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(bowHit);
                        }
                        final int bowDmg = super_rune.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(bowDmg);
                        }
                        final int magicDmg = super_rune.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(magicDmg);
                        }
                        final int magicReduction = super_rune.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(magicReduction);
                        }
                        final int reductionDmg = super_rune.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(reductionDmg);
                        }
                        final int fire = super_rune.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(fire);
                        }
                        final int water = super_rune.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(water);
                        }
                        final int wind = super_rune.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(wind);
                        }
                        final int earth = super_rune.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(earth);
                        }
                        final int registStun = super_rune.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(registStun);
                        }
                        final int registSustain = super_rune.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(registSustain);
                        }
                        final int registStone = super_rune.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(registStone);
                        }
                        final int registSleep = super_rune.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(registSleep);
                        }
                        final int registFreeze = super_rune.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(registFreeze);
                        }
                        final int registBlind = super_rune.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(registBlind);
                        }
                    }
                }
            }
        }
        //SRC0907 END
        if (addhp != 0) {
            _owner.addMaxHp(addhp);
        }
        if (addmp != 0) {
            _owner.addMaxMp(addmp);
        }
        if (addMr != 0) {
            _owner.addMr(addMr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (addSp != 0) {
            _owner.addSp(addSp);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        int fearlevel = item.get_FearLevel();
        if (fearlevel != 0) {
            this._owner.add_FearLevel(fearlevel);
        }
        // 經驗加倍指數 (以%計算) by terry0412
        _owner.setExpPoint(_owner.getExpPoint() + item.getExpPoint());
        this._owner.addStr(get_addstr);// 力量
        this._owner.addDex(get_adddex);// 敏捷
        this._owner.addCon(get_addcon);// 體質
        this._owner.addWis(get_addwis);// 精神
        if (get_addwis != 0) {
            this._owner.resetBaseMr();
        }
        this._owner.addInt(get_addint);// 智力
        this._owner.addCha(get_addcha);// 魅力
        // 7.6
        if (get_addstr != 0 || get_adddex != 0 || get_addcon != 0 || get_addwis != 0 || get_addint != 0 || item.getWeightReduction() != 0) {
            _owner.sendDetails();
        }
    }

    public void setSecond(L1ItemInstance eq) {
        L1Item item = eq.getItem();
        if (item.getType2() == 0) {
            return;
        }
        int addhp = eq.get_addhp();
        if (addhp != 0) {
            _owner.addMaxHp(addhp);
        }
        int addmp = eq.get_addmp();
        if (addmp != 0) {
            _owner.addMaxMp(addmp);
        }
        int get_addstr = item.get_addstr();
        int get_adddex = item.get_adddex();
        int get_addcon = item.get_addcon();
        int get_addwis = item.get_addwis();
        int get_addint = item.get_addint();
        int get_addcha = item.get_addcha();
        int addMr = 0;
        addMr += eq.getMr();
        if ((eq.getName().equals("$187")) && (_owner.isElf())) {
            addMr += 5;
        }
        if (addMr != 0) {
            _owner.addMr(addMr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        int addSp = 0;
        addSp += eq.getSp();
        if (addSp != 0) {
            _owner.addSp(addSp);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        boolean isHasteItem = item.isHasteItem();
        if (isHasteItem) {
            _owner.addHasteItemEquipped(1);
            _owner.removeHasteSkillEffect();
            if (_owner.getMoveSpeed() != 1) {
                _owner.setMoveSpeed(1);
                _owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
                _owner.broadcastPacketAll(new S_SkillHaste(_owner.getId(), 1, 0));
            }
        }
        final int pvpdmg = item.getPvpDmg();// pvp攻擊
        if (pvpdmg != 0) {
            _owner.setPvpDmg(pvpdmg);
        }
        final int pvpdmg_r = item.getPvpDmg_R();// pvp減免
        if (pvpdmg_r != 0) {
            _owner.setPvpDmg_R(pvpdmg_r);
        }
        // 強化擴充能力
        final int updatestr = eq.getUpdateStr();// 力量
        if (updatestr != 0) {
            get_addstr += updatestr;
        }
        final int updatedex = eq.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            get_adddex += updatedex;
        }
        final int updatecon = eq.getUpdateCon();// 體質
        if (updatecon != 0) {
            get_addcon += updatecon;
        }
        final int updatewis = eq.getUpdateWis();// 精神
        if (updatewis != 0) {
            get_addwis += updatewis;
        }
        final int updateint = eq.getUpdateInt();// 智力
        if (updateint != 0) {
            get_addint += updateint;
        }
        final int updatecha = eq.getUpdateCha();// 魅力
        if (updatecha != 0) {
            get_addcha += updatecha;
        }
        final int updatehp = eq.getUpdateHp();// 血量
        if (updatehp != 0) {
            _owner.addMaxHp(updatehp);
        }
        final int updatemp = eq.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _owner.addMaxMp(updatemp);
        }
        final int updateearth = eq.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _owner.addEarth(updateearth);
        }
        final int updatewind = eq.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _owner.addWind(updatewind);
        }
        final int updatewater = eq.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _owner.addWater(updatewater);
        }
        final int updatefire = eq.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _owner.addFire(updatefire);
        }
        final int updatemr = eq.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            _owner.addMr(updatemr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        final int updateac = eq.getUpdateAc();// 防禦
        if (updateac != 0) {
            _owner.addAc(-updateac);
        }
        final int updatesp = eq.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            _owner.addSp(updatesp);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        final int updatehpr = eq.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _owner.addHpr(updatehpr);
        }
        final int updatempr = eq.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _owner.addMpr(updatempr);
        }
        final int updatedmg = eq.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _owner.addDmgModifierByArmor(updatedmg);
        }
        final int updatehit = eq.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _owner.addHitModifierByArmor(updatehit);
        }
        final int updatebowdmg = eq.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _owner.addBowDmgModifierByArmor(updatebowdmg);
        }
        final int updatebowhit = eq.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _owner.addBowHitModifierByArmor(updatebowhit);
        }
        final int updatepvpdmg = eq.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _owner.setPvpDmg(updatepvpdmg);
        }
        final int updatepvpdmg_r = eq.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _owner.setPvpDmg_R(updatepvpdmg_r);
        }
        // 強化擴充能力 end
        // 取回全部古文字組合 (fixed by terry0412)
        if (eq.get_power_name() != null && eq.get_power_name().get_power_name() != null) {
            for (final L1ItemPower_text values : ItemPowerTable.POWER_TEXT.values()) {
                if (values.check_pc(this._owner)) {
                    _owner.add_power(values);
                }
            }
        }
        if (eq.get_power_bless() != null) {
            L1ItemPower_bless power = eq.get_power_bless();
            final int stonePower[] = {power.get_hole_1(), power.get_hole_2(), power.get_hole_3(), power.get_hole_4(), power.get_hole_5()};
            for (int j : stonePower) {
                if (j != 0) {
                    final int id = j;
                    final L1StonePower stone_power = StonePowerTable.getInstance().get(id);
                    if (stone_power != null) {
                        final int ac = stone_power.getAc();
                        if (ac != 0) {
                            _owner.addAc(ac);
                        }
                        final int hp = stone_power.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = stone_power.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = stone_power.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(hpr);
                        }
                        final int mpr = stone_power.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(mpr);
                        }
                        final int str = stone_power.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = stone_power.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = stone_power.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = stone_power.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = stone_power.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = stone_power.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        final int sp = stone_power.getSp();
                        if (sp != 0) {
                            addSp += sp;
                        }
                        final int mr = stone_power.getMr();
                        if (mr != 0) {
                            addMr += mr;
                        }
                        final int hitModifer = stone_power.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(hitModifer);
                        }
                        final int dmgModifer = stone_power.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(dmgModifer);
                        }
                        final int bowHit = stone_power.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(bowHit);
                        }
                        final int bowDmg = stone_power.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(bowDmg);
                        }
                        final int magicDmg = stone_power.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(magicDmg);
                        }
                        final int magicReduction = stone_power.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(magicReduction);
                        }
                        final int reductionDmg = stone_power.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(reductionDmg);
                        }
                        final int fire = stone_power.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(fire);
                        }
                        final int water = stone_power.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(water);
                        }
                        final int wind = stone_power.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(wind);
                        }
                        final int earth = stone_power.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(earth);
                        }
                        final int registStun = stone_power.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(registStun);
                        }
                        final int registSustain = stone_power.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(registSustain);
                        }
                        final int registStone = stone_power.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(registStone);
                        }
                        final int registSleep = stone_power.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(registSleep);
                        }
                        final int registFreeze = stone_power.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(registFreeze);
                        }
                        final int registBlind = stone_power.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(registBlind);
                        }
                    }
                }
            }
            if (power.get_hole_str() != 0) {
                get_addstr += power.get_hole_str();
            }
            if (power.get_hole_dex() != 0) {
                get_adddex += power.get_hole_dex();
            }
            if (power.get_hole_int() != 0) {
                get_addint += power.get_hole_int();
            }
            if (power.get_hole_dmg() != 0) {
                _owner.addDmgModifierByArmor(power.get_hole_dmg());
            }
            if (power.get_hole_bowdmg() != 0) {
                _owner.addBowDmgModifierByArmor(power.get_hole_bowdmg());
            }
            if (power.get_hole_mcdmg() != 0) {
                _owner.addmcDmgModifierByArmor(power.get_hole_mcdmg());
            }
        }
        //SRC0907
        if (eq.get_power_name() != null) {
            final L1ItemPower_name power = eq.get_power_name();
            if (power.get_super_rune_4() == 1) {
                _owner.addCounterattack(80);
            }
            if (power.get_super_rune_4() == 2) {
                _owner.addBowcounterattack(80);
            }
            final int superRune[] = {power.get_super_rune_1(), power.get_super_rune_2(), power.get_super_rune_3()};
            for (int j : superRune) {
                if (j != 0) {
                    final int id = j;
                    final L1SuperRune super_rune = SuperRuneTable.getInstance().get(id);
                    if (super_rune != null) {
                        final int ac = super_rune.getAc();
                        if (ac != 0) {
                            _owner.addAc(ac);
                        }
                        final int hp = super_rune.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = super_rune.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = super_rune.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(hpr);
                        }
                        final int mpr = super_rune.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(mpr);
                        }
                        final int str = super_rune.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = super_rune.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = super_rune.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = super_rune.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = super_rune.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = super_rune.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        final int sp = super_rune.getSp();
                        if (sp != 0) {
                            addSp += sp;
                        }
                        final int mr = super_rune.getMr();
                        if (mr != 0) {
                            addMr += mr;
                        }
                        final int hitModifer = super_rune.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(hitModifer);
                        }
                        final int dmgModifer = super_rune.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(dmgModifer);
                        }
                        final int bowHit = super_rune.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(bowHit);
                        }
                        final int bowDmg = super_rune.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(bowDmg);
                        }
                        final int magicDmg = super_rune.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(magicDmg);
                        }
                        final int magicReduction = super_rune.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(magicReduction);
                        }
                        final int reductionDmg = super_rune.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(reductionDmg);
                        }
                        final int fire = super_rune.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(fire);
                        }
                        final int water = super_rune.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(water);
                        }
                        final int wind = super_rune.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(wind);
                        }
                        final int earth = super_rune.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(earth);
                        }
                        final int registStun = super_rune.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(registStun);
                        }
                        final int registSustain = super_rune.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(registSustain);
                        }
                        final int registStone = super_rune.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(registStone);
                        }
                        final int registSleep = super_rune.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(registSleep);
                        }
                        final int registFreeze = super_rune.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(registFreeze);
                        }
                        final int registBlind = super_rune.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(registBlind);
                        }
                    }
                }
            }
        }
        int fearlevel = item.get_FearLevel();
        if (fearlevel != 0) {
            this._owner.add_FearLevel(fearlevel);
        }
        //SRC0907 END
        // 經驗加倍指數 (以%計算) by terry0412
        _owner.setExpPoint(_owner.getExpPoint() + item.getExpPoint());
        this._owner.addStr(get_addstr);// 力量
        this._owner.addDex(get_adddex);// 敏捷
        this._owner.addCon(get_addcon);// 體質
        this._owner.addWis(get_addwis);// 精神
        if (get_addwis != 0) {
            this._owner.resetBaseMr();
        }
        this._owner.addInt(get_addint);// 智力
        this._owner.addCha(get_addcha);// 魅力
        // 7.6
        if (get_addstr != 0 || get_adddex != 0 || get_addcon != 0 || get_addwis != 0 || get_addint != 0 || item.getWeightReduction() != 0) {
            _owner.sendDetails();
        }
    }

    /**
     * 脫下道具的處理
     */
    public void remove(L1ItemInstance eq) {
        L1Item item = eq.getItem();
        if (item.getType2() == 0) {
            return;
        }
        int addhp = eq.get_addhp();
        int addmp = eq.get_addmp();
        int get_addstr = item.get_addstr() + eq.getItemStr();
        int get_adddex = item.get_adddex() + eq.getItemDex();
        int get_addcon = item.get_addcon() + eq.getItemCon();
        int get_addwis = item.get_addwis() + eq.getItemWis();
        int get_addint = item.get_addint() + eq.getItemInt();
        int get_addcha = item.get_addcha() + eq.getItemCha();
        int addMr = 0;
        addMr -= eq.getMr();
        if ((eq.getName().equals("$187")) && (_owner.isElf())) {
            addMr -= 5;
        }
        int addSp = 0;
        addSp -= eq.getSp();
        addSp -= eq.getItemSp();
        boolean isHasteItem = item.isHasteItem();
        if (isHasteItem) {
            _owner.addHasteItemEquipped(-1);
            if (_owner.getHasteItemEquipped() == 0) {
                _owner.setMoveSpeed(0);
                _owner.sendPacketsAll(new S_SkillHaste(_owner.getId(), 0, 0));
            }
        }
        switch (item.getType2()) {
            case 1:
                removeWeapon(eq);
                ItemClass.get().item_weapon(false, _owner, eq);
                break;
            case 2:
                removeMagic(_owner.getId(), eq);
                removeArmor(eq);
                ItemClass.get().item_armor(false, _owner, eq);
        }
        final int pvpdmg = item.getPvpDmg();// pvp攻擊
        if (pvpdmg != 0) {
            _owner.setPvpDmg(-pvpdmg);
        }
        final int pvpdmg_r = item.getPvpDmg_R();// pvp減免
        if (pvpdmg_r != 0) {
            _owner.setPvpDmg_R(-pvpdmg_r);
        }
        // 強化擴充能力
        final int updatestr = eq.getUpdateStr();// 力量
        if (updatestr != 0) {
            get_addstr += updatestr;
        }
        final int updatedex = eq.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            get_adddex += updatedex;
        }
        final int updatecon = eq.getUpdateCon();// 體質
        if (updatecon != 0) {
            get_addcon += updatecon;
        }
        final int updatewis = eq.getUpdateWis();// 精神
        if (updatewis != 0) {
            get_addwis += updatewis;
        }
        final int updateint = eq.getUpdateInt();// 智力
        if (updateint != 0) {
            get_addint += updateint;
        }
        final int updatecha = eq.getUpdateCha();// 魅力
        if (updatecha != 0) {
            get_addcha += updatecha;
        }
        final int updatehp = eq.getUpdateHp();// 血量
        if (updatehp != 0) {
            _owner.addMaxHp(-updatehp);
        }
        final int updatemp = eq.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _owner.addMaxMp(-updatemp);
        }
        final int updateearth = eq.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _owner.addEarth(-updateearth);
        }
        final int updatewind = eq.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _owner.addWind(-updatewind);
        }
        final int updatewater = eq.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _owner.addWater(-updatewater);
        }
        final int updatefire = eq.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _owner.addFire(-updatefire);
        }
        final int updatemr = eq.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            addMr -= updatemr;
        }
        final int updateac = eq.getUpdateAc();// 防禦
        if (updateac != 0) {
            _owner.addAc(updateac);
        }
        final int updatesp = eq.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            addSp -= updatesp;
        }
        final int updatehpr = eq.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _owner.addHpr(-updatehpr);
        }
        final int updatempr = eq.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _owner.addMpr(-updatempr);
        }
        final int updatedmg = eq.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _owner.addDmgModifierByArmor(-updatedmg);
        }
        final int updatehit = eq.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _owner.addHitModifierByArmor(-updatehit);
        }
        final int updatebowdmg = eq.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _owner.addBowDmgModifierByArmor(-updatebowdmg);
        }
        final int updatebowhit = eq.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _owner.addBowHitModifierByArmor(-updatebowhit);
        }
        final int updatepvpdmg = eq.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _owner.setPvpDmg(-updatepvpdmg);
        }
        final int updatepvpdmg_r = eq.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _owner.setPvpDmg_R(-updatepvpdmg_r);
        }
        // 強化擴充能力 end
        // 移除古文字效果 (fixed by terry0412)
        if (eq.get_power_name() != null && eq.get_power_name().get_power_name() != null) {
            for (L1ItemPower_text power : _owner.get_powers().values()) {
                if (!power.check_pc(_owner)) {
                    _owner.remove_power(power);
                }
            }
        }
        if (eq.get_power_bless() != null) {
            L1ItemPower_bless power = eq.get_power_bless();
            final int stonePower[] = {power.get_hole_1(), power.get_hole_2(), power.get_hole_3(), power.get_hole_4(), power.get_hole_5()};
            for (int j : stonePower) {
                if (j != 0) {
                    final int id = j;
                    final L1StonePower l1StonePower = StonePowerTable.getInstance().get(id);
                    if (l1StonePower != null) {
                        final int ac = l1StonePower.getAc();
                        if (ac != 0) {
                            _owner.addAc(-(ac));
                        }
                        final int hp = l1StonePower.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = l1StonePower.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = l1StonePower.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(-(hpr));
                        }
                        final int mpr = l1StonePower.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(-(mpr));
                        }
                        final int str = l1StonePower.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = l1StonePower.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = l1StonePower.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = l1StonePower.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = l1StonePower.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = l1StonePower.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        // SRC0909
                        final int sp = l1StonePower.getSp();
                        if (sp != 0) {
                            addSp += sp;
                        }
                        final int mr = l1StonePower.getMr();
                        if (mr != 0) {
                            addMr += mr;
                        }
                        // SRC0909  END
                        final int hitModifer = l1StonePower.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(-(hitModifer));
                        }
                        final int dmgModifer = l1StonePower.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(-(dmgModifer));
                        }
                        final int bowHit = l1StonePower.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(-(bowHit));
                        }
                        final int bowDmg = l1StonePower.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(-(bowDmg));
                        }
                        final int magicDmg = l1StonePower.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(-(magicDmg));
                        }
                        final int magicReduction = l1StonePower.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(-(magicReduction));
                        }
                        final int reductionDmg = l1StonePower.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(-(reductionDmg));
                        }
                        final int fire = l1StonePower.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(-(fire));
                        }
                        final int water = l1StonePower.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(-(water));
                        }
                        final int wind = l1StonePower.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(-(wind));
                        }
                        final int earth = l1StonePower.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(-(earth));
                        }
                        final int registStun = l1StonePower.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(-(registStun));
                        }
                        final int registSustain = l1StonePower.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(-(registSustain));
                        }
                        final int registStone = l1StonePower.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(-(registStone));
                        }
                        final int registSleep = l1StonePower.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(-(registSleep));
                        }
                        final int registFreeze = l1StonePower.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(-(registFreeze));
                        }
                        final int registBlind = l1StonePower.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(-(registBlind));
                        }
                    }
                }
            }
            if (power.get_hole_str() != 0) {
                get_addstr += power.get_hole_str();
            }
            if (power.get_hole_dex() != 0) {
                get_adddex += power.get_hole_dex();
            }
            if (power.get_hole_int() != 0) {
                get_addint += power.get_hole_int();
            }
            if (power.get_hole_dmg() != 0) {
                _owner.addDmgModifierByArmor(-power.get_hole_dmg());
            }
            if (power.get_hole_bowdmg() != 0) {
                _owner.addBowDmgModifierByArmor(-power.get_hole_bowdmg());
            }
            if (power.get_hole_mcdmg() != 0) {
                _owner.addmcDmgModifierByArmor(-power.get_hole_mcdmg());
            }
        }
        //SRC0907
        if (eq.get_power_name() != null) {
            final L1ItemPower_name power = eq.get_power_name();
            if (power.get_super_rune_4() == 1) {
                _owner.addCounterattack(-(80));
            }
            if (power.get_super_rune_4() == 2) {
                _owner.addBowcounterattack(-(80));
            }
            final int superRune[] = {power.get_super_rune_1(), power.get_super_rune_2(), power.get_super_rune_3()};
            for (int j : superRune) {
                if (j != 0) {
                    final int id = j;
                    final L1SuperRune super_rune_1 = SuperRuneTable.getInstance().get(id);
                    if (super_rune_1 != null) {
                        final int ac = super_rune_1.getAc();
                        if (ac != 0) {
                            _owner.addAc(-(ac));
                        }
                        final int hp = super_rune_1.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = super_rune_1.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = super_rune_1.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(-(hpr));
                        }
                        final int mpr = super_rune_1.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(-(mpr));
                        }
                        final int str = super_rune_1.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = super_rune_1.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = super_rune_1.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = super_rune_1.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = super_rune_1.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = super_rune_1.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        final int sp = super_rune_1.getSp();
                        if (sp != 0) {
                            addSp -= sp;
                        }
                        final int mr = super_rune_1.getMr();
                        if (mr != 0) {
                            addMr -= mr;
                        }
                        final int hitModifer = super_rune_1.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(-(hitModifer));
                        }
                        final int dmgModifer = super_rune_1.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(-(dmgModifer));
                        }
                        final int bowHit = super_rune_1.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(-(bowHit));
                        }
                        final int bowDmg = super_rune_1.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(-(bowDmg));
                        }
                        final int magicDmg = super_rune_1.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(-(magicDmg));
                        }
                        final int magicReduction = super_rune_1.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(-(magicReduction));
                        }
                        final int reductionDmg = super_rune_1.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(-(reductionDmg));
                        }
                        final int fire = super_rune_1.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(-(fire));
                        }
                        final int water = super_rune_1.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(-(water));
                        }
                        final int wind = super_rune_1.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(-(wind));
                        }
                        final int earth = super_rune_1.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(-(earth));
                        }
                        final int registStun = super_rune_1.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(-(registStun));
                        }
                        final int registSustain = super_rune_1.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(-(registSustain));
                        }
                        final int registStone = super_rune_1.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(-(registStone));
                        }
                        final int registSleep = super_rune_1.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(-(registSleep));
                        }
                        final int registFreeze = super_rune_1.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(-(registFreeze));
                        }
                        final int registBlind = super_rune_1.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(-(registBlind));
                        }
                    }
                }
            }
        }
        //SRC0907 END
        if (addhp != 0) {
            _owner.addMaxHp(-addhp);
        }
        if (addmp != 0) {
            _owner.addMaxMp(-addmp);
        }
        if (addMr != 0) {
            _owner.addMr(addMr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        if (addSp != 0) {
            _owner.addSp(addSp);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        int fearlevel = item.get_FearLevel();
        if (fearlevel != 0) {
            this._owner.add_FearLevel(-fearlevel);
        }
        // 經驗加倍指數 (以%計算) by terry0412
        _owner.setExpPoint(_owner.getExpPoint() - item.getExpPoint());
        this._owner.addStr((byte) -get_addstr);// 力量
        this._owner.addDex((byte) -get_adddex);// 敏捷
        this._owner.addCon((byte) -get_addcon);// 體質
        this._owner.addWis((byte) -get_addwis);// 精神
        if (get_addwis != 0) {
            this._owner.resetBaseMr();
        }
        this._owner.addInt((byte) -get_addint);// 智力
        this._owner.addCha((byte) -get_addcha);// 魅力
        //7.6
        if (get_addstr != 0 || get_adddex != 0 || get_addcon != 0 || get_addwis != 0 || get_addint != 0 || item.getWeightReduction() != 0) {
            _owner.sendDetails();
        }
    }

    /**
     * 脫下道具的處理
     */
    public void removeSecond(L1ItemInstance eq) {
        L1Item item = eq.getItem();
        if (item.getType2() == 0) {
            return;
        }
        int addhp = eq.get_addhp();
        if (addhp != 0) {
            _owner.addMaxHp(-addhp);
        }
        int addmp = eq.get_addmp();
        if (addmp != 0) {
            _owner.addMaxMp(-addmp);
        }
        int get_addstr = item.get_addstr();
        int get_adddex = item.get_adddex();
        int get_addcon = item.get_addcon();
        int get_addwis = item.get_addwis();
        int get_addint = item.get_addint();
        int get_addcha = item.get_addcha();
        int addMr = 0;
        addMr -= eq.getMr();
        if ((eq.getName().equals("$187")) && (_owner.isElf())) {
            addMr -= 5;
        }
        if (addMr != 0) {
            _owner.addMr(addMr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        int addSp = 0;
        addSp -= eq.getSp();
        if (addSp != 0) {
            _owner.addSp(addSp);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        boolean isHasteItem = item.isHasteItem();
        if (isHasteItem) {
            _owner.addHasteItemEquipped(-1);
            if (_owner.getHasteItemEquipped() == 0) {
                _owner.setMoveSpeed(0);
                _owner.sendPacketsAll(new S_SkillHaste(_owner.getId(), 0, 0));
            }
        }
        final int pvpdmg = item.getPvpDmg();// pvp攻擊
        if (pvpdmg != 0) {
            _owner.setPvpDmg(-pvpdmg);
        }
        final int pvpdmg_r = item.getPvpDmg_R();// pvp減免
        if (pvpdmg_r != 0) {
            _owner.setPvpDmg_R(-pvpdmg_r);
        }
        // 強化擴充能力
        final int updatestr = eq.getUpdateStr();// 力量
        if (updatestr != 0) {
            get_addstr += updatestr;
        }
        final int updatedex = eq.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            get_adddex += updatedex;
        }
        final int updatecon = eq.getUpdateCon();// 體質
        if (updatecon != 0) {
            get_addcon += updatecon;
        }
        final int updatewis = eq.getUpdateWis();// 精神
        if (updatewis != 0) {
            get_addwis += updatewis;
        }
        final int updateint = eq.getUpdateInt();// 智力
        if (updateint != 0) {
            get_addint += updateint;
        }
        final int updatecha = eq.getUpdateCha();// 魅力
        if (updatecha != 0) {
            get_addcha += updatecha;
        }
        final int updatehp = eq.getUpdateHp();// 血量
        if (updatehp != 0) {
            _owner.addMaxHp(-updatehp);
        }
        final int updatemp = eq.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _owner.addMaxMp(-updatemp);
        }
        final int updateearth = eq.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _owner.addEarth(-updateearth);
        }
        final int updatewind = eq.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _owner.addWind(-updatewind);
        }
        final int updatewater = eq.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _owner.addWater(-updatewater);
        }
        final int updatefire = eq.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _owner.addFire(-updatefire);
        }
        final int updatemr = eq.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            _owner.addMr(-updatemr);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        final int updateac = eq.getUpdateAc();// 防禦
        if (updateac != 0) {
            _owner.addAc(updateac);
        }
        final int updatesp = eq.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            _owner.addSp(-updatesp);
            _owner.sendPackets(new S_SPMR(_owner));
        }
        final int updatehpr = eq.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _owner.addHpr(-updatehpr);
        }
        final int updatempr = eq.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _owner.addMpr(-updatempr);
        }
        final int updatedmg = eq.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _owner.addDmgModifierByArmor(-updatedmg);
        }
        final int updatehit = eq.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _owner.addHitModifierByArmor(-updatehit);
        }
        final int updatebowdmg = eq.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _owner.addBowDmgModifierByArmor(-updatebowdmg);
        }
        final int updatebowhit = eq.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _owner.addBowHitModifierByArmor(-updatebowhit);
        }
        final int updatepvpdmg = eq.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _owner.setPvpDmg(-updatepvpdmg);
        }
        final int updatepvpdmg_r = eq.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _owner.setPvpDmg_R(-updatepvpdmg_r);
        }
        // 強化擴充能力 end
        if (eq.get_power_name() != null && eq.get_power_name().get_power_name() != null) {
            for (L1ItemPower_text power : _owner.get_powers().values()) {
                if (!power.check_pc(_owner)) {
                    _owner.remove_power(power);
                }
            }
        }
        //SRC0907
        if (eq.get_power_name() != null) {
            final L1ItemPower_name power = eq.get_power_name();
            if (power.get_super_rune_4() == 1) {
                _owner.addCounterattack(-(80));
            }
            if (power.get_super_rune_4() == 2) {
                _owner.addBowcounterattack(-(80));
            }
            final int superRune[] = {power.get_super_rune_1(), power.get_super_rune_2(), power.get_super_rune_3()};
            for (int j : superRune) {
                if (j != 0) {
                    final int id = j;
                    final L1SuperRune super_rune_1 = SuperRuneTable.getInstance().get(id);
                    if (super_rune_1 != null) {
                        final int ac = super_rune_1.getAc();
                        if (ac != 0) {
                            _owner.addAc(-(ac));
                        }
                        final int hp = super_rune_1.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = super_rune_1.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = super_rune_1.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(-(hpr));
                        }
                        final int mpr = super_rune_1.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(-(mpr));
                        }
                        final int str = super_rune_1.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = super_rune_1.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = super_rune_1.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = super_rune_1.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = super_rune_1.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = super_rune_1.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        final int sp = super_rune_1.getSp();
                        if (sp != 0) {
                            addSp -= sp;
                        }
                        final int mr = super_rune_1.getMr();
                        if (mr != 0) {
                            addMr -= mr;
                        }
                        final int hitModifer = super_rune_1.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(-(hitModifer));
                        }
                        final int dmgModifer = super_rune_1.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(-(dmgModifer));
                        }
                        final int bowHit = super_rune_1.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(-(bowHit));
                        }
                        final int bowDmg = super_rune_1.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(-(bowDmg));
                        }
                        final int magicDmg = super_rune_1.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(-(magicDmg));
                        }
                        final int magicReduction = super_rune_1.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(-(magicReduction));
                        }
                        final int reductionDmg = super_rune_1.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(-(reductionDmg));
                        }
                        final int fire = super_rune_1.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(-(fire));
                        }
                        final int water = super_rune_1.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(-(water));
                        }
                        final int wind = super_rune_1.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(-(wind));
                        }
                        final int earth = super_rune_1.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(-(earth));
                        }
                        final int registStun = super_rune_1.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(-(registStun));
                        }
                        final int registSustain = super_rune_1.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(-(registSustain));
                        }
                        final int registStone = super_rune_1.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(-(registStone));
                        }
                        final int registSleep = super_rune_1.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(-(registSleep));
                        }
                        final int registFreeze = super_rune_1.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(-(registFreeze));
                        }
                        final int registBlind = super_rune_1.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(-(registBlind));
                        }
                    }
                }
            }
        }
        //SRC0907 END
        if (eq.get_power_bless() != null) {
            L1ItemPower_bless power = eq.get_power_bless();
            final int stonePower[] = {power.get_hole_1(), power.get_hole_2(), power.get_hole_3(), power.get_hole_4(), power.get_hole_5()};
            for (int j : stonePower) {
                if (j != 0) {
                    final int id = j;
                    final L1StonePower l1StonePower = StonePowerTable.getInstance().get(id);
                    if (l1StonePower != null) {
                        final int ac = l1StonePower.getAc();
                        if (ac != 0) {
                            _owner.addAc(-(ac));
                        }
                        final int hp = l1StonePower.getHp();
                        if (hp != 0) {
                            addhp += hp;
                        }
                        final int mp = l1StonePower.getMp();
                        if (mp != 0) {
                            addmp += mp;
                        }
                        final int hpr = l1StonePower.getHpr();
                        if (hpr != 0) {
                            _owner.addHpr(-(hpr));
                        }
                        final int mpr = l1StonePower.getMpr();
                        if (mpr != 0) {
                            _owner.addMpr(-(mpr));
                        }
                        final int str = l1StonePower.getStr();
                        if (str != 0) {
                            get_addstr += str;
                        }
                        final int con = l1StonePower.getCon();
                        if (con != 0) {
                            get_addcon += con;
                        }
                        final int dex = l1StonePower.getDex();
                        if (dex != 0) {
                            get_adddex += dex;
                        }
                        final int wis = l1StonePower.getWis();
                        if (wis != 0) {
                            get_addwis += wis;
                        }
                        final int cha = l1StonePower.getCha();
                        if (cha != 0) {
                            get_addcha += cha;
                        }
                        final int inter = l1StonePower.getInt();
                        if (inter != 0) {
                            get_addint += inter;
                        }
                        // SRC0909
                        final int sp = l1StonePower.getSp();
                        if (sp != 0) {
                            addSp -= sp;
                        }
                        final int mr = l1StonePower.getMr();
                        if (mr != 0) {
                            addMr -= mr;
                        }
                        // SRC0909 END
                        final int hitModifer = l1StonePower.getHitModifier();
                        if (hitModifer != 0) {
                            _owner.addHitModifierByArmor(-(hitModifer));
                        }
                        final int dmgModifer = l1StonePower.getDmgModifier();
                        if (dmgModifer != 0) {
                            _owner.addDmgModifierByArmor(-(dmgModifer));
                        }
                        final int bowHit = l1StonePower.getBowHitModifier();
                        if (bowHit != 0) {
                            _owner.addBowHitModifierByArmor(-(bowHit));
                        }
                        final int bowDmg = l1StonePower.getBowDmgModifier();
                        if (bowDmg != 0) {
                            _owner.addBowDmgModifierByArmor(-(bowDmg));
                        }
                        final int magicDmg = l1StonePower.getMagicDmgModifier();
                        if (magicDmg != 0) {
                            _owner.addMagicDmgModifier(-(magicDmg));
                        }
                        final int magicReduction = l1StonePower.getMagicDmgReduction();
                        if (magicReduction != 0) {
                            _owner.addMagicDmgReduction(-(magicReduction));
                        }
                        final int reductionDmg = l1StonePower.getReductionDmg();
                        if (reductionDmg != 0) {
                            _owner.addDamageReductionByArmor(-(reductionDmg));
                        }
                        final int fire = l1StonePower.getDefenseFire();
                        if (fire != 0) {
                            _owner.addFire(-(fire));
                        }
                        final int water = l1StonePower.getDefenseWater();
                        if (water != 0) {
                            _owner.addWater(-(water));
                        }
                        final int wind = l1StonePower.getDefenseWind();
                        if (wind != 0) {
                            _owner.addWind(-(wind));
                        }
                        final int earth = l1StonePower.getDefenseEarth();
                        if (earth != 0) {
                            _owner.addEarth(-(earth));
                        }
                        final int registStun = l1StonePower.getRegistStun();
                        if (registStun != 0) {
                            _owner.addRegistStun(-(registStun));
                        }
                        final int registSustain = l1StonePower.getRegistSustain();
                        if (registSustain != 0) {
                            _owner.addRegistSustain(-(registSustain));
                        }
                        final int registStone = l1StonePower.getRegistStone();
                        if (registStone != 0) {
                            _owner.addRegistStone(-(registStone));
                        }
                        final int registSleep = l1StonePower.getRegistSleep();
                        if (registSleep != 0) {
                            _owner.addRegistSleep(-(registSleep));
                        }
                        final int registFreeze = l1StonePower.getRegistFreeze();
                        if (registFreeze != 0) {
                            _owner.add_regist_freeze(-(registFreeze));
                        }
                        final int registBlind = l1StonePower.getRegistBlind();
                        if (registBlind != 0) {
                            _owner.addRegistBlind(-(registBlind));
                        }
                    }
                }
            }
            if (power.get_hole_str() != 0) {
                get_addstr += power.get_hole_str();
            }
            if (power.get_hole_dex() != 0) {
                get_adddex += power.get_hole_dex();
            }
            if (power.get_hole_int() != 0) {
                get_addint += power.get_hole_int();
            }
            if (power.get_hole_dmg() != 0) {
                _owner.addDmgModifierByArmor(-power.get_hole_dmg());
            }
            if (power.get_hole_bowdmg() != 0) {
                _owner.addBowDmgModifierByArmor(-power.get_hole_bowdmg());
            }
            if (power.get_hole_mcdmg() != 0) {
                _owner.addmcDmgModifierByArmor(-power.get_hole_mcdmg());
            }
        }
        int fearlevel = item.get_FearLevel();
        if (fearlevel != 0) {
            this._owner.add_FearLevel(-fearlevel);
        }
        // 經驗加倍指數 (以%計算) by terry0412
        _owner.setExpPoint(_owner.getExpPoint() - item.getExpPoint());
        this._owner.addStr((byte) -get_addstr);// 力量
        this._owner.addDex((byte) -get_adddex);// 敏捷
        this._owner.addCon((byte) -get_addcon);// 體質
        this._owner.addWis((byte) -get_addwis);// 精神
        if (get_addwis != 0) {
            this._owner.resetBaseMr();
        }
        this._owner.addInt((byte) -get_addint);// 智力
        this._owner.addCha((byte) -get_addcha);// 魅力
        // 7.6
        if (get_addstr != 0 || get_adddex != 0 || get_addcon != 0 || get_addwis != 0 || get_addint != 0 || item.getWeightReduction() != 0) {
            _owner.sendDetails();
        }
    }

    /**
     * 穿上特殊魔法裝備
     *
     */
    private void setMagic(L1ItemInstance item) {
        switch (item.getItemId()) {
            case 402000:// 地靈守護之煉
            case 402001:// 水靈守護之煉
            case 402002:// 火靈守護之煉
            case 402003:// 風靈守護之煉
                if (!_owner.hasSkillEffect(FIRESTUN)) {
                    _owner.setSkillEffect(FIRESTUN, 0);
                }
                break;
            case 402004:// 真 地靈守護之煉
            case 402005:// 真 水靈守護之煉
            case 402006:// 真 火靈守護之煉
            case 402007:// 真 風靈守護之煉
                if (!_owner.hasSkillEffect(TRUEFIRESTUN)) {
                    _owner.setSkillEffect(TRUEFIRESTUN, 0);
                }
                break;
            case 70203:// 月亮力量項鏈
            case 70204:// 月亮敏捷項鏈
            case 70205:// 月亮智力項鏈
                if (!_owner.hasSkillEffect(MOONATTACK)) {
                    _owner.setSkillEffect(MOONATTACK, 0);
                }
                break;
            case 20062:// 炎魔的血光斗篷
            case 20077:// 隱身斗篷
            case 120077:// 隱身斗篷
                if (!_owner.hasSkillEffect(INVISIBILITY)) {
                    _owner.killSkillEffectTimer(BLIND_HIDING);
                    _owner.setSkillEffect(INVISIBILITY, 0);
                    _owner.sendPackets(new S_Invis(_owner.getId(), 1));
                    _owner.broadcastPacketAll(new S_RemoveObject(_owner));
                }
                break;
            case 20288:// 傳送控制戒指
            case 120288:
                _owner.sendPackets(new S_Ability(1, true));
                break;
            case 20281:// 變形控制戒指
            case 120281:
                _owner.sendPackets(new S_Ability(2, true));
                break;
            case 20284:// 召喚控制戒指
            case 120284:
                _owner.sendPackets(new S_Ability(5, true));
                break;
            case 20383:// 軍馬頭盔
                if (item.getChargeCount() != 0) {
                    item.setChargeCount(item.getChargeCount() - 1);
                    _owner.getInventory().updateItem(item, 128);
                }
                if (_owner.hasSkillEffect(1000)) {
                    _owner.killSkillEffectTimer(1000);
                    _owner.sendPacketsAll(new S_SkillBrave(_owner.getId(), 0, 0));
                    _owner.setBraveSpeed(0);
                }
                break;
            case 20013:// 敏捷魔法頭盔
                if (!_owner.isSkillMastery(26)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 26));
                }
                if (!_owner.isSkillMastery(43)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 43));
                }
                break;
            case 20014:// 治癒魔法頭盔
                if (!_owner.isSkillMastery(1)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 1));
                }
                if (!_owner.isSkillMastery(19)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 19));
                }
                break;
            case 20015:// 力量魔法頭盔
                if (!_owner.isSkillMastery(12)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 12));
                }
                if (!_owner.isSkillMastery(13)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 13));
                }
                if (!_owner.isSkillMastery(42)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 42));
                }
                break;
            case 20008:// 小型風之頭盔
                if (!_owner.isSkillMastery(43)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 43));
                }
                break;
            case 20023:// 風之頭盔
                if (!_owner.isSkillMastery(54)) {
                    _owner.sendPackets(new S_AddSkill(_owner, 54));
                }
                break;
        }
    }

    /**
     * 解除特殊魔法裝備
     *
     */
    private void removeMagic(int objectId, L1ItemInstance item) {
        switch (item.getItemId()) {
            case 402000:// 地靈守護之煉
            case 402001:// 水靈守護之煉
            case 402002:// 火靈守護之煉
            case 402003:// 風靈守護之煉
                if (_owner.hasSkillEffect(FIRESTUN)) {
                    _owner.removeSkillEffect(FIRESTUN);
                }
                break;
            case 402004:// 真 地靈守護之煉
            case 402005:// 真 水靈守護之煉
            case 402006:// 真 火靈守護之煉
            case 402007:// 真 風靈守護之煉
                if (_owner.hasSkillEffect(TRUEFIRESTUN)) {
                    _owner.removeSkillEffect(TRUEFIRESTUN);
                }
                break;
            case 70203:// 月亮力量項鏈
            case 70204:// 月亮敏捷項鏈
            case 70205:// 月亮智力項鏈
                if (_owner.hasSkillEffect(MOONATTACK)) {
                    _owner.removeSkillEffect(MOONATTACK);
                }
                break;
            case 20062:// 炎魔的血光斗篷
            case 20077:// 隱身斗篷
            case 120077:// 隱身斗篷
                _owner.delInvis();
                break;
            case 20288:// 傳送控制戒指
            case 120288:
                _owner.sendPackets(new S_Ability(1, false));
                break;
            case 20281:// 變形控制戒指
            case 120281:
                _owner.sendPackets(new S_Ability(2, false));
                break;
            case 20284:// 召喚控制戒指
            case 120284:
                _owner.sendPackets(new S_Ability(5, false));
                break;
            case 20383:// 軍馬頭盔
                break;
            case 20013:// 敏捷魔法頭盔
                if (!CharSkillReading.get().spellCheck(objectId, 26)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 26));
                }
                if (!CharSkillReading.get().spellCheck(objectId, 43)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 43));
                }
                break;
            case 20014:// 治癒魔法頭盔
                if (!CharSkillReading.get().spellCheck(objectId, 1)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 1));
                }
                if (!CharSkillReading.get().spellCheck(objectId, 19)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 19));
                }
                break;
            case 20015:// 力量魔法頭盔
                if (!CharSkillReading.get().spellCheck(objectId, 12)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 12));
                }
                if (!CharSkillReading.get().spellCheck(objectId, 13)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 13));
                }
                if (!CharSkillReading.get().spellCheck(objectId, 42)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 42));
                }
                break;
            case 20008:// 小型風之頭盔
                if (!CharSkillReading.get().spellCheck(objectId, 43)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 43));
                }
                break;
            case 20023:// 風之頭盔
                if (!CharSkillReading.get().spellCheck(objectId, 54)) {
                    _owner.sendPackets(new S_DelSkill(_owner, 54));
                }
                break;
        }
    }

    private void set_time_item(final L1ItemInstance item) {
        if (item.get_time() == null) {
            final L1ItemTime itemTime = ItemTimeTable.TIME.get(item.getItemId());
            if (itemTime != null && itemTime.is_equipped()) {
				/*final long upTime = System.currentTimeMillis() + itemTime.get_remain_time() * 60 * 1000;
				final Timestamp ts = new Timestamp(upTime);
				item.set_time(ts);*/
                long time = System.currentTimeMillis();// 目前時間豪秒
                long x1 = itemTime.get_remain_time() * 60L;// 指定分鐘耗用秒數
                long x2 = x1 * 1000;// 轉為豪秒
                long upTime = x2 + time;// 目前時間 加上指定天數耗用秒數
                // 時間數據
                final Timestamp ts = new Timestamp(upTime);
                item.set_time(ts);
                CharItemsTimeReading.get().addTime(item.getId(), ts);
                _owner.sendPackets(new S_ItemName(item));
                _owner.sendPackets(new S_ItemStatus(item)); // 更新 X年X月X日 以後自動刪除
            }
        }
    }
}
