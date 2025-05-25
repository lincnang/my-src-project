package com.lineage.data.item_etcitem.wand;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;

import java.util.Iterator;
import java.util.Random;

/**
 * 火焰之魔杖
 */
public class Lightning_Magic_ICE extends ItemExecutor {
    /**
     *
     */
    private Lightning_Magic_ICE() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Lightning_Magic_ICE();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        int spellsc_objid = data[0];
        int spellsc_x = data[1];
        int spellsc_y = data[2];
        // 解除魔法技能??屏障
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        if (pc.isInvisble()) {// 隱身狀態
            pc.delInvis(); // 解除隱身狀態
        }
        // 送出動作封包
        pc.sendPacketsX10(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Wand));
        boolean isChecked = false;
        for (Integer integer : ConfigOtherSet2.iceKeyMapList) {
            int mapid = integer;
            if (mapid == pc.getMapId()) {
                isChecked = true;
                break;
            }
        }
        if (!isChecked) {
            // pc.sendPackets(new S_ServerMessage(在這裡不可以使用));
            pc.sendPackets(new S_SystemMessage("不是在冰女副本，不可以使用 火焰之魔杖。"));
            return;
        }
        // 可用次數
        final int chargeCount = item.getChargeCount();
        if (chargeCount <= 0) {
            if (pc.getInventory().removeItem(item, 1) == 1) {
                // 79 沒有任何事情發生
                pc.sendPackets(new S_ServerMessage(79));
            }
        } else {
            item.setChargeCount(item.getChargeCount() - 1);
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
            L1Object target = World.get().findObject(spellsc_objid);
            final Random _random = new Random();
            if (target != null) {
                if (pc.getId() == target.getId()) {
                    return;
                }
                if (!pc.glanceCheck(target.getX(), target.getY())) {
                    return;
                }
                //doWandAction(pc, target);
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) target;
                    pc.sendPacketsXR(new S_EffectLocation(new L1Location(tgpc.getX(), tgpc.getY(), pc.getMapId()), 762), 7);
                    //pc.sendPacketsXR(new S_EffectLocation(new L1Location(tgpc.getX()-2, tgpc.getY()+2, pc.getMapId()), 762), 7);
                    //pc.sendPacketsXR(new S_EffectLocation(new L1Location(tgpc.getX()-2, tgpc.getY()-2, pc.getMapId()), 762), 7);
                    //pc.sendPacketsXR(new S_EffectLocation(new L1Location(tgpc.getX()+2, tgpc.getY()-2, pc.getMapId()), 762), 7);
                    if ((tgpc.getMap().isSafetyZone(tgpc.getLocation())) || (pc.checkNonPvP(pc, tgpc))) {
                        return;
                    }
                    if ((tgpc.hasSkillEffect(50)) || (tgpc.hasSkillEffect(78)) || (tgpc.hasSkillEffect(157))) {
                        return;
                    }
                    int dmg = _random.nextInt(60) + 160;
                    // 被攻擊者受傷
                    tgpc.receiveDamage(pc, (dmg * 0.8), false, false);
                    // 受傷動作
                    tgpc.broadcastPacketX10(new S_DoActionGFX(tgpc.getId(), ActionCodes.ACTION_Damage));
                } else if ((target instanceof L1MonsterInstance)) {
                    L1MonsterInstance mob = (L1MonsterInstance) target;
                    pc.sendPacketsXR(new S_EffectLocation(new L1Location(mob.getX(), mob.getY(), pc.getMapId()), 762), 7);
                    //pc.sendPacketsXR(new S_EffectLocation(new L1Location(mob.getX()-2, mob.getY()+2, pc.getMapId()), 762), 7);
                    //pc.sendPacketsXR(new S_EffectLocation(new L1Location(mob.getX()-2, mob.getY()-2, pc.getMapId()), 762), 7);
                    //pc.sendPacketsXR(new S_EffectLocation(new L1Location(mob.getX()+2, mob.getY()-2, pc.getMapId()), 762), 7);
                    int dmg = _random.nextInt(60) + 160;
                    mob.broadcastPacketX10(new S_SkillSound(mob.getId(), 762));
                    mob.receiveDamage(pc, dmg);
                    mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
                }
                // 周邊對像MOB傷害
                for (L1Object object : World.get().getVisibleObjects(target, 4)) {
                    if (object instanceof L1MonsterInstance) {
                        L1MonsterInstance mob = (L1MonsterInstance) object;
                        // 被攻擊者受傷(傷害為160-220)
                        int dmg = _random.nextInt(60) + 160;
                        // 被攻擊者受傷
                        mob.receiveDamage(pc, dmg);
                        // 受傷動作
                        mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
                    } else if (object instanceof L1PcInstance) {
                        L1PcInstance tgpc1 = (L1PcInstance) object;
                        if (tgpc1.getId() == pc.getId()) {
                            return;
                        }
                        // 被攻擊者受傷(傷害為160-220)
                        int dmg = _random.nextInt(60) + 160;
                        // 被攻擊者受傷
                        tgpc1.receiveDamage(pc, (dmg * 0.8), false, false);
                        // 受傷動作
                        tgpc1.broadcastPacketX10(new S_DoActionGFX(tgpc1.getId(), ActionCodes.ACTION_Damage));
                    }
                }
                pc.setHeading(pc.targetDirection(target.getX(), target.getY()));
                pc.sendPacketsX10(new S_ChangeHeading(pc));
                pc.sendPacketsX10(new S_DoActionGFX(pc.getId(), 17));
            } else {
                pc.sendPacketsXR(new S_EffectLocation(new L1Location(spellsc_x, spellsc_y, pc.getMapId()), 762), 7);
                //pc.sendPacketsXR(new S_EffectLocation(new L1Location(spellsc_x-2, spellsc_y+2, pc.getMapId()), 762), 7);
                //pc.sendPacketsXR(new S_EffectLocation(new L1Location(spellsc_x-2, spellsc_y-2, pc.getMapId()), 762), 7);
                //pc.sendPacketsXR(new S_EffectLocation(new L1Location(spellsc_x+2, spellsc_y-2, pc.getMapId()), 762), 7);
            }
        }
    }
}
