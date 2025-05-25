package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

public class MagicReel_Long extends ItemExecutor {
    private final Random _random = new Random();
    private int _skillid = 0;
    private int _consume = 1;

    public static ItemExecutor get() {
        return new MagicReel_Long();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        if ((pc.isInvisble()) || (pc.isInvisDelay())) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        int targetID = data[0];
        int spellsc_x = data[1];
        int spellsc_y = data[2];
        if ((targetID == pc.getId()) || (targetID == 0)) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        if (pc.getMapId() == 5801) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        pc.getInventory().removeItem(item, _consume);
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        if (_random.nextInt(100) < 80) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, _skillid, targetID, spellsc_x, spellsc_y, 0, 2);
        } else {
            // 280：\f1施咒失敗。
            pc.sendPackets(new S_ServerMessage(280));
        }
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
 * com.lineage.data.item_etcitem.magicreel.MagicReel_Long2 JD-Core Version:
 * 0.6.2
 */