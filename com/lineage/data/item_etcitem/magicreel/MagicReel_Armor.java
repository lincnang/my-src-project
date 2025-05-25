package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;

public class MagicReel_Armor extends ItemExecutor {
    private int _skillid = 0;
    private int _consume = 1;

    public static ItemExecutor get() {
        return new MagicReel_Armor();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        int targetID = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targetID);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 2) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        if (tgItem.getItem().getType() != 2) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        pc.getInventory().removeItem(item, _consume);
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        L1SkillUse l1skilluse = new L1SkillUse();
        l1skilluse.handleCommands(pc, _skillid, targetID, 0, 0, 0, 2);
    }

    public void set_set(String[] set) {
        try {
            _skillid = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _consume = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.magicreel.MagicReel_Armor JD-Core Version:
 * 0.6.2
 */