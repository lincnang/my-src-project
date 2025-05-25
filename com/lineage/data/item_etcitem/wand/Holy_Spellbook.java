package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * 神聖魔法書
 * 範圍 單位 最小傷害 最大傷害 爆擊率 爆擊最小傷害 爆擊最大傷害 特效圖檔 消耗物品 消耗數量
 * wand.Holy_Spellbook 4 1 250 300 30 400 600 13993 41246 20
 *
 * @author Henry
 */
public class Holy_Spellbook extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Holy_Spellbook.class);
    private Random randon = new Random();
    // DB設定範圍
    private int _range = 0;
    // DB設定個數
    private int _count = 0;
    // DB設定最小傷害值
    private int _dmgMin = 0;
    // DB設定最大傷害值
    private int _dmgMax = 0;
    // DB設定爆擊機率
    private int _chance = 0;
    // DB設定最小爆擊傷害值
    private int _dmgMin_bon = 0;
    // DB設定最大爆擊傷害值
    private int _dmgMax_bon = 0;
    // DB設定特效編號
    private int _gfxId = 0;
    // DB設定消耗物品ID
    private int _itemId = 0;
    // DB設定消耗數量
    private int _itemCount = 0;

    public static ItemExecutor get() {
        return new Holy_Spellbook();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int spellsc_objid = data == null ? 0 : data[0];
        int spellsc_x = data == null ? 0 : data[1];
        int spellsc_y = data == null ? 0 : data[2];
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        if (_count > 1) {
            doAction(pc, null);
        } else {
            L1Object target = World.get().findObject(spellsc_objid);
            if (target != null) {
                doAction(pc, target);
            } else {
                pc.sendPacketsXR(new S_EffectLocation(new L1Location(spellsc_x, spellsc_y, pc.getMapId()), 10), 7);
            }
        }
        //pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), 17));
    }

    private boolean check(L1PcInstance user, L1Object target) {
        // 無法對自身使用
        if (user.getId() == target.getId()) {
            _log.info("無法對自身使用");
            return false;
        }
        // 檢查是否有障礙物
        if (!user.glanceCheck(target.getX(), target.getY())) {
            _log.info("目標與自身間有障礙物");
            return false;
        }
        // 範圍控制
        int location = target.getLocation().getTileLineDistance(user.getLocation());
        if (location > _range) {
            user.sendPackets(new S_SystemMessage("施法距離為:" + _range));
            _log.info("施法距離為:" + _range);
            return false;
        }
        return true;
    }

    /**
     * 計算傷害值並顯示特效
     *
     */
    private void doAction(L1PcInstance user, L1Object target) {
        Random _random = new Random();
        // 如果單位數為1則檢查對像
        if (_count == 1) {
            if (!check(user, target)) {
                return;
            }
        }
        // 傷害計算
        int dmg = 0;
        if ((_dmgMax - _dmgMin) <= 0) {
            dmg = _dmgMin;
        } else {
            dmg = _random.nextInt(_dmgMax - _dmgMin) + _dmgMin;
        }
        // 爆擊計算
        if (randon.nextInt(100) < _chance) {
            if ((_dmgMax_bon - _dmgMin_bon) <= 0) {
                dmg = _dmgMin_bon;
            } else {
                dmg = _random.nextInt(_dmgMax_bon - _dmgMin_bon) + _dmgMin_bon;
            }
        }
        //扣除施法材料
        if (_itemId != 0) {
            if (!user.getInventory().consumeItem(_itemId, _itemCount)) {
                user.sendPackets(new S_SystemMessage("施法材料不足"));
                return;
            }
        }
        if (_count > 1) {
            ArrayList<L1Object> list = World.get().getVisibleObjects(user, _count);
            if (list != null) {
                for (L1Object object : list) {
                    if ((object instanceof L1PcInstance)) {
                        L1PcInstance pc = (L1PcInstance) object;
                        // 安全區域檢查
                        if ((pc.getMap().isSafetyZone(pc.getLocation())) || (user.checkNonPvP(user, pc))) {
                            return;
                        }
                        // 技能判斷施法無效
                        if ((pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) || (pc.hasSkillEffect(L1SkillId.EARTH_BIND))) {
                            return;
                        }
                        // 聖界減半
                        if (pc.hasSkillEffect(L1SkillId.IMMUNE_TO_HARM)) {
                            dmg /= 2;
                        }
                        int newHp = pc.getCurrentHp() - dmg;
                        if ((newHp <= 0) && (!pc.isGm())) {
                            pc.death(user);
                        }
                        pc.setCurrentHp(newHp);
                        pc.sendPacketsX10(new S_DoActionGFX(pc.getId(), 2));
                    } else if ((object instanceof L1MonsterInstance)) {
                        L1MonsterInstance mob = (L1MonsterInstance) object;
                        mob.receiveDamage(user, dmg);
                        mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), 2));
                    }
                }
                user.sendPacketsX10(new S_SkillSound(user.getId(), _gfxId));
            }
        } else {
            if ((target instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) target;
                // 安全區域檢查
                if ((pc.getMap().isSafetyZone(pc.getLocation())) || (user.checkNonPvP(user, pc))) {
                    return;
                }
                // 技能判斷施法無效
                if ((pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) || (pc.hasSkillEffect(L1SkillId.EARTH_BIND))) {
                    return;
                }
                // 聖界減半
                if (pc.hasSkillEffect(L1SkillId.IMMUNE_TO_HARM)) {
                    dmg /= 2;
                }
                int newHp = pc.getCurrentHp() - dmg;
                if ((newHp <= 0) && (!pc.isGm())) {
                    pc.death(user);
                }
                pc.setCurrentHp(newHp);
            } else if ((target instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) target;
                mob.receiveDamage(user, dmg);
            }
            user.setHeading(user.targetDirection(target.getX(), target.getY()));
            user.sendPacketsX10(new S_ChangeHeading(user));
            //user.sendPacketsX10(new S_DoActionGFX(user.getId(), 17));
            if ((target instanceof L1PcInstance)) {
                L1PcInstance tgpc = (L1PcInstance) target;
                tgpc.sendPacketsX10(new S_SkillSound(tgpc.getId(), _gfxId));
                tgpc.sendPacketsX10(new S_DoActionGFX(tgpc.getId(), 2));
            } else if ((target instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) target;
                mob.broadcastPacketX10(new S_SkillSound(mob.getId(), _gfxId));
                mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), 2));
            } else {
                user.sendPacketsXR(new S_EffectLocation(new L1Location(target.getX(), target.getY(), user.getMapId()), _gfxId), 7);
            }
        }
        if (_count > 1) {
            ArrayList<L1Object> list = World.get().getVisibleObjects(user, _count);
            if (list != null) {
                for (L1Object object : list) {
                    if ((object instanceof L1PcInstance)) {
                        L1PcInstance pc = (L1PcInstance) object;
                        // 安全區域檢查
                        if ((pc.getMap().isSafetyZone(pc.getLocation())) || (user.checkNonPvP(user, pc))) {
                            return;
                        }
                        // 技能判斷施法無效
                        if ((pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) || (pc.hasSkillEffect(L1SkillId.EARTH_BIND))) {
                            return;
                        }
                        // 聖界減半
                        if (pc.hasSkillEffect(L1SkillId.IMMUNE_TO_HARM2)) {
                            dmg /= 2;
                        }
                        int newHp = pc.getCurrentHp() - dmg;
                        if ((newHp <= 0) && (!pc.isGm())) {
                            pc.death(user);
                        }
                        pc.setCurrentHp(newHp);
                        pc.sendPacketsX10(new S_DoActionGFX(pc.getId(), 2));
                    } else if ((object instanceof L1MonsterInstance)) {
                        L1MonsterInstance mob = (L1MonsterInstance) object;
                        mob.receiveDamage(user, dmg);
                        mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), 2));
                    }
                }
                user.sendPacketsX10(new S_SkillSound(user.getId(), _gfxId));
            }
        } else {
            if ((target instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) target;
                // 安全區域檢查
                if ((pc.getMap().isSafetyZone(pc.getLocation())) || (user.checkNonPvP(user, pc))) {
                    return;
                }
                // 技能判斷施法無效
                if ((pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) || (pc.hasSkillEffect(L1SkillId.EARTH_BIND))) {
                    return;
                }
                // 聖界減半
                if (pc.hasSkillEffect(L1SkillId.IMMUNE_TO_HARM2)) {
                    dmg /= 2;
                }
                int newHp = pc.getCurrentHp() - dmg;
                if ((newHp <= 0) && (!pc.isGm())) {
                    pc.death(user);
                }
                pc.setCurrentHp(newHp);
            } else if ((target instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) target;
                mob.receiveDamage(user, dmg);
            }
            user.setHeading(user.targetDirection(target.getX(), target.getY()));
            user.sendPacketsX10(new S_ChangeHeading(user));
            //user.sendPacketsX10(new S_DoActionGFX(user.getId(), 17));
            if ((target instanceof L1PcInstance)) {
                L1PcInstance tgpc = (L1PcInstance) target;
                tgpc.sendPacketsX10(new S_SkillSound(tgpc.getId(), _gfxId));
                tgpc.sendPacketsX10(new S_DoActionGFX(tgpc.getId(), 2));
            } else if ((target instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) target;
                mob.broadcastPacketX10(new S_SkillSound(mob.getId(), _gfxId));
                mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), 2));
            } else {
                user.sendPacketsXR(new S_EffectLocation(new L1Location(target.getX(), target.getY(), user.getMapId()), _gfxId), 7);
            }
        }
    }

    public void set_set(String[] set) {
        try {
            _range = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _count = Integer.parseInt(set[2]);
        } catch (Exception localException) {
        }
        try {
            _dmgMin = Integer.parseInt(set[3]);
        } catch (Exception localException) {
        }
        try {
            _dmgMax = Integer.parseInt(set[4]);
        } catch (Exception localException) {
        }
        try {
            _chance = Integer.parseInt(set[5]);
        } catch (Exception localException) {
        }
        try {
            _dmgMin_bon = Integer.parseInt(set[6]);
        } catch (Exception localException) {
        }
        try {
            _dmgMax_bon = Integer.parseInt(set[7]);
        } catch (Exception localException) {
        }
        try {
            _gfxId = Integer.parseInt(set[8]);
        } catch (Exception localException) {
        }
        try {
            _itemId = Integer.parseInt(set[9]);
        } catch (Exception localException) {
        }
        try {
            _itemCount = Integer.parseInt(set[10]);
        } catch (Exception localException) {
        }
    }
}