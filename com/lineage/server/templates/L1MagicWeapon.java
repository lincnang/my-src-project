/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.templates;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1CurseParalysis;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1WeaponSkill;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.POWERGRIP;

/**
 * 武器魔法DIY系統(DB自製)
 *
 * @author terry0412
 */
public class L1MagicWeapon {
    private static final Random _random = new Random();
    private final int _item_id; // 魔法石道具編號
    private final String _skill_name; // 會顯示在武器上的魔法說明
    private final int _success_random; // 強化成功機率
    private final int _max_use_time; // 附加在武器上的魔法時間 (單位:秒)
    private final String _success_msg; // 加持成功 對話欄顯示
    private final String _failure_msg; // 加持失敗 對話欄顯示
    private final int _probability; // 效果發動機率
    private final boolean _isLongRange; // 是否對遠距武器(弓or鐵手甲) 發動率除以二
    private final int _fixDamage; // 固定傷害
    private final int _randomDamage; // 隨機傷害
    private final double _doubleDmgValue; // 暴擊倍率
    private final int _gfxId; // 發動特效編號
    private final boolean _gfxIdTarget; // 目標對像 (0 = 自身, 1 = 對方)
    private final List<int[]> _gfxIdOtherLoc; // 額外發動特效座標 (請輸入座標格式)
    private final int _area; // 幾格範圍傷害
    private final boolean _arrowType; // 飛行效果 (0 = 關閉, 1 = 開啟)
    private final int _effectId; // 額外技能skill編號
    private final int _effectTime; // 額外技能持續效果時間 (單位:秒)
    private final int _negativeId; // 新增額外負面技能類型(不受距離限制)
    private final int _negativeTime; // 新增額外負面技能時間(不受距離限制)
    private final int _attr; // 傷害附加屬性 (0:無, 1:地, 2:火, 4:水, 8:風, 16:光)
    private final int _hpAbsorb; // 吸血量
    private final int _mpAbsorb; // 吸魔量

    public L1MagicWeapon(final int item_id, final String skill_name, final int success_random, final int max_use_time, final String success_msg, final String failure_msg, final int probability, final boolean isLongRange, final int fixDamage, final int randomDamage, final double doubleDmgValue, final int gfxId, final boolean gfxIdTarget, final List<int[]> gfxIdOtherLoc, final int area, final boolean arrowType, final int effectId, final int effectTime, final int negativeId, final int negativeTime, final int attr, final int hpAbsorb, final int mpAbsorb) {
        _item_id = item_id;
        _skill_name = skill_name;
        _success_random = success_random;
        _max_use_time = max_use_time;
        _success_msg = success_msg;
        _failure_msg = failure_msg;
        _probability = probability;
        _isLongRange = isLongRange;
        _fixDamage = fixDamage;
        _randomDamage = randomDamage;
        _doubleDmgValue = doubleDmgValue;
        _gfxId = gfxId;
        _gfxIdTarget = gfxIdTarget;
        _gfxIdOtherLoc = gfxIdOtherLoc;
        _area = area;
        _arrowType = arrowType;
        _effectId = effectId;
        _effectTime = effectTime;
        _negativeId = negativeId;
        _negativeTime = negativeTime;
        _attr = attr;
        _hpAbsorb = hpAbsorb;
        _mpAbsorb = mpAbsorb;
    }

    /**
     * 附魔武器傷害判定
     *
     */
    public static double getWeaponSkillDamage(final L1PcInstance pc, final L1Character cha, final double damage, final L1MagicWeapon magicWeapon, final boolean isLongRange) {
        if (pc == null || cha == null || magicWeapon == null) {
            return 0;
        }
        // 隨機機率
        final int chance;
        // 是否對遠距武器(弓or鐵手甲) 發動率除以二
        if (isLongRange && magicWeapon._isLongRange) {
            chance = _random.nextInt(200);
        } else {
            chance = _random.nextInt(100);
        }
        // 發動機率 (近距)
        if (magicWeapon._probability < chance) {
            return 0;
        }
        // 戰爭期間 by terry0412
        boolean isNowWar = ServerWarExecutor.get().isNowWar();
        // 魔法特效編號
        final int gfxId = magicWeapon._gfxId;
        if (gfxId != 0) {
            int locX;
            int locY;
            int targetId;
            // 顯示特效於目標對像
            if (magicWeapon._gfxIdTarget) {
                locX = cha.getX();
                locY = cha.getY();
                targetId = cha.getId();
            } else {
                locX = pc.getX();
                locY = pc.getY();
                targetId = pc.getId();
            }
            // 發送特效
            sendGfxids(pc, magicWeapon, locX, // X座標
                    locY, // Y座標
                    targetId, gfxId, isNowWar);
            // 額外發動特效座標 (請輸入座標格式)
            if (magicWeapon._gfxIdOtherLoc != null && !magicWeapon._gfxIdOtherLoc.isEmpty()) {
                for (final int[] location : magicWeapon._gfxIdOtherLoc) {
                    // 發送特效
                    sendGfxids(pc, magicWeapon, locX + location[0], // X座標
                            locY + location[1], // Y座標
                            targetId, gfxId, isNowWar);
                }
            }
        }
        // 負面效果判斷
        int effectTime = magicWeapon._effectTime;
        if (effectTime > 0) {
            effectTime = effectTime * 1000;
        }
        // 修正為 直接對應技能編號 by terry0412
        final int effectId = magicWeapon._effectId;
        if (effectId > 0) {
            final L1Character target;
            if (magicWeapon._gfxIdTarget) {
                target = cha;
            } else {
                target = pc;
            }
            final L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, magicWeapon._effectId, target.getId(), target.getX(), target.getY(), magicWeapon._effectTime, L1SkillUse.TYPE_GMBUFF);
        }
        // 新增額外負面技能類型(不受距離限制)
        int negativeTime = magicWeapon._negativeTime;
        // 新增額外負面技能類型(不受距離限制)
        final int negativeId = magicWeapon._negativeId;
        if (negativeId > 0) {
            if (cha.hasSkillEffect(L1SkillId.COUNTER_MAGIC)) {
                cha.removeSkillEffect(L1SkillId.COUNTER_MAGIC);
                int castgfx2 = SkillsTable.get().getTemplate(L1SkillId.COUNTER_MAGIC).getCastGfx2();
                cha.broadcastPacketAll(new S_SkillSound(cha.getId(), castgfx2));
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) cha;
                    tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), castgfx2));
                }
                return negativeId;
            }
            switch (negativeId) {
                case 1: // 20闇盲咒術
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        if (player.hasSkillEffect(1012)) {
                            player.sendPackets(new S_CurseBlind(2));
                        } else {
                            player.sendPackets(new S_CurseBlind(1));
                        }
                    }
                    cha.setSkillEffect(L1SkillId.DARKNESS, negativeTime * 1000);
                    break;
                case 2: // 29緩速術
                    if (cha.getMoveSpeed() == 0) {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance player = (L1PcInstance) cha;
                            player.sendPackets(new S_SkillHaste(player.getId(), 2, negativeTime * 1000));
                        }
                        cha.broadcastPacketAll(new S_SkillHaste(cha.getId(), 2, negativeTime * 1000));
                        cha.setMoveSpeed(2);
                        cha.setSkillEffect(29, negativeTime * 1000);
                    } else if (cha.getMoveSpeed() == 1) {
                        int skillNum = 0;
                        if (cha.hasSkillEffect(43)) {
                            skillNum = 43;
                        } else if (cha.hasSkillEffect(54)) {
                            skillNum = 54;
                        } else if (cha.hasSkillEffect(1001)) {
                            skillNum = 1001;
                        }
                        if (skillNum != 0) {
                            cha.removeSkillEffect(skillNum);
                            cha.removeSkillEffect(29);
                            cha.setMoveSpeed(0);
                        }
                    } else if (cha.getMoveSpeed() == 2) {
                        cha.setSkillEffect(29, negativeTime * 1000);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 752));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 752));
                    break;
                case 3: // 33木乃伊的詛咒
                    if ((!cha.hasSkillEffect(157)) && (!cha.hasSkillEffect(50))) {
                        if ((cha instanceof L1PcInstance)) {
                            L1CurseParalysis.curse(cha, 5000, negativeTime * 1000, 1);
                        } else if ((cha instanceof L1MonsterInstance)) {
                            L1CurseParalysis.curse(cha, 5000, negativeTime * 1000, 0);
                        }
                        pc.sendPackets(new S_SkillSound(cha.getId(), 746));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 746));
                    }
                    break;
                case 4: // 39魔力奪取
                    int radMp = RandomArrayList.getInt(10) + 5;
                    int drainMana = radMp + (pc.getInt() / 2);
                    if (cha.getCurrentMp() < drainMana) {
                        drainMana = cha.getCurrentMp();
                        cha.setCurrentMp(0);
                    } else {
                        cha.setCurrentMp(cha.getCurrentMp() - drainMana);
                    }
                    if ((pc.getCurrentMp() + drainMana) > (int) pc.getMaxMp()) {
                        pc.setCurrentMp((int) pc.getMaxMp());
                    } else {
                        pc.setCurrentMp(pc.getCurrentMp() + drainMana);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 2171));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2171));
                    break;
                case 5: // 64魔法封印
                    if ((cha instanceof L1PcInstance)) {
                        cha.setSkillEffect(64, negativeTime * 1000);
                        pc.sendPackets(new S_SkillSound(cha.getId(), 2177));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2177));
                    }
                    break;
                case 6: // 71藥水霜化術
                    if ((cha instanceof L1PcInstance)) {
                        cha.setSkillEffect(71, negativeTime * 1000);
                        pc.sendPackets(new S_SkillSound(cha.getId(), 2232));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2232));
                    }
                    break;
                case 7: // 74流星雨
                    final L1SkillUse l1skilluse = new L1SkillUse();
                    l1skilluse.handleCommands(pc, 74, cha.getId(), cha.getX(), cha.getY(), negativeTime, L1SkillUse.TYPE_GMBUFF);
                    break;
                case 8: // 87衝擊之暈
                    if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(87))) {
                        negativeTime += cha.getSkillEffectTimeSec(87);
                    }
                    cha.setSkillEffect(87, negativeTime * 1000);
                    L1SpawnUtil.spawnEffect(81162, negativeTime, cha.getX(), cha.getY(), pc.getMapId(), pc, 0);
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.sendPackets(new S_Paralysis(5, true));
                        player.setParalyzed(true);  // 修復：設置玩家麻痹狀態
                    } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                        L1NpcInstance tgnpc = (L1NpcInstance) cha;
                        tgnpc.setParalyzed(true);
                    }
                    break;
                case 9: // 173污濁之水
                    if ((cha instanceof L1PcInstance)) {
                        cha.setSkillEffect(173, negativeTime * 1000);
                        pc.sendPackets(new S_SkillSound(cha.getId(), 5830));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 5830));
                    }
                    break;
                case 10: // 183護衛毀滅
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        if (player.hasSkillEffect(183)) {
                            player.removeSkillEffect(183);
                        }
                        player.setSkillEffect(183, negativeTime * 1000);
                        player.addAc(10);
                        player.sendPackets(new S_OwnCharStatus(player)); // 防禦更新
                        pc.sendPackets(new S_SkillSound(player.getId(), 6508));
                        pc.broadcastPacketAll(new S_SkillSound(player.getId(), 6508));
                    }
                    break;
                case 11: // 放逐-使對方無法攻擊
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.setSkillEffect(L1SkillId.ReiSkill_2, negativeTime * 1000);
                        pc.sendPackets(new S_SystemMessage("發動武器技能，使對手無法攻擊。"));
                        player.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 566));// 開啟圖示
                    }
                    break;
                case 12: // 禁錮-束縛對方
                    int grip = negativeTime;
                    if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(POWERGRIP)) {
                        grip += cha.getSkillEffectTimeSec(POWERGRIP);
                    }
                    if (grip > 6) {
                        grip = 6;
                    }
                    cha.setSkillEffect(POWERGRIP, grip * 1000);
                    L1SpawnUtil.spawnEffect(93004, grip, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance player = (L1PcInstance) cha;
                        player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_POWERGRIP, true));
                    } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                        final L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.setGripped(true);
                    }
                    break;
                case 13: // 破甲-使對方沒有任何減傷(PVP減傷減半)
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.setSkillEffect(L1SkillId.negativeId13, negativeTime * 1000);
                        pc.sendPackets(new S_SystemMessage("發動武器技能，使對方沒有任何減傷(PVP減傷減半)。"));
                        player.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 561));// 開啟圖示
                    }
                    break;
                case 14: // 滅魔-使對方失明(並扣除對方總魔力)
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        if (player.hasSkillEffect(1012)) {
                            player.sendPackets(new S_CurseBlind(2));
                        } else {
                            player.sendPackets(new S_CurseBlind(1));
                        }
                    }
                    cha.setSkillEffect(L1SkillId.DARKNESS, 5 * 1000);
                    //int drainMp = (cha.getCurrentMp() / 100) * negativeTime;
                    int drainMp = (cha.getMaxMp() / 100) * negativeTime;
                    if (cha.getCurrentMp() < drainMp) {
                        drainMp = cha.getCurrentMp();
                        cha.setCurrentMp(0);
                    } else {
                        cha.setCurrentMp(cha.getCurrentMp() - drainMp);
                    }
                    if ((pc.getCurrentMp() + drainMp) > (int) pc.getMaxMp()) {
                        pc.setCurrentMp((int) pc.getMaxMp());
                    } else {
                        pc.setCurrentMp(pc.getCurrentMp() + drainMp);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 2171));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2171));
                    break;
                case 15: // 虛弱-使對方攻擊減半
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.setSkillEffect(L1SkillId.negativeId15, negativeTime * 1000);
                        pc.sendPackets(new S_SystemMessage("發動武器技能，使對方攻擊減半。"));
                        player.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 488));// 開啟圖示
                    }
                    break;
                case 16: // 收割-使對方轉移最大生命給自己
                    int drainHp = (cha.getCurrentHp() / 100) * negativeTime;
                    //int drainHp = (cha.getMaxHp() / 100) * negativeTime;
                    if (cha.getCurrentHp() < drainHp) {
                        drainHp = cha.getCurrentHp();
                        cha.setCurrentHp(0);
                    } else {
                        cha.setCurrentHp(cha.getCurrentHp() - drainHp);
                    }
                    if ((pc.getCurrentHp() + drainHp) > (int) pc.getMaxHp()) {
                        pc.setCurrentHp((int) pc.getMaxHp());
                    } else {
                        pc.setCurrentHp(pc.getCurrentHp() + drainHp);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 236));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 236));
                    break;
            }
        }
        // 固定傷害 + 隨機傷害
        double fixDamage = magicWeapon._fixDamage;
        final int randomDamage = magicWeapon._randomDamage;
        if (randomDamage > 0) {
            fixDamage += _random.nextInt(randomDamage);
        }
        // 暴擊倍率 (只計算武器的物理總傷害)
        final double doubleDmgValue = magicWeapon._doubleDmgValue;
        if (doubleDmgValue > 0) {
            fixDamage += damage * doubleDmgValue;
        }
        // 吸取體力
        if (magicWeapon._hpAbsorb > 0) {
            // 不造成額外吸血傷害
            pc.setCurrentHp(pc.getCurrentHp() + magicWeapon._hpAbsorb);
        }
        // 吸取魔力
        if (magicWeapon._mpAbsorb > 0) {
            if (cha.getCurrentMp() > 0) {
                cha.setCurrentMp(Math.max(cha.getCurrentMp() - magicWeapon._mpAbsorb, 0));
                pc.setCurrentMp(pc.getCurrentMp() + magicWeapon._mpAbsorb);
            }
        }
        // 幾格範圍傷害 (範圍傷害 = 總傷害的50%)
        final int area = magicWeapon._area;
        if (area != 0) {
            for (final L1Object object : World.get().getVisibleObjects(cha, area)) {
                if (object == null) {
                    continue;
                }
                if (!(object instanceof L1Character)) {
                    continue;
                }
                if (object.getId() == pc.getId()) {
                    continue;
                }
                // 攻撃対象はL1Attackで処理するため除外
                if (object.getId() == cha.getId()) {
                    continue;
                }
                // 攻撃対象がMOBの場合は、範囲内のMOBにのみ当たる
                // 攻撃対象がPC,Summon,Petの場合は、範囲内のPC,Summon,Pet,MOBに当たる
                if (cha instanceof L1MonsterInstance) {
                    if (!(object instanceof L1MonsterInstance)) {
                        continue;
                    }
                }
                if ((cha instanceof L1PcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                    if (!((object instanceof L1PcInstance) || (object instanceof L1SummonInstance) || (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
                        continue;
                    }
                }
                double fixDamageArea = L1WeaponSkill.calcDamageReduction(pc, (L1Character) object, (fixDamage * 0.5), magicWeapon._attr);
                if (fixDamageArea <= 0) {
                    continue;
                }
                if (object instanceof L1PcInstance) {
                    final L1PcInstance targetPc = (L1PcInstance) object;
                    // 受傷動作
                    targetPc.sendPacketsX8(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
                    targetPc.receiveDamage(pc, (int) fixDamageArea, false, false);
                } else if ((object instanceof L1SummonInstance) || (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance)) {
                    final L1NpcInstance targetNpc = (L1NpcInstance) object;
                    // 受傷動作
                    targetNpc.broadcastPacketX8(new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
                    targetNpc.receiveDamage(pc, (int) fixDamageArea);
                }
            }
        }
        // 屬性減免傷害判定
        return L1WeaponSkill.calcDamageReduction(pc, cha, fixDamage, magicWeapon._attr);
    }

    /**
     * 發送特效到指定座標
     *
     */
    private static void sendGfxids(final L1PcInstance pc, final L1MagicWeapon magicWeapon, final int locX, final int locY, final int targetId, final int gfxId, final boolean isNowWar) {
        // 10格內畫面可見人物
        final ArrayList<L1PcInstance> pc_list = World.get().getVisiblePlayer(pc, 10);
        // 飛行效果
        if (magicWeapon._arrowType) {
            S_UseAttackSkill packet = new S_UseAttackSkill(pc, targetId, gfxId, locX, locY, ActionCodes.ACTION_Attack, false);
            pc.sendPackets(packet);
            // 非戰爭期間 by terry0412
            if (!isNowWar) {
                for (final L1PcInstance otherPc : pc_list) {
                    otherPc.sendPackets(packet);
                }
            }
        } else {
            S_EffectLocation packet = new S_EffectLocation(locX, locY, gfxId);
            pc.sendPackets(packet);
            // 非戰爭期間 by terry0412
            if (!isNowWar) {
                for (final L1PcInstance otherPc : pc_list) {
                    otherPc.sendPackets(packet);
                }
            }
        }
    }

    public final int getItemId() {
        return _item_id;
    }

    public final int geteffectTime() {
        return _effectTime;
    }

    /**
     * 新增額外負面技能類型(不受距離限制)
     *
     */
    public final int getNegativeId() {
        return _negativeId;
    }

    /**
     * 新增額外負面技能時間(不受距離限制)
     *
     */
    public final int getNegativeTime() {
        return _negativeTime;
    }

    public final String getSkillName() {
        return _skill_name;
    }

    public final int getSuccessRandom() {
        return _success_random;
    }

    public final int getMaxUseTime() {
        return _max_use_time;
    }

    public final String getSuccessMsg() {
        return _success_msg;
    }

    public final String getFailureMsg() {
        return _failure_msg;
    }
}
