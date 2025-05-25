package com.lineage.server.model;

import com.lineage.config.ConfigSkillKnight;
import com.lineage.data.event.SubItemSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class L1AttackMode {
    protected static final int PC_PC = 1;
    protected static final int PC_NPC = 2;
    protected static final int NPC_PC = 3;
    protected static final int NPC_NPC = 4;
    protected static final Random _random = new Random();
    private static final Log _log = LogFactory.getLog(L1AttackMode.class);
    // 目標物件
    protected L1Character _target;
    /**
     * 執行PC
     */
    protected L1PcInstance _pc;
    /**
     * 目標PC
     */
    protected L1PcInstance _targetPc;
    // 執行NPC
    protected L1NpcInstance _npc;
    // 目標NPC
    protected L1NpcInstance _targetNpc;
    protected int _targetId;
    protected int _targetX;
    protected int _targetY;
    protected int _statusDamage;
    protected int _hitRate;
    protected int _calcType;
    protected boolean _isHit;
    protected int _damage;
    protected int _drainMana;
    protected int _drainHp;
    protected int _attckGrfxId;
    protected int _attckActId;
    protected L1ItemInstance _weapon;
    protected int _weaponId;
    /**
     * <font color=#ff0000>[weapon]-武器類型</font><br>
     * sword: 4 <font color=#00800>劍</font><br>
     * dagger: 46 <font color=#00800>匕首</font><br>
     * tohandsword: 50 <font color=#00800>雙手劍</font><br>
     * bow: 20 <font color=#00800>弓</font><br>
     * blunt: 11 <font color=#00800>斧(單手)</font><br>
     * spear: 24 <font color=#00800>矛(雙手)</font><br>
     * staff: 40 <font color=#00800>魔杖</font><br>
     * throwingknife: 2922 <font color=#00800>■刀</font><br>
     * arrow: 66 <font color=#00800>箭</font><br>
     * gauntlet: 62 <font color=#00800>鐵手甲</font><br>
     * claw: 58 <font color=#00800>鋼爪</font><br>
     * edoryu: 54 <font color=#00800>雙刀</font><br>
     * singlebow: 20 <font color=#00800>弓(單手)</font><br>
     * singlespear: 24 <font color=#00800>矛(單手)</font><br>
     * tohandblunt: 11 <font color=#00800>雙手斧</font><br>
     * tohandstaff: 40 <font color=#00800>魔杖(雙手)</font><br>
     * kiringku: 58 <font color=#00800>奇古獸</font><br>
     * chainsword: 24 <font color=#00800>鎖鏈劍</font><br>
     * </p>
     */
    protected int _weaponType;
    protected int _weaponType2;
    protected int _weaponAddHit;
    protected int _weaponAddDmg;
    protected int _weaponSmall;
    protected int _weaponLarge;
    protected int _weaponRange = 1;
    protected int _weaponBless = 1;
    protected int _weaponEnchant;
    protected int _weaponMaterial;
    protected int _weaponDoubleDmgChance;
    protected int _weaponAttrEnchantKind;
    protected int _weaponAttrEnchantLevel;
    protected int _weaponboosEnchantKind = 0;
    protected int _weaponboosEnchantLevel = 0;
    protected L1ItemInstance _arrow;
    protected int _arrowGfxid = 66;
    protected L1ItemInstance _sting;
    protected int _stingGfxid = 2989;
    protected int _leverage = 10; // 攻擊倍率(1/10)

    /**
     * 血盟技能傷害增加
     *
     * @return
     */
    protected static double getDamageUpByClan(L1PcInstance pc) {
        double dmg = 0.0D;
        try {
            if (pc == null) {
                return 0.0D;
            }
            L1Clan clan = pc.getClan();
            if (clan == null) {
                return 0.0D;
            }
            if (clan.isClanskill()) {
                if (pc.get_other().get_clanskill() == 1) {
                    int clanMan = clan.getOnlineClanMemberSize50();
                    dmg += 0.25D * clanMan;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return dmg;
    }

    /**
     * 血盟技能傷害減免
     *
     * @param targetPc
     * @return
     */
    protected static double getDamageReductionByClan(L1PcInstance targetPc) {
        double dmg = 0.0D;
        try {
            if (targetPc == null) {
                return 0.0D;
            }
            L1Clan clan = targetPc.getClan();
            if (clan == null) {
                return 0.0D;
            }
            if (clan.isClanskill()) {
                if (targetPc.get_other().get_clanskill() == 2) {
                    int clanMan = clan.getOnlineClanMemberSize50();
                    dmg += 0.25D * clanMan;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return dmg;
    }

    /**
     * 身上有特定法術效果 傷害為0
     *
     * @param character
     * @return
     */
    protected static boolean dmg0(L1Character character) {
        try {
            if (character == null) {
                return false;
            }
            if (character.getSkillisEmpty()) {
                return false;
            }
            if (character.getSkillEffect().size() <= 0) {
                return false;
            }
            for (Integer key : character.getSkillEffect()) {
                final Integer integer = L1AttackList.SKM0.get(key);
                if (integer != null) {
                    return true;
                }
            }
        } catch (final ConcurrentModificationException e) {
            // 技能取回發生其他線程進行修改
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 技能增加閃避
     */
    protected static int attackerDice(L1Character character) {
        try {
            int attackerDice = 0;
            if (character.get_dodge() > 0) {
                attackerDice -= character.get_dodge();
            }
            if (character.get_dodge_down() > 0) {
                attackerDice += character.get_dodge_down();
            }
            return attackerDice;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 攻擊倍率(1/10)
     *
     * @return
     */
    protected int getLeverage() {
        return _leverage;
    }

    /**
     * 攻擊倍率(1/10)
     *
     * @param i
     */
    public void setLeverage(int i) {
        _leverage = i;
    }

    public int getActId() {
        return _attckActId;
    }

    public void setActId(int actId) {
        _attckActId = actId;
    }

    public int getGfxId() {
        return _attckGrfxId;
    }

    public void setGfxId(int gfxId) {
        _attckGrfxId = gfxId;
    }

    /**
     * 遠距離迴避率計算 亂數100
     */
    protected boolean calcErEvasion() {
        int er = _targetPc.getEr();
        int rnd = ThreadLocalRandom.current().nextInt(100) + 1;
        return er < rnd; // true:命中 false:未命中
    }

    /**
     * 完全閃避率計算 亂數1000
     */
    protected boolean calcEvasion() {
        if (_targetPc == null) {
            return false;
        }
        int ev = _targetPc.get_evasion();
        if (ev == 0) {
            return false;
        }
        int rnd = ThreadLocalRandom.current().nextInt(1000) + 1;
        if (rnd <= ev) {
            if (!_targetPc.getDolls().isEmpty()) {
                for (L1DollInstance doll : _targetPc.getDolls().values()) {
                    doll.show_action(2);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * PC防禦力傷害減低
     *
     * @return
     */
    protected int calcPcDefense() {
        try {
            if (_targetPc != null) {
                int ac = Math.max(0, 10 - _targetPc.getAc());
                int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
                if (acDefMax != 0) {
                    // (>> 1: 除) (<< 1: 乘) XXX
                    int srcacd = Math.max(1, acDefMax >> 3);
                    return ThreadLocalRandom.current().nextInt(acDefMax) + srcacd;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * (NPC防禦力 + 額外傷害減低) 傷害減低
     *
     * @return
     */
    protected int calcNpcDamageReduction() {
        int damagereduction = _targetNpc.getNpcTemplate().get_damagereduction(); // 額外傷害減低
        try {
            int srcac = _targetNpc.getAc();
            int ac = Math.max(0, 10 - srcac);
            int acDefMax = ac / 7; // 防禦力傷害減免降低1/7 XXX
            if (acDefMax != 0) {
                int srcacd = Math.max(1, acDefMax);
                return ThreadLocalRandom.current().nextInt(acDefMax) + srcacd + damagereduction;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return damagereduction;
    }

    /**
     * 計算反擊屏障傷害
     *
     * @return
     */
    protected int calcCounterBarrierDamage() {
        // 初始化傷害值為 0
        int damage = 0;
        try {
            // 檢查反擊目標是否為玩家角色 (PC)
            if (_targetPc != null) {
                // 獲取玩家角色裝備的武器
                final L1ItemInstance weapon = _targetPc.getWeapon();
                // 確保玩家裝備了武器
                if (weapon != null) {
                    // 檢查武器類型是否為雙手劍，類型代碼為 3
                    if (weapon.getItem().getType() == 3) { // 雙手劍
                        // 計算反擊傷害：
                        // 使用武器「大」傷害值 + 強化等級 + 追加傷害，再乘以反擊系數
                        // 註解中提到的公式：(BIG最大傷害 + 強化數 + 追加傷害)*2
                        // 其中 ConfigSkillKnight.Counterattack 通常為乘數 2
                        damage = (weapon.getItem().getDmgLarge()
                                + weapon.getEnchantLevel()
                                + weapon.getItem().getDmgModifier())
                                * ConfigSkillKnight.Counterattack;
                    }
                }
                // 如果反擊目標不是玩家角色而是 NPC
            } else if (_targetNpc != null) {
                // 計算 NPC 反擊傷害：
                // 使用 NPC 的力量 + 等級後再左移 1 位，相當於乘以 2
                damage = (_targetNpc.getStr() + _targetNpc.getLevel()) << 1;
            }
        } catch (Exception e) {
            // 捕獲並記錄可能出現的例外錯誤訊息，避免程序崩潰
            _log.error(e.getLocalizedMessage(), e);
        }
        // 返回計算後的反擊傷害值
        return damage;
    }

    public abstract boolean calcHit();

    public abstract int getHit();

    /**
     * 攻擊資訊送出
     */
    public abstract void action();

    /**
     * 傷害計算
     *
     * @return
     */
    public abstract int calcDamage();

    /**
     * 計算結果反映
     */
    public abstract void commit();

    /**
     * 攻擊使用武器是否為近距離武器判斷
     *
     * @return
     */
    public abstract boolean isShortDistance();

    /**
     * 反擊屏障的傷害反擊
     */
    public abstract void commitCounterBarrier();

    protected double calcSubMagic(final double dmg, final L1Character cha, final L1Character tgcha) {
        // 初始化新的傷害值 newdmg，預設為傳入的基本傷害 dmg
        double newdmg = dmg;
        // 檢查攻擊者 cha、角色 _pc 和目標 _target 是否均不為 null
        if (cha != null && _pc != null && _target != null) {
            // 遍歷玩家 _pc 的所有物品清單
            for (final L1ItemInstance item : _pc.getInventory().getItems()) {
                // 只有在傷害值大於 0 且物品已裝備時才進行以下處理
                if (newdmg > 0.0 && item.isEquipped()) {
                    // 如果 SubItemSet 啟用 且此物品具有靈魂轉化（Sublimation）效果
                    if (SubItemSet.START && item.getSublimation() != null) {
                        // 建立一個新的 L1AttackItemSub 實例，用於計算該物品對傷害的影響
                        final L1AttackItemSub sub = new L1AttackItemSub(_pc, _target, null, item.getSublimation());
                        // 調整傷害值 newdmg，根據物品效果增加傷害（或其他影響），這裡 false 可能表示不顯示額外訊息
                        newdmg = sub.add_dmg_forPc(newdmg, false);
                    }
                }
            }
            // 如果玩家 _pc 擁有召喚的娃娃，且新傷害值仍大於 0
            if (!_pc.getDolls().isEmpty() && newdmg > 0.0) {
                // 遍歷所有玩家 _pc 的娃娃實例
                for (final L1DollInstance doll : _pc.getDolls().values()) {
                    // 如果 SubItemSet 啟用 且此娃娃具有靈魂轉化效果
                    if (SubItemSet.START && doll.getInstance().getSublimation() != null) {
                        // 類似上面的過程，建立 L1AttackItemSub 來處理娃娃的靈魂轉化效果
                        final L1AttackItemSub sub = new L1AttackItemSub(_pc, _target, null, doll.getInstance().getSublimation());
                        // 調整傷害值 newdmg，基於娃娃的效果
                        newdmg = sub.add_dmg_forPc(newdmg, false);
                    }
                }
            }
        }

        // 檢查目標相關條件，確保 SubItemSet 開啟、目標角色 tgcha 不為 null 並且 _targetPc 已設定
        if (SubItemSet.START && tgcha != null && _targetPc != null) {
            // 遍歷目標角色 _targetPc 的所有裝備物品
            for (final L1ItemInstance item : _targetPc.getInventory().getItems()) {
                // 如果物品已裝備
                if (item.isEquipped()) {
                    // 且物品具有靈魂轉化效果
                    if (item.getSublimation() != null) {
                        L1AttackItemSub sub = null;
                        // 根據 _pc 是否存在來決定如何初始化 L1AttackItemSub
                        if (_pc != null) {
                            // 如果 _pc 存在，則創建 L1AttackItemSub，將 _pc 作為攻擊來源
                            sub = new L1AttackItemSub(null, _targetPc, _pc, item.getSublimation());
                        } else {
                            // 如果 _pc 不存在，則以 _npc 作為攻擊來源
                            sub = new L1AttackItemSub(null, _targetPc, _npc, item.getSublimation());
                        }
                        // 調整新傷害值 newdmg，考慮物品對目標的影響（此處將傷害轉為對目標的效果）
                        newdmg = sub.add_dmg_forTarget((int) newdmg, false);
                    }
                }
            }
            // 如果目標角色 _targetPc 擁有召喚的娃娃
            if (!_targetPc.getDolls().isEmpty()) {
                // 取得所有娃娃實例的集合
                final Collection<L1DollInstance> dollList = _targetPc.getDolls().values();
                // 遍歷每個娃娃
                for (final L1DollInstance doll : dollList) {
                    // 如果該娃娃具有靈魂轉化效果
                    if (doll.getInstance().getSublimation() != null) {
                        L1AttackItemSub sub = null;
                        // 根據 _pc 是否存在決定如何初始化 L1AttackItemSub
                        if (_pc != null) {
                            sub = new L1AttackItemSub(null, _targetPc, _pc, doll.getInstance().getSublimation());
                        } else {
                            sub = new L1AttackItemSub(null, _targetPc, _npc, doll.getInstance().getSublimation());
                        }
                        // 調整傷害值 newdmg，基於娃娃的靈魂轉化效果
                        newdmg = sub.add_dmg_forTarget(newdmg, false);
                    }
                }
            }
        }

        // 返回計算後的最終傷害值 newdmg
        return newdmg;
    }

    // TODO 戰士

    /**
     * 反映泰坦傷害
     *
     * @param dmg 傷害值
     */
    public void commitTitan(final int dmg) {
        if (dmg == 0) {
            return;
        }
        if (_calcType == PC_PC) {
            if ((_pc != null) && (_targetPc != null)) {
                _pc.receiveDamage(_targetPc, dmg, false, false);
            }
        } else if (_calcType == NPC_PC) {
            if ((_npc != null) && (_targetPc != null)) {
                _npc.receiveDamage(_targetPc, dmg);
            }
        }
    }

    /**
     * 泰坦行動
     *
     * @param check 是否觸發特定效果
     */
    public void actionTitan(final boolean check) {
        int gfxid = 12555;
        if (check) {
            gfxid = 12557;
        }
        if (_calcType == PC_PC) {
            _pc.setHeading(_pc.targetDirection(_targetX, _targetY));
            _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
            _pc.sendPacketsAll(new S_SkillSound(_targetId, gfxid));
        } else if (_calcType == NPC_PC) {
            _npc.setHeading(_npc.targetDirection(_targetX, _targetY));
            _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
            _npc.broadcastPacketAll(new S_SkillSound(_targetId, gfxid));
        }
    }
}
