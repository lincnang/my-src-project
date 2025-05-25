package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.world.World;

import java.util.Random;

public class Lightning_Magic_Wand extends ItemExecutor {
    private static int _gfxid = 10;

    public static ItemExecutor get() {
        return new Lightning_Magic_Wand();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int spellsc_objid = data[0];
        int spellsc_x = data[1];
        int spellsc_y = data[2];
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        L1Object target = World.get().findObject(spellsc_objid);
        if (target != null) {
            doWandAction(pc, target);
        } else {
            pc.sendPacketsXR(new S_EffectLocation(new L1Location(spellsc_x, spellsc_y, pc.getMapId()), _gfxid), 7);
        }
        pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), 17));
        int newchargecount = item.getChargeCount() - 1;
        item.setChargeCount(newchargecount);
        pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        if (newchargecount <= 0) {
            pc.getInventory().deleteItem(item);
        }
    }

    private void doWandAction(L1PcInstance user, L1Object target) {
        Random _random = new Random();
        if (user.getId() == target.getId()) {
            return;
        }
        if (!user.glanceCheck(target.getX(), target.getY())) {
            return;
        }
        int dmg = _random.nextInt(11) - 5 + user.getInt();
        dmg = Math.max(1, dmg);
        if ((target instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) target;
            if ((pc.getMap().isSafetyZone(pc.getLocation())) || (user.checkNonPvP(user, pc))) {
                return;
            }
            if ((pc.hasSkillEffect(50)) || (pc.hasSkillEffect(78)) || (pc.hasSkillEffect(157))) {
                return;
            }
            int newHp = pc.getCurrentHp() - dmg;
            if ((newHp <= 0) && (!pc.isGm())) {
                pc.death(user);
            }
            pc.setCurrentHp(newHp);
        } else if ((target instanceof L1MonsterInstance)) {
            L1MonsterInstance mob = (L1MonsterInstance) target;
            switch (mob.getNpcId()) {
                case 71100:
                case 91072:
                    user.sendPacketsXR(new S_EffectLocation(new L1Location(target.getX(), target.getY(), user.getMapId()), _gfxid), 7);
                    return;
            }
            mob.receiveDamage(user, dmg);
        }
        user.setHeading(user.targetDirection(target.getX(), target.getY()));
        user.sendPacketsX10(new S_ChangeHeading(user));
        user.sendPacketsX10(new S_DoActionGFX(user.getId(), 17));
        if ((target instanceof L1PcInstance)) {
            L1PcInstance tgpc = (L1PcInstance) target;
            tgpc.sendPacketsX10(new S_SkillSound(tgpc.getId(), _gfxid));
            tgpc.sendPacketsX10(new S_DoActionGFX(tgpc.getId(), 2));
        } else if ((target instanceof L1MonsterInstance)) {
            L1MonsterInstance mob = (L1MonsterInstance) target;
            mob.broadcastPacketX10(new S_SkillSound(mob.getId(), _gfxid));
            mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), 2));
        } else {
            user.sendPacketsXR(new S_EffectLocation(new L1Location(target.getX(), target.getY(), user.getMapId()), _gfxid), 7);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.wand.Lightning_Magic_Wand JD-Core Version:
 * 0.6.2
 */